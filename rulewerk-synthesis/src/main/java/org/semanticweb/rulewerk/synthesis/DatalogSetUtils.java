package org.semanticweb.rulewerk.synthesis;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.TermFactory;

public class DatalogSetUtils {
	static TermFactory tf = new TermFactory();
	
	static UniversalVariable u 		= tf.makeUniversalVariable("u");
	static UniversalVariable u_min	= tf.makeUniversalVariable("u-");
	static UniversalVariable v 		= tf.makeUniversalVariable("v");
	static UniversalVariable v_min 	= tf.makeUniversalVariable("v-");
	static UniversalVariable w		= tf.makeUniversalVariable("w");
	static UniversalVariable w_plus	= tf.makeUniversalVariable("w+");
	static UniversalVariable x		= tf.makeUniversalVariable("x");
	static UniversalVariable y		= tf.makeUniversalVariable("y");
	static ExistentialVariable z 	= tf.makeExistentialVariable("z");
	
	static AbstractConstant c = tf.makeAbstractConstant("c");

	static Predicate truth 	= Expressions.makePredicate("true", 1);
	static Predicate emp 	= Expressions.makePredicate("empty", 1);
	static Predicate set 	= Expressions.makePredicate("set", 1);
	static Predicate in 	= Expressions.makePredicate("in", 2);
	static Predicate getSU 	= Expressions.makePredicate("getSU", 2);
	static Predicate SU 	= Expressions.makePredicate("SU", 3);
	static Predicate getU 	= Expressions.makePredicate("getU", 2);
	static Predicate U 		= Expressions.makePredicate("U", 3);
	static Predicate ckSub	= Expressions.makePredicate("ckSub", 3);
	static Predicate sub	= Expressions.makePredicate("sub", 2);
	
	public static Set<Statement> getR_SU() {
		Set<Statement> r_su = new HashSet<Statement>();

		r_su.add(Expressions.makeFact(truth, c));
		r_su.add(Expressions.makeRule(
			Expressions.makePositiveConjunction(
				Expressions.makePositiveLiteral(emp, z),
				Expressions.makePositiveLiteral(set, z)),
			Expressions.makeConjunction(
				Expressions.makePositiveLiteral(truth, c))));
		r_su.add(Expressions.makeRule(
			Expressions.makePositiveConjunction(
				Expressions.makePositiveLiteral(SU, x, u, z),
				Expressions.makePositiveLiteral(SU, x, z, z),
				Expressions.makePositiveLiteral(set, z)), 
			Expressions.makeConjunction(
				Expressions.makePositiveLiteral(getSU, x, u))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(SU, y, v, v)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(SU, x, u, v),
					Expressions.makePositiveLiteral(SU, y, u, u))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(U, v, w, w)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(getU, v, w),
					Expressions.makePositiveLiteral(emp, v))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(getSU, x, w)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(getU, v, w),
					Expressions.makePositiveLiteral(SU, x, v_min, v))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(getU, v_min, w_plus)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(getU, v, w),
					Expressions.makePositiveLiteral(SU, x, v_min, v),
					Expressions.makePositiveLiteral(SU, x, w, w_plus))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(U, v, w, u)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(getU, v, w),
					Expressions.makePositiveLiteral(SU, x, v_min, v),
					Expressions.makePositiveLiteral(SU, x, w, w_plus),
					Expressions.makePositiveLiteral(U, v_min, w_plus, u))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(in, x, u)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(SU, x, u, u))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(ckSub, v, v, w)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(set, v),
					Expressions.makePositiveLiteral(set, w))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(ckSub, u_min, v, w)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(ckSub, u, v, w),
					Expressions.makePositiveLiteral(SU, x, u_min, u),
					Expressions.makePositiveLiteral(in, x, w))));
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(sub, v, w)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(ckSub, u, v, w),
					Expressions.makePositiveLiteral(emp, u))));
		
		return r_su;
	}
}
