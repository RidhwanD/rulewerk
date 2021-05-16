package org.semanticweb.rulewerk.rpq.model.api;

/**
 * Interface for {@link RegExpression#KLEENE_STAR} regular expression. A KStarRegExpression is a 
 * regular expression with Kleene star constructor.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public interface KPlusRegExpression extends RegExpression {
	public RegExpression getExp();
	public void setExp(RegExpression exp);
	
	@Override
	default RegExpressionType getType() {
		return RegExpressionType.KLEENE_PLUS;
	}
}
