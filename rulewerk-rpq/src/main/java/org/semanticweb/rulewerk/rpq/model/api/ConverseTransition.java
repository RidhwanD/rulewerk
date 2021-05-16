package org.semanticweb.rulewerk.rpq.model.api;

public interface ConverseTransition {
	public State getOrigin();
	public State getDest();
	public ConverseEdgeLabel getLabel();
}
