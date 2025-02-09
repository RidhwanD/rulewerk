package org.semanticweb.rulewerk.synthesis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

import com.microsoft.z3.Context;

public class Benchmark {
	public static void main(String[] arg) throws IOException, ParsingException {
		// "sql-01","sql-02","sql-04","sql-05","sql-06","sql-07","sql-09","sql-12","sql-13","sql-14","sql-03","sql-08","sql-11","traffic", "small", "abduce", "ship", "inflamation", "animals", "rvcheck", "path","1-object-1-type","1-type","1-object","rsg","escape","modref","andersen","sgen","buildwall","cliquer","sql-15","1-call-site","2-call-site", "union-find","nearlyscc","scc","polysite","downcast","sql-10"
		// "sql-01","sql-02","sql-04","sql-05","sql-06","sql-07","sql-09","sql-12","sql-13","sql-14","sql-03","sql-08","sql-11","abduce","animals","inflamation","ship","small","traffic","nearlyscc","rvcheck","sql-15","cliquer","union-find","polysite","downcast","sql-10"
		List<String> benchmarks = new ArrayList<>(Arrays.asList("rvcheck"));
		int iter = 1;
		for (int expCase = 7; expCase <= 7; expCase++) {
			for (String benchCase : benchmarks) {
				for (int expNum = 1; expNum <= iter; expNum++) {
					boolean write = true;
					String size = "small";
					System.out.println(benchCase + " - " + expCase + " - " + expNum);
					System.out.println("Parse Input");
					File inputFile = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-input.txt");
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
				
					File expFile = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-exp-pred.txt");
					Scanner myReader = new Scanner(expFile);
					List<Predicate> expPred = new ArrayList<>();
					while (myReader.hasNextLine()) {
						String[] data = myReader.nextLine().split(" ");
						expPred.add(Expressions.makePredicate(data[0], Integer.valueOf(data[1])));
					}
					myReader.close();
					
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
				
					System.out.println("Parsing Candidate Rules");
					File rulesFile = new File(ReasoningUtils.INPUT_FOLDER + benchCase + "/rulewerk-rules-" + size + ".txt");
					FileInputStream rulesStream = new FileInputStream(rulesFile);
					List<Rule> rules = new ArrayList<>();
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
					Context ctx = new Context();
					
					if (rules.size() == 0) {
						System.out.println("Rule Generator Started");
						List<Predicate> inpPred = new ArrayList<>();
						for (Fact f : inputTuple) {
							if (!inpPred.contains(f.getPredicate()))
								inpPred.add(f.getPredicate());
						}
			
						RuleGenerator rg = new RuleGenerator(null, inpPred, expPred, new ArrayList<>());
						rules = rg.enumerateLiteralGenerator(2);
						System.out.println("- " + rules.size() + " rules generated.");
					}
					
					System.out.println("Synthesis Process Started");
					DatalogSynthesisImpl ds = new DatalogSynthesisImpl(inputTuple, expPred, outputTupleP, outputTupleM, rules, ctx);
					
					List<Rule> result = new ArrayList<>();
					long startTime = System.nanoTime();
					if (expCase == 1)
						result = ds.synthesis();
					else if (expCase == 2)
						result = ds.synthesisOrig();
					else if (expCase == 3)
						result = ds.synthesisSet();
					else if (expCase == 4)
						result = ds.synthesisSetAll();
					else if (expCase == 5)
						result = ds.synthesisSet2();
					else if (expCase == 6)
						result = ds.synthesisSetAll2();
					else if (expCase == 7)
						result = ds.synthesisSetAll3();
					long endTime = System.nanoTime();
					System.out.println("Resulting Program:");
					long duration = (endTime - startTime);
					if (write) {
						try {
							FileWriter myWriter = new FileWriter(ReasoningUtils.OUTPUT_FOLDER + benchCase + "/rulewerk-result-"+expCase+"-"+expNum+".txt");
							FileWriter myWriter2 = new FileWriter(ReasoningUtils.OUTPUT_FOLDER + benchCase + "/rulewerk-log-"+expCase+"-"+expNum+".txt");
							if (result != null) {
								for (Rule r : result) {
									System.out.println("- "+r);
									myWriter.write(r.toString()+"\n");
								}
							}
							myWriter2.write(String.valueOf(duration)+"\n");
							myWriter2.write(String.valueOf(ds.getIteration())+"\n");
							myWriter2.write(String.valueOf(ds.getRulewerkCall())+"\n");
							myWriter2.write(String.valueOf(ds.getZ3Call())+"\n");
							myWriter.close();
							myWriter2.close();
							System.out.println("Successfully wrote to the file.");
						} 
						catch (IOException e) {
							System.out.println("An error occurred.");
							e.printStackTrace();
						}
					} else {
						if (result != null) {
							for (Rule r : result) {
								System.out.println("- "+r);
							}
						}
					}
					ctx.close();
					System.out.println("Synthesis time: "+duration+ " ns / "+(duration/1000000)+" ms");

					if (result == null) break;
				}
			}
		}
	}
}
