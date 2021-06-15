package org.semanticweb.rulewerk.synthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
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
						}
						idx++;
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
