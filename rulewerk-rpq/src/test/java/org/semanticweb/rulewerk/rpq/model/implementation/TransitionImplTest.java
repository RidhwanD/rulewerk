package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

public class TransitionImplTest {

	@Test
	public void testGetters() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		final Transition t1 = RPQExpressions.makeTransition(s1, s2, x);
		final Transition t2 = new TransitionImpl(s1, s2, x);
		final Transition t3 = RPQExpressions.makeTransition(s2, s1, x);

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
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final EdgeLabel y = RPQExpressions.makeEdgeLabel("Y");
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		final Transition t1 = RPQExpressions.makeTransition(s1, s2, x);
		final Transition t2 = new TransitionImpl(s1, s2, x);
		final Transition t3 = RPQExpressions.makeTransition(s2, s1, x);
		final Transition t4 = RPQExpressions.makeTransition(s1, s2, y);

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
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final State s2 = RPQExpressions.makeState("q2");
		
		new TransitionImpl(null, s2, x);
	}
	
	@Test(expected = NullPointerException.class)
	public void destNotNull() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final State s1 = RPQExpressions.makeState("q1");
		
		new TransitionImpl(s1, null, x);
	}
	
	@Test(expected = NullPointerException.class)
	public void labelNotNull() {
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		new TransitionImpl(s1, s2, null);
	}

	@Test
	public void transitionTostringTest() {
		final EdgeLabel x = RPQExpressions.makeEdgeLabel("X");
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = RPQExpressions.makeState("q2");
		
		final Transition t1 = RPQExpressions.makeTransition(s1, s2, x);
		final Transition t2 = new TransitionImpl(s1, s2, x);
		
		assertEquals("(q1, X, q2)", t1.toString());
		assertEquals("(q1, X, q2)", t2.toString());
	}
	
}
