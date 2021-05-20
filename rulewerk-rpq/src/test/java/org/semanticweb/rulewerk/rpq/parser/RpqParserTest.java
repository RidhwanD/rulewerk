package org.semanticweb.rulewerk.rpq.parser;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

public class RpqParserTest {
	private final Variable x = Expressions.makeUniversalVariable("X");
	private final Variable y = Expressions.makeUniversalVariable("Y");
	private final Constant c = Expressions.makeAbstractConstant("http://example.org/c");
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
	
	// GMARK test RPQ
	private final Variable x0 = Expressions.makeUniversalVariable("x0");
	private final Variable x1 = Expressions.makeUniversalVariable("x1");
	private final Variable x2 = Expressions.makeUniversalVariable("x2");
	private final Variable x3 = Expressions.makeUniversalVariable("x3");
	
	private final EdgeLabel kw = RPQExpressions.makeEdgeLabel("http://example.org/gmark/pkeywords");
	private final EdgeLabel lk = RPQExpressions.makeEdgeLabel("http://example.org/gmark/plike");
	private final EdgeLabel at = RPQExpressions.makeEdgeLabel("http://example.org/gmark/partist");
	private final EdgeLabel tx = RPQExpressions.makeEdgeLabel("http://example.org/gmark/ptext");
	private final EdgeLabel hr = RPQExpressions.makeEdgeLabel("http://example.org/gmark/phasReview");
	private final EdgeLabel hg = RPQExpressions.makeEdgeLabel("http://example.org/gmark/phasGenre");
	private final EdgeLabel tp = RPQExpressions.makeEdgeLabel("http://example.org/gmark/ptype");
	private final EdgeLabel ct = RPQExpressions.makeEdgeLabel("http://example.org/gmark/pcaption");
	private final EdgeLabel tt = RPQExpressions.makeEdgeLabel("http://example.org/gmark/ptitle");
	
	private final ConverseEdgeLabel ckw = RPQExpressions.makeConverseEdgeLabel(kw);
	private final ConverseEdgeLabel clk = RPQExpressions.makeConverseEdgeLabel(lk);
	private final ConverseEdgeLabel cat = RPQExpressions.makeConverseEdgeLabel(at);
	private final ConverseEdgeLabel ctt = RPQExpressions.makeConverseEdgeLabel(tt);
	private final ConverseEdgeLabel chr = RPQExpressions.makeConverseEdgeLabel(hr);
	private final ConverseEdgeLabel ctx = RPQExpressions.makeConverseEdgeLabel(tx);
	private final ConverseEdgeLabel cct = RPQExpressions.makeConverseEdgeLabel(ct);
	
	private final ConcatRegExpression cr1 = RPQExpressions.makeConcatRegExpression(ckw, clk);
	private final ConcatRegExpression cr2 = RPQExpressions.makeConcatRegExpression(cr1, cat);
	private final ConcatRegExpression cr3 = RPQExpressions.makeConcatRegExpression(cr2, tx);

	private final ConcatRegExpression cr4 = RPQExpressions.makeConcatRegExpression(ctt, chr);
	private final ConcatRegExpression cr5 = RPQExpressions.makeConcatRegExpression(cr4, tx);
	
	private final AlternRegExpression ar1 = RPQExpressions.makeAlternRegExpression(cr3, cr5);
	private final RegPathQuery q1 = RPQExpressions.makeRegPathQuery(ar1, x0, x1);
	
	private final ConcatRegExpression cr6 = RPQExpressions.makeConcatRegExpression(ctx, hg);
	private final ConcatRegExpression cr7 = RPQExpressions.makeConcatRegExpression(cr6, tp);
	
	private final ConcatRegExpression cr8 = RPQExpressions.makeConcatRegExpression(cct, hg);
	private final ConcatRegExpression cr9 = RPQExpressions.makeConcatRegExpression(cr8, tp);
	
	private final ConcatRegExpression cr10 = RPQExpressions.makeConcatRegExpression(ctx, hg);
	private final ConcatRegExpression cr11 = RPQExpressions.makeConcatRegExpression(cr10, tp);
	
	private final AlternRegExpression ar2 = RPQExpressions.makeAlternRegExpression(cr7, cr9);
	private final AlternRegExpression ar3 = RPQExpressions.makeAlternRegExpression(ar2, cr11);
	private final RegPathQuery q2 = RPQExpressions.makeRegPathQuery(ar3, x0, x2);
	
	private final ConcatRegExpression cr12 = RPQExpressions.makeConcatRegExpression(ctt, hg);
	private final ConcatRegExpression cr13 = RPQExpressions.makeConcatRegExpression(cr12, tp);
	
	private final AlternRegExpression ar4 = RPQExpressions.makeAlternRegExpression(cr11, cr13);
	private final RegPathQuery q3 = RPQExpressions.makeRegPathQuery(ar4, x1, x3);
	
	private final RPQConjunction<RegPathQuery> rpqc = RPQExpressions.makeRPQConjunction(Arrays.asList(q1, q2, q3), Arrays.asList(x0));
	
	// The test methods.
	
	@Test
	public void testEdgeLabel() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . select ?X ?Y where {{ ?X ex:l ex:c . ex:c ex:m ?Y . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq1, statement);
	}
	
	@Test
	public void testEdgeLabel2() throws ParsingException {
		String input = "select distinct ?X where {{ ?X <indifferent> bobby . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq9, statement);
	}
	
	@Test
	public void testEdgeLabel3() throws ParsingException {
		String input = "ask {{ ?X <indifferent> bobby . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq11, statement);
	}
	
	@Test
	public void testConvEdgeLabel() throws ParsingException {
		String input = "select ?X where {{ ?X ( ^<http://example.org/l> ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq2, statement);
	}
	
	@Test
	public void testConvEdgeLabel2() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . select ?X where {{ ?X ( ^ex:l ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq2, statement);
	}

	@Test
	public void testAlternRegExp() throws ParsingException {
		String input = "select ?X where {{ ?X ( <http://example.org/m> | <http://example.org/n> ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq3, statement);
	}
	
	@Test
	public void testAlternRegExp2() throws ParsingException {
		String input = "@prefix ex: <http://example.org/> . select ?X where {{ ?X ( ex:m | <http://example.org/n> ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq3, statement);
	}

	@Test
	public void testConcatRegExp() throws ParsingException {
		String input = "select ?X where {{ ?X ( <http://example.org/o> / <http://example.org/p> ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq4, statement);
	}

	@Test
	public void testKStarRegExp() throws ParsingException {
		String input = "select ?X where {{ ?X ( <http://example.org/p>* ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq5, statement);
	}
	
	@Test
	public void testKPlusRegExp() throws ParsingException {
		String input = "select ?X where {{ ?X ( <http://example.org/p>+ ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq10, statement);
	}

	@Test
	public void testComplexConcRegExp() throws ParsingException {
		String input = "select ?X where {{ ?X ( ( <http://example.org/m> | <http://example.org/n> ) / (( ^<http://example.org/l> )* ) ) <http://example.org/c> . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq6, statement);
	}
	
	@Test
	public void testComplexAlternRegExp() throws ParsingException {
		String input = "select ?X where {{ ?X ( ( <http://example.org/o> / <http://example.org/p> ) | ( ( <http://example.org/o> / <http://example.org/p> )* ) ) <http://example.org/c> .}}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq7, statement);
	}
	
	@Test
	public void testComplexKStarRegExp() throws ParsingException {
		String input = "select ?X where {{ ?X ( ( ( <http://example.org/m> | <http://example.org/n> ) / ( (^<http://example.org/l>)* ) )* ) <http://example.org/c> .}}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpq8, statement);
	}
	
	@Test
	public void testGMarkQuery() throws ParsingException {
		String input = "@prefix g: <http://example.org/gmark/> . SELECT DISTINCT ?x0 WHERE {  {  ?x0 (((((^g:pkeywords)/(^g:plike))/(^g:partist))/g:ptext)|(((^g:ptitle)/(^g:phasReview))/g:ptext)) ?x1 . ?x0 (((((^g:ptext)/g:phasGenre)/g:ptype)|(((^g:pcaption)/g:phasGenre)/g:ptype))|(((^g:ptext)/g:phasGenre)/g:ptype)) ?x2 . ?x1 ((((^g:ptext)/g:phasGenre)/g:ptype)|(((^g:ptitle)/g:phasGenre)/g:ptype)) ?x3 . } }";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		assertEquals(rpqc, statement);
	}
}
