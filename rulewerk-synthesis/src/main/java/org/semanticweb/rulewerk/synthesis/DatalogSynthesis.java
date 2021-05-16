package org.semanticweb.rulewerk.synthesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class DatalogSynthesis {
	private List<Fact> inputTuple;
	private List<Fact> outputPTuple;
	private List<Fact> outputNTuple;
	private List<Rule> ruleSet;

	public DatalogSynthesis(List<Fact> inputTuple, List<Fact> outputPTuple, List<Fact> outputNTuple, List<Rule> ruleSet){
		this.inputTuple = inputTuple;
		this.outputPTuple = outputPTuple;
		this.outputNTuple = outputNTuple;
		this.ruleSet = ruleSet;
		ReasoningUtils.configureLogging(); // use simple logger for the example
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
		
		for (Fact f : this.inputTuple) {
			Predicate p = f.getPredicate();
			if (!storedPred.contains(p)) {
				storedPred.add(p);
				Predicate newp = Expressions.makePredicate(p.getName()+"_en", p.getArity()+1);
				Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(newp, x,y,z), Expressions.makePositiveLiteral(p, x,y));
				enSimp.add(r);
			}
		}
		return enSimp;
	}
	
	public List<Rule> getExistNeg(List<Rule> Pplus){
		List<Rule> enList = new ArrayList<Rule>();
		for (Rule r : Pplus) {
			enList.add(this.transformRule(r));
		}
		enList.addAll(this.transformInput());
		return enList;
	}
	
	public List<Rule> whyProv(Term t, List<Rule> Pplus){
		// should return list of rules that produce term t.
		return Pplus;
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
		// Use the delta debugging here
		int d = 2;
		while (d <= Pmin.size()) {
			List<List<Rule>> partition = this.split(Pmin, d);
			boolean deltabuggy = false; boolean revdeltabuggy = false;
			int idx = 0; int iddbuggy = 0; int idrbuggy = 0;
			while (idx < partition.size() && !deltabuggy) {
				KnowledgeBase kb = new KnowledgeBase();
				List<Rule> buggy = new ArrayList<Rule>(this.ruleSet);
				buggy.removeAll(partition.get(idx));
				kb.addStatements(buggy);
				try (final Reasoner reasoner = new VLogReasoner(kb)) {
					reasoner.reason();
					long generate = ReasoningUtils.printOutQueryCount(t, reasoner);
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
				try (final Reasoner reasoner2 = new VLogReasoner(kb2)) {
					reasoner2.reason();
					long generate = ReasoningUtils.printOutQueryCount(t, reasoner2);
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
	
	public List<Rule> coprovInv(PositiveLiteral t, List<Rule>Pplus) throws IOException {
		List<Rule> enRules = this.getExistNeg(Pplus);
		List<Rule> coprov = new ArrayList<Rule>();
		Predicate p = t.getPredicate();
		for (Rule r: Pplus) {
			List<Term> args = new ArrayList<Term>();
			for (Term term : t.getArguments()){
				args.add(term);
			}
			args.add(this.getRuleConstant(r));
			PositiveLiteral newt = Expressions.makePositiveLiteral(p, args);
			KnowledgeBase kb = new KnowledgeBase();
			kb.addStatements(enRules);
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				reasoner.reason();
				long generate = ReasoningUtils.printOutQueryCount(newt, reasoner);
				if (generate == 1) coprov.add(r);
			}
		}
		return coprov;
	}
}
