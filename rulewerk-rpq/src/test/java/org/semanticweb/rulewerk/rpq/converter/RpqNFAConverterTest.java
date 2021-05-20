package org.semanticweb.rulewerk.rpq.converter;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.NDFAQuery;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;

public class RpqNFAConverterTest {
	private final Constant const2 = Expressions.makeAbstractConstant("2");
	private final UniversalVariable x = Expressions.makeUniversalVariable("X");
	private final UniversalVariable y = Expressions.makeUniversalVariable("Y");
	private final UniversalVariable xs = Expressions.makeUniversalVariable("x");
	private final UniversalVariable ys = Expressions.makeUniversalVariable("y");
	private final UniversalVariable zs = Expressions.makeUniversalVariable("z");
	private final Predicate triple = Expressions.makePredicate("TRIPLE", 3);
	private final Predicate cek = Expressions.makePredicate("Check", 1);
	
	private final Predicate sTop = Expressions.makePredicate("S_Top", 2);
	private final State q0 = RPQExpressions.makeState("q0");
	private final Predicate sq0 = Expressions.makePredicate("S_q0", 2);
	private final State q1 = RPQExpressions.makeState("q1");
	private final Predicate sq1 = Expressions.makePredicate("S_q1", 2);
	private final State q2 = RPQExpressions.makeState("q2");
	private final Predicate sq2 = Expressions.makePredicate("S_q2", 2);
	private final State q3 = RPQExpressions.makeState("q3");
	private final Predicate sq3 = Expressions.makePredicate("S_q3", 2);
	private final State q4 = RPQExpressions.makeState("q4");
	private final State q5 = RPQExpressions.makeState("q5");
	private final State q6 = RPQExpressions.makeState("q6");
	private final State q7 = RPQExpressions.makeState("q7");
	private final State q8 = RPQExpressions.makeState("q8");
	private final State q9 = RPQExpressions.makeState("q9");
	private final State q10 = RPQExpressions.makeState("q10");
	private final State q11 = RPQExpressions.makeState("q11");
	private final State q12 = RPQExpressions.makeState("q12");
	private final State q13 = RPQExpressions.makeState("q13");
	private final State q14 = RPQExpressions.makeState("q14");
	private final State q15 = RPQExpressions.makeState("q15");
	private final State q16 = RPQExpressions.makeState("q16");
	private final State q17 = RPQExpressions.makeState("q17");
	
	private final EdgeLabel epsilon = RPQExpressions.makeEdgeLabel("");
	private final EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
	private final Constant ca = Expressions.makeAbstractConstant("a");
	private final EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
	private final Constant cb = Expressions.makeAbstractConstant("b");
	private final EdgeLabel d = RPQExpressions.makeEdgeLabel("d");
	private final Constant cd = Expressions.makeAbstractConstant("d");
	private final KStarRegExpression as = RPQExpressions.makeKStarRegExpression(a);
	private final KStarRegExpression bs = RPQExpressions.makeKStarRegExpression(b);
	private final AlternRegExpression db = RPQExpressions.makeAlternRegExpression(d, b);
	private final ConcatRegExpression c1 = RPQExpressions.makeConcatRegExpression(as, db);
	private final ConcatRegExpression c2 = RPQExpressions.makeConcatRegExpression(c1, a);
	private final ConcatRegExpression c3 = RPQExpressions.makeConcatRegExpression(c2, bs);
	private final ConverseEdgeLabel ac = RPQExpressions.makeConverseEdgeLabel(a);
	private final ConcatRegExpression c4 = RPQExpressions.makeConcatRegExpression(a, ac);
	private final KPlusRegExpression c4s = RPQExpressions.makeKPlusRegExpression(c4);
	private final EdgeLabel n3 = RPQExpressions.makeEdgeLabel("n3");
	private final Constant cn3 = Expressions.makeAbstractConstant("n3");
	private final KStarRegExpression n3s = RPQExpressions.makeKStarRegExpression(n3);
	private final ConcatRegExpression c5 = RPQExpressions.makeConcatRegExpression(n3, n3s);
	private final EdgeLabel n8 = RPQExpressions.makeEdgeLabel("n8");
	private final Constant cn8 = Expressions.makeAbstractConstant("n8");
	private final KStarRegExpression n8s = RPQExpressions.makeKStarRegExpression(n8);
	private final ConcatRegExpression c6 = RPQExpressions.makeConcatRegExpression(n8, n8s);
	
	private final Rule r101 = Expressions.makeRule(Expressions.makePositiveLiteral(sq0, xs, xs), 
			Expressions.makePositiveLiteral(triple, xs, ys, zs));
	private final Rule r102 = Expressions.makeRule(Expressions.makePositiveLiteral(sq0, xs, xs), 
			Expressions.makePositiveLiteral(triple, zs, ys, xs));
	private final Rule r103 = Expressions.makeRule(Expressions.makePositiveLiteral(sq0, xs, zs), 
			Expressions.makePositiveLiteral(sq0, xs, ys), Expressions.makePositiveLiteral(triple, ys, ca, zs));
	private final Rule r104 = Expressions.makeRule(Expressions.makePositiveLiteral(sq1, xs, zs), 
			Expressions.makePositiveLiteral(sq0, xs, ys), Expressions.makePositiveLiteral(triple, ys, cb, zs));
	private final Rule r105 = Expressions.makeRule(Expressions.makePositiveLiteral(sq1, xs, zs), 
			Expressions.makePositiveLiteral(sq0, xs, ys), Expressions.makePositiveLiteral(triple, ys, cd, zs));
	private final Rule r106 = Expressions.makeRule(Expressions.makePositiveLiteral(sq2, xs, zs), 
			Expressions.makePositiveLiteral(sq1, xs, ys), Expressions.makePositiveLiteral(triple, ys, ca, zs));
	private final Rule r107 = Expressions.makeRule(Expressions.makePositiveLiteral(sq2, xs, zs), 
			Expressions.makePositiveLiteral(sq2, xs, ys), Expressions.makePositiveLiteral(triple, ys, cb, zs));
	private final Rule r108 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((((a)* / (d | b)) / a) / (b)*)", 2), x, y), 
			Expressions.makePositiveLiteral(sq2, x, y));
	
	private final Rule r201 = Expressions.makeRule(Expressions.makePositiveLiteral(sq0, xs, ys), 
			Expressions.makePositiveLiteral(sq2, xs, ys));
	private final Rule r202 = Expressions.makeRule(Expressions.makePositiveLiteral(sq1, xs, zs), 
			Expressions.makePositiveLiteral(sq0, xs, ys), Expressions.makePositiveLiteral(triple, ys, ca, zs));
	private final Rule r203 = Expressions.makeRule(Expressions.makePositiveLiteral(sq2, xs, zs), 
			Expressions.makePositiveLiteral(sq1, xs, ys), Expressions.makePositiveLiteral(triple, zs, ca, ys));
	private final Rule r204 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((a / ^a))+", 2), x, const2), 
			Expressions.makePositiveLiteral(sq2, x, const2));
	
	private final Rule r301 = Expressions.makeRule(Expressions.makePositiveLiteral(sTop, xs, xs), 
			Expressions.makePositiveLiteral(triple, xs, ys, zs));
	private final Rule r302 = Expressions.makeRule(Expressions.makePositiveLiteral(sTop, xs, xs), 
			Expressions.makePositiveLiteral(triple, zs, ys, xs));
	private final Rule r303 = Expressions.makeRule(Expressions.makePositiveLiteral(sq0, xs, ys), 
			Expressions.makePositiveLiteral(sTop, xs, ys));
	private final Rule r304 = Expressions.makeRule(Expressions.makePositiveLiteral(sq1, xs, zs), 
			Expressions.makePositiveLiteral(sq0, xs, ys), Expressions.makePositiveLiteral(triple, ys, cn3, zs));
	private final Rule r305 = Expressions.makeRule(Expressions.makePositiveLiteral(sq1, xs, zs), 
			Expressions.makePositiveLiteral(sq1, xs, ys), Expressions.makePositiveLiteral(triple, ys, cn3, zs));
	private final Rule r306 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3 / (n3)*)", 2), x, y), 
			Expressions.makePositiveLiteral(sq0, x, y));
	private final Rule r307 = Expressions.makeRule(Expressions.makePositiveLiteral(sq2, xs, ys), 
			Expressions.makePositiveLiteral(sTop, xs, ys));
	private final Rule r308 = Expressions.makeRule(Expressions.makePositiveLiteral(sq3, xs, zs), 
			Expressions.makePositiveLiteral(sq2, xs, ys), Expressions.makePositiveLiteral(triple, ys, cn8, zs));
	private final Rule r309 = Expressions.makeRule(Expressions.makePositiveLiteral(sq3, xs, zs), 
			Expressions.makePositiveLiteral(sq3, xs, ys), Expressions.makePositiveLiteral(triple, ys, cn8, zs));
	private final Rule r310 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8 / (n8)*)", 2), x, y), 
			Expressions.makePositiveLiteral(sq2, x, y));
	private final Rule r311 = Expressions.makeRule(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", 2), x, y), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n3 / (n3)*)", 2), x, y), 
			Expressions.makePositiveLiteral(Expressions.makePredicate("Q_(n8 / (n8)*)", 2), x, y), 
			Expressions.makePositiveLiteral(cek, x));
	
	// -------------------------------------- Begin Test ---------------------------------- //
	
	@Test
	public void testNDFAQueryTranslate() {
		final Set<State> states = new HashSet<State>(Arrays.asList(q0,q1,q2));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q2));
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a,b,d));
		final Set<Transition> transition = new HashSet<Transition>();
		transition.add(RPQExpressions.makeTransition(q0, q0, a));
		transition.add(RPQExpressions.makeTransition(q0, q1, b));
		transition.add(RPQExpressions.makeTransition(q0, q1, d));
		transition.add(RPQExpressions.makeTransition(q1, q2, a));
		transition.add(RPQExpressions.makeTransition(q2, q2, b));
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		final NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(c3, states, alphabet, q0, finStates, transition, convTransition);
		final NDFAQuery ndfaq = RPQExpressions.makeNDFAQuery(ndfa, x, y);
		final List<Statement> datalogResult = RpqNFAConverter.NDFAQueryTranslate(ndfaq);
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r101);	comparator.add(r102);	comparator.add(r103);	comparator.add(r104);
		comparator.add(r105);	comparator.add(r106);	comparator.add(r107);	comparator.add(r108);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testNDFAQueryTranslate2() {
		final Set<State> states = new HashSet<State>(Arrays.asList(q0,q1,q2));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q2));
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a));
		final Set<Transition> transition = new HashSet<Transition>();
		transition.add(RPQExpressions.makeTransition(q0, q1, a));
		transition.add(RPQExpressions.makeTransition(q2, q0, epsilon));
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		convTransition.add(RPQExpressions.makeConverseTransition(q1, q2, ac));
		final NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(c4s, states, alphabet, q0, finStates, transition, convTransition);
		final NDFAQuery ndfaq = RPQExpressions.makeNDFAQuery(ndfa, x, const2);
		final List<Statement> datalogResult = RpqNFAConverter.NDFAQueryTranslate(ndfaq);
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r101);	comparator.add(r102);	comparator.add(r201);	
		comparator.add(r202);	comparator.add(r203);	comparator.add(r204);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testNDFAQueryTranslate3() {
		final Set<State> states1 = new HashSet<State>(Arrays.asList(q0,q1));
		final Set<State> finStates1 = new HashSet<State>(Arrays.asList(q0));
		final Set<EdgeLabel> alphabet1 = new HashSet<EdgeLabel>(Arrays.asList(n3));
		final Set<Transition> transition1 = new HashSet<Transition>();
		transition1.add(RPQExpressions.makeTransition(q0, q1, n3));
		transition1.add(RPQExpressions.makeTransition(q1, q1, n3));
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(c5, states1, alphabet1, q0, finStates1, transition1, convTransition);
		final NDFAQuery ndfaq1 = RPQExpressions.makeNDFAQuery(ndfa1, x, y);
		
		final Set<State> states2 = new HashSet<State>(Arrays.asList(q2,q3));
		final Set<State> finStates2 = new HashSet<State>(Arrays.asList(q2));
		final Set<EdgeLabel> alphabet2 = new HashSet<EdgeLabel>(Arrays.asList(n8));
		final Set<Transition> transition2 = new HashSet<Transition>();
		transition2.add(RPQExpressions.makeTransition(q2, q3, n8));
		transition2.add(RPQExpressions.makeTransition(q3, q3, n8));
		final NDFiniteAutomata ndfa2 = RPQExpressions.makeNDFiniteAutomata(c6, states2, alphabet2, q2, finStates2, transition2, convTransition);
		final NDFAQuery ndfaq2 = RPQExpressions.makeNDFAQuery(ndfa2, x, y);
		final List<NDFAQuery> queries = new ArrayList<NDFAQuery>(Arrays.asList(ndfaq1, ndfaq2));
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(cek, Arrays.asList(x)));
		final List<Term> uvars = new ArrayList<Term>(Arrays.asList(x, y));
		
		final List<Statement> datalogResult = RpqNFAConverter.CNDFATranslate(uvars, queries, conjunct);
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r301);	comparator.add(r302);	comparator.add(r303);	comparator.add(r304);
		comparator.add(r305);	comparator.add(r306);	comparator.add(r307);	comparator.add(r308);
		comparator.add(r309);	comparator.add(r310);	comparator.add(r311);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testRegex2NFAConverter1() {		
		final Set<State> states = new HashSet<State>(Arrays.asList(q0, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15, q16, q17));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q15));
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a, b, d));
		final Set<Transition> transition = new HashSet<Transition>();
		transition.add(RPQExpressions.makeTransition(q0, q1, epsilon));
		transition.add(RPQExpressions.makeTransition(q0, q3, epsilon));
		transition.add(RPQExpressions.makeTransition(q1, q0, epsilon));
		transition.add(RPQExpressions.makeTransition(q1, q2, epsilon));
		transition.add(RPQExpressions.makeTransition(q2, q5, epsilon));
		transition.add(RPQExpressions.makeTransition(q3, q4, a));
		transition.add(RPQExpressions.makeTransition(q4, q1, epsilon));
		transition.add(RPQExpressions.makeTransition(q5, q7, epsilon));
		transition.add(RPQExpressions.makeTransition(q5, q9, epsilon));
		transition.add(RPQExpressions.makeTransition(q6, q11, epsilon));
		transition.add(RPQExpressions.makeTransition(q7, q8, d));
		transition.add(RPQExpressions.makeTransition(q8, q6, epsilon));
		transition.add(RPQExpressions.makeTransition(q9, q10, b));
		transition.add(RPQExpressions.makeTransition(q10, q6, epsilon));
		transition.add(RPQExpressions.makeTransition(q11, q12, a));
		transition.add(RPQExpressions.makeTransition(q12, q13, epsilon));
		transition.add(RPQExpressions.makeTransition(q13, q14, epsilon));
		transition.add(RPQExpressions.makeTransition(q13, q16, epsilon));
		transition.add(RPQExpressions.makeTransition(q14, q13, epsilon));
		transition.add(RPQExpressions.makeTransition(q14, q15, epsilon));
		transition.add(RPQExpressions.makeTransition(q16, q17, b));
		transition.add(RPQExpressions.makeTransition(q17, q14, epsilon));
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		NDFiniteAutomata comparator = RPQExpressions.makeNDFiniteAutomata(c3, states, alphabet, q0, finStates, transition, convTransition);
		
		NDFiniteAutomata result = RpqNFAConverter.regex2NFAConverter(c3, 0);
		assertEquals(comparator, result);
	}
	
	@Test
	public void testRegex2NFAConverter2() {		
		final Set<State> states = new HashSet<State>(Arrays.asList(q0, q1, q2, q3, q4, q5, q6));
		final Set<State> finStates = new HashSet<State>(Arrays.asList(q2));
		final Set<EdgeLabel> alphabet = new HashSet<EdgeLabel>(Arrays.asList(a));
		final Set<Transition> transition = new HashSet<Transition>();
		transition.add(RPQExpressions.makeTransition(q0, q3, epsilon));
		transition.add(RPQExpressions.makeTransition(q1, q2, epsilon));
		transition.add(RPQExpressions.makeTransition(q1, q0, epsilon));
		transition.add(RPQExpressions.makeTransition(q3, q4, a));
		transition.add(RPQExpressions.makeTransition(q4, q5, epsilon));
		transition.add(RPQExpressions.makeTransition(q6, q1, epsilon));
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		convTransition.add(RPQExpressions.makeConverseTransition(q5, q6, ac));
		NDFiniteAutomata comparator = RPQExpressions.makeNDFiniteAutomata(c4s, states, alphabet, q0, finStates, transition, convTransition);
		
		NDFiniteAutomata result = RpqNFAConverter.regex2NFAConverter(c4s, 0);
		assertEquals(comparator, result);
	}
	
	@Test
	public void testCRPQTranslate() {
		final Set<State> states1 = new HashSet<State>(Arrays.asList(q0,q1));
		final Set<State> finStates1 = new HashSet<State>(Arrays.asList(q0));
		final Set<EdgeLabel> alphabet1 = new HashSet<EdgeLabel>(Arrays.asList(n3));
		final Set<Transition> transition1 = new HashSet<Transition>();
		transition1.add(RPQExpressions.makeTransition(q0, q1, n3));
		transition1.add(RPQExpressions.makeTransition(q1, q1, n3));
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(c5, states1, alphabet1, q0, finStates1, transition1, convTransition);
		final NDFAQuery ndfaq1 = RPQExpressions.makeNDFAQuery(ndfa1, x, y);
		
		final Set<State> states2 = new HashSet<State>(Arrays.asList(q2,q3));
		final Set<State> finStates2 = new HashSet<State>(Arrays.asList(q2));
		final Set<EdgeLabel> alphabet2 = new HashSet<EdgeLabel>(Arrays.asList(n8));
		final Set<Transition> transition2 = new HashSet<Transition>();
		transition2.add(RPQExpressions.makeTransition(q2, q3, n8));
		transition2.add(RPQExpressions.makeTransition(q3, q3, n8));
		final NDFiniteAutomata ndfa2 = RPQExpressions.makeNDFiniteAutomata(c6, states2, alphabet2, q2, finStates2, transition2, convTransition);
		final NDFAQuery ndfaq2 = RPQExpressions.makeNDFAQuery(ndfa2, x, y);
		final List<NDFAQuery> queries = new ArrayList<NDFAQuery>(Arrays.asList(ndfaq1, ndfaq2));
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(cek, Arrays.asList(x)));
		final List<Term> uvars = new ArrayList<Term>(Arrays.asList(x, y));
		
		List<RegPathQuery> qs = new ArrayList<RegPathQuery>();
		qs.add(RPQExpressions.makeRegPathQuery(c5, x, y));
		qs.add(RPQExpressions.makeRegPathQuery(c6, x, y));
		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslate(uvars, RPQExpressions.makeRPQConjunction(qs, uvars), conjunct);
		for (Statement r: datalogResult) {
			System.out.println(r);
		}
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r301);	comparator.add(r302);	comparator.add(r303);	comparator.add(r304);
		comparator.add(r305);	comparator.add(r306);	comparator.add(r307);	comparator.add(r308);
		comparator.add(r309);	comparator.add(r310);	comparator.add(r311);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testCRPQTranslateAlt() {
		final Set<State> states1 = new HashSet<State>(Arrays.asList(q0,q1));
		final Set<State> finStates1 = new HashSet<State>(Arrays.asList(q0));
		final Set<EdgeLabel> alphabet1 = new HashSet<EdgeLabel>(Arrays.asList(n3));
		final Set<Transition> transition1 = new HashSet<Transition>();
		transition1.add(RPQExpressions.makeTransition(q0, q1, n3));
		transition1.add(RPQExpressions.makeTransition(q1, q1, n3));
		final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(c5, states1, alphabet1, q0, finStates1, transition1, convTransition);
		final NDFAQuery ndfaq1 = RPQExpressions.makeNDFAQuery(ndfa1, x, y);
		
		final Set<State> states2 = new HashSet<State>(Arrays.asList(q2,q3));
		final Set<State> finStates2 = new HashSet<State>(Arrays.asList(q2));
		final Set<EdgeLabel> alphabet2 = new HashSet<EdgeLabel>(Arrays.asList(n8));
		final Set<Transition> transition2 = new HashSet<Transition>();
		transition2.add(RPQExpressions.makeTransition(q2, q3, n8));
		transition2.add(RPQExpressions.makeTransition(q3, q3, n8));
		final NDFiniteAutomata ndfa2 = RPQExpressions.makeNDFiniteAutomata(c6, states2, alphabet2, q2, finStates2, transition2, convTransition);
		final NDFAQuery ndfaq2 = RPQExpressions.makeNDFAQuery(ndfa2, x, y);
		final List<NDFAQuery> queries = new ArrayList<NDFAQuery>(Arrays.asList(ndfaq1, ndfaq2));
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(cek, Arrays.asList(x)));
		final List<Term> uvars = new ArrayList<Term>(Arrays.asList(x, y));
		
		List<RegPathQuery> qs = new ArrayList<RegPathQuery>();
		qs.add(RPQExpressions.makeRegPathQuery(c5, x, y));
		qs.add(RPQExpressions.makeRegPathQuery(c6, x, y));
		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslate(uvars, RPQExpressions.makeRPQConjunction(qs, uvars), conjunct);
		for (Statement r: datalogResult) {
			System.out.println(r);
		}
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r301);	comparator.add(r302);	comparator.add(r303);	comparator.add(r304);
		comparator.add(r305);	comparator.add(r306);	comparator.add(r307);	comparator.add(r308);
		comparator.add(r309);	comparator.add(r310);	comparator.add(r311);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
//	public static void main(String[] arg) throws ParsingException {
//		////////////////////////////////////////////////////
//		
//		String input = "@prefix g: <http://example.org/gmark/> . SELECT DISTINCT ?x0 ?x1 WHERE {  {  ?x0 ((((^g:ptag)/g:ptitle)/(^g:pemail))/(g:plike*)) ?x1 . }}";
////		String input = "select ?X where {{ ?X ( ( <http://example.org/m> | <http://example.org/n> ) / (( ^<http://example.org/l> )* ) ) <http://example.org/c> . }}";
////		String input = "select ?X where {{ ?X ( ( <http://example.org/o> / <http://example.org/p> ) | ( ( <http://example.org/o> / <http://example.org/p> )* ) ) <http://example.org/c> .}}";
////		String input = "select ?X where {{ ?X ( ( ( <http://example.org/m> | <http://example.org/n> ) / ( (^<http://example.org/l>)* ) )* ) <http://example.org/c> .}}";
//		
//		RPQConjunction<RegPathQuery> rpqs = RPQParser.parse(input);
//		
//		NDFiniteAutomata nfa = RpqNFAConverter.regex2NFAConverter(rpqs.getRPQs().get(0).getExpression(), 0);
//		System.out.println(nfa.getState());
//		System.out.println(nfa.getInitState());
//		System.out.println(nfa.getFinState());
//		System.out.println(nfa.getTransition());
//		System.out.println(nfa.getConvTransition());
//		
//		System.out.println("Initial Translation");
//		final List<Statement> datalogResult = RpqNFAConverter.RPQTranslate(rpqs.getRPQs().get(0), kb1);
//		for (Statement r:datalogResult) System.out.println(r);
//
//		System.out.println();
//		NDFiniteAutomataAlt nnfa = RpqConverterUtils.convertToAlt(nfa);
//		System.out.println(nnfa.getTransition());
//		System.out.println(nnfa.getConvTransition());
//		
//		System.out.println();
//		System.out.println("Simplified Translation");
//		final List<Statement> datalogResult2 = RpqNFAConverter.RPQTranslateAlt(rpqs.getRPQs().get(0), kb1);
//		for (Statement r:datalogResult2) System.out.println(r);
//		
//		System.out.println();
//		NDFiniteAutomataAlt nnfas = RpqConverterUtils.simplify(nnfa);
//		System.out.println(nnfas.getTransition());
//		System.out.println(nnfas.getConvTransition());
//	}
}
