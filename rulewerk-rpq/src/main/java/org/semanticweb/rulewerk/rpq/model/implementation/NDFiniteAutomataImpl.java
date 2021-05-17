package org.semanticweb.rulewerk.rpq.model.implementation;

import java.util.Set;

import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.State;

import java.util.Map;
import java.util.List;

public class NDFiniteAutomataImpl implements NDFiniteAutomata{
	private RegExpression regex;
	private Set<State> states;
	private Set<EdgeLabel> alphabet;
	private State initState;
	private Set<State> finState;
	private Map<State,Map<EdgeLabel,List<State>>> transition;
	private Map<State,Map<ConverseEdgeLabel,List<State>>> convTransition;
	private int e = 0;
	
	public NDFiniteAutomataImpl(RegExpression regex, Set<State> states, Set<EdgeLabel> alphabet, State initState, Set<State> finState, Map<State,Map<EdgeLabel,List<State>>> transition, Map<State,Map<ConverseEdgeLabel,List<State>>> convTransition) {
		this.regex = regex;
		this.states = states;
		this.alphabet = alphabet;
		if (states.contains(initState))
			this.initState = initState;
		else throw new IllegalArgumentException("Initial state must be contained in the set of states.");
		if (states.containsAll(finState))
			this.finState = finState;
		else throw new IllegalArgumentException("Final states must be contained in the set of states.");
		for (State key : transition.keySet()) {
			if (!states.contains(key)) throw new IllegalArgumentException("Transition must only involves states in the set of states.");
			for (EdgeLabel el : transition.get(key).keySet()) {
				if (!alphabet.contains(el)) throw new IllegalArgumentException("Transition must only involves EdgeLabel in the set of alphabet.");
				if (!states.containsAll(transition.get(key).get(el))) throw new IllegalArgumentException("Transition must only involves states in the set of states.");
				int leavingTrans = transition.get(key).get(el).size();
				if (transition.get(key).get(el).size() > this.e) this.e = leavingTrans;
			}
		}
		this.transition = transition;
		for (State key : convTransition.keySet()) {
			if (!states.contains(key)) throw new IllegalArgumentException("Transition must only involves states in the set of states.");
			for (ConverseEdgeLabel el : convTransition.get(key).keySet()) {
				if (!alphabet.contains(el.getConverseOf())) throw new IllegalArgumentException("Transition must only involves EdgeLabel in the set of alphabet.");
				if (!states.containsAll(convTransition.get(key).get(el))) throw new IllegalArgumentException("Transition must only involves states in the set of states.");
				int leavingTrans = convTransition.get(key).get(el).size();
				if (convTransition.get(key).get(el).size() > this.e) this.e = leavingTrans;
			}
		}
		this.convTransition = convTransition;
	}
	
	public State getInitState() {
		return this.initState;
	}
	
	public Set<State> getFinState() {
		return this.finState;
	}
	
	public Map<State,Map<EdgeLabel,List<State>>> getTransition() {
		return this.transition;
	}
	
	public Map<State,Map<ConverseEdgeLabel,List<State>>> getConvTransition() {
		return this.convTransition;
	}
	
	public RegExpression getRegex() {
		return this.regex;
	}

	@Override
	public int hashCode() {
		return this.transition.hashCode();
	}
	
	@Override
	public String toString() {
		return this.regex.toString();
	}
	
	@Override
	public boolean equals(Object obj) { 
		if (obj == this) { 
			return true; 
		} 
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NDFiniteAutomata)) { 
            return false; 
        } 
          
		NDFiniteAutomata c = (NDFiniteAutomata) obj;
        return this.regex.equals(c.getRegex()) && this.initState.equals(c.getInitState()) && this.finState.equals(c.getFinState())
        		&& this.transition.equals(c.getTransition()) && this.convTransition.equals(c.getConvTransition());        
	}

	@Override
	public Set<State> getState() {
		return this.states;
	}

	@Override
	public Set<EdgeLabel> getAlphabet() {
		return this.alphabet;
	}
	
	public boolean isAuxiliary(State s) {
		boolean aux = true;
		
		EdgeLabel emptyString = RPQExpressions.makeEdgeLabel("");
		Map<EdgeLabel,List<State>> transTab = transition.get(s);
		Set<EdgeLabel> t1 = null;
		if (transTab != null)
			t1 = transTab.keySet();
		Set<ConverseEdgeLabel> t2 = null;
		Map<ConverseEdgeLabel,List<State>> ctransTab = null;
		if (convTransition != null)
			ctransTab = convTransition.get(s);
		if (ctransTab != null)
			t2 = ctransTab.keySet();
		if (t2 != null) aux = false;
		if (t1 != null) {
			if (t1.size() > 1) aux = false;
			else if (t1.size() == 1) {
				for (EdgeLabel e : t1) {
					if (!e.equals(emptyString)) aux = false;
				}
			}
		}
		for (State e : transition.keySet()) {
			for (EdgeLabel l : transition.get(e).keySet()) {
				if (!l.equals(emptyString) && transition.get(e).get(l).contains(s)) aux = false;
			}
		}
		if (convTransition != null) {
			for (State e : convTransition.keySet()) {
				for (ConverseEdgeLabel l : convTransition.get(e).keySet()) {
					if (convTransition.get(e).get(l).contains(s)) aux = false;
				}
			}
		}
		return aux;
	}
	
	public boolean isTMFA() {
		return this.e <= 2;
	}
}
