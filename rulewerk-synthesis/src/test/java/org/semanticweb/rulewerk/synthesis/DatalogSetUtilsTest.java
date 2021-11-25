package org.semanticweb.rulewerk.synthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.AbstractConstant;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetConstruct;
import org.semanticweb.rulewerk.core.model.api.SetPredicateType;
import org.semanticweb.rulewerk.core.model.api.SetTerm;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.Statement;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class DatalogSetUtilsTest {
	private final SetVariable u = Expressions.makeSetVariable("U");
	private final SetVariable v = Expressions.makeSetVariable("V");

	private final UniversalVariable x = Expressions.makeUniversalVariable("x");
	private final UniversalVariable y = Expressions.makeUniversalVariable("y");
	private final UniversalVariable z = Expressions.makeUniversalVariable("z");
	
	private final AbstractConstant cr1 = Expressions.makeAbstractConstant("cr1");
	private final AbstractConstant cr2 = Expressions.makeAbstractConstant("cr2");
	private final AbstractConstant cr3 = Expressions.makeAbstractConstant("cr3");
	private final AbstractConstant cr4 = Expressions.makeAbstractConstant("cr4");
	
	private final SetConstruct set1 = Expressions.makeSetConstruct(cr1);
	private final SetConstruct set2 = Expressions.makeSetConstruct(cr2);
	
	private final SetUnion un1 = Expressions.makeSetUnion(set1, u);
	private final SetUnion un2 = Expressions.makeSetUnion(set2, u);
	private final SetUnion un3 = Expressions.makeSetUnion(u, v);
	
	private final Predicate ed = Expressions.makePredicate("edge", 2);
	private final Predicate pt2 = Expressions.makePredicate("path", 2);
	private final Predicate pt3 = Expressions.makePredicate("path", 3);
	
	@Test
	public void testCountSetVarOccurence() {
		Rule r1 = Expressions.makeRule(Expressions.makePositiveLiteral(pt2, x, y), Expressions.makePositiveLiteral(ed, x, y));
		Rule r2 = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un2), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(ed, z, y));
		Rule r3 = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, u), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(pt3, z, y, u));
		Rule r4 = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, set1), Expressions.makePositiveLiteral(ed, x, y));
		assertEquals(DatalogSetUtils.countSetVarOccurenceBody(r1, u), 0);
		assertEquals(DatalogSetUtils.countSetVarOccurenceBody(r2, u), 1);
		assertEquals(DatalogSetUtils.countSetVarOccurenceBody(r3, u), 2);
		assertEquals(DatalogSetUtils.countSetVarOccurenceBody(r4, u), 0);
	}
	
	@Test
	public void testNormalizeNone() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un1), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(ed, z, y));
		Rule nr = DatalogSetUtils.normalize(r);
		assertEquals(r,nr);
	}
	
	@Test
	public void testNormalize1() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un1), Expressions.makePositiveLiteral(pt3, x, z, un2), Expressions.makePositiveLiteral(ed, z, y));
		Rule exp = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un1), Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeSetVariable("V_"+un2.hashCode())), 
				Expressions.makePositiveLiteral(ed, z, y), Expressions.makePositiveLiteral("equal", Expressions.makeSetVariable("V_"+un2.hashCode()), un2));
		Rule nr = DatalogSetUtils.normalize(r);
		assertEquals(nr,exp);
	}
	
	@Test
	public void testNormalize2() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un3), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(pt3, z, y, u), Expressions.makePositiveLiteral(pt3, z, y, v));
		Rule exp = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un3), Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeSetVariable("V_"+(u.hashCode()+1+1))), Expressions.makePositiveLiteral(pt3, z, y, u), 
				Expressions.makePositiveLiteral(pt3, z, y, v), Expressions.makePositiveLiteral("equal", Expressions.makeSetVariable("V_"+(u.hashCode()+1+1)), u));
		Rule nr = DatalogSetUtils.normalize(r);
		assertEquals(nr,exp);
	}
		
	@Test
	public void testNormalize3() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, u), Expressions.makePositiveLiteral(pt3, x, z, un3), Expressions.makePositiveLiteral(pt3, z, y, u), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(pt3, x, y, v));
		Rule exp = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, u), Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeSetVariable("V_"+un3.hashCode())), Expressions.makePositiveLiteral(pt3, z, y, Expressions.makeSetVariable("V_"+(u.hashCode()+2+1))), 
				Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(pt3, x, y, v), Expressions.makePositiveLiteral("equal", Expressions.makeSetVariable("V_"+un3.hashCode()), un3), Expressions.makePositiveLiteral("equal", Expressions.makeSetVariable("V_"+(u.hashCode()+2+1)), u));
		Rule nr = DatalogSetUtils.normalize(r);
		assertEquals(nr,exp);
	}
	
	@Test
	public void testOrder1() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, u), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(ed, z, y));
		List<SetTerm> order = DatalogSetUtils.getOrder(r);
		assertTrue(order.isEmpty());
	}
	
	@Test
	public void testOrder2() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un1), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(pt3, z, y, set1));
		List<SetTerm> exp = new ArrayList<>(Arrays.asList(set1, un1));
		List<SetTerm> order = DatalogSetUtils.getOrder(r);
		assertEquals(order,exp);
	}
	
	@Test
	public void testRepLitIN() {
		Literal l = Expressions.makePositiveLiteral(Expressions.makeSetPredicate("element", 2, SetPredicateType.IS_ELEMENT_OF), cr1, u);
		Literal exp = Expressions.makePositiveLiteral(Expressions.makePredicate("in", 2), cr1, Expressions.makeUniversalVariable("v_("+u.getName()+")"));
		Literal rep = DatalogSetUtils.replaceLiteral(l);
		assertEquals(rep,exp);
	}
	
	@Test
	public void testRepLitSUB() {
		Literal l = Expressions.makePositiveLiteral(Expressions.makeSetPredicate("subset", 2, SetPredicateType.IS_SUBSET_OF), set1, u);
		Literal exp = Expressions.makePositiveLiteral(Expressions.makePredicate("sub", 2), Expressions.makeUniversalVariable("v_("+set1.getName()+")"), Expressions.makeUniversalVariable("v_("+u.getName()+")"));
		Literal rep = DatalogSetUtils.replaceLiteral(l);
		assertEquals(rep,exp);
	}
	
	@Test
	public void testRepLit() {
		Literal l = Expressions.makePositiveLiteral(Expressions.makePredicate("tes", 3), cr1, set1, u);
		Literal exp = Expressions.makePositiveLiteral(Expressions.makePredicate("tes", 3), cr1, Expressions.makeUniversalVariable("v_("+set1.getName()+")"), Expressions.makeUniversalVariable("v_("+u.getName()+")"));
		Literal rep = DatalogSetUtils.replaceLiteral(l);
		assertEquals(rep,exp);
	}
	
	@Test
	public void testTransformFact1() {
		Fact f = Expressions.makeFact(Expressions.makePredicate("tes", 2), cr1, cr2);
		Set<Statement> tf = DatalogSetUtils.transform(f);
		Set<Statement> exp = new HashSet<>(Arrays.asList(f));
		assertEquals(tf,exp);
	}
	
	@Test
	public void testTransformFact2() {
		Fact f = Expressions.makeFact(Expressions.makePredicate("tes", 2), set2, cr1);
		Set<Statement> tf = DatalogSetUtils.transform(f);
		Set<Statement> exp = new HashSet<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("getSU", cr2, Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})"))));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("tes", Expressions.makeUniversalVariable("v_({cr2})"), cr1),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral("SU", cr2, Expressions.makeUniversalVariable("v_({})"), Expressions.makeUniversalVariable("v_({cr2})"))));
		assertEquals(tf,exp);
	}
	
	@Test
	public void testTransformRule1() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt2, x, y), Expressions.makePositiveLiteral(pt2, x, z), Expressions.makePositiveLiteral(ed, z, y));
		Set<Statement> tf = DatalogSetUtils.transform(r);
		Set<Statement> exp = new HashSet<>(Arrays.asList(r));
		assertEquals(tf,exp);
	}
	
	@Test
	public void testTransformRule2() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, un1), Expressions.makePositiveLiteral(pt3, x, z, u), Expressions.makePositiveLiteral(ed, z, y));
		Set<Statement> tf = DatalogSetUtils.transform(r);
		Set<Statement> exp = new HashSet<>();
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("getSU", cr1, Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeUniversalVariable("v_(U)")), Expressions.makePositiveLiteral(ed, z, y),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})"))));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral("getU", Expressions.makeUniversalVariable("v_({cr1})"), Expressions.makeUniversalVariable("v_(U)")),
				Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeUniversalVariable("v_(U)")), Expressions.makePositiveLiteral(ed, z, y),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral("SU", cr1, Expressions.makeUniversalVariable("v_({})"), Expressions.makeUniversalVariable("v_({cr1})"))));
		exp.add(Expressions.makeRule(Expressions.makePositiveLiteral(pt3, x, y, Expressions.makeUniversalVariable("v_({cr1} U $U)")),
				Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeUniversalVariable("v_(U)")), Expressions.makePositiveLiteral(ed, z, y),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral("SU", cr1, Expressions.makeUniversalVariable("v_({})"), Expressions.makeUniversalVariable("v_({cr1})")),
				Expressions.makePositiveLiteral("U", Expressions.makeUniversalVariable("v_({cr1})"), Expressions.makeUniversalVariable("v_(U)"), Expressions.makeUniversalVariable("v_({cr1} U $U)"))));
		assertEquals(tf,exp);
	}
	
	@Test
	public void testSetDiff() {
		Set<Constant> s1 = new HashSet<Constant>(Arrays.asList(cr1, cr2));
		Set<Constant> s2 = new HashSet<Constant>(Arrays.asList(cr2, cr3));
		Set<Constant> s3 = new HashSet<Constant>(Arrays.asList(cr3, cr4));
		Set<Constant> s4 = new HashSet<Constant>(Arrays.asList(cr1));
		Set<Constant> res1 = DatalogSetUtils.setDifference(s1,s3);
		Set<Constant> res2 = DatalogSetUtils.setDifference(s1,s2);
		assertEquals(res1,s1);
		assertEquals(res2,s4);
	}
	
	@Test
	public void testSimplify1() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral("getU", Expressions.makeUniversalVariable("v_({cr1})"), Expressions.makeUniversalVariable("v_(U)")),
				Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeUniversalVariable("v_(U)")), Expressions.makePositiveLiteral(ed, z, y),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral("SU", cr1, Expressions.makeUniversalVariable("v_({})"), Expressions.makeUniversalVariable("v_({cr1})")));
		Rule sr = DatalogSetUtils.simplify(r);
		assertEquals(sr,r);
	}
	
	@Test
	public void testSimplify2() {
		Rule r = Expressions.makeRule(Expressions.makePositiveLiteral("getSU", cr1, Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral(pt3, x, z, Expressions.makeUniversalVariable("v_(U)")), Expressions.makePositiveLiteral(ed, z, y),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})")));
		Rule exp = Expressions.makeRule(Expressions.makePositiveLiteral("getSU", cr1, Expressions.makeUniversalVariable("v_({})")),
				Expressions.makePositiveLiteral("empty", Expressions.makeUniversalVariable("v_({})")));
		Rule sr = DatalogSetUtils.simplify(r);
		assertEquals(sr,exp);
	}
}
