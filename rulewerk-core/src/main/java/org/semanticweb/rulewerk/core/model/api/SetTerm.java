package org.semanticweb.rulewerk.core.model.api;

public interface SetTerm extends Term {
	/**
	 * Return the type of this term.
	 *
	 * @return the type of this term
	 */
	TermType getType();
	
	/**
	 * Returns true if the term represents some kind of set construct.
	 *
	 * @return true if term is a set construct or a union of two sets.
	 */
	default boolean isSetConstruct() {
		return this.getType() == TermType.SET_CONSTRUCT || this.getType() == TermType.SET_UNION;
	}

	/**
	 * Returns true if the term represents some kind of variable.
	 *
	 * @return true if term is a set variable
	 */
	default boolean isVariable() {
		return this.getType() == TermType.SET_VARIABLE;
	}

	/**
	 * Accept a {@link TermVisitor} and return its output.
	 *
	 * @param termVisitor the TermVisitor
	 * @return output of the visitor
	 */
	<T> T accept(TermVisitor<T> termVisitor);
	
}
