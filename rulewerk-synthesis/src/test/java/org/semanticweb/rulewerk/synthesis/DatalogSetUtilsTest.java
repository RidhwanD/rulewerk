package org.semanticweb.rulewerk.synthesis;

import java.util.Set;

import org.semanticweb.rulewerk.core.model.api.Statement;

public class DatalogSetUtilsTest {
	public static void main(String[] arg) {
		Set<Statement> r_su = DatalogSetUtils.getR_SU();
		
		for (Statement s : r_su) {
			System.out.println(s);
		}
	}
}
