package org.semanticweb.rulewerk.core.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.SetConstruct;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.TermVisitor;

public class SetConstructImpl implements SetConstruct {
	
	private final String name;
	private final Term element;
	private final boolean isEmpty;
	
	public SetConstructImpl(final Term element) {
		Validate.notNull(element);
		this.name = "{" + element.getName() + "}";
		if (!isValidElement(element)) {
			throw new IllegalArgumentException(
					"Element must be an object term. Object was: " + element);
		}
		this.element = element;
		this.isEmpty = false;
	}
	
	public SetConstructImpl() {
		this.name = "{}";
		this.element = null;
		this.isEmpty = true;
	}
	
	private boolean isValidElement(final Term element) {
		return element.getType().equals(TermType.ABSTRACT_CONSTANT) || element.getType().equals(TermType.UNIVERSAL_VARIABLE);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}
	
	@Override
	public Term getElement() {
		return this.element;
	}
	
	@Override
	public boolean isEmpty() {
		return this.isEmpty;
	}
	
	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeSetConstruct(this));
	}
	
	public int hashCode() {
		int prime = 31;
		if (!this.isEmpty)
			return prime + this.element.hashCode();
		else return prime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SetConstruct)) {
			return false;
		}
		final SetConstruct other = (SetConstruct) obj;
		if ((this.isEmpty && !other.isEmpty()) || (!this.isEmpty && other.isEmpty())) {
			return false;
		}
		
		return this.element.equals(other.getElement());
	}
}
