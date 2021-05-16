package org.semanticweb.rulewerk.rpq.model.implementation;

import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;

public class KPlusRegExpressionImpl implements KPlusRegExpression {
private RegExpression exp;
	
	public KPlusRegExpressionImpl(RegExpression exp) {
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
		return String.format("(%s)+", this.exp.getName());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof KPlusRegExpression)) {
			return false;
		}
		final KPlusRegExpression other = (KPlusRegExpression) obj;

		return this.exp.equals(other.getExp());
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
