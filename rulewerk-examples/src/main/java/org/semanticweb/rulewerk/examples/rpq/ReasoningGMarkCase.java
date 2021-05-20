package org.semanticweb.rulewerk.examples.rpq;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

public class ReasoningGMarkCase {
	public static void main(String[] arg) throws IOException, ParsingException {
		int ver = 2;
		ReasoningUtils.configureLogging(); // use simple logger for the example
		
		for (int iter = 3; iter < 4; iter++) {
		int size = 500000;
		while (size <= 2000000) {
		
		FileWriter csvWriter = new FileWriter(ExamplesUtils.OUTPUT_FOLDER + "rpq/new-"+ver+"-"+size+"-"+iter+"-nc_2.csv");
		csvWriter.append("Query");	csvWriter.append(",");	
		csvWriter.append("DS");		csvWriter.append(",");	
		csvWriter.append("FQA");	csvWriter.append(",");	
		csvWriter.append("PT");		csvWriter.append(",");	
		csvWriter.append("TT");		csvWriter.append(",");
		csvWriter.append("RT");		csvWriter.append(",");	
		csvWriter.append("NR");		csvWriter.append("\n");	
		
		FileWriter csvWriter2 = new FileWriter(ExamplesUtils.OUTPUT_FOLDER + "rpq/new-"+ver+"-"+size+"-"+iter+"-nc_mem.csv");
		csvWriter2.append("Query");	csvWriter2.append(",");	
		csvWriter2.append("NR");	csvWriter2.append(",");
		csvWriter2.append("PM");	csvWriter2.append(",");	
		csvWriter2.append("TM");	csvWriter2.append("\n");
		
		System.out.println("Loading Knowledge Base from file");
		File rpqGMarkShopFile = new File(ExamplesUtils.INPUT_FOLDER + "rpq/shop-a-graph-triple-"+size+".txt");
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
			
		for (int i = 2; i < 4; i++) {
		for (int j = 1; j < 4; j++) {
		for (int k = 1; k < 6; k += 2) {
		for (int l = 0; l < 10; l++) {
			System.out.println("Loading and Parsing Query["+i+"_"+j+"_"+k+"-"+(k+1)+"] "+l+" for size " + size + "in iteration " + iter);
			File queryInput = new File(ExamplesUtils.INPUT_FOLDER + "rpq/newqueries/shop-a-["+i+"_"+j+"_"+k+"-"+(k+1)+"]-noconverse/query-"+l+".sparql");
			FileInputStream input = new FileInputStream(queryInput);
			
			// ============================== PARSING =============================== //
			long startTime1 = System.nanoTime();
			long beforeUsedMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
			long afterUsedMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long endTime1 = System.nanoTime();
			long duration1 = (endTime1 - startTime1);
			long actualMemUsed1 = afterUsedMem1 - beforeUsedMem1;

			System.out.println("Translating Query");
			// ============================ TRANSLATING ============================= //
			long startTime2 = System.nanoTime();
			long beforeUsedMem2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			final List<Term> uvars = statement.getProjVars();
			List<Statement> datalogResult = null;
			if (ver == 1) {
				datalogResult = RpqConverter.CRPQTranslate(uvars, statement, null);
			} else {
				datalogResult = RpqNFAConverter.CRPQTranslate(uvars, statement, null);
			}
			long afterUsedMem2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long endTime2 = System.nanoTime();
			long duration2 = (endTime2 - startTime2);
			long actualMemUsed2 = afterUsedMem2 - beforeUsedMem2;
			
			final List<Predicate> preds = new ArrayList<Predicate>();
			int numRuleGenerated = 0;
			for (Statement r: datalogResult) {
				kb.addStatement(r);
				Rule rs = (Rule) r;
				Predicate p = rs.getHead().getLiterals().get(0).getPredicate();
				if (!preds.contains(p) && !p.getName().equals("Ans")) preds.add(p);
				numRuleGenerated++;
			}
//			for (int i = 0; i < preds.size(); i++)
//				System.out.println(preds.get(i));

			System.out.println("Reasoning");
			long duration3 = 0;
			long actualMemUsed3 = 0;
			long ans = 0;
			long total = 0;
			// ============================ REASONING ============================== //
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				long startTime3 = System.nanoTime();
				long beforeUsedMem3 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				reasoner.reason();
				/* Execute some queries */
				System.out.println("- Answering Query");
				ans = ReasoningUtils.printOutQueryCount(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", uvars.size()), uvars), reasoner);
				
				long endTime3 = System.nanoTime();
				long afterUsedMem3 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				duration3 = (endTime3 - startTime3);
				actualMemUsed3 = afterUsedMem3 - beforeUsedMem3;

				total = ReasoningUtils.printOutQueryCountMult(preds, reasoner);
			}
			
			System.out.println("Generated Datalog statements: " + numRuleGenerated);
			System.out.println("Parsing time: "+duration1+ " ms");
			System.out.println("Translating time: "+duration2+ " ms");
			System.out.println("Reasoning time: "+duration3+ " ms");
			System.out.println();
			
			kb.removeStatements(datalogResult);
			
			csvWriter.append(String.valueOf(i+"_"+j+"_"+k+"_"+l)); 	csvWriter.append(",");
			csvWriter.append(String.valueOf(numRuleGenerated)); 	csvWriter.append(",");
			csvWriter.append(String.valueOf(total));				csvWriter.append(",");
			csvWriter.append(String.valueOf(duration1));			csvWriter.append(",");
			csvWriter.append(String.valueOf(duration2));			csvWriter.append(",");
			csvWriter.append(String.valueOf(duration3));			csvWriter.append(",");
			csvWriter.append(String.valueOf(ans));					csvWriter.append("\n");
			
			csvWriter2.append(String.valueOf(i+"_"+j+"_"+k+"_"+l)); csvWriter2.append(",");
			csvWriter2.append(String.valueOf(actualMemUsed1));		csvWriter2.append(",");
			csvWriter2.append(String.valueOf(actualMemUsed2));		csvWriter2.append(",");
			csvWriter2.append(String.valueOf(actualMemUsed3));		csvWriter2.append("\n");
		}}}}
		csvWriter.flush();		csvWriter.close();
		csvWriter2.flush();		csvWriter2.close();
		System.out.println(size+" FINISHED");
		size += 500000;
		}
		}
	}
}
