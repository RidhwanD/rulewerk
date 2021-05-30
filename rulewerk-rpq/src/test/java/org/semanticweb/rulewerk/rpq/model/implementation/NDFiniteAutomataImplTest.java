package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;

public class NDFiniteAutomataImplTest {
	private final State q0 = RPQExpressions.makeState("q0");
	private final State q1 = RPQExpressions.makeState("q1");
	private final State q2 = RPQExpressions.makeState("q2");
	private final State q3 = RPQExpressions.makeState("q3");
	private final State q4 = RPQExpressions.makeState("q4");
	private final State q5 = RPQExpressions.makeState("q5");
	private final State q6 = RPQExpressions.makeState("q6");
	
	private final EdgeLabel empStr = RPQExpressions.makeEdgeLabel("");
	private final EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
	private final EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
	private final EdgeLabel c = RPQExpressions.makeEdgeLabel("c");
	private final ConverseEdgeLabel cb = RPQExpressions.makeConverseEdgeLabel(b);
	private final ConverseEdgeLabel cc = RPQExpressions.makeConverseEdgeLabel(c);
	
	ConcatRegExpression ab = RPQExpressions.makeConcatRegExpression(a, b);
	AlternRegExpression abe = RPQExpressions.makeAlternRegExpression(ab, empStr);
	
	final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a,b));
	final Set<EdgeLabel> alphabet2 = new HashSet<EdgeLabel>(Arrays.asList(a,b,c));
	
	final Set<State> states = new HashSet<State>(Arrays.asList(q0,q1,q2,q3,q4,q5));
	final Set<State> states2 = new HashSet<State>(Arrays.asList(q0,q1,q2,q3,q4,q5,q6));
	final Set<State> finStates = new HashSet<State>(Arrays.asList(q3,q5));
	
	final Set<Transition> transition = new HashSet<Transition>();
	
	final Set<Transition> transition2 = new HashSet<Transition>();
	
	final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
	final Set<ConverseTransition> convTransition2 = new HashSet<ConverseTransition>();
	
	@Test
	public void testGetters() {
		transition.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition.add(RPQExpressions.makeTransition(q0, q2, empStr));
		transition.add(RPQExpressions.makeTransition(q1, q3, empStr));
		transition.add(RPQExpressions.makeTransition(q2, q4, a));
		transition.add(RPQExpressions.makeTransition(q4, q5, b));
		
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa2 = new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa3 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q4, new HashSet<State>(Arrays.asList(q1, q4)), transition, convTransition);
		
		assertEquals(abe, ndfa1.getRegex());
		assertEquals(states, ndfa1.getState());
		assertEquals(alphabet, ndfa1.getAlphabet());
		assertEquals(q0, ndfa1.getInitState());
		assertEquals(finStates, ndfa1.getFinState());
		assertEquals(transition, ndfa1.getTransition());
		assertEquals(convTransition, ndfa1.getConvTransition());

		assertEquals(abe, ndfa2.getRegex());
		assertEquals(states, ndfa2.getState());
		assertEquals(alphabet, ndfa2.getAlphabet());
		assertEquals(q0, ndfa2.getInitState());
		assertEquals(finStates, ndfa2.getFinState());
		assertEquals(transition, ndfa2.getTransition());
		assertEquals(convTransition, ndfa2.getConvTransition());

		assertEquals(abe, ndfa3.getRegex());
		assertEquals(states, ndfa3.getState());
		assertEquals(alphabet, ndfa3.getAlphabet());
		assertEquals(q4, ndfa3.getInitState());
		assertEquals(new HashSet<State>(Arrays.asList(q1, q4)), ndfa3.getFinState());
		assertEquals(transition, ndfa3.getTransition());
		assertEquals(convTransition, ndfa3.getConvTransition());
	}
	
	@Test
	public void testEquals() {
		transition.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition.add(RPQExpressions.makeTransition(q0, q2, empStr));
		transition.add(RPQExpressions.makeTransition(q1, q3, empStr));
		transition.add(RPQExpressions.makeTransition(q2, q4, a));
		transition.add(RPQExpressions.makeTransition(q4, q5, b));
		transition2.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition2.add(RPQExpressions.makeTransition(q0, q2, empStr));
		transition2.add(RPQExpressions.makeTransition(q1, q3, empStr));
		transition2.add(RPQExpressions.makeTransition(q2, q4, a));
		convTransition2.add(RPQExpressions.makeConverseTransition(q1, q0, cb));
		
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa2 = new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa3 = RPQExpressions.makeNDFiniteAutomata(ab, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa4 = RPQExpressions.makeNDFiniteAutomata(abe, states2, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa5 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet2, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa6 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q4, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa7 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, new HashSet<State>(Arrays.asList(q1, q4)), transition, convTransition);
		final NDFiniteAutomata ndfa8 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition2, convTransition);
		final NDFiniteAutomata ndfa9 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition2);
		
		assertEquals(ndfa1, ndfa1);
		assertEquals(ndfa1, ndfa2);
		assertEquals(ndfa1.hashCode(), ndfa1.hashCode());
		assertNotEquals(ndfa3, ndfa1);
		assertNotEquals(ndfa3.hashCode(), ndfa1.hashCode());
		assertNotEquals(ndfa4, ndfa1);
		assertNotEquals(ndfa4.hashCode(), ndfa1.hashCode());
		assertNotEquals(ndfa5, ndfa1);
		assertNotEquals(ndfa5.hashCode(), ndfa1.hashCode());
		assertNotEquals(ndfa6, ndfa1);
		assertNotEquals(ndfa6.hashCode(), ndfa1.hashCode());
		assertNotEquals(ndfa7, ndfa1);
		assertNotEquals(ndfa7.hashCode(), ndfa1.hashCode());
		assertNotEquals(ndfa8, ndfa1);
		assertNotEquals(ndfa8.hashCode(), ndfa1.hashCode());
		assertNotEquals(ndfa9, ndfa1);
		assertNotEquals(ndfa9.hashCode(), ndfa1.hashCode());
		assertFalse(ndfa1.equals(null));
		assertFalse(ndfa1.equals(c));
	}
	
	@Test(expected = NullPointerException.class)
	public void regexNotNull() {
		new NDFiniteAutomataImpl(null, states, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = NullPointerException.class)
	public void statesNotNull() {
		new NDFiniteAutomataImpl(abe, null, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = NullPointerException.class)
	public void alphabetNotNull() {
		new NDFiniteAutomataImpl(abe, states, null, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = NullPointerException.class)
	public void initNotNull() {
		new NDFiniteAutomataImpl(abe, states, alphabet, null, finStates, transition, convTransition);
	}
	
	@Test(expected = NullPointerException.class)
	public void finStatesNotNull() {
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, null, transition, convTransition);
	}
	
	@Test(expected = NullPointerException.class)
	public void transNotNull() {
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, null, convTransition);
	}
	
	@Test(expected = NullPointerException.class)
	public void convTransNotNull() {
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void initNotInStates() {
		new NDFiniteAutomataImpl(abe, states, alphabet, q6, finStates, transition, convTransition);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void finNotInStates() {
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, new HashSet<State>(Arrays.asList(q6)), transition, convTransition);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void transOrigNotInStates() {
		transition.add(RPQExpressions.makeTransition(q6, q0, empStr));
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void transDestNotInStates() {
		transition.add(RPQExpressions.makeTransition(q0, q6, empStr));
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void transLabelNotInAlphabet() {
		transition.add(RPQExpressions.makeTransition(q0, q1, c));
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void convTransOrigNotInStates() {
		convTransition.add(RPQExpressions.makeConverseTransition(q6, q0, cb));
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void convTransDestNotInStates() {
		convTransition.add(RPQExpressions.makeConverseTransition(q0, q6, cb));
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void convTransLabelNotInAlphabet() {
		convTransition.add(RPQExpressions.makeConverseTransition(q0, q1, cc));
		new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
	}
	
	@Test
	public void ndfaTostringTest() {
		transition.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition.add(RPQExpressions.makeTransition(q0, q2, empStr));
		transition.add(RPQExpressions.makeTransition(q1, q3, empStr));
		transition.add(RPQExpressions.makeTransition(q2, q4, a));
		transition.add(RPQExpressions.makeTransition(q4, q5, b));
		transition2.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition2.add(RPQExpressions.makeTransition(q0, q2, empStr));
		transition2.add(RPQExpressions.makeTransition(q1, q3, empStr));
		transition2.add(RPQExpressions.makeTransition(q2, q4, a));
		convTransition2.add(RPQExpressions.makeConverseTransition(q1, q0, cb));
		
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa2 = new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition, convTransition);
		
		assertEquals(abe.toString(), ndfa1.toString());
		assertEquals(abe.toString(), ndfa2.toString());
	}
	
}
