package org.semanticweb.rulewerk.rpq.model.implementation;

import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;

public class TransitionImpl implements Transition {
	private State origin;
	private State destination;
	private EdgeLabel label;
	
	public TransitionImpl(State orig, State dest, EdgeLabel label) {
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
	public EdgeLabel getLabel() {
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
		if (!(obj instanceof EdgeLabel)) {
			return false;
		}
		final Transition other = (Transition) obj;

		return this.origin.equals(other.getOrigin()) && this.label.equals(other.getLabel()) && this.destination.equals(other.getDest());
	}
	
	@Override
	public String toString() {
		return "("+this.origin+", "+this.label+", "+this.destination+")";
	}
}
