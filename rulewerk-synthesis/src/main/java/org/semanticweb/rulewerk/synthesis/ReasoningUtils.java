package org.semanticweb.rulewerk.synthesis;

/*-
 * #%L
 * Rulewerk Examples
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.semanticweb.rulewerk.core.exceptions.ReasonerStateException;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.Variable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.Correctness;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LiteralQueryResultPrinter;
import org.semanticweb.rulewerk.core.reasoner.QueryAnswerCount;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;

public final class ReasoningUtils {

	public static final String OUTPUT_FOLDER = "src/main/data/output/";
	public static final String INPUT_FOLDER = "src/main/data/input/";

	/*
	 * This is a utility class. Therefore, it is best practice to do the following:
	 * (1) Make the class final, (2) make its constructor private, (3) make all its
	 * fields and methods static. This prevents the classes instantiation and
	 * inheritance.
	 */
	private ReasoningUtils() {

	}

	/**
	 * Prints out the answers given by {@code reasoner} to the query
	 * ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 */
	public static void printOutQueryAnswers(final PositiveLiteral queryAtom, final Reasoner reasoner) {
		System.out.println("Answers to query " + queryAtom + " :");
		OutputStreamWriter writer = new OutputStreamWriter(System.out);
		LiteralQueryResultPrinter printer = new LiteralQueryResultPrinter(queryAtom, writer,
				reasoner.getKnowledgeBase().getPrefixDeclarationRegistry());
		try (final QueryResultIterator answers = reasoner.answerQuery(queryAtom, true)) {
			int numOfAnswers = 0;
			while (answers.hasNext()) {
				numOfAnswers++;
				System.out.print(numOfAnswers+" ");
				printer.write(answers.next());
				writer.flush();
			}
			System.out.println("Query answers are: " + answers.getCorrectness());
			System.out.println("Number of answers: " + numOfAnswers);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println();
	}
	
	/**
	 * Prints out the answers given by {@code reasoner} to the query
	 * ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 */
	public static Map<Term, List<Term>> getAllDifferentSets(final PositiveLiteral queryAtom, final Reasoner reasoner) {
		Map<Term, List<Term>> result = new HashMap<>();
		try (final QueryResultIterator answers = reasoner.answerQuery(queryAtom, true)) {
			while (answers.hasNext()) {
				List<Term> ans = answers.next().getTerms();
				if (result.containsKey(ans.get(ans.size()-2))) {
					result.get(ans.get(ans.size()-2)).add(ans.get(ans.size()-1));
				} else {
					result.put(ans.get(ans.size()-2), new ArrayList<Term>(Arrays.asList(ans.get(ans.size()-1))));
				}
			}
		}
		return result;
	}

	/**
	 * Prints out the answers given by {@code reasoner} to the query
	 * ({@code queryAtom}).
	 *
	 * @param queryAtom query to be answered
	 * @param reasoner  reasoner to query on
	 */
	public static List<Fact> getQueryAnswerAsListWhyProv(final PositiveLiteral queryAtom, final Reasoner reasoner) {
		List<Fact> answer = new ArrayList<Fact>();
		try (final QueryResultIterator answers = reasoner.answerQuery(queryAtom, true)) {
			while (answers.hasNext()) {
				answer.add(Expressions.makeFact(queryAtom.getPredicate(), answers.next().getTerms()));
			}
		}
		return answer;
	}

	public static long printOutQueryCount(final PositiveLiteral queryAtom, final Reasoner reasoner) {
		final QueryAnswerCount answers = reasoner.countQueryAnswers(queryAtom, true);
		System.out.print("Query " + queryAtom + ": ");
		System.out.println(answers.getCount() + " answers, " + answers.getCorrectness());
		return answers.getCount();
	}

	public static long isDerived(final PositiveLiteral queryAtom, final Reasoner reasoner) {
		final QueryAnswerCount answers = reasoner.countQueryAnswers(queryAtom, true);
		return answers.getCount();
	}
	
	public static long printOutQueryCountMult(final List<Predicate> preds, final Reasoner reasoner) {
		long total = 0;
		for (Predicate p : preds) {
			final Variable x0 = Expressions.makeUniversalVariable("x0");
			final Variable x1 = Expressions.makeUniversalVariable("x1");
			final PositiveLiteral queryAtom = Expressions.makePositiveLiteral(p, x0, x1);
			final QueryAnswerCount answers = reasoner.countQueryAnswers(queryAtom, true);
			System.out.print("Query " + queryAtom + ": ");
			System.out.println(answers.getCount() + " answers, " + answers.getCorrectness());
			total += answers.getCount();
		}
		System.out.println("Intermediate facts generater: " + total);
		return total;
	}
	
	/**
	 * Creates an Atom with @numberOfVariables distinct variables
	 *
	 * @param predicateName for the new predicate
	 * @param arity         number of variables
	 */
	private static PositiveLiteral makeQueryAtom(final String predicateName, final int arity) {
		final List<Term> vars = new ArrayList<>();
		for (int i = 0; i < arity; i++) {
			vars.add(Expressions.makeUniversalVariable("x" + i));
		}
		return Expressions.makePositiveLiteral(predicateName, vars);
	}

	/**
	 * Exports the extension of the Atom with name @predicateName
	 *
	 * @param reasoner reasoner to query on
	 * @param atomName atom's name
	 * @param arity    atom's arity
	 */
	public static void exportQueryAnswersToCSV(final Reasoner reasoner, final String atomName, final int arity)
			throws ReasonerStateException, IOException {
		final PositiveLiteral atom = makeQueryAtom(atomName, arity);
		final String path = ReasoningUtils.OUTPUT_FOLDER + atomName + ".csv";

		final Correctness correctness = reasoner.exportQueryAnswersToCsv(atom, path, true);

		System.out.println("Query answers are: " + correctness);
	}

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 *
	 * Note: The VLog C++ backend performs its own logging. The log-level for this
	 * can be configured using
	 * {@link Reasoner#setLogLevel(org.semanticweb.rulewerk.core.reasoner.LogLevel)}.
	 * It is also possible to specify a separate log file for this part of the logs.
	 */
	public static void configureLogging() {
		// Create the appender that will write log messages to the console.
		final ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.ERROR);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}
	
	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 *
	 * Note: The VLog C++ backend performs its own logging. The log-level for this
	 * can be configured using
	 * {@link Reasoner#setLogLevel(org.semanticweb.rulewerk.core.reasoner.LogLevel)}.
	 * It is also possible to specify a separate log file for this part of the logs.
	 */
	public static void configureLoggingAll() {
		// Create the appender that will write log messages to the console.
		final ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.ALL);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}
	
	public static void printKB(KnowledgeBase kb) {
		for (Statement s : kb.getStatements()) {
			System.out.println(s);
		}
	}
}
