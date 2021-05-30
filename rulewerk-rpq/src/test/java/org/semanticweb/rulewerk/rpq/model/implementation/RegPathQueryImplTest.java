package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

public class RegPathQueryImplTest {

	@Test
	public void testGetters() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");	
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final EdgeLabel q = RPQExpressions.makeEdgeLabel("q");
		
		final RegPathQuery rpq1 = RPQExpressions.makeRegPathQuery(p, x, c);
		final RegPathQuery rpq2 = new RegPathQueryImpl(q, d, y);

		assertEquals("p", rpq1.getExpression().getName());
		assertEquals(x, rpq1.getTerm1());
		assertEquals(c, rpq1.getTerm2());

		assertEquals("q", rpq2.getExpression().getName());
		assertEquals(d, rpq2.getTerm1());
		assertEquals(y, rpq2.getTerm2());
	}
	
	@Test
	public void testEquals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Constant c = Expressions.makeAbstractConstant("c");

		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final EdgeLabel q = RPQExpressions.makeEdgeLabel("q");

		final RegPathQuery rpq1 = RPQExpressions.makeRegPathQuery(p, x, c);
		final RegPathQuery rpq2 = new RegPathQueryImpl(p, x, c);
		final RegPathQuery rpq3 = new RegPathQueryImpl(q, x, c);
		final RegPathQuery rpq4 = new RegPathQueryImpl(p, c, x);

		assertEquals(rpq1, rpq1);
		assertEquals(rpq1, rpq2);
		assertEquals(rpq1.hashCode(), rpq1.hashCode());
		assertNotEquals(rpq3, rpq1);
		assertNotEquals(rpq3.hashCode(), rpq1.hashCode());
		assertNotEquals(rpq4, rpq1);
		assertNotEquals(rpq4.hashCode(), rpq1.hashCode());
		assertFalse(rpq1.equals(null));
		assertFalse(rpq1.equals(c));
	}
	
	@Test(expected = NullPointerException.class)
	public void term1NotNull() {
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final Variable x = Expressions.makeUniversalVariable("X");
		new RegPathQueryImpl(p, null, x);
	}

	@Test(expected = NullPointerException.class)
	public void term2NotNull() {
		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
		final Variable x = Expressions.makeUniversalVariable("X");
		new RegPathQueryImpl(p, x, null);
	}

	@Test(expected = NullPointerException.class)
	public void regexNotNull() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		new RegPathQueryImpl(null, y, x);
	}

	@Test
	public void positiveLiteralTostringTest() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Constant c = Expressions.makeAbstractConstant("c");

		final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");

		final RegPathQuery rpq1 = RPQExpressions.makeRegPathQuery(p, x, c);
		final RegPathQuery rpq2 = new RegPathQueryImpl(p, x, c);
		assertEquals("?X p c .", rpq1.toString());
		assertEquals("?X p c .", rpq2.toString());
	}
	
}
