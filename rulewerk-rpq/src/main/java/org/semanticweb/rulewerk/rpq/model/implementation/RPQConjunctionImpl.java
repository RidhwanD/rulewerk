package org.semanticweb.rulewerk.rpq.model.implementation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

/**
 * Standard implementation of the {@link RPQConjunction} interface.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public class RPQConjunctionImpl <T extends RegPathQuery> implements RPQConjunction<T> {

	final List<? extends T> rpqs;
	final List<Term> projVars;
	
	public RPQConjunctionImpl(List<? extends T> rpqs, List<Term> vars) {
		Validate.noNullElements(rpqs);
		this.rpqs = rpqs;
		this.projVars = vars;
	}
	
	@Override
	public Iterator<T> iterator() {
		return getRPQs().iterator();
	}
	
	@Override
	public List<T> getRPQs() {
		return Collections.unmodifiableList(this.rpqs);
	}

	@Override
	public int hashCode() {
		return this.rpqs.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RPQConjunction<?>)) {
			return false;
		}
		final RPQConjunction<?> other = (RPQConjunction<?>) obj;
		return this.rpqs.equals(other.getRPQs()) && this.projVars.equals(other.getProjVars());
	}
	
	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeRPQConjunction(this));
	}

	@Override
	public List<Term> getProjVars() {
		return projVars;
	}
}
