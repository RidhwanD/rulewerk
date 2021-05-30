package org.semanticweb.rulewerk.rpq.model.implementation;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;

/**
 * Standard implementation of the {@link KStarRegExpression} interface.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public class KStarRegExpressionImpl implements KStarRegExpression {
	private RegExpression exp;
	
	public KStarRegExpressionImpl(RegExpression exp) {
		Validate.notNull(exp);
		this.exp = exp;
	}
	
	public RegExpression getExp() {
		return this.exp;
	}
	
	public void setExp(RegExpression exp) {
		this.exp = exp;
	}
	
	@Override
	public String getName() {
		return String.format("(%s*)", this.exp.getName());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof KStarRegExpression)) {
			return false;
		}
		final KStarRegExpression other = (KStarRegExpression) obj;

		return this.exp.equals(other.getExp());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
