package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

public class ConcatRegExpressionImplTest {
	
	@Test
	public void testGetters() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		final EdgeLabel z = RPQExpressions.makeEdgeLabel("Z");
		
		final ConcatRegExpression concatXY = RPQExpressions.makeConcatRegExpression(x, y);
		final ConcatRegExpression cconcatXZ = new ConcatRegExpressionImpl(x, z);
		final ConcatRegExpression concatXYZ = RPQExpressions.makeConcatRegExpression(concatXY, z);

		assertEquals("(X / Y)", concatXY.getName());
		assertEquals(x, concatXY.getExp1());
		assertEquals(y, concatXY.getExp2());

		assertEquals("(X / Z)", cconcatXZ.getName());
		assertEquals(x, cconcatXZ.getExp1());
		assertEquals(z, cconcatXZ.getExp2());
		
		assertEquals("((X / Y) / Z)", concatXYZ.getName());
		assertEquals(concatXY, concatXYZ.getExp1());
		assertEquals(z, concatXYZ.getExp2());
	}
	
	@Test
	public void testEquals() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		final EdgeLabel z = RPQExpressions.makeEdgeLabel("Z");
		
		final ConcatRegExpression altern1 = RPQExpressions.makeConcatRegExpression(x, y);
		final ConcatRegExpression altern2 = new ConcatRegExpressionImpl(x, y);
		final ConcatRegExpression altern3 = RPQExpressions.makeConcatRegExpression(altern1, z);
		final ConcatRegExpression altern4 = new ConcatRegExpressionImpl(y, x);

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
		
		final ConcatRegExpression alternXY = RPQExpressions.makeConcatRegExpression(x, y);

		assertEquals("(X / Y)", alternXY.getName());
		assertEquals(x, alternXY.getExp1());
		assertEquals(y, alternXY.getExp2());

		alternXY.setExp2(z);
		assertEquals("(X / Z)", alternXY.getName());
		assertEquals(x, alternXY.getExp1());
		assertEquals(z, alternXY.getExp2());
		
		alternXY.setExp1(v);
		assertEquals("(V / Z)", alternXY.getName());
		assertEquals(v, alternXY.getExp1());
		assertEquals(z, alternXY.getExp2());
	}
	
	@Test(expected = NullPointerException.class)
	public void exp1NotNull() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		new ConcatRegExpressionImpl(null, x);
	}
	
	@Test(expected = NullPointerException.class)
	public void exp2NotNull() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		new ConcatRegExpressionImpl(x, null);
	}

	@Test
	public void concatTostringTest() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		
		final ConcatRegExpression concat1 = RPQExpressions.makeConcatRegExpression(x, y);
		final ConcatRegExpression concat2 = new ConcatRegExpressionImpl(x, y);
		
		assertEquals("(X / Y)", concat1.toString());
		assertEquals("(X / Y)", concat2.toString());
	}
	
}
