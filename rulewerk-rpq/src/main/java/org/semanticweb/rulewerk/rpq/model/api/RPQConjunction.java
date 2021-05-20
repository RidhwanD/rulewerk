package org.semanticweb.rulewerk.rpq.model.api;

import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Entity;
import org.semanticweb.rulewerk.core.model.api.Term;

/**
 * Interface for representing conjunctions of {@link RegPathQuery}s, i.e., lists of
 * regular path queries that are connected with logical AND.
 * Conjunctions may have free variables, since they contain no quantifiers.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public interface RPQConjunction<T extends RegPathQuery> extends Iterable<T>, Entity {

	/**
	 * Returns the list of RPQs that are part of this conjunction.
	 * 
	 * @return list of RPQs
	 */
	List<T> getRPQs();
	List<Term> getProjVars();
}
