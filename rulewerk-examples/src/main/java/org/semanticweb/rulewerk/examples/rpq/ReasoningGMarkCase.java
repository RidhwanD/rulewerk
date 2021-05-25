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
		while (size <= 500000) {
		
//		FileWriter csvWriter = new FileWriter(ExamplesUtils.OUTPUT_FOLDER + "rpq/new/new-"+ver+"-"+size+"-"+iter+"-nc_2.csv");
//		csvWriter.append("Query");	csvWriter.append(",");	
//		csvWriter.append("DS");		csvWriter.append(",");	
//		csvWriter.append("FQA");	csvWriter.append(",");	
//		csvWriter.append("PT");		csvWriter.append(",");	
//		csvWriter.append("TT");		csvWriter.append(",");
//		csvWriter.append("RT");		csvWriter.append(",");	
//		csvWriter.append("NR");		csvWriter.append("\n");	
//		
//		FileWriter csvWriter2 = new FileWriter(ExamplesUtils.OUTPUT_FOLDER + "rpq/new/new-"+ver+"-"+size+"-"+iter+"-nc_mem.csv");
//		csvWriter2.append("Query");	csvWriter2.append(",");	
//		csvWriter2.append("TTMB");	csvWriter2.append(",");
//		csvWriter2.append("TFMB");	csvWriter2.append(",");	
//		csvWriter2.append("TTMA");	csvWriter2.append(",");
//		csvWriter2.append("TFMA");	csvWriter2.append(",");	
//		csvWriter2.append("RTMB");	csvWriter2.append(",");
//		csvWriter2.append("RFMB");	csvWriter2.append(",");	
//		csvWriter2.append("RTMA");	csvWriter2.append(",");
//		csvWriter2.append("RFMA");	csvWriter2.append(",");
//		csvWriter2.append("Peak");	csvWriter2.append("\n");
		
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
//			long beforeUsedMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			RPQConjunction<RegPathQuery> statement = RPQParser.parse(input);
//			long afterUsedMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long endTime1 = System.nanoTime();
			long duration1 = (endTime1 - startTime1);
//			long actualMemUsed1 = afterUsedMem1 - beforeUsedMem1;

			System.out.println("Translating Query");
			// ============================ TRANSLATING ============================= //
			long startTime2 = System.nanoTime();
			long beforeTotMem1 = Runtime.getRuntime().totalMemory();
			long beforeFreeMem1 = Runtime.getRuntime().freeMemory();
			final List<Term> uvars = statement.getProjVars();
			List<Statement> datalogResult = null;
			if (ver == 1) {
				datalogResult = RpqConverter.CRPQTranslate(uvars, statement, null);
			} else if (ver == 2) {
				datalogResult = RpqNFAConverter.CRPQTranslate(uvars, statement, null);
			} else if (ver == 3) {
				datalogResult = RpqNFAConverter.CRPQTranslateAlt(uvars, statement, null);
			}
			long afterTotMem1 = Runtime.getRuntime().totalMemory();
			long afterFreeMem1 = Runtime.getRuntime().freeMemory();
			long endTime2 = System.nanoTime();
			long duration2 = (endTime2 - startTime2);
//			long actualMemUsed2 = afterUsedMem2 - beforeUsedMem2;
			
			final List<Predicate> preds = new ArrayList<Predicate>();
			int numRuleGenerated = 0;
			for (Statement r: datalogResult) {
				kb.addStatement(r);
				Rule rs = (Rule) r;
				Predicate p = rs.getHead().getLiterals().get(0).getPredicate();
				if (!preds.contains(p) && !p.getName().equals("Ans")) preds.add(p);
				numRuleGenerated++;
			}

			System.out.println("Reasoning");
			long duration3 = 0;
//			long actualMemUsed3 = 0;
			long beforeTotMem2 = 0;
			long beforeFreeMem2 = 0;
			long afterTotMem2 = 0;
			long afterFreeMem2 = 0;
			long ans = 0;
			long total = 0;
			// ============================ REASONING ============================== //
			try (final Reasoner reasoner = new VLogReasoner(kb)) {
				long startTime3 = System.nanoTime();
				beforeTotMem2 = Runtime.getRuntime().totalMemory();
				beforeFreeMem2 = Runtime.getRuntime().freeMemory();
				reasoner.reason();
				/* Execute some queries */
				System.out.println("- Answering Query");
				ans = ReasoningUtils.printOutQueryCount(Expressions.makePositiveLiteral(Expressions.makePredicate("Ans", uvars.size()), uvars), reasoner);
				
				long endTime3 = System.nanoTime();
				afterTotMem2 = Runtime.getRuntime().totalMemory();
				afterFreeMem2 = Runtime.getRuntime().freeMemory();
				duration3 = (endTime3 - startTime3);
//				actualMemUsed3 = afterUsedMem3 - beforeUsedMem3;

				total = ReasoningUtils.printOutQueryCountMult(preds, reasoner);
			}
			
			long actualMemUsed1 = afterTotMem1 - beforeTotMem1;
			long actualMemUsed2 = afterTotMem2 - beforeTotMem2;
			
			System.out.println("Generated Datalog statements: " + numRuleGenerated);
			System.out.println("Parsing time: "+duration1+ " ms");
			System.out.println("Translating time: "+duration2+ " ms; Memory usage: "+actualMemUsed1/1024+" KB");
			System.out.println("Reasoning time: "+duration3+ " ms; Memory usage: "+actualMemUsed2/1024+" KB");
			System.out.println("Peak memory usage: "+duration1+ " ms");
			System.out.println();
			
			kb.removeStatements(datalogResult);
			
//			csvWriter.append(String.valueOf(i+"_"+j+"_"+k+"_"+l)); 	csvWriter.append(",");
//			csvWriter.append(String.valueOf(numRuleGenerated)); 	csvWriter.append(",");
//			csvWriter.append(String.valueOf(total));				csvWriter.append(",");
//			csvWriter.append(String.valueOf(duration1));			csvWriter.append(",");
//			csvWriter.append(String.valueOf(duration2));			csvWriter.append(",");
//			csvWriter.append(String.valueOf(duration3));			csvWriter.append(",");
//			csvWriter.append(String.valueOf(ans));					csvWriter.append("\n");
//			
//			csvWriter2.append(String.valueOf(i+"_"+j+"_"+k+"_"+l)); csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(beforeTotMem1));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(beforeFreeMem1));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(afterTotMem1));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(afterFreeMem1));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(beforeTotMem2));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(beforeFreeMem2));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(afterTotMem2));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(afterFreeMem2));		csvWriter2.append(",");
//			csvWriter2.append(String.valueOf(afterFreeMem2));		csvWriter2.append("\n");
		}}}}
//		csvWriter.flush();		csvWriter.close();
//		csvWriter2.flush();		csvWriter2.close();
		System.out.println(size+" FINISHED");
		size += 500000;
		}
		}
	}
}
