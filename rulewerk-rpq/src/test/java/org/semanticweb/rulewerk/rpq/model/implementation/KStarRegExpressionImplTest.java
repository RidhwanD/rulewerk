package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;

public class KStarRegExpressionImplTest {

	@Test
	public void testGetters() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		
		final KStarRegExpression kp1 = RPQExpressions.makeKStarRegExpression(x);
		final KStarRegExpression kp2 = new KStarRegExpressionImpl(y);
		final KStarRegExpression kp3 = new KStarRegExpressionImpl(x);

		assertEquals("(X*)", kp1.getName());
		assertEquals(x, kp1.getExp());

		assertEquals("(Y*)", kp2.getName());
		assertEquals(y, kp2.getExp());
		
		assertEquals("(X*)", kp3.getName());
		assertEquals(x, kp3.getExp());
	}
	
	@Test
	public void testEquals() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		
		final KStarRegExpression kp1 = RPQExpressions.makeKStarRegExpression(x);
		final KStarRegExpression kp2 = new KStarRegExpressionImpl(y);
		final KStarRegExpression kp3 = new KStarRegExpressionImpl(x);

		assertEquals(kp1, kp1);
		assertEquals(kp1, kp3);
		assertEquals(kp1.hashCode(), kp1.hashCode());
		assertNotEquals(kp2, kp1);
		assertNotEquals(kp2.hashCode(), kp1.hashCode());
		assertNotEquals(kp2, kp3);
		assertNotEquals(kp2.hashCode(), kp3.hashCode());
		assertFalse(kp1.equals(null));
		assertFalse(kp1.equals(x));
	}
	
	@Test(expected = NullPointerException.class)
	public void elNotNull() {
		new KStarRegExpressionImpl(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void expNotNull() {
		final EdgeLabel nullEdgeLabel = null;
		RPQExpressions.makeKStarRegExpression(nullEdgeLabel);
	}

	@Test
	public void positiveLiteralTostringTest() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		
		final KStarRegExpression ks1 = RPQExpressions.makeKStarRegExpression(x);
		final KStarRegExpression ks2 = new KStarRegExpressionImpl(x);
		
		assertEquals("(X*)", ks1.toString());
		assertEquals("(X*)", ks2.toString());
	}
	
}
