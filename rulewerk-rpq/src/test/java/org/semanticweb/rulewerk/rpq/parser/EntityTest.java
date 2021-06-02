package org.semanticweb.rulewerk.rpq.parser;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.model.implementation.LanguageStringConstantImpl;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;

public class EntityTest {
	final Variable x = Expressions.makeUniversalVariable("X");
	final Constant c = Expressions.makeAbstractConstant("c");
	final EdgeLabel p = RPQExpressions.makeEdgeLabel("p");
	
	@Test
	public void languageStringConstantToStringRoundTripTest() throws ParsingException {
		LanguageStringConstantImpl s = new LanguageStringConstantImpl("Test", "en");
		System.out.println(s.toString());
		final RegPathQuery rpq = RPQExpressions.makeRegPathQuery(p, x, c);
		final List<RegPathQuery> rpqList = Arrays.asList(rpq);
		final List<Term> projVars = Arrays.asList(x);
		final RPQConjunction<RegPathQuery> conjunction = RPQExpressions.makeRPQConjunction(rpqList, projVars);
		System.out.println(conjunction.toString());
		assertEquals(conjunction, RPQParser.parse(conjunction.toString()));
	}
}
