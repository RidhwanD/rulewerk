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
	// Assumption: meta rule is of the form P_0(...) :- P_1(...), ..., P_n(...).
	private List<Rule> metaRuleSet;
	private List<Predicate> inputRelation;
	private List<Predicate> outputRelation;
	private List<Predicate> inventedRelation;
	private int maxArity;
	
	public RuleGenerator(List<Rule> metaRuleSet, List<Predicate> inputRelation, List<Predicate> outputRelation, List<Predicate> inventedRelation){
		this.metaRuleSet = metaRuleSet;
		this.inputRelation = inputRelation;
		this.outputRelation = outputRelation;
		this.inventedRelation = inventedRelation;
		this.maxArity = this.getMaximalArity();
	}
	
	public int getMaximalArity() {
		int maxArity = 0;
		for (Predicate p : inputRelation) {
			if (p.getArity() > maxArity) maxArity = p.getArity();
		}
		for (Predicate p : outputRelation) {
			if (p.getArity() > maxArity) maxArity = p.getArity();
		}
		for (Predicate p : inventedRelation) {
			if (p.getArity() > maxArity) maxArity = p.getArity();
		}
		return maxArity;
	}
	
	public List<Term> extractLiterals(Rule r) {
		List<Term> resVars = new ArrayList<>();
		for (Literal l : r.getHead().getLiterals()) {
			resVars.addAll(l.getArguments());
		}
		for (Literal l : r.getBody().getLiterals()) {
			resVars.addAll(l.getArguments());
		}
		return resVars;
	}
	
	public List<Predicate> extractPredicates(Rule r) {
		List<Predicate> resPreds = new ArrayList<>();
		for (Literal l : r.getHead().getLiterals()) {
			resPreds.add(l.getPredicate());
		}
		for (Literal l : r.getBody().getLiterals()) {
			resPreds.add(l.getPredicate());
		}
		return resPreds;
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
	
	// TODO: make a generic T permutation
	public List<Term> getVarPermutation(int n, List<Variable> arr, int len, int L) {
		List<Term> res = new ArrayList<>();
		for (int i = 0; i < L; i++) {
			res.add(arr.get(n % len));
			n /= len;
		}
		return res;
	}

	public List<List<Term>> permutationVar(List<Variable> arr, int len, int L) {
		List<List<Term>> res = new ArrayList<>();
		for (int i = 0; i < (int) Math.pow(len, L); i++) {
			res.add(getVarPermutation(i, arr, len, L));
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
		List<Term> occurenceVars = this.extractLiterals(metaR);
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
	
	public boolean isArityMatch(Literal l) {
		for (Predicate p : inputRelation) {
			if (p.getArity() == l.getPredicate().getArity()) return true;
		}
		for (Predicate p : outputRelation) {
			if (p.getArity() == l.getPredicate().getArity()) return true;
		}
		for (Predicate p : inventedRelation) {
			if (p.getArity() == l.getPredicate().getArity()) return true;
		}
		return false;
	}
	
	public boolean canBeInstantiated(Rule metaR) {
		Literal head = metaR.getHead().getLiterals().get(0);
		List<Literal> body = metaR.getBody().getLiterals();
		boolean res = this.isArityMatch(head);
		for (Literal l : body) {
			res = res && this.isArityMatch(l);
		}
		return res;
	}
	
	public boolean isValid(Rule metaR) {
		if (varClauseRepeat(metaR)) return false;
		if (varAppearsOnce(metaR)) return false;
		if (reoccurClause(metaR)) return false;
		if (!canBeInstantiated(metaR)) return false;
		return true;
	}
	
	public Rule removeArgument(Rule metaR, Variable v) {
		// Remove variable v from meta-rule r.
		if (!metaR.getVariables().toList().contains(v)) {
			return metaR;
		} else {
			List<PositiveLiteral> head = new ArrayList<>();
			for (Literal l : metaR.getHead()) {
				List<Term> terms = new ArrayList<>(l.getArguments());
				terms.remove(v);
				head.add(Expressions.makePositiveLiteral(l.getPredicate().getName(), terms));
			}
			List<Literal> body = new ArrayList<>();
			for (Literal l : metaR.getBody()) {
				List<Term> terms = new ArrayList<>(l.getArguments());
				terms.remove(v);
				body.add(Expressions.makePositiveLiteral(l.getPredicate().getName(), terms));
			}
			Rule newR = Expressions.makeRule(Expressions.makeConjunction(head), Expressions.makeConjunction(body));
			if (isValid(newR)) return newR;
			else return metaR;
		}
	}
	
	public List<Rule> addBodyArgToBody(Rule metaR) {
		// Given a meta-rule r and a variable v that only appear in body. Add v to head of r.
		Set<Variable> headVars = new HashSet<>(metaR.getHead().getVariables().toList());
		Set<Variable> bodyVars = new HashSet<>(metaR.getBody().getVariables().toList());
		List<Rule> result = new ArrayList<>();
		bodyVars.removeAll(headVars);
		if (bodyVars.size() == 0)
			return result;
		else {
			List<Variable> vars = new ArrayList<>(bodyVars);
			Literal head = metaR.getHead().getLiterals().get(0);
			for (int j = 0; j < vars.size(); j++) {
				Variable toAdd = vars.get(j);
				for (int i = 0; i <= head.getPredicate().getArity(); i++) {
					List<Term> added = new ArrayList<>(head.getArguments());
					added.add(i, toAdd);
					if (added.size() <= this.maxArity) {
						result.add(Expressions.makeRule(
								Expressions.makePositiveConjunction(Expressions.makePositiveLiteral(head.getPredicate().getName(), added)), 
								metaR.getBody()));
					}
				}
			}
			return result;
		}
	}
	
	public List<Rule> expandBodyArgs(Rule metaR) {
		// Given a meta-rule r and a variable v that only appear in body. 
		// Add v to head of r as long as there is variables to be added, while still below the maximum arity.
		List<Rule> temp = this.addBodyArgToBody(metaR);
		List<Rule> result = new ArrayList<>();
		for (Rule r : temp) {
			if (!this.existSimilar(result, r))
				result.add(r);
		}
		while (temp.size() > 0) {
			List<Rule> accumulator = new ArrayList<>();
			for (Rule r : temp) {
				List<Rule> res = this.addBodyArgToBody(r);
				if (res != null)
					accumulator.addAll(res);
			}
			temp = accumulator;
			for (Rule r : temp) {
				if (!this.existSimilar(result, r))
					result.add(r);
			}
		}
		return result;
	}
	
	public List<Rule> renameVars(Rule metaR, Variable v) {
		// Assumption: v does not appear in r 
		List<List<Term>> vars = new ArrayList<>();
		vars.add(this.extractLiterals(metaR));
		List<List<Term>> resVars = new ArrayList<>();
		for (int j = 0; j < this.extractLiterals(metaR).size(); j++) {
			List<List<Term>> temp = new ArrayList<>();
			for (List<Term> vs: vars) {
				for (int i = 0; i < vs.size(); i++) {
					List<Term> temp2 = new ArrayList<>(vs);
					if (!v.equals(temp2.get(i))) {
						temp2.set(i, v);
						if (!resVars.contains(temp2))
							resVars.add(temp2);
						temp.add(temp2);
					}
				}
			}
			vars = temp;
		}
		List<Rule> result = new ArrayList<>();
		Predicate head = metaR.getHead().getLiterals().get(0).getPredicate();
		List<Literal> body = metaR.getBody().getLiterals();
		for (List<Term> varPerm : resVars) {
			List<Literal> bodies = new ArrayList<>();
			int start = head.getArity();
			int end = start;
			for (int i = 0; i < body.size(); i++) {
				if (i != 0) start += body.get(i-1).getPredicate().getArity();
				end +=  body.get(i).getPredicate().getArity();
				bodies.add(Expressions.makePositiveLiteral(body.get(i).getPredicate().getName(), varPerm.subList(start, end)));
			}
			try {
					Rule r = Expressions.makeRule(Expressions.makePositiveConjunction(Expressions.makePositiveLiteral(head, varPerm.subList(0, head.getArity()))), 
					Expressions.makeConjunction(bodies));
				if (this.isValid(r) && !this.existSimilar(result, r)) result.add(r);
			} catch(Exception c) {
				// Do nothing.
			}
		}
		return result;
	}
	
	public boolean isSimilarVariables(List<Term> vars1, List<Term> vars2) {
		if (vars1.size() != vars2.size()) return false;
		Map<Term, Term> mapping = new HashMap<>();
		for (int i = 0; i < vars1.size(); i++) {
			if (!mapping.containsKey(vars1.get(i))) {
				mapping.put(vars1.get(i), vars2.get(i));
			} else {
				if (!vars2.get(i).equals(mapping.get(vars1.get(i)))) 
					return false;
			}
		}
		return true;
	}
	
	public boolean existSimilar(List<Rule> rules, Rule r) {
		for (Rule comp : rules) {
			List<Term> vars1 = this.extractLiterals(r);
			List<Term> vars2 = this.extractLiterals(comp);
			if (isSimilarVariables(vars1, vars2) && this.extractPredicates(r).equals(this.extractPredicates(comp))) 
				return true;
		}
		return false;
	}
	
	public List<Rule> enumerateLiteralGenerator(int maxBodySize) {
		List<Rule> result = new ArrayList<>();
		List<Predicate> considered = new ArrayList<>(this.inputRelation);
		considered.addAll(this.outputRelation);
		considered.addAll(this.inventedRelation);
		for (int k = 1; k <= maxBodySize; k++) {
			List<List<Predicate>> perms = this.permutation(considered, considered.size(), k);
			for (Predicate op : this.outputRelation) {
				for (List<Predicate> body : perms) {
					// get maximal number of variables
					int totalBodyArity = 0;
					for (Predicate b : body) {
						totalBodyArity += b.getArity();
					}
					int maxVar = Math.max(op.getArity(), totalBodyArity);
					// generate the variables
					List<Variable> vars = new ArrayList<>();
					for (int i = 0; i < maxVar; i++)
						vars.add(Expressions.makeUniversalVariable("x"+i));
					// generate permutation of variables
					List<List<Term>> varsPerm = this.permutationVar(vars, vars.size(), (op.getArity() + totalBodyArity));
					// enumerate all valid candidate rules
					for (List<Term> varPerm : varsPerm) {
						List<Literal> bodies = new ArrayList<>();
						int start = op.getArity();
						int end = start;
						for (int i = 0; i < body.size(); i++) {
							if (i != 0) start += body.get(i-1).getArity();
							end +=  body.get(i).getArity();
							bodies.add(Expressions.makePositiveLiteral(body.get(i).getName(), varPerm.subList(start, end)));
						}
						try {
								Rule r = Expressions.makeRule(Expressions.makePositiveConjunction(Expressions.makePositiveLiteral(op, varPerm.subList(0, op.getArity()))), 
								Expressions.makeConjunction(bodies));
							if (this.isValid(r) && !this.existSimilar(result, r)) result.add(r);
						} catch(Exception c) {
							// Do nothing.
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public List<Rule> simpleMetaGenerator() {
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
