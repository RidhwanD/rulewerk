package org.semanticweb.rulewerk.rpq.converter;

import org.semanticweb.rulewerk.rpq.model.api.AlternRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.ConcatRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.EdgeLabel;
import org.semanticweb.rulewerk.rpq.model.api.KPlusRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.KStarRegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;
import org.semanticweb.rulewerk.rpq.converter.RpqConverterUtils;

public class RpqConverterUtilsTest {
	public static void main(String[] arg) {
		EdgeLabel a = RPQExpressions.makeEdgeLabel("a");
		EdgeLabel b = RPQExpressions.makeEdgeLabel("b");
		ConcatRegExpression ab = RPQExpressions.makeConcatRegExpression(a, b);
		KStarRegExpression abs = RPQExpressions.makeKStarRegExpression(ab);
		KStarRegExpression abss = RPQExpressions.makeKStarRegExpression(abs);
		KStarRegExpression as = RPQExpressions.makeKStarRegExpression(a);
		KStarRegExpression ass = RPQExpressions.makeKStarRegExpression(as);
		KStarRegExpression asss = RPQExpressions.makeKStarRegExpression(ass);
		KPlusRegExpression assp = RPQExpressions.makeKPlusRegExpression(ass);
		ConcatRegExpression ccc = RPQExpressions.makeConcatRegExpression(abss, assp);
		
//		System.out.println(ccc);
//		RegExpression optimal = RpqConverterUtils.optimizeKStar(ccc);
//		System.out.println(optimal);
		
		AlternRegExpression abass = RPQExpressions.makeAlternRegExpression(ab, asss);
		KStarRegExpression ddd = RPQExpressions.makeKStarRegExpression(abass);
		KStarRegExpression bs = RPQExpressions.makeKStarRegExpression(b);
		AlternRegExpression asbs = RPQExpressions.makeAlternRegExpression(ass, bs);
		KStarRegExpression asbss = RPQExpressions.makeKStarRegExpression(asbs);
		
		System.out.println(ddd);
		RegExpression optimal2 = RpqConverterUtils.optimizeKStar(ddd);
		System.out.println(optimal2);
	}
}
