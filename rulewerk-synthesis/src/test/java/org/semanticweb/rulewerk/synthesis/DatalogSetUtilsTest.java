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
		ExistentialVariable z = Expressions.makeExistentialVariable("z");
		
		Constant const1 = Expressions.makeAbstractConstant("1");
		
		SetConstruct empSet = Expressions.makeEmptySet();
		SetConstruct set2 = Expressions.makeSetConstruct(x);
		SetConstruct set3 = Expressions.makeSetConstruct(y);
		SetUnion un1 = Expressions.makeSetUnion(set2, ws);
		
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
		
		System.out.println(" ===== Test Normalization ===== ");
		
		System.out.println(r1);
		System.out.println(DatalogSetUtils.normalize(r1));
		
		System.out.println(r3);
		System.out.println(DatalogSetUtils.normalize(r3));
		
	}
}
