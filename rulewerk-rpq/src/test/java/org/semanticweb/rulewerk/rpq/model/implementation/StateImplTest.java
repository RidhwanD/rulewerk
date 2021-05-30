package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.State;

public class StateImplTest {

	@Test
	public void testGetters() {
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = new StateImpl("q2");

		assertEquals("q1", s1.getName());
		assertEquals("q2", s2.getName());
	}
	
	@Test
	public void testEquals() {
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = new StateImpl("q1");
		final State s3 = RPQExpressions.makeState("q2");
		
		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertEquals(s1.hashCode(), s1.hashCode());
		assertNotEquals(s3, s1);
		assertNotEquals(s3.hashCode(), s1.hashCode());
		assertNotEquals(s3, s2);
		assertNotEquals(s3.hashCode(), s2.hashCode());
		assertFalse(s1.equals(null));
		assertFalse(s1.equals("X"));
	}
	
	@Test(expected = NullPointerException.class)
	public void stateNotNull() {
		new StateImpl(null);
	}

	@Test(expected = NullPointerException.class)
	public void stateNameNotNull() {
		final String nullStateName = null;
		RPQExpressions.makeState(nullStateName);
	}

	@Test
	public void stateTostringTest() {
		final State s1 = RPQExpressions.makeState("q1");
		final State s2 = new StateImpl("q1");
		
		assertEquals("q1", s1.toString());
		assertEquals("q1", s2.toString());
	}
	
}
