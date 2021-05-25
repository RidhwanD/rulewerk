package org.semanticweb.rulewerk.core.model.implementation;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetConstruct;
import org.semanticweb.rulewerk.core.model.api.SetPredicate;
import org.semanticweb.rulewerk.core.model.api.SetPredicateType;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;

public class SetExpressionTest {
	public static void main(String[] arg) {
		SetVariable u = Expressions.makeSetVariable("U");
		SetVariable v = Expressions.makeSetVariable("V");
		SetVariable vs = Expressions.makeSetVariable("V'");
		SetVariable w = Expressions.makeSetVariable("W");
		SetVariable ws = Expressions.makeSetVariable("W'");
		
		UniversalVariable x = Expressions.makeUniversalVariable("x");
		UniversalVariable y = Expressions.makeUniversalVariable("y");
		ExistentialVariable z = Expressions.makeExistentialVariable("y");
		
		Constant const1 = Expressions.makeAbstractConstant("1");
		
		SetConstruct empSet = Expressions.makeEmptySet();
		SetConstruct set1 = Expressions.makeSetConstruct(const1);
		SetConstruct set2 = Expressions.makeSetConstruct(x);
		SetConstruct set3 = Expressions.makeSetConstruct(y);
		SetUnion un1 = Expressions.makeSetUnion(set2, ws);
		SetUnion un2 = Expressions.makeSetUnion(set2, w);
		
		Predicate p = Expressions.makePredicate("full", 1);
		Predicate q = Expressions.makePredicate("n", 2);
		Predicate r = Expressions.makePredicate("parts", 3);
		Predicate s = Expressions.makePredicate("succ", 2);
		SetPredicate in = Expressions.makeSetPredicate("elementOf", 2, SetPredicateType.IS_ELEMENT_OF);
//		SetPredicate in_error = SetExpressions.makeSetPredicate("elementOf", 3, SetPredicateType.IS_ELEMENT_OF);
		SetPredicate subset = Expressions.makeSetPredicate("subsetOf", 2, SetPredicateType.IS_SUBSET_OF);
//		SetPredicate subset_error = SetExpressions.makeSetPredicate("subsetOf", 3, SetPredicateType.IS_ELEMENT_OF);
		
		PositiveLiteral t1 = Expressions.makePositiveLiteral(in, x, u);
//		Literal t1_error = Expressions.makePositiveLiteral(in, x, y);
		PositiveLiteral t2 = Expressions.makePositiveLiteral(subset, u, v);
//		Literal t2_error = Expressions.makePositiveLiteral(subset, v, y);
		
		Fact f1 = Expressions.makeFact(p, const1);
//		Fact f1_error_2 = Expressions.makeFact(p, u);
//		Fact f1_error_1 = Expressions.makeFact(p, x);
		Fact f2 = Expressions.makeFact(p, empSet);
//		Fact f2_error_1 = Expressions.makeFact(in, const1, set1);
//		Fact f2_error_2 = Expressions.makeFact(subset, empSet, set1);
		Fact f3 = Expressions.makeFact(q, empSet, set1);
		Fact f4 = Expressions.makeFact(r, set1, const1, empSet);

		PositiveLiteral l1 = Expressions.makePositiveLiteral(q, v, un1);
		PositiveLiteral l2 = Expressions.makePositiveLiteral(r, un1, x, ws);
		PositiveLiteral l3 = Expressions.makePositiveLiteral(q, u, v);
		PositiveLiteral l4 = Expressions.makePositiveLiteral(r, v, x, vs);
		PositiveLiteral l5 = Expressions.makePositiveLiteral(q, vs, ws);
		PositiveLiteral l6 = Expressions.makePositiveLiteral(q, vs, w);
		PositiveLiteral l7 = Expressions.makePositiveLiteral(q, v, w);
		PositiveLiteral l8 = Expressions.makePositiveLiteral(p, z);
		PositiveLiteral l9 = Expressions.makePositiveLiteral(in, x, un2);
		Rule r1 = Expressions.makeRule(Expressions.makePositiveConjunction(l1, l2), Expressions.makeConjunction(l3, l4, l5));
//		Rule r1_error_1 = Expressions.makeRule(Expressions.makePositiveConjunction(l1, l2), Expressions.makeConjunction(l3, l4, l6));
//		Rule r1_error_2 = Expressions.makeRule(Expressions.makePositiveConjunction(l7, l2), Expressions.makeConjunction(l3, l4, l5));
//		Rule r1_error_3 = Expressions.makeRule(Expressions.makePositiveConjunction(l1, l2, l8), Expressions.makeConjunction(l3, l4, l5));
//		Rule r1_error_4 = Expressions.makeRule(Expressions.makePositiveConjunction(l1, l2, t1), Expressions.makeConjunction(l3, l4, l5));
//		Rule r1_error_5 = Expressions.makeRule(Expressions.makePositiveConjunction(l1, l2), Expressions.makeConjunction(l3, l4, l5, l9));
		
		PositiveLiteral l10 = Expressions.makePositiveLiteral(p, vs);
		PositiveLiteral l11 = Expressions.makePositiveLiteral(s, x, y);
		PositiveLiteral l12 = Expressions.makePositiveLiteral(q, v, set3);
		PositiveLiteral l13 = Expressions.makePositiveLiteral(r, set3, y, empSet);
		PositiveLiteral l14 = Expressions.makePositiveLiteral(p, v);
		Rule r2 = Expressions.makeRule(Expressions.makePositiveConjunction(l12, l13, l14), Expressions.makeConjunction(l3, l4, l10, l11));
		
		System.out.println(empSet);
		System.out.println(set1);
		System.out.println(set2);
		System.out.println(set3);
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(f1);
		System.out.println(f2);
		System.out.println(f3);
		System.out.println(f4);
		System.out.println(r1);
		System.out.println(r2);
	}
}
