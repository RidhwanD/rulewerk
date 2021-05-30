package org.semanticweb.rulewerk.rpq.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

/**
 * Standard implementation of the {@link ConverseEdgeLabel} interface.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public class ConverseEdgeLabelImpl implements ConverseEdgeLabel {
	private EdgeLabel converseOf;
	
	public ConverseEdgeLabelImpl(EdgeLabel label) {
		Validate.notNull(label);
		Validate.notBlank(label.getName());
		this.converseOf = label;
	}
	
	public EdgeLabel getConverseOf() {
		return converseOf;
	}
	
	@Override
	public String getName() {
		return String.format("(^%s)", this.converseOf.getName());
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConverseEdgeLabel)) {
			return false;
		}
		final ConverseEdgeLabel other = (ConverseEdgeLabel) obj;

		return this.converseOf.equals(other.getConverseOf());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
