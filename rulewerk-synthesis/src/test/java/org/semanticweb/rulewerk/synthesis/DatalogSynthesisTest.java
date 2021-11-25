package org.semanticweb.rulewerk.synthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

import com.microsoft.z3.Context;

public class DatalogSynthesisTest {
	private final Variable x0 = Expressions.makeUniversalVariable("x0");
	private final Variable x1 = Expressions.makeUniversalVariable("x1");
	private final Variable x2 = Expressions.makeUniversalVariable("x2");
	private final Variable x3 = Expressions.makeUniversalVariable("x3");
	
	private final Constant c1 = Expressions.makeAbstractConstant("c1");
	private final Constant c2 = Expressions.makeAbstractConstant("c2");
	private final Constant c3 = Expressions.makeAbstractConstant("c3");
	
	private final Predicate inp = Expressions.makePredicate("edge", 2);
	private final Predicate oup = Expressions.makePredicate("path", 2);
	private final Predicate inv = Expressions.makePredicate("inv", 2);
	
	HashMap<String, String> cfg = new HashMap<String, String>()
	{{
		put("model", "true");
	}};
	Context ctx = new Context();
	
	DatalogSynthesisImpl ds = new DatalogSynthesisImpl(
			Arrays.asList(Expressions.makeFact(inp, c1, c2), Expressions.makeFact(inp, c2, c3)), 
			Arrays.asList(oup),
			Arrays.asList(Expressions.makeFact(oup, c1, c2), Expressions.makeFact(oup, c2, c3), Expressions.makeFact(oup, c1, c3)),
			Arrays.asList(),
			Arrays.asList(
					Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
					Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
					Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
					Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2)),
					Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
					Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x2, x1), Expressions.makePositiveLiteral(oup, x0, x1))
					),
			ctx);

	@Test
	public void testIsObvRecursive() {
		assertFalse(ds.isObvRecursive(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1))));
		assertFalse(ds.isObvRecursive(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2))));
		assertTrue(ds.isObvRecursive(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(oup, x0, x1))));
		assertTrue(ds.isObvRecursive(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2))));
	}
	
	@Test
	public void testIsBase() {
		assertTrue(ds.isBase(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1))));
		assertTrue(ds.isBase(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inp, x0, x2))));
		assertFalse(ds.isBase(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2))));
		assertFalse(ds.isBase(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(oup, x0, x1))));
		assertFalse(ds.isBase(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2))));
	}
	
	@Test
	public void testGetPredicate() {
		Set<Predicate> idb = new HashSet<>(ds.getIDBPredicate());
		Set<Predicate> edb = ds.getEDBPredicate();
		assertEquals(idb, new HashSet<Predicate>(Arrays.asList(oup, inv)));
		assertEquals(edb, new HashSet<Predicate>(Arrays.asList(inp)));
	}
	
	@Test
	public void testBuildDepMatrix() {
		boolean[][] depMtx = {{true,true},{true,true}};
		assertTrue(Arrays.deepEquals(depMtx, ds.buildDepMatrix(
				Arrays.asList(
						Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
						Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
						Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
						Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2)),
						Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
						Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x2, x1), Expressions.makePositiveLiteral(oup, x0, x1))
				))));
	}
	
	@Test
	public void testTransClosure() {
		boolean[][] depMtx1 = {{false,true},{true,false}};
		boolean[][] depMtx2 = {{false,true},{false,false}};
		boolean[][] depMtx3 = {{true,true},{true,true}};
		assertTrue(Arrays.deepEquals(depMtx3, ds.transClosure(depMtx1)));
		assertTrue(Arrays.deepEquals(depMtx2, ds.transClosure(depMtx2)));
	}
	
	@Test
	public void testGetNonRecSubset() {
		List<Rule> r = Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2)),
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x2, x1), Expressions.makePositiveLiteral(oup, x0, x1))
		);
		List<Rule> exp = Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2))
		);
		assertEquals(exp, ds.getNonRecSubset(r));
	}
	
	@Test
	public void testIsCyclic() {
		boolean[][] depMtx1 = {{false,false},{true,false}};
		boolean[][] depMtx2 = {{false,true},{false,false}};
		boolean[][] depMtx3 = {{true,true},{true,true}};
		assertFalse(ds.isCyclic(depMtx1));
		assertFalse(ds.isCyclic(depMtx2));
		assertTrue(ds.isCyclic(depMtx3));
	}
	
	@Test
	public void testGetMaximalRuleSets() {
		Set<Rule> r1 = new HashSet<>(Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1))
		));
		Set<Rule> r2 = new HashSet<>(Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1))
		));
		Set<Rule> r3 = new HashSet<>(Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1))
		));
		Set<Rule> r4 = new HashSet<>(Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inv, x0, x1))
		));
		List<Set<Rule>> rs1 = Arrays.asList(r1, r2, r3, r4);
		List<Set<Rule>> rs2 = Arrays.asList(r1, r2);
		List<Set<Rule>> exp = Arrays.asList(r3, r4);
		
		assertEquals(exp, ds.getMaximalRuleSets(rs1));
		assertEquals(rs2, ds.getMaximalRuleSets(rs2));
	}
	
	@Test
	public void testGetAllNonRecSubsets() {
		List<Rule> r = Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2)),
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x0, x2)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x2, x1), Expressions.makePositiveLiteral(oup, x0, x1))
		);
		List<Rule> exp = Arrays.asList(
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x0, x1)),
				Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x0, x2))
		);
		assertEquals(exp, ds.getNonRecSubset(r));
	}
	
	// ============================================ CO-PROV UTILITIES ============================================== //
	
	@Test
	public void testTransformRule() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x2, x1), Expressions.makePositiveLiteral(oup, x0, x1));
		Rule exp = Expressions.makeRule(Expressions.makePositiveLiteral(oup.getName()+"_en", x0, x2, Expressions.makeUniversalVariable("r1")), 
				Expressions.makePositiveLiteral(inp.getName()+"_en", x2, x1, Expressions.makeUniversalVariable("r1")), 
				Expressions.makePositiveLiteral(oup.getName()+"_en", x0, x1, Expressions.makeUniversalVariable("r1")),
				Expressions.makeNegativeLiteral("Equal", Expressions.makeUniversalVariable("r1"), Expressions.makeAbstractConstant("cr_5")));
		assertEquals(ds.transformRule(r), exp);
	}
	
	@Test
	public void testTransformInput() {
		List<Rule> exp = Arrays.asList(Expressions.makeRule(Expressions.makePositiveLiteral(inp.getName()+"_en", x0, x1, Expressions.makeUniversalVariable("r")),
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral("isRulePred", Expressions.makeUniversalVariable("r"))));
		assertEquals(ds.transformInput(), exp);
	}
	
	@Test
	public void testGetExistNeg() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), Expressions.makePositiveLiteral(inp, x2, x1), Expressions.makePositiveLiteral(oup, x0, x1));
		Rule rs = Expressions.makeRule(Expressions.makePositiveLiteral(oup.getName()+"_en", x0, x2, Expressions.makeUniversalVariable("r1")), 
				Expressions.makePositiveLiteral(inp.getName()+"_en", x2, x1, Expressions.makeUniversalVariable("r1")), 
				Expressions.makePositiveLiteral(oup.getName()+"_en", x0, x1, Expressions.makeUniversalVariable("r1")),
				Expressions.makeNegativeLiteral("Equal", Expressions.makeUniversalVariable("r1"), Expressions.makeAbstractConstant("cr_5")));
		List<Statement> exp = new ArrayList<>(Arrays.asList(Expressions.makeRule(Expressions.makePositiveLiteral(inp.getName()+"_en", x0, x1, Expressions.makeUniversalVariable("r")),
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral("isRulePred", Expressions.makeUniversalVariable("r")))));
		List<Rule> in = Arrays.asList(r);
		exp.add(rs);
		exp.add(Expressions.makeFact("isRulePred", Expressions.makeAbstractConstant("cr_5")));
		exp.add(Expressions.makeFact("Equal", Expressions.makeAbstractConstant("cr_5"), Expressions.makeAbstractConstant("cr_5")));
		assertEquals(new HashSet<Statement>(ds.getExistNeg(in)), new HashSet<Statement>(exp));
	}
	
	// ============================================ DATALOG(S) UTILITIES ============================================== //
	
	@Test
	public void testRulesFromExpPred() {
		List<Statement> exp = new ArrayList<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(oup.getName(), x0, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("WhyProv_"+oup.getName(), x0, x1, x2, x3), 
				Expressions.makePositiveLiteral(oup.getName(), x0, x1, x2), Expressions.makePositiveLiteral("in", x3, x2)));
		assertEquals(new HashSet<Statement>(ds.rulesFromExpPred()), new HashSet<Statement>(exp));
	}
}
