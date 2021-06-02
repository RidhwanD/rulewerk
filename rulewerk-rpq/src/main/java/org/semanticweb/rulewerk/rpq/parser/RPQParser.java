package org.semanticweb.rulewerk.rpq.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.core.model.api.Entity;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.rpq.model.implementation.RPQExpressions;
import org.semanticweb.rulewerk.rpq.parser.javacc.JavaCCRPQParser;
import org.semanticweb.rulewerk.rpq.parser.javacc.ParseException;
import org.semanticweb.rulewerk.rpq.parser.javacc.TokenMgrError;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegExpression;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPQParser {
	public static final String DEFAULT_STRING_ENCODING = "UTF-8";

	private static Logger LOGGER = LoggerFactory.getLogger(RPQParser.class);

	private RPQParser() {
	}
	
	/**
	 * Interface for a method parsing a fragment of the supported syntax.
	 *
	 * This is needed to specify the exceptions thrown by the parse method.
	 */
	@FunctionalInterface
	interface SyntaxFragmentParser<T extends Entity> {
		T parse(final JavaCCRPQParser parser)
				throws ParsingException, ParseException, PrefixDeclarationException, TokenMgrError;
	}

	/**
	 * Parse a syntax fragment.
	 *
	 * @param input               Input string.
	 * @param parserAction        Parsing method for the {@code T}.
	 * @param syntaxFragmentType  Description of the type {@code T} being parsed.
	 * @param parserConfiguration {@link ParserConfiguration} instance, or null.
	 *
	 * @throws ParsingException when an error during parsing occurs.
	 * @return an appropriate instance of {@code T}
	 */
	static <T extends Entity> T parseSyntaxFragment(final String input, SyntaxFragmentParser<T> parserAction,
			final String syntaxFragmentType, final ParserConfiguration parserConfiguration) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		final JavaCCRPQParser localParser = new JavaCCRPQParser(inputStream, "UTF-8");

		if (parserConfiguration != null) {
			localParser.setParserConfiguration(parserConfiguration);
		}
		
		T result;
		try {
			result = parserAction.parse(localParser);
			localParser.ensureEndOfInput();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError | RuntimeException e) {
			LOGGER.error("Error parsing " + syntaxFragmentType + ": {}!", input);
			throw new ParsingException("Error parsing " + syntaxFragmentType + ": " + e.getMessage(), e);
		}
		return result;
	}
	
	public static RPQConjunction<RegPathQuery> parse(final InputStream stream, final String encoding) throws ParsingException {
		return doParse(new JavaCCRPQParser(stream, encoding));
	}

	public static RPQConjunction<RegPathQuery> parse(final InputStream stream) throws ParsingException {
		return parse(stream, DEFAULT_STRING_ENCODING);
	}

	public static RPQConjunction<RegPathQuery> parse(final String input) throws ParsingException {
		final InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		return parse(inputStream);
	}
	
	static RPQConjunction<RegPathQuery> doParse(final JavaCCRPQParser parser) throws ParsingException {
		try {
			parser.parse();
		} catch (ParseException | PrefixDeclarationException | TokenMgrError e) {
			LOGGER.error("Error parsing Knowledge Base: " + e.getMessage(), e);
			throw new ParsingException(e.getMessage(), e);
		}

		RPQConjunction<RegPathQuery> rpqs = RPQExpressions.makeRPQConjunction(parser.getRPQs(), parser.getProjVars());

		return rpqs;
	}
	
	public static RPQConjunction<RegPathQuery> parseQuery(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parseSyntaxFragment(input, JavaCCRPQParser::query, "RPQ", parserConfiguration);
	}

	public static RPQConjunction<RegPathQuery> parseQuery(final String input) throws ParsingException {
		return parseQuery(input, null);
	}

	public static RegPathQuery parseRegPathQuery(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parseSyntaxFragment(input, JavaCCRPQParser::regPathQuery, "RPQ", parserConfiguration);
	}

	public static RegPathQuery parseRegPathQuery(final String input) throws ParsingException {
		return parseRegPathQuery(input, null);
	}

	public static RegExpression parseRegExpression(final String input, final ParserConfiguration parserConfiguration)
			throws ParsingException {
		return parseSyntaxFragment(input, JavaCCRPQParser::regExpression, "RPQ", parserConfiguration);
	}

	public static RegExpression parseRegExpression(final String input) throws ParsingException {
		return parseRegExpression(input, null);
	}
	
	public static Term parseTerm(final String input, final ParserConfiguration parserConfiguration) throws ParsingException {
		return parseSyntaxFragment(input, parser -> parser.term(), "term", parserConfiguration);
	}

	public static Term parseTerm(final String input) throws ParsingException {
		return parseTerm(input, (ParserConfiguration) null);
	}
}
