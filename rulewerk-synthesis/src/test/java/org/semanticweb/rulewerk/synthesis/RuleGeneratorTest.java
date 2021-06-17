package org.semanticweb.rulewerk.synthesis;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class RuleGeneratorTest {
	public static void main (String arg[]) {
		List<Rule> metaRuleSet = new ArrayList<>();
		List<Predicate> inputRelation = new ArrayList<>();
		List<Predicate> outputRelation = new ArrayList<>();
		List<Predicate> inventedRelation = new ArrayList<>();
		
		Variable x0 = Expressions.makeUniversalVariable("x0");
		Variable x1 = Expressions.makeUniversalVariable("x1");
		Variable x2 = Expressions.makeUniversalVariable("x2");
		
		metaRuleSet.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1)));
		metaRuleSet.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0), Expressions.makePositiveLiteral("P1", x0)));
		metaRuleSet.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x2), 
				Expressions.makePositiveLiteral("P1", x0, x1), Expressions.makePositiveLiteral("P2", x1, x2)));
		metaRuleSet.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x2), 
				Expressions.makePositiveLiteral("P1", x0, x1), Expressions.makePositiveLiteral("P2", x2)));
		
		// --------------------------------- Comment the relevant one --------------------------------- //
		// Abduce
		inputRelation.add(Expressions.makePredicate("father", 2));
		inputRelation.add(Expressions.makePredicate("mother", 2));
		inventedRelation.add(Expressions.makePredicate("parent", 2));
		outputRelation.add(Expressions.makePredicate("grandparent", 2));
		
		// Path
		inputRelation.add(Expressions.makePredicate("edge", 2));
		outputRelation.add(Expressions.makePredicate("path", 2));
		
		// Start rule generator
		RuleGenerator rg = new RuleGenerator(metaRuleSet, inputRelation, outputRelation, inventedRelation);
		int i = 1;
		for (Rule r : rg.simpleGenerator()) {
			System.out.println(i+" "+r);
			i++;
		}
	}
}
