package org.semanticweb.rulewerk.synthesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import com.microsoft.z3.*;

public class DatalogSynthesis {
	private List<Fact> inputTuple;
	private List<Fact> outputPTuple;
	private List<Fact> outputNTuple;
	private List<Rule> ruleSet;
	private Map<BoolExpr, Rule> var2rule;
	private Map<Rule, BoolExpr> rule2var;
	private Context ctx;
	private double coprovChance = 0.05; 		// Set probability of coprov being performed.
	private static final Logger logger = LogManager.getLogger("HelloWorld");

	public DatalogSynthesis(List<Fact> inputTuple, List<Fact> outputPTuple, List<Fact> outputNTuple, List<Rule> ruleSet, Context ctx){
		this.inputTuple = inputTuple;
		this.outputPTuple = outputPTuple;
		this.outputNTuple = outputNTuple;
		this.ruleSet = ruleSet;
		this.ctx = ctx;
		var2rule = new HashMap<BoolExpr, Rule>();
		rule2var = new HashMap<Rule, BoolExpr>();
		this.setVarRuleMapping(ruleSet);
		ReasoningUtils.configureLogging(); // use simple logger for the example
	}
	
	private void setVarRuleMapping(List<Rule> ruleSet) {
		// Formatting of boolean variable for r_i = vr_i
		int idx = 1;
		for (Rule r : ruleSet) {
			BoolExpr v = ctx.mkBoolConst("vr_"+idx);
			this.var2rule.put(v, r);
			this.rule2var.put(r, v);
			idx++;
		}
	}
	
	public Term getRuleConstant(Rule r) {
		return Expressions.makeAbstractConstant("Rule_Constant_"+(this.ruleSet.indexOf(r)+1));
	}
	
	public Rule transformRule(Rule rule) {
		Conjunction<PositiveLiteral> head = rule.getHead();
		Conjunction<Literal> body = rule.getBody();
		Variable var = Expressions.makeUniversalVariable("r1");
		int i = 1;
		while (rule.getVariables().anyMatch(var::equals)) {
			i++;
			var = Expressions.makeUniversalVariable("r"+String.valueOf(i));
		}
		List<PositiveLiteral> nh = new ArrayList<PositiveLiteral>();
		for (PositiveLiteral pl : head) {
			List<Term> args = new ArrayList<Term>();
			for (Term t : pl.getArguments()){
				args.add(t);
			}
			args.add(var);
			PositiveLiteral newpl = Expressions.makePositiveLiteral(
					Expressions.makePredicate(pl.getPredicate().getName()+"_en", pl.getPredicate().getArity()+1), args);
			nh.add(newpl);
		}
		List<Literal> nb = new ArrayList<Literal>();
		for (Literal pl : body) {
			List<Term> args = new ArrayList<Term>();
			for (Term t : pl.getArguments()){
				args.add(t);
			}
			args.add(var);
			Literal newpl = Expressions.makePositiveLiteral(
					Expressions.makePredicate(pl.getPredicate().getName()+"_en", pl.getPredicate().getArity()+1), args);
			nb.add(newpl);
		}
		nb.add(Expressions.makeNegativeLiteral(
				Expressions.makePredicate("Equal", 2), Arrays.asList(var, getRuleConstant(rule))));
		return Expressions.makeRule(Expressions.makeConjunction(nh), Expressions.makeConjunction(nb));
	}
	
	public List<Rule> transformInput() {
		List<Rule> enSimp = new ArrayList<Rule>();
		List<Predicate> storedPred = new  ArrayList<Predicate>();
		final UniversalVariable x = Expressions.makeUniversalVariable("X");
		final UniversalVariable y = Expressions.makeUniversalVariable("Y");
		final UniversalVariable z = Expressions.makeUniversalVariable("Z");
		Predicate iRP = Expressions.makePredicate("isRulePred", 1);
		for (Fact f : this.inputTuple) {
			Predicate p = f.getPredicate();
			if (!storedPred.contains(p)) {
				storedPred.add(p);
				Predicate newp = Expressions.makePredicate(p.getName()+"_en", p.getArity()+1);
				Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(newp, x,y,z), Expressions.makePositiveLiteral(p, x,y), Expressions.makePositiveLiteral(iRP, z));
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
			enList.add(Expressions.makeFact(iRP,getRuleConstant(r)));
			Predicate eq = Expressions.makePredicate("Equal", 2);
			enList.add(Expressions.makeFact(eq,getRuleConstant(r),getRuleConstant(r)));
		}
		enList.addAll(this.transformInput());
		return enList;
	}
	
	public List<Rule> whyProv(PositiveLiteral t, List<Rule> Pplus){
		logger.info("Investigate "+t);
		// should return list of rules that produce term t.
		return Pplus;
	}
	
	public List<Rule> whyProvAlt(PositiveLiteral t, List<Rule> Pplus) throws IOException{
		logger.info("Investigate "+t);
		// Alternative of why provenance  the delta debugging here
		int d = 2;
		while (d <= Pplus.size()) {
			List<List<Rule>> partition = split(Pplus, d);
			logger.debug("Partition: "+partition);
			boolean deltabuggy = false; boolean revdeltabuggy = false;
			int idx = 0; int iddbuggy = 0; int idrbuggy = 0;
			while (idx < partition.size() && !deltabuggy) {
				KnowledgeBase kb = new KnowledgeBase();
				kb.addStatements(partition.get(idx));
				kb.addStatements(this.inputTuple);
				try (final Reasoner reasoner = new VLogReasoner(kb)) {
					logger.debug("Using knowledge base 1 containing ");
					for (Statement s : kb.getStatements()) {
						logger.debug("- "+s);
					}
					reasoner.reason();
					long generate = ReasoningUtils.isDerived(t, reasoner);
					if (generate == 1) {
						deltabuggy = true;
						iddbuggy = idx;
					}
				}
				if (!deltabuggy) {
					KnowledgeBase kb2 = new KnowledgeBase();
					List<Rule> revDelta = new ArrayList<Rule>(Pplus);
					revDelta.removeAll(partition.get(idx));
					kb2.addStatements(revDelta);
					kb2.addStatements(this.inputTuple);
					try (final Reasoner reasoner2 = new VLogReasoner(kb2)) {
						logger.debug("Using knowledge base 2 containing ");
						for (Statement s : kb.getStatements()) {
							logger.debug("- "+s);
						}
						reasoner2.reason();
						long generate = ReasoningUtils.isDerived(t, reasoner2);
						if (generate == 1) {
							revdeltabuggy = true;
							idrbuggy = idx;
						}
					}
				}
				idx += 1;
			}
			if (deltabuggy) {
				Pplus = partition.get(iddbuggy);
				d = 2;
			} else if (revdeltabuggy) {
				List<Rule> revDelta = new ArrayList<Rule>(Pplus);
				revDelta.removeAll(partition.get(idrbuggy));
				Pplus = revDelta;
				d -= 1;
			} else d *= 2;
		}
		
		return Pplus;
	}
	
	public BoolExpr whyProvExpr(List<Rule> wp) {
		BoolExpr conjVars = this.rule2var.get(wp.get(0));
		if (wp.size() > 1) {
			for (Rule r : wp.subList(1, wp.size())) {
				conjVars = this.ctx.mkAnd(conjVars, this.rule2var.get(r));
			}
		}
		BoolExpr negConjVars = this.ctx.mkNot(conjVars);
		return negConjVars;
	}
	
	public static <T> List<List<T>> split(List<T> list, int numberOfParts) {
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
	
	public List<Rule> whyNotProv(PositiveLiteral t, List<Rule> Pmin) throws IOException{
		logger.info("Investigate "+t);
		// Use the delta debugging here
		int d = 2;
		while (d <= Pmin.size()) {
			List<List<Rule>> partition = split(Pmin, d);
			logger.debug("Partition: "+partition);
			boolean deltabuggy = false; boolean revdeltabuggy = false;
			int idx = 0; int iddbuggy = 0; int idrbuggy = 0;
			while (idx < partition.size() && !deltabuggy) {
				KnowledgeBase kb = new KnowledgeBase();
				List<Rule> buggy = new ArrayList<Rule>(this.ruleSet);
				buggy.removeAll(partition.get(idx));
				kb.addStatements(buggy);
				kb.addStatements(this.inputTuple);
				try (final Reasoner reasoner = new VLogReasoner(kb)) {
					reasoner.reason();
					long generate = ReasoningUtils.isDerived(t, reasoner);
					if (generate == 0) {
						deltabuggy = true;
						iddbuggy = idx;
					}
				}
				KnowledgeBase kb2 = new KnowledgeBase();
				List<Rule> revDelta = new ArrayList<Rule>(Pmin);
				revDelta.removeAll(partition.get(idx));
				List<Rule> buggy2 = new ArrayList<Rule>(this.ruleSet);
				buggy2.removeAll(revDelta);
				kb2.addStatements(buggy2);
				kb2.addStatements(this.inputTuple);
				try (final Reasoner reasoner2 = new VLogReasoner(kb2)) {
					reasoner2.reason();
					long generate = ReasoningUtils.isDerived(t, reasoner2);
					if (generate == 0) {
						revdeltabuggy = true;
						idrbuggy = idx;
					}
				}
				idx += 1;
			}
			if (deltabuggy) {
				Pmin = partition.get(iddbuggy);
				d = 2;
			} else if (revdeltabuggy) {
				List<Rule> revDelta = new ArrayList<Rule>(Pmin);
				revDelta.removeAll(partition.get(idrbuggy));
				Pmin = revDelta;
				d -= 1;
			} else d *= 2;
		}
		
		return Pmin;
	}
	
	public BoolExpr whyNotProvExpr(List<Rule> wnp) {
		BoolExpr disjVars = this.rule2var.get(wnp.get(0));
		if (wnp.size() > 1) {
			for (Rule r : wnp.subList(1, wnp.size())) {
				disjVars = this.ctx.mkOr(disjVars, this.rule2var.get(r));
			}
		}
		return disjVars;
	}
	
	public List<Rule> coprovInv(Fact t, List<Rule>Pplus) {
		logger.info("Investigate "+t);
		List<Statement> enRules = this.getExistNeg(Pplus);
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
			args.add(this.getRuleConstant(r));
			Predicate newp = Expressions.makePredicate(p.getName()+"_en", p.getArity()+1);
			Fact newt = Expressions.makeFact(newp, args);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(enRules);
			kb.addStatements(this.inputTuple);
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
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
	
	public BoolExpr whyNotCoProvExpr(List<Rule> pMin, List<Rule> cpr) {
		BoolExpr disjVars = this.rule2var.get(pMin.get(0));
		if (pMin.size() > 1) {
			for (Rule r : pMin.subList(1, pMin.size())) {
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
		return coprov;
	}
	
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
	
	public List<Rule> derivePMinus(Model m) {
		List<Rule> pMin = new ArrayList<Rule>();
		for (Rule r : this.ruleSet) {
			Expr<BoolSort> truth = m.getConstInterp(this.rule2var.get(r));
			if (truth == null || truth.isFalse()) {
				pMin.add(r);
			}
		}
		return pMin;
	}
	
	public Model consultSATSolver(BoolExpr expr) {
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
	
	public List<Rule> synthesis() {
		int wp = 0; int wnp = 0; int cp = 0;
		BoolExpr phi = this.ctx.mkTrue();
		List<Rule> pPlus = this.ruleSet;
		List<Rule> pMin = new ArrayList<Rule>();
		Model result = this.consultSATSolver(phi);
		boolean loop = true; int iter = 0;
		while (result != null && loop) {
			iter++;
			loop = false;
			logger.debug("P+:");
			for (Rule r:pPlus)
				logger.debug("- "+r);
			logger.debug("P-:");
			for (Rule r:pMin)
				logger.debug("- "+r);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(pPlus);
			kb.addStatements(this.inputTuple);
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				reasoner.reason();
				for (Fact t : this.outputPTuple) {
					long generate = ReasoningUtils.isDerived(t, reasoner);
					if (generate == 0) {
						loop = true;
						if (pMin.size() > 0) {
							logger.info("============= Perform Why Not Provenance ==============");
							phi = this.ctx.mkAnd(phi, this.whyNotProvExpr(this.whyNotProv(t, pMin)));
							wnp++;
							logger.info("=============== Why Not Provenance End ================");
						}
					} else if (generate == 1) {
						Random rand = new Random();
						double n = rand.nextInt(100)/100;
						if (n <= this.coprovChance) {
							if (pMin.size() > 0 && pPlus.size() > 0) {
								logger.info("============= Perform Co-Provenance ==============");
								phi = this.ctx.mkAnd(phi, this.whyNotCoProvExpr(pMin, this.coprovInv(t, pPlus)));
								cp++;
								logger.info("=============== Co-Provenance End ================");
							}
						}
					}
				}
				for (Fact t : this.outputNTuple) {
					long generate = ReasoningUtils.isDerived(t, reasoner);
					if (generate == 1) {
						loop = true;
						if (pPlus.size() > 0) {
							logger.info("============= Perform Why Provenance ==============");
							phi = this.ctx.mkAnd(phi, this.whyProvExpr(this.whyProvAlt(t, pPlus)));
							wp++;
							logger.info("=============== Why Provenance End ================");
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result = this.consultSATSolver(phi);
			if (result != null) {
				pPlus = this.derivePPlus(result);
				pMin = this.derivePMinus(result);
			}
		}
		if (result != null) {
			System.out.println("Synthesis finished in "+iter+" iteration(s):");
			System.out.println("- "+wp+" call of why-provenance");
			System.out.println("- "+wnp+" call of why-not-provenance");
			System.out.println("- "+cp+" call of co-provenance");
			return pPlus;
		} else {
			System.out.println("No solutions found.");
			return null;
		}
	}
}
