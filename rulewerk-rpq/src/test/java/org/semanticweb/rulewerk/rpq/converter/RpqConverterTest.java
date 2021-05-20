package org.semanticweb.rulewerk.rpq.converter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
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

public class RpqConverterTest {
	private final UniversalVariable x = Expressions.makeUniversalVariable("X");
	private final UniversalVariable x1 = Expressions.makeUniversalVariable("x1");
	private final UniversalVariable x2 = Expressions.makeUniversalVariable("x2");
	private final UniversalVariable xs = Expressions.makeUniversalVariable("x");
	private final UniversalVariable y = Expressions.makeUniversalVariable("Y");
	private final UniversalVariable ys = Expressions.makeUniversalVariable("y");
	private final ExistentialVariable z = Expressions.makeExistentialVariable("Z");
	private final UniversalVariable zu = Expressions.makeUniversalVariable("Z");
	private final UniversalVariable zs = Expressions.makeUniversalVariable("z");
	private final Constant c = Expressions.makeAbstractConstant("C");
	private final Constant const2 = Expressions.makeAbstractConstant("2");
	private final Predicate triple = Expressions.makePredicate("TRIPLE", 3);
	private final Predicate cek = Expressions.makePredicate("Check", 1);
	/////////////////////////////////////////////////
	
	private final EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
	private final Predicate qa = Expressions.makePredicate("Q_a", 2);
	private final Constant ca = Expressions.makeAbstractConstant("a");
	private final EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
	private final Predicate qb = Expressions.makePredicate("Q_b", 2);
	private final Constant cb = Expressions.makeAbstractConstant("b");
	private final EdgeLabel d = RPQExpressions.makeEdgeLabel("d");
	private final Predicate qd = Expressions.makePredicate("Q_d", 2);
	private final Constant cd = Expressions.makeAbstractConstant("d");
	private final ConverseEdgeLabel conv_a = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("a"));
	private final AlternRegExpression dab = RPQExpressions.makeAlternRegExpression(d, b);
	private final KStarRegExpression ks = RPQExpressions.makeKStarRegExpression(a);
	private final ConcatRegExpression acd = RPQExpressions.makeConcatRegExpression(a, d);
	private final KPlusRegExpression kp = RPQExpressions.makeKPlusRegExpression(a);
	
	private final RegPathQuery Q1 = RPQExpressions.makeRegPathQuery(d, c, x);
	private final RegPathQuery Q2 = RPQExpressions.makeRegPathQuery(conv_a, x, y);
	private final RegPathQuery Q3 = RPQExpressions.makeRegPathQuery(dab, c, x);
	private final RegPathQuery Q4 = RPQExpressions.makeRegPathQuery(acd, x, y);
	private final RegPathQuery Q5 = RPQExpressions.makeRegPathQuery(ks, x, y);
	private final RegPathQuery Q6 = RPQExpressions.makeRegPathQuery(kp, x, y);
	private final RegPathQuery Q7 = RPQExpressions.makeRegPathQuery(dab, x, z);
	
	private final List<Term> uvars = new ArrayList<Term>(Arrays.asList(x));
	private final RPQConjunction<RegPathQuery> conj = RPQExpressions.makeRPQConjunction(Arrays.asList(Q4, Q7), uvars);
	private final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(cek, Arrays.asList(z)));
	
	private final Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral(qd, Arrays.asList(c, x)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(c, cd, x)));
	private final Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_^a", 2), Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(y, ca, x)));
	private final Rule r3 = Expressions.makeRule(Expressions.makePositiveLiteral(qb, Arrays.asList(c, x)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(c, cb, x)));
	private final Rule r4 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(c, x)), 
			Expressions.makePositiveLiteral(qd, Arrays.asList(c, x)));
	private final Rule r5 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(c, x)), 
			Expressions.makePositiveLiteral(qb, Arrays.asList(c, x)));
	private final Rule r6 = Expressions.makeRule(Expressions.makePositiveLiteral(qa, Arrays.asList(x, x1)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x, ca, x1)));
	private final Rule r7 = Expressions.makeRule(Expressions.makePositiveLiteral(qd, Arrays.asList(x1, y)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x1, cd, y)));
	private final Rule r8 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a / d)", 2), Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(qa, Arrays.asList(x, x1)), Expressions.makePositiveLiteral(qd, Arrays.asList(x1, y)));
	private final Rule r9 = Expressions.makeRule(Expressions.makePositiveLiteral(qa, Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x, ca, y)));
	private final Rule r10 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, ys, zs)));
	private final Rule r11 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(zs, ys, xs)));
	private final Rule r12 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(x, x1)), Expressions.makePositiveLiteral(qa, Arrays.asList(x1, y)));
	private final Rule r13 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)+", 2), Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(qa, Arrays.asList(x, y)));
	private final Rule r14 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)+", 2), Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)+", 2), Arrays.asList(x, x1)), Expressions.makePositiveLiteral(qa, Arrays.asList(x1, y)));
	private final Rule r15 = Expressions.makeRule(Expressions.makePositiveLiteral(qa, Arrays.asList(xs, ys)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, ca, ys)));
	private final Rule r16 = Expressions.makeRule(Expressions.makePositiveLiteral(qb, Arrays.asList(xs, ys)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, cb, ys)));
	private final Rule r17 = Expressions.makeRule(Expressions.makePositiveLiteral(qd, Arrays.asList(xs, ys)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, cd, ys)));
	private final Rule r18 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(xs, ys)), 
			Expressions.makePositiveLiteral(qd, Arrays.asList(xs, ys)));
	private final Rule r19 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(xs, ys)), 
			Expressions.makePositiveLiteral(qb, Arrays.asList(xs, ys)));
	private final Rule r20 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a / d)", 2), Arrays.asList(xs, zs)), 
			Expressions.makePositiveLiteral(qa, Arrays.asList(xs, ys)), Expressions.makePositiveLiteral(qd, Arrays.asList(ys, zs)));
	private final Rule r21 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", 1), Arrays.asList(x)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a / d)", 2), Arrays.asList(x, y)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(x, zu)), Expressions.makePositiveLiteral(cek, Arrays.asList(zu)));
	
	/////////////////////////////////////////////////
	
	private final KStarRegExpression as = RPQExpressions.makeKStarRegExpression(a);
	private final KStarRegExpression bs = RPQExpressions.makeKStarRegExpression(b);
	private final AlternRegExpression db = RPQExpressions.makeAlternRegExpression(d, b);
	private final ConcatRegExpression c1 = RPQExpressions.makeConcatRegExpression(as, db);
	private final ConcatRegExpression c2 = RPQExpressions.makeConcatRegExpression(c1, a);
	private final ConcatRegExpression c3 = RPQExpressions.makeConcatRegExpression(c2, bs);
	private final RegPathQuery Q8 = RPQExpressions.makeRegPathQuery(c3, x, y);
	
	private final Rule r201 = Expressions.makeRule(Expressions.makePositiveLiteral(qa, Arrays.asList(x, x1)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x, ca, x1)));
	private final Rule r202 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, ys, zs)));
	private final Rule r203 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(zs, ys, xs)));
	private final Rule r204 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(x, x1)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(x, x2)), Expressions.makePositiveLiteral(qa, Arrays.asList(x2, x1)));
	private final Rule r205 = Expressions.makeRule(Expressions.makePositiveLiteral(qb, Arrays.asList(x1, y)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x1, cb, y)));
	private final Rule r206 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(b)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, ys, zs)));
	private final Rule r207 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(b)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(zs, ys, xs)));
	private final Rule r208 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(b)*", 2), Arrays.asList(x1, y)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(b)*", 2), Arrays.asList(x1, x2)), Expressions.makePositiveLiteral(qb, Arrays.asList(x2, y)));
	private final Rule r209 = Expressions.makeRule(Expressions.makePositiveLiteral(qa, Arrays.asList(x2, x1)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x2, ca, x1)));
	private final Rule r210 = Expressions.makeRule(Expressions.makePositiveLiteral(qb, Arrays.asList(x1, x2)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x1, cb, x2)));
	private final Rule r211 = Expressions.makeRule(Expressions.makePositiveLiteral(qd, Arrays.asList(x1, x2)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(x1, cd, x2)));
	private final Rule r212 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(x1, x2)), 
			Expressions.makePositiveLiteral(qd, Arrays.asList(x1, x2)));
	private final Rule r213 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(x1, x2)), 
			Expressions.makePositiveLiteral(qb, Arrays.asList(x1, x2)));
	private final Rule r214 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((a)* / (d | b))", 2), Arrays.asList(x, x2)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a)*", 2), Arrays.asList(x, x1)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(d | b)", 2), Arrays.asList(x1, x2)));
	private final Rule r215 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(((a)* / (d | b)) / a)", 2), Arrays.asList(x, x1)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((a)* / (d | b))", 2), Arrays.asList(x, x2)), Expressions.makePositiveLiteral(qa, Arrays.asList(x2, x1)));
	private final Rule r216 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((((a)* / (d | b)) / a) / (b)*)", 2), Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(((a)* / (d | b)) / a)", 2), Arrays.asList(x, x1)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(b)*", 2), Arrays.asList(x1, y)));
	
	/////////////////////////////////////////////////
	
	private final ConverseEdgeLabel ac = RPQExpressions.makeConverseEdgeLabel(a);
	private final ConcatRegExpression c4 = RPQExpressions.makeConcatRegExpression(a, ac);
	private final KStarRegExpression c4s = RPQExpressions.makeKStarRegExpression(c4);
	private final RegPathQuery Q9 = RPQExpressions.makeRegPathQuery(c4s, x, const2);
	
	private final Rule r301 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_^a", 2), Arrays.asList(x1, const2)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(const2, ca, x1)));
	private final Rule r302 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a / ^a)", 2), Arrays.asList(x, const2)), 
			Expressions.makePositiveLiteral(qa, Arrays.asList(x, x1)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_^a", 2), Arrays.asList(x1, const2)));
	private final Rule r303 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((a / ^a))*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, ys, zs)));
	private final Rule r304 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((a / ^a))*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(zs, ys, xs)));
	private final Rule r305 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((a / ^a))*", 2), Arrays.asList(x, const2)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((a / ^a))*", 2), Arrays.asList(x, x1)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(a / ^a)", 2), Arrays.asList(x1, const2)));
	
	/////////////////////////////////////////////////
		
	private final EdgeLabel n3 = RPQExpressions.makeEdgeLabel("n3");
	private final Constant n3c = Expressions.makeAbstractConstant("n3");
	private final KStarRegExpression n3s = RPQExpressions.makeKStarRegExpression(n3);
	private final ConcatRegExpression c5 = RPQExpressions.makeConcatRegExpression(n3, n3s);
	private final RegPathQuery Q10 = RPQExpressions.makeRegPathQuery(c5, x, y);
	
	private final EdgeLabel n8 = RPQExpressions.makeEdgeLabel("n8");
	private final Constant n8c = Expressions.makeAbstractConstant("n8");
	private final KStarRegExpression n8s = RPQExpressions.makeKStarRegExpression(n8);
	private final ConcatRegExpression c6 = RPQExpressions.makeConcatRegExpression(n8, n8s);
	private final RegPathQuery Q11 = RPQExpressions.makeRegPathQuery(c6, x, y);
	
	private final List<Term> uvars2 = new ArrayList<Term>(Arrays.asList(x, y));
	private final RPQConjunction<RegPathQuery> conj2 = RPQExpressions.makeRPQConjunction(Arrays.asList(Q10, Q11), Arrays.asList(x, y));
	private final Conjunction<Literal> conjunct2 = Expressions.makeConjunction(Expressions.makePositiveLiteral(cek, Arrays.asList(x)));
	
	private final Rule r401 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_n3", 2), Arrays.asList(xs, ys)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, n3c, ys)));
	private final Rule r402 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, ys, zs)));
	private final Rule r403 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(zs, ys, xs)));
	private final Rule r404 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3)*", 2), Arrays.asList(xs, zs)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3)*", 2), Arrays.asList(xs, ys)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_n3", 2), Arrays.asList(ys, zs)));
	private final Rule r405 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3 / (n3)*)", 2), Arrays.asList(xs, zs)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_n3", 2), Arrays.asList(xs, ys)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3)*", 2), Arrays.asList(ys, zs)));
	private final Rule r406 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_n8", 2), Arrays.asList(xs, ys)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, n8c, ys)));
	private final Rule r407 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(xs, ys, zs)));
	private final Rule r408 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8)*", 2), Arrays.asList(xs, xs)), 
			Expressions.makePositiveLiteral(triple, Arrays.asList(zs, ys, xs)));
	private final Rule r409 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8)*", 2), Arrays.asList(xs, zs)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8)*", 2), Arrays.asList(xs, ys)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_n8", 2), Arrays.asList(ys, zs)));
	private final Rule r410 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8 / (n8)*)", 2), Arrays.asList(xs, zs)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_n8", 2), Arrays.asList(xs, ys)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8)*", 2), Arrays.asList(ys, zs)));
	private final Rule r411 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", 2), Arrays.asList(x, y)), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3 / (n3)*)", 2), Arrays.asList(x, y)), Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8 / (n8)*)", 2), Arrays.asList(x, y)), Expressions.makePositiveLiteral(cek, Arrays.asList(x)));
	
	/////////////////////////////////////////////////

	@Test
	public void testEdgeLabel() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q1);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r1);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
		
	@Test
	public void testConverseEdgeLabel() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q2);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r2);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testAlternRegExp() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q3);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r1);	comparator.add(r3);	comparator.add(r4);	comparator.add(r5);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testConcatRegExp() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q4);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r6);	comparator.add(r7);	comparator.add(r8);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testKStarRegExp() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q5);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r9);	comparator.add(r10);	comparator.add(r11);	
		comparator.add(r12);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testKPlusRegExp() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q6);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r9);	comparator.add(r13);	comparator.add(r14);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testCRPQ() {
		final List<Statement> datalogResult = RpqConverter.CRPQTranslate(uvars, conj, conjunct);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r15);	comparator.add(r16);	comparator.add(r17);	comparator.add(r18);
		comparator.add(r19);	comparator.add(r20);	comparator.add(r21);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testComplexRPQ1() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q8);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r201);	comparator.add(r202);	comparator.add(r203);	comparator.add(r204);
		comparator.add(r205);	comparator.add(r206);	comparator.add(r207);	comparator.add(r208);
		comparator.add(r209);	comparator.add(r210);	comparator.add(r211);	comparator.add(r212);
		comparator.add(r213);	comparator.add(r214);	comparator.add(r215);	comparator.add(r216);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testComplexRPQ2() {
		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q9);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r201);	comparator.add(r301);	comparator.add(r302);	comparator.add(r303);
		comparator.add(r304);	comparator.add(r305);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testComplexCRPQ() {
		final List<Statement> datalogResult = RpqConverter.CRPQTranslate(uvars2, conj2, conjunct2);
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r401);	comparator.add(r402);	comparator.add(r403);	comparator.add(r404);
		comparator.add(r405);	comparator.add(r406);	comparator.add(r407);	comparator.add(r408);
		comparator.add(r409);	comparator.add(r410);	comparator.add(r411);
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
}
