package org.semanticweb.rulewerk.synthesis;

import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetConstruct;
import org.semanticweb.rulewerk.core.model.api.SetPredicate;
import org.semanticweb.rulewerk.core.model.api.SetPredicateType;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class DatalogSetUtilsTest {
	public static void main(String[] arg) {
		Set<Statement> r_su = DatalogSetUtils.getR_SU();
		
		for (Statement s : r_su) {
			System.out.println(s);
		}
		
		SetVariable u = Expressions.makeSetVariable("U");
		SetVariable v = Expressions.makeSetVariable("V");
		SetVariable vs = Expressions.makeSetVariable("V'");
		SetVariable w = Expressions.makeSetVariable("W");
		SetVariable ws = Expressions.makeSetVariable("W'");
		
		UniversalVariable x = Expressions.makeUniversalVariable("x");
		UniversalVariable y = Expressions.makeUniversalVariable("y");
		
		SetConstruct empSet = Expressions.makeEmptySet();
		SetConstruct set2 = Expressions.makeSetConstruct(x);
		SetConstruct set3 = Expressions.makeSetConstruct(y);
		SetUnion un1 = Expressions.makeSetUnion(set2, ws);
		SetUnion un2 = Expressions.makeSetUnion(empSet, set3);
		SetUnion un3 = Expressions.makeSetUnion(un1, un2);
		SetUnion un4 = Expressions.makeSetUnion(u, un3);
		
		Predicate p = Expressions.makePredicate("full", 1);
		Predicate q = Expressions.makePredicate("n", 2);
		Predicate r = Expressions.makePredicate("parts", 3);
		Predicate s = Expressions.makePredicate("succ", 2);
		SetPredicate in = Expressions.makeSetPredicate("elementOf", 2, SetPredicateType.IS_ELEMENT_OF);
		SetPredicate subset = Expressions.makeSetPredicate("subsetOf", 2, SetPredicateType.IS_SUBSET_OF);
		
		PositiveLiteral t1 = Expressions.makePositiveLiteral(in, x, u);
		PositiveLiteral t2 = Expressions.makePositiveLiteral(subset, u, v);

		PositiveLiteral l1 = Expressions.makePositiveLiteral(q, v, un1);
		PositiveLiteral l2 = Expressions.makePositiveLiteral(r, un1, x, ws);
		PositiveLiteral l3 = Expressions.makePositiveLiteral(q, u, v);
		PositiveLiteral l4 = Expressions.makePositiveLiteral(r, v, x, vs);
		PositiveLiteral l5 = Expressions.makePositiveLiteral(q, vs, ws);
		PositiveLiteral l6 = Expressions.makePositiveLiteral(q, u, ws);
		PositiveLiteral l10 = Expressions.makePositiveLiteral(p, vs);
		PositiveLiteral l11 = Expressions.makePositiveLiteral(s, x, y);
		PositiveLiteral l12 = Expressions.makePositiveLiteral(q, v, set3);
		PositiveLiteral l13 = Expressions.makePositiveLiteral(r, set3, y, empSet);
		PositiveLiteral l14 = Expressions.makePositiveLiteral(p, v);
		
		Rule r1 = Expressions.makeRule(Expressions.makePositiveConjunction(l1, l2), Expressions.makeConjunction(l3, l4, l5));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveConjunction(l12, l13, l14), Expressions.makeConjunction(l3, l4, l10, l11));
		Rule r3 = Expressions.makeRule(Expressions.makePositiveConjunction(l1), Expressions.makeConjunction(l3, l6));
		
		SetVariable sv = Expressions.makeSetVariable("S");
		SetVariable tv = Expressions.makeSetVariable("T");
		Constant a = Expressions.makeAbstractConstant("a");
		SetConstruct sa = Expressions.makeSetConstruct(a);
		SetUnion aus = Expressions.makeSetUnion(sa, sv);
		SetUnion sut = Expressions.makeSetUnion(sv, tv);
		
		Predicate pp = Expressions.makePredicate("p", 4);
		Predicate qq = Expressions.makePredicate("q", 1);
		PositiveLiteral body = Expressions.makePositiveLiteral(pp, a, sv, tv, aus);
		PositiveLiteral head = Expressions.makePositiveLiteral(qq, sut);
		Rule r4 = Expressions.makeRule(head, body);
		
		Rule r5 = Expressions.makeRule(Expressions.makePositiveLiteral(qq, u), Expressions.makePositiveLiteral(qq, un4));
		
		System.out.println(un4.isSubTerm(w));
		System.out.println(un4.getSubTerms());
		
		System.out.println(" ===== Test ===== ");
		
		System.out.println(r1);
		Rule nr1 = DatalogSetUtils.normalize(r1);
		System.out.println(nr1);
		System.out.println(DatalogSetUtils.getOrder(nr1));

		System.out.println(r2);
		Rule nr2 = DatalogSetUtils.normalize(r2);
		System.out.println(nr2);
		System.out.println(DatalogSetUtils.getOrder(nr2));
		
		System.out.println(r3);
		Rule nr3 = DatalogSetUtils.normalize(r3);
		System.out.println(nr3);
		System.out.println(DatalogSetUtils.getOrder(nr3));
		
		System.out.println(r4);
		Rule nr4 = DatalogSetUtils.normalize(r4);
		System.out.println(nr4);
		System.out.println(DatalogSetUtils.getOrder(nr4));

		System.out.println(r5);
		Rule nr5 = DatalogSetUtils.normalize(r5);
		System.out.println(nr5);
		System.out.println(DatalogSetUtils.getOrder(nr5));
	}
}
