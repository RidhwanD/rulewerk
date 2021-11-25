package org.semanticweb.rulewerk.synthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class RuleGeneratorTest {
	private final Variable x0 = Expressions.makeUniversalVariable("x0");
	private final Variable x1 = Expressions.makeUniversalVariable("x1");
	private final Variable x2 = Expressions.makeUniversalVariable("x2");
	private final Variable x3 = Expressions.makeUniversalVariable("x3");
	
	private final Constant c1 = Expressions.makeAbstractConstant("c1");
	private final Constant c2 = Expressions.makeAbstractConstant("c2");
	
	private final Predicate inp = Expressions.makePredicate("edge", 2);
	private final Predicate inv = Expressions.makePredicate("inv", 2);
	private final Predicate oup = Expressions.makePredicate("path", 2);

	private final Rule meta = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x2), 
				Expressions.makePositiveLiteral("P1", x0, x1), Expressions.makePositiveLiteral("P2", x1, x2));
	
	private final RuleGenerator rg = new RuleGenerator(
			new ArrayList<Rule>(Arrays.asList(meta)), 
			new ArrayList<Predicate>(Arrays.asList(inp)),
			new ArrayList<Predicate>(Arrays.asList(oup)),
			new ArrayList<Predicate>(Arrays.asList(inv))
	);
	
	@Test
	public void testMaxArity() {
		assertEquals(2, rg.getMaximalArity());
	}
	
	@Test
	public void testExtractTerms() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(oup, c1, x1), Expressions.makePositiveLiteral(inp, c1, c2), Expressions.makePositiveLiteral(oup, c2, x1));
		List<Term> res = rg.extractTerms(r);
		List<Term> exp = new ArrayList<>(Arrays.asList(c1,x1,c1,c2,c2,x1));
		assertEquals(res, exp);
	}

	@Test
	public void testExtractPredicates() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(oup, c1, x1), Expressions.makePositiveLiteral(inp, c1, c2), Expressions.makePositiveLiteral(oup, c2, x1));
		List<Predicate> res = rg.extractPredicates(r);
		List<Predicate> exp = new ArrayList<>(Arrays.asList(oup,inp,oup));
		assertEquals(res, exp);
	}
	
	@Test
	public void testPredPerm() {
		List<List<Predicate>> res = rg.permutation(Arrays.asList(inp,oup,inv), 3, 2);
		List<List<Predicate>> exp = new ArrayList<>();
		exp.add(Arrays.asList(inp,inp));
		exp.add(Arrays.asList(inp,oup));
		exp.add(Arrays.asList(inp,inv));
		exp.add(Arrays.asList(oup,inp));
		exp.add(Arrays.asList(oup,oup));
		exp.add(Arrays.asList(oup,inv));
		exp.add(Arrays.asList(inv,inp));
		exp.add(Arrays.asList(inv,oup));
		exp.add(Arrays.asList(inv,inv));
		assertEquals(new HashSet<List<Predicate>>(res), new HashSet<List<Predicate>>(exp));
	}
	
	@Test
	public void testVarPerm() {
		List<List<Term>> res = rg.permutationVar(Arrays.asList(x0,x1,x2), 3, 2);
		List<List<Term>> exp = new ArrayList<>();
		exp.add(Arrays.asList(x0,x0));
		exp.add(Arrays.asList(x0,x1));
		exp.add(Arrays.asList(x0,x2));
		exp.add(Arrays.asList(x1,x0));
		exp.add(Arrays.asList(x1,x1));
		exp.add(Arrays.asList(x1,x2));
		exp.add(Arrays.asList(x2,x0));
		exp.add(Arrays.asList(x2,x1));
		exp.add(Arrays.asList(x2,x2));
		assertEquals(new HashSet<List<Term>>(res), new HashSet<List<Term>>(exp));
	}
	
	@Test
	public void testVarClauseRepeat() {
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x0, x1));
		assertTrue(rg.varClauseRepeat(r2));
		assertFalse(rg.varClauseRepeat(r1));
	}
	
	@Test
	public void testVarAppearsOnce() {
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x0, x1));
		assertTrue(rg.varAppearsOnce(r2));
		assertFalse(rg.varAppearsOnce(r1));
	}
	
	@Test
	public void testReoccurClause() {
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r3 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P0", x0, x1));
		assertTrue(rg.reoccurClause(r2));
		assertTrue(rg.reoccurClause(r3));
		assertFalse(rg.reoccurClause(r1));
	}
	
	@Test
	public void testIsArityMatch() {
		Literal l1 = Expressions.makePositiveLiteral("P0", x0, x1);
		Literal l2 = Expressions.makePositiveLiteral("P0", x0, x1, x3);
		assertTrue(rg.isArityMatch(l1));
		assertFalse(rg.isArityMatch(l2));
	}
	
	@Test
	public void testIsInstantiable() {
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x0, x1, x3));
		assertTrue(rg.isInstantiable(r1));
		assertFalse(rg.isInstantiable(r2));
	}
	
	@Test
	public void testIsValid() {
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x0, x1, x3));
		Rule r3 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r4 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P0", x0, x1));
		Rule r5 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r6 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x0, x1));
		assertTrue(rg.isValid(r1));
		assertFalse(rg.isValid(r2));
		assertFalse(rg.isValid(r3));
		assertFalse(rg.isValid(r4));
		assertFalse(rg.isValid(r5));
		assertFalse(rg.isValid(r6));
	}
	
	@Test
	public void testRemoveArgument() {
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1, x2), Expressions.makePositiveLiteral("P1", x0, x2, x1));
		
		Rule re1 = rg.removeArgument(r1, x0);
		assertEquals(r1,re1);

		Rule re2 = rg.removeArgument(r1, x2);
		assertEquals(r1,re2);

		Rule re3 = rg.removeArgument(r2, x2);
		assertEquals(r1,re3);
	}
	
	@Test
	public void testExpandBodyArgs() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0), Expressions.makePositiveLiteral("P1", x0, x1));
		List<Rule> res = rg.expandBodyArgs(r);
		List<Rule> exp = new ArrayList<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x1, x0), Expressions.makePositiveLiteral("P1", x0, x1)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1)));
		assertEquals(exp,res);
	}

	@Test
	public void testRenameVars() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x2), 
				Expressions.makePositiveLiteral("P1", x0, x1), Expressions.makePositiveLiteral("P2", x1, x2));
		List<Rule> res = rg.renameVars(r, x3);
		List<Rule> exp = new ArrayList<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x3, x2), 
				Expressions.makePositiveLiteral("P1", x3, x1), Expressions.makePositiveLiteral("P2", x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x3), 
				Expressions.makePositiveLiteral("P1", x0, x1), Expressions.makePositiveLiteral("P2", x1, x3)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x2), 
				Expressions.makePositiveLiteral("P1", x0, x3), Expressions.makePositiveLiteral("P2", x3, x2)));
		assertEquals(exp,res);
	}
	
	@Test
	public void testInsertVars() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0), Expressions.makePositiveLiteral("P1", x0));
		List<Rule> res = rg.insertVar(r,x1);
		List<Rule> exp = new ArrayList<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x1, x0), Expressions.makePositiveLiteral("P1", x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x1, x0), Expressions.makePositiveLiteral("P1", x0, x1)));
		assertEquals(exp,res);
	}
	
	@Test
	public void testIsSimilarVariable() {
		assertTrue(rg.isSimilarVariables(Arrays.asList(), Arrays.asList()));
		assertTrue(rg.isSimilarVariables(Arrays.asList(x1), Arrays.asList(x0)));
		assertTrue(rg.isSimilarVariables(Arrays.asList(x1), Arrays.asList(x1)));
		assertTrue(rg.isSimilarVariables(Arrays.asList(x0,x1), Arrays.asList(x1,x0)));
		assertTrue(rg.isSimilarVariables(Arrays.asList(x0,x1), Arrays.asList(x0,x1)));
		assertTrue(rg.isSimilarVariables(Arrays.asList(x0,x1), Arrays.asList(x0,x0)));
		assertFalse(rg.isSimilarVariables(Arrays.asList(x0,x0), Arrays.asList(x1,x0)));
		assertFalse(rg.isSimilarVariables(Arrays.asList(x1), Arrays.asList()));
		assertFalse(rg.isSimilarVariables(Arrays.asList(), Arrays.asList(x1)));
	}
	
	@Test
	public void testExistsSimilar() {
		List<Rule> set = new ArrayList<>();
		set.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x1, x0), Expressions.makePositiveLiteral("P1", x1, x0)));
		set.add(Expressions.makeRule(Expressions.makePositiveLiteral("P0", x1, x0), Expressions.makePositiveLiteral("P1", x0, x1)));
		assertTrue(rg.existSimilar(set, Expressions.makeRule(Expressions.makePositiveLiteral("P0", x1, x0), Expressions.makePositiveLiteral("P1", x1, x0))));
		assertTrue(rg.existSimilar(set, Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x1), Expressions.makePositiveLiteral("P1", x0, x1))));
		assertTrue(rg.existSimilar(set, Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x0, x0))));
		assertFalse(rg.existSimilar(set, Expressions.makeRule(Expressions.makePositiveLiteral("P0", x0, x0), Expressions.makePositiveLiteral("P1", x1, x0))));
	}
	
	@Test
	public void testEnumerate() {
		List<Rule> res = rg.enumerateLiteralGenerator(1);
		List<Rule> exp = new ArrayList<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x1, x0), Expressions.makePositiveLiteral(inp, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inv, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x1, x0), Expressions.makePositiveLiteral(oup, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(oup, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x1, x0), Expressions.makePositiveLiteral(inp, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x1, x0), Expressions.makePositiveLiteral(inv, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inv, x1, x0)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(oup, x1, x0)));
		assertEquals(new HashSet<Rule>(res), new HashSet<Rule>(exp));
	}
	
	@Test
	public void testMetaGen() {
		List<Rule> res = rg.simpleMetaGenerator();
		List<Rule> exp = new ArrayList<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inp, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inv, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(oup, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inv, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(inv, x0, x2), 
				Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(oup, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inp, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(inv, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(inp, x0, x1), Expressions.makePositiveLiteral(oup, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inp, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(inv, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(inv, x0, x1), Expressions.makePositiveLiteral(oup, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inp, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(inv, x1, x2)));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(oup, x0, x2), 
				Expressions.makePositiveLiteral(oup, x0, x1), Expressions.makePositiveLiteral(oup, x1, x2)));
		assertEquals(new HashSet<Rule>(res), new HashSet<Rule>(exp));
	}
}
