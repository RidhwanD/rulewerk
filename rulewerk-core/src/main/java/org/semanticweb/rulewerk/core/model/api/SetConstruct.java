package org.semanticweb.rulewerk.core.model.api;

/**
* Interface for {@link TermType#SET_CONSTRUCT} terms.
*
* @author Ridhwan Dewoprabowo
*/
public interface SetConstruct extends SetTerm {

	@Override
	default TermType getType() {
		return TermType.SET_CONSTRUCT;
	}
		
	public Term getElement();
	public boolean isEmpty();
	
}
