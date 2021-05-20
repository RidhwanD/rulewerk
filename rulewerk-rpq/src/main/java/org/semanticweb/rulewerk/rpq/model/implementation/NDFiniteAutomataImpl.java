package org.semanticweb.rulewerk.rpq.model.implementation;

import java.util.Set;

import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;

public class NDFiniteAutomataImpl implements NDFiniteAutomata {
	private RegExpression regex;
	private Set<State> states;
	private Set<EdgeLabel> alphabet;
	private State initState;
	private Set<State> finState;
	private Set<Transition> transition;
	private Set<ConverseTransition> convTransition;
	
	public NDFiniteAutomataImpl(RegExpression regex, Set<State> states, Set<EdgeLabel> alphabet, State initState, Set<State> finState, Set<Transition> transition, Set<ConverseTransition> convTransition) {
		this.regex = regex;
		this.states = states;
		this.alphabet = alphabet;
		if (states.contains(initState))
			this.initState = initState;
		else throw new IllegalArgumentException("Initial state must be contained in the set of states.");
		if (states.containsAll(finState))
			this.finState = finState;
		else throw new IllegalArgumentException("Final states must be contained in the set of states.");
		for (Transition tran : transition) {
			if (!states.contains(tran.getOrigin()) || !states.contains(tran.getDest())) throw new IllegalArgumentException("Transition must only involves states in the set of states: "+tran);
			if (!alphabet.contains(tran.getLabel()) && !tran.getLabel().getName().equals("")) throw new IllegalArgumentException("Transition must only involves EdgeLabel in the set of alphabet: "+tran);
		}
		this.transition = transition;
		for (ConverseTransition tran : convTransition) {
			if (!states.contains(tran.getOrigin()) || !states.contains(tran.getDest())) throw new IllegalArgumentException("Transition must only involves states in the set of states: "+tran);
			if (!alphabet.contains(tran.getLabel().getConverseOf())) throw new IllegalArgumentException("Transition must only involves EdgeLabel in the set of alphabet: "+tran);
		}
		this.convTransition = convTransition;
	}
	
	private <E> boolean compareSet(Set<E> s1, Set<E> s2) {
		return s1.size() == s2.size() && this.isContained(s1, s2) && this.isContained(s2, s1);
	}
	
	private <E> boolean isContained(Set<E> s1, Set<E> s2) {
		for (E o1 : s1) {
			boolean found = false;
			for (E o2 : s2) {
				if (o1.equals(o2))
					found = true;
			}
			if (!found) return false;
		}
		return true;
	}
	
	@Override
	public State getInitState() {
		return initState;
	}

	@Override
	public Set<State> getFinState() {
		return finState;
	}

	@Override
	public Set<State> getState() {
		return states;
	}

	@Override
	public Set<EdgeLabel> getAlphabet() {
		return alphabet;
	}

	@Override
	public Set<Transition> getTransition() {
		return transition;
	}

	@Override
	public Set<ConverseTransition> getConvTransition() {
		return convTransition;
	}

	@Override
	public RegExpression getRegex() {
		return regex;
	}

	@Override
	public boolean isAuxiliary(State s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTMFA() {
		// TODO Auto-generated method stub
		return false;
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
		return this.regex.equals(c.getRegex()) && this.initState.equals(c.getInitState()) && this.compareSet(this.alphabet, c.getAlphabet())
				&& this.compareSet(this.states, c.getState()) && this.compareSet(this.finState, c.getFinState())
        		&& this.compareSet(this.transition, c.getTransition()) &&  this.compareSet(this.convTransition, c.getConvTransition());        
	}
	
	@Override
	public int hashCode() {
		return this.transition.hashCode();
	}
	
	@Override
	public String toString() {
		return this.regex.toString();
	}
}
