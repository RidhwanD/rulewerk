package org.semanticweb.rulewerk.core.model.implementation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/*-
 * #%L
 * Rulewerk Core Components
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

import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Conjunction;
import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.SetPredicate;
import org.semanticweb.rulewerk.core.model.api.SetPredicateType;
import org.semanticweb.rulewerk.core.model.api.SetUnion;
import org.semanticweb.rulewerk.core.model.api.SetVariable;
import org.semanticweb.rulewerk.core.model.api.StatementVisitor;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.UniversalVariable;

/**
 * Implementation for {@link Rule}. Represents rules with non-empty heads and
 * bodies.
 * 
 * @author Irina Dragoste
 *
 */
public class RuleImpl implements Rule {

	final Conjunction<Literal> body;
	final Conjunction<PositiveLiteral> head;

	/**
	 * Creates a Rule with a non-empty body and an non-empty head. All variables in
	 * the body must be universally quantified; all variables in the head that do
	 * not occur in the body must be existentially quantified.
	 *
	 * @param head list of Literals (negated or non-negated) representing the rule
	 *             body conjuncts.
	 * @param body list of positive (non-negated) Literals representing the rule
	 *             head conjuncts.
	 */
	public RuleImpl(final Conjunction<PositiveLiteral> head, final Conjunction<Literal> body) {
		Validate.notNull(head);
		Validate.notNull(body);
		Validate.notEmpty(body.getLiterals(),
				"Empty rule body not supported. Use Fact objects to assert unconditionally true atoms.");
		Validate.notEmpty(head.getLiterals(),
				"Empty rule head not supported. To capture integrity constraints, use a dedicated predicate that represents a contradiction.");
		if (body.getExistentialVariables().count() > 0) {
			throw new IllegalArgumentException(
					"Rule body cannot contain existential variables. Rule was: " + head + " :- " + body);
		}
		Set<UniversalVariable> bodyVariables = body.getUniversalVariables().collect(Collectors.toSet());
		if (head.getUniversalVariables().filter(x -> !bodyVariables.contains(x)).count() > 0) {
			throw new IllegalArgumentException(
					"Universally quantified variables in rule head must also occur in rule body. Rule was: " + head
							+ " :- " + body);
		}
		this.validateSetRule(head, body);
		
		this.head = head;
		this.body = body;

	}
	
	private void validateSetRule(final Conjunction<PositiveLiteral> head, final Conjunction<Literal> body) {
		for (PositiveLiteral h : head) {
			if (h.getPredicate() instanceof SetPredicate) {
				SetPredicate p = (SetPredicate) h.getPredicate();
				if (p.getPredicateType() == SetPredicateType.IS_ELEMENT_OF || p.getPredicateType() == SetPredicateType.IS_SUBSET_OF)
					throw new IllegalArgumentException(
						"Rule head cannot contains special predicate. Rule was: " + head + " :- " + body);
			}
		}
		
		Set<SetVariable> setVars = head.getSetVariables().collect(Collectors.toSet());
		setVars.addAll(body.getSetVariables().collect(Collectors.toSet()));
		
		for (Term t : setVars) {
			boolean appear = false;
					
			for (Literal l : body) {
				Set<Term> terms = l.getSetTerms().collect(Collectors.toSet());
				Set<Term> newTerms = new HashSet<Term>();
				for (Term ts : terms) {
					if (ts instanceof SetUnion) {
						newTerms.addAll(((SetUnion) ts).getSubTerms());
					}
				}
				terms.addAll(newTerms);
				if (l.getPredicate() instanceof SetPredicate) {
					SetPredicate p = (SetPredicate) l.getPredicate();
					if (p.getPredicateType() == SetPredicateType.NORMAL && terms.contains(t)) {
						appear = true;
					}
				} else if (!(l.getPredicate() instanceof SetPredicate) && terms.contains(t)) {
					appear = true;
				}
			}
			if (!appear) 
				throw new IllegalArgumentException(
					"Set variable in the rule must occurs in a body atom that uses some non-special predicate. Rule was: " + head + " :- " + body);
		}
		
		if (head.getSetTerms().count() + body.getSetTerms().count() > 0 && head.getExistentialVariables().count() > 0) 
			throw new IllegalArgumentException(
				"Existential rules cannot contain set terms. Rule was: " + head + " :- " + body);
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = this.body.hashCode();
		result = prime * result + this.head.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Rule)) {
			return false;
		}
		final Rule other = (Rule) obj;

		return this.head.equals(other.getHead()) && this.body.equals(other.getBody());
	}

	@Override
	public String toString() {
		return Serializer.getSerialization(serializer -> serializer.writeRule(this));
	}

	@Override
	public Conjunction<PositiveLiteral> getHead() {
		return this.head;
	}

	@Override
	public Conjunction<Literal> getBody() {
		return this.body;
	}

	@Override
	public <T> T accept(StatementVisitor<T> statementVisitor) {
		return statementVisitor.visit(this);
	}

	@Override
	public Stream<Term> getTerms() {
		return Stream.concat(this.body.getTerms(), this.head.getTerms()).distinct();
	}

}
