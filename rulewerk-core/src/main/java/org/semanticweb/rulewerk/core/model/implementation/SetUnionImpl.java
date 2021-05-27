package org.semanticweb.rulewerk.core.model.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.SetTerm;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.TermVisitor;

public class SetUnionImpl implements SetUnion {

	private final String name;
	private final SetTerm setTerm1;
	private final SetTerm setTerm2;
	
	public SetUnionImpl(final SetTerm term1, final SetTerm term2) {
		this.name = term1 + " U " + term2;
		if (!isValidTerm(term1) || !isValidTerm(term2)) {
			throw new IllegalArgumentException(
					"Terms must be set terms. Terms were: " + term1 + " and " + term2);
		}
		this.setTerm1 = term1;
		this.setTerm2 = term2;
	}
	
	private boolean isValidTerm(SetTerm t) {
		return t.getType().equals(TermType.SET_VARIABLE) || t.getType().equals(TermType.SET_CONSTRUCT) || t.getType().equals(TermType.SET_UNION);
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public SetTerm getSetTerm1() {
		return this.setTerm1;
	}

	@Override
	public SetTerm getSetTerm2() {
		return this.setTerm2;
	}

	@Override
	public <T> T accept(TermVisitor<T> termVisitor) {
		return termVisitor.visit(this);
	}

	public List<SetVariable> getSetVariables() {
		List<SetVariable> setVars = new ArrayList<SetVariable>();
		if (this.setTerm1 instanceof SetVariable) 
			setVars.add((SetVariable) this.setTerm1);
		else if (this.setTerm1 instanceof SetUnion) {
			SetUnion t = (SetUnion) this.setTerm1;
			setVars.addAll(t.getSetVariables());
		}
		if (this.setTerm2 instanceof SetVariable) setVars.add((SetVariable) this.setTerm2);
		else if (this.setTerm2 instanceof SetUnion) {
			SetUnion t = (SetUnion) this.setTerm2;
			setVars.addAll(t.getSetVariables());
		}
		
		return setVars;
	}
	
	public boolean isSubTerm(SetTerm t) {
		if (this.setTerm1.equals(t) || this.setTerm2.equals(t)) return true;
		if (this.setTerm1 instanceof SetUnion) {
			SetUnion su = (SetUnion) this.setTerm1;
			if (su.isSubTerm(t)) return true;
		} 
		if (this.setTerm2 instanceof SetUnion) {
			SetUnion su = (SetUnion) this.setTerm2;
			if (su.isSubTerm(t)) return true;
		}
		return false;
	}
	
	public Set<SetTerm> getSubTerms() {
		Set<SetTerm> subTerms = new HashSet<SetTerm>(Arrays.asList(this.setTerm1, this.setTerm2));
		if (this.setTerm1 instanceof SetUnion) {
			SetUnion su = (SetUnion) this.setTerm1;
			subTerms.addAll(su.getSubTerms());
		} 
		if (this.setTerm2 instanceof SetUnion) {
			SetUnion su = (SetUnion) this.setTerm2;
			subTerms.addAll(su.getSubTerms());
		}
		return subTerms;
	}
	
	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeSetUnion(this));
	}

}
