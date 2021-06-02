package org.semanticweb.rulewerk.rpq.parser.javacc;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.semanticweb.rulewerk.core.exceptions.PrefixDeclarationException;
import org.semanticweb.rulewerk.rpq.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.rpq.parser.ParserConfiguration;
import org.semanticweb.rulewerk.rpq.parser.ParsingException;
import org.semanticweb.rulewerk.rpq.parser.DatatypeConstantHandler;

public class JavaCCRPQParserBaseTest {
	private JavaCCRPQParserBase parserBase;
	private static final String DATATYPE_NAME = "https://example.org/test-type";

	private DatatypeConstantHandler datatypeConstantHandler = mock(DatatypeConstantHandler.class);

	@Before
	public void init() {
		parserBase = new JavaCCRPQParserBase();
	}
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Test
	public void createConstant_undeclaredPrefix_throws() throws ParseException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Failed to parse IRI");
		parserBase.createConstant("ïnvälid://test");
	}

	@Test
	public void createConstant_throwingDatatypeConstantHandler_throws() throws ParseException, ParsingException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Failed to parse Constant");

		when(datatypeConstantHandler.createConstant(anyString())).thenThrow(ParsingException.class);
		ParserConfiguration parserConfiguration = new DefaultParserConfiguration().registerDatatype(DATATYPE_NAME,
				datatypeConstantHandler);
		parserBase.setParserConfiguration(parserConfiguration);
		parserBase.createConstant("test", DATATYPE_NAME);
	}

	@Test
	public void unescapeStr_escapeChars_succeeds() throws ParseException {
		String input = "\\\\test\r\ntest: \\n\\t\\r\\b\\f\\'\\\"\\\\";
		String expected = "\\test\r\ntest: \n\t\r\b\f\'\"\\";
		String result = JavaCCRPQParserBase.unescapeStr(input, 0, 0);
		assertEquals(result, expected);
	}

	@Test
	public void unescapeStr_illegalEscapeAtEndOfString_throws() throws ParseException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Illegal escape at end of string");

		JavaCCRPQParserBase.unescapeStr("\\", 0, 0);
	}

	@Test
	public void unescapeStr_unknownEscapeSequence_throws() throws ParseException {
		exceptionRule.expect(ParseException.class);
		exceptionRule.expectMessage("Unknown escape");

		JavaCCRPQParserBase.unescapeStr("\\y", 0, 0);
	}

	@Test
	public void setBase_changingBase_throws() throws PrefixDeclarationException {
		exceptionRule.expect(PrefixDeclarationException.class);
		exceptionRule.expectMessage("Base is already defined as");

		parserBase.setBase("https://example.org/");
		parserBase.setBase("https://example.com/");
	}
}
