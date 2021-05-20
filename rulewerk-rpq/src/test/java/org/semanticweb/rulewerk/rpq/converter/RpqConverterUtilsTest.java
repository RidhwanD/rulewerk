package org.semanticweb.rulewerk.rpq.converter;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;

public class RpqConverterUtilsTest {
	private final Variable x = Expressions.makeUniversalVariable("x");
	private final Variable y = Expressions.makeUniversalVariable("y");
	private final Variable z = Expressions.makeUniversalVariable("z");
	private final Predicate triple = Expressions.makePredicate("TRIPLE", 3);
	private final Constant c1 = Expressions.makeAbstractConstant("c1");
	private final Constant c2 = Expressions.makeAbstractConstant("c2");
	private final Constant c3 = Expressions.makeAbstractConstant("c3");
	private final Constant c4 = Expressions.makeAbstractConstant("c4");
	private final Constant ela = Expressions.makeAbstractConstant("a");
	private final Fact f1 = Expressions.makeFact(triple, c1, ela, c2);
	private final Fact f2 = Expressions.makeFact(triple, c3, ela, c4);
	
	private final EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
	private final EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
	private final EdgeLabel d = RPQExpressions.makeEdgeLabel("d");
	private final EdgeLabel epsilon = RPQExpressions.makeEdgeLabel("");
	private final ConverseEdgeLabel ac = RPQExpressions.makeConverseEdgeLabel(a);
	
	private final State q0 = RPQExpressions.makeState("q0");
	private final State q1 = RPQExpressions.makeState("q1");
	private final State q2 = RPQExpressions.makeState("q2");
	private final State q3 = RPQExpressions.makeState("q3");
	private final State q4 = RPQExpressions.makeState("q4");
	private final State q5 = RPQExpressions.makeState("q5");
	private final State q6 = RPQExpressions.makeState("q6");
	private final State q7 = RPQExpressions.makeState("q7");

	// ---------------------------------- Begin Test ----------------------------------------- //
	
	@Test
	public void testProduceFacts() {
		final Predicate p = Expressions.makePredicate("p", 2);
		final Fact f1 = Expressions.makeFact(p, c1, c1);
		final Fact f2 = Expressions.makeFact(p, c2, c2);
		final Fact f3 = Expressions.makeFact(p, c3, c3);
		final Fact f4 = Expressions.makeFact(p, c4, c4);
		final Fact f5 = Expressions.makeFact(p, ela, ela);
		Set<Term> terms = new HashSet<Term>(Arrays.asList(c1, c2, c3, c4, ela));
		Set<Statement> result = RpqConverterUtils.produceFacts(p, terms);
		Set<Statement> comparator = new HashSet<Statement>(Arrays.asList(f1, f2, f3, f4, f5));
		assertEquals(result, comparator);
	}
	
	@Test
	public void testKBase() {
		final Predicate p = Expressions.makePredicate("p", 2);
		final Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral(p, x, x), 
				Expressions.makePositiveLiteral(triple, x, y, z));
		final Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral(p, x, x), 
				Expressions.makePositiveLiteral(triple, z, y, x));
		Set<Rule> result = RpqConverterUtils.createKBaseRules(p);
		Set<Rule> comparator = new HashSet<Rule>(Arrays.asList(r1, r2));
		assertEquals(result, comparator);
	}
	
	@Test
	public void testRetrieveTerms() {
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(f1);	kb.addStatement(f2);
		Set<Term> result = RpqConverterUtils.getTermFromKB(kb);
		Set<Term> comparator = new HashSet<Term>(Arrays.asList(c1, c2, c3, c4, ela));
		assertEquals(result, comparator);
	}
	
	@Test
	public void testGetInOutNonNull() {
		Set<Transition> trans = new HashSet<Transition>();
		trans.add(RPQExpressions.makeTransition(q5, q0, b));
		trans.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		trans.add(RPQExpressions.makeTransition(q1, q4, epsilon));
		trans.add(RPQExpressions.makeTransition(q3, q2, epsilon));
		trans.add(RPQExpressions.makeTransition(q2, q4, d));
		Set<ConverseTransition> ctrans = new HashSet<ConverseTransition>();
		ctrans.add(RPQExpressions.makeConverseTransition(q5, q3, ac));
		Map<Integer, Set<State>> result = RpqConverterUtils.getInOut(q1, trans, ctrans);
		
		Map<Integer, Set<State>> comparator = new HashMap<Integer, Set<State>>();
		comparator.put(0, new HashSet<State>(Arrays.asList(q0)));
		comparator.put(1, new HashSet<State>(Arrays.asList(q4)));
		
		assertEquals(result, comparator);
	}
	
	@Test
	public void testGetInOutNullWithTrans() {
		Set<Transition> trans = new HashSet<Transition>();
		trans.add(RPQExpressions.makeTransition(q5, q0, b));
		trans.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		trans.add(RPQExpressions.makeTransition(q1, q4, epsilon));
		trans.add(RPQExpressions.makeTransition(q3, q2, epsilon));
		trans.add(RPQExpressions.makeTransition(q2, q4, d));
		Set<ConverseTransition> ctrans = new HashSet<ConverseTransition>();
		ctrans.add(RPQExpressions.makeConverseTransition(q5, q3, ac));
		Map<Integer, Set<State>> result = RpqConverterUtils.getInOut(q2, trans, ctrans);
		
		assertEquals(result, null);
	}
	
	@Test
	public void testGetInOutNullWithConv() {
		Set<Transition> trans = new HashSet<Transition>();
		trans.add(RPQExpressions.makeTransition(q5, q0, b));
		trans.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		trans.add(RPQExpressions.makeTransition(q1, q4, epsilon));
		trans.add(RPQExpressions.makeTransition(q3, q2, epsilon));
		trans.add(RPQExpressions.makeTransition(q2, q4, d));
		Set<ConverseTransition> ctrans = new HashSet<ConverseTransition>();
		ctrans.add(RPQExpressions.makeConverseTransition(q5, q3, ac));
		Map<Integer, Set<State>> result = RpqConverterUtils.getInOut(q3, trans, ctrans);
		
		assertEquals(result, null);
	}
	
	@Test
	public void testIsTranExistTrue() {
		Set<Transition> trans = new HashSet<Transition>();
		trans.add(RPQExpressions.makeTransition(q5, q0, b));
		trans.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		trans.add(RPQExpressions.makeTransition(q1, q4, epsilon));
		
		assertTrue(RpqConverterUtils.isTranExist(trans, RPQExpressions.makeTransition(q0, q1, epsilon)));
	}
	
	@Test
	public void testIsTranExistFalse() {
		Set<Transition> trans = new HashSet<Transition>();
		trans.add(RPQExpressions.makeTransition(q5, q0, b));
		trans.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		trans.add(RPQExpressions.makeTransition(q1, q4, epsilon));
		
		assertFalse(RpqConverterUtils.isTranExist(trans, RPQExpressions.makeTransition(q1, q1, epsilon)));
	}
	
	@Test
	public void testSimplifyNone() {
		final Set<State> states = new HashSet<State>(Arrays.asList(q0,q1,q2));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q2));
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a,b,d));
		final Set<Transition> transitionAlt = new HashSet<Transition>();
		transitionAlt.add(RPQExpressions.makeTransition(q0, q0, a));
		transitionAlt.add(RPQExpressions.makeTransition(q0, q1, b));
		transitionAlt.add(RPQExpressions.makeTransition(q0, q1, d));
		transitionAlt.add(RPQExpressions.makeTransition(q1, q2, a));
		transitionAlt.add(RPQExpressions.makeTransition(q2, q2, b));
		final Set<ConverseTransition> convTransitionAlt = new HashSet<ConverseTransition>();
		final NDFiniteAutomata nfa = RPQExpressions.makeNDFiniteAutomata(a, states, alphabet, q0, finStates, transitionAlt, convTransitionAlt);
		
		final NDFiniteAutomata result = RpqConverterUtils.simplify(nfa);
		assertEquals(result, nfa);
	}
	
	@Test
	public void testSimplifyCorrect1() {
		final Set<State> states = new HashSet<State>(Arrays.asList(q0,q1,q2,q3,q4,q5,q6,q7));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q7));
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a,b,d));
		Set<Transition> trans = new HashSet<Transition>();
		trans.add(RPQExpressions.makeTransition(q6, q5, epsilon));
		trans.add(RPQExpressions.makeTransition(q5, q0, b));
		trans.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		trans.add(RPQExpressions.makeTransition(q1, q4, epsilon));
		trans.add(RPQExpressions.makeTransition(q3, q2, epsilon));
		trans.add(RPQExpressions.makeTransition(q2, q4, d));
		trans.add(RPQExpressions.makeTransition(q4, q7, epsilon));
		Set<ConverseTransition> ctrans = new HashSet<ConverseTransition>();
		ctrans.add(RPQExpressions.makeConverseTransition(q5, q3, ac));
		final NDFiniteAutomata nfa = RPQExpressions.makeNDFiniteAutomata(a, states, alphabet, q6, finStates, trans, ctrans);
		final NDFiniteAutomata result = RpqConverterUtils.simplify(nfa);
		
		final Set<State> states2 = new HashSet<State>(Arrays.asList(q0,q2,q3,q4,q5,q6,q7));
		final Set<State> finStates2 = new HashSet<State>(Arrays.asList(q7));
		final Set<EdgeLabel> alphabet2 = new HashSet<EdgeLabel>(Arrays.asList(a,b,d));
		Set<Transition> trans2 = new HashSet<Transition>();
		trans2.add(RPQExpressions.makeTransition(q6, q5, epsilon));
		trans2.add(RPQExpressions.makeTransition(q5, q0, b));
		trans2.add(RPQExpressions.makeTransition(q0, q4, epsilon));
		trans2.add(RPQExpressions.makeTransition(q3, q2, epsilon));
		trans2.add(RPQExpressions.makeTransition(q2, q4, d));
		trans2.add(RPQExpressions.makeTransition(q4, q7, epsilon));
		Set<ConverseTransition> ctrans2 = new HashSet<ConverseTransition>();
		ctrans2.add(RPQExpressions.makeConverseTransition(q5, q3, ac));
		final NDFiniteAutomata comparator = RPQExpressions.makeNDFiniteAutomata(a, states2, alphabet2, q6, finStates2, trans2, ctrans2);
		
		assertEquals(result, comparator);
	}
	
	@Test
	public void testSimplifyCorrect2() {
		final Set<State> states = new HashSet<State>(Arrays.asList(q0,q1,q2,q3,q4,q5,q6,q7));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q7));
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a,b,d));
		Set<Transition> trans = new HashSet<Transition>();
		trans.add(RPQExpressions.makeTransition(q6, q5, epsilon));
		trans.add(RPQExpressions.makeTransition(q5, q0, epsilon));
		trans.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		trans.add(RPQExpressions.makeTransition(q1, q4, b));
		trans.add(RPQExpressions.makeTransition(q5, q3, epsilon));
		trans.add(RPQExpressions.makeTransition(q2, q4, d));
		trans.add(RPQExpressions.makeTransition(q4, q7, epsilon));
		Set<ConverseTransition> ctrans = new HashSet<ConverseTransition>();
		ctrans.add(RPQExpressions.makeConverseTransition(q3, q2, ac));
		final NDFiniteAutomata nfa = RPQExpressions.makeNDFiniteAutomata(a, states, alphabet, q6, finStates, trans, ctrans);
		final NDFiniteAutomata result = RpqConverterUtils.simplify(nfa);
		
		final Set<State> states2 = new HashSet<State>(Arrays.asList(q1,q2,q3,q4,q6,q7));
		final Set<State> finStates2 = new HashSet<State>(Arrays.asList(q7));
		final Set<EdgeLabel> alphabet2 = new HashSet<EdgeLabel>(Arrays.asList(a,b,d));
		Set<Transition> trans2 = new HashSet<Transition>();
		trans2.add(RPQExpressions.makeTransition(q6, q1, epsilon));
		trans2.add(RPQExpressions.makeTransition(q6, q3, epsilon));
		trans2.add(RPQExpressions.makeTransition(q1, q4, b));
		trans2.add(RPQExpressions.makeTransition(q2, q4, d));
		trans2.add(RPQExpressions.makeTransition(q4, q7, epsilon));
		Set<ConverseTransition> ctrans2 = new HashSet<ConverseTransition>();
		ctrans2.add(RPQExpressions.makeConverseTransition(q3, q2, ac));
		final NDFiniteAutomata comparator = RPQExpressions.makeNDFiniteAutomata(a, states2, alphabet2, q6, finStates2, trans2, ctrans2);
		
		assertEquals(result, comparator);
	}
}
