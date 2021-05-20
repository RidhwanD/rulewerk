package org.semanticweb.rulewerk.rpq.model.api;

/**
 * Interface for {@link RegExpression#CONCATENATION} regular expression. A ConcatRegExpression is a combination 
 * of two regular expressions connected by concatenation.
 * 
 * @author Ridhwan Dewoprabowo
 *
 */
public interface ConcatRegExpression extends RegExpression {
	public RegExpression getExp1();
	public RegExpression getExp2();
	public void setExp1(RegExpression exp);
	public void setExp2(RegExpression exp);
	
	@Override
	default RegExpressionType getType() {
		return RegExpressionType.CONCATENATION;
	}
}

