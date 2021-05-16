package org.semanticweb.rulewerk.rpq.model.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Entity;

public interface NDFiniteAutomata extends Entity {
	public State getInitState();
	public Set<State> getFinState();
	public Set<State> getState();
	public Set<EdgeLabel> getAlphabet();
	public Map<State,Map<EdgeLabel,List<State>>> getTransition();
	public int hashCode();
	public Map<State,Map<ConverseEdgeLabel,List<State>>> getConvTransition();
	public RegExpression getRegex();
	public boolean isAuxiliary(State s);
	public boolean isTMFA();
}
