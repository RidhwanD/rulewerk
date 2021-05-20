package org.semanticweb.rulewerk.rpq.model.implementation;

import org.semanticweb.rulewerk.rpq.model.api.State;

public class StateImpl implements State {
	private final String name;
	
	public StateImpl(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof State)) {
			return false;
		}
		final State other = (State) obj;

		return this.name.equals(other.getName());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
