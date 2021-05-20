package org.semanticweb.rulewerk.rpq.model.implementation;

import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;

/**
 * Standard implementation of the {@link ConcatRegExpression} interface.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public class ConcatRegExpressionImpl implements ConcatRegExpression {
	private RegExpression exp1;
	private RegExpression exp2;
	
	public ConcatRegExpressionImpl(RegExpression exp1, RegExpression exp2) {
		this.exp1 = exp1;
		this.exp2 = exp2;
	}
	
	public RegExpression getExp1() {
		return this.exp1;
	}
	
	public RegExpression getExp2() {
		return this.exp2;
	}
	
	public void setExp1(RegExpression exp) {
		this.exp1 = exp;
	}
	
	public void setExp2(RegExpression exp) {
		this.exp2 = exp;
	}
	
	@Override
	public String getName() {
		return String.format("(%s / %s)", this.exp1.getName(), this.exp2.getName());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ConcatRegExpression)) {
			return false;
		}
		final ConcatRegExpression other = (ConcatRegExpression) obj;

		return this.exp1.equals(other.getExp1()) && this.exp2.equals(other.getExp2());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
