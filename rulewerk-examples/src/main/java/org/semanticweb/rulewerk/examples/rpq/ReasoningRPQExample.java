package org.semanticweb.rulewerk.examples.rpq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.rpq.converter.RpqConverter;
import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConverseEdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

public class ReasoningRPQExample {
	public static void main(String[] arg) throws IOException {
		ReasoningUtils.configureLogging(); // use simple logger for the example
		
		final UniversalVariable x = Expressions.makeUniversalVariable("X");
		final UniversalVariable y = Expressions.makeUniversalVariable("Y");
		final ExistentialVariable z = Expressions.makeExistentialVariable("Z");
		final Constant c = Expressions.makeAbstractConstant("C");
		final Constant const1 = Expressions.makeAbstractConstant("1");
		final Constant const2 = Expressions.makeAbstractConstant("2");
		final Constant const3 = Expressions.makeAbstractConstant("3");
		final Constant const4 = Expressions.makeAbstractConstant("4");
		final Constant const5 = Expressions.makeAbstractConstant("5");
		
		/////////////////////////////////////////////////
		
		final EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
		final EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
		final EdgeLabel d = RPQExpressions.makeEdgeLabel("d");
		final ConverseEdgeLabel conv_a = RPQExpressions.makeConverseEdgeLabel(RPQExpressions.makeEdgeLabel("a"));
		final AlternRegExpression dab = RPQExpressions.makeAlternRegExpression(d, b);
		final KStarRegExpression ks = RPQExpressions.makeKStarRegExpression(a);
		final ConcatRegExpression acd = RPQExpressions.makeConcatRegExpression(a, d);
		final KPlusRegExpression kp = RPQExpressions.makeKPlusRegExpression(b);
		
		final RegPathQuery Q1 = RPQExpressions.makeRegPathQuery(d, c, x);
		final RegPathQuery Q2 = RPQExpressions.makeRegPathQuery(conv_a, x, y);
		final RegPathQuery Q3 = RPQExpressions.makeRegPathQuery(dab, c, y);
		final RegPathQuery Q4 = RPQExpressions.makeRegPathQuery(acd, x, y);
		final RegPathQuery Q5 = RPQExpressions.makeRegPathQuery(ks, x, y);	
		final RegPathQuery Q6 = RPQExpressions.makeRegPathQuery(kp, x, y);	
		final RegPathQuery Q7 = RPQExpressions.makeRegPathQuery(dab, x, z);
		
		final List<Term> uvars = new ArrayList<Term>(Arrays.asList(x));
		final RPQConjunction<RegPathQuery> conj = RPQExpressions.makeRPQConjunction(Arrays.asList(Q4, Q7), uvars);
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral(Expressions.makePredicate("Check", 1), Arrays.asList(z)));
		
		final KnowledgeBase kb = new KnowledgeBase();
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const2)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("d"),const3)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const4)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("d"),c)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("b"),c)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("b"),const2)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const4,Expressions.makeAbstractConstant("d"),const2)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(c,Expressions.makeAbstractConstant("d"),const4)));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("Check", 1), const1));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("Check", 1), const2));
		kb.addStatement(Expressions.makeFact(Expressions.makePredicate("Check", 1), const4));
		
		/////////////////////////////////////////////////
		
		final KStarRegExpression as = RPQExpressions.makeKStarRegExpression(a);
		final KStarRegExpression bs = RPQExpressions.makeKStarRegExpression(b);
		final AlternRegExpression db = RPQExpressions.makeAlternRegExpression(d, b);
		final ConcatRegExpression c1 = RPQExpressions.makeConcatRegExpression(as, db);
		final ConcatRegExpression c2 = RPQExpressions.makeConcatRegExpression(c1, a);
		final ConcatRegExpression c3 = RPQExpressions.makeConcatRegExpression(c2, bs);
		final RegPathQuery Q8 = RPQExpressions.makeRegPathQuery(c3, x, y);	
		
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
		final RegPathQuery Q9 = RPQExpressions.makeRegPathQuery(c4s, x, const2);
		
		final KnowledgeBase kb2 = new KnowledgeBase();
		kb2.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const3)));
		kb2.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const3)));
		kb2.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const5,Expressions.makeAbstractConstant("a"),const4)));
		
		/////////////////////////////////////////////////
		
		final EdgeLabel n3 = RPQExpressions.makeEdgeLabel("n3");
		final KStarRegExpression n3s = RPQExpressions.makeKStarRegExpression(n3);
		final ConcatRegExpression c5 = RPQExpressions.makeConcatRegExpression(n3, n3s);
		final RegPathQuery Q10 = RPQExpressions.makeRegPathQuery(c5, x, y);
		
		final EdgeLabel n8 = RPQExpressions.makeEdgeLabel("n8");
		final KStarRegExpression n8s = RPQExpressions.makeKStarRegExpression(n8);
		final ConcatRegExpression c6 = RPQExpressions.makeConcatRegExpression(n8, n8s);
		final RegPathQuery Q11 = RPQExpressions.makeRegPathQuery(c6, x, y);
		
		final KnowledgeBase kb3 = new KnowledgeBase();
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("n3"),const3)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("n3"),const3)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("n8"),const3)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("n8"),const4)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("n3"),const4)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("n8"),const4)));
		kb3.addStatement(Expressions.makeFact(Expressions.makePredicate("Check", 1), const1));
		
		final List<Term> uvars2 = new ArrayList<Term>(Arrays.asList(x, y));
		final RPQConjunction<RegPathQuery> conj2 = RPQExpressions.makeRPQConjunction(Arrays.asList(Q10, Q11), Arrays.asList(x, y));
		final Conjunction<Literal> conjunct2 = Expressions.makeConjunction(Expressions.makePositiveLiteral(Expressions.makePredicate("Check", 1), Arrays.asList(x)));
		
		/////////////////////////////////////////////////

//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q1, kb);
		
//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q2, kb);
		
//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q3, kb);
		
//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q4, kb);
		
//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q5, kb);
		
//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q6, kb);
//		
//		for (Statement r: datalogResult) {
//			kb.addStatement(r);
//			System.out.println(r);
//		}
//		
//		try (final Reasoner reasoner = new VLogReasoner(kb)) {
//			reasoner.reason();
//			/* Execute some queries */
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+Q1.getExpression().getName(), 2), Arrays.asList(Q1.getTerm1(), Q1.getTerm2())), reasoner);
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+Q2.getExpression().getName(), 2), Arrays.asList(Q2.getTerm1(), Q2.getTerm2())), reasoner);
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+Q3.getExpression().getName(), 2), Arrays.asList(Q3.getTerm1(), Q3.getTerm2())), reasoner);
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+Q4.getExpression().getName(), 2), Arrays.asList(Q4.getTerm1(), Q4.getTerm2())), reasoner);
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+Q5.getExpression().getName(), 2), Arrays.asList(Q5.getTerm1(), Q5.getTerm2())), reasoner);
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+Q6.getExpression().getName(), 2), Arrays.asList(Q6.getTerm1(), Q6.getTerm2())), reasoner);
//		}
		
		/////////////////////////////////////////////////
		
//		final List<Statement> datalogResult = RpqConverter.CRPQTranslate(uvars, conj, conjunct, kb);
//		
//		for (Statement r: datalogResult) {
//			kb.addStatement(r);
//			System.out.println(r);
//		}
//		
//		try (final Reasoner reasoner = new VLogReasoner(kb)) {
//			reasoner.reason();
//			/* Execute some queries */
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", 1), Arrays.asList(x)), reasoner);
//		}
		
		/////////////////////////////////////////////////
		
//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q8, kb1);
//		
//		for (Statement r: datalogResult) {
//			kb1.addStatement(r);
//			System.out.println(r);
//		}
//		
//		try (final Reasoner reasoner = new VLogReasoner(kb1)) {
//			reasoner.reason();
//			/* Execute some queries */
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+c3.getName(), 2), Arrays.asList(x, y)), reasoner);
//		}
		
		/////////////////////////////////////////////////
		
//		final List<Statement> datalogResult = RpqConverter.RPQTranslate(Q9, kb2);
//		
//		for (Statement r: datalogResult) {
//			kb2.addStatement(r);
//			System.out.println(r);
//		}
//		
//		try (final Reasoner reasoner = new VLogReasoner(kb2)) {
//			reasoner.reason();
//			/* Execute some queries */
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_"+c4s.getName(), 2), Arrays.asList(x, const2)), reasoner);
//		}
		
		/////////////////////////////////////////////////
		
//		final List<Statement> datalogResult = RpqConverter.CRPQTranslate(uvars2, conj2, conjunct2, kb3);
//		
//		for (Statement r: datalogResult) {
//			kb3.addStatement(r);
//			System.out.println(r);
//		}
//		
//		try (final Reasoner reasoner = new VLogReasoner(kb3)) {
//			reasoner.reason();
//			/* Execute some queries */
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", 2), uvars2), reasoner);
//		}
	}
}
