package org.semanticweb.rulewerk.rpq.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;

/**
 * Standard implementation of the {@link EdgeLabel} interface.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public class EdgeLabelImpl implements EdgeLabel {
	private String edgeName;
	
	public EdgeLabelImpl(String edgeName) {
		Validate.notNull(edgeName);
		this.edgeName = edgeName;
	}
	
	@Override
	public String getName() {
		return this.edgeName;
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
		final EdgeLabel other = (EdgeLabel) obj;

		return this.edgeName.equals(other.getName());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
