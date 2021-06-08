package org.semanticweb.rulewerk.synthesis;

import java.io.IOException;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetConstruct;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class DatalogSetUtilsTest {
	public static void main(String[] arg) throws IOException {
		ReasoningUtils.configureLoggingAll(); // use simple logger for the example
		Set<Statement> r_su = DatalogSetUtils.getR_SU();
		
		for (Statement s : r_su) {
			System.out.println(s);
		}
		
		SetVariable u = Expressions.makeSetVariable("U");
		
		UniversalVariable x = Expressions.makeUniversalVariable("x");
		UniversalVariable y = Expressions.makeUniversalVariable("y");
		UniversalVariable z = Expressions.makeUniversalVariable("z");
		
		AbstractConstant a = Expressions.makeAbstractConstant("a");
		AbstractConstant b = Expressions.makeAbstractConstant("b");
		AbstractConstant c = Expressions.makeAbstractConstant("c");
		AbstractConstant d = Expressions.makeAbstractConstant("d");
		AbstractConstant e = Expressions.makeAbstractConstant("e");
		AbstractConstant f = Expressions.makeAbstractConstant("f");
		AbstractConstant g = Expressions.makeAbstractConstant("g");
		AbstractConstant h = Expressions.makeAbstractConstant("h");
		AbstractConstant i = Expressions.makeAbstractConstant("i");
		
		SetConstruct sety = Expressions.makeSetConstruct(y);
		SetUnion un1 = Expressions.makeSetUnion(sety, u);
		
		Predicate p = Expressions.makePredicate("parent", 2);
		Predicate an = Expressions.makePredicate("ancestor", 2);
		Predicate ans = Expressions.makePredicate("ancestors", 2);
		Predicate in = Expressions.makePredicate("in", 2);
		
		KnowledgeBase kb = new KnowledgeBase();
		
		Fact f1 = Expressions.makeFact(p, a, b);
		kb.addStatement(f1);
		Fact f2 = Expressions.makeFact(p, b, c);
		kb.addStatement(f2);
		Fact f3 = Expressions.makeFact(p, b, d);
		kb.addStatement(f3);
		Fact f4 = Expressions.makeFact(p, a, e);
		kb.addStatement(f4);
		Fact f5 = Expressions.makeFact(p, e, f);
		kb.addStatement(f5);
		Fact f6 = Expressions.makeFact(p, f, g);
		kb.addStatement(f6);
		Fact f7 = Expressions.makeFact(p, h, i);
		kb.addStatement(f7);

		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral(an, x, y), Expressions.makePositiveLiteral(p, x, y));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral(an, x, z), 
				Expressions.makePositiveLiteral(an, x, y), Expressions.makePositiveLiteral(p, y, z));
		Rule r3 = Expressions.makeRule(Expressions.makePositiveLiteral(ans, x, sety), 
				Expressions.makePositiveLiteral(p, x, y));
		Rule r4 = Expressions.makeRule(Expressions.makePositiveLiteral(ans, x, un1), 
				Expressions.makePositiveLiteral(an, x, y), Expressions.makePositiveLiteral(ans, x, u));
		
		kb.addStatements(r_su);
		
		System.out.println("\n ===== Test ===== ");
		
		System.out.println();
		System.out.println(r1);
		Rule nr1 = DatalogSetUtils.normalize(r1);
		System.out.println(nr1);
		System.out.println(DatalogSetUtils.getOrder(nr1));
		System.out.println("Result of transformation: ");
		for (Rule rule : DatalogSetUtils.transformRule(r1)) {
			kb.addStatement(rule);
			System.out.println("- "+rule);
		}
		
		System.out.println();
		System.out.println(r2);
		Rule nr2 = DatalogSetUtils.normalize(r2);
		System.out.println(nr2);
		System.out.println(DatalogSetUtils.getOrder(nr2));
		System.out.println("Result of transformation: ");
		for (Rule rule : DatalogSetUtils.transformRule(r2)) {
			kb.addStatement(rule);
			System.out.println("- "+rule);
		}
		
		System.out.println();
		System.out.println(r3);
		Rule nr3 = DatalogSetUtils.normalize(r3);
		System.out.println(DatalogSetUtils.getOrder(r3));
		System.out.println("Result of transformation: ");
		for (Statement rule : DatalogSetUtils.transformRule(nr3)) {
			kb.addStatement(rule);
			System.out.println("- "+rule);
		}
		
		System.out.println();
		System.out.println(r4);
		Rule nr4 = DatalogSetUtils.normalize(r4);
		System.out.println(nr4);
		System.out.println(DatalogSetUtils.getOrder(nr4));
		System.out.println("Result of transformation: ");
		for (Rule rule : DatalogSetUtils.transformRule(r4)) {
			kb.addStatement(rule);
			System.out.println("- "+rule);
		}
		
		kb.addStatement(Expressions.makeRule(Expressions.makePositiveLiteral("Ans", x, y, z), 
				Expressions.makePositiveLiteral(ans, x, z), Expressions.makePositiveLiteral(in, y, z)));
		
		System.out.println();
		System.out.println("ALL STATEMENTS");
		for (Statement s : kb.getStatements()) {
			System.out.println(s);
		}
		
		System.out.println();
		System.out.println(" === Start Reasoning === ");
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			/* Execute some queries */
			System.out.println("- Answering Query");
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(an, x, y), reasoner);
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(in, x, y), reasoner);
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("sub", x, y), reasoner);
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(ans, x, y), reasoner);
//			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Ans", x, y, z), reasoner);
		}
	}
}
