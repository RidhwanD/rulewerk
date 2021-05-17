package org.semanticweb.rulewerk.rpq.converter;

import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.text.MessageFormat;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegExpressionType;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

public final class RpqConverter {
	/**
	 * Creates a set of Datalog {@link Statement} from a {@link RegPathQuery}.
	 *
	 * @param exp 	non-null {@link RegExpression} of the RPQ
	 * @param v1 	non-null {@link Term} from the RPQ 
	 * @param v2 	non-null {@link Term} from the RPQ 
	 * @param terms	non-null list of {@link Term} that are constants from the knowledge base 
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */
	public static List<Statement> translateExpression(final RegExpression exp, final Term v1, final Term v2, final Set<Term> terms) {
		if (exp.getType() == RegExpressionType.EDGE_LABEL || exp.getType() == RegExpressionType.CONVERSE_EDGE_LABEL) {
			final List<Statement> datalogRule = new ArrayList<Statement>();
			final Predicate IDBpredicate = Expressions.makePredicate("Q_"+exp.getName(), 2);
			if (exp.getType() == RegExpressionType.EDGE_LABEL) {
				final Term term = Expressions.makeAbstractConstant(exp.getName());
				final Predicate EDBpredicate = Expressions.makePredicate("TRIPLE", 3);
				datalogRule.add(Expressions.makeRule(
						Expressions.makePositiveLiteral(IDBpredicate, Arrays.asList(v1,v2)),
						Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(v1,term,v2))));
			} else if (exp.getType() == RegExpressionType.CONVERSE_EDGE_LABEL) {
				final ConverseEdgeLabel cexp = (ConverseEdgeLabel) exp;
				final Term term = Expressions.makeAbstractConstant(cexp.getConverseOf().getName());
				final Predicate EDBpredicate = Expressions.makePredicate("TRIPLE", 3);
				datalogRule.add(Expressions.makeRule(
						Expressions.makePositiveLiteral(IDBpredicate, Arrays.asList(v1,v2)),
						Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(v2,term,v1))));
			}
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.CONCATENATION) {
			ConcatRegExpression expc = (ConcatRegExpression) exp; 
			Variable var = Expressions.makeUniversalVariable("x1");
			int i = 1;
			while (var.equals(v1) || var.equals(v2)) {
				i++;
				var = Expressions.makeUniversalVariable("x"+String.valueOf(i));
			}
			final Variable x = Expressions.makeUniversalVariable(var.getName());
        	final List<Statement> listOne = translateExpression(expc.getExp1(), v1, x, terms);
			final List<Statement> listTwo = translateExpression(expc.getExp2(), x, v2, terms);
			final List<Statement> datalogRule = Stream.concat(listOne.stream(), listTwo.stream())
                    .collect(Collectors.toList());
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp1().getName(), 2);
			final Predicate IDBpredicate3 = Expressions.makePredicate("Q_"+expc.getExp2().getName(), 2);
			datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,v2)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(v1,x)),
					Expressions.makePositiveLiteral(IDBpredicate3, Arrays.asList(x,v2))));
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.ALTERNATION) {
			AlternRegExpression expc = (AlternRegExpression) exp;
			final List<Statement> listOne = translateExpression(expc.getExp1(), v1, v2, terms);
			final List<Statement> listTwo = translateExpression(expc.getExp2(), v1, v2, terms);
			final List<Statement> datalogRule = Stream.concat(listOne.stream(), listTwo.stream())
                    .collect(Collectors.toList());
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp1().getName(), 2);
			final Predicate IDBpredicate3 = Expressions.makePredicate("Q_"+expc.getExp2().getName(), 2);
        	datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,v2)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(v1,v2))));
        	datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,v2)),
					Expressions.makePositiveLiteral(IDBpredicate3, Arrays.asList(v1,v2))));
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.KLEENE_STAR) {
			KStarRegExpression expc = (KStarRegExpression) exp;
			final List<Statement> datalogRule = translateExpression(expc.getExp(), v1, v2, terms);
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp().getName(), 2);
			Variable var = Expressions.makeUniversalVariable("x1");
			int i = 1;
			while (var.equals(v1) || var.equals(v2)) {
				i++;
				var = Expressions.makeUniversalVariable("x"+String.valueOf(i));
			}
			final Variable x = Expressions.makeUniversalVariable(var.getName());
        	datalogRule.addAll(RpqConverterUtils.produceFacts(IDBpredicate1, terms));
        	datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,v2)),
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,x)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(x,v2))));
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.KLEENE_PLUS) {
			KPlusRegExpression expc = (KPlusRegExpression) exp;
			final List<Statement> datalogRule = translateExpression(expc.getExp(), v1, v2, terms);
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp().getName(), 2);
			Variable var = Expressions.makeUniversalVariable("x1");
			int i = 1;
			while (var.equals(v1) || var.equals(v2)) {
				i++;
				var = Expressions.makeUniversalVariable("x"+String.valueOf(i));
			}
			final Variable x = Expressions.makeUniversalVariable(var.getName());
			datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,v2)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(v1,v2))));
			datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,v2)),
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(v1,x)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(x,v2))));
			return datalogRule;
		} else {
			throw new RpqConvertException(MessageFormat
					.format("Invalid regex operator when converting {0}}.", exp.toString()));
		}
	}
	
	public static List<Statement> translateExpression(final RegExpression exp) {
		final Variable x = Expressions.makeUniversalVariable("x");
		final Variable y = Expressions.makeUniversalVariable("y");
		final Variable z = Expressions.makeUniversalVariable("z");
		
		if (exp.getType() == RegExpressionType.EDGE_LABEL || exp.getType() == RegExpressionType.CONVERSE_EDGE_LABEL) {
			final List<Statement> datalogRule = new ArrayList<Statement>();
			final Predicate IDBpredicate = Expressions.makePredicate("Q_"+exp.getName(), 2);
			if (exp.getType() == RegExpressionType.EDGE_LABEL) {
				final Term term = Expressions.makeAbstractConstant(exp.getName());
				final Predicate EDBpredicate = Expressions.makePredicate("TRIPLE", 3);
				datalogRule.add(Expressions.makeRule(
						Expressions.makePositiveLiteral(IDBpredicate, Arrays.asList(x,y)),
						Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(x,term,y))));
			} else if (exp.getType() == RegExpressionType.CONVERSE_EDGE_LABEL) {
				final ConverseEdgeLabel cexp = (ConverseEdgeLabel) exp;
				final Term term = Expressions.makeAbstractConstant(cexp.getConverseOf().getName());
				final Predicate EDBpredicate = Expressions.makePredicate("TRIPLE", 3);
				datalogRule.add(Expressions.makeRule(
						Expressions.makePositiveLiteral(IDBpredicate, Arrays.asList(x,y)),
						Expressions.makePositiveLiteral(EDBpredicate, Arrays.asList(y,term,x))));
			}
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.CONCATENATION) {
			ConcatRegExpression expc = (ConcatRegExpression) exp; 
			final List<Statement> listOne = translateExpression(expc.getExp1());
			final List<Statement> listTwo = translateExpression(expc.getExp2());
			final List<Statement> datalogRule = Stream.concat(listOne.stream(), listTwo.stream())
                    .collect(Collectors.toList());
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp1().getName(), 2);
			final Predicate IDBpredicate3 = Expressions.makePredicate("Q_"+expc.getExp2().getName(), 2);
			datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,z)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(x,y)),
					Expressions.makePositiveLiteral(IDBpredicate3, Arrays.asList(y,z))));
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.ALTERNATION) {
			AlternRegExpression expc = (AlternRegExpression) exp;
			final List<Statement> listOne = translateExpression(expc.getExp1());
			final List<Statement> listTwo = translateExpression(expc.getExp2());
			final List<Statement> datalogRule = Stream.concat(listOne.stream(), listTwo.stream())
                    .collect(Collectors.toList());
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp1().getName(), 2);
			final Predicate IDBpredicate3 = Expressions.makePredicate("Q_"+expc.getExp2().getName(), 2);
        	datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,y)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(x,y))));
        	datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,y)),
					Expressions.makePositiveLiteral(IDBpredicate3, Arrays.asList(x,y))));
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.KLEENE_STAR) {
			KStarRegExpression expc = (KStarRegExpression) exp;
			final List<Statement> datalogRule = translateExpression(expc.getExp());
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp().getName(), 2);
			datalogRule.addAll(RpqConverterUtils.produceFacts(IDBpredicate1));
        	datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,z)),
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,y)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(y,z))));
			return datalogRule;
		} else if (exp.getType() == RegExpressionType.KLEENE_PLUS) {
			KPlusRegExpression expc = (KPlusRegExpression) exp;
			final List<Statement> datalogRule = translateExpression(expc.getExp());
			final Predicate IDBpredicate1 = Expressions.makePredicate("Q_"+expc.getName(), 2);
			final Predicate IDBpredicate2 = Expressions.makePredicate("Q_"+expc.getExp().getName(), 2);
			datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,y)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(x,y))));
			datalogRule.add(Expressions.makeRule(
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,z)),
					Expressions.makePositiveLiteral(IDBpredicate1, Arrays.asList(x,y)),
					Expressions.makePositiveLiteral(IDBpredicate2, Arrays.asList(y,z))));
			return datalogRule;
		} else {
			throw new RpqConvertException(MessageFormat
					.format("Invalid regex operator when converting {0}}.", exp.toString()));
		}
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from a 2RPQ{@link RegPathQuery}.
	 *
	 * @param query non-null {@link RegPathQuery}
	 * @param kb 	non-null {@link KnowledgeBase}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */
	public static List<Statement> RPQTranslate(final RegPathQuery query, final KnowledgeBase kb) {
		Set<Term> terms = RpqConverterUtils.getTermFromKB(kb);
		return translateExpression(query.getExpression(), query.getTerm1(), query.getTerm2(), terms);
	}
	
	/**
	 * Creates a set of Datalog {@link Statement} from a C2RPQ.
	 *
	 * @param uvars		non-null List of {@link UniversalVariable}
	 * @param rpqcon	non-null {@link RPQConjunction} of {@link RegPathQuery}
	 * @param con		{@link Conjunction} of {@link Literal}
	 * @param kb 		non-null {@link KnowledgeBase}
	 * @return a set of {@link Statement} of Datalog rules corresponding to the input
	 */
	public static List<Statement> CRPQTranslate(final List<Term> uvars, final RPQConjunction<RegPathQuery> rpqcon, final Conjunction<Literal> con, final KnowledgeBase kb) {
//		final Set<Term> kbterms = RpqConverterUtils.getTermFromKB(kb);
		final List<Literal> literals = new ArrayList<Literal>();
		final List<Statement> datalogRule = new ArrayList<Statement>();
		for (RegPathQuery rpq : rpqcon.getRPQs()) {
			Term t1 = rpq.getTerm1();
			Term t2 = rpq.getTerm2();
			if (t1.getType() == TermType.EXISTENTIAL_VARIABLE) {
				t1 = Expressions.makeUniversalVariable(t1.getName());
			} 
			if (t2.getType() == TermType.EXISTENTIAL_VARIABLE) {
				t2 = Expressions.makeUniversalVariable(t2.getName());
			}
//			datalogRule.addAll(translateExpression(rpq.getExpression(), t1, t2, kbterms));
			datalogRule.addAll(translateExpression(rpq.getExpression()));
			literals.add(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+rpq.getExpression().getName(), 2), Arrays.asList(t1,t2)));
		}
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
}
