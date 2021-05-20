package org.semanticweb.rulewerk.rpq.model.api;

/**
 * Interface for {@link RegExpression#ALTERNATION} regular expression. A AlternRegExpression is a combination 
 * of two regular expressions connected by alternation.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public interface AlternRegExpression extends RegExpression {
	public RegExpression getExp1();
	public RegExpression getExp2();
	public void setExp1(RegExpression exp);
	public void setExp2(RegExpression exp);
	
	@Override
	default RegExpressionType getType() {
		return RegExpressionType.ALTERNATION;
	}
}
