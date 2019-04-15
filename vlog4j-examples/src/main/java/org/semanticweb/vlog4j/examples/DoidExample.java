package org.semanticweb.vlog4j.examples;

/*-
 * #%L
 * VLog4j Examples
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePredicate;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.vlog4j.core.model.api.Atom;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.reasoner.DataSource;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;
import org.semanticweb.vlog4j.core.reasoner.implementation.QueryResultIterator;
import org.semanticweb.vlog4j.core.reasoner.implementation.RdfFileDataSource;
import org.semanticweb.vlog4j.core.reasoner.implementation.SparqlQueryResultDataSource;
import org.semanticweb.vlog4j.graal.GraalToVLog4JModelConverter;

import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * This example reasons about human diseases, based on information from the
 * Disease Ontology (DOID) and Wikidata. It illustrates how to load data from
 * different sources (RDF file, SPARQL), and reason about these input using
 * rules that are loaded from a file. The rules used here employ existential
 * quantifiers and stratified negation.
 * 
 * @author Markus Kroetzsch
 */
public class DoidExample {

	public static void main(String[] args)
			throws ReasonerStateException, IOException, EdbIdbSeparationException, IncompatiblePredicateArityException {

		/* Create rules with negated literals (they will be used latter) */
		final Variable x = makeVariable("x");
		final Variable y = makeVariable("y");
		final Variable z = makeVariable("z");
		final Atom neg_hasDoid = Expressions.makeAtom("neg_hasDoid", y);
		final Atom neg_cancerDisease = Expressions.makeAtom("neg_cancerDisease", z);
		final Atom diseaseId = Expressions.makeAtom("diseaseId", y, z);
		final Atom deathCause = Expressions.makeAtom("deathCause", x, y);
		final Atom humansWhoDiedOfCancer = Expressions.makeAtom("humansWhoDiedOfCancer", x);
		final Atom humansWhoDiedOfNoncancer = Expressions.makeAtom("humansWhoDiedOfNoncancer", x);
		final Rule rule1 = Expressions.makeRule(Expressions.makeConjunction(humansWhoDiedOfNoncancer),
				Expressions.makeConjunction(deathCause, diseaseId, neg_cancerDisease));
		final Rule rule2 = Expressions.makeRule(Expressions.makeConjunction(humansWhoDiedOfNoncancer),
				Expressions.makeConjunction(deathCause, neg_hasDoid));

		final URL wikidataSparqlEndpoint = new URL("https://query.wikidata.org/sparql");

		try (final Reasoner reasoner = Reasoner.getInstance()) {

			/* Configure RDF data source */
			final Predicate doidTriplePredicate = makePredicate("doidTriple", 3);
			final DataSource doidDataSource = new RdfFileDataSource(
					new File(ExamplesUtils.INPUT_FOLDER + "doid.nt.gz"));
			reasoner.addFactsFromDataSource(doidTriplePredicate, doidDataSource);

			/* Configure SPARQL data sources */
			final String sparqlHumansWithDisease = "?disease wdt:P699 ?doid .";
			// (wdt:P669 = "Disease Ontology ID")
			final DataSource diseasesDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"disease,doid", sparqlHumansWithDisease);
			final Predicate diseaseIdPredicate = Expressions.makePredicate("diseaseId", 2);
			reasoner.addFactsFromDataSource(diseaseIdPredicate, diseasesDataSource);

			final String sparqlRecentDeaths = "?human wdt:P31 wd:Q5; wdt:P570 ?deathDate . FILTER (YEAR(?deathDate) = 2018)";
			// (wdt:P31 = "instance of"; wd:Q5 = "human", wdt:570 = "date of death")
			final DataSource recentDeathsDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint, "human",
					sparqlRecentDeaths);
			final Predicate recentDeathsPredicate = Expressions.makePredicate("recentDeaths", 1);
			reasoner.addFactsFromDataSource(recentDeathsPredicate, recentDeathsDataSource);

			final String sparqlRecentDeathsCause = sparqlRecentDeaths + "?human wdt:P509 ?causeOfDeath . ";
			// (wdt:P509 = "cause of death")
			final DataSource recentDeathsCauseDataSource = new SparqlQueryResultDataSource(wikidataSparqlEndpoint,
					"human,causeOfDeath", sparqlRecentDeathsCause);
			final Predicate recentDeathsCausePredicate = Expressions.makePredicate("recentDeathsCause", 2);
			reasoner.addFactsFromDataSource(recentDeathsCausePredicate, recentDeathsCauseDataSource);

			/* Load rules from DLGP file */
			List<Rule> vlogRules = new ArrayList<>();
			try (final DlgpParser parser = new DlgpParser(
					new File(ExamplesUtils.INPUT_FOLDER + "/graal", "doid-example.dlgp"))) {
				while (parser.hasNext()) {
					final Object object = parser.next();
					if (object instanceof fr.lirmm.graphik.graal.api.core.Rule) {
						vlogRules.add(
								GraalToVLog4JModelConverter.convertRule((fr.lirmm.graphik.graal.api.core.Rule) object));
					}
				}
			}

			vlogRules.add(rule1);
			vlogRules.add(rule2);

			/* Add all rules to the reasoner */
			reasoner.addRules(vlogRules);

			System.out.println("Rules configured:\n--");
			vlogRules.forEach(System.out::println);
			System.out.println("--");
			reasoner.load();
			System.out.println("Loading completed.");
			System.out.println("Starting reasoning (including SPARQL query answering) ...");
			reasoner.reason();
			System.out.println("... reasoning completed.");

			final QueryResultIterator answersCancer = reasoner.answerQuery(humansWhoDiedOfCancer, true);
			System.out.println("Number of humans who died of Cancer: " + ExamplesUtils.iteratorSize(answersCancer));

			final QueryResultIterator answersNoncancer = reasoner.answerQuery(humansWhoDiedOfNoncancer, true);
			System.out.println(
					"Number of humans who died of Non cancer: " + ExamplesUtils.iteratorSize(answersNoncancer));

		}

	}

}
