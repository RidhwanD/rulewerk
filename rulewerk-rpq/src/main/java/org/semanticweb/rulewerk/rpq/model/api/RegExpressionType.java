package org.semanticweb.rulewerk.rpq.model.api;

/**
 * Enumeration listing the different types of regular expression.
 *
 */
public enum RegExpressionType {
	/**
	 * A concatenation (RS) denotes the set of strings that can be obtained by concatenating 
	 * a string accepted by R and a string accepted by S (in that order).
	 */
	CONCATENATION,
	/**
	 * An alteration (R|S) denotes the set union of sets described by R and S.
	 */
	ALTERNATION,
	/**
	 * A Kleene star (R*) denotes the smallest superset of the set described by R 
	 * that contains empty string and is closed under string concatenation. 
	 */
	KLEENE_STAR,
	/**
	 * A Kleene plus (R+) denotes the smallest superset of the set described by R 
	 * that contains R and is closed under string concatenation. 
	 */
	KLEENE_PLUS,
	/**
	 * An edge label is the alphabet for regular expression syntax.
	 */
	EDGE_LABEL,
	/**
	 * A converse edge label is the converse of an existing edge label (^-).
	 */
	CONVERSE_EDGE_LABEL
}

