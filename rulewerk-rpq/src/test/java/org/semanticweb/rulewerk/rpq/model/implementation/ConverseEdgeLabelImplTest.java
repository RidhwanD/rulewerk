package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

public class ConverseEdgeLabelImplTest {

	@Test
	public void testGetters() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		
		final ConverseEdgeLabel cel1 = RPQExpressions.makeConverseEdgeLabel(x);
		final ConverseEdgeLabel cel2 = new ConverseEdgeLabelImpl(y);
		final Object[] cel3 = RPQExpressions.makeEdgeLabelAndConverse("X");

		assertEquals("(^X)", cel1.getName());
		assertEquals(x, cel1.getConverseOf());

		assertEquals("(^Y)", cel2.getName());
		assertEquals(y, cel2.getConverseOf());
		
		assertEquals("(^X)", ((ConverseEdgeLabel) cel3[1]).getName());
		assertEquals(x, ((ConverseEdgeLabel) cel3[1]).getConverseOf());
		assertEquals(x, ((EdgeLabel) cel3[0]));
	}
	
	@Test
	public void testEquals() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		
		final ConverseEdgeLabel cel1 = RPQExpressions.makeConverseEdgeLabel(x);
		final ConverseEdgeLabel cel2 = new ConverseEdgeLabelImpl(y);
		final Object[] cel3 = RPQExpressions.makeEdgeLabelAndConverse("X");
		final ConverseEdgeLabel cel4 = new ConverseEdgeLabelImpl(x);

		assertEquals(cel1, cel1);
		assertEquals(cel1, ((ConverseEdgeLabel) cel3[1]));
		assertEquals(cel1, cel4);
		assertEquals(cel1.hashCode(), cel1.hashCode());
		assertNotEquals(cel2, cel1);
		assertNotEquals(cel2.hashCode(), cel1.hashCode());
		assertNotEquals(cel2, cel4);
		assertNotEquals(cel2.hashCode(), cel4.hashCode());
		assertFalse(cel1.equals(null));
		assertFalse(cel1.equals(x));
	}
	
	@Test(expected = NullPointerException.class)
	public void elNotNull() {
		new ConverseEdgeLabelImpl(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void elNotEmpty() {
		final EdgeLabel empty = RPQExpressions.makeEdgeLabel("");
		new ConverseEdgeLabelImpl(empty);
	}

	@Test(expected = NullPointerException.class)
	public void edgeLabelNotNull() {
		final EdgeLabel nullEdgeLabel = null;
		RPQExpressions.makeConverseEdgeLabel(nullEdgeLabel);
	}

	@Test(expected = NullPointerException.class)
	public void edgeLabelNameNotNull() {
		final String nullEdgeLabelName = null;
		RPQExpressions.makeEdgeLabelAndConverse(nullEdgeLabelName);
	}

	@Test
	public void converseTostringTest() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		
		final ConverseEdgeLabel cel1 = RPQExpressions.makeConverseEdgeLabel(x);
		final ConverseEdgeLabel cel2 = new ConverseEdgeLabelImpl(x);
		
		assertEquals("(^X)", cel1.toString());
		assertEquals("(^X)", cel2.toString());
	}
	
}
