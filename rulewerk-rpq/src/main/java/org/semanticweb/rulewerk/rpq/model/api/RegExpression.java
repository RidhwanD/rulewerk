package org.semanticweb.rulewerk.rpq.model.api;

import org.semanticweb.rulewerk.core.model.api.Entity;

/**
 * Interface for {@link RegExpression} regular expression.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public interface RegExpression extends Entity {
	String getName();
	RegExpressionType getType();
}

