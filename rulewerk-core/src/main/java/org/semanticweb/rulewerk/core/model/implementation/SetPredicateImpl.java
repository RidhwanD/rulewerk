package org.semanticweb.rulewerk.core.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.SetPredicate;
import org.semanticweb.rulewerk.core.model.api.SetPredicateType;

public class SetPredicateImpl extends PredicateImpl implements SetPredicate {

	final private SetPredicateType type;
	
	/**
	 * Constructor for {@link SetPredicate}s of arity 1 or higher.
	 *
	 * @param name  a non-blank String (not null, nor empty or whitespace).
	 * @param arity an int value strictly greater than 0.
	 * @param type 	a SetPredicateType.
	 */
	public SetPredicateImpl(final String name, final int arity, SetPredicateType type) {
		super(name, arity);
		Validate.notNull(type);
		if (type != SetPredicateType.NORMAL && type != SetPredicateType.IS_SUBSET_OF && type != SetPredicateType.IS_ELEMENT_OF)
			throw new IllegalArgumentException(
					"Invalid predicate type. Type was: " + type);
		if (type == SetPredicateType.IS_ELEMENT_OF || type == SetPredicateType.IS_SUBSET_OF) {
			if (arity != 2)
				throw new IllegalArgumentException(
						"Special predicate must have arity 2. Arity was: " + arity);
		}
		this.type = type;
	}
	

	@Override
	public SetPredicateType getPredicateType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SetPredicate)) {
			return false;
		}
		final SetPredicate other = (SetPredicate) obj;

		return super.getArity() == other.getArity() && super.getName().equals(other.getName()) && this.type.equals(other.getPredicateType());
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writePredicate(this));
	}
}
