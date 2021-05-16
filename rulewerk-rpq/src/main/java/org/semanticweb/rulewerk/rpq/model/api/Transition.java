package org.semanticweb.rulewerk.rpq.model.api;

public interface Transition {
	public State getOrigin();
	public State getDest();
	public EdgeLabel getLabel();
}
