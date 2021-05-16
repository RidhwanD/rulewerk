package org.semanticweb.rulewerk.synthesis;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class DatalogSynthesisTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Fact> it = new ArrayList<Fact>();
		List<Rule> rs = new ArrayList<Rule>();
		
		final UniversalVariable x = Expressions.makeUniversalVariable("X");
		final UniversalVariable y = Expressions.makeUniversalVariable("Y");
		final UniversalVariable z = Expressions.makeUniversalVariable("Z");
		
		Rule r1 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, z), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), z, y));

		Rule r2 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, z), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), y, z));

		Rule r3 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), x, y));

		Rule r4 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), x, z), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), z, y));
		
		Rule r5 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), x, z), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), y, z));
		
		rs.add(r1); rs.add(r2); rs.add(r3); rs.add(r4); rs.add(r5);

		DatalogSynthesis ds = new DatalogSynthesis(it, it, it, rs);
		
//		ds.whyNotProv(, rs);
		
//		List<Rule> newRs = ds.getExistNeg();
//		for (Rule r : newRs) {
//			System.out.println(r);
//		}
	}

}
