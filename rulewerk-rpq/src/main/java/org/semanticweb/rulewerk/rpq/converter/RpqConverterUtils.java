package org.semanticweb.rulewerk.rpq.converter;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.ConverseTransition;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomata;
import org.semanticweb.rulewerk.rpq.model.api.NDFiniteAutomataAlt;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegExpressionType;
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
//	public static Set<Statement> produceFacts(final Predicate IDBpredicate, final Set<Term> terms) {
//		final Set<Statement> datalogRule = new HashSet<Statement>();
//		for (Term t : terms) {
//			datalogRule.add(Expressions.makeFact(IDBpredicate, t, t));
//		}
//		return datalogRule;
//	}
	public static Set<Rule> produceFacts(final Predicate IDBpredicate, final Set<Term> terms) {
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
				Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(y,x,z))));
		datalogRule.add(Expressions.makeRule(
				Expressions.makePositiveLiteral(IDBpredicate, Arrays.asList(x,x)),
				Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(z,y,x))));
		return datalogRule;
	}
	
	public static Set<Rule> produceFacts(final Predicate IDBpredicate) {
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
				Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(y,x,z))));
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
	
	public static RegExpression optimizeKStar(RegExpression exp) {
		if (exp.getType() == RegExpressionType.EDGE_LABEL || exp.getType() == RegExpressionType.CONVERSE_EDGE_LABEL) {
			return exp;
		} else if (exp.getType() == RegExpressionType.KLEENE_STAR) {
			KStarRegExpression c = (KStarRegExpression) exp;
			RegExpression newRE = null;
			if (c.getExp().getType() == RegExpressionType.KLEENE_STAR) {
				KStarRegExpression d = (KStarRegExpression) c.getExp();
				newRE = optimizeKStar(d.getExp());
			} else if (c.getExp().getType() == RegExpressionType.ALTERNATION) {
				AlternRegExpression d = (AlternRegExpression) c.getExp();
				boolean simplify = false;
				RegExpression newRE1 = d.getExp1();
				RegExpression newRE2 = d.getExp2();
				if (d.getExp1().getType() == RegExpressionType.KLEENE_STAR) {
					simplify = true;
					KStarRegExpression e = (KStarRegExpression) d.getExp1();
					newRE1 = optimizeKStar(e.getExp());
				}
				if (d.getExp2().getType() == RegExpressionType.KLEENE_STAR) {
					simplify = true;
					KStarRegExpression e = (KStarRegExpression) d.getExp2();
					newRE2 = optimizeKStar(e.getExp());
				}
				if (simplify) {
					newRE = RPQExpressions.makeAlternRegExpression(newRE1, newRE2);
				} else {
					newRE = optimizeKStar(c.getExp());
				}
			} else if (c.getExp().getType() == RegExpressionType.CONCATENATION) {
				ConcatRegExpression d = (ConcatRegExpression) c.getExp();
				RegExpression newRE1 = d.getExp1();
				RegExpression newRE2 = d.getExp2();
				if (d.getExp1().getType() == RegExpressionType.KLEENE_STAR && d.getExp2().getType() == RegExpressionType.KLEENE_STAR) {
					KStarRegExpression e = (KStarRegExpression) d.getExp1();
					KStarRegExpression f = (KStarRegExpression) d.getExp2();
					newRE1 = optimizeKStar(e.getExp());
					newRE2 = optimizeKStar(f.getExp());
					newRE = RPQExpressions.makeAlternRegExpression(newRE1, newRE2);
				} else {
					newRE = optimizeKStar(c.getExp());
				}
			} else {
				newRE = optimizeKStar(c.getExp());
			}
			c.setExp(newRE);
			return c;
		} else if (exp.getType() == RegExpressionType.KLEENE_PLUS) {
			KPlusRegExpression c = (KPlusRegExpression) exp;
			RegExpression newRE = optimizeKStar(c.getExp());
			c.setExp(newRE);
			return c;
		} else if (exp.getType() == RegExpressionType.ALTERNATION) {
			AlternRegExpression c = (AlternRegExpression) exp;
			RegExpression newRE1 = optimizeKStar(c.getExp1());
			RegExpression newRE2 = optimizeKStar(c.getExp2());
			c.setExp1(newRE1);
			c.setExp1(newRE2);
			return c;
		} else if (exp.getType() == RegExpressionType.CONCATENATION) {
			ConcatRegExpression c = (ConcatRegExpression) exp;
			RegExpression newRE1 = optimizeKStar(c.getExp1());
			RegExpression newRE2 = optimizeKStar(c.getExp2());
			c.setExp1(newRE1);
			c.setExp1(newRE2);
			return c;
		} else {
			throw new RpqConvertException(MessageFormat
					.format("Invalid regex operator when converting {0}}.", exp.toString()));
		}
	}
	
	public static NDFiniteAutomataAlt convertToAlt(NDFiniteAutomata ndfa) {
		Set<Transition> transitions = new HashSet<Transition>();
		for (State key : ndfa.getTransition().keySet()) {
			for (EdgeLabel el : ndfa.getTransition().get(key).keySet()) {
				for (State s : ndfa.getTransition().get(key).get(el)) {
					transitions.add(RPQExpressions.makeTransition(key, s, el));
				}
			}
		}
		Set<ConverseTransition> convTransitions = new HashSet<ConverseTransition>();
		for (State key : ndfa.getConvTransition().keySet()) {
			for (ConverseEdgeLabel el : ndfa.getConvTransition().get(key).keySet()) {
				for (State s : ndfa.getConvTransition().get(key).get(el)) {
					convTransitions.add(RPQExpressions.makeConverseTransition(key, s, el));
				}
			}
		}
		NDFiniteAutomataAlt nndfa = RPQExpressions.makeNDFiniteAutomataAlt(ndfa.getRegex(), ndfa.getState(), ndfa.getAlphabet(), ndfa.getInitState(), ndfa.getFinState(), transitions, convTransitions);
		return nndfa;
	}
	
//	public boolean checkStateRem(State s, Set<Transition> transition) {
//		for (Transition t : transition) {
//			if (t.getOrigin().equals(s) && !t.getLabel().getName().equals("")) return false;
//			if (t.getDest().equals(s) && !t.getLabel().getName().equals("")) return false;
//		}
//		return true;
//	}
	
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
	
	public static boolean isExist(Set<Transition> trans, Transition tran) {
		for (Transition t : trans) {
			if (t.equals(tran)) return true;
		}
		return false;
	}
	
	public static NDFiniteAutomataAlt simplify(NDFiniteAutomataAlt ndfa) {
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
									if (!isExist(transCopy,t) && !isExist(newTrans,t))
										newTrans.add(t);
								}
							}
						}
						for (Transition t : transCopy) {
							if (!t.getOrigin().equals(s) && ! t.getDest().equals(s) && !isExist(newTrans,t)) {
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
		
		NDFiniteAutomataAlt nndfa = RPQExpressions.makeNDFiniteAutomataAlt(ndfa.getRegex(), stateCopy, ndfa.getAlphabet(), ndfa.getInitState(), ndfa.getFinState(), transCopy, ndfa.getConvTransition());
		return nndfa;
	}
}
