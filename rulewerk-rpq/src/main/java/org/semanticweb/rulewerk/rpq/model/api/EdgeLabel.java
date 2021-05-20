package org.semanticweb.rulewerk.rpq.model.api;

/**
 * Interface for {@link RegExpression#EDGE_LABEL} regular expression. A edge label is used to represent
 * the base of regular path query. It represents the label of edge in graph databases.
 *
 * @author Ridhwan Dewoprabowo
 * 
 */
public interface EdgeLabel extends RegExpression {
	
	@Override
	default RegExpressionType getType() {
		return RegExpressionType.EDGE_LABEL;
	}

}
