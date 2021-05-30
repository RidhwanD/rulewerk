package org.semanticweb.rulewerk.rpq.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.rpq.model.api.NDFAQuery;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;

public class NDFAQueryImpl implements NDFAQuery {
	private NDFiniteAutomata ndfa;
	private Term t1;
	private Term t2;
	
	public NDFAQueryImpl(NDFiniteAutomata ndfa, Term t1, Term t2) {
		Validate.notNull(ndfa);
		Validate.notNull(t1);
		Validate.notNull(t2);
		this.ndfa = ndfa;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public Term getTerm1() {
		return this.t1;
	}
	
	public Term getTerm2() {
		return this.t2;
	}
	
	public NDFiniteAutomata getNDFA() {
		return this.ndfa;
	}
	
	@Override
	public String toString() {
		return t1 + " " + this.ndfa + " " + t2 +" .";
	}
	
	@Override
	public boolean equals(Object obj) { 
		if (obj == this) { 
			return true; 
		} 
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NDFAQuery)) { 
            return false; 
        } 
          
		NDFAQuery c = (NDFAQuery) obj;
        return this.ndfa.equals(c.getNDFA()) && this.t1.equals(c.getTerm1()) && this.t2.equals(c.getTerm2());        
	}
}
