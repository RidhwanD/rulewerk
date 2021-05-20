package org.semanticweb.rulewerk.rpq.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.State;
import org.semanticweb.rulewerk.rpq.model.api.Transition;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;

public class RpqConverterUtils {
	/**
	 * Creates a set of facts as {@link Statement} from a given {@link Predicates} and a list of terms.
	 *
	 * @param IDBpredicate 	non-null {@link Predicate}
	 * @param terms			non-null list of {@link Term} that are constants from the knowledge base 
	 * @return a set of {@link Statement} of facts of the form IDBpredicate(t,t) corresponding to the input
	 */
	public static Set<Statement> produceFacts(final Predicate IDBpredicate, final Set<Term> terms) {
		final Set<Statement> datalogRule = new HashSet<Statement>();
		for (Term t : terms) {
			datalogRule.add(Expressions.makeFact(IDBpredicate, t, t));
		}
		return datalogRule;
	}
	
	/**
	 * Creates a set of base rules for {@link KStarRegExpression} from a given {@link Predicates}.
	 *
	 * @param IDBpredicate 	non-null {@link Predicate} 
	 * @return a set of {@link Statement} of rules corresponding to the input.
	 */
	public static Set<Rule> createKBaseRules(final Predicate IDBpredicate) {
		final Set<Rule> datalogRule = new HashSet<Rule>();
		final Predicate EDBpredicate = Expressions.makePredicate("TRIPLE", 3);

		final Variable x = Expressions.makeUniversalVariable("x");
		final Variable y = Expressions.makeUniversalVariable("y");
		final Variable z = Expressions.makeUniversalVariable("z");
		
		datalogRule.add(Expressions.makeRule(
				Expressions.makePositiveLiteral(IDBpredicate, Arrays.asList(x,x)),
				Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(x,y,z))));
		datalogRule.add(Expressions.makeRule(
				Expressions.makePositiveLiteral(IDBpredicate, Arrays.asList(x,x)),
				Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(z,y,x))));
		return datalogRule;
	}
	
	/**
	 * Creates a set of {@link Term} from a given {@link KnowledgeBase}.
	 *
	 * @param kb 	non-null {@link KnowledgeBase}
	 * @return a set of {@link Term} that appear in the facts of the input
	 */
	public static Set<Term> getTermFromKB(final KnowledgeBase kb) {
		Set<Term> terms = new HashSet<>(); 
		for (Fact f : kb.getFacts()) {
			f.getTerms().forEach(terms::add);
		}
		return terms;
	}
	
	/**
	 * Find the source and destination of a transition from a {@link State} in a set of {@link Transition} if the state is removable, i.e., only have epsilon transition from and to it.
	 *
	 * @param s 		non-null {@link State}
	 * @param trans 	non-null set of {@link Transition}
	 * @param ctrans 	non-null set of {@link ConverseTransition}
	 * @return a map containing two set of {@link State}, with key 0 for sources and key 1 for destinations if removable, null otherwise.
	 */
	public static Map<Integer, Set<State>> getInOut(State s, Set<Transition> trans, Set<ConverseTransition> ctrans) {
		Set<State> orig = new HashSet<State>();
		Set<State> dest = new HashSet<State>();
		Map<Integer, Set<State>> res = new HashMap<Integer, Set<State>>();
		
		for (Transition t : trans) {
			if (t.getOrigin().equals(s) && !t.getLabel().getName().equals("")) return null;
			if (t.getDest().equals(s) && !t.getLabel().getName().equals("")) return null;
			if (t.getOrigin().equals(s) && t.getLabel().getName().equals("")) dest.add(t.getDest());
			if (t.getDest().equals(s) && t.getLabel().getName().equals("")) orig.add(t.getOrigin());
		}
		
		for (ConverseTransition t : ctrans) {
			if (t.getOrigin().equals(s) || t.getDest().equals(s)) return null;
		}
		res.put(0, orig);
		res.put(1, dest);
		return res;
	}
	
	/**
	 * Check if a {@link Transition} exists in a set of {@link Transition}.
	 *
	 * @param trans 	non-null set of {@link Transition}
	 * @param tran	 	non-null {@link Transition}
	 * @return true if it exists, false otherwise.
	 */
	public static boolean isTranExist(Set<Transition> trans, Transition tran) {
		for (Transition t : trans) {
			if (t.equals(tran)) return true;
		}
		return false;
	}
	
	/**
	 * Simplify an {@link NDFiniteAutomata} based on removable {@link State}.
	 *
	 * @param ndfa 	non-null set of {@link NDFiniteAutomata}
	 * @return a simplified {@link NDFiniteAutomata}.
	 */
	public static NDFiniteAutomata simplify(NDFiniteAutomata ndfa) {
		Set<State> stateCopy = new HashSet<State>();
		for (State s : ndfa.getState()) {
			stateCopy.add(s);
		}
		Set<Transition> transCopy = new HashSet<Transition>();
		for (Transition t : ndfa.getTransition()) {
			transCopy.add(t);
		}
		EdgeLabel epsilon = RPQExpressions.makeEdgeLabel("");
		
		while (true) {
			boolean iterate = false;
			Set<State> removable = new HashSet<State>();
			
			for (State s : stateCopy) {
				if (!s.equals(ndfa.getInitState()) && !ndfa.getFinState().contains(s)) {
					Map<Integer, Set<State>> inOut = getInOut(s, transCopy, ndfa.getConvTransition());
					if (inOut != null) {
						Set<Transition> newTrans = new HashSet<Transition>();
						iterate = true;
						removable.add(s);
						// Move all epsilon transition.
						for (State orig : inOut.get(0)) {
							for (State dest : inOut.get(1)) {
								if (!orig.equals(s) && !dest.equals(s)) {
									Transition t = RPQExpressions.makeTransition(orig, dest, epsilon);
									if (!isTranExist(transCopy,t) && !isTranExist(newTrans,t))
										newTrans.add(t);
								}
							}
						}
						for (Transition t : transCopy) {
							if (!t.getOrigin().equals(s) && ! t.getDest().equals(s) && !isTranExist(newTrans,t)) {
								newTrans.add(t);
							} else {
							}
						}
						transCopy = newTrans;
					}
				}
			}
			for (State s : removable) stateCopy.remove(s);
			if (!iterate) break;
		}
		
		NDFiniteAutomata nndfa = RPQExpressions.makeNDFiniteAutomata(ndfa.getRegex(), stateCopy, ndfa.getAlphabet(), ndfa.getInitState(), ndfa.getFinState(), transCopy, ndfa.getConvTransition());
		return nndfa;
	}
	
	/**
	 * Combine two set of Datalog {@link Statement}.
	 *
	 * @param prog1		non-null List of {@link Statement}
	 * @param prog2		non-null List of {@link Statement}
	 * @return a List of {@link Statement} of Datalog rules without repetition.
	 */
	public static List<Statement> combineProgram(List<Statement> prog1, List<Statement> prog2) {
		for (Statement s : prog2) {
			if (!prog1.contains(s))
				prog1.add(s);
		}
		return prog1;
	}
}
