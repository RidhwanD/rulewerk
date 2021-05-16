package org.semanticweb.rulewerk.rpq.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;
import org.semanticweb.rulewerk.rpq.parser.ParsingException;
import org.semanticweb.rulewerk.rpq.parser.RPQParser;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.rpq.converter.RpqNFAConverter;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.NDFAQuery;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomataAlt;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.model.api.State;

public class RpqNFAConverterTest {
	public static void main(String[] arg) throws ParsingException {
		
		final Constant const1 = Expressions.makeAbstractConstant("1");
		final Constant const2 = Expressions.makeAbstractConstant("2");
		final Constant const3 = Expressions.makeAbstractConstant("3");
		final Constant const4 = Expressions.makeAbstractConstant("4");
		
		final State q0 = RPQExpressions.makeState("q0");
		final State q1 = RPQExpressions.makeState("q1");
		final State q2 = RPQExpressions.makeState("q2");
		
		final Set<State> states1 = new HashSet<State>(Arrays.asList(q0,q1,q2));
		final Set<State> finStates1 = new HashSet<State>(Arrays.asList(q2));

		final EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
		final EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
		final EdgeLabel d = RPQExpressions.makeEdgeLabel("d");
		final KStarRegExpression as = RPQExpressions.makeKStarRegExpression(a);
		final KStarRegExpression bs = RPQExpressions.makeKStarRegExpression(b);
		final AlternRegExpression db = RPQExpressions.makeAlternRegExpression(d, b);
		final ConcatRegExpression c1 = RPQExpressions.makeConcatRegExpression(as, db);
		final ConcatRegExpression c2 = RPQExpressions.makeConcatRegExpression(c1, a);
		final ConcatRegExpression c3 = RPQExpressions.makeConcatRegExpression(c2, bs);
		
		final Set<EdgeLabel> alphabet1 = new HashSet<EdgeLabel>(Arrays.asList(a,b,d));
		
		final Map<State,Map<EdgeLabel,List<State>>> transition1 = new HashMap<State,Map<EdgeLabel,List<State>>>();
		transition1.put(q0, new HashMap<EdgeLabel,List<State>>() {{ 
			put(a, new ArrayList<State>(Arrays.asList(q0)));
			put(b, new ArrayList<State>(Arrays.asList(q1))); 
			put(d, new ArrayList<State>(Arrays.asList(q1)));
		}});
		transition1.put(q1, new HashMap<EdgeLabel,List<State>>() {{ put(a, new ArrayList<State>(Arrays.asList(q2))); }});
		transition1.put(q2, new HashMap<EdgeLabel,List<State>>() {{ put(b, new ArrayList<State>(Arrays.asList(q2))); }});
		
		final Map<State,Map<ConverseEdgeLabel,List<State>>> convTransition1 = new HashMap<State,Map<ConverseEdgeLabel,List<State>>>();
		
		final NDFiniteAutomata ndfa1 = RPQExpressions.makeNDFiniteAutomata(c3, states1, alphabet1, q0, finStates1, transition1, convTransition1);
		
		final UniversalVariable x = Expressions.makeUniversalVariable("X");
		final UniversalVariable y = Expressions.makeUniversalVariable("Y");
		
		final NDFAQuery ndfaq1 = RPQExpressions.makeNDFAQuery(ndfa1, x, y);
		
		final KnowledgeBase kb1 = new KnowledgeBase();
		kb1.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const2)));
		kb1.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const4)));
		kb1.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("b"),const2)));
		kb1.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("d"),const3)));
		kb1.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const4,Expressions.makeAbstractConstant("d"),const2)));
		
		/////////////////////////////////////////////////
		
		final ConverseEdgeLabel ac = RPQExpressions.makeConverseEdgeLabel(a);
		final ConcatRegExpression c4 = RPQExpressions.makeConcatRegExpression(a, ac);
		final KStarRegExpression c4s = RPQExpressions.makeKStarRegExpression(c4);
		
		final Set<State> states2 = new HashSet<State>(Arrays.asList(q0,q1));
		final Set<State> finStates2 = new HashSet<State>(Arrays.asList(q0));
		
		final Set<EdgeLabel> alphabet2 = new HashSet<EdgeLabel>(Arrays.asList(a));
		
		final Map<State,Map<EdgeLabel,List<State>>> transition2 = new HashMap<State,Map<EdgeLabel,List<State>>>();
		transition2.put(q0, new HashMap<EdgeLabel,List<State>>() {{ put(a, new ArrayList<State>(Arrays.asList(q1))); }});
		
		final Map<State,Map<ConverseEdgeLabel,List<State>>> convTransition2 = new HashMap<State,Map<ConverseEdgeLabel,List<State>>>();
		convTransition2.put(q1, new HashMap<ConverseEdgeLabel,List<State>>() {{ put(ac, new ArrayList<State>(Arrays.asList(q0))); }});
		
		final NDFiniteAutomata ndfa2 = RPQExpressions.makeNDFiniteAutomata(c4s, states2, alphabet2, q0, finStates2, transition2, convTransition2);
		
		final NDFAQuery ndfaq2 = RPQExpressions.makeNDFAQuery(ndfa2, x, const2);
		
		final KnowledgeBase kb2 = new KnowledgeBase();
		kb2.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const3)));
		kb2.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const3)));
		kb2.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const4)));
		
		/////////////////////////////////////////////////
		
		final State q3 = RPQExpressions.makeState("q3");
		
		final EdgeLabel n3 = RPQExpressions.makeEdgeLabel("n3");
		final KStarRegExpression n3s = RPQExpressions.makeKStarRegExpression(n3);
		final ConcatRegExpression c5 = RPQExpressions.makeConcatRegExpression(n3, n3s);
		
		final Set<State> states3 = new HashSet<State>(Arrays.asList(q0,q1));
		final Set<State> finStates3 = new HashSet<State>(Arrays.asList(q1));
		
		final Set<EdgeLabel> alphabet3 = new HashSet<EdgeLabel>(Arrays.asList(n3));
		
		final Map<State,Map<EdgeLabel,List<State>>> transition3 = new HashMap<State,Map<EdgeLabel,List<State>>>();
		transition3.put(q0, new HashMap<EdgeLabel,List<State>>() {{ put(n3, new ArrayList<State>(Arrays.asList(q1))); }});
		transition3.put(q1, new HashMap<EdgeLabel,List<State>>() {{ put(n3, new ArrayList<State>(Arrays.asList(q1))); }});
		
		final NDFiniteAutomata ndfa3 = RPQExpressions.makeNDFiniteAutomata(c5, states3, alphabet3, q0, finStates3, transition3, convTransition1);
		
		final NDFAQuery ndfaq3 = RPQExpressions.makeNDFAQuery(ndfa3, x, y);
		
		final EdgeLabel n8 = RPQExpressions.makeEdgeLabel("n8");
		final KStarRegExpression n8s = RPQExpressions.makeKStarRegExpression(n8);
		final ConcatRegExpression c6 = RPQExpressions.makeConcatRegExpression(n8, n8s);
		
		final Set<State> states4 = new HashSet<State>(Arrays.asList(q2,q3));
		final Set<State> finStates4 = new HashSet<State>(Arrays.asList(q3));
		
		final Set<EdgeLabel> alphabet4 = new HashSet<EdgeLabel>(Arrays.asList(n8));
		
		final Map<State,Map<EdgeLabel,List<State>>> transition4 = new HashMap<State,Map<EdgeLabel,List<State>>>();
		transition4.put(q2, new HashMap<EdgeLabel,List<State>>() {{ put(n8, new ArrayList<State>(Arrays.asList(q3))); }});
		transition4.put(q3, new HashMap<EdgeLabel,List<State>>() {{ put(n8, new ArrayList<State>(Arrays.asList(q3))); }});
		
		final NDFiniteAutomata ndfa4 = RPQExpressions.makeNDFiniteAutomata(c6, states4, alphabet4, q2, finStates4, transition4, convTransition1);
		
		final NDFAQuery ndfaq4 = RPQExpressions.makeNDFAQuery(ndfa4, x, y);
		
		final KnowledgeBase kb3 = new KnowledgeBase();
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("n3"),const3)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("n3"),const3)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("n8"),const3)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("n8"),const4)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("n3"),const4)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("n8"),const4)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("Check", 1), const1));
		
		final List<NDFAQuery> queries = new ArrayList<NDFAQuery>(Arrays.asList(ndfaq3, ndfaq4));
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(Expressions.makePredicate("Check", 1), Arrays.asList(x)));
		final List<Term> uvars = new ArrayList<Term>(Arrays.asList(x, y));
		
		/////////////////////////////////////////////////

//		final List<Statement> datalogResult = RpqNFAConverter.NDFAQueryTranslate(ndfaq1, kb1);
		
//		final List<Statement> datalogResult = RpqNFAConverter.RPQTranslate(RPQExpressions.makeRegPathQuery(ndfaq1.getNDFA().getRegex(), ndfaq1.getTerm1(), ndfaq1.getTerm2()), kb1);
		
//		final List<Statement> datalogResult = RpqNFAConverter.NDFAQueryTranslate(ndfaq2, kb2);
		
//		final List<Statement> datalogResult = RpqNFAConverter.RPQTranslate(RPQExpressions.makeRegPathQuery(ndfaq2.getNDFA().getRegex(), ndfaq2.getTerm1(), ndfaq2.getTerm2()), kb2);
		
//		final List<Statement> datalogResult = RpqNFAConverter.CNDFATranslate(uvars, queries, conjunct, kb3);
		
//		List<RegPathQuery> qs = new ArrayList<RegPathQuery>();
//		for (NDFAQuery nfa : queries) {
//			qs.add(RPQExpressions.makeRegPathQuery(nfa.getNDFA().getRegex(), nfa.getTerm1(), nfa.getTerm2()));
//		}
//		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslate(uvars, RPQExpressions.makeRPQConjunction(qs, uvars), conjunct, kb3);
//		
//		for (Statement r: datalogResult) {
//			System.out.println(r);
//		}
		
		////////////////////////////////////////////////////
		
		String input = "@prefix g: <http://example.org/gmark/> . SELECT DISTINCT ?x2 ?x0 ?x1 WHERE {  {  ?x0 ((((^g:ptag)/g:ptitle)/(^g:pemail))/(g:plike*)) ?x1 . }}";
		
		RPQConjunction<RegPathQuery> rpqs = RPQParser.parse(input);
		
		NDFiniteAutomata nfa = RpqNFAConverter.regex2NFAConverter(rpqs.getRPQs().get(0).getExpression(), 0);
		System.out.println(nfa.getState());
		System.out.println(nfa.getInitState());
		System.out.println(nfa.getFinState());
		System.out.println(nfa.getTransition());
		System.out.println(nfa.getConvTransition());
		
		System.out.println("Initial Translation");
		final List<Statement> datalogResult = RpqNFAConverter.RPQTranslate(rpqs.getRPQs().get(0), kb1);
		for (Statement r:datalogResult) System.out.println(r);

		System.out.println();
		NDFiniteAutomataAlt nnfa = RpqConverterUtils.convertToAlt(nfa);
		System.out.println(nnfa.getTransition());
		System.out.println(nnfa.getConvTransition());
		
		System.out.println();
		NDFiniteAutomataAlt nnfas = RpqConverterUtils.simplify(nnfa);
		System.out.println(nnfas.getTransition());
		System.out.println(nnfas.getConvTransition());
		System.out.println("Simplified Translation");
		final List<Statement> datalogResult2 = RpqNFAConverter.RPQTranslateAlt(rpqs.getRPQs().get(0), kb1);
		for (Statement r:datalogResult2) System.out.println(r);
	}
}
