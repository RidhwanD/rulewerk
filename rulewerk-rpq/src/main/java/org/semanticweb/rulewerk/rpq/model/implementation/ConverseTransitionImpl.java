package org.semanticweb.rulewerk.rpq.model.implementation;

import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.State;

public class ConverseTransitionImpl implements ConverseTransition {
	private State origin;
	private State destination;
	private ConverseEdgeLabel label;
	
	public ConverseTransitionImpl(State orig, State dest, ConverseEdgeLabel label) {
		this.origin = orig;
		this.destination = dest;
		this.label = label;
	}
	
	@Override
	public State getOrigin() {
		return origin;
	}

	@Override
	public State getDest() {
		return destination;
	}

	@Override
	public ConverseEdgeLabel getLabel() {
		return label;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConverseTransition)) {
			return false;
		}
		final ConverseTransition other = (ConverseTransition) obj;

		return this.origin.equals(other.getOrigin()) && this.label.equals(other.getLabel()) && this.destination.equals(other.getDest());
	}
	
	@Override
	public String toString() {
		return "("+this.origin+", "+this.label+", "+this.destination+")";
	}
}
