package org.semanticweb.rulewerk.core.model.implementation;

import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.TermVisitor;

/**
 * Simple implementation of {@link SetVariable}.
 *
 * @author Ridhwan Dewoprabowo
 */
public class SetVariableImpl extends AbstractTermImpl implements SetVariable {

	/**
	 * Constructor.
	 *
	 * @param name cannot be a blank String (null, empty or whitespace).
	 */
	public SetVariableImpl(final String name) {
		super(name);
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeSetVariable(this));
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}
}
