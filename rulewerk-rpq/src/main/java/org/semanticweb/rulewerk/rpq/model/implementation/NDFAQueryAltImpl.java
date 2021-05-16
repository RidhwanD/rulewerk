package org.semanticweb.rulewerk.rpq.model.implementation;

import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.rpq.model.api.NDFAQuery;
import org.semanticweb.rulewerk.rpq.model.api.NDFAQueryAlt;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomataAlt;

public class NDFAQueryAltImpl implements NDFAQueryAlt {
	private NDFiniteAutomataAlt ndfa;
	private Term t1;
	private Term t2;
	
	public NDFAQueryAltImpl(NDFiniteAutomataAlt ndfa, Term t1, Term t2) {
		this.ndfa = ndfa;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public Term getTerm1() {
		return t1;
	}
	
	public Term getTerm2() {
		return t2;
	}
	
	public NDFiniteAutomataAlt getNDFA() {
		return ndfa;
	}
	
	@Override
	public String toString() {
		return t1 + " " + ndfa.getRegex() + " " + t2 +" .";
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
