package org.semanticweb.rulewerk.core.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetConstruct;
import org.semanticweb.rulewerk.core.model.api.SetPredicate;
import org.semanticweb.rulewerk.core.model.api.SetPredicateType;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Variable;

public class SetExpressionTest {
	final Predicate p = Expressions.makePredicate("p", 3);
	final SetPredicate in = Expressions.makeSetPredicate("elementOf", 2, SetPredicateType.IS_ELEMENT_OF);
	final SetPredicate subset = Expressions.makeSetPredicate("subsetOf", 2, SetPredicateType.IS_SUBSET_OF);
	final Constant c = Expressions.makeAbstractConstant("c");
	final Constant d = Expressions.makeAbstractConstant("d");
	final Variable x = Expressions.makeUniversalVariable("X");
	final Variable y = Expressions.makeUniversalVariable("Y");
	final SetVariable u = Expressions.makeSetVariable("U");
	final SetConstruct set1 = Expressions.makeSetConstruct(c);
	final SetConstruct set2 = Expressions.makeSetConstruct(d);
	final SetUnion un1 = Expressions.makeSetUnion(set1, u);
	final SetUnion un2 = Expressions.makeSetUnion(set1, set2);
	
	@Test
	public void factsWithSetTermConstructor() {
		final Fact f1 = Expressions.makeFact(p, Arrays.asList(un2, set2, c));
		final Fact f2 = Expressions.makeFact("p", Arrays.asList(un2, set2, c));
		final Fact f3 = new FactImpl(p, Arrays.asList(un2, set2, c));
		
		assertEquals(f1, f2);
		assertEquals(f1, f3);
		assertEquals(f2, f3);
	}
	
	@Test
	public void setLiteralWithSetTermConstructor() {
		final PositiveLiteral t1 = Expressions.makePositiveLiteral(in, x, u);
		final PositiveLiteral t2 = new PositiveLiteralImpl(in, Arrays.asList(x, u));
		
		final PositiveLiteral t3 = Expressions.makePositiveLiteral(subset, u, un1);
		final PositiveLiteral t4 = new PositiveLiteralImpl(subset, Arrays.asList(u, un1));
		
		assertEquals(t1, t1);
		assertEquals(t1, t2);
		assertEquals(t3, t3);
		assertEquals(t3, t4);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noSetVarInFact() {
		final Predicate p = Expressions.makePredicate("p", 1);
		new FactImpl(p, Arrays.asList(u));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noElementFact() {
		new FactImpl(in, Arrays.asList(u, set1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noSubsetFact() {
		new FactImpl(subset, Arrays.asList(set1, set2));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setInSetTest() {
		new PositiveLiteralImpl(in, Arrays.asList(set1, un1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noSetInNoSetTest() {
		new PositiveLiteralImpl(in, Arrays.asList(c, d));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setSubsetNoSetTest() {
		new PositiveLiteralImpl(subset, Arrays.asList(set1, c));
		new PositiveLiteralImpl(subset, Arrays.asList(un1, c));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noSetSubsetSetTest() {
		new PositiveLiteralImpl(subset, Arrays.asList(c, set2));
		new PositiveLiteralImpl(subset, Arrays.asList(c, un1));
	}
	
	@Test
	public void setFactToStringTest() {
		final Fact f1 = Expressions.makeFact(p, Arrays.asList(un2, set2, c));
		assertEquals("p({c} U {d}, {d}, c) .", f1.toString());
	}
	
	@Test
	public void testSetRuleGetters() {
		final Literal atom1 = Expressions.makePositiveLiteral(in, x, u);
		final Literal atom2 = Expressions.makePositiveLiteral("p", x, y);
		final Literal atom3 = Expressions.makePositiveLiteral("s", u, y);
		final PositiveLiteral atom4 = Expressions.makePositiveLiteral("r", u, y);
		final PositiveLiteral atom5 = Expressions.makePositiveLiteral("s", x, set1);
		final Conjunction<Literal> body = Expressions.makeConjunction(atom1, atom2, atom3);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom4, atom5);
		final Rule rule = Expressions.makeRule(head, body);
		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
	}
	
	@Test
	public void testSetRuleEquals() {
		final Literal atom1 = Expressions.makePositiveLiteral("s", u, y);
		final Literal atom3 = Expressions.makePositiveLiteral("s", u, x);
		final PositiveLiteral atom2 = Expressions.makePositiveLiteral("r", u, y);
		final PositiveLiteral atom4 = Expressions.makePositiveLiteral("r", u, x);
		final Conjunction<Literal> body = Expressions.makeConjunction(atom1);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom2);
		final Rule rule1 = Expressions.makeRule(head, body);
		final Rule rule2 = new RuleImpl(head, body);
		final Rule rule3 = Expressions.makeRule(atom2, atom1);
		final Rule rule4 = Expressions.makeRule(atom4, atom3);
		
		assertEquals(rule1, rule1);
		assertEquals(rule2, rule1);
		assertEquals(rule2.hashCode(), rule1.hashCode());
		assertEquals(rule3, rule1);
		assertEquals(rule3.hashCode(), rule1.hashCode());
		assertNotEquals(rule4, rule1);
		assertFalse(rule1.equals(null));
		assertFalse(rule1.equals(c));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void noSpecPredInHead() {
		final PositiveLiteral atom1 = Expressions.makePositiveLiteral(in, x, u);
		final Literal atom2 = Expressions.makePositiveLiteral("r", u, x);
		final Conjunction<Literal> body = Expressions.makeConjunction(atom2);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom1);
		final Rule rule = Expressions.makeRule(head, body);
		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setPredInNotReoccur() {
		final Literal atom1 = Expressions.makePositiveLiteral(in, x, u);
		final PositiveLiteral atom2 = Expressions.makePositiveLiteral("r", u, x);
		final Conjunction<Literal> body = Expressions.makeConjunction(atom1);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom2);
		final Rule rule = Expressions.makeRule(head, body);
		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setPredInExistentialRule() {
		final Variable z = Expressions.makeExistentialVariable("Z");
		final Literal atom1 = Expressions.makePositiveLiteral("p", x, u);
		final PositiveLiteral atom2 = Expressions.makePositiveLiteral("r", u, x, z);
		final Conjunction<Literal> body = Expressions.makeConjunction(atom1);
		final Conjunction<PositiveLiteral> head = Expressions.makePositiveConjunction(atom2);
		final Rule rule = Expressions.makeRule(head, body);
		assertEquals(body, rule.getBody());
		assertEquals(head, rule.getHead());
	}
	
}
