package org.semanticweb.rulewerk.core.model.api;

import java.util.ArrayList;
import java.util.List;
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

/**
 * General interface for all classes that represent syntactic objects that might
 * contain atomic terms, in particular all kinds of logical formulas. Compound
 * terms (with nested functions) would also be of this type if we had them.
 * 
 * @author Markus Kroetzsch
 *
 */
public interface SyntaxObject extends Entity {

	/**
	 * Returns the stream of distinct terms that occur in this object.
	 * 
	 * @return stream of distinct terms used in this object
	 */
	Stream<Term> getTerms();

	/**
	 * Return the stream of distinct universal variables in this object.
	 * 
	 * @return stream of universal variables
	 */
	default Stream<UniversalVariable> getUniversalVariables() {
		List<UniversalVariable> univVarsSets = new ArrayList<UniversalVariable>();
		for (SetConstruct sc : Terms.getSetConstructs(getTerms()).collect(Collectors.toList())) {
			if (sc.getElement() != null && sc.getElement().isVariable())
				univVarsSets.add((UniversalVariable) sc.getElement());
		}
		return Stream.concat(Terms.getUniversalVariables(getTerms()), univVarsSets.stream());
	}

	/**
	 * Return the stream of distinct existential variables in this object.
	 * 
	 * @return stream of existential variables
	 */
	default Stream<ExistentialVariable> getExistentialVariables() {
		return Terms.getExistentialVariables(getTerms());
	}

	/**
	 * Return the stream of distinct variables in this object.
	 * 
	 * @return stream of variables
	 */
	default Stream<Variable> getVariables() {
		return Terms.getVariables(getTerms());
	}

	/**
	 * Return the stream of distinct constants in this object.
	 * 
	 * @return stream of constants
	 */
	default Stream<Constant> getConstants() {
		return Terms.getConstants(getTerms());
	}

	/**
	 * Return the stream of distinct abstract constants in this object.
	 * 
	 * @return stream of abstract constants
	 */
	default Stream<AbstractConstant> getAbstractConstants() {
		return Terms.getAbstractConstants(getTerms());
	}

	/**
	 * Return the stream of distinct datatype constants in this object.
	 * 
	 * @return stream of datatype constants
	 */
	default Stream<DatatypeConstant> getDatatypeConstants() {
		return Terms.getDatatypeConstants(getTerms());
	}

	/**
	 * Return the stream of distinct named nulls in this object.
	 * 
	 * @return stream of named nulls
	 */
	default Stream<NamedNull> getNamedNulls() {
		return Terms.getNamedNulls(getTerms());
	}
	
	/**
	 * Return the stream of distinct named nulls in this object.
	 * 
	 * @return stream of named nulls
	 */
	default Stream<SetTerm> getSetTerms() {
		return Terms.getSetTerms(getTerms());
	}
	
	/**
	 * Return the stream of distinct named nulls in this object.
	 * 
	 * @return stream of named nulls
	 */
	default Stream<SetUnion> getSetUnions() {
		return Terms.getSetUnions(getTerms());
	}

	/**
	 * Return the stream of distinct set variables in this object.
	 * 
	 * @return stream of set variables
	 */
	default Stream<SetVariable> getSetVariables() {
		List<SetVariable> setVarsUnions = new ArrayList<SetVariable>();
		for (SetUnion su : Terms.getSetUnions(getTerms()).collect(Collectors.toList())) {
			setVarsUnions.addAll(su.getSetVariables());
		}
		return Stream.concat(Terms.getSetVariables(getTerms()), setVarsUnions.stream());
	}

}
