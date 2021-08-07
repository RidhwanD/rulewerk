package org.semanticweb.rulewerk.synthesis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	
	public int getRulewerkCall() {
		return this.rulewerkCall;
	}
	
	public int getZ3Call() {
		return this.z3Call;
	}
	
	public int getIteration() {
		return this.iteration;
	}
	
	// ============================================ CO-PROV UTILITIES ============================================== //
	
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
	
	public List<Statement> rulesFromExpPred() {
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
		return result;
	}
	
	public void transformToDatalogS() {
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
		
	private void initMapping() {
		// Format of boolean variable for r_i = vr_i
		// Format of constant for r_i = cr_i
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
	
	public BoolExpr initPhi() {
		List<BoolExpr> rules = new ArrayList<>(this.var2rule.keySet());
		BoolExpr disjVars = rules.get(0);
		if (rules.size() > 1) {
			for (BoolExpr b : rules) {
				disjVars = this.ctx.mkOr(disjVars, b);
			}
		}
		return disjVars;
	}
	
	private Set<Predicate> getEDB() {
		Set<Predicate> edb = new HashSet<>();
		for (Literal l : this.inputTuple) {
			edb.add(l.getPredicate());
		}
		return edb;
	}
	
	private static <T> List<List<T>> split(List<T> list, int numberOfParts) {
		List<List<T>> numberOfPartss = new ArrayList<>(numberOfParts);
		int size = list.size();
		int sizePernumberOfParts = (int) Math.ceil(((double) size) / numberOfParts);
		int leftElements = size;
		int i = 0;
		while (i < size && numberOfParts != 0) {
			numberOfPartss.add(list.subList(i, i + sizePernumberOfParts));
			i = i + sizePernumberOfParts;
			leftElements = leftElements - sizePernumberOfParts;
			sizePernumberOfParts = (int) Math.ceil(((double) leftElements) / --numberOfParts);
		}
		return numberOfPartss;
	}
	
	private static <T> List<List<T>> split2(List<T> list, int numberOfParts) {
		List<List<T>> numberOfPartss = new ArrayList<>(numberOfParts);
		float avg = list.size() / (float) numberOfParts;
		float last = 0;
        while (last < list.size()) {
            numberOfPartss.add(list.subList((int) last,(int) (last+avg)));
            last += avg;
        }
		return numberOfPartss;
	}
	
	private PositiveLiteral produceQuery(Predicate p) {
		List<Term> vars = new ArrayList<>();
		for (int i = 0; i < p.getArity(); i++) {
			vars.add(Expressions.makeUniversalVariable("X"+i));
		}
		return Expressions.makePositiveLiteral(p, vars);
	}
	
	// ============================================== WHY PROVENANCE =============================================== //
	
	public boolean whyProvDebugTool(PositiveLiteral t, List<Rule> wpResult, List<Rule> Pplus) throws IOException {
		System.out.println("Debug "+t+" for result "+wpResult);
		boolean satisfied = true;
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(this.inputTuple);
		kb.addStatements(wpResult);
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			if (ReasoningUtils.isDerived(t, reasoner) == 0) {
				System.out.println("NON-DERIVE: "+kb.getRules()+" not derive "+t);
				satisfied = false;
			}
		}
		kb.addStatements(Pplus);
		kb.removeStatements(wpResult);
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			if (ReasoningUtils.isDerived(t, reasoner) == 1) {
				System.out.println("REMAIN: "+kb.getRules()+" - remaining program still derive "+t);
				satisfied = false;
			}
		}
		return satisfied;
	}
	
	public List<Rule> whyProvDeltaAlt(PositiveLiteral t, List<Rule> Pplus) throws IOException {
		List<Rule> code = new ArrayList<>(Pplus);
		int d = 2;
		while (true) {
			for (List<Rule> codeChunk : split2(code, d)) {
				Set<Rule> currRPlus = new HashSet<>(codeChunk);
				boolean bugProduced = false;
				KnowledgeBase kb = new KnowledgeBase();
				kb.addStatements(currRPlus);
				kb.addStatements(this.inputTuple);
				try (final Reasoner reasoner = new VLogReasoner(kb)) {
					this.rulewerkCall++;
					reasoner.reason();
					if (ReasoningUtils.isDerived(t, reasoner) == 1) {
						bugProduced = true;
					}
				}
				if (bugProduced) {
					Pplus = new ArrayList<>(currRPlus);
				}
			}
			if (d == code.size())
                break;
			d = Math.min(code.size(), d*2);
			if (d == 0)
                break;
		}
		if (debug)
			System.out.println(whyProvDebugTool(t, Pplus, code));
		return Pplus;
	}
	
	public List<Rule> whyProvDelta(PositiveLiteral t, List<Rule> Pplus) throws IOException{
		logger.info("Investigate "+t);
		List<Rule> code = new ArrayList<>(Pplus);
		// Alternative of why provenance using the delta debugging
		int d = 2;
		while (d < Pplus.size() && d > 0) {
			List<List<Rule>> partition = split2(Pplus, d);
			logger.debug("Partition: "+partition);
			boolean deltaBuggy = false; boolean revDeltaBuggy = false;
			int idx = 0;
			while (idx < partition.size() && !deltaBuggy) {
				KnowledgeBase kb = new KnowledgeBase();
				kb.addStatements(partition.get(idx));
				kb.addStatements(this.inputTuple);
				try (final Reasoner reasoner = new VLogReasoner(kb)) {
					this.rulewerkCall++;
					reasoner.reason();
					if (ReasoningUtils.isDerived(t, reasoner) == 1) {
						Pplus = partition.get(idx);
						deltaBuggy = true;
						d = 2;
					}
				}
				idx += 1;
			}
			if (!deltaBuggy) {
				idx = 0;
				while (idx < partition.size() && !revDeltaBuggy) {
					KnowledgeBase kb = new KnowledgeBase();
					List<Rule> revDelta = new ArrayList<Rule>(Pplus);
					revDelta.removeAll(partition.get(idx));
					kb.addStatements(revDelta);
					kb.addStatements(this.inputTuple);
					try (final Reasoner reasoner = new VLogReasoner(kb)) {
						this.rulewerkCall++;
						reasoner.reason();
						if (ReasoningUtils.isDerived(t, reasoner) == 1) {
							Pplus.removeAll(partition.get(idx));
							revDeltaBuggy = true;
							d -= 1;
						}
					}
					idx += 1;
				}
			}
			if (!deltaBuggy && !revDeltaBuggy) {
				d *= 2;
			}
		}
		if (debug)
			System.out.println(whyProvDebugTool(t, Pplus, code));
		return Pplus;
	}
	
	public BoolExpr whyProvExpr(List<Rule> wp) {
		if (wp.size() > 0) {
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
			return this.ctx.mkTrue();
		}
	}
	
	// ============================================== WHY-NOT PROVENANCE =============================================== //
	
	public boolean whyNotProvDebugTool(PositiveLiteral t, Set<Rule> wnpResult) throws IOException {
		System.out.println("Debug "+t+" for result "+wnpResult);
		// Check based partially on Lemma 4.4
		boolean satisfied = true;
		List<Rule> PPlusDelta = new ArrayList<>(this.ruleSet);
		PPlusDelta.removeAll(wnpResult);
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(this.inputTuple);
		kb.addStatements(PPlusDelta);
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			if (ReasoningUtils.isDerived(t, reasoner) == 1) {
				System.out.println("REMAIN: Remainder derive "+t);
				satisfied = false;
//				System.exit(2);
			}
		}
		for (Rule r : wnpResult) {
			List<Rule> ppdr = new ArrayList<>(PPlusDelta);
			ppdr.add(r);
			KnowledgeBase kbr = new KnowledgeBase();
			kbr.addStatements(this.inputTuple);
			kbr.addStatements(ppdr);
			try (final Reasoner reasoner = new VLogReasoner(kbr)) {
				reasoner.reason();
				if (ReasoningUtils.isDerived(t, reasoner) == 0) {
					System.out.println("NON-DERIVE: "+r+" not derive "+t);
					satisfied = false;
//					System.exit(1);
				}
			}
		}
		return satisfied;
	}
	
	public List<Rule> whyNotProvAlt(PositiveLiteral t, List<Rule> inPplus) throws IOException {
//		FileWriter myWriter = new FileWriter(ReasoningUtils.INPUT_FOLDER + "small" + "/rulewerk-log-whynot.txt");
//		myWriter.write("WNP: Investigate "+t+" with Rules: "+inPplus+"\n");
		Set<Rule> Pmin = new HashSet<>(this.ruleSet);
		Pmin.removeAll(inPplus);
		Set<Rule> Pplus = new HashSet<>(inPplus);
		List<Rule> code = new ArrayList<>(Pmin);
		int d = 2;
		while (true) {
			for (List<Rule> codeChunk : split2(code, d)) {
				Set<Rule> currRMinus = new HashSet<>(Pmin);
				currRMinus.removeAll(codeChunk);
				Set<Rule> currRPlus = new HashSet<>(Pplus);
				currRPlus.addAll(codeChunk);
				boolean bugProduced = false;
				KnowledgeBase kb = new KnowledgeBase();
				kb.addStatements(currRPlus);
				kb.addStatements(this.inputTuple);
				try (final Reasoner reasoner = new VLogReasoner(kb)) {
					this.rulewerkCall++;
					reasoner.reason();
					if (ReasoningUtils.isDerived(t, reasoner) == 0) {
						bugProduced = true;
					}
				}
//				myWriter.write("d: "+d+" with chunk: "+codeChunk+"\n");
//				myWriter.write("currRMinus: "+currRMinus+"\n");
//				myWriter.write("currRPlus: "+currRPlus+"\n");
				if (bugProduced) {
//					myWriter.write("BUGGY\n");
					Pplus = new HashSet<>(currRPlus);
					Pmin = new HashSet<>(currRMinus);
				}
//				myWriter.write("------------------------------------------------------------\n");
			}
			if (d == code.size())
                break;
			d = Math.min(code.size(), d*2);
			if (d == 0)
                break;
		}
//		myWriter.close();
		if (debug)
			System.out.println(whyNotProvDebugTool(t, Pmin));
		return new ArrayList<>(Pmin);
	}
	
	public List<Rule> whyNotProv(PositiveLiteral t, List<Rule> Pplus) throws IOException{
		logger.info("Investigate "+t);
		// Use the delta debugging here
		List<Rule> Pmin = new ArrayList<>(this.ruleSet);
		Pmin.removeAll(Pplus);
		int d = 2;
		while (d <= Pmin.size() && d > 0) {
//			System.out.println("Pmin: "+Pmin);
			List<List<Rule>> partition = split(Pmin, d);	
			logger.debug("Partition: "+partition);
//			System.out.println("d: "+d+" with partition: "+partition);
			boolean deltabuggy = false; boolean revdeltabuggy = false;
			for (List<Rule> chunk : partition) {
				KnowledgeBase kb = new KnowledgeBase();
				List<Rule> deltaBuggy = new ArrayList<Rule>(this.ruleSet);
				deltaBuggy.removeAll(chunk);
//				System.out.println("Chunk: "+chunk+", \n -- deltaBuggy: "+deltaBuggy);
				kb.addStatements(deltaBuggy);
				kb.addStatements(this.inputTuple);
				try (final Reasoner reasoner = new VLogReasoner(kb)) {
					this.rulewerkCall++;
					reasoner.reason();
					if (ReasoningUtils.isDerived(t, reasoner) == 0) {
//						System.out.println("-- buggy");
						deltabuggy = true;
						Pmin = chunk;
						d = 2;
						break;
					}
				}
			}
			if (!deltabuggy) {
				for (List<Rule> chunk : partition) {
					KnowledgeBase kb = new KnowledgeBase();
					List<Rule> revDelta = new ArrayList<Rule>(Pmin);
					revDelta.removeAll(chunk);
					List<Rule> revDeltaBuggy = new ArrayList<Rule>(this.ruleSet);
					revDeltaBuggy.removeAll(revDelta);
//					System.out.println("Chunk: "+chunk+", \n -- revDeltaBuggy: "+revDeltaBuggy);
					kb.addStatements(revDeltaBuggy);
					kb.addStatements(this.inputTuple);
					try (final Reasoner reasoner = new VLogReasoner(kb)) {
						this.rulewerkCall++;
						reasoner.reason();
						if (ReasoningUtils.isDerived(t, reasoner) == 0) {
//							System.out.println("-- buggy");
							revdeltabuggy = true;
							Pmin.removeAll(chunk);
							d -= 1;
							break;
						}
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
	
	public BoolExpr whyNotProvExpr(List<Rule> wnp) {
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
	
	public List<Statement> getRelevantExistNeg(List<Rule> Pplus) {
		List<Statement> rel = new ArrayList<Statement>();
		for (Rule r : Pplus) {
			rel.add(this.ruleSetExistNeg.get(this.ruleSet.indexOf(r)));
		}
		return rel;
	}
	
	public List<Rule> coprovInv(Fact t, List<Rule>Pplus) {
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
				this.rulewerkCall++;
				reasoner.reason();
				long generate = ReasoningUtils.isDerived(newt, reasoner);
				if (generate == 1) coprovIn.add(r);
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
	
	public BoolExpr whyNotCoProvExpr(List<Rule> pPlus, List<Rule> cpr) {
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
	
	// =========================================== Synthesis Process ======================================== //
	
	public List<Fact> getNonExpectedResults(Reasoner reasoner) {
		List<Fact> NOutput = new ArrayList<Fact>();
		for (Predicate p : this.expPred) {
			for (Fact f :ReasoningUtils.getQueryAnswerAsListWhyProv(produceQuery(p), reasoner)) {
				if (!this.outputPTuple.contains(f)) NOutput.add(f);
			}
		}
		return NOutput;
	}
	
	public List<Fact> getNonExpectedResultsDecl(Reasoner reasoner) {
		List<Fact> NOutput = new ArrayList<Fact>();
		for (Fact t : this.outputNTuple) {
			if (ReasoningUtils.isDerived(t, reasoner) == 1) {
				NOutput.add(t);
			}
		}
		return NOutput;
	}
	
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
				reasoner.reason();
				int newWhyNots = 0;
				for (Fact t : this.outputPTuple) {
					long generate = ReasoningUtils.isDerived(t, reasoner);
					if (generate == 0) {
						loop = true;
						if (pPlus.size() < this.ruleSet.size()) {
							logger.info("============= Perform Why Not Provenance ==============");
							wnp++; newWhyNots++;
							System.out.println("- "+wnp+" call of why-not-provenance");
							phi = this.ctx.mkAnd(phi, this.whyNotProvExpr(this.whyNotProvAlt(t, pPlus)));
							logger.info("=============== Why Not Provenance End ================");
						}
					} else if (generate == 1) {
						if (this.coprov) {
							if (pPlus.size() < this.ruleSet.size() && pPlus.size() > 0) {
								logger.info("============= Perform Co-Provenance ==============");
								cp++;
								System.out.println("- "+cp+" call of co-provenance");
								phi = this.ctx.mkAnd(phi, this.whyNotCoProvExpr(pPlus, this.coprovInv(t, pPlus)));
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
						System.out.println("- "+wp+" call of why-provenance");
						phi = this.ctx.mkAnd(phi, this.whyProvExpr(this.whyProvDeltaAlt(f, pPlus)));
						logger.info("=============== Why Provenance End ================");
					}
					if (newWhys >= 3) break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = this.consultSATSolver(phi);
			if (result != null) {
				pPlus = this.derivePPlus(result);
				if (pPlus.size() == 0) loop = true;
			}
			System.out.println("Iteration "+iter+" complete.");
			System.out.println("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		}
		this.iteration = iter;
		if (result != null) {
			System.out.println("Synthesis finished in "+iter+" iteration(s):");
			System.out.println("- "+wp+" call of why-provenance");
			System.out.println("- "+wnp+" call of why-not-provenance");
			System.out.println("- "+cp+" call of co-provenance");
			return pPlus;
		} else {
			System.out.println("Cannot find solution.");
			return null;
		}
	}
	
	// ============================================== WHY PROVENANCE SET =============================================== //
	
	public List<Statement> getTransFromPlus(List<Rule> Pplus) {
		List<Statement> transFromPlus = new ArrayList<>();
		for (Rule r : Pplus) {
			transFromPlus.addAll(this.ruleSetTrans.get(r));
			transFromPlus.add(Expressions.makeFact("Rule", this.rule2const.get(r)));
		}
		return transFromPlus;
	}
	
	public boolean debugSetTool(PositiveLiteral t, List<Term> wpResult) throws IOException {
		boolean satisfied = true;
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(this.inputTuple);
		for (Term term : wpResult) {
			kb.addStatement(this.const2rule.get(term));
		}
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			if (ReasoningUtils.isDerived(t, reasoner) == 0) {
				System.out.println("NON-DERIVE: "+kb.getRules()+" not derive "+t);
				satisfied = false;
			}
		}
		return satisfied;
	}
	
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
			reasoner.reason();
			if (ReasoningUtils.isDerived(t, reasoner) == 1) {
				System.out.println("DERIVE: "+kb.getRules()+" still derive "+t);
				satisfied = false;
			}
		}
		return satisfied;
	}
	
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
				reasoner.reason();
				int newWhyNots = 0;
				for (Fact t : this.outputPTuple) {
					long generate = ReasoningUtils.isDerived(t, reasoner);
					if (generate == 0) {
						loop = true;
						if (pPlus.size() < this.ruleSet.size()) {
							logger.info("============= Perform Why Not Provenance ==============");
							wnp++; newWhyNots++;
							System.out.println("- "+wnp+" call of why-not-provenance");
							phi = this.ctx.mkAnd(phi, this.whyNotProvExpr(this.whyNotProv(t, pPlus)));
							logger.info("=============== Why Not Provenance End ================");
						}
					} else if (generate == 1) {
						if (this.coprov) {
							if (pPlus.size() < this.ruleSet.size() && pPlus.size() > 0) {
								logger.info("============= Perform Co-Provenance ==============");
								cp++;
								System.out.println("- "+cp+" call of co-provenance");
								phi = this.ctx.mkAnd(phi, this.whyNotCoProvExpr(pPlus, this.coprovInv(t, pPlus)));
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
						System.out.println("- "+wp+" call of why-provenance");
						phi = this.ctx.mkAnd(phi, whyProvSet(nonExpectedTuples, pPlus, reasoner));
						logger.info("=============== Why Provenance End ================");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = this.consultSATSolver(phi);
			if (result != null) {
				pPlus = this.derivePPlus(result);
				if (pPlus.size() == 0) loop = true;
			}
			System.out.println("Iteration "+iter+" complete.");
			System.out.println("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
		}
		this.iteration = iter;
		if (result != null) {
			System.out.println("Synthesis finished in "+iter+" iteration(s):");
			System.out.println("- "+wp+" call of why-provenance");
			System.out.println("- "+wnp+" call of why-not-provenance");
			System.out.println("- "+cp+" call of co-provenance");
			return pPlus;
		} else {
			System.out.println("Cannot find solution.");
			return null;
		}
	}
	
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
				reasoner.reason();
				this.rulewerkCall++;
				// Ensure all expected output are generated.
				for (Fact f : this.outputPTuple) {
					long generate = ReasoningUtils.isDerived(f, reasoner);
					if (iter == 1 || (iter > 1 && generate == 0)) {
						logger.info("============= Perform Why Not Provenance ==============");
						wnp++; loop = true;
						System.out.println("- "+wnp+" call of why-not-provenance");
						phi = this.ctx.mkAnd(phi, this.whyNotProvSet(f, pPlus, reasoner));
						logger.info("=============== Why Not Provenance End ================");
					}	
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
						System.out.println("- "+wp+" call of why-provenance");
						phi = this.ctx.mkAnd(phi, whyProvSet(nonExpectedTuples, pPlus, reasoner));
						logger.info("=============== Why Provenance End ================");
					}
				}
			}
			result = this.consultSATSolver(phi);
			pPlus = this.derivePPlus(result);
			if (pPlus.size() == 0) loop = true;
		}
		if (result != null) {
			System.out.println("Synthesis finished in "+iter+" iteration(s):");
			System.out.println("- "+wp+" call of why-provenance");
			System.out.println("- "+wnp+" call of why-not-provenance");
			System.out.println("Made "+this.z3Call+" calls to Z3 and "+this.rulewerkCall+" calls to Rulewerk.");
			return pPlus;
		} else {
			System.out.println("Cannot find solution.");
			return null;
		}
	}
}
