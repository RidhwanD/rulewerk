package org.semanticweb.rulewerk.rpq.model.implementation;

import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Term;
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
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;

public class RPQExpressions {

	
	/**
	 * Creates an {@link EdgeLabel} and its {@link CoverseEdgeLabel}.
	 *
	 * @param name non-blank {@link EdgeLabel} name
	 * @return a list of Object containing {@link EdgeLabel} and {@link ConverseEdgeLabel} corresponding to the input
	 */
	public static Object[] makeEdgeLabelAndConverse(final String name) {
		EdgeLabelImpl el = new EdgeLabelImpl(name);
		return new Object[]{el, new ConverseEdgeLabelImpl(el)};
	}
	
	/**
	 * Creates an {@link EdgeLabel}.
	 *
	 * @param name non-blank {@link EdgeLabel} name
	 * @return an {@link EdgeLabel} corresponding to the input
	 */
	public static EdgeLabel makeEdgeLabel(final String name) {
		return new EdgeLabelImpl(name);
	}
	
	/**
	 * Creates a {@link ConverseEdgeLabel}.
	 *
	 * @param name non-null {@link EdgeLabel}
	 * @return a {@link ConverseEdgeLabel} corresponding to the input
	 */
	public static ConverseEdgeLabel makeConverseEdgeLabel(final EdgeLabel el) {
		return new ConverseEdgeLabelImpl(el);
	}
	
	/**
	 * Creates a {@link ConcatRegExpression}.
	 *
	 * @param exp1 non-null {@link RegExpression} for the left part of concatenation
	 * @param exp2 non-null {@link RegExpression} for the right part of concatenation
	 * @return a {@link ConcatRegExpression} of exp1 and exp2
	 */
	public static ConcatRegExpression makeConcatRegExpression(final RegExpression exp1, final RegExpression exp2) {
		return new ConcatRegExpressionImpl(exp1, exp2);

	}
	
	/**
	 * Creates an {@link AlternRegExpression}.
	 *
	 * @param exp1 non-null {@link RegExpression} for the left part of alternation
	 * @param exp2 non-null {@link RegExpression} for the right part of alternation
	 * @return a {@link AlternRegExpression} of exp1 and exp2
	 */
	public static AlternRegExpression makeAlternRegExpression(final RegExpression exp1, final RegExpression exp2) {
		return new AlternRegExpressionImpl(exp1, exp2);

	}
	
	/**
	 * Creates a {@link KStarRegExpression}.
	 *
	 * @param exp non-null {@link RegExpression}
	 * @return a {@link KStarRegExpression} of exp
	 */
	public static KStarRegExpression makeKStarRegExpression(final RegExpression exp) {
		return new KStarRegExpressionImpl(exp);

	}
	
	/**
	 * Creates a {@link KPlusRegExpression}.
	 *
	 * @param exp non-null {@link RegExpression}
	 * @return a {@link KPlusRegExpression} of exp
	 */
	public static KPlusRegExpression makeKPlusRegExpression(final RegExpression exp) {
		return new KPlusRegExpressionImpl(exp);

	}
	
	/**
	 * Creates a {@link RegPathQuery}.
	 *
	 * @param exp non-null {@link RegExpression}
	 * @param t1 non-null {@link Term}
	 * @param t2 non-null {@link Term}
	 * @return a {@link RegPathQuery} corresponding to the input
	 */
	public static RegPathQuery makeRegPathQuery(final RegExpression exp, final Term t1, final Term t2) {
		return new RegPathQueryImpl(exp, t1, t2);
		
	}
	
	/**
	 * Creates a {@link RPQConjunction} of {@code T} ({@link RegPathQuery} type) objects.
	 *
	 * @param literals list of non-null rpqs
	 * @return a {@link RPQConjunction} corresponding to the input
	 */
	public static <T extends RegPathQuery> RPQConjunction<T> makeRPQConjunction(final List<T> rpqs, final List<Term> vars) {
		return new RPQConjunctionImpl<>(rpqs, vars);
		
	}
	
	public static State makeState(String name) {
		return new StateImpl(name);
	}
	
	public static Transition makeTransition(State origin, State destination, EdgeLabel label) {
		return new TransitionImpl(origin, destination, label);
	}
	
	public static ConverseTransition makeConverseTransition(State origin, State destination, ConverseEdgeLabel label) {
		return new ConverseTransitionImpl(origin, destination, label);
	}
	
	public static NDFiniteAutomata makeNDFiniteAutomata(RegExpression regex, Set<State> states, Set<EdgeLabel> alphabet, State initState, Set<State> finState, Set<Transition> transition, Set<ConverseTransition> convTransition) {
		return new NDFiniteAutomataImpl(regex, states, alphabet, initState, finState, transition, convTransition);
	}
	
	public static NDFAQuery makeNDFAQuery(NDFiniteAutomata ndfa, Term t1, Term t2) {
		return new NDFAQueryImpl(ndfa, t1, t2);
	}
}
