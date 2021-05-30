package org.semanticweb.rulewerk.rpq.model.api;

import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Entity;

public interface NDFiniteAutomata extends Entity  {
	public State getInitState();
	public Set<State> getFinState();
	public Set<State> getState();
	public Set<EdgeLabel> getAlphabet();
	public Set<Transition> getTransition();
	public Set<ConverseTransition> getConvTransition();
	public RegExpression getRegex();
	public int hashCode();
}
