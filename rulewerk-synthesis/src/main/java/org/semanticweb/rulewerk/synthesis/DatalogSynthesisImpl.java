package org.semanticweb.rulewerk.synthesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetTerm;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class DatalogSynthesisImpl {
	private final List<Fact> inputTuple;
	private final List<Predicate> expPred;
	private final List<Fact> outputPTuple;
	private final List<Fact> outputNTuple;
	private final List<Rule> ruleSet;
	private List<Statement> ruleSetExistNeg;
	private Map<Rule,Set<Rule>> ruleSetTrans;
	private Map<BoolExpr, Rule> var2rule;
	private Map<Rule, BoolExpr> rule2var;
	private Map<AbstractConstant, Rule> const2rule;
	private Map<Rule, AbstractConstant> rule2const;
	private Context ctx;
	private boolean coprov = false;
	private static final Logger logger = LogManager.getLogger("");
	private int rulewerkCall = 0;
	private int z3Call = 0;
	private int iteration = 0;
	private boolean debug = false;
	private int whyremainbuggy = 0;
	private int whyderivebuggy = 0;
	private int whynotremainbuggy = 0;
	private int whynotderivebuggy = 0;
	private int timeout = 300; 			// seconds
	
	public DatalogSynthesisImpl(List<Fact> inputTuple, List<Predicate> expPred, List<Fact> outputPTuple, List<Fact> outputNTuple, List<Rule> ruleSet, Context ctx){
		this.inputTuple = inputTuple;
		this.expPred = expPred;
		this.outputPTuple = outputPTuple;
		this.outputNTuple = outputNTuple;
		this.ruleSet = ruleSet;
		this.ctx = ctx;
		this.var2rule = new HashMap<>();
		this.rule2var = new HashMap<>();
		this.const2rule = new HashMap<>();
		this.rule2const = new HashMap<>();
		this.initMapping();
		if (coprov) {
			this.ruleSetExistNeg = this.getExistNeg(ruleSet);
		}
		this.ruleSetTrans = new HashMap<>();
		this.transformToDatalogS();
		ReasoningUtils.configureLogging(); // use simple logger for the example
	}
	
	/**
	 * Get the number of calls to Rulewerk reasoner during a synthesis process.
	 */
	public int getRulewerkCall() {
		return this.rulewerkCall;
	}
	
	/**
	 * Get the number of calls to Z3 SMT solver during a synthesis process.
	 */
	public int getZ3Call() {
		return this.z3Call;
	}
	
	/**
	 * Get the number of iterations during a synthesis process.
	 */
	public int getIteration() {
		return this.iteration;
	}
	
	// ============================================ CO-PROV UTILITIES ============================================== //
	
	/**
	 * Transform a {@link Rule} to its shadow {@link Rule}.
	 *
	 * @param rule 	non-null {@link Rule}
	 * @return The shadow {@link Rule} that is obtained by transforming its {@link Predicate}s by their shadow. 
	 */
	public Rule transformRule(Rule rule) {
		Conjunction<PositiveLiteral> head = rule.getHead();
		Conjunction<Literal> body = rule.getBody();
		Variable var = Expressions.makeUniversalVariable("r1");
		int i = 1;
		while (rule.getVariables().anyMatch(var::equals)) {
			i++;
			var = Expressions.makeUniversalVariable("r"+String.valueOf(i));
		}
		List<PositiveLiteral> nh = new ArrayList<>();
		for (PositiveLiteral pl : head) {
			List<Term> args = new ArrayList<>();
			for (Term t : pl.getArguments()){
				args.add(t);
			}
			args.add(var);
			PositiveLiteral newpl = Expressions.makePositiveLiteral(
					Expressions.makePredicate(pl.getPredicate().getName()+"_en", pl.getPredicate().getArity()+1), args);
			nh.add(newpl);
		}
		List<Literal> nb = new ArrayList<>();
		for (Literal pl : body) {
			List<Term> args = new ArrayList<>();
			for (Term t : pl.getArguments()){
				args.add(t);
			}
			args.add(var);
			Literal newpl = Expressions.makePositiveLiteral(
					Expressions.makePredicate(pl.getPredicate().getName()+"_en", pl.getPredicate().getArity()+1), args);
			nb.add(newpl);
		}
		nb.add(Expressions.makeNegativeLiteral(
				Expressions.makePredicate("Equal", 2), Arrays.asList(var, rule2const.get(rule))));
		return Expressions.makeRule(Expressions.makeConjunction(nh), Expressions.makeConjunction(nb));
	}
	
	/**
	 * Return a list of shadow {@link Rule}s associated with the set of input {@link Fact}s.
	 *
	 * @return the list of shadow {@link Rule}s associated with input {@link Fact}s.
	 */
	public List<Rule> transformInput() {
		List<Rule> enSimp = new ArrayList<>();
		List<Predicate> storedPred = new  ArrayList<>();
		final UniversalVariable z = Expressions.makeUniversalVariable("Z");
		Predicate iRP = Expressions.makePredicate("isRulePred", 1);
		for (Fact f : this.inputTuple) {
			Predicate p = f.getPredicate();
			if (!storedPred.contains(p)) {
				storedPred.add(p);
				Predicate newp = Expressions.makePredicate(p.getName()+"_en", p.getArity()+1);
				List<Term> termP = new ArrayList<>();
				List<Term> termnewP = new ArrayList<>();
				for (int i = 0; i < p.getArity(); i++) {
					UniversalVariable x = Expressions.makeUniversalVariable("X"+i);
					termP.add(x);
					termnewP.add(x);
				}
				termnewP.add(z);
				Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(newp, termnewP), 
						Expressions.makePositiveLiteral(p, termP), Expressions.makePositiveLiteral(iRP, z));
				enSimp.add(r);
			}
		}
		return enSimp;
	}
	
	/**
	 * Get the set of shadow {@link Rule}s depending of the selected {@link Rule}s.
	 *
	 * @param Pplus 	non-null set of selected {@link Rule}s
	 * @return The shadow {@link Rule}s that is obtained by replacing the {@link Rule}s and input {@link Fact}s by its shadow. 
	 */
	public List<Statement> getExistNeg(List<Rule> Pplus){
		List<Statement> enList = new ArrayList<Statement>();
		for (Rule r : Pplus) {
			enList.add(this.transformRule(r));
			Predicate iRP = Expressions.makePredicate("isRulePred", 1);
			enList.add(Expressions.makeFact(iRP, rule2const.get(r)));
			Predicate eq = Expressions.makePredicate("Equal", 2);
			enList.add(Expressions.makeFact(eq, rule2const.get(r), rule2const.get(r)));
		}
		enList.addAll(this.transformInput());
		return enList;
	}

	// ============================================ DATALOG(S) UTILITIES ============================================== //
	
	/**
	 * Get a set of {@link Rule}s to extract results from Datalog(S) reasoning by using the IDB {@link Predicate}s.
	 *
	 * @return The set of {@link Rule}s that is used to extract Datalog(S) reasoning result. 
	 */
	private List<Statement> rulesFromExpPred() {
		List<Statement> result = new ArrayList<>();
		for (Predicate p : this.expPred) {
			List<Term> vars = new ArrayList<>();
			for (int i = 0; i <= p.getArity() + 1; i++) {
				vars.add(Expressions.makeUniversalVariable("X"+i));
			}
			result.add(Expressions.makeRule(Expressions.makePositiveLiteral(p, vars.subList(0, vars.size()-2)), 
					Expressions.makePositiveLiteral(p.getName(), vars.subList(0, vars.size()-1))));
			result.add(Expressions.makeRule(Expressions.makePositiveLiteral("WhyProv_"+p.getName(), vars), 
					Expressions.makePositiveLiteral(p.getName(), vars.subList(0, vars.size()-1)), 
					Expressions.makePositiveLiteral("in", vars.get(vars.size()-1), vars.get(vars.size()-2))));
		}
		System.out.println(result);
		return result;
	}
	
	/**
	 * Transform each {@link Rule}s in the ruleset to its set of Datalog(S)-derived existential {@link Rule}s.
	 */
	private void transformToDatalogS() {
		Set<Predicate> edb = getEDB();
		for (Rule r : this.ruleSet) {
			Conjunction<PositiveLiteral> head = r.getHead();
			Conjunction<Literal> body = r.getBody();
			List<SetVariable> setVs = new ArrayList<>();
			List<Literal> newBody = new ArrayList<>();
			Term cr = this.rule2const.get(r);
			newBody.add(Expressions.makePositiveLiteral("Rule", cr));
			int newVar = 0;
			for (Literal l : body) {
				if (!edb.contains(l.getPredicate())) {
					SetVariable v = Expressions.makeSetVariable("U"+newVar);
					List<Term> newArgs = new ArrayList<>(l.getArguments());
					newArgs.add(v);
					Literal newL = Expressions.makePositiveLiteral(l.getPredicate().getName(), newArgs);
					newBody.add(newL);
					setVs.add(v);
					newVar++;
				} else {
					newBody.add(l);
				}
			}
			SetTerm scr = Expressions.makeSetConstruct(cr);
			SetTerm hTerm = scr;
			if (setVs.size() > 0) {
				hTerm = Expressions.makeSetUnion(scr, setVs.get(0));
				for (int idx = 1; idx < setVs.size(); idx++) {
					hTerm = Expressions.makeSetUnion(hTerm, setVs.get(idx));
				}
			}
			List<PositiveLiteral> newHead = new ArrayList<>();
			for (PositiveLiteral l : head) {
				List<Term> newArgs = new ArrayList<>(l.getArguments());
				newArgs.add(hTerm);
				PositiveLiteral newL = Expressions.makePositiveLiteral(l.getPredicate().getName(), newArgs);
				newHead.add(newL);
			}
			Rule newRule = Expressions.makeRule(
					Expressions.makePositiveConjunction(newHead),
					Expressions.makeConjunction(newBody));
			Set<Rule> translation = new HashSet<>();
			for (Rule rule : DatalogSetUtils.transformRule(newRule)) {
				translation.add(DatalogSetUtils.simplify(rule));
			}
			this.ruleSetTrans.put(r, translation);
		}
	}
	
	// ============================================ GENERAL UTILITIES ============================================== //
		
	/**
	 * Initiate the mappings of each {@link Rule} to its corresponding {@link Variable} and {@link Constant}.
	 * Format of boolean {@link Variable} for {@link Rule} r_i is vr_i.
	 * Format of {@link Constant} for {@link Rule} r_i is cr_i.
	 */
	private void initMapping() {
		int idx = 0;
		for (Rule r : this.ruleSet) {
			BoolExpr v = ctx.mkBoolConst("vr_"+idx);
			AbstractConstant c = Expressions.makeAbstractConstant("cr_"+idx); 
			this.var2rule.put(v, r);
			this.rule2var.put(r, v);
			this.const2rule.put(c, r);
			this.rule2const.put(r, c);
			idx++;
		}
	}
	
	/**
	 * Initiate the constraint formula for the synthesis process.
	 * 
	 * @return The disjunction of each {@link Rule}'s boolean variable. 
	 */
	private BoolExpr initPhi() {
		List<BoolExpr> rules = new ArrayList<>(this.var2rule.keySet());
		BoolExpr disjVars = rules.get(0);
		if (rules.size() > 1) {
			for (BoolExpr b : rules) {
				disjVars = this.ctx.mkOr(disjVars, b);
			}
		}
		return disjVars;
	}
	
	/**
	 * Get the set of EDB {@link Predicate}s from the input. 
	 * 
	 * @return the set of EDB {@link Predicate}s.
	 */
	private Set<Predicate> getEDB() {
		Set<Predicate> edb = new HashSet<>();
		for (Literal l : this.inputTuple) {
			edb.add(l.getPredicate());
		}
		return edb;
	}
	
	/**
	 * Create a query from a {@link Predicate}.
	 *
	 * @param p 	non-null {@link Predicate}.
	 * @return A {@link PositiveLiteral} query that is obtained from the corresponding {@link Predicate}. 
	 */
	private PositiveLiteral produceQuery(Predicate p) {
		List<Term> vars = new ArrayList<>();
		for (int i = 0; i < p.getArity(); i++) {
			vars.add(Expressions.makeUniversalVariable("X"+i));
		}
		return Expressions.makePositiveLiteral(p, vars);
	}
	
	/**
	 * Check whether a set of {@link Rule}s and a {@link Literal} t is buggy.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param rules 	non-null non-empty set of {@link Rule}s.
	 * @param ifDerived	non-null boolean to return depending on the definition of buggy.
	 * @return A pair of boolean: 0 index to check if the {@link Reasoner} timeout. 1 index to check whether the set of {@link Rule}s is buggy.
	 */
	private List<Boolean> isBuggy(PositiveLiteral t, List<Rule> rules, boolean ifDerived) throws IOException {
		ArrayList<Boolean> result = new ArrayList<>(Arrays.asList(false, false));
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(rules);
		kb.addStatements(this.inputTuple);
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setReasoningTimeout(this.timeout);
			this.rulewerkCall++;
			if (reasoner.reason()) {
				result.set(0, true);
				if (ReasoningUtils.isDerived(t, reasoner)) {
					result.set(1, ifDerived);
				} else {
					result.set(1, !ifDerived);
				}
			}
		}
		return result;
	}
	
	// ============================================== WHY PROVENANCE =============================================== //
	
	/**
	 * A debugging tool for why-provenance result.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param wpResult 	non-null set of {@link Rule}s that is the why-provenance result.
	 * @param Pplus		non-null set of {@link Rule}s that is the why-provenance input.
	 * @return true if the result conform to the debugging specification. false otherwise.
	 */
	public boolean whyProvDebugTool(PositiveLiteral t, List<Rule> wpResult, List<Rule> Pplus) throws IOException {
		System.out.println("Debug "+t+" for result "+wpResult);
		boolean satisfied = true;
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(this.inputTuple);
		kb.addStatements(wpResult);
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setReasoningTimeout(this.timeout);
			if (reasoner.reason()) {
				if (!ReasoningUtils.isDerived(t, reasoner)) {
					System.out.println("NON-DERIVE: "+kb.getRules()+" not derive "+t);
					satisfied = false;
					this.whyderivebuggy++;
				} 
			} else System.out.println("NON-TERMINATION: checking timeout");
		}
		kb.addStatements(Pplus);
		kb.removeStatements(wpResult);
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setReasoningTimeout(this.timeout);
			if (reasoner.reason()) {
				if (ReasoningUtils.isDerived(t, reasoner)) {
					System.out.println("REMAIN: "+kb.getRules()+" - remaining program still derive "+t);
					satisfied = false;
					this.whyremainbuggy++;
				} 
			} else System.out.println("NON-TERMINATION: checking timeout");
		}
		return satisfied;
	}
	
	/**
	 * Generate a conjunct to strengthen the constraint formula from the result of why-provenance.
	 *
	 * @param wp 	non-null set of {@link Rule}s that is the why-provenance result.
	 * @return Z3 {@link BoolExpr} denoting the conjunct from the why-provenance result.
	 */
	public BoolExpr whyProvExpr(List<Rule> wp) {
		if (wp != null && wp.size() > 0) {
			BoolExpr conjVars = this.rule2var.get(wp.get(0));
			if (wp.size() > 1) {
				for (Rule r : wp.subList(1, wp.size())) {
					conjVars = this.ctx.mkAnd(conjVars, this.rule2var.get(r));
				}
			}
			BoolExpr negConjVars = this.ctx.mkNot(conjVars);
			logger.info("Add "+negConjVars+" as why-provenance constraint");
			return negConjVars;
		} else {
			logger.info("Add TRUE as why-provenance constraint");
			return this.ctx.mkFalse();
		}
	}
	
	// ============================================== WHY-NOT PROVENANCE =============================================== //
	
	/**
	 * A debugging tool for why-not provenance result.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param wpResult 	non-null set of {@link Rule}s that is the why-not provenance result.
	 * @return true if the result conform to the debugging specification. false otherwise.
	 */
	public boolean whyNotProvDebugTool(PositiveLiteral t, Set<Rule> wnpResult) throws IOException {
		// Check based partially on Lemma 4.4
		boolean satisfied = true;
		List<Rule> PPlusDelta = new ArrayList<>(this.ruleSet);
		PPlusDelta.removeAll(wnpResult);
		System.out.println("Debug "+t+" for result "+wnpResult+" againts "+PPlusDelta);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(this.inputTuple);
		kb.addStatements(PPlusDelta);
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setReasoningTimeout(this.timeout);
			if (reasoner.reason()) {
				if (ReasoningUtils.isDerived(t, reasoner)) {
					System.out.println("REMAIN: Remainder derive "+t);
					satisfied = false;
					this.whynotremainbuggy++;
				} 
			} else System.out.println("NON-TERMINATION: checking timeout");
		}
		for (Rule r : wnpResult) {
			List<Rule> ppdr = new ArrayList<>(PPlusDelta);
			ppdr.add(r);
			KnowledgeBase kbr = new KnowledgeBase();
			kbr.addStatements(this.inputTuple);
			kbr.addStatements(ppdr);
			try (final Reasoner reasoner = new VLogReasoner(kbr)) {
				reasoner.setReasoningTimeout(this.timeout);
				if (reasoner.reason()) {
					if (!ReasoningUtils.isDerived(t, reasoner)) {
						System.out.println("NON-DERIVE: "+r+" not derive "+t);
						satisfied = false;
						this.whynotderivebuggy++;
					} 
				} else System.out.println("NON-TERMINATION: checking timeout");
			}
		}
		return satisfied;
	}
	
	/**
	 * Compute the why-not provenance of an unproduced desirable output tuple.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @return A subset of P+ that is the why-not provenance of the tuple t.
	 */
	public List<Rule> whyNotProv(PositiveLiteral t, List<Rule> Pplus) throws IOException{
		logger.info("Investigate "+t);
		// Use the delta debugging here
		List<Rule> Pmin = new ArrayList<>(this.ruleSet);
		Pmin.removeAll(Pplus);
		int d = 2;
		while (d <= Pmin.size() && d > 0) {
			List<List<Rule>> partition = DatalogSynthesisUtils.split(Pmin, d);	
			logger.debug("Partition: "+partition);
			boolean deltabuggy = false; boolean revdeltabuggy = false;
			for (List<Rule> chunk : partition) {
				List<Rule> deltaBuggy = new ArrayList<Rule>(this.ruleSet);
				deltaBuggy.removeAll(chunk);
				List<Boolean> isBuggy = this.isBuggy(t, deltaBuggy, false);
				if (isBuggy.get(0)) {	
					deltabuggy = isBuggy.get(1);
					if (deltabuggy) {
						Pmin = chunk;
						d = 2;
						break;
					}
				} else {
					logger.info("Why-not provenance non-termination");
					return null;
				}
			}
			if (!deltabuggy) {
				for (List<Rule> chunk : partition) {
					List<Rule> revDelta = new ArrayList<Rule>(Pmin);
					revDelta.removeAll(chunk);
					List<Rule> revDeltaBuggy = new ArrayList<Rule>(this.ruleSet);
					revDeltaBuggy.removeAll(revDelta);
					List<Boolean> isBuggy = this.isBuggy(t, revDeltaBuggy, false);
					if (isBuggy.get(0)) {
						revdeltabuggy = isBuggy.get(1);
						if (revdeltabuggy) {
							Pmin.removeAll(chunk);
							d -= 1;
							break;
						}
					} else {
						logger.info("Why-not provenance non-termination");
						return null;
					}
				}
			}
			if (!deltabuggy && !revdeltabuggy) {
				d *= 2;
			}
		}
		if (debug)
			System.out.println(whyNotProvDebugTool(t, new HashSet<>(Pmin)));
		return Pmin;
	}
	
	/**
	 * Alternative method for the why-not provenance.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param inPplus 	non-null set of selected {@link Rule}s P+.
	 * @return A subset of P+ that is the why-not provenance of the tuple t.
	 */
	public List<Rule> whyNotProvAlt(PositiveLiteral t, List<Rule> inPplus) throws IOException {
		Set<Rule> Pmin = new HashSet<>(this.ruleSet);
		Pmin.removeAll(inPplus);
		Set<Rule> Pplus = new HashSet<>(inPplus);
		List<Rule> code = new ArrayList<>(Pmin);
		int d = 2;
		while (true) {
			for (List<Rule> codeChunk : DatalogSynthesisUtils.split2(code, d)) {
				Set<Rule> currRMinus = new HashSet<>(Pmin);
				currRMinus.removeAll(codeChunk);
				Set<Rule> currRPlus = new HashSet<>(Pplus);
				currRPlus.addAll(codeChunk);
				List<Boolean> isBuggy = this.isBuggy(t, new ArrayList<>(currRPlus), false);
				if (isBuggy.get(0)) {
					boolean bugProduced = isBuggy.get(1);
					if (bugProduced) {
						Pplus = new HashSet<>(currRPlus);
						Pmin = new HashSet<>(currRMinus);
					}
				} else {
					logger.info("Why-not provenance non-termination");
					return null;
				}
			}
			if (d == code.size())
                break;
			d = Math.min(code.size(), d*2);
			if (d == 0)
                break;
		}
		if (debug)
			System.out.println(whyNotProvDebugTool(t, Pmin));
		return new ArrayList<>(Pmin);
	}
	
	/**
	 * Generate a conjunct to strengthen the constraint formula from the result of why-not provenance.
	 *
	 * @param wnp 	non-null set of {@link Rule}s that is the why-not provenance result.
	 * @return Z3 {@link BoolExpr} denoting the conjunct from the why-not provenance result.
	 */
	public BoolExpr whyNotProvExpr(List<Rule> wnp) {
		if (wnp == null) return this.ctx.mkFalse();
		if (wnp.size() > 0) {
			BoolExpr disjVars = this.rule2var.get(wnp.get(0));
			if (wnp.size() > 1) {
				for (Rule r : wnp.subList(1, wnp.size())) {
					disjVars = this.ctx.mkOr(disjVars, this.rule2var.get(r));
				}
			}
			logger.info("Add "+disjVars+" as why-not-provenance constraint");
			return disjVars;
		} else {
			logger.info("Add TRUE as why-not-provenance constraint");
			return this.ctx.mkTrue();
		}
	}
	
	// ============================================== CO-PROVENANCE =============================================== // 
	
	/**
	 * Retrieve the corresponding shadow {@link Rule}s for the set of {@link Rule}s P+.
	 *
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @return A set of shadow {@link Rule}s corresponding to P+.
	 */
	private List<Statement> getRelevantExistNeg(List<Rule> Pplus) {
		List<Statement> rel = new ArrayList<Statement>();
		for (Rule r : Pplus) {
			rel.add(this.ruleSetExistNeg.get(this.ruleSet.indexOf(r)));
		}
		return rel;
	}
	
	/**
	 * Compute the co-provenance of a produced desirable output tuple.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @return A subset of P+ that is the co-provenance of the tuple t.
	 */
	public List<Rule> coprovInv(PositiveLiteral t, List<Rule>Pplus) {
		logger.info("Investigate "+t);
		List<Statement> enRules = this.getRelevantExistNeg(Pplus);
		for (Statement r : enRules) {
			logger.debug(r);
		}
		List<Rule> coprovIn = new ArrayList<Rule>();
		Predicate p = t.getPredicate();
		for (Rule r: Pplus) {
			List<Term> args = new ArrayList<Term>();
			for (Term term : t.getArguments()){
				args.add(term);
			}
			args.add(rule2const.get(r));
			Predicate newp = Expressions.makePredicate(p.getName()+"_en", p.getArity()+1);
			Fact newt = Expressions.makeFact(newp, args);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(enRules);
			kb.addStatements(this.inputTuple);
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				reasoner.setReasoningTimeout(this.timeout);
				this.rulewerkCall++;
				if (reasoner.reason()) {
					if (ReasoningUtils.isDerived(newt, reasoner)) coprovIn.add(r);
				} else {
					logger.info("Co-provenance non-termination");
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<Rule> coprov = new ArrayList<Rule>();
		for (Rule r : Pplus) {
			if (!coprovIn.contains(r)) {
				coprov.add(r);
			}
		}
		return coprov;
	}
	
	/**
	 * Generate a conjunct to strengthen the constraint formula from the result of co-provenance.
	 *
	 * @param pPlus non-null set of {@link Rule}s that is the co-provenance input.
	 * @param cpr 	non-null set of {@link Rule}s that is the co-provenance result.
	 * @return Z3 {@link BoolExpr} denoting the conjunct from the co-provenance result.
	 */
	public BoolExpr coProvExpr(List<Rule> pPlus, List<Rule> cpr) {
		if (cpr == null) return this.ctx.mkFalse();
		if (cpr.size() > 0) {
			List<Rule> Pmin = new ArrayList<>(this.ruleSet);
			Pmin.removeAll(pPlus);
			BoolExpr disjVars = this.rule2var.get(Pmin.get(0));
			if (Pmin.size() > 1) {
				for (Rule r : Pmin.subList(1, Pmin.size())) {
					disjVars = this.ctx.mkOr(disjVars, this.rule2var.get(r));
				}
			}
			BoolExpr conjVars = this.rule2var.get(cpr.get(0));
			if (cpr.size() > 1) {
				for (Rule r : cpr.subList(1, cpr.size())) {
					conjVars = this.ctx.mkAnd(conjVars, this.rule2var.get(r));
				}
			}
			BoolExpr coprov = this.ctx.mkOr(disjVars, conjVars);
			logger.info("Add "+coprov+" as co-provenance constraint");
			return coprov;
		} else {
			logger.info("Add TRUE as co-provenance constraint");
			return this.ctx.mkTrue();
		}
	}
	
	// =========================================== Z3 Utility ============================================ //
	
	/**
	 * Derive the P+ from a model of the constraint formula.
	 *
	 * @param m 	non-null model of the constraint formula.
	 * @return List of {@link Rule}s P+ derived from the model. 
	 */
	public List<Rule> derivePPlus(Model m) {
		List<Rule> pPlus = new ArrayList<Rule>();
		for (Rule r : this.ruleSet) {
			Expr<BoolSort> truth = m.getConstInterp(this.rule2var.get(r));
			if (truth != null && truth.isTrue()) {
				pPlus.add(r);
			}
		}
		return pPlus;
	}
	
	/**
	 * Utility method to consult the Z3 solver given a boolean expression.
	 *
	 * @param expr 	non-null Z3 boolean expression.
	 * @return The model of the corresponding input according to the Z3 solver. 
	 */
	public Model consultSATSolver(BoolExpr expr) {
		this.z3Call++;
		Solver s = this.ctx.mkSolver();
		s.add(expr);
		Status result = s.check();
		if (result == Status.SATISFIABLE){
			logger.info("Model for: "+expr.toString());  
			logger.info(s.getModel());
			return s.getModel();
		} else if(result == Status.UNSATISFIABLE)
			logger.info("UNSAT");
		else
			logger.info("UNKNOWN");
		return null;
	}
	
	// ============================================== DATALOG(S) PROVENANCE =============================================== //
	
	
	/**
	 * Retrieve the Datalog(S) existential {@link Rule}s result from P+
	 *
	 * @param Pplus 	non-null list of {@link Rule}s P+.
	 * @return the list of existential {@link Rule}s corresponding to P+.
	 */
	private List<Statement> getTransFromPlus(List<Rule> Pplus) {
		List<Statement> transFromPlus = new ArrayList<>();
		for (Rule r : Pplus) {
			transFromPlus.addAll(this.ruleSetTrans.get(r));
			transFromPlus.add(Expressions.makeFact("Rule", this.rule2const.get(r)));
		}
		return transFromPlus;
	}
	
	/**
	 * A helper debugging tool for why-provenance set result.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param wpResult 	non-null set of list of {@link Rule}s that is the why-provenance result.
	 * @return true if the result conform to the debugging specification. false otherwise.
	 */
	public boolean debugSetTool(PositiveLiteral t, List<Term> wpResult) throws IOException {
		boolean satisfied = true;
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(this.inputTuple);
		for (Term term : wpResult) {
			kb.addStatement(this.const2rule.get(term));
		}
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setReasoningTimeout(this.timeout);
			if (reasoner.reason()) {
				if (!ReasoningUtils.isDerived(t, reasoner)) {
					this.whyderivebuggy++;
					System.out.println("NON-DERIVE: "+kb.getRules()+" not derive "+t);
					satisfied = false;
				}
			} else System.out.println("NON-TERMINATION: checking timeout");
		}
		return satisfied;
	}
	
	/**
	 * A debugging tool for why-provenance set result.
	 *
	 * @param t 			non-null {@link PositiveLiteral}.
	 * @param Pplus			non-null set of {@link Rule}s that is the why-provenance input.
	 * @param wpAllResult 	non-null set of list of {@link Rule}s that is the why-provenance result.
	 * @return true if the result conform to the debugging specification. false otherwise.
	 */
	public boolean debugWhyProvSet(PositiveLiteral t, List<Rule> Pplus, Set<List<Term>> wpAllResult) throws IOException {
		System.out.println("Debug "+t+" for result "+wpAllResult);
		boolean satisfied = true;
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(this.inputTuple);
		kb.addStatements(Pplus);
		for (List<Term> res : wpAllResult) {
			if (!debugSetTool(t, res)) satisfied = false;
			for (Term term : res) {
				kb.removeStatement(this.const2rule.get(term));
			}
		}
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.setReasoningTimeout(this.timeout);
			if (reasoner.reason()) {
				if (ReasoningUtils.isDerived(t, reasoner)) {
					this.whyremainbuggy++;
					System.out.println("REMAIN: "+kb.getRules()+" still derive "+t);
					satisfied = false;
				}
			} else System.out.println("NON-TERMINATION: checking timeout");
		}
		return satisfied;
	}
	
	/**
	 * Compute the why-provenance of produced non-desirable output tuples using Datalog(S)-based reasoning.
	 *
	 * @param ts 		non-null list of produced non-desirable output {@link PositiveLiteral}s.
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @param reasoner	non-null Rulewerk {@link Reasoner}.
	 * @return Boolean expression that denote the resulting why-provenance to strengthen the constraint formula.
	 */
	public BoolExpr whyProvSet(List<Fact> ts, List<Rule> Pplus, Reasoner reasoner) throws IOException{
		Set<List<Term>> result = new HashSet<>();
		for (PositiveLiteral t : ts) {
			List<Term> newTerm = new ArrayList<>(t.getArguments());
			newTerm.add(Expressions.makeUniversalVariable("x"));
			newTerm.add(Expressions.makeUniversalVariable("y"));
			PositiveLiteral l = Expressions.makePositiveLiteral("WhyProv_"+t.getPredicate().getName(), newTerm);
			Map<Term,List<Term>> res = ReasoningUtils.getAllDifferentSets(l, reasoner);
			Set<List<Term>> debugCont = new HashSet<>();
			for (Term key : res.keySet()) {
				result.add(res.get(key));
				debugCont.add(res.get(key));
			}
			if (debug)
				System.out.println(debugWhyProvSet(t, Pplus, debugCont));
		}
		if (result.size() > 0) {
			BoolExpr outerConjunct = this.ctx.mkTrue();
			for (List<Term> terms : result) {
				BoolExpr conjVars = this.rule2var.get(this.const2rule.get(terms.get(0)));
				if (terms.size() > 1) {
					for (Term t : terms.subList(1, terms.size())) {
						conjVars = this.ctx.mkAnd(conjVars, this.rule2var.get(this.const2rule.get(t)));
					}
				}
				BoolExpr negConjVars = this.ctx.mkNot(conjVars);
				if (result.size() > 1)
					outerConjunct = this.ctx.mkAnd(outerConjunct, negConjVars);
				else
					outerConjunct = negConjVars;
			}
			logger.info("Add "+outerConjunct+" as why-provenance constraint");
			return outerConjunct;
		} else {
			logger.info("Add TRUE as why-provenance constraint");
			return this.ctx.mkTrue();
		}
	}
	
	/**
	 * Compute the why-not provenance of non-produced desirable output tuples using Datalog(S)-based reasoning.
	 *
	 * @param f 		non-null {@link Fact}.
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @param reasoner	non-null Rulewerk {@link Reasoner}.
	 * @return Boolean expression that denote the resulting why-not provenance to strengthen the constraint formula.
	 */
	public BoolExpr whyNotProvSet(Fact f, List<Rule> Pplus, Reasoner reasoner) throws IOException{
		List<Term> newTerm = new ArrayList<>(f.getArguments());
		newTerm.add(Expressions.makeUniversalVariable("x"));
		newTerm.add(Expressions.makeUniversalVariable("y"));
		PositiveLiteral l = Expressions.makePositiveLiteral("WhyProv_"+f.getPredicate().getName(), newTerm);
		Map<Term,List<Term>> res = ReasoningUtils.getAllDifferentSets(l, reasoner);
		Set<List<Term>> result = new HashSet<>();
		for (Term key : res.keySet()) {
			result.add(res.get(key));
		}
		if (debug)
			System.out.println(debugWhyProvSet(f, Pplus, result));
		if (result.size() > 0) {
			BoolExpr outerDisjunct = this.ctx.mkFalse();
			for (List<Term> terms : result) {
				BoolExpr conjVars = this.rule2var.get(this.const2rule.get(terms.get(0)));
				if (terms.size() > 1) {
					for (Term t : terms.subList(1, terms.size())) {
						conjVars = this.ctx.mkAnd(conjVars, this.rule2var.get(this.const2rule.get(t)));
					}
				}
				if (result.size() > 1)
					outerDisjunct = this.ctx.mkOr(outerDisjunct, conjVars);
				else
					outerDisjunct = conjVars;
			}
			logger.info("Add "+outerDisjunct+" as why-provenance constraint");
			return outerDisjunct;
		} else {
			logger.info("Add TRUE as why-provenance constraint");
			return this.ctx.mkTrue();
		}
	}
	
	/**
	 * Compute the why-not provenance of non-produced desirable output tuples using Datalog(S)-based reasoning.
	 *
	 * @param f 		non-null {@link Fact}.
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @param reasoner	non-null Rulewerk {@link Reasoner}.
	 * @return Boolean expression that denote the resulting why-not provenance to strengthen the constraint formula.
	 */
	public List<Rule> coProvSet(Fact f, List<Rule> Pplus, Reasoner reasoner) throws IOException{
		List<Term> newTerm = new ArrayList<>(f.getArguments());
		newTerm.add(Expressions.makeUniversalVariable("x"));
		newTerm.add(Expressions.makeUniversalVariable("y"));
		PositiveLiteral l = Expressions.makePositiveLiteral("WhyProv_"+f.getPredicate().getName(), newTerm);
		Map<Term,List<Term>> res = ReasoningUtils.getAllDifferentSets(l, reasoner);
		Set<List<Term>> result = new HashSet<>();
		for (Term key : res.keySet()) {
			result.add(res.get(key));
		}
		if (debug)
			System.out.println(debugWhyProvSet(f, Pplus, result));
		List<Term> coprov = new ArrayList<>();
		if (result.size() > 0) {		
			List<List<Term>> whyProvRes = new ArrayList<>(result);
			coprov = whyProvRes.get(0);
			for (int i = 1; i < result.size(); i++) {
				coprov.retainAll(whyProvRes.get(i));
			}
		}
		List<Rule> coProvRes = new ArrayList<>();
		for (Term t : coprov) {
			coProvRes.add(this.const2rule.get(t));
		}
		return coProvRes;
	}
	
	// ======================================== ORIGINAL DELTA DEBUGGING ALGS BY ZELLER ======================================== //
	
	/**
	 * Helper of the why-not provenance computation.
	 *
	 * @param t 			non-null {@link PositiveLiteral}.
	 * @param Pmin	 		non-null subset of unselected {@link Rule}s P-.
	 * @param accumulator	non-null set of accumulator {@link Rule}s.
	 * @return A subset of P- that is the why-not provenance of the tuple t.
	 */
	public List<Rule> whyNotDeltaOrigAcc(PositiveLiteral t, List<Rule> Pmin, List<Rule> accumulator) throws IOException {
		List<Rule> code = new ArrayList<>(Pmin);
		List<List<Rule>> codeChunks = DatalogSynthesisUtils.split2(code, 2);
		boolean existBug = false;
		List<Rule> result = new ArrayList<>();
		for (List<Rule> codeChunk : codeChunks) {
			boolean bugProduced = false;
			if (codeChunk.size() > 0) {
				List<Rule> rules = new ArrayList<>(this.ruleSet);
				rules.removeAll(codeChunk);
				rules.removeAll(accumulator);
				List<Boolean> isBuggy = isBuggy(t, rules, false);
				if (isBuggy.get(0)) {
					bugProduced = isBuggy.get(1);
					existBug = (existBug || bugProduced);
					if (bugProduced) {
						if (codeChunk.size() > 1) {
							List<Rule> res = whyNotDeltaOrigAcc(t, codeChunk, accumulator);
							return res;
						} else return codeChunk;
					}
				} else return null;
			}
		}
		if (!existBug && Pmin.size() > 1) {
			List<Rule> acc1 = new ArrayList<>(accumulator);
			acc1.addAll(codeChunks.get(1));
			List<Rule> res1 = whyNotDeltaOrigAcc(t, codeChunks.get(0), acc1);
			if (res1 == null) return null;
			result.addAll(res1);
			List<Rule> acc2 = new ArrayList<>(accumulator);
			acc2.addAll(res1);
			List<Rule> res2 = whyNotDeltaOrigAcc(t, codeChunks.get(1), acc2);
			if (res2 == null) return null;
			result.addAll(res2);
		}
		return result;
	}
	
	/**
	 * Compute the why-not provenance of an unproduced desirable output tuple.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @return A subset of P+ that is the why-not provenance of the tuple t.
	 */
	public List<Rule> whyNotDeltaOrig(PositiveLiteral t, List<Rule> Pplus) throws IOException {
		List<Rule> Pmin = new ArrayList<>(this.ruleSet);
		Pmin.removeAll(Pplus);
		Set<Rule> result = new HashSet<>(whyNotDeltaOrigAcc(t, Pmin, new ArrayList<>()));
		List<Rule> res = new ArrayList<>(result);
		if (debug)
			System.out.println(whyNotProvDebugTool(t, new HashSet<>(res)));
		return res;
	}
	
	/**
	 * Helper of the why-provenance computation.
	 *
	 * @param t 			non-null {@link PositiveLiteral}.
	 * @param Pplus	 		non-null subset of selected {@link Rule}s P+.
	 * @param accumulator	non-null set of accumulator {@link Rule}s.
	 * @return A subset of P- that is the why-not provenance of the tuple t.
	 */
	public List<Rule> whyDeltaOrigAcc(PositiveLiteral t, List<Rule> Pplus, List<Rule> accumulator) throws IOException {
		List<Rule> code = new ArrayList<>(Pplus);
		List<List<Rule>> codeChunks = DatalogSynthesisUtils.split2(code, 2);
		List<Rule> result = new ArrayList<>();
		boolean existBug = false;
		for (List<Rule> codeChunk : codeChunks) {
			boolean bugProduced = false;
			List<Rule> rules = new ArrayList<>(codeChunk);
			rules.addAll(accumulator);
			List<Boolean> isBuggy = isBuggy(t, rules, true);
			if (isBuggy.get(0)) {
				bugProduced = isBuggy.get(1);
				existBug = (existBug || bugProduced);
				if (bugProduced) {
					if (codeChunk.size() > 1) {
						result.addAll(whyDeltaOrigAcc(t, codeChunk, accumulator));
					} else {
						result.addAll(codeChunk);
					}
				}
			} else return null;
		}
		if (!existBug && Pplus.size() > 1) {
			List<Rule> acc1 = new ArrayList<>(accumulator);
			acc1.addAll(codeChunks.get(1));
			List<Rule> res1 = whyDeltaOrigAcc(t, codeChunks.get(0), acc1);
			if (res1 == null) return null;
			result.addAll(res1);
			List<Rule> acc2 = new ArrayList<>(accumulator);
			acc2.addAll(res1);
			List<Rule> res2 = whyDeltaOrigAcc(t, codeChunks.get(1), acc2);
			result.addAll(res2);
			if (res2 == null) return null;
		}
		return result;
	}
	
	/**
	 * Compute the why-provenance of a produced un-desirable output tuple.
	 *
	 * @param t 		non-null {@link PositiveLiteral}.
	 * @param Pplus 	non-null set of selected {@link Rule}s P+.
	 * @return A subset of P+ that is the why-provenance of the tuple t.
	 */
	public List<Rule> whyDeltaOrig(PositiveLiteral t, List<Rule> Pplus) throws IOException {
		Set<Rule> result = new HashSet<>(whyDeltaOrigAcc(t, Pplus, new ArrayList<>()));
		List<Rule> res = new ArrayList<>(result);
		if (debug)
			System.out.println(whyProvDebugTool(t, Pplus, res));
		return res;
	}
	
	// =========================================== Synthesis Process ======================================== //
	
	/**
	 * Derive the non-expected output tuples that is derived in a reasoning process if the non-expected tuples are not declared.
	 *
	 * @param reasoner 	non-null Rulewerk {@link Reasoner}.
	 * @return List of non-expected {@link Fact}s that are derived by the reasoner. 
	 */
	public List<Fact> getNonExpectedResults(Reasoner reasoner) {
		List<Fact> NOutput = new ArrayList<Fact>();
		for (Predicate p : this.expPred) {
			for (Fact f :ReasoningUtils.getQueryAnswerAsListWhyProv(produceQuery(p), reasoner)) {
				if (!this.outputPTuple.contains(f)) NOutput.add(f);
			}
		}
		return NOutput;
	}
	
	/**
	 * Derive the non-expected output tuples that is derived in a reasoning process if the non-expected tuples are declared.
	 *
	 * @param reasoner 	non-null Rulewerk {@link Reasoner}.
	 * @return List of non-expected {@link Fact}s that are derived by the reasoner. 
	 */
	public List<Fact> getNonExpectedResultsDecl(Reasoner reasoner) {
		List<Fact> NOutput = new ArrayList<Fact>();
		for (Fact t : this.outputNTuple) {
			if (ReasoningUtils.isDerived(t, reasoner)) {
				NOutput.add(t);
			}
		}
		return NOutput;
	}
	
	/**
	 * Derive the non-generated expected output tuples in a reasoning process.
	 *
	 * @param reasoner 	non-null Rulewerk {@link Reasoner}.
	 * @return List of expected {@link Fact}s that are not derived by the reasoner. 
	 */
	public List<Fact> getNonGeneratedResults(Reasoner reasoner) {
		List<Fact> YOutput = new ArrayList<Fact>();
		for (Predicate p : this.expPred) {
			List<Fact> ROutput = ReasoningUtils.getQueryAnswerAsListWhyProv(produceQuery(p), reasoner);
			for (Fact f : this.outputPTuple) {
				if (!ROutput.contains(f)) YOutput.add(f);
			}
		}
		return YOutput;
	}
	
	/**
	 * Synthesis process using why-not provenance according to the [Raghothaman et al. 2019] and why-provenance derived from [Zeller, 1999].
	 *
	 * @return List of expected {@link Fact}s that are not derived by the reasoner. 
	 */
	public List<Rule> synthesis() {
		this.rulewerkCall = 0; this.z3Call = 0; this.iteration = 0;
		int wp = 0; int wnp = 0; int cp = 0;
		BoolExpr phi = initPhi();
		List<Rule> pPlus = new ArrayList<Rule>();
		Model result = this.consultSATSolver(phi);
		boolean loop = true; int iter = 0;
		while (result != null && loop) {
			iter++;
			loop = false;
			logger.debug("P+:");
			for (Rule r:pPlus)
				logger.debug("- "+r);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(pPlus);
			kb.addStatements(this.inputTuple);
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				reasoner.setReasoningTimeout(this.timeout);
				if (reasoner.reason()) {
					int newWhyNots = 0;
					for (Fact t : this.outputPTuple) {
						boolean generate = ReasoningUtils.isDerived(t, reasoner);
						if (!generate) {
							loop = true;
							if (pPlus.size() < this.ruleSet.size()) {
								logger.info("============= Perform Why Not Provenance ==============");
								wnp++; newWhyNots++;
								logger.info("- "+wnp+" call of why-not-provenance");
								phi = this.ctx.mkAnd(phi, this.whyNotProvExpr(this.whyNotProv(t, pPlus)));
								logger.info("=============== Why Not Provenance End ================");
							}
						} else if (generate) {
							if (this.coprov) {
								if (pPlus.size() < this.ruleSet.size() && pPlus.size() > 0) {
									logger.info("============= Perform Co-Provenance ==============");
									cp++;
									logger.info("- "+cp+" call of co-provenance");
									phi = this.ctx.mkAnd(phi, this.coProvExpr(pPlus, this.coprovInv(t, pPlus)));
									logger.info("=============== Co-Provenance End ================");
								}
							}
						}
						if (newWhyNots >= 3) break;
					}
					List<Fact> nonExpectedTuples = new ArrayList<>();
					if (this.outputNTuple.size() > 0) {
						nonExpectedTuples = this.getNonExpectedResultsDecl(reasoner);
					} else {
						nonExpectedTuples = this.getNonExpectedResults(reasoner);
					}
					int newWhys = 0;
					for (Fact f : nonExpectedTuples) {
						loop = true;
						if (pPlus.size() > 0) {
							logger.info("============= Perform Why Provenance ==============");
							wp++; newWhys++;
							logger.info("- "+wp+" call of why-provenance");
							phi = this.ctx.mkAnd(phi, this.whyProvExpr(this.whyDeltaOrig(f, pPlus)));
							logger.info("=============== Why Provenance End ================");
						}
						if (newWhys >= 3) break;
					}
				} else {
					System.out.println("Cannot find solution: Non-termination");
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = this.consultSATSolver(phi);
			if (result != null) {
				pPlus = this.derivePPlus(result);
				if (pPlus.size() == 0) loop = true;
			}
			logger.info("Iteration "+iter+" complete.");
			logger.info("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		}
		this.iteration = iter;
		System.out.println("Synthesis finished in "+iter+" iteration(s):");
		System.out.println("- "+wp+" call of why-provenance");
		System.out.println("- "+wnp+" call of why-not-provenance");
		System.out.println("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		if (debug) {
			System.out.println("why-provenance non-derive buggy: "+this.whyderivebuggy);
			System.out.println("why-provenance remain derive buggy: "+this.whyremainbuggy);
			System.out.println("why-not-provenance non-derive buggy: "+this.whynotderivebuggy);
			System.out.println("why-not-provenance remain derive buggy: "+this.whynotremainbuggy);
		}
		if (result != null) {
			return pPlus;
		} else {
			System.out.println("Cannot find solution.");
			return null;
		}
	}
	
	/**
	 * Synthesis process using why-not provenance and why-provenance derived from [Zeller, 1999].
	 *
	 * @return List of expected {@link Fact}s that are not derived by the reasoner. 
	 */
	public List<Rule> synthesisOrig() {
		this.rulewerkCall = 0; this.z3Call = 0; this.iteration = 0;
		int wp = 0; int wnp = 0; int cp = 0;
		BoolExpr phi = initPhi();
		List<Rule> pPlus = new ArrayList<Rule>();
		Model result = this.consultSATSolver(phi);
		boolean loop = true; int iter = 0;
		while (result != null && loop) {
			iter++;
			loop = false;
			logger.debug("P+:");
			for (Rule r:pPlus)
				logger.debug("- "+r);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(pPlus);
			kb.addStatements(this.inputTuple);
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				reasoner.setReasoningTimeout(this.timeout);
				if (reasoner.reason()) {
					int newWhyNots = 0;
					for (Fact t : this.outputPTuple) {
						boolean generate = ReasoningUtils.isDerived(t, reasoner);
						if (!generate) {
							loop = true;
							if (pPlus.size() < this.ruleSet.size()) {
								logger.info("============= Perform Why Not Provenance ==============");
								wnp++; newWhyNots++;
								logger.info("- "+wnp+" call of why-not-provenance");
								phi = this.ctx.mkAnd(phi, this.whyNotProvExpr(this.whyNotDeltaOrig(t, pPlus)));
								logger.info("=============== Why Not Provenance End ================");
							}
						} else if (generate) {
							if (this.coprov) {
								if (pPlus.size() < this.ruleSet.size() && pPlus.size() > 0) {
									logger.info("============= Perform Co-Provenance ==============");
									cp++;
									logger.info("- "+cp+" call of co-provenance");
									phi = this.ctx.mkAnd(phi, this.coProvExpr(pPlus, this.coprovInv(t, pPlus)));
									logger.info("=============== Co-Provenance End ================");
								}
							}
						}
						if (newWhyNots >= 3) break;
					}
					List<Fact> nonExpectedTuples = new ArrayList<>();
					if (this.outputNTuple.size() > 0) {
						nonExpectedTuples = this.getNonExpectedResultsDecl(reasoner);
					} else {
						nonExpectedTuples = this.getNonExpectedResults(reasoner);
					}
					int newWhys = 0;
					for (Fact f : nonExpectedTuples) {
						loop = true;
						if (pPlus.size() > 0) {
							logger.info("============= Perform Why Provenance ==============");
							wp++; newWhys++;
							logger.info("- "+wp+" call of why-provenance");
							phi = this.ctx.mkAnd(phi, this.whyProvExpr(this.whyDeltaOrig(f, pPlus)));
							logger.info("=============== Why Provenance End ================");
						}
						if (newWhys >= 3) break;
					}
				} else {
					System.out.println("Cannot find solution: Non-termination");
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = this.consultSATSolver(phi);
			if (result != null) {
				pPlus = this.derivePPlus(result);
				if (pPlus.size() == 0) loop = true;
			}
			logger.info("Iteration "+iter+" complete.");
			logger.info("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		}
		this.iteration = iter;
		System.out.println("Synthesis finished in "+iter+" iteration(s):");
		System.out.println("- "+wp+" call of why-provenance");
		System.out.println("- "+wnp+" call of why-not-provenance");
		System.out.println("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		if (debug) {
			System.out.println("why-provenance non-derive buggy: "+this.whyderivebuggy);
			System.out.println("why-provenance remain derive buggy: "+this.whyremainbuggy);
			System.out.println("why-not-provenance non-derive buggy: "+this.whynotderivebuggy);
			System.out.println("why-not-provenance remain derive buggy: "+this.whynotremainbuggy);
		}
		if (result != null) {
			return pPlus;
		} else {
			System.out.println("Cannot find solution.");
			return null;
		}
	}

	/**
	 * Synthesis process using why-not provenance derived from [Zeller, 1999] and Datalog(S)-based why-provenance.
	 *
	 * @return List of expected {@link Fact}s that are not derived by the reasoner. 
	 */
	public List<Rule> synthesisSet() {
		this.rulewerkCall = 0; this.z3Call = 0; this.iteration = 0;
		int wp = 0; int wnp = 0; int cp = 0;
		BoolExpr phi = initPhi();
		Model result = this.consultSATSolver(phi);
		List<Rule> pPlus = new ArrayList<>();
		boolean loop = true; int iter = 0;
		while (result != null && loop) {
			iter++;
			loop = false;
			logger.info("P+:");
			for (Rule r:pPlus)
				logger.info("- "+r);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(DatalogSetUtils.getR_SU());
			kb.addStatements(getTransFromPlus(pPlus));
			kb.addStatements(rulesFromExpPred());
			kb.addStatements(this.inputTuple);
			
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				reasoner.setReasoningTimeout(this.timeout);
				if (reasoner.reason()) {
					int newWhyNots = 0;
					for (Fact t : this.outputPTuple) {
						boolean generate = ReasoningUtils.isDerived(t, reasoner);
						if (!generate) {
							loop = true;
							if (pPlus.size() < this.ruleSet.size()) {
								logger.info("============= Perform Why Not Provenance ==============");
								wnp++; newWhyNots++;
								logger.info("- "+wnp+" call of why-not-provenance");
								phi = this.ctx.mkAnd(phi, this.whyNotProvExpr(this.whyNotDeltaOrig(t, pPlus)));
								logger.info("=============== Why Not Provenance End ================");
							}
						} else if (generate) {
							if (this.coprov) {
								if (pPlus.size() < this.ruleSet.size() && pPlus.size() > 0) {
									logger.info("============= Perform Co-Provenance ==============");
									cp++;
									logger.info("- "+cp+" call of co-provenance");
									phi = this.ctx.mkAnd(phi, this.coProvExpr(pPlus, this.coprovInv(t, pPlus)));
									logger.info("=============== Co-Provenance End ================");
								}
							}
						}
						if (newWhyNots >= 3) break;
					}
					List<Fact> nonExpectedTuples = new ArrayList<>();
					if (this.outputNTuple.size() > 0) {
						nonExpectedTuples = this.getNonExpectedResultsDecl(reasoner);
					} else {
						nonExpectedTuples = this.getNonExpectedResults(reasoner);
					}
					if (nonExpectedTuples.size() > 0) {
						loop = true;
						if (pPlus.size() > 0) {
							logger.info("============= Perform Why Provenance ==============");
							wp++;
							logger.info("- "+wp+" call of why-provenance");
							phi = this.ctx.mkAnd(phi, whyProvSet(nonExpectedTuples, pPlus, reasoner));
							logger.info("=============== Why Provenance End ================");
						}
					}
				} else {
					System.out.println("Cannot find solution: Non-termination");
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = this.consultSATSolver(phi);
			if (result != null) {
				pPlus = this.derivePPlus(result);
				if (pPlus.size() == 0) loop = true;
			}
			logger.info("Iteration "+iter+" complete.");
			logger.info("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		}
		this.iteration = iter;
		System.out.println("Synthesis finished in "+iter+" iteration(s):");
		System.out.println("- "+wp+" call of why-provenance");
		System.out.println("- "+wnp+" call of why-not-provenance");
		System.out.println("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		if (debug) {
			System.out.println("why-provenance non-derive buggy: "+this.whyderivebuggy);
			System.out.println("why-provenance remain derive buggy: "+this.whyremainbuggy);
			System.out.println("why-not-provenance non-derive buggy: "+this.whynotderivebuggy);
			System.out.println("why-not-provenance remain derive buggy: "+this.whynotremainbuggy);
		}
		if (result != null) {
			return pPlus;
		} else {
			System.out.println("Cannot find solution.");
			return null;
		}
	}
	
	/**
	 * Synthesis process using Datalog(S)-based why-not provenance and why-provenance.
	 *
	 * @return List of expected {@link Fact}s that are not derived by the reasoner. 
	 */
	public List<Rule> synthesisSetAll() throws IOException {
		this.rulewerkCall = 0; this.z3Call = 0; this.iteration = 0;
		int wp = 0; int wnp = 0; 
		BoolExpr phi = initPhi();
		Model result = this.consultSATSolver(phi);
		List<Rule> pPlus = new ArrayList<>(this.ruleSet);
		boolean loop = true; int iter = 0;
		while (result != null && loop) {
			iter++; loop = false;
			logger.info("P+:");
			for (Rule r:pPlus)
				logger.info("- "+r);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(DatalogSetUtils.getR_SU());
			kb.addStatements(getTransFromPlus(pPlus));
			kb.addStatements(rulesFromExpPred());
			kb.addStatements(this.inputTuple);
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				reasoner.setReasoningTimeout(this.timeout);
				if (reasoner.reason()) {
					this.rulewerkCall++;
					// Ensure all expected output are generated.
					for (Fact f : this.outputPTuple) {
						boolean generate = ReasoningUtils.isDerived(f, reasoner);
						if (iter == 1) {
							logger.info("============= Perform Why Not Provenance ==============");
							wnp++; loop = true;
							logger.info("- "+wnp+" call of why-not-provenance");
							phi = this.ctx.mkAnd(phi, this.whyNotProvSet(f, pPlus, reasoner));
							logger.info("=============== Why Not Provenance End ================");
						}
						if (iter > 1 && !generate) logger.error("Tuple "+f+" cannot be generated.");
					}
	
					// Ensure all undesired output are not generated.
					List<Fact> nonExpectedTuples = new ArrayList<>();
					if (this.outputNTuple.size() > 0) {
						nonExpectedTuples = this.getNonExpectedResultsDecl(reasoner);
					} else {
						nonExpectedTuples = this.getNonExpectedResults(reasoner);
					}
					if (nonExpectedTuples.size() > 0) {
						loop = true;
						if (pPlus.size() > 0) {
							logger.info("============= Perform Why Provenance ==============");
							wp++; loop = true;
							logger.info("- "+wp+" call of why-provenance");
							phi = this.ctx.mkAnd(phi, whyProvSet(nonExpectedTuples, pPlus, reasoner));
							logger.info("=============== Why Provenance End ================");
						}
					}
				} else {
					System.out.println("Cannot find solution: Non-termination");
					return null;
				}
			}
			result = this.consultSATSolver(phi);
			pPlus = this.derivePPlus(result);
			if (pPlus.size() == 0) loop = true;
		}
		
		System.out.println("Synthesis finished in "+iter+" iteration(s):");
		System.out.println("- "+wp+" call of why-provenance");
		System.out.println("- "+wnp+" call of why-not-provenance");
		System.out.println("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		if (debug) {
			System.out.println("why-provenance non-derive buggy: "+this.whyderivebuggy);
			System.out.println("why-provenance remain derive buggy: "+this.whyremainbuggy);
			System.out.println("why-not-provenance non-derive buggy: "+this.whynotderivebuggy);
			System.out.println("why-not-provenance remain derive buggy: "+this.whynotremainbuggy);
		}
		if (result != null) {
			return pPlus;
		} else {
			System.out.println("Cannot find solution.");
			return null;
		}
	}
}
