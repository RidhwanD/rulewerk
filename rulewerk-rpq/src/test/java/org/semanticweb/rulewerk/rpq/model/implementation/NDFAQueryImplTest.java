package org.semanticweb.rulewerk.rpq.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.NDFAQuery;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;

public class NDFAQueryImplTest {
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
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		final Constant c = Expressions.makeAbstractConstant("c");
		final Constant d = Expressions.makeAbstractConstant("d");
		
		transition.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition2.add(RPQExpressions.makeTransition(q0, q2, empStr));
		
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa2 = new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition2, convTransition);
		
		final NDFAQuery ndfaq1 = RPQExpressions.makeNDFAQuery(ndfa1, x, c);
		final NDFAQuery ndfaq2 = new NDFAQueryImpl(ndfa2, d, y);

		assertEquals(ndfa1, ndfaq1.getNDFA());
		assertEquals(x, ndfaq1.getTerm1());
		assertEquals(c, ndfaq1.getTerm2());

		assertEquals(ndfa2, ndfaq2.getNDFA());
		assertEquals(d, ndfaq2.getTerm1());
		assertEquals(y, ndfaq2.getTerm2());
	}
	
	@Test
	public void testEquals() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Constant c = Expressions.makeAbstractConstant("c");

		transition.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition2.add(RPQExpressions.makeTransition(q0, q2, empStr));
		
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		final NDFiniteAutomata ndfa2 = new NDFiniteAutomataImpl(abe, states, alphabet, q0, finStates, transition2, convTransition);
		
		final NDFAQuery ndfaq1 = RPQExpressions.makeNDFAQuery(ndfa1, x, c);
		final NDFAQuery ndfaq2 = new NDFAQueryImpl(ndfa1, x, c);
		final NDFAQuery ndfaq3 = new NDFAQueryImpl(ndfa2, x, c);
		final NDFAQuery ndfaq4 = new NDFAQueryImpl(ndfa1, x, x);
		final NDFAQuery ndfaq5 = new NDFAQueryImpl(ndfa1, c, c);

		assertEquals(ndfaq1, ndfaq1);
		assertEquals(ndfaq1, ndfaq2);
		assertEquals(ndfaq1.hashCode(), ndfaq1.hashCode());
		assertNotEquals(ndfaq3, ndfaq1);
		assertNotEquals(ndfaq3.hashCode(), ndfaq1.hashCode());
		assertNotEquals(ndfaq4, ndfaq1);
		assertNotEquals(ndfaq4.hashCode(), ndfaq1.hashCode());
		assertNotEquals(ndfaq5, ndfaq1);
		assertNotEquals(ndfaq5.hashCode(), ndfaq1.hashCode());
		assertFalse(ndfaq1.equals(null));
		assertFalse(ndfaq1.equals(c));
	}
	
	@Test(expected = NullPointerException.class)
	public void term1NotNull() {
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		final Variable x = Expressions.makeUniversalVariable("X");
		new NDFAQueryImpl(ndfa1, null, x);
	}

	@Test(expected = NullPointerException.class)
	public void term2NotNull() {
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		final Variable x = Expressions.makeUniversalVariable("X");
		new NDFAQueryImpl(ndfa1, x, null);
	}

	@Test(expected = NullPointerException.class)
	public void ndfaNotNull() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Variable y = Expressions.makeUniversalVariable("Y");
		new NDFAQueryImpl(null, y, x);
	}

	@Test
	public void ndfaqTostringTest() {
		final Variable x = Expressions.makeUniversalVariable("X");
		final Constant c = Expressions.makeAbstractConstant("c");

		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		
		final NDFAQuery ndfaq1 = RPQExpressions.makeNDFAQuery(ndfa1, x, c);
		final NDFAQuery ndfaq2 = new NDFAQueryImpl(ndfa1, x, c);
		assertEquals("?X " + ndfa1 + " c .", ndfaq1.toString());
		assertEquals("?X " + ndfa1 + " c .", ndfaq2.toString());
	}
	
}
