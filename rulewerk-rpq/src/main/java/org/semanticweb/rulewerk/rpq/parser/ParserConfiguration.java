package org.semanticweb.rulewerk.rpq.parser;

/*-
 * #%L
 * Rulewerk Parser
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.semanticweb.rulewerk.core.model.api.Constant;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.TermFactory;
import org.semanticweb.rulewerk.rpq.parser.javacc.SubParserFactory;
import org.semanticweb.rulewerk.rpq.parser.javacc.JavaCCRPQParserBase.ConfigurableLiteralDelimiter;

/**
 * Class to keep parser configuration.
 *
 * @author Maximilian Marx
 */
public class ParserConfiguration {
	/**
	 * Reserved directive names that are not allowed to be registered.
	 */
	public static final List<String> RESERVED_DIRECTIVE_NAMES = Arrays.asList("base", "prefix", "source");

	/**
	 * Whether parsing Named Nulls is allowed.
	 */
	private boolean allowNamedNulls = true;

	/**
	 * The registered datatypes.
	 */
	private HashMap<String, DatatypeConstantHandler> datatypes = new HashMap<>();

	/**
	 * The registered configurable literals.
	 */
	private HashMap<ConfigurableLiteralDelimiter, ConfigurableLiteralHandler> literals = new HashMap<>();

	/**
	 * The current base path to resolve imports against. Defaults to the current
	 * working directory.
	 */
	private String importBasePath = System.getProperty("user.dir");

	public ParserConfiguration() {
	}

	/**
	 * Copy constructor.
	 *
	 * @param other {@link ParserConfiguration} to copy
	 */
	public ParserConfiguration(ParserConfiguration other) {
		this.allowNamedNulls = other.allowNamedNulls;
		this.literals = new HashMap<>(other.literals);
		this.importBasePath = new String(other.importBasePath);
	}

	/**
	 * Parse a constant with optional data type.
	 *
	 * @param lexicalForm the (unescaped) lexical form of the constant.
	 * @param datatype    the datatype, or null if not present.
	 * @param termFactory the {@link TermFactory} to use for creating the result
	 *
	 * @throws ParsingException when the lexical form is invalid for the given data
	 *                          type.
	 * @return the {@link Constant} corresponding to the given arguments.
	 */
	public Constant parseDatatypeConstant(final String lexicalForm, final String datatype,
			final TermFactory termFactory) throws ParsingException {
		final String type = ((datatype != null) ? datatype : PrefixDeclarationRegistry.XSD_STRING);
		final DatatypeConstantHandler handler = this.datatypes.get(type);

		if (handler != null) {
			return handler.createConstant(lexicalForm);
		}

		return termFactory.makeDatatypeConstant(lexicalForm, type);
	}

	/**
	 * Check if a handler for this
	 * {@link org.semanticweb.rulewerk.parser.javacc.JavaCCParserBase.ConfigurableLiteralDelimiter}
	 * is registered
	 *
	 * @param delimiter delimiter to check.
	 * @return true if a handler for the given delimiter is registered.
	 */
	public boolean isConfigurableLiteralRegistered(ConfigurableLiteralDelimiter delimiter) {
		return literals.containsKey(delimiter);
	}

	/**
	 * Parse a configurable literal.
	 *
	 * @param delimiter        delimiter given for the syntactic form.
	 * @param syntacticForm    syntantic form of the literal to parse.
	 * @param subParserFactory a {@link SubParserFactory} instance that creates
	 *                         parser with the same context as the current parser.
	 *
	 * @throws ParsingException when no handler for the literal is registered, or
	 *                          the given syntactic form is invalid.
	 * @return an appropriate {@link Constant} instance.
	 */
	public Term parseConfigurableLiteral(ConfigurableLiteralDelimiter delimiter, String syntacticForm,
			final SubParserFactory subParserFactory) throws ParsingException {
		if (!isConfigurableLiteralRegistered(delimiter)) {
			throw new ParsingException(
					"No handler for configurable literal delimiter \"" + delimiter + "\" registered.");
		}

		ConfigurableLiteralHandler handler = literals.get(delimiter);
		return handler.parseLiteral(syntacticForm, subParserFactory);
	}

	/**
	 * Register a new data type.
	 *
	 * @param name    the IRI representing the data type.
	 * @param handler a {@link DatatypeConstantHandler} that parses a syntactic form
	 *                into a {@link Constant}.
	 *
	 * @throws IllegalArgumentException when the data type name has already been
	 *                                  registered.
	 *
	 * @return this
	 */
	public ParserConfiguration registerDatatype(final String name, final DatatypeConstantHandler handler)
			throws IllegalArgumentException {
		Validate.isTrue(!this.datatypes.containsKey(name), "The Data type \"%s\" is already registered.", name);

		this.datatypes.put(name, handler);
		return this;
	}

	/**
	 * Register a custom literal handler.
	 *
	 * @param delimiter the delimiter to handle.
	 * @param handler   the handler for this literal type.
	 *
	 * @throws IllegalArgumentException when the literal delimiter has already been
	 *                                  registered.
	 *
	 * @return this
	 */
	public ParserConfiguration registerLiteral(ConfigurableLiteralDelimiter delimiter,
			ConfigurableLiteralHandler handler) throws IllegalArgumentException {
		Validate.isTrue(!this.literals.containsKey(delimiter), "Literal delimiter \"%s\" is already registered.",
				delimiter);

		this.literals.put(delimiter, handler);
		return this;
	}

	/**
	 * Set whether to allow parsing of
	 * {@link org.semanticweb.rulewerk.core.model.api.NamedNull}.
	 *
	 * @param allow true allows parsing of named nulls.
	 *
	 * @return this
	 */
	public ParserConfiguration setNamedNulls(boolean allow) {
		this.allowNamedNulls = allow;
		return this;
	}

	/**
	 * Allow parsing of {@link org.semanticweb.rulewerk.core.model.api.NamedNull}.
	 *
	 * @return this
	 */
	public ParserConfiguration allowNamedNulls() {
		return this.setNamedNulls(true);
	}

	/**
	 * Disallow parsing of
	 * {@link org.semanticweb.rulewerk.core.model.api.NamedNull}.
	 *
	 * @return this
	 */
	public ParserConfiguration disallowNamedNulls() {
		return this.setNamedNulls(false);
	}

	/**
	 * Whether parsing of {@link org.semanticweb.rulewerk.core.model.api.NamedNull}
	 * is allowed.
	 *
	 * @return true iff parsing of NamedNulls is allowed.
	 */
	public boolean isParsingOfNamedNullsAllowed() {
		return this.allowNamedNulls;
	}

	/**
	 * Get the base path for file imports.
	 *
	 * @return the path that relative imports will be resolved against.
	 */
	public String getImportBasePath() {
		return this.importBasePath;
	}

	/**
	 * Set a new base path for file imports.
	 *
	 * @param importBasePath path that relative imports will be
	 * resolved against. If null, default to current working
	 * directory.
	 */
	public ParserConfiguration setImportBasePath(String importBasePath) {
		if (importBasePath != null) {
			this.importBasePath = importBasePath;
		} else {
			this.importBasePath = System.getProperty("user.dir");
		}

		return this;
	}

}
