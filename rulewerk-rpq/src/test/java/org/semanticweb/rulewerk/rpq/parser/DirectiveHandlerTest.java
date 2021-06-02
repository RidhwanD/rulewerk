package org.semanticweb.rulewerk.rpq.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.semanticweb.rulewerk.core.model.api.Argument;
import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;

public class DirectiveHandlerTest {
	
	private static final String BASE_PATH = System.getProperty("user.dir");
	private static final String STRING = "src/test/resources/query.rls";
	private static final Term STRINGTERM = Expressions.makeDatatypeConstant(STRING,
			PrefixDeclarationRegistry.XSD_STRING);
	private static final Term INTTERM = Expressions.makeDatatypeConstant("42", PrefixDeclarationRegistry.XSD_INT);

	private static final Argument TERM_STRING_ARGUMENT = Argument.term(STRINGTERM);
	private static final Argument TERM_INT_ARGUMENT = Argument.term(INTTERM);

	@Test
	public void validateStringArgument_stringArgument_succeeds() throws ParsingException {
		assertEquals(STRING, DirectiveHandler.validateStringArgument(TERM_STRING_ARGUMENT, "string argument"));
	}

	@Test(expected = ParsingException.class)
	public void validateStringArgument_stringArgument_throws() throws ParsingException {
		assertEquals(STRING, DirectiveHandler.validateStringArgument(TERM_INT_ARGUMENT, "string argument"));
	}

	@Test
	public void validateTermArgument_termArgument_succeeds() throws ParsingException {
		assertEquals(STRINGTERM, DirectiveHandler.validateTermArgument(TERM_STRING_ARGUMENT, "term argument"));
	}

	@Test
	public void validateFilenameArgument_filename_succeeds() throws ParsingException {
		assertEquals(new File(BASE_PATH + File.separator + STRING),
				DirectiveHandler.validateFilenameArgument(TERM_STRING_ARGUMENT, "filename argument", BASE_PATH));
	}

	@Test
	public void validateFilenameArgument_invalidFilename_throws() throws ParsingException {
		DirectiveHandler.validateFilenameArgument(Argument
				.term(Expressions.makeDatatypeConstant(STRING + "-nonexistent", PrefixDeclarationRegistry.XSD_STRING)),
				"filename argument", BASE_PATH);
	}
	
}
