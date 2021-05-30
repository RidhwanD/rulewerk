package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

public class AlternRegExpressionImplTest {
	
	@Test
	public void testGetters() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		final EdgeLabel z = RPQExpressions.makeEdgeLabel("Z");
		
		final AlternRegExpression alternXY = RPQExpressions.makeAlternRegExpression(x, y);
		final AlternRegExpression calternXZ = new AlternRegExpressionImpl(x, z);
		final AlternRegExpression alternXYZ = RPQExpressions.makeAlternRegExpression(alternXY, z);

		assertEquals("(X | Y)", alternXY.getName());
		assertEquals(x, alternXY.getExp1());
		assertEquals(y, alternXY.getExp2());

		assertEquals("(X | Z)", calternXZ.getName());
		assertEquals(x, calternXZ.getExp1());
		assertEquals(z, calternXZ.getExp2());
		
		assertEquals("((X | Y) | Z)", alternXYZ.getName());
		assertEquals(alternXY, alternXYZ.getExp1());
		assertEquals(z, alternXYZ.getExp2());
	}
	
	@Test
	public void testEquals() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		final EdgeLabel z = RPQExpressions.makeEdgeLabel("Z");
		
		final AlternRegExpression altern1 = RPQExpressions.makeAlternRegExpression(x, y);
		final AlternRegExpression altern2 = new AlternRegExpressionImpl(x, y);
		final AlternRegExpression altern3 = RPQExpressions.makeAlternRegExpression(altern1, z);
		final AlternRegExpression altern4 = new AlternRegExpressionImpl(y, x);

		assertEquals(altern1, altern1);
		assertEquals(altern1, altern2);
		assertEquals(altern1, altern4);
		assertEquals(altern1.hashCode(), altern1.hashCode());
		assertNotEquals(altern3, altern1);
		assertNotEquals(altern3.hashCode(), altern1.hashCode());
		assertNotEquals(altern3, altern4);
		assertNotEquals(altern3.hashCode(), altern4.hashCode());
		assertFalse(altern1.equals(null));
		assertFalse(altern1.equals(x));
	}
	
	@Test
	public void testSetters() {
		final EdgeLabel v = RPQExpressions.makeEdgeLabel("V");
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		final EdgeLabel z = RPQExpressions.makeEdgeLabel("Z");
		
		final AlternRegExpression alternXY = RPQExpressions.makeAlternRegExpression(x, y);

		assertEquals("(X | Y)", alternXY.getName());
		assertEquals(x, alternXY.getExp1());
		assertEquals(y, alternXY.getExp2());

		alternXY.setExp2(z);
		assertEquals("(X | Z)", alternXY.getName());
		assertEquals(x, alternXY.getExp1());
		assertEquals(z, alternXY.getExp2());
		
		alternXY.setExp1(v);
		assertEquals("(V | Z)", alternXY.getName());
		assertEquals(v, alternXY.getExp1());
		assertEquals(z, alternXY.getExp2());
	}
	
	@Test(expected = NullPointerException.class)
	public void exp1NotNull() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		new AlternRegExpressionImpl(null, x);
	}
	
	@Test(expected = NullPointerException.class)
	public void exp2NotNull() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		new AlternRegExpressionImpl(x, null);
	}

	@Test
	public void alternTostringTest() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		
		final AlternRegExpression altern1 = RPQExpressions.makeAlternRegExpression(x, y);
		final AlternRegExpression altern2 = new AlternRegExpressionImpl(x, y);
		
		assertEquals("(X | Y)", altern1.toString());
		assertEquals("(X | Y)", altern2.toString());
	}
	
}
