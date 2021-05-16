package org.semanticweb.rulewerk.rpq.model.api;

import org.semanticweb.rulewerk.core.model.api.Entity;
import org.semanticweb.rulewerk.core.model.api.Term;

public interface NDFAQuery extends Entity {
	public NDFiniteAutomata getNDFA();
	public Term getTerm1();
	public Term getTerm2();
}
