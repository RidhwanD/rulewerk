package org.semanticweb.rulewerk.examples.rpq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.rpq.converter.RpqConverter;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.parser.ParsingException;
import org.semanticweb.rulewerk.rpq.parser.RPQParser;

public class ReasoningRPQParserExample {
	public static void main(String[] arg) throws IOException, ParsingException {
		ReasoningUtils.configureLogging(); // use simple logger for the example
		
		final UniversalVariable x = Expressions.makeUniversalVariable("X");
		final UniversalVariable y = Expressions.makeUniversalVariable("Y");
		final Constant const1 = Expressions.makeAbstractConstant("1");
		final Constant const2 = Expressions.makeAbstractConstant("2");
		final Constant const3 = Expressions.makeAbstractConstant("3");
		final Constant const4 = Expressions.makeAbstractConstant("4");
		final Constant const5 = Expressions.makeAbstractConstant("5");
		
		/////////////////////////////////////////////////
		
		final KnowledgeBase kb1 = new KnowledgeBase();
		kb1.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const2)));
		kb1.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const4)));
		kb1.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const3,Expressions.makeAbstractConstant("b"),const2)));
		kb1.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const1,Expressions.makeAbstractConstant("d"),const3)));
		kb1.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const4,Expressions.makeAbstractConstant("d"),const2)));
		
		final List<Term> uvars = new ArrayList<Term>(Arrays.asList(x, y));
		
		String input = "select ?X ?Y where {{ ?X ((((<a>*) / (<d> | <b>)) / <a>) / (<b>*)) ?Y . }}";
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		System.out.println(statement);
		
		final List<Statement> datalogResult1 = RpqConverter.CRPQTranslate(uvars, statement, null);
		
		for (Statement r: datalogResult1) {
			kb1.addStatement(r);
			System.out.println(r);
		}
		
		try (final Reasoner reasoner = new VLogReasoner(kb1)) {
			reasoner.reason();
			/* Execute some queries */
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Ans", x, y), reasoner);
		}
		
		/////////////////////////////////////////////////
		
		final KnowledgeBase kb2 = new KnowledgeBase();
		kb2.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const1,Expressions.makeAbstractConstant("a"),const3)));
		kb2.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const2,Expressions.makeAbstractConstant("a"),const3)));
		kb2.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const5,Expressions.makeAbstractConstant("a"),const4)));
		
		final List<Term> uvars2 = new ArrayList<Term>(Arrays.asList(x));
		
		input = "select ?X where {{ ?X ((<a> / (^<a>))*) <2> . }}";
		statement = RPQParser.parse(input);
		System.out.println(statement);
		
		final List<Statement> datalogResult2 = RpqConverter.CRPQTranslate(uvars2, statement, null);
		
		for (Statement r: datalogResult2) {
			kb2.addStatement(r);
			System.out.println(r);
		}
		
		try (final Reasoner reasoner = new VLogReasoner(kb2)) {
			reasoner.reason();
			/* Execute some queries */
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Ans", x), reasoner);
		}
		
		/////////////////////////////////////////////////
		
		final KnowledgeBase kb3 = new KnowledgeBase();
		kb3.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const1,Expressions.makeAbstractConstant("n3"),const3)));
		kb3.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const2,Expressions.makeAbstractConstant("n3"),const3)));
		kb3.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const1,Expressions.makeAbstractConstant("n8"),const3)));
		kb3.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const2,Expressions.makeAbstractConstant("n8"),const4)));
		kb3.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const3,Expressions.makeAbstractConstant("n3"),const4)));
		kb3.addStatement(Expressions.makeFact("TRIPLE", Arrays.asList(const3,Expressions.makeAbstractConstant("n8"),const4)));
		kb3.addStatement(Expressions.makeFact("Check", const1));
		
		final List<Term> uvars3 = new ArrayList<Term>(Arrays.asList(x, y));
		final Conjunction<Literal> conjunct = Expressions.makeConjunction(Expressions.makePositiveLiteral("Check", x));
		
		input = "select ?X ?Y where {{ ?X (<n3> / (<n3>*)) ?Y .  ?X (<n8> / (<n8>*)) ?Y . }}";
		statement = RPQParser.parse(input);
		System.out.println(statement);
		
		final List<Statement> datalogResult3 = RpqConverter.CRPQTranslate(uvars3, statement, conjunct);
		
		for (Statement r: datalogResult3) {
			kb3.addStatement(r);
			System.out.println(r);
		}
		
		try (final Reasoner reasoner = new VLogReasoner(kb3)) {
			reasoner.reason();
			/* Execute some queries */
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Ans", x, y), reasoner);
		}
	}
}
