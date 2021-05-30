package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

public class RPQConjunctionImplTest {

	@Test
	public void testGettersRPQs() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");

		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final EdgeLabel q = RPQExpressions.makeEdgeLabel("q");
		final KStarRegExpression ps = RPQExpressions.makeKStarRegExpression(p);
		final ConcatRegExpression pq = RPQExpressions.makeConcatRegExpression(p, q);
		
		final RegPathQuery rpq1 = RPQExpressions.makeRegPathQuery(p, x, c);
		final RegPathQuery rpq2 = RPQExpressions.makeRegPathQuery(ps, y, x);
		final RegPathQuery rpq3 = RPQExpressions.makeRegPathQuery(pq, x, d);
		
		final List<RegPathQuery> rpqList = Arrays.asList(rpq1, rpq2, rpq3);
		final List<Term> projVars = Arrays.asList(x, y);
		final RPQConjunction<RegPathQuery> conjunction = new RPQConjunctionImpl<>(rpqList, projVars);

		assertEquals(rpqList, conjunction.getRPQs());
		assertEquals(projVars, conjunction.getProjVars());
	}

	@Test
	public void testEqualsRPQs() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final EdgeLabel q = RPQExpressions.makeEdgeLabel("q");
		final KStarRegExpression ps = RPQExpressions.makeKStarRegExpression(p);
		final ConcatRegExpression pq = RPQExpressions.makeConcatRegExpression(p, q);
		
		final RegPathQuery rpq1 = RPQExpressions.makeRegPathQuery(p, x, c);
		final RegPathQuery rpq2 = RPQExpressions.makeRegPathQuery(ps, y, x);
		final RegPathQuery rpq3 = RPQExpressions.makeRegPathQuery(pq, x, d);
		
		final List<RegPathQuery> rpqList = Arrays.asList(rpq1, rpq2, rpq3);
		final List<Term> projVars = Arrays.asList(x, y);
		
		final RPQConjunction<RegPathQuery> conjunction1 = new RPQConjunctionImpl<>(rpqList, projVars);
		final RPQConjunction<RegPathQuery> conjunction2 = RPQExpressions.makeRPQConjunction(rpqList, projVars);
		final RPQConjunction<RegPathQuery> conjunction3 = RPQExpressions.makeRPQConjunction(Arrays.asList(rpq1, rpq2, rpq3), projVars);
		final RPQConjunction<RegPathQuery> conjunction4 = RPQExpressions.makeRPQConjunction(rpqList, Arrays.asList(y, x));
		final RPQConjunction<RegPathQuery> conjunction5 = RPQExpressions.makeRPQConjunction(Arrays.asList(rpq1, rpq3, rpq2), projVars);
		final RPQConjunction<RegPathQuery> conjunction6 = RPQExpressions.makeRPQConjunction(rpqList, Arrays.asList(x));

		assertEquals(conjunction1, conjunction1);
		assertEquals(conjunction2, conjunction1);
		assertEquals(conjunction3, conjunction1);
		assertEquals(conjunction4, conjunction1);
		assertEquals(conjunction2.hashCode(), conjunction1.hashCode());
		assertEquals(conjunction3.hashCode(), conjunction1.hashCode());
		assertEquals(conjunction4.hashCode(), conjunction1.hashCode());
		assertNotEquals(conjunction5, conjunction1);
		assertNotEquals(conjunction5.hashCode(), conjunction1.hashCode());
		assertNotEquals(conjunction6, conjunction1);
		assertFalse(conjunction1.equals(null));
		assertFalse(conjunction1.equals(c));
	}
	
	@Test(expected = NullPointerException.class)
	public void rpqsNotNull() {
		final Variable x = Expressions.makeUniversalVariable("X");
		new RPQConjunctionImpl<RegPathQuery>(null, Arrays.asList(x));
	}

	@Test(expected = NullPointerException.class)
	public void projVarsNotNull() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final RegPathQuery rpq = RPQExpressions.makeRegPathQuery(p, x, x);
		new RPQConjunctionImpl<RegPathQuery>(Arrays.asList(rpq), null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void rpqsNoNullElements() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final RegPathQuery rpq = RPQExpressions.makeRegPathQuery(p, x, x);
		new RPQConjunctionImpl<RegPathQuery>(Arrays.asList(rpq, null), Arrays.asList(x));
	}

	@Test(expected = IllegalArgumentException.class)
	public void projVarsNoNullElements() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final RegPathQuery rpq = RPQExpressions.makeRegPathQuery(p, x, x);
		new RPQConjunctionImpl<RegPathQuery>(Arrays.asList(rpq), Arrays.asList(x, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void projVarsNoDuplElements() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final RegPathQuery rpq = RPQExpressions.makeRegPathQuery(p, x, x);
		new RPQConjunctionImpl<RegPathQuery>(Arrays.asList(rpq), Arrays.asList(x, x));
	}

	@Test
	public void conjunctionToStringTest() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final EdgeLabel q = RPQExpressions.makeEdgeLabel("q");
		final KStarRegExpression ps = RPQExpressions.makeKStarRegExpression(p);
		final ConcatRegExpression pq = RPQExpressions.makeConcatRegExpression(p, q);
		
		final RegPathQuery rpq1 = RPQExpressions.makeRegPathQuery(p, x, c);
		final RegPathQuery rpq2 = RPQExpressions.makeRegPathQuery(ps, y, x);
		final RegPathQuery rpq3 = RPQExpressions.makeRegPathQuery(pq, x, d);
		
		final List<RegPathQuery> rpqList = Arrays.asList(rpq1, rpq2, rpq3);
		final List<Term> projVars = Arrays.asList(x, y);
		
		final RPQConjunction<RegPathQuery> conjunction1 = new RPQConjunctionImpl<>(rpqList, projVars);
		assertEquals("?X p c . ?Y (p*) ?X . ?X (p / q) d .", conjunction1.toString());
	}
	
}
