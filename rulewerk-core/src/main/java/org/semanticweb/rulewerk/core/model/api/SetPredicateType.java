package org.semanticweb.rulewerk.core.model.api;

public enum SetPredicateType {
	/**
	 * A set predicate to check if an object is an element of a set.
	 */
	IS_ELEMENT_OF,
	/**
	 * A set predicate to check if a set is a subset of another set.
	 */
	IS_SUBSET_OF,
	/**
	 * A usual normal predicate
	 */
	NORMAL
}
