package org.semanticweb.rulewerk.core.model.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.SetConstruct;
import org.semanticweb.rulewerk.core.model.api.SetTerm;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;

public class SetTermImplTest {
	Term t1 = new AbstractConstantImpl("t1");
	Term t2 = new AbstractConstantImpl("t2");
	SetConstruct c = new SetConstructImpl(t1);
	SetConstruct d = new SetConstructImpl();
	SetVariable v = new SetVariableImpl("V");
	SetUnion u = new SetUnionImpl(c, v);
	
	@Test
	public void setConstructGetterTest() {
		assertEquals("{t1}", c.getName());
		assertEquals(TermType.SET_CONSTRUCT, c.getType());
		assertEquals(t1, c.getElement());
		assertFalse(c.isEmpty());
		assertEquals("{}", d.getName());
		assertEquals(TermType.SET_CONSTRUCT, d.getType());
		assertEquals(null, d.getElement());
		assertTrue(d.isEmpty());
	}
	
	@Test
	public void setUnionGetterTest() {
		assertEquals("{t1} U $V", u.getName());
		assertEquals(TermType.SET_UNION, u.getType());
		assertEquals(c, u.getSetTerm1());
		assertEquals(v, u.getSetTerm2());
		assertEquals(new HashSet<SetTerm>(Arrays.asList(c,v)), u.getSubTerms());
	}

	@Test
	public void setVariableGetterTest() {
		assertEquals("V", v.getName());
		assertEquals(TermType.SET_VARIABLE, v.getType());
	}
	
	@Test
	public void setConstructEqualityTest() {
		SetConstruct ctoo = new SetConstructImpl(t1);
		SetConstruct e = new SetConstructImpl(t2);
		
		assertEquals(c, c);
		assertEquals(ctoo, c);
		assertNotEquals(c, d);
		assertNotEquals(e, c);
		assertNotEquals(u, c);
		assertNotEquals(v, c);
		assertEquals(c.hashCode(), ctoo.hashCode());
		assertFalse(c.equals(null)); // written like this for recording coverage properly
	}
	
	@Test
	public void setUnionEqualityTest() {
		SetConstruct ctoo = new SetConstructImpl(t1);
		SetConstruct e = new SetConstructImpl(t2);
		SetUnion u1 = new SetUnionImpl(v, c);
		SetUnion u2 = new SetUnionImpl(ctoo, v);
		SetUnion u3 = new SetUnionImpl(c, e);
		
		assertEquals(u, u);
		assertEquals(u1, u);
		assertEquals(u2, u);
		assertNotEquals(u, c);
		assertNotEquals(u, u3);
		assertEquals(u.hashCode(), u1.hashCode());
		assertEquals(u.hashCode(), u2.hashCode());
		assertFalse(u.equals(null)); // written like this for recording coverage properly
	}
	
	@Test
	public void subTermUnionTest() {
		SetConstruct e = new SetConstructImpl(t2);
		SetUnion u2 = new SetUnionImpl(u, d);
		
		assertTrue(u.isSubTerm(c));
		assertTrue(u.isSubTerm(v));
		assertFalse(u.isSubTerm(d));
		assertFalse(u.isSubTerm(u));
		assertTrue(u2.isSubTerm(c));
		assertTrue(u2.isSubTerm(d));
		assertTrue(u2.isSubTerm(u));
		assertFalse(u2.isSubTerm(e));
	}

	@Test
	public void setVariableToStringTest() {
		assertEquals("$V", v.toString());
	}
	
	@Test
	public void setConstructToStringTest() {
		assertEquals("{t1}", c.toString());
		assertEquals("{}", d.toString());
	}

	@Test
	public void setUnionToStringTest() {
		SetUnion u2 = new SetUnionImpl(u, d);
		assertEquals("{t1} U $V", u.toString());
		assertEquals("{t1} U $V U {}", u2.toString());
	}

	@Test(expected = NullPointerException.class)
	public void setVarNameValueNonNull() {
		new SetVariableImpl(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setVarNameNonEmptyTest() {
		new SetVariableImpl("");
	}

	@Test(expected = NullPointerException.class)
	public void setElementValueNonNull() {
		new SetConstructImpl(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setElementSetTermTest() {
		new SetConstructImpl(v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setElementIllegalNonSetTermTest() {
		Term m = new DatatypeConstantImpl("m", "http://example.org/mystring");
		new SetConstructImpl(m);
	}

	@Test(expected = NullPointerException.class)
	public void setTerm1ValueNonNull() {
		new SetUnionImpl(null, v);
	}

	@Test(expected = NullPointerException.class)
	public void setTerm2ValueNonNull() {
		new SetUnionImpl(v, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setTerm1Test() {
		new SetUnionImpl(t1, v);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setTerm2Test() {
		new SetUnionImpl(v, t1);
	}
	
}
