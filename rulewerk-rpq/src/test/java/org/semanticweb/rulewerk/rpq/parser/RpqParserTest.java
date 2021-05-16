package org.semanticweb.rulewerk.rpq.parser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;
import org.semanticweb.rulewerk.rpq.parser.ParsingException;
import org.semanticweb.rulewerk.rpq.parser.RPQParser;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

public class RpqParserTest {
	private final Variable x = Expressions.makeUniversalVariable("X");
	private final Variable y = Expressions.makeUniversalVariable("Y");
	private final Variable z = Expressions.makeUniversalVariable("Z");
	private final Constant c = Expressions.makeAbstractConstant("http://example.org/c");
	private final Constant d = Expressions.makeAbstractConstant("http://example.org/d");
	private final Constant e = Expressions.makeAbstractConstant("https://example.org/e");
	private final Constant bb = Expressions.makeAbstractConstant("bobby");
	private final EdgeLabel l1 = RPQExpressions.makeEdgeLabel("http://example.org/l");
	private final EdgeLabel l2 = RPQExpressions.makeEdgeLabel("http://example.org/m");
	private final EdgeLabel l3 = RPQExpressions.makeEdgeLabel("http://example.org/n");
	private final EdgeLabel l4 = RPQExpressions.makeEdgeLabel("http://example.org/o");
	private final EdgeLabel l5 = RPQExpressions.makeEdgeLabel("http://example.org/p");
	private final EdgeLabel l6 = RPQExpressions.makeEdgeLabel("indifferent");
	private final ConverseEdgeLabel c1 = RPQExpressions.makeConverseEdgeLabel(l1);
	private final AlternRegExpression al1 = RPQExpressions.makeAlternRegExpression(l2, l3);
	private final ConcatRegExpression co1 = RPQExpressions.makeConcatRegExpression(l4, l5);
	private final KStarRegExpression ks1 = RPQExpressions.makeKStarRegExpression(l5);
	private final KStarRegExpression ks2 = RPQExpressions.makeKStarRegExpression(c1);
	private final KStarRegExpression ks3 = RPQExpressions.makeKStarRegExpression(co1);
	private final ConcatRegExpression co2 = RPQExpressions.makeConcatRegExpression(al1, ks2);
	private final AlternRegExpression al2 = RPQExpressions.makeAlternRegExpression(co1, ks3);
	private final KStarRegExpression ks4 = RPQExpressions.makeKStarRegExpression(co2);
	private final KPlusRegExpression kp1 = RPQExpressions.makeKPlusRegExpression(l5);
	private final RPQConjunction<RegPathQuery> rpq1 = RPQExpressions.makeRPQConjunction(Arrays.asList(
			RPQExpressions.makeRegPathQuery(l1, x, c), RPQExpressions.makeRegPathQuery(l2, c, y)), 
			Arrays.asList(x, y));
	private final RPQConjunction<RegPathQuery> rpq2 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(c1, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq3 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(al1, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq4 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(co1, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq5 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(ks1, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq6 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(co2, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq7 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(al2, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq8 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(ks4, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq9 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(l6, x, bb)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq10 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(kp1, x, c)), Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> rpq11 = RPQExpressions.makeRPQConjunction(Arrays.asList(RPQExpressions.makeRegPathQuery(l6, x, bb)), Arrays.asList());
	
//	@Test
//	public void testEdgeLabel() throws ParsingException {
//		String input = "@prefix ex: <http://example.org/> . select ?X ?Y where {{ ?X ex:l ex:c . ex:c ex:m ?Y . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq1);
//		assertEquals(rpq1, statement);
//	}
//	
//	@Test
//	public void testEdgeLabel2() throws ParsingException {
//		String input = "select distinct ?X where {{ ?X <indifferent> bobby . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq9);
//		assertEquals(rpq9, statement);
//	}
//	
//	@Test
//	public void testEdgeLabel3() throws ParsingException {
//		String input = "ask {{ ?X <indifferent> bobby . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq11);
//		assertEquals(rpq11, statement);
//	}
//	
//	@Test
//	public void testConvEdgeLabel() throws ParsingException {
//		String input = "select ?X where {{ ?X ( ^<http://example.org/l> ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq2);
//		assertEquals(rpq2, statement);
//	}
//	
//	@Test
//	public void testConvEdgeLabel2() throws ParsingException {
//		String input = "@prefix ex: <http://example.org/> . select ?X where {{ ?X ( ^ex:l ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq2);
//		assertEquals(rpq2, statement);
//	}
//
//	@Test
//	public void testAlternRegExp() throws ParsingException {
//		String input = "select ?X where {{ ?X ( <http://example.org/m> | <http://example.org/n> ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq3);
//		assertEquals(rpq3, statement);
//	}
//	
//	@Test
//	public void testAlternRegExp2() throws ParsingException {
//		String input = "@prefix ex: <http://example.org/> . select ?X where {{ ?X ( ex:m | <http://example.org/n> ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq3);
//		assertEquals(rpq3, statement);
//	}
//
//	@Test
//	public void testConcatRegExp() throws ParsingException {
//		String input = "select ?X where {{ ?X ( <http://example.org/o> / <http://example.org/p> ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq4);
//		assertEquals(rpq4, statement);
//	}
//
//	@Test
//	public void testKStarRegExp() throws ParsingException {
//		String input = "select ?X where {{ ?X ( <http://example.org/p>* ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq5);
//		assertEquals(rpq5, statement);
//	}
//	
//	@Test
//	public void testKPlusRegExp() throws ParsingException {
//		String input = "select ?X where {{ ?X ( <http://example.org/p>+ ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq10);
//		assertEquals(rpq10, statement);
//	}
//
//	@Test
//	public void testComplexConcRegExp() throws ParsingException {
//		String input = "select ?X where {{ ?X ( ( <http://example.org/m> | <http://example.org/n> ) / (( ^<http://example.org/l> )* ) ) <http://example.org/c> . }}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq6);
//		assertEquals(rpq6, statement);
//	}
//	
//	@Test
//	public void testComplexAlternRegExp() throws ParsingException {
//		String input = "select ?X where {{ ?X ( ( <http://example.org/o> / <http://example.org/p> ) | ( ( <http://example.org/o> / <http://example.org/p> )* ) ) <http://example.org/c> .}}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq7);
//		assertEquals(rpq7, statement);
//	}
//	
//	@Test
//	public void testComplexKStarRegExp() throws ParsingException {
//		String input = "select ?X where {{ ?X ( ( ( <http://example.org/m> | <http://example.org/n> ) / ( (^<http://example.org/l>)* ) )* ) <http://example.org/c> .}}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(rpq8);
//		assertEquals(rpq8, statement);
//	}
	
	@Test
	public void testNumLiteral() throws ParsingException {
		String input = "ask {{ ?X <indifferent> 12 . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		System.out.println(statement);
	}
	
//	@Test
//	public void testGMark() throws ParsingException {
//		String input = "@PREFIX g: <http://example.org/gmark/> . SELECT DISTINCT ?x0 WHERE { " + 
//				"	{  	?x0 (((g:p75 / g:p60) / (^g:p60)) / (^g:p75)) ?x1 . " + 
//				"		?x1 (((((g:p30/(^g:p23))/g:p5)/(^g:p19))|(g:p27/(^g:p1)))|(((g:p31/(^g:p1))/(^g:p79))/g:p79)) ?x2 . " + 
//				"		?x0 (((((g:p33/(^g:p15))/g:p15)/(^g:p3))|(g:p27/(^g:p43)))|(g:p30/(^g:p44))) ?x3 . " + 
//				"		?x2 (((((g:p29/(^g:p2))/g:p2)/(^g:p3))|(g:p27/(^g:p43)))|(((g:p29/(^g:p50))/g:p71)/(^g:p80))) ?x4 . " + 
//				"	} " + 
//				"}";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//	}
	
//	@Test
//	public void testGMarkQ() throws ParsingException {
//		String input = "@prefix g: <http://example.org/gmark/> . SELECT DISTINCT ?x0 WHERE {  {  ?x0 (((((^g:pkeywords)/(^g:plike))/(^g:partist))/g:ptext)|(((^g:ptitle)/(^g:phasReview))/g:ptext)) ?x1 . ?x0 (((((^g:ptext)/g:phasGenre)/g:ptype)|(((^g:pcaption)/g:phasGenre)/g:ptype))|(((^g:ptext)/g:phasGenre)/g:ptype)) ?x2 . ?x1 ((((^g:ptext)/g:phasGenre)/g:ptype)|(((^g:ptitle)/g:phasGenre)/g:ptype)) ?x3 . } }";
//		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//		System.out.println(statement);
//		System.out.println(statement.getRPQs().size());
//		System.out.println(statement.getProjVars());
//	}
}
