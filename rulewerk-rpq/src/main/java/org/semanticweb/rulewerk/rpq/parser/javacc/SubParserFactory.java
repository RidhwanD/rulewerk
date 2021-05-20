package org.semanticweb.rulewerk.rpq.parser.javacc;

import java.io.ByteArrayInputStream;

import java.io.InputStream;

import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.rpq.parser.ParserConfiguration;
import org.semanticweb.rulewerk.rpq.parser.RPQParser;

/**
 * Factory for creating a SubParser sharing configuration, (semantic) state, and
 * prefixes, but with an independent input stream, to be used, e.g., for parsing
 * arguments in data source declarations. The parser will start in the
 * {@code DEFAULT} lexical state.
 *
 * @author Maximilian Marx
 */
public class SubParserFactory {
	private final ParserConfiguration parserConfiguration;
	private final PrefixDeclarationRegistry prefixDeclarationRegistry;

	/**
	 * Construct a SubParserFactory.
	 *
	 * @param parser the parser instance to get the (semantic) state from.
	 */
	SubParserFactory(final JavaCCRPQParser parser) {
		this.prefixDeclarationRegistry = parser.getPrefixDeclarationRegistry();
		this.parserConfiguration = parser.getParserConfiguration();
	}

	/**
	 * Create a new parser with the specified (semantic) state and given input.
	 *
	 * @param inputStream the input stream to parse.
	 * @param encoding    encoding of the input stream.
	 *
	 * @return A new {@link JavaCCParser} bound to inputStream and with the
	 *         specified parser state.
	 */
	public JavaCCRPQParser makeSubParser(final InputStream inputStream, final String encoding) {
		final JavaCCRPQParser subParser = new JavaCCRPQParser(inputStream, encoding);
		subParser.setPrefixDeclarationRegistry(this.prefixDeclarationRegistry);
		subParser.setParserConfiguration(this.parserConfiguration);

		return subParser;
	}

	public JavaCCRPQParser makeSubParser(final InputStream inputStream) {
		return this.makeSubParser(inputStream, RPQParser.DEFAULT_STRING_ENCODING);
	}

	public JavaCCRPQParser makeSubParser(final String string) {
		return this.makeSubParser(new ByteArrayInputStream(string.getBytes()));
	}
}
