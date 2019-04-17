package org.semanticweb.vlog4j.core.reasoner.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeConstant;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makePositiveLiteral;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeRule;
import static org.semanticweb.vlog4j.core.model.implementation.Expressions.makeVariable;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.PositiveLiteral;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.Rule;
import org.semanticweb.vlog4j.core.model.api.Variable;
import org.semanticweb.vlog4j.core.model.implementation.PredicateImpl;
import org.semanticweb.vlog4j.core.reasoner.CyclicityResult;
import org.semanticweb.vlog4j.core.reasoner.Reasoner;
import org.semanticweb.vlog4j.core.reasoner.exceptions.EdbIdbSeparationException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.IncompatiblePredicateArityException;
import org.semanticweb.vlog4j.core.reasoner.exceptions.ReasonerStateException;

import karmaresearch.vlog.NotStartedException;

public class AcyclicityTest {

	final Variable x = makeVariable("x");
	final Variable y = makeVariable("y");
	final Variable z = makeVariable("z");
	final Variable w = makeVariable("w");
	final Variable v = makeVariable("v");
	final Variable u = makeVariable("u");

	final Constant a = makeConstant("a");
	final Constant b = makeConstant("b");
	final Constant c = makeConstant("c");
	final Constant d = makeConstant("d");
	final Constant e = makeConstant("e");

	final Predicate edbC = new PredicateImpl("edbC", 1);
	final Predicate idbC = new PredicateImpl("idbC", 1);
	final Predicate edbD = new PredicateImpl("edbD", 1);
	final Predicate idbD = new PredicateImpl("idbD", 1);
	final Predicate edbE = new PredicateImpl("edbE", 1);
	final Predicate idbE = new PredicateImpl("idbE", 1);

	final Predicate edbR = new PredicateImpl("edbR", 2);
	final Predicate idbR = new PredicateImpl("idbR", 2);
	final Predicate edbS = new PredicateImpl("edbS", 2);
	final Predicate idbS = new PredicateImpl("idbS", 2);
	final Predicate edbV = new PredicateImpl("edbV", 2);
	final Predicate idbV = new PredicateImpl("idbV", 2);

	final Rule mappingRuleC = makeRule(makePositiveLiteral(idbC, x), makePositiveLiteral(edbC, x));
	final Rule mappingRuleD = makeRule(makePositiveLiteral(idbD, x), makePositiveLiteral(edbD, x));
	final Rule mappingRuleR = makeRule(makePositiveLiteral(idbR, x, y), makePositiveLiteral(edbR, x, y));
	final Rule mappingRuleS = makeRule(makePositiveLiteral(idbS, x, y), makePositiveLiteral(edbS, x, y));
	final Rule mappingRuleV = makeRule(makePositiveLiteral(idbV, x, y), makePositiveLiteral(edbV, x, y));

	final PositiveLiteral factInstEDBC = makePositiveLiteral(edbC, c);
	final PositiveLiteral factInstEDBD = makePositiveLiteral(edbD, c);
	final PositiveLiteral factInstEDBR = makePositiveLiteral(edbR, c, d);
	final PositiveLiteral factInstEDBS = makePositiveLiteral(edbS, c, d);
	final PositiveLiteral factInstEDBV = makePositiveLiteral(edbV, c, d);

	@Test
	public void testAcyclicityJA() throws ReasonerStateException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, NotStartedException {
		// JA, RJA, MFA, RMFA, not MFC, not RMFC, and CyclicityResult.ACYCLIC

		// idbS(?y, ?z) :- idbR(?x, ?y)
		final Rule rule1 = makeRule(makePositiveLiteral(idbS, y, z), makePositiveLiteral(idbR, x, y));
		// idbV(?y, ?z) :- idbS(?x, ?y)
		final Rule rule2 = makeRule(makePositiveLiteral(idbV, y, z), makePositiveLiteral(idbS, x, y));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(mappingRuleR, mappingRuleS, mappingRuleV, rule1, rule2);
			reasoner.addFacts(factInstEDBR, factInstEDBS, factInstEDBV);
			reasoner.load();
			assertTrue(reasoner.isJA());
			assertTrue(reasoner.isRJA());
			assertTrue(reasoner.isMFA());
			assertTrue(reasoner.isRMFA());
			assertFalse(reasoner.isMFC());
			// Uncomment the following line when the RMFC check is implemented
			// assertFalse(reasoner.isRMFC());
			assertEquals(CyclicityResult.ACYCLIC, reasoner.checkForCycles());
		}
	}

	@Test
	public void testAcyclicityMFA() throws ReasonerStateException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, NotStartedException {
		// JA, not RJA, MFA, RMFA, not MFC, not RMFC, and CyclicityResult.ACYCLIC

		// idbS(?y, ?z) :- idbC(?x), idbR(?x, ?y)
		final Rule rule1 = makeRule(makePositiveLiteral(idbS, y, z), makePositiveLiteral(idbC, x),
				makePositiveLiteral(idbR, x, y));
		// idbS(?y, ?z) :- idbC(?x), idbR(?x, ?y)
		final Rule rule2 = makeRule(makePositiveLiteral(idbR, y, z), makePositiveLiteral(idbD, x),
				makePositiveLiteral(idbS, x, y));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(mappingRuleC, mappingRuleD, mappingRuleR, mappingRuleS, rule1, rule2);
			reasoner.addFacts(factInstEDBC, factInstEDBD, factInstEDBR, factInstEDBS);

			reasoner.load();
			assertFalse(reasoner.isJA());
			assertFalse(reasoner.isRJA());
			assertTrue(reasoner.isMFA());
			// Uncommenting the following line results in a "fatal error"
			// assertTrue(reasoner.isRMFA());
			assertFalse(reasoner.isMFC());
			// Uncomment the following line when the RMFC check is implemented
			// assertFalse(reasoner.isRMFC());
			assertEquals(CyclicityResult.ACYCLIC, reasoner.checkForCycles());
		}
	}

	@Test
	public void testAcyclicityRJA1() throws ReasonerStateException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, NotStartedException {
		// JA, RJA, not MFA, RMFA, not MFC, not RMFC, and CyclicityResult.ACYCLIC

		final Rule rule1 = makeRule(makePositiveLiteral(idbR, y, z), makePositiveLiteral(idbR, x, y));
		// idbR(?y, ?z) :- idbR(?x, ?y)
		final Rule rule2 = makeRule(makePositiveLiteral(idbR, y, x), makePositiveLiteral(idbR, x, y));
		// idbR(?y, ?x) :- idbR(?x, ?y)

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(mappingRuleC, mappingRuleD, mappingRuleR, mappingRuleS, rule1, rule2);
			reasoner.addFacts(factInstEDBC, factInstEDBD, factInstEDBR, factInstEDBS);

			reasoner.load();
			assertFalse(reasoner.isJA());
			assertTrue(reasoner.isRJA());
			assertFalse(reasoner.isMFA());
			assertTrue(reasoner.isRMFA());
			assertFalse(reasoner.isMFC());
			// Uncomment the following line when the RMFC check is implemented
			// assertFalse(reasoner.isRMFC());
			assertEquals(CyclicityResult.ACYCLIC, reasoner.checkForCycles());
		}
	}

	@Test
	public void testAcyclicityRJA2() throws ReasonerStateException, EdbIdbSeparationException,
			IncompatiblePredicateArityException, IOException, NotStartedException {
		// JA, RJA, not MFA, RMFA, not MFC, not RMFC, and CyclicityResult.ACYCLIC

		final Rule rule1 = makeRule(makePositiveLiteral(idbR, x, y), makePositiveLiteral(idbC, x));
		final Rule rule2 = makeRule(makePositiveLiteral(idbC, z), makePositiveLiteral(idbR, v, z));
		final Rule rule3 = makeRule(makePositiveLiteral(idbR, w, u), makePositiveLiteral(idbR, u, w));

		try (final Reasoner reasoner = Reasoner.getInstance()) {
			reasoner.addRules(mappingRuleC, mappingRuleD, mappingRuleR, mappingRuleS, rule1, rule2, rule3);
			reasoner.addFacts(factInstEDBC, factInstEDBD, factInstEDBR, factInstEDBS);

			reasoner.load();
			assertFalse(reasoner.isJA());
			assertTrue(reasoner.isRJA());
			assertFalse(reasoner.isMFA());
			assertTrue(reasoner.isRMFA());
			assertFalse(reasoner.isMFC());
			// Uncomment the following line when the RMFC check is implemented
			// assertFalse(reasoner.isRMFC());
			assertEquals(CyclicityResult.ACYCLIC, reasoner.checkForCycles());
		}
	}

	// @Test
	// public void testCyclicity() throws ReasonerStateException,
	// EdbIdbSeparationException,
	// IncompatiblePredicateArityException, IOException, NotStartedException {
	// final Literal epXY = makePositiveLiteral("ep", x, y);
	// final PositiveLiteral ipXY = makePositiveLiteral("ip", x, y);
	// final PositiveLiteral ipYZ = makePositiveLiteral("ip", y, z);
	//
	// // ip(?x, ?y) :- ep(?x, ?y)
	// final Rule rule1 = makeRule(ipXY, epXY);
	// // ip(?y, ?z) :- ip(?x, ?y)
	// final Rule rule2 = makeRule(ipYZ, ipXY);
	// final PositiveLiteral fact = makePositiveLiteral("ep", c, d);
	//
	// try (final Reasoner reasoner = Reasoner.getInstance()) {
	// reasoner.addRules(rule1);
	// reasoner.addRules(rule2);
	// reasoner.addFacts(fact);
	//
	// reasoner.load();
	// assertEquals(CyclicityResult.CYCLIC, reasoner.checkForCycles());
	// }
	// }
	//
	// @Test
	// public void testAcyclicity() throws ReasonerStateException,
	// EdbIdbSeparationException,
	// IncompatiblePredicateArityException, IOException, NotStartedException {
	// final Literal epXY = makePositiveLiteral("ep", x, y);
	// final PositiveLiteral ip1XY = makePositiveLiteral("ip1", x, y);
	// final PositiveLiteral ip2YZ = makePositiveLiteral("ip2", y, z);
	//
	// // ip1(?x, ?y) :- ep(?x, ?y)
	// final Rule rule1 = makeRule(ip1XY, epXY);
	// // ip2(?y, ?z) :- ip1(?x, ?y)
	// final Rule rule2 = makeRule(ip2YZ, ip1XY);
	// final PositiveLiteral fact = makePositiveLiteral("ep", c, d);
	//
	// try (final Reasoner reasoner = Reasoner.getInstance()) {
	// reasoner.addRules(rule1);
	// reasoner.addRules(rule2);
	// reasoner.addFacts(fact);
	//
	// reasoner.load();
	// assertEquals(CyclicityResult.ACYCLIC, reasoner.checkForCycles());
	// }
	// }
}
