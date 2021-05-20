package org.semanticweb.rulewerk.rpq.model.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;

public class NDFiniteAutomataImplTest {
	public static void main(String[] arg) {
		EdgeLabel empStr = RPQExpressions.makeEdgeLabel("");
		EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
		EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
		ConcatRegExpression ab = RPQExpressions.makeConcatRegExpression(a, b);
		AlternRegExpression abe = RPQExpressions.makeAlternRegExpression(ab, empStr);
		
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a,b,empStr));
		
		State q0 = RPQExpressions.makeState("q0");
		State q1 = RPQExpressions.makeState("q1");
		State q2 = RPQExpressions.makeState("q2");
		State q3 = RPQExpressions.makeState("q3");
		State q4 = RPQExpressions.makeState("q4");
		State q5 = RPQExpressions.makeState("q5");
		
		final Set<State> states = new HashSet<State>(Arrays.asList(q0,q1,q2,q3,q4,q5));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q3,q5));
		
		final Set<Transition> transition = new HashSet<Transition>();
		transition.add(RPQExpressions.makeTransition(q0, q1, empStr));
		transition.add(RPQExpressions.makeTransition(q0, q2, empStr));
		transition.add(RPQExpressions.makeTransition(q1, q3, empStr));
		transition.add(RPQExpressions.makeTransition(q2, q4, a));
		transition.add(RPQExpressions.makeTransition(q4, q5, b));
		
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		
		final NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
	}
}
