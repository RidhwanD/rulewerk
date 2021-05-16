package org.semanticweb.rulewerk.rpq.model.api;

/**
 * Interface for {@link RegExpression#CONVERSE_EDGE_LABEL} regular expression. It represents the converse 
 * of an {@link EdgeLabel}.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public interface ConverseEdgeLabel extends RegExpression {
	
	public EdgeLabel getConverseOf();
	
	@Override
	default RegExpressionType getType() {
		return RegExpressionType.CONVERSE_EDGE_LABEL;
	}
}
