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
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.State;

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
		
		final Map<State,Map<EdgeLabel,List<State>>> transition = new HashMap<State,Map<EdgeLabel,List<State>>>();
		transition.put(q0, new HashMap<EdgeLabel,List<State>>() {{ put(empStr, new ArrayList<State>(Arrays.asList(q1,q2))); }});
		transition.put(q1, new HashMap<EdgeLabel,List<State>>() {{ put(empStr, new ArrayList<State>(Arrays.asList(q3))); }});
		transition.put(q2, new HashMap<EdgeLabel,List<State>>() {{ put(a, new ArrayList<State>(Arrays.asList(q4))); }});
		transition.put(q4, new HashMap<EdgeLabel,List<State>>() {{ put(b, new ArrayList<State>(Arrays.asList(q5))); }});
		
		final Map<State,Map<ConverseEdgeLabel,List<State>>> convTransition = new HashMap<State,Map<ConverseEdgeLabel,List<State>>>();
		
		final NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(abe, states, alphabet, q0, finStates, transition, convTransition);
		
		System.out.println(ndfa.isAuxiliary(q0));
		System.out.println(ndfa.isAuxiliary(q1));
		System.out.println(ndfa.isAuxiliary(q2));
		System.out.println(ndfa.isAuxiliary(q3));
		System.out.println(ndfa.isAuxiliary(q4));
		System.out.println(ndfa.isAuxiliary(q5));
		
	}
}
