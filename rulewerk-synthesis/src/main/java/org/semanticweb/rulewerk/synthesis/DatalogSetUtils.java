package org.semanticweb.rulewerk.synthesis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetTerm;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
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
	static Predicate eq	= Expressions.makePredicate("eq", 2);
	
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
		r_su.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(
					Expressions.makePositiveLiteral(eq, v, w)),
				Expressions.makeConjunction(
					Expressions.makePositiveLiteral(sub, v, w),
					Expressions.makePositiveLiteral(sub, w, v))));
		
		return r_su;
	}
	
	public static int countSetVarOccurenceBody(Rule r, SetVariable v) {
		int count = 0;
		List<Literal> ls = new ArrayList<Literal>(r.getBody().getLiterals());
		for (Literal pl : ls) {
			if (!pl.getPredicate().equals(eq)) {
				for (Term t : pl.getArguments()) {
					if (t.equals(v)) count++;
					if (t instanceof SetUnion) {
						for (Term tu : ((SetUnion) t).getSetVariables()) {
							if (tu.equals(v)) count++;
						}
					}
				}
			}
		}
		return count;
	}
	
	public static Rule normalize(Rule r) {
		// TODO: Add code to normalize Datalog(S) rule r.
		// Replace every set term of the form S1 U S2 in a non-special predicate in the body with a fresh set variable S
		// and add body atoms S = S1 U S2.
		if (r.getSetTerms().count() > 0) {
			Set<SetUnion> unions = r.getBody().getSetUnions().collect(Collectors.toSet());
			List<PositiveLiteral> head = new ArrayList<PositiveLiteral>(r.getHead().getLiterals());
			List<Literal> body = new ArrayList<Literal>(r.getBody().getLiterals());
			Set<Literal> newLiterals = new HashSet<Literal>();
			for (SetUnion su : unions) {
				SetVariable v = Expressions.makeSetVariable("V_"+unions.hashCode());
				newLiterals.add(Expressions.makePositiveLiteral(eq, v, su));
				List<Literal> ls = new ArrayList<Literal>(r.getBody().getLiterals());
				for (Literal h : ls) {
					if (h.getTerms().collect(Collectors.toSet()).contains(su)) {
						List<Term> terms = new ArrayList<Term>(h.getArguments());
						terms.set(terms.indexOf(su), v);
						body.set(body.indexOf(h),
								Expressions.makePositiveLiteral(h.getPredicate(), terms));
					}
				}
			}
			body.addAll(newLiterals);
			
			// As long as a set variable S occurs more than once in a non-special predicate in the body of a rule
			// Replace one of these occurrences by a fresh variable S′ and add S=S′ to the body. 
			Rule temp_r = Expressions.makeRule(Expressions.makePositiveConjunction(head), Expressions.makeConjunction(body));
			Set<SetVariable> vars = new HashSet<SetVariable>(temp_r.getBody().getSetVariables().collect(Collectors.toList()));
			for (SetVariable v : vars) {
				while (countSetVarOccurenceBody(temp_r, v) > 1) {
					List<Literal> cbd = new ArrayList<Literal>(temp_r.getBody().getLiterals());
					List<Literal> ls = new ArrayList<Literal>(temp_r.getBody().getLiterals());
					boolean changed = false; int idx = 0;
					while (!changed && idx < ls.size()) {
						List<Term> ts = new ArrayList<Term>(ls.get(idx).getTerms().collect(Collectors.toList()));
						if (ts.contains(v)) {
							changed = true;
							SetVariable newv = Expressions.makeSetVariable("V_"+(v.hashCode()+idx+ts.indexOf(v)));
							ts.set(ts.indexOf(v), newv);
							PositiveLiteral newL = Expressions.makePositiveLiteral(ls.get(idx).getPredicate(), ts);
							cbd.set(idx, newL);
							cbd.add(Expressions.makePositiveLiteral(eq, newv, v));
						}
						idx++;
					}
					temp_r = Expressions.makeRule(Expressions.makePositiveConjunction(head), Expressions.makeConjunction(cbd));
				}
			}
			return temp_r;
		} else
			return r;
	}
	
	public static List<SetTerm> getOrder(Rule r) {
		Set<SetTerm> terms = r.getSetTerms().collect(Collectors.toSet());
		Set<SetTerm> newTerms = new HashSet<SetTerm>();
		for (SetTerm t : terms) {
			if (t instanceof SetUnion) {
				newTerms.addAll(((SetUnion) t).getSubTerms());
			}
		}
		terms.addAll(newTerms);
		List<SetTerm> order = new ArrayList<SetTerm>();
		for (SetTerm t : terms) {
			if (!t.isSetVariable()) {
				boolean insert = false; int idx = 0;
				while (!insert && idx < order.size()) {
					if (order.get(idx) instanceof SetUnion) {
						if (((SetUnion) order.get(idx)).isSubTerm(t)) {
							insert = true;
							order.add(idx, t);
						}
					}
					idx++;
				}
				if (!insert) order.add(t);
			}
		}
		return order;
	}
}
