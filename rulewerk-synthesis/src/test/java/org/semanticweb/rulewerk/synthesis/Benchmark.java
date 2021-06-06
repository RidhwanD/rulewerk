package org.semanticweb.rulewerk.synthesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

import com.microsoft.z3.Context;

public class Benchmark {
	public static void main(String[] arg) throws IOException, ParsingException {
		System.out.println("Parse Input");
		File inputFile = new File(ReasoningUtils.INPUT_FOLDER + "abduce/rulewerk-input.txt");
		FileInputStream inputStream = new FileInputStream(inputFile);
		List<Fact> inputTuple;
		try {
			inputTuple = RuleParser.parse(inputStream).getFacts();
			System.out.println("Input tuples parsed: " + inputTuple.size() + " facts");
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		inputStream.close();
	
		System.out.println("Parsing Expected Output");
		File outputPFile = new File(ReasoningUtils.INPUT_FOLDER + "abduce/rulewerk-output-plus.txt");
		FileInputStream outputPStream = new FileInputStream(outputPFile);
		List<Fact> outputTupleP;
		try {
			outputTupleP = RuleParser.parse(outputPStream).getFacts();
			System.out.println("Expected output tuples parsed: " + outputTupleP.size() + " facts");
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		outputPStream.close();
	
		System.out.println("Parsing Non-Expected Output");
		File outputMFile = new File(ReasoningUtils.INPUT_FOLDER + "abduce/rulewerk-output-min.txt");
		FileInputStream outputMStream = new FileInputStream(outputMFile);
		List<Fact> outputTupleM;
		try {
			outputTupleM = RuleParser.parse(outputMStream).getFacts();
			System.out.println("Non-expected output tuples parsed: " + outputTupleM.size() + " facts");
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		outputMStream.close();
	
		System.out.println("Parsing Candidate Rules");
		File rulesFile = new File(ReasoningUtils.INPUT_FOLDER + "abduce/rulewerk-rules-small.txt");
		FileInputStream rulesStream = new FileInputStream(rulesFile);
		List<Rule> rules;
		try {
			rules = RuleParser.parse(rulesStream).getRules();
			System.out.println("Candidate rules parsed: " + rules.size() + " rules");
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		rulesStream.close();
		
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		
		System.out.println("Synthesis Process Started");
		DatalogSynthesis ds = new DatalogSynthesis(inputTuple, outputTupleP, outputTupleM, rules, ctx);
		List<Rule> result = ds.synthesis();
		System.out.println("Resulting Program:");
		if (result != null) {
			for (Rule r : result) {
				System.out.println("- "+r);
			}	
		}
		ctx.close();
	}
}
