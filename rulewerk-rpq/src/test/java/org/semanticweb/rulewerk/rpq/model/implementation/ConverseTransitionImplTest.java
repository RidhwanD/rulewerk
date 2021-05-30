package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;

public class ConverseTransitionImplTest {

	@Test
	public void testGetters() {
		final ConverseEdgeLabel x = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("X"));
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		final ConverseTransition t1 = RPQExpressions.makeConverseTransition(s1, s2, x);
		final ConverseTransition t2 = new ConverseTransitionImpl(s1, s2, x);
		final ConverseTransition t3 = RPQExpressions.makeConverseTransition(s2, s1, x);

		assertEquals(s1, t1.getOrigin());
		assertEquals(s2, t1.getDest());
		assertEquals(x, t1.getLabel());

		assertEquals(s1, t2.getOrigin());
		assertEquals(s2, t2.getDest());
		assertEquals(x, t2.getLabel());
		
		assertEquals(s2, t3.getOrigin());
		assertEquals(s1, t3.getDest());
		assertEquals(x, t3.getLabel());
	}
	
	@Test
	public void testEquals() {
		final ConverseEdgeLabel x = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("X"));
		final ConverseEdgeLabel y = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("Y"));
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		final ConverseTransition t1 = RPQExpressions.makeConverseTransition(s1, s2, x);
		final ConverseTransition t2 = new ConverseTransitionImpl(s1, s2, x);
		final ConverseTransition t3 = RPQExpressions.makeConverseTransition(s2, s1, x);
		final ConverseTransition t4 = RPQExpressions.makeConverseTransition(s1, s2, y);

		assertEquals(t1, t1);
		assertEquals(t1, t2);
		assertEquals(t1.hashCode(), t1.hashCode());
		assertNotEquals(t3, t1);
		assertNotEquals(t3.hashCode(), t1.hashCode());
		assertNotEquals(t3, t2);
		assertNotEquals(t3.hashCode(), t2.hashCode());
		assertNotEquals(t4, t1);
		assertNotEquals(t4.hashCode(), t1.hashCode());
		assertNotEquals(t4, t2);
		assertNotEquals(t4.hashCode(), t2.hashCode());
		assertFalse(t1.equals(null));
		assertFalse(t1.equals(x));
	}
	
	@Test(expected = NullPointerException.class)
	public void origNotNull() {
		final ConverseEdgeLabel x = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("X"));
		final State s2 = RPQExpressions.makeState("q2");
		
		new ConverseTransitionImpl(null, s2, x);
	}
	
	@Test(expected = NullPointerException.class)
	public void destNotNull() {
		final ConverseEdgeLabel x = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("X"));
		final State s1 = RPQExpressions.makeState("q1");
		
		new ConverseTransitionImpl(s1, null, x);
	}
	
	@Test(expected = NullPointerException.class)
	public void convLabelNotNull() {
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		new ConverseTransitionImpl(s1, s2, null);
	}

	@Test
	public void convTransTostringTest() {
		final ConverseEdgeLabel x = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("X"));
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		final ConverseTransition t1 = RPQExpressions.makeConverseTransition(s1, s2, x);
		final ConverseTransition t2 = new ConverseTransitionImpl(s1, s2, x);
		
		assertEquals("(q1, (^X), q2)", t1.toString());
		assertEquals("(q1, (^X), q2)", t2.toString());
	}
	
}
