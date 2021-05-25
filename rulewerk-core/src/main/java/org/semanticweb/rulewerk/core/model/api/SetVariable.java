package org.semanticweb.rulewerk.core.model.api;

/**
 * Interface for set variables, i.e., variables that can only
 * be instantiated by set terms.
 *
 * @author Ridhwan Dewoprabowo
 */
public interface SetVariable extends SetTerm {
	
	@Override
	default TermType getType() {
		return TermType.SET_VARIABLE;
	}
	
}
