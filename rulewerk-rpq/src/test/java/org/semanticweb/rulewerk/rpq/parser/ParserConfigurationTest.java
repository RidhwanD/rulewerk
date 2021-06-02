package org.semanticweb.rulewerk.rpq.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.rpq.parser.javacc.SubParserFactory;
import org.semanticweb.rulewerk.rpq.parser.javacc.JavaCCRPQParserBase.ConfigurableLiteralDelimiter;

public class ParserConfigurationTest {
	private static final String TYPE_NAME = "test-type";

	private ParserConfiguration parserConfiguration;

	@Mock
	private DatatypeConstantHandler datatypeConstantHandler;
	@Mock
	private SubParserFactory subParserFactory;
	@Mock
	private DirectiveHandler<KnowledgeBase> directiveHandler;

	@Before
	public void init() {
		parserConfiguration = new ParserConfiguration();
	}

	@Test(expected = IllegalArgumentException.class)
	public void registerDatatype_duplicateName_throws() {
		parserConfiguration.registerDatatype(TYPE_NAME, datatypeConstantHandler).registerDatatype(TYPE_NAME,
				datatypeConstantHandler);
	}

	@Test
	public void isParsingOfNamedNullsAllowed_default_returnsTrue() {
		assertTrue("named nulls are allowed by default", parserConfiguration.isParsingOfNamedNullsAllowed());
	}

	@Test
	public void isParsingOfNamedNullsAllowed_disabled_returnsFalse() {
		parserConfiguration.disallowNamedNulls();
		assertFalse("named nulls are disallowed after disallowing them",
				parserConfiguration.isParsingOfNamedNullsAllowed());
	}

	@Test
	public void isParsingOfNamedNullsAllowed_disabledAndEnabled_returnsTrue() {
		parserConfiguration.disallowNamedNulls();
		assertFalse("named nulls are disallowed after disallowing them",
				parserConfiguration.isParsingOfNamedNullsAllowed());
		parserConfiguration.allowNamedNulls();
		assertTrue("named nulls are allowed after allowing them", parserConfiguration.isParsingOfNamedNullsAllowed());
	}

	@Test(expected = ParsingException.class)
	public void parseConfigurableLiteral_unregisteredLiteral_throws() throws ParsingException {
		parserConfiguration.parseConfigurableLiteral(ConfigurableLiteralDelimiter.BRACE, "test", subParserFactory);
	}
	
}
