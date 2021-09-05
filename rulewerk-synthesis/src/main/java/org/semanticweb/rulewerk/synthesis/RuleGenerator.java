package org.semanticweb.rulewerk.synthesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class RuleGenerator {
	private List<Rule> metaRuleSet;
	private List<Predicate> inputRelation;
	private List<Predicate> outputRelation;
	private List<Predicate> inventedRelation;
	
	public RuleGenerator(List<Rule> metaRuleSet, List<Predicate> inputRelation, List<Predicate> outputRelation, List<Predicate> inventedRelation){
		this.metaRuleSet = metaRuleSet;
		this.inputRelation = inputRelation;
		this.outputRelation = outputRelation;
		this.inventedRelation = inventedRelation;
	}
	
	public List<Predicate> getPermutation(int n, List<Predicate> arr, int len, int L) {
		List<Predicate> res = new ArrayList<>();
		for (int i = 0; i < L; i++) {
			res.add(arr.get(n % len));
			n /= len;
		}
		return res;
	}

	public List<List<Predicate>> permutation(List<Predicate> arr, int len, int L) {
		List<List<Predicate>> res = new ArrayList<>();
		for (int i = 0; i < (int) Math.pow(len, L); i++) {
			res.add(getPermutation(i, arr, len, L));
		}
		return res;
	}
	
	public boolean varClauseRepeat(Rule metaR) {
		// Check whether in meta-rule r, a variable are repeated in a clause, e.g. Edge(?v0, ?v0)
		// Assumption: a meta-rule r does not contain any constant in its arguments.
		for (Literal l : metaR.getHead()) {
			List<Variable> headVars = l.getVariables().toList();
			if (headVars.size() < l.getPredicate().getArity()) return true;
		}
		for (Literal l : metaR.getBody()) {
			List<Variable> bodyVars = l.getVariables().toList();
			if (bodyVars.size() < l.getPredicate().getArity()) return true;
		}
		return false;
	}
	
	public boolean varAppearsOnce(Rule metaR) {
		// Check whether in meta-rule r, a variable appears exactly once.
		// Assumption: a meta-rule r does not contain any constant in its arguments.
		List<Variable> vars = metaR.getVariables().toList();
		List<Term> occurenceVars = new ArrayList<>();
		for (Literal l : metaR.getHead()) {
			occurenceVars.addAll(l.getArguments());
		}
		for (Literal l : metaR.getBody()) {
			occurenceVars.addAll(l.getArguments());
		}
		for (Variable v : vars) {
			if (Collections.frequency(occurenceVars, v) == 1) return true;
		}
		return false;
	}
	
	public boolean reoccurClause(Rule metaR) {
		// Check whether in meta-rule r, two clauses share same set of variables in same order.
		// Assumption: a meta-rule r does not contain any constant in its arguments.
		
		// In combination?
		List<Literal> clauses = new ArrayList<>(metaR.getBody().getLiterals());
		clauses.addAll(metaR.getHead().getLiterals());
		Set<Literal> clausesSet = new HashSet<>(clauses);
		if (clausesSet.size() < clauses.size()) return true;
		
		// Or separated by head and body?
		List<Literal> headClauses = new ArrayList<>(metaR.getHead().getLiterals());
		Set<Literal> headClausesSet = new HashSet<>(headClauses);
		if (headClausesSet.size() < headClauses.size()) return true;
		List<Literal> bodyClauses = new ArrayList<>(metaR.getBody().getLiterals());
		Set<Literal> bodyClausesSet = new HashSet<>(bodyClauses);
		if (bodyClausesSet.size() < bodyClauses.size()) return true;
		
		return false;
	}
		
	public List<Rule> simpleGenerator() {
		// Simple generator: assume meta rule set has the form P1(x0,...,xn) :- P2(..), ..., Pm(...).
		Map<Integer,List<List<Predicate>>> generatedPerm = new HashMap<>();
		List<Rule> result = new ArrayList<>();
		for (Rule metaRule : this.metaRuleSet) {
			int len = metaRule.getBody().getLiterals().size();
			List<List<Predicate>> perms;
			if (generatedPerm.containsKey(len)) {
				perms = generatedPerm.get(len);
			} else {
				List<Predicate> considered = new ArrayList<>(this.inputRelation);
				considered.addAll(this.outputRelation);
				considered.addAll(this.inventedRelation);
				perms = this.permutation(considered, considered.size(), len);
				generatedPerm.put(len, perms);
			}
			List<Predicate> forHead = new ArrayList<>(this.outputRelation);
			forHead.addAll(this.inventedRelation);
			for (Predicate h : forHead) {
				PositiveLiteral head;
				if (h.getArity() == metaRule.getHead().getLiterals().get(0).getPredicate().getArity()) {
					head = Expressions.makePositiveLiteral(h, metaRule.getHead().getLiterals().get(0).getArguments());
				} else break;
				for (List<Predicate> perm : perms) {
					List<Literal> body = new ArrayList<>();
					int idx = 0;
					boolean consider = (len != 1 || !head.getPredicate().equals(perm.get(idx)));
					while (idx < len && consider) {
						if (perm.get(idx).getArity() == metaRule.getBody().getLiterals().get(idx).getPredicate().getArity()) {
							body.add(idx, Expressions.makePositiveLiteral(perm.get(idx), metaRule.getBody().getLiterals().get(idx).getArguments()));
							idx++;
						} else break;
					}
					if (idx == len) {
						result.add(Expressions.makeRule(Expressions.makePositiveConjunction(head), Expressions.makeConjunction(body)));
					}
				}
			}
		}
		return result;
	}
}
