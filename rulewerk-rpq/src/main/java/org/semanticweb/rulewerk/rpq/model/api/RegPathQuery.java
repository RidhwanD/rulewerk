package org.semanticweb.rulewerk.rpq.model.api;

import org.semanticweb.rulewerk.core.model.api.Entity;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Interface for {@link RegPathQuery} Regular Path Query (RPQ). An RPQ is a query that uses Regular Expression
 * to match the pattern on a graph database.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public interface RegPathQuery extends Entity {
	public RegExpression getExpression();
	public Term getTerm1();
	public Term getTerm2();
}
