package org.semanticweb.vlog4j.parser.javacc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*-
 * #%L
 * vlog4j-parser
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.semanticweb.vlog4j.core.exceptions.PrefixDeclarationException;
import org.semanticweb.vlog4j.core.model.api.AbstractConstant;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.DataSource;
import org.semanticweb.vlog4j.core.model.api.NamedNull;
import org.semanticweb.vlog4j.core.model.api.Predicate;
import org.semanticweb.vlog4j.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.vlog4j.core.model.api.Statement;
import org.semanticweb.vlog4j.core.model.api.Term;
import org.semanticweb.vlog4j.core.model.implementation.DataSourceDeclarationImpl;
import org.semanticweb.vlog4j.core.model.implementation.Expressions;
import org.semanticweb.vlog4j.core.model.implementation.RenamedNamedNull;
import org.semanticweb.vlog4j.core.reasoner.KnowledgeBase;
import org.semanticweb.vlog4j.parser.DefaultParserConfiguration;
import org.semanticweb.vlog4j.parser.DirectiveArgument;
import org.semanticweb.vlog4j.parser.LocalPrefixDeclarationRegistry;
import org.semanticweb.vlog4j.parser.ParserConfiguration;
import org.semanticweb.vlog4j.parser.ParsingException;

/**
 * Basic methods used in the JavaCC-generated parser.
 *
 * Implementation of some string escaping methods adapted from Apache Jena,
 * released under Apache 2.0 license terms.
 *
 * @see <a href=
 *      "https://github.com/apache/jena/blob/master/jena-core/src/main/java/org/apache/jena/n3/turtle/ParserBase.java">https://github.com/apache/jena/blob/master/jena-core/src/main/java/org/apache/jena/n3/turtle/ParserBase.java</a>
 *
 * @author Markus Kroetzsch
 * @author Larry Gonzalez
 * @author Maximilian Marx
 * @author Jena developers, Apache Software Foundation (ASF)
 *
 */
public class JavaCCParserBase {
	private PrefixDeclarationRegistry prefixDeclarationRegistry;

	private KnowledgeBase knowledgeBase;
	private ParserConfiguration parserConfiguration;
	private byte[] namedNullNamespace = UUID.randomUUID().toString().getBytes();

	/**
	 * "Local" variable to remember (universal) body variables during parsing.
	 */
	protected final HashSet<String> bodyVars = new HashSet<String>();
	/**
	 * "Local" variable to remember existential head variables during parsing.
	 */
	protected final HashSet<String> headExiVars = new HashSet<String>();
	/**
	 * "Local" variable to remember universal head variables during parsing.
	 */
	protected final HashSet<String> headUniVars = new HashSet<String>();

	/**
	 * Defines the context for parsing sub-formulas.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	public enum FormulaContext {
		/**
		 * Formula is to be interpreted in the context of a rule head (positive
		 * occurrence).
		 */
		HEAD,
		/**
		 * Formula is to be interpreted in the context of a rule body (negative
		 * occurrence).
		 */
		BODY
	}

	/**
	 * Defines delimiters for configurable literals.
	 *
	 * Since the parser is generated from a fixed grammar, we need to provide
	 * productions for these literals, even if they are not part of the syntax. With
	 * the {@link DefaultParserConfiguration}, any occurence of these literals will
	 * result in a {@link ParseException}.
	 *
	 * @author Maximilian Marx
	 */
	public enum ConfigurableLiteralDelimiter {
		/**
		 * Literals of the form {@code |…|}
		 */
		PIPE,
		/**
		 * Literals of the form {@code #…#}
		 */
		HASH,
		/**
		 * Literals of the form {@code (…)}
		 */
		PAREN,
		/**
		 * Literals of the form {@code {…}}
		 */
		BRACE,
		/**
		 * Literals of the form {@code […]}
		 */
		BRACKET,
	}

	public JavaCCParserBase() {
		this.knowledgeBase = new KnowledgeBase();
		this.prefixDeclarationRegistry = new LocalPrefixDeclarationRegistry();
		this.parserConfiguration = new DefaultParserConfiguration();
	}

	AbstractConstant createConstant(String lexicalForm) throws ParseException {
		String absoluteIri;
		try {
			absoluteIri = absolutizeIri(lexicalForm);
		} catch (PrefixDeclarationException e) {
			throw makeParseExceptionWithCause("Failed to parse IRI", e);
		}
		return Expressions.makeAbstractConstant(absoluteIri);
	}

	/**
	 * Creates a suitable {@link Constant} from the parsed data.
	 *
	 * @param string   the string data (unescaped)
	 * @param datatype the datatype, or null if not provided
	 * @return suitable constant
	 */
	Constant createConstant(String lexicalForm, String datatype) throws ParseException {
		try {
			return parserConfiguration.parseDatatypeConstant(lexicalForm, datatype);
		} catch (ParsingException e) {
			throw makeParseExceptionWithCause("Failed to parse Constant", e);
		}
	}

	NamedNull createNamedNull(String lexicalForm) throws ParseException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			stream.write(namedNullNamespace);
			stream.write(lexicalForm.getBytes());
		} catch (IOException e) {
			throw makeParseExceptionWithCause("Failed to generate a unique name for named null", e);
		}

		return new RenamedNamedNull(UUID.nameUUIDFromBytes(stream.toByteArray()));
	}

	void addStatement(Statement statement) {
		knowledgeBase.addStatement(statement);
	}

	void addDataSource(String predicateName, int arity, DataSource dataSource) throws ParseException {
		if (dataSource.getRequiredArity().isPresent()) {
			Integer requiredArity = dataSource.getRequiredArity().get();
			if (arity != requiredArity) {
				throw new ParseException(
						"Invalid arity " + arity + " for data source, " + "expected " + requiredArity + ".");
			}
		}

		Predicate predicate = Expressions.makePredicate(predicateName, arity);
		addStatement(new DataSourceDeclarationImpl(predicate, dataSource));
	}

	static String unescapeStr(String s, int line, int column) throws ParseException {
		return unescape(s, '\\', line, column);
	}

	static String unescape(String s, char escape, int line, int column) throws ParseException {
		int i = s.indexOf(escape);

		if (i == -1) {
			return s;
		}

		// Dump the initial part straight into the string buffer
		StringBuilder sb = new StringBuilder(s.substring(0, i));

		for (; i < s.length(); i++) {
			char ch = s.charAt(i);
			// Keep line and column numbers.
			switch (ch) {
			case '\n':
			case '\r':
				line++;
				column = 1;
				break;

			default:
				column++;
				break;
			}

			if (ch != escape) {
				sb.append(ch);
				continue;
			}

			// Escape
			if (i >= s.length() - 1) {
				throw new ParseException("Illegal escape at end of string, line: " + line + ", column: " + column);
			}
			char ch2 = s.charAt(i + 1);
			column = column + 1;
			i = i + 1;

			// Not just codepoints. Must be a legal escape.
			char ch3 = 0;
			switch (ch2) {
			case 'n':
				ch3 = '\n';
				break;
			case 't':
				ch3 = '\t';
				break;
			case 'r':
				ch3 = '\r';
				break;
			case 'b':
				ch3 = '\b';
				break;
			case 'f':
				ch3 = '\f';
				break;
			case '\'':
				ch3 = '\'';
				break;
			case '\"':
				ch3 = '\"';
				break;
			case '\\':
				ch3 = '\\';
				break;
			default:
				throw new ParseException("Unknown escape: \\" + ch2 + ", line: " + line + ", column: " + column);
			}
			sb.append(ch3);
		}
		return sb.toString();
	}

	/**
	 * Remove the first and last {@code n} characters from string {@code s}
	 *
	 * @param s string to strip delimiters from
	 * @param n number of characters to strip from both ends
	 *
	 * @return the stripped string.
	 */
	static String stripDelimiters(String s, int n) {
		return s.substring(n, s.length() - n);
	}

	/** remove the first n charcacters from the string */
	static String stripChars(String s, int n) {
		return s.substring(n, s.length());
	}

	/**
	 * Reset the local set variables used when parsing a rule.
	 */
	void resetVariableSets() {
		this.bodyVars.clear();
		this.headExiVars.clear();
		this.headUniVars.clear();
	}

	/**
	 * Convert a throwable into a ParseException.
	 *
	 * @param message The error message.
	 * @param cause   The {@link Throwable} that caused this exception.
	 *
	 * @return A {@link ParseException} with appropriate cause and message.
	 */
	protected ParseException makeParseExceptionWithCause(String message, Throwable cause) {
		ParseException parseException = new ParseException(message);
		parseException.initCause(cause);
		return parseException;
	}

	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	public void setParserConfiguration(ParserConfiguration parserConfiguration) {
		this.parserConfiguration = parserConfiguration;
	}

	public ParserConfiguration getParserConfiguration() {
		return parserConfiguration;
	}

	byte[] getNamedNullNamespace() {
		return namedNullNamespace;
	}

	void setNamedNullNamespace(byte[] namedNullNamespace) {
		this.namedNullNamespace = namedNullNamespace;
	}

	public void setPrefixDeclarationRegistry(PrefixDeclarationRegistry prefixDeclarationRegistry) {
		this.prefixDeclarationRegistry = prefixDeclarationRegistry;
	}

	public PrefixDeclarationRegistry getPrefixDeclarationRegistry() {
		return this.prefixDeclarationRegistry;
	}

	DataSource parseDataSourceSpecificPartOfDataSourceDeclaration(String syntacticForm,
			List<DirectiveArgument> arguments, SubParserFactory subParserFactory) throws ParseException {
		try {
			return parserConfiguration.parseDataSourceSpecificPartOfDataSourceDeclaration(syntacticForm, arguments,
					subParserFactory);
		} catch (ParsingException e) {
			throw makeParseExceptionWithCause(
					"Failed while trying to parse the source-specific part of a data source declaration", e);
		}
	}

	Term parseConfigurableLiteral(ConfigurableLiteralDelimiter delimiter, String syntacticForm,
			SubParserFactory subParserFactory) throws ParsingException {
		return parserConfiguration.parseConfigurableLiteral(delimiter, syntacticForm, subParserFactory);
	}

	KnowledgeBase parseDirectiveStatement(String name, List<DirectiveArgument> arguments,
			SubParserFactory subParserFactory) throws ParseException {
		try {
			return parserConfiguration.parseDirectiveStatement(name, arguments, subParserFactory);
		} catch (ParsingException e) {
			throw makeParseExceptionWithCause("Failed while trying to parse directive statement", e);
		}
	}

	boolean isConfigurableLiteralRegistered(ConfigurableLiteralDelimiter delimiter) {
		return parserConfiguration.isConfigurableLiteralRegistered(delimiter);
	}

	boolean isParsingOfNamedNullsAllowed() {
		return parserConfiguration.isParsingOfNamedNullsAllowed();
	}

	void setBase(String baseIri) throws PrefixDeclarationException {
		prefixDeclarationRegistry.setBaseIri(baseIri);
	}

	void setPrefix(String prefixName, String baseIri) throws PrefixDeclarationException {
		prefixDeclarationRegistry.setPrefixIri(prefixName, baseIri);
	}

	String absolutizeIri(String iri) throws PrefixDeclarationException {
		return prefixDeclarationRegistry.absolutizeIri(iri);
	}

	String resolvePrefixedName(String prefixedName) throws PrefixDeclarationException {
		return prefixDeclarationRegistry.resolvePrefixedName(prefixedName);
	}
}
