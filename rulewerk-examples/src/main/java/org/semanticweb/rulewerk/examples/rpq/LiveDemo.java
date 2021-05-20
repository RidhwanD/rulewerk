package org.semanticweb.rulewerk.examples.rpq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.rpq.converter.RpqConverter;
import org.semanticweb.rulewerk.rpq.converter.RpqNFAConverter;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.parser.ParsingException;
import org.semanticweb.rulewerk.rpq.parser.RPQParser;

public class LiveDemo {
	public static void main(String[] arg) throws IOException, ParsingException {
		ReasoningUtils.configureLogging();
		
		System.out.println("Loading Knowledge Base from file");
		File inputStreamLiveDemoFile = new File(ExamplesUtils.INPUT_FOLDER + "rpq/livedemo.txt");
		FileInputStream inputStreamLiveDemo = new FileInputStream(inputStreamLiveDemoFile);
		KnowledgeBase kb;
		try {
			kb = RuleParser.parse(inputStreamLiveDemo);
			System.out.println("Knowledge base parsed: " + kb.getFacts().size() + " facts");
		} catch (final org.semanticweb.rulewerk.parser.ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		inputStreamLiveDemo.close();
		
		System.out.println("Loading and Parsing Query ");
		File queryInput = new File(ExamplesUtils.INPUT_FOLDER + "rpq/livedemo/query-" + 3 + ".sparql");
		FileInputStream input = new FileInputStream(queryInput);
		
		// Set of people who have authored something and year of their published works
		// String input = "SELECT DISTINCT ?x0 ?x1 WHERE {{  ?x0 (<authorOf>/<hasPublicationDate>) ?x1 . }}";
		
		// Set of people who have some historian friend
		// String input = "SELECT DISTINCT ?x0 WHERE {{  ?x0 ((<friendOf>|(^<friendOf>))/<hasProfession>) <Historian> . }}";
		
		// Set of people who are illustrator and their Archaeologist friend closure
		// String input = "SELECT DISTINCT ?x0 ?x1 WHERE {{  ?x0 <hasProfession> <Illustrator> . ?x0 ((<friendOf>|(^<friendOf>))+) ?x1 . ?x1 <hasProfession> <Archaeologist> . }}";
		
		// Set of archaeologist friends of people that have passed away
		// String input = "SELECT DISTINCT ?x0 ?x1 WHERE {{  ?x0 <deathDate> ?x2 . ?x0 (<friendOf>|(^<friendOf>)) ?x1 . ?x1 <hasProfession> <Archaeologist> . }}";
		
		// Set of author and their friend closure, including themselves (to show kleene star)
		// String input = "SELECT DISTINCT ?x0 ?x1 WHERE {{  ?x0 <authorOf> ?x2 . ?x0 ((<friendOf>|(^<friendOf>))*) ?x1 . ?x1 <authorOf> ?x3 . }}";
		
		// ============================== PARSING =============================== //
		long startTime1 = System.currentTimeMillis();
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		long endTime1 = System.currentTimeMillis();
		long duration1 = (endTime1 - startTime1);

		System.out.println("Translating Query");
		// ============================ TRANSLATING ============================= //
		long startTime2 = System.currentTimeMillis();
		final List<Term> uvars = statement.getProjVars();
//		final List<Statement> datalogResult = RpqConverter.CRPQTranslate(uvars, statement, null);
//		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslate(uvars, statement, null);
		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslateAlt(uvars, statement, null);
		long endTime2 = System.currentTimeMillis();
		long duration2 = (endTime2 - startTime2);
		
		final List<Predicate> preds = new ArrayList<Predicate>();
		int numRuleGenerated = 0;
		for (Statement r: datalogResult) {
			kb.addStatement(r);
			Rule rs = (Rule) r;
			Predicate p = rs.getHead().getLiterals().get(0).getPredicate();
			if (!preds.contains(p) && !p.getName().equals("Ans")) preds.add(p);
			numRuleGenerated++;
			System.out.println(r);
		}
		
		System.out.println("Reasoning");
		long duration3 = 0;
		// ============================ REASONING ============================== //
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			long startTime3 = System.currentTimeMillis();
			reasoner.reason();
			/* Execute some queries */
			System.out.println("- Answering Query");
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", uvars.size()), uvars), reasoner);			
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_hasProfession", uvars.size()), uvars.get(0),Expressions.makeAbstractConstant("Illustrator")), reasoner);			
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_((friendOf | ^friendOf))+", uvars.size()), uvars), reasoner);		
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(Expressions.makePredicate("Q_hasProfession", uvars.size()), Arrays.asList(uvars.get(0),Expressions.makeAbstractConstant("Archaeologist"))), reasoner);		
			long endTime3 = System.currentTimeMillis();
			duration3 = (endTime3 - startTime3);
			
			ReasoningUtils.printOutQueryCountMult(preds, reasoner);
		}
		
		System.out.println("Generated Datalog statements: " + numRuleGenerated);
		System.out.println("Parsing time: "+duration1+ " ms");
		System.out.println("Translating time: "+duration2+ " ms");
		System.out.println("Reasoning time: "+duration3+ " ms");
		System.out.println();
		
		kb.removeStatements(datalogResult);

		System.out.println("FINISHED");
	}
}