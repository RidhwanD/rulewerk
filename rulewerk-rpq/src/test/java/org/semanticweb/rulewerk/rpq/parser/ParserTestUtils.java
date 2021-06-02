package org.semanticweb.rulewerk.rpq.parser;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.NamedNullImpl;
import org.semanticweb.rulewerk.core.model.implementation.RenamedNamedNull;

public interface ParserTestUtils {
	public default void assertUuid(String uuidLike) {
		try {
			UUID.fromString(uuidLike);
		} catch (IllegalArgumentException e) {
			throw new AssertionError("expected a valid UUID, but got \"" + uuidLike + "\"", e);
		}
	}

	public default void assertArgumentIsNamedNull(Literal literal, int argument) {
		List<Term> arguments = literal.getArguments();
		assertTrue("argument is positive", argument >= 1);
		assertTrue("argument is a valid position", argument <= arguments.size());
		Term term = arguments.get(argument - 1);
		assertTrue("argument is a named null", term instanceof NamedNullImpl);

		if (term instanceof RenamedNamedNull) {
			assertUuid(term.getName());
		}
	}
}
