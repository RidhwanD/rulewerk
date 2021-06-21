package org.semanticweb.rulewerk.synthesis;

import java.io.IOException;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class Test {
	
	public static void main (String[] arg) throws IOException {
		ReasoningUtils.configureLoggingAll(); // use simple logger for the example
		
		Variable x0 = Expressions.makeUniversalVariable("x0");
		Variable x1 = Expressions.makeUniversalVariable("x1");
		Variable x2 = Expressions.makeUniversalVariable("x2");
		Variable x3 = Expressions.makeUniversalVariable("x3");
		Variable x4 = Expressions.makeUniversalVariable("x4");
		Variable x5 = Expressions.makeUniversalVariable("x5");
		Variable x6 = Expressions.makeUniversalVariable("x6");

		SetVariable u = Expressions.makeSetVariable("U");
		SetVariable v = Expressions.makeSetVariable("V");
		
		AbstractConstant c1 = Expressions.makeAbstractConstant("c1");
		AbstractConstant c2 = Expressions.makeAbstractConstant("c2");
		AbstractConstant c3 = Expressions.makeAbstractConstant("c3");
		AbstractConstant c4 = Expressions.makeAbstractConstant("c4");
		AbstractConstant c5 = Expressions.makeAbstractConstant("c5");
		
		AbstractConstant cr1 = Expressions.makeAbstractConstant("cr1");
		AbstractConstant cr2 = Expressions.makeAbstractConstant("cr2");
		AbstractConstant cr3 = Expressions.makeAbstractConstant("cr3");

		SetUnion cr1Uu = Expressions.makeSetUnion(Expressions.makeSetConstruct(cr1), u);
		SetUnion cr2Uu = Expressions.makeSetUnion(Expressions.makeSetConstruct(cr2), u);
		SetUnion cr2UuUv = Expressions.makeSetUnion(cr2Uu, v);
		
		// Test non-recursive
		
//		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("ancestor", x0, x2, Expressions.makeSetConstruct(cr1)), 
//				Expressions.makePositiveLiteral("Rule", cr1), Expressions.makePositiveLiteral("parent", x0, x1), 
//				Expressions.makePositiveLiteral("parent", x2, x1));
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral("ancestor", x0, x2, cr1Uu), 
				Expressions.makePositiveLiteral("Rule", cr1), Expressions.makePositiveLiteral("ancestor", x0, x1, u), 
				Expressions.makePositiveLiteral("parent", x2, x1));
//		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("ancestor", x0, x2, cr2Uu), 
//				Expressions.makePositiveLiteral("Rule", cr2), Expressions.makePositiveLiteral("ancestor", x0, x1, u), 
//				Expressions.makePositiveLiteral("parent", x1, x2));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral("ancestor", x0, x2, cr2UuUv), 
				Expressions.makePositiveLiteral("Rule", cr2), Expressions.makePositiveLiteral("ancestor", x0, x1, u), 
				Expressions.makePositiveLiteral("ancestor", x1, x2, v));
		Rule r3 = Expressions.makeRule(Expressions.makePositiveLiteral("ancestor", x0, x1, Expressions.makeSetConstruct(cr3)), 
				Expressions.makePositiveLiteral("Rule", cr3), Expressions.makePositiveLiteral("parent", x0, x1));
		
		Fact f1 = Expressions.makeFact("parent", c1, c2);
		Fact f2 = Expressions.makeFact("parent", c2, c4);
		Fact f3 = Expressions.makeFact("parent", c3, c4);
		Fact f4 = Expressions.makeFact("parent", c4, c5);

		Fact rf1 = Expressions.makeFact("Rule", cr1);
		Fact rf2 = Expressions.makeFact("Rule", cr2);
		Fact rf3 = Expressions.makeFact("Rule", cr3);
		
		System.out.println(r1);
		System.out.println(r2);
		System.out.println(r3);
		
		KnowledgeBase kb = new KnowledgeBase();
		kb.addStatements(DatalogSetUtils.getR_SU());
		
//		System.out.println();
//		System.out.println(r1);
//		Rule nr1 = DatalogSetUtils.normalize(r1);
//		System.out.println(nr1);
//		System.out.println(DatalogSetUtils.getOrder(nr1));
//		System.out.println("Result of transformation: ");
//		for (Rule rule : DatalogSetUtils.transformRule(nr1)) {
//			kb.addStatement(rule);
//			System.out.println("- "+rule);
//		}
		
		System.out.println();
		System.out.println(r2);
		Rule nr2 = DatalogSetUtils.normalize(r2);
		System.out.println(nr2);
		System.out.println(DatalogSetUtils.getOrder(nr2));
		System.out.println("Result of transformation: ");
		for (Rule rule : DatalogSetUtils.transformRule(nr2)) {
			kb.addStatement(rule);
			System.out.println("- "+rule);
		}
		
		System.out.println();
		System.out.println(r3);
		Rule nr3 = DatalogSetUtils.normalize(r3);
		System.out.println(nr3);
		System.out.println(DatalogSetUtils.getOrder(nr3));
		System.out.println("Result of transformation: ");
		for (Rule rule : DatalogSetUtils.transformRule(nr3)) {
			kb.addStatement(rule);
			System.out.println("- "+rule);
		}
		
		kb.addStatement(f1);
		kb.addStatement(f2);
		kb.addStatement(f3);
		kb.addStatement(f4);
		kb.addStatement(rf1);
		kb.addStatement(rf2);
		kb.addStatement(rf3);

		kb.addStatement(Expressions.makeRule(Expressions.makePositiveLiteral("Ans", x0, x1, x2, x3), 
				Expressions.makePositiveLiteral("ancestor", x0, x1, x3), Expressions.makePositiveLiteral("in", x2, x3)));
		
		kb.addStatement(Expressions.makeRule(Expressions.makePositiveLiteral("Ans2", x6, x3), 
				Expressions.makePositiveLiteral("Rule", x6), 
				Expressions.makePositiveLiteral("ancestor", x0, x1, x4), Expressions.makePositiveLiteral("ancestor", x1, x2, x5), 
				Expressions.makePositiveLiteral("empty", x3)));
		
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			/* Execute some queries */
			System.out.println("- Answering Query");
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Rule", x0), reasoner);
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("ancestor", x0, x1, x2), reasoner);
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Ans", c1, c2, x2, x3), reasoner);
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Ans2", x0, x1), reasoner);
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("in", x0, x1), reasoner);
		}
	}
}
