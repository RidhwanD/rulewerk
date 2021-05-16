package org.semanticweb.rulewerk.synthesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;

public class DatalogSynthesisTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Fact> it = new ArrayList<Fact>();
		List<Fact> otp = new ArrayList<Fact>();
		List<Fact> otm = new ArrayList<Fact>();
		List<Rule> rs = new ArrayList<Rule>();
		
		final UniversalVariable x = Expressions.makeUniversalVariable("X");
		final UniversalVariable y = Expressions.makeUniversalVariable("Y");
		final UniversalVariable z = Expressions.makeUniversalVariable("Z");
		final Constant ch1 = Expressions.makeAbstractConstant("Charles 1");
		final Constant jm1 = Expressions.makeAbstractConstant("James 1");
		final Constant ezb = Expressions.makeAbstractConstant("Elizabeth");
		final Constant ch2 = Expressions.makeAbstractConstant("Charles 2");
		final Constant ctr = Expressions.makeAbstractConstant("Catherine");
		
		final Fact i1 = Expressions.makeFact(Expressions.makePredicate("parent", 2), ch1, jm1);
		final Fact i2 = Expressions.makeFact(Expressions.makePredicate("parent", 2), ezb, jm1);
		final Fact i3 = Expressions.makeFact(Expressions.makePredicate("parent", 2), ch2, ch1);
		final Fact i4 = Expressions.makeFact(Expressions.makePredicate("parent", 2), ctr, ch1);
		it.add(i1); it.add(i2); it.add(i3); it.add(i4);

		final Fact op1 = Expressions.makeFact(Expressions.makePredicate("Ancestor", 2), ch1, jm1);
		final Fact op2 = Expressions.makeFact(Expressions.makePredicate("Ancestor", 2), ezb, jm1);
		final Fact op3 = Expressions.makeFact(Expressions.makePredicate("Ancestor", 2), ch2, ch1);
		final Fact op4 = Expressions.makeFact(Expressions.makePredicate("Ancestor", 2), ch2, jm1);
		otp.add(op1); otp.add(op2); otp.add(op3); otp.add(op4);

		final Fact om1 = Expressions.makeFact(Expressions.makePredicate("Ancestor", 2), ch1, ezb);
		final Fact om2 = Expressions.makeFact(Expressions.makePredicate("Ancestor", 2), ch2, ctr);
		otm.add(om1); otm.add(om2);
		
		final Rule r1 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, z), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), z, y));

		final Rule r2 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, z), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), y, z));

		final Rule r3 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), x, y));

		final Rule r4 = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Ancestor", 2), x, y), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), x, z), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("parent", 2), z, y));
		
		rs.add(r1); rs.add(r2); rs.add(r3); rs.add(r4);
		
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);

		DatalogSynthesis ds = new DatalogSynthesis(it, otp, otm, rs, ctx);
		
//		ds.whyNotProv(, rs);
		
//		List<Statement> newRs = ds.getExistNeg(rs);
//		for (Statement r : newRs) {
//			System.out.println(r);
//		}
		
//		BoolExpr v_a = ctx.mkBoolConst("a");
//		BoolExpr v_b = ctx.mkBoolConst("b");
//		BoolExpr v_c = ctx.mkBoolConst("c");
//		BoolExpr v_aORv_b = ctx.mkOr(v_a, v_b);
//		BoolExpr v_aORv_bANDv_c = ctx.mkAnd(v_aORv_b, v_c);
//		
//		Model m = ds.consultSATSolver(v_aORv_bANDv_c);
//		
//		System.out.println(m.getConstInterp(v_c).isFalse());
		
//		List<Rule> wp = new ArrayList<Rule>();
//		wp.add(r1);
//		wp.add(r3);
//		wp.add(r4);
//		ds.whyProvExpr(wp);
//		
//		
//		List<Rule> wm = new ArrayList<Rule>();
//		wm.add(r2);
//		
//		System.out.println(ds.whyNotCoProvExpr(wm,
//				ds.coprovInv(op1, wp)));
		
		ds.synthesis();
	}

}
