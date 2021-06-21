package org.semanticweb.rulewerk.synthesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;
import org.semanticweb.rulewerk.reasoner.vlog.VLogReasoner;

public class SolutionChecker {
	private static <E> boolean isSameSet(Set<E> s1, Set<E> s2) {
		return s1.size() == s2.size() && s1.containsAll(s2) && s2.containsAll(s1);
	}
	
	public static void main(String[] arg) throws IOException, ParsingException {
		String benchCase = "abduce";
		System.out.println(benchCase);
		KnowledgeBase kb = new KnowledgeBase();
		ReasoningUtils.configureLogging();
		
		System.out.println("Parse Input");
		File inputFile = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-input.txt");
		FileInputStream inputStream = new FileInputStream(inputFile);
		List<Fact> inputTuple;
		try {
			inputTuple = RuleParser.parse(inputStream).getFacts();
			System.out.println("Input tuples parsed: " + inputTuple.size() + " facts");
		} catch (final ParsingException e) {
			System.out.println("Failed to parse input: " + e.getMessage());
			return;
		}
		inputStream.close();
		kb.addStatements(inputTuple);
		
		System.out.println("Parse Result");
		File resultFile = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-result.txt");
		FileInputStream resultStream = new FileInputStream(resultFile);
		List<Rule> resultRule;
		try {
			resultRule = RuleParser.parse(resultStream).getRules();
			System.out.println("Rules parsed: " + resultRule.size() + " facts");
		} catch (final ParsingException e) {
			System.out.println("Failed to parse rules: " + e.getMessage());
			return;
		}
		resultStream.close();
		kb.addStatements(resultRule);
		
		// ============================ REASONING ============================== //
		List<Fact> solution = new ArrayList<>();
		
		try (final Reasoner reasoner = new VLogReasoner(kb)) {
			reasoner.reason();
			/* Execute some queries */
			System.out.println("- Generating Answers");
			File myObj = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-exp-pred.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String[] data = myReader.nextLine().split(" ");
				List<Term> var = new ArrayList<>();
				for (int i = 0; i < Integer.valueOf(data[1]); i++) {
					var.add(Expressions.makeUniversalVariable("x"+i));
				}
				ReasoningUtils.printOutQueryAnswers(Expressions.makePositiveLiteral(data[0], var), reasoner);
				solution.addAll(ReasoningUtils.getQueryAnswerAsListWhyProv(Expressions.makePositiveLiteral(data[0], var), reasoner));
			}
			myReader.close();
		}
		
		System.out.println("Parsing Expected Output");
		File outputPFile = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-output-plus.txt");
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
		File outputMFile = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-output-min.txt");
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
		
		Set<Fact> sol = new HashSet<Fact>(solution);
		Set<Fact> exp = new HashSet<Fact>(outputTupleP);
		Set<Fact> nexp = new HashSet<Fact>(outputTupleM);
		Set<Fact> intersect = new HashSet<Fact>(solution);
		intersect.retainAll(nexp);
		if (nexp.isEmpty()) {
			System.out.println("Output as expected: " + isSameSet(sol, exp));
		} else {
			System.out.print("Output as expected: ");
			System.out.println(sol.containsAll(exp) && intersect.isEmpty());
		}
	}
}
