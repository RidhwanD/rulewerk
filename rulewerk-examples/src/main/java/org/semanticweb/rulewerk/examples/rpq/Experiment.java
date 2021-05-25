package org.semanticweb.rulewerk.examples.rpq;

import java.io.IOException;
import java.util.Arrays;

import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class Checking {
	public static void main(String[] arg) throws IOException {
		ReasoningUtils.configureLogging(); // use simple logger for the example
		
		final UniversalVariable x = Expressions.makeUniversalVariable("X");
		final UniversalVariable y = Expressions.makeUniversalVariable("Y");
		final UniversalVariable x1 = Expressions.makeUniversalVariable("x1");
		final UniversalVariable x2 = Expressions.makeUniversalVariable("x2");
		final UniversalVariable x3 = Expressions.makeUniversalVariable("x3");
		final UniversalVariable x4 = Expressions.makeUniversalVariable("x4");
		final UniversalVariable x5 = Expressions.makeUniversalVariable("x5");
		final UniversalVariable x6 = Expressions.makeUniversalVariable("x6");
		final UniversalVariable x7 = Expressions.makeUniversalVariable("x7");
		final UniversalVariable x8 = Expressions.makeUniversalVariable("x8");
		final UniversalVariable x9 = Expressions.makeUniversalVariable("x9");
		final UniversalVariable x10 = Expressions.makeUniversalVariable("x10");
		final UniversalVariable x11 = Expressions.makeUniversalVariable("x11");
		final Constant const1 = Expressions.makeAbstractConstant("1");
		final Constant const2 = Expressions.makeAbstractConstant("2");
		final Constant const3 = Expressions.makeAbstractConstant("3");
		final Constant const4 = Expressions.makeAbstractConstant("4");
		final Constant const5 = Expressions.makeAbstractConstant("5");
				
		final KnowledgeBase kberr = new KnowledgeBase();
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const2)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const3)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const4)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const5)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const1,Expressions.makeAbstractConstant("b"),const5)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("b"),const4)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("b"),const4)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const2,Expressions.makeAbstractConstant("c"),const3)));
		kberr.addStatement(Expressions.makeFact(Expressions.makePredicate("TRIPLE", 3), Arrays.asList(const3,Expressions.makeAbstractConstant("c"),const4)));
		
		Rule rx = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek1", 2), x1, x2), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("TRIPLE", 3), x1, Expressions.makeAbstractConstant("a"), x2));
		Rule ry = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek2", 2), x3, x4), 
//				Expressions.makePositiveLiteral(Expressions.makePredicate("TRIPLE", 3), x3, Expressions.makeAbstractConstant("b"), x4));
				Expressions.makePositiveLiteral(Expressions.makePredicate("TRIPLE", 3), x4, Expressions.makeAbstractConstant("b"), x3));
		Rule ra = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek3", 2), x5, x7), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek1", 2), x5, x6),
				Expressions.makePositiveLiteral(Expressions.makePredicate("TRIPLE", 3), x6, Expressions.makeAbstractConstant("c"), x7));
		Rule rb = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek4", 2), x10, x11), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek3", 2), x10, x11));
		Rule rz = Expressions.makeRule(
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek", 2), x7, x9), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek3", 2), x7, x8), 
				Expressions.makePositiveLiteral(Expressions.makePredicate("Cek2", 2), x8, x9));
//				Expressions.makePositiveLiteral(Expressions.makePredicate("TRIPLE", 3), x9, Expressions.makeAbstractConstant("b"), x8));
		kberr.addStatement(rx);
		kberr.addStatement(ry);
		kberr.addStatement(ra);
		kberr.addStatement(rb);
		kberr.addStatement(rz);
		
		for (Statement f : kberr.getStatements()) {
			System.out.println(f);
		}
		
		System.out.println("Reasoning");
		try (final Reasoner reasoner = new VLogReasoner(kberr)) {
//		System.out.println("- Materialization");
		reasoner.reason();
		/* Execute some queries */
		System.out.println("- Answering Query");
		System.out.println(rx);
		ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Cek1", 2), x, y), reasoner);
		System.out.println(ry);
		ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Cek2", 2), x, y), reasoner);
		System.out.println(ra);
		ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Cek3", 2), x, y), reasoner);
		System.out.println(rb);
		ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Cek4", 2), x, y), reasoner);
		System.out.println(rz);
		ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Cek", 2), x, y), reasoner);
		}
	}
}
