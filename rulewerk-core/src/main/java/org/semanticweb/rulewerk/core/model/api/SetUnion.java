package org.semanticweb.rulewerk.core.model.api;

import java.util.List;
import java.util.Set;

/**
* Interface for {@link TermType#SET_UNION} terms.
*
* @author Ridhwan Dewoprabowo
*/
public interface SetUnion extends SetTerm {

	@Override
	default TermType getType() {
		return TermType.SET_UNION;
	}
	
	public Term getSetTerm1();
	public Term getSetTerm2();
	public List<SetVariable> getSetVariables();
	public boolean isSubTerm(SetTerm t);
	public Set<Term> getSubTerms();
	
}
