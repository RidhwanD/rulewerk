package org.semanticweb.rulewerk.rpq.converter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.NDFAQuery;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegExpressionType;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;

public class RpqNFAConverter {
	/**
	 * Creates a set of Datalog {@link Statement} from an {@link NDFiniteAutomata} and some {@link Term}.
	 *
	 * @param ndfa 		non-null {@link NDFiniteAutomata} of the NDFA
	 * @param v1 		non-null {@link Term} from the RPQ 
	 * @param v2 		non-null {@link Term} from the RPQ 
	 * @param inCRPQ	non-null boolean
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */	
	public static List<Statement> translateExpression(final NDFiniteAutomata ndfa, final Term t1, final Term t2, final boolean inCRPQ) {
		final List<Statement> datalogRule = new ArrayList<Statement>();
		final Variable x = Expressions.makeUniversalVariable("x");
		final Variable y = Expressions.makeUniversalVariable("y");
		final Variable z = Expressions.makeUniversalVariable("z");
		
		// For the initial state q0, add the facts S_q0(x,x) for all terms x in knowledge base
		if (inCRPQ) {
			datalogRule.add(Expressions.makeRule(Expressions.makePositiveLiteral(
					Expressions.makePredicate("S_"+ndfa.getInitState().getName(), 2), x, y), 
					Expressions.makePositiveLiteral(
							Expressions.makePredicate("S_Top", 2), x, y)));
		} else
			datalogRule.addAll(RpqConverterUtils.createKBaseRules(Expressions.makePredicate("S_"+ndfa.getInitState().getName(), 2)));
		
		// For every final states qf, we add a rule Q_E(x,y) :- S_qf(x,y)
		final Predicate IDBPredicateF = Expressions.makePredicate("Q_"+ndfa.toString(), 2);
		for (State s : ndfa.getFinState()) {
			final Predicate IDBPredicateSt = Expressions.makePredicate("S_"+s.getName(), 2);
			datalogRule.add(Expressions.makeRule(Expressions.makePositiveLiteral(IDBPredicateF, t1, t2), Expressions.makePositiveLiteral(IDBPredicateSt, t1, t2)));
		}
		
		// For every transition q -l-> q', we add a rule S_q'(x,z) :- S_q(x,y), TRIPLE(y,l,z)
		final Predicate EDBPredicateB = Expressions.makePredicate("TRIPLE", 3);
		for (Transition tr : ndfa.getTransition()) {
			final Predicate IDBPredicateB = Expressions.makePredicate("S_"+tr.getOrigin(), 2);
			final Predicate IDBPredicateH = Expressions.makePredicate("S_"+tr.getDest(), 2);
			if (tr.getLabel().getName().equals("")) {
				datalogRule.add(Expressions.makeRule(Expressions.makePositiveLiteral(IDBPredicateH, Arrays.asList(x,y)), 
						Expressions.makePositiveLiteral(IDBPredicateB, Arrays.asList(x,y))));
			} else {
				final Term t = Expressions.makeAbstractConstant(tr.getLabel().getName());
				datalogRule.add(Expressions.makeRule(Expressions.makePositiveLiteral(IDBPredicateH, Arrays.asList(x,z)), 
						Expressions.makePositiveLiteral(IDBPredicateB, Arrays.asList(x,y)),
						Expressions.makePositiveLiteral(EDBPredicateB, Arrays.asList(y,t,z))));
			}
		}
		
		for (ConverseTransition tr : ndfa.getConvTransition()) {
			final Predicate IDBPredicateB = Expressions.makePredicate("S_"+tr.getOrigin(), 2);
			final Predicate IDBPredicateH = Expressions.makePredicate("S_"+tr.getDest(), 2);
			final Term t = Expressions.makeAbstractConstant(tr.getLabel().getConverseOf().getName());
			datalogRule.add(Expressions.makeRule(Expressions.makePositiveLiteral(IDBPredicateH, Arrays.asList(x,z)), 
					Expressions.makePositiveLiteral(IDBPredicateB, Arrays.asList(x,y)),
					Expressions.makePositiveLiteral(EDBPredicateB, Arrays.asList(z,t,y))));
		}
		
		return datalogRule;
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from an {@link NDFAQuery}.
	 *
	 * @param ndfa	non-null {@link NDFAQuery}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */	
	public static List<Statement> NDFAQueryTranslate(NDFAQuery ndfa) {
		return translateExpression(ndfa.getNDFA(), ndfa.getTerm1(), ndfa.getTerm2(), false);
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from a C2RPQ.
	 *
	 * @param uvars			non-null List of {@link UniversalVariable}
	 * @param ndfaQueries	non-null List of {@link NDFAQuery}
	 * @param con			non-null {@link Conjunction} of {@link Literal}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */
	public static List<Statement> CNDFATranslate(final List<Term> uvars, final List<NDFAQuery> ndfaQueries, final Conjunction<Literal> con) {
		final List<Literal> literals = new ArrayList<Literal>();
		final List<Statement> datalogRule = new ArrayList<Statement>();
		for (NDFAQuery ndfa : ndfaQueries) {
			Term t1 = ndfa.getTerm1();
			Term t2 = ndfa.getTerm2();
			if (t1.getType() == TermType.EXISTENTIAL_VARIABLE) {
				t1 = Expressions.makeUniversalVariable(t1.getName());
			} 
			if (t2.getType() == TermType.EXISTENTIAL_VARIABLE) {
				t2 = Expressions.makeUniversalVariable(t2.getName());
			}
			RpqConverterUtils.combineProgram(datalogRule, translateExpression(ndfa.getNDFA(), t1, t2, true));
			literals.add(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+ndfa.getNDFA().toString(), 2), Arrays.asList(t1,t2)));
		}
		datalogRule.addAll(RpqConverterUtils.createKBaseRules(Expressions.makePredicate("S_Top", 2)));
		if (con != null) {
			for (Literal literal : con.getLiterals()) {
				List<Term> terms = new ArrayList<>();
				literal.getTerms().forEach(terms::add);
				for (int i = 0; i < terms.size(); i++) {
					if (terms.get(i).getType() == TermType.EXISTENTIAL_VARIABLE) {
						terms.set(i, Expressions.makeUniversalVariable(terms.get(i).getName()));
					}
				}
				literal = Expressions.makePositiveLiteral(literal.getPredicate(), terms);
				literals.add(literal);
			}
		}
		List<Term> uvar = new ArrayList<Term>(uvars);
		datalogRule.add(Expressions.makeRule(
				Expressions.makePositiveConjunction(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", uvars.size()), uvar)), 
				Expressions.makeConjunction(literals)));
		return datalogRule;
	}
	
	/**
	 * Recursively build an {@link NDFiniteAutomata} given a {@link RegExpression}
	 *
	 * @param regex	non-null {@link RegExpression}
	 * @param index	non-null integer as starting index for {@link State}
	 * @return an {@link NDFiniteAutomata} corresponding to the input {@link RegExpression}
	 */
	public static NDFiniteAutomata regex2NFAConverter(RegExpression regex, int index) {
		RegExpressionType type = regex.getType();
		if (type == RegExpressionType.EDGE_LABEL || type == RegExpressionType.CONVERSE_EDGE_LABEL) {
			NDFiniteAutomata ndfa = null;
			State qs = RPQExpressions.makeState("q"+(index));
			State qf = RPQExpressions.makeState("q"+(index+1));
			final Set<Transition> transition = new HashSet<Transition>();
			final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>();
			
			if (type == RegExpressionType.EDGE_LABEL) {
				EdgeLabel alp = (EdgeLabel) regex;
				transition.add(RPQExpressions.makeTransition(qs, qf, alp));
				ndfa = RPQExpressions.makeNDFiniteAutomata(regex, 
						new HashSet<State>(Arrays.asList(qs,qf)), 
						new HashSet<EdgeLabel>(Arrays.asList(alp)), qs, 
						new HashSet<State>(Arrays.asList(qf)), transition, convTransition);
			} else if (type == RegExpressionType.CONVERSE_EDGE_LABEL) {
				ConverseEdgeLabel c = (ConverseEdgeLabel) regex;
				convTransition.add(RPQExpressions.makeConverseTransition(qs, qf, c));
				ndfa = RPQExpressions.makeNDFiniteAutomata(regex, 
						new HashSet<State>(Arrays.asList(qs,qf)), 
						new HashSet<EdgeLabel>(Arrays.asList(c.getConverseOf())), qs, 
						new HashSet<State>(Arrays.asList(qf)), transition, convTransition);
			}
			return ndfa;
		} else if (type == RegExpressionType.ALTERNATION) {
			AlternRegExpression c = (AlternRegExpression) regex;
			State qs = RPQExpressions.makeState("q"+(index));
			State qf = RPQExpressions.makeState("q"+(index+1));

			NDFiniteAutomata nfaLeft = regex2NFAConverter(c.getExp1(), index+2);
			NDFiniteAutomata nfaRight = regex2NFAConverter(c.getExp2(), index+2+nfaLeft.getState().size());
			
			Set<State> states = new HashSet<State>(Arrays.asList(qs,qf));
			states.addAll(nfaLeft.getState());
			states.addAll(nfaRight.getState());
			
			Set<EdgeLabel> alps = new HashSet<EdgeLabel>(nfaLeft.getAlphabet());
			alps.addAll(nfaRight.getAlphabet());
			EdgeLabel emptyString = RPQExpressions.makeEdgeLabel("");
			
			final Set<Transition> transition = new HashSet<Transition>(nfaLeft.getTransition());
			transition.addAll(nfaRight.getTransition());
			transition.add(RPQExpressions.makeTransition(qs, nfaLeft.getInitState(), emptyString));
			transition.add(RPQExpressions.makeTransition(qs, nfaRight.getInitState(), emptyString));
			for (State s : nfaLeft.getFinState()) {
				transition.add(RPQExpressions.makeTransition(s, qf, emptyString));
			}
			for (State s : nfaRight.getFinState()) {
				transition.add(RPQExpressions.makeTransition(s, qf, emptyString));
			}
			
			final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>(nfaLeft.getConvTransition());
			convTransition.addAll(nfaRight.getConvTransition());
			
			NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(regex, states, alps, qs, 
					new HashSet<State>(Arrays.asList(qf)), transition, convTransition);
			
			return ndfa;
		} else if (type == RegExpressionType.CONCATENATION) {
			ConcatRegExpression c = (ConcatRegExpression) regex;
			NDFiniteAutomata nfaLeft = regex2NFAConverter(c.getExp1(), index);
			NDFiniteAutomata nfaRight = regex2NFAConverter(c.getExp2(), index+nfaLeft.getState().size());
			
			Set<State> states = new HashSet<State>(nfaLeft.getState());
			states.addAll(nfaRight.getState());
			
			Set<EdgeLabel> alps = new HashSet<EdgeLabel>(nfaLeft.getAlphabet());
			alps.addAll(nfaRight.getAlphabet());
			EdgeLabel emptyString = RPQExpressions.makeEdgeLabel("");
			
			final Set<Transition> transition = new HashSet<Transition>(nfaLeft.getTransition());
			transition.addAll(nfaRight.getTransition());
			for (State s : nfaLeft.getFinState()) {
				transition.add(RPQExpressions.makeTransition(s, nfaRight.getInitState(), emptyString));
			}
			
			final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>(nfaLeft.getConvTransition());
			convTransition.addAll(nfaRight.getConvTransition());
			
			NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(regex, states, alps, nfaLeft.getInitState(), 
					nfaRight.getFinState(), transition, convTransition);
			
			return ndfa;
		} else if (type == RegExpressionType.KLEENE_STAR) {
			KStarRegExpression c = (KStarRegExpression) regex;
			State qs = RPQExpressions.makeState("q"+(index));
			State qf = RPQExpressions.makeState("q"+(index+1));
			State qd = RPQExpressions.makeState("q"+(index+2));

			NDFiniteAutomata nfaIn = regex2NFAConverter(c.getExp(), index+3);
			
			Set<State> states = new HashSet<State>(Arrays.asList(qs,qf,qd));
			states.addAll(nfaIn.getState());
			
			Set<EdgeLabel> alps = new HashSet<EdgeLabel>(nfaIn.getAlphabet());
			EdgeLabel emptyString = RPQExpressions.makeEdgeLabel("");
			
			final Set<Transition> transition = new HashSet<Transition>(nfaIn.getTransition());
			transition.add(RPQExpressions.makeTransition(qs, nfaIn.getInitState(), emptyString));
			transition.add(RPQExpressions.makeTransition(qs, qf, emptyString));
			transition.add(RPQExpressions.makeTransition(qf, qs, emptyString));
			transition.add(RPQExpressions.makeTransition(qf, qd, emptyString));
			for (State s : nfaIn.getFinState()) {
				transition.add(RPQExpressions.makeTransition(s, qf, emptyString));
			}
			
			final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>(nfaIn.getConvTransition());
			
			NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(regex, states, alps, qs, 
					new HashSet<State>(Arrays.asList(qd)), transition, convTransition);
			
			return ndfa;
		} else if (type == RegExpressionType.KLEENE_PLUS) {
			KPlusRegExpression c = (KPlusRegExpression) regex;
			State qs = RPQExpressions.makeState("q"+(index));
			State qf = RPQExpressions.makeState("q"+(index+1));
			State qd = RPQExpressions.makeState("q"+(index+2));

			NDFiniteAutomata nfaIn = regex2NFAConverter(c.getExp(), index+3);
			
			Set<State> states = new HashSet<State>(Arrays.asList(qs,qf,qd));
			states.addAll(nfaIn.getState());
			
			Set<EdgeLabel> alps = new HashSet<EdgeLabel>(nfaIn.getAlphabet());
			EdgeLabel emptyString = RPQExpressions.makeEdgeLabel("");
			
			final Set<Transition> transition = new HashSet<Transition>(nfaIn.getTransition());
			transition.add(RPQExpressions.makeTransition(qs, nfaIn.getInitState(), emptyString));
			transition.add(RPQExpressions.makeTransition(qf, qs, emptyString));
			transition.add(RPQExpressions.makeTransition(qf, qd, emptyString));
			for (State s : nfaIn.getFinState()) {
				transition.add(RPQExpressions.makeTransition(s, qf, emptyString));
			}
			
			final Set<ConverseTransition> convTransition = new HashSet<ConverseTransition>(nfaIn.getConvTransition());
			
			NDFiniteAutomata ndfa = RPQExpressions.makeNDFiniteAutomata(regex, states, alps, qs, 
					new HashSet<State>(Arrays.asList(qd)), transition, convTransition);
			
			return ndfa;
		} else {
			throw new RpqConvertException(MessageFormat
					.format("Invalid regex operator when converting {0}}.", regex.toString()));
		}
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from a {@link RegPathQuery}.
	 *
	 * @param rpq	non-null {@link RegPathQuery}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */	
	public static List<Statement> RPQTranslate(RegPathQuery rpq) {
		NDFiniteAutomata ndfa = regex2NFAConverter(rpq.getExpression(),0);
		return translateExpression(ndfa, rpq.getTerm1(), rpq.getTerm2(), false);
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from an {@link RPQConjunction}.
	 *
	 * @param uvars		non-null List of {@link UniversalVariable}
	 * @param rpqcon	non-null {@link RPQConjunction}
	 * @param con		non-null {@link Conjunction} of {@link Literal}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */
	public static List<Statement> CRPQTranslate(final List<Term> uvars, final RPQConjunction<RegPathQuery> rpqcon, final Conjunction<Literal> con) {
		List<NDFAQuery> queries = new ArrayList<NDFAQuery>();
		int idx = 0;
		for (RegPathQuery rpq : rpqcon.getRPQs()) {
			NDFiniteAutomata nfa = regex2NFAConverter(rpq.getExpression(),idx);
			idx += nfa.getState().size();
			queries.add(RPQExpressions.makeNDFAQuery(nfa, rpq.getTerm1(), rpq.getTerm2()));
		}
		return CNDFATranslate(uvars, queries, con);
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from a {@link RegPathQuery} with simplified {@link NDFiniteAutomata}.
	 *
	 * @param rpq	non-null {@link RegPathQuery}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */	
	public static List<Statement> RPQTranslateAlt(RegPathQuery rpq) {
		NDFiniteAutomata ndfa = RpqConverterUtils.simplify(regex2NFAConverter(rpq.getExpression(),0));
		return translateExpression(ndfa, rpq.getTerm1(), rpq.getTerm2(), false);
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from an {@link RPQConjunction} with simplified {@link NDFiniteAutomata}.
	 *
	 * @param uvars		non-null List of {@link UniversalVariable}
	 * @param rpqcon	non-null {@link RPQConjunction}
	 * @param con		non-null {@link Conjunction} of {@link Literal}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */
	public static List<Statement> CRPQTranslateAlt(final List<Term> uvars, final RPQConjunction<RegPathQuery> rpqcon, final Conjunction<Literal> con) {
		List<NDFAQuery> queries = new ArrayList<NDFAQuery>();
		int idx = 0;
		for (RegPathQuery rpq : rpqcon.getRPQs()) {
			NDFiniteAutomata nfaInit = regex2NFAConverter(rpq.getExpression(),idx);
			NDFiniteAutomata nfa = RpqConverterUtils.simplify(nfaInit);
			idx += nfaInit.getState().size();
			queries.add(RPQExpressions.makeNDFAQuery(nfa, rpq.getTerm1(), rpq.getTerm2()));
		}
		return CNDFATranslate(uvars, queries, con);
	}
}
