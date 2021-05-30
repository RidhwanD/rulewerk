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
	
	private final State q0 = RPQExpressions.makeState("q0");
	private final State q1 = RPQExpressions.makeState("q1");
	private final State q2 = RPQExpressions.makeState("q2");
	private final State q3 = RPQExpressions.makeState("q3");
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
	private final KStarRegExpression dbs = RPQExpressions.makeKStarRegExpression(db);
	
	private final Rule r101 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, xs), 
			Expressions.makePositiveLiteral(triple, xs, ys, zs));
	private final Rule r102 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, xs), 
			Expressions.makePositiveLiteral(triple, zs, ys, xs));
	private final Rule r103 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, zs), 
			Expressions.makePositiveLiteral("S_q0", xs, ys), Expressions.makePositiveLiteral(triple, ys, ca, zs));
	private final Rule r104 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q1", xs, zs), 
			Expressions.makePositiveLiteral("S_q0", xs, ys), Expressions.makePositiveLiteral(triple, ys, cb, zs));
	private final Rule r105 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q1", xs, zs), 
			Expressions.makePositiveLiteral("S_q0", xs, ys), Expressions.makePositiveLiteral(triple, ys, cd, zs));
	private final Rule r106 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, zs), 
			Expressions.makePositiveLiteral("S_q1", xs, ys), Expressions.makePositiveLiteral(triple, ys, ca, zs));
	private final Rule r107 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, zs), 
			Expressions.makePositiveLiteral("S_q2", xs, ys), Expressions.makePositiveLiteral(triple, ys, cb, zs));
	private final Rule r108 = Expressions.makeRule(Expressions.makePositiveLiteral("Q_((((a*) / (d | b)) / a) / (b*))", x, y), 
			Expressions.makePositiveLiteral("S_q2", x, y));
	
	private final Rule r201 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, ys), 
			Expressions.makePositiveLiteral("S_q2", xs, ys));
	private final Rule r202 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q1", xs, zs), 
			Expressions.makePositiveLiteral("S_q0", xs, ys), Expressions.makePositiveLiteral(triple, ys, ca, zs));
	private final Rule r203 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, zs), 
			Expressions.makePositiveLiteral("S_q1", xs, ys), Expressions.makePositiveLiteral(triple, zs, ca, ys));
	private final Rule r204 = Expressions.makeRule(Expressions.makePositiveLiteral("Q_((a / (^a))+)", x, const2), 
			Expressions.makePositiveLiteral("S_q2", x, const2));
	
	private final Rule r301 = Expressions.makeRule(Expressions.makePositiveLiteral("S_Top", xs, xs), 
			Expressions.makePositiveLiteral(triple, xs, ys, zs));
	private final Rule r302 = Expressions.makeRule(Expressions.makePositiveLiteral("S_Top", xs, xs), 
			Expressions.makePositiveLiteral(triple, zs, ys, xs));
	private final Rule r303 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, ys), 
			Expressions.makePositiveLiteral("S_Top", xs, ys));
	private final Rule r304 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q1", xs, zs), 
			Expressions.makePositiveLiteral("S_q0", xs, ys), Expressions.makePositiveLiteral(triple, ys, cn3, zs));
	private final Rule r305 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q1", xs, zs), 
			Expressions.makePositiveLiteral("S_q1", xs, ys), Expressions.makePositiveLiteral(triple, ys, cn3, zs));
	private final Rule r306 = Expressions.makeRule(Expressions.makePositiveLiteral("Q_(n3 / (n3*))", x, y), 
			Expressions.makePositiveLiteral("S_q0", x, y));
	private final Rule r307 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, ys), 
			Expressions.makePositiveLiteral("S_Top", xs, ys));
	private final Rule r308 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q3", xs, zs), 
			Expressions.makePositiveLiteral("S_q2", xs, ys), Expressions.makePositiveLiteral(triple, ys, cn8, zs));
	private final Rule r309 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q3", xs, zs), 
			Expressions.makePositiveLiteral("S_q3", xs, ys), Expressions.makePositiveLiteral(triple, ys, cn8, zs));
	private final Rule r310 = Expressions.makeRule(Expressions.makePositiveLiteral("Q_(n8 / (n8*))", x, y), 
			Expressions.makePositiveLiteral("S_q2", x, y));
	private final Rule r311 = Expressions.makeRule(Expressions.makePositiveLiteral("Ans", x, y), 
			Expressions.makePositiveLiteral("Q_(n3 / (n3*))", x, y), 
			Expressions.makePositiveLiteral("Q_(n8 / (n8*))", x, y), 
			Expressions.makePositiveLiteral(cek, x));
	
	private final Rule r401 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, ys), 
			Expressions.makePositiveLiteral("S_Top", xs, ys));
	private final Rule r402 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q10", xs, zs), 
			Expressions.makePositiveLiteral("S_q9", xs, ys), Expressions.makePositiveLiteral("TRIPLE", ys, cn3, zs));
	private final Rule r403 = Expressions.makeRule(Expressions.makePositiveLiteral("Q_n3", x, y), 
			Expressions.makePositiveLiteral("S_q10", x, y));
	private final Rule r404 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q9", xs, ys), 
			Expressions.makePositiveLiteral("S_Top", xs, ys));
	private final Rule r405 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, ys), 
			Expressions.makePositiveLiteral("S_q1", xs, ys));
	private final Rule r406 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q5", xs, ys), 
			Expressions.makePositiveLiteral("S_q3", xs, ys));
	private final Rule r407 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q4", xs, ys), 
			Expressions.makePositiveLiteral("S_q6", xs, ys));
	private final Rule r408 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q1", xs, ys), 
			Expressions.makePositiveLiteral("S_q4", xs, ys));
	private final Rule r409 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q3", xs, ys), 
			Expressions.makePositiveLiteral("S_q0", xs, ys));
	private final Rule r410 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, ys), 
			Expressions.makePositiveLiteral("S_q1", xs, ys));
	private final Rule r411 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q7", xs, ys), 
			Expressions.makePositiveLiteral("S_q3", xs, ys));
	private final Rule r412 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q6", xs, zs), 
			Expressions.makePositiveLiteral("S_q5", xs, ys), Expressions.makePositiveLiteral("TRIPLE", ys, cd, zs));
	private final Rule r413 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q1", xs, ys), 
			Expressions.makePositiveLiteral("S_q0", xs, ys));
	private final Rule r414 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q4", xs, ys), 
			Expressions.makePositiveLiteral("S_q8", xs, ys));
	private final Rule r415 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q8", xs, zs), 
			Expressions.makePositiveLiteral("S_q7", xs, ys), Expressions.makePositiveLiteral("TRIPLE", ys, cb, zs));
	private final Rule r416 = Expressions.makeRule(Expressions.makePositiveLiteral("S_Top", xs, xs), 
			Expressions.makePositiveLiteral("TRIPLE", xs, ys, zs));
	private final Rule r417 = Expressions.makeRule(Expressions.makePositiveLiteral("S_Top", xs, xs), 
			Expressions.makePositiveLiteral("TRIPLE", zs, ys, xs));
	private final Rule r418 = Expressions.makeRule(Expressions.makePositiveLiteral("Q_((d | b)*)", x, y), 
			Expressions.makePositiveLiteral("S_q2", x, y));
	private final Rule r419 = Expressions.makeRule(Expressions.makePositiveLiteral("Ans", x, y), 
			Expressions.makePositiveLiteral("Q_((d | b)*)", x, y), Expressions.makePositiveLiteral("Q_n3", x, y), Expressions.makePositiveLiteral("Check", x));
	
	private final Rule r420 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q5", xs, ys), 
			Expressions.makePositiveLiteral("S_q0", xs, ys));
	private final Rule r421 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, ys), 
			Expressions.makePositiveLiteral("S_q8", xs, ys));
	private final Rule r422 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, ys), 
			Expressions.makePositiveLiteral("S_q6", xs, ys));
	private final Rule r423 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q7", xs, ys), 
			Expressions.makePositiveLiteral("S_q0", xs, ys));
	private final Rule r424 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, ys), 
			Expressions.makePositiveLiteral("S_q6", xs, ys));
	private final Rule r425 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, ys), 
			Expressions.makePositiveLiteral("S_q0", xs, ys));
	private final Rule r426 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, ys), 
			Expressions.makePositiveLiteral("S_q0", xs, ys));
	private final Rule r427 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q2", xs, ys), 
			Expressions.makePositiveLiteral("S_q8", xs, ys));

	private final Rule r428 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, xs), 
			Expressions.makePositiveLiteral("TRIPLE", xs, ys, zs));
	private final Rule r429 = Expressions.makeRule(Expressions.makePositiveLiteral("S_q0", xs, xs), 
			Expressions.makePositiveLiteral("TRIPLE", zs, ys, xs));
	
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
		final List<Term> uvars = Arrays.asList(x, y);
		
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
	public void testRPQTranslate() {
		RegPathQuery rpq = RPQExpressions.makeRegPathQuery(dbs, x, y);
		final List<Statement> datalogResult = RpqNFAConverter.RPQTranslate(rpq);
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r405);	comparator.add(r406);	comparator.add(r407);	comparator.add(r408);
		comparator.add(r409);	comparator.add(r410);	comparator.add(r411);	comparator.add(r412);
		comparator.add(r413);	comparator.add(r414);	comparator.add(r415);	comparator.add(r418);
		comparator.add(r428);	comparator.add(r429);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testRPQTranslateAlt() {
		RegPathQuery rpq = RPQExpressions.makeRegPathQuery(dbs, x, y);
		final List<Statement> datalogResult = RpqNFAConverter.RPQTranslateAlt(rpq);
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r412);	comparator.add(r415);	comparator.add(r418);	comparator.add(r420);	
		comparator.add(r421);	comparator.add(r422);	comparator.add(r423);	comparator.add(r424);	
		comparator.add(r425);	comparator.add(r426);	comparator.add(r427);	comparator.add(r428);	
		comparator.add(r429);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testCRPQTranslate() {
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(cek, Arrays.asList(x)));
		final List<Term> uvars = Arrays.asList(x, y);
		
		List<RegPathQuery> qs = new ArrayList<RegPathQuery>();
		qs.add(RPQExpressions.makeRegPathQuery(dbs, x, y));
		qs.add(RPQExpressions.makeRegPathQuery(n3, x, y));
		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslate(uvars, RPQExpressions.makeRPQConjunction(qs, uvars), conjunct);
		
		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r401);	comparator.add(r402);	comparator.add(r403);	comparator.add(r404);
		comparator.add(r405);	comparator.add(r406);	comparator.add(r407);	comparator.add(r408);
		comparator.add(r409);	comparator.add(r410);	comparator.add(r411);	comparator.add(r412);
		comparator.add(r413);	comparator.add(r414);	comparator.add(r415);	comparator.add(r416);
		comparator.add(r417);	comparator.add(r418);	comparator.add(r419);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
	
	@Test
	public void testCRPQTranslateAlt() {
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(cek, Arrays.asList(x)));
		final List<Term> uvars = Arrays.asList(x, y);
		
		List<RegPathQuery> qs = new ArrayList<RegPathQuery>();
		qs.add(RPQExpressions.makeRegPathQuery(dbs, x, y));
		qs.add(RPQExpressions.makeRegPathQuery(n3, x, y));
		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslateAlt(uvars, RPQExpressions.makeRPQConjunction(qs, uvars), conjunct);

		final List<Statement> comparator = new ArrayList<Statement>();
		comparator.add(r401);	comparator.add(r402);	comparator.add(r403);	comparator.add(r404);
		comparator.add(r412);	comparator.add(r415);	comparator.add(r416);	comparator.add(r417);
		comparator.add(r418);	comparator.add(r419);	comparator.add(r420);	comparator.add(r421);	
		comparator.add(r422);	comparator.add(r423);	comparator.add(r424);	comparator.add(r425);	
		comparator.add(r426);	comparator.add(r427);
		
		assertEquals(new HashSet<Statement>(comparator), new HashSet<Statement>(datalogResult));
	}
}
