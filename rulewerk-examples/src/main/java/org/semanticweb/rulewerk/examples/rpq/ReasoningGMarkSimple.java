package org.semanticweb.rulewerk.examples.rpq;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.examples.ExamplesUtils;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;
import org.semanticweb.rulewerk.rpq.converter.RpqNFAConverter;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.semanticweb.rulewerk.rpq.parser.ParsingException;
import org.semanticweb.rulewerk.rpq.parser.RPQParser;

public class ReasoningGMarkSimple {
	public static void main(String[] arg) throws IOException, ParsingException {
		ReasoningUtils.configureLogging(); // use simple logger for the example
		
		System.out.println("Loading Knowledge Base from file");
		File rpqGMarkShopFile = new File(ExamplesUtils.INPUT_FOLDER + "rpq/shop-a-graph-triple-1000000.txt");
		FileInputStream inputStreamGMarkShop = new FileInputStream(rpqGMarkShopFile);
		KnowledgeBase kb;
		try {
			kb = RuleParser.parse(inputStreamGMarkShop);
			System.out.println("Knowledge base parsed: " + kb.getFacts().size() + " facts");
		} catch (final org.semanticweb.rulewerk.parser.ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		inputStreamGMarkShop.close();
			
		System.out.println("Loading and Parsing Query ");
		File queryInput = new File(ExamplesUtils.INPUT_FOLDER + "rpq/newqueries/shop-a-[2_3_3-4]/query-1.sparql");
		FileInputStream input = new FileInputStream(queryInput);
		
		// ============================== PARSING =============================== //
		long startTime1 = System.currentTimeMillis();
		long beforeUsedMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
		long afterUsedMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long endTime1 = System.currentTimeMillis();
		long duration1 = (endTime1 - startTime1);
		long actualMemUsed1 = afterUsedMem1 - beforeUsedMem1;
		System.out.println("Translating Query");
		// ============================ TRANSLATING ============================= //
		long startTime2 = System.currentTimeMillis();
		long beforeUsedMem2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		final List<Term> uvars = statement.getProjVars();
//		final List<Statement> datalogResult = RpqConverter.CRPQTranslate(uvars, statement, null);
		final List<Statement> datalogResult = RpqNFAConverter.CRPQTranslate(uvars, statement, null);
		long afterUsedMem2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long endTime2 = System.currentTimeMillis();
		long duration2 = (endTime2 - startTime2);
		long actualMemUsed2 = afterUsedMem2 - beforeUsedMem2;
		
		int numRuleGenerated = 0;
		for (Statement r: datalogResult) {
			kb.addStatement(r);
			System.out.println(r);
			numRuleGenerated++;
		}
		
		System.out.println("Reasoning");
		long duration3 = 0;
		long actualMemUsed3 = 0;
		// ============================ REASONING ============================== //
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			long startTime3 = System.currentTimeMillis();
			long beforeUsedMem3 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("- Materialization");
			reasoner.reason();
			/* Execute some queries */
			System.out.println("- Answering Query");
			ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral("Ans", uvars), reasoner);
			
			long endTime3 = System.currentTimeMillis();
			long afterUsedMem3 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			duration3 = (endTime3 - startTime3);
			actualMemUsed3 = afterUsedMem3 - beforeUsedMem3;
		}
		
		System.out.println("Generated Datalog statements: " + numRuleGenerated);
		System.out.println("Parsing time: "+duration1+ " ms; Memory usage: "+actualMemUsed1/1024+" KB");
		System.out.println("Translating time: "+duration2+ " ms; Memory usage: "+actualMemUsed2/1024+" KB");
		System.out.println("Reasoning time: "+duration3+ " ms; Memory usage: "+actualMemUsed3/1024+" KB");
		System.out.println();
		
		kb.removeStatements(datalogResult);

		System.out.println("FINISHED");
	}
}
