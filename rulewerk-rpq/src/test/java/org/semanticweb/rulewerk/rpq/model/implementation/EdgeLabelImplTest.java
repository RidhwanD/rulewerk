package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

public class EdgeLabelImplTest {

	@Test
	public void testGetters() {
		final EdgeLabel el1 = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel el2 = new EdgeLabelImpl("Y");
		final Object[] el3 = RPQExpressions.makeEdgeLabelAndConverse("X");

		assertEquals("X", el1.getName());

		assertEquals("Y", el2.getName());
		
		assertEquals("X", ((EdgeLabel) el3[0]).getName());
	}
	
	@Test
	public void testEquals() {
		final EdgeLabel el1 = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel el2 = new EdgeLabelImpl("Y");
		final Object[] el3 = RPQExpressions.makeEdgeLabelAndConverse("X");
		final EdgeLabel el4 = new EdgeLabelImpl("X");

		assertEquals(el1, el1);
		assertEquals(el1, ((EdgeLabel) el3[0]));
		assertEquals(el1, el4);
		assertEquals(el1.hashCode(), el1.hashCode());
		assertNotEquals(el2, el1);
		assertNotEquals(el2.hashCode(), el1.hashCode());
		assertNotEquals(el2, el4);
		assertNotEquals(el2.hashCode(), el4.hashCode());
		assertFalse(el1.equals(null));
		assertFalse(el1.equals("X"));
	}
	
	@Test(expected = NullPointerException.class)
	public void elNotNull() {
		new EdgeLabelImpl(null);
	}

	@Test(expected = NullPointerException.class)
	public void edgeLabelNameNotNull() {
		final String nullEdgeLabelName = null;
		RPQExpressions.makeEdgeLabelAndConverse(nullEdgeLabelName);
	}

	@Test
	public void positiveLiteralTostringTest() {
		final EdgeLabel el1 = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel el2 = new EdgeLabelImpl("X");
		
		assertEquals("X", el1.toString());
		assertEquals("X", el2.toString());
	}
	
}
