package org.semanticweb.rulewerk.rpq.model.implementation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		Validate.noNullElements(vars);
		Set<Term> varSet = new HashSet<>(vars);
		if (varSet.size() != vars.size())
			throw new IllegalArgumentException(
					"Multiple occurences of projected variable(s). Projected Variables was: " + vars);
		this.rpqs = rpqs;
		this.projVars = vars;
	}
	
	private <E> boolean compareList(List<E> rpqs2, List<E> list) {
		return rpqs2.size() == list.size() && this.isContained(rpqs2, list) && this.isContained(list, rpqs2);
	}
	
	private <E> boolean isContained(List<E> s1, List<E> s2) {
		for (E o1 : s1) {
			boolean found = false;
			for (E o2 : s2) {
				if (o1.equals(o2))
					found = true;
			}
			if (!found) return false;
		}
		return true;
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
		return this.rpqs.equals(other.getRPQs()) && compareList(this.projVars, other.getProjVars());
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
