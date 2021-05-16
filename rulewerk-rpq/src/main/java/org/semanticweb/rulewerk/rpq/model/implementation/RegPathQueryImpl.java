package org.semanticweb.rulewerk.rpq.model.implementation;

import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

/**
 * Standard implementation of the {@link RegPathQuery} interface.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public class RegPathQueryImpl implements RegPathQuery{
	private RegExpression regex;
	private Term t1;
	private Term t2;
	
	public RegPathQueryImpl(RegExpression regex, Term t1, Term t2) {
		this.regex = regex;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public Term getTerm1() {
		return t1;
	}
	
	public Term getTerm2() {
		return t2;
	}
	
	public RegExpression getExpression() {
		return regex;
	}
	
	@Override
	public String toString() {
		return t1 + " " + regex + " " + t2 +" .";
	}
	
	@Override
	public boolean equals(Object obj) { 
		if (obj == this) { 
			return true; 
		} 
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RegPathQuery)) { 
            return false; 
        } 
          
		RegPathQuery c = (RegPathQuery) obj;
        return this.regex.equals(c.getExpression()) && this.t1.equals(c.getTerm1()) && this.t2.equals(c.getTerm2());        
	}
}
