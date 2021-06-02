package org.semanticweb.rulewerk.core.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.SetPredicate;
import org.semanticweb.rulewerk.core.model.api.SetPredicateType;

public class SetPredicateImplTest {
	
	@Test
	public void testEquals() {
		final SetPredicate p1 = new SetPredicateImpl("p", 1, SetPredicateType.NORMAL);
		final SetPredicate p1too = Expressions.makeSetPredicate("p", 1, SetPredicateType.NORMAL);
		final SetPredicate p2 = new SetPredicateImpl("p", 2, SetPredicateType.NORMAL);
		final SetPredicate p2i = new SetPredicateImpl("p", 2, SetPredicateType.IS_ELEMENT_OF);
		final SetPredicate p2s = new SetPredicateImpl("p", 2, SetPredicateType.IS_SUBSET_OF);
		final SetPredicate q1 = new SetPredicateImpl("q", 1, SetPredicateType.NORMAL);
		final SetPredicate q2i = new SetPredicateImpl("q", 2, SetPredicateType.IS_ELEMENT_OF);
		final SetPredicate q2s = new SetPredicateImpl("q", 2, SetPredicateType.IS_SUBSET_OF);

		assertEquals(p1, p1);
		assertEquals(p1too, p1);
		assertNotEquals(p2, p1);
		assertNotEquals(p2, p2i);
		assertNotEquals(p2, p2s);
		assertNotEquals(p2i, p2s);
		assertNotEquals(q1, p1);
		assertNotEquals(q2i, p2i);
		assertNotEquals(q2s, p2s);
		assertNotEquals(p2.hashCode(), p1.hashCode());
		assertNotEquals(q1.hashCode(), p1.hashCode());
		assertFalse(p1.equals(null)); // written like this for recording coverage properly
		assertFalse(p1.equals("p")); // written like this for recording coverage properly
	}
	
	@Test
	public void testGetter() {
		final SetPredicate p = new SetPredicateImpl("p", 2, SetPredicateType.NORMAL);
		final SetPredicate pi = new SetPredicateImpl("p", 2, SetPredicateType.IS_ELEMENT_OF);
		final SetPredicate ps = new SetPredicateImpl("p", 2, SetPredicateType.IS_SUBSET_OF);
		
		assertEquals(p.getPredicateType(), SetPredicateType.NORMAL);
		assertEquals(pi.getPredicateType(), SetPredicateType.IS_ELEMENT_OF);
		assertEquals(ps.getPredicateType(), SetPredicateType.IS_SUBSET_OF);
	}

	@Test
	public void predicateToStringTest() {
		final SetPredicate p = new SetPredicateImpl("p", 2, SetPredicateType.NORMAL);
		final SetPredicate pi = new SetPredicateImpl("p", 2, SetPredicateType.IS_ELEMENT_OF);
		final SetPredicate ps = new SetPredicateImpl("p", 2, SetPredicateType.IS_SUBSET_OF);
		
		assertEquals("p[2]", p.toString());
		assertEquals("p[2]", pi.toString());
		assertEquals("p[2]", ps.toString());
	}

	@Test(expected = NullPointerException.class)
	public void predicateNameNotNull() {
		new SetPredicateImpl(null, 2, SetPredicateType.NORMAL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotEmpty() {
		new SetPredicateImpl("", 1, SetPredicateType.NORMAL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void predicateNameNotWhitespace() {
		new SetPredicateImpl(" ", 1, SetPredicateType.NORMAL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void arityNegative() {
		new SetPredicateImpl("p", -1, SetPredicateType.NORMAL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void arityZero() {
		new SetPredicateImpl("p", 0, SetPredicateType.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void predicateTypeNotNull() {
		new SetPredicateImpl("p", 2, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void arityElementNotTwo() {
		new SetPredicateImpl("p", 1, SetPredicateType.IS_ELEMENT_OF);
	}

	@Test(expected = IllegalArgumentException.class)
	public void aritySubsetNotTwo() {
		new SetPredicateImpl("p", 1, SetPredicateType.IS_SUBSET_OF);
	}
	
}
