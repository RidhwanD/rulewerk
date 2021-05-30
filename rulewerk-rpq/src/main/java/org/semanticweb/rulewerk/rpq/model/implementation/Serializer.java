package org.semanticweb.rulewerk.rpq.model.implementation;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

import org.semanticweb.rulewerk.core.model.api.PrefixDeclarationRegistry;
import org.semanticweb.rulewerk.core.model.implementation.AbstractPrefixDeclarationRegistry;
import org.semanticweb.rulewerk.rpq.model.api.RPQConjunction;
import org.semanticweb.rulewerk.rpq.model.api.RegPathQuery;

public class Serializer {
	final Writer writer;
	final Function<String, String> iriTransformer;
	
	public static final String STATEMENT_END = " .";

	/**
	 * Default IRI serializer that can be used if no abbreviations (prefixes, base,
	 * etc.) are used.
	 */
	public static final Function<String, String> identityIriSerializer = new Function<String, String>() {
		@Override
		public String apply(String iri) {
			if (iri.contains(":") || !iri.matches(AbstractPrefixDeclarationRegistry.REGEXP_LOCNAME)) {
				return "<" + iri + ">";
			} else {
				return iri;
			}
		}
	};
	
	/**
	 * Interface for a method that writes something to a writer.
	 */
	@FunctionalInterface
	public interface SerializationWriter {
		void write(final Serializer serializer) throws IOException;
	}

	/**
	 * Construct a serializer that uses a specific function to serialize IRIs.
	 * 
	 * @param writer         the object used to write serializations
	 * @param iriTransformer a function used to abbreviate IRIs, e.g., if namespace
	 *                       prefixes were declared
	 */
	public Serializer(final Writer writer, final Function<String, String> iriTransformer) {
		this.writer = writer;
		this.iriTransformer = iriTransformer;
	}
	
	/**
	 * Construct a serializer that serializes IRIs without any form of
	 * transformation or abbreviation.
	 * 
	 * @param writer the object used to write serializations
	 */
	public Serializer(final Writer writer) {
		this(writer, identityIriSerializer);
	}

	/**
	 * Construct a serializer that uses the given {@link PrefixDeclarationRegistry}
	 * to abbreviate IRIs.
	 * 
	 * @param writer                    the object used to write serializations
	 * @param prefixDeclarationRegistry the object used to abbreviate IRIs
	 */
	public Serializer(final Writer writer, PrefixDeclarationRegistry prefixDeclarationRegistry) {
		this(writer, (string) -> {
			return prefixDeclarationRegistry.unresolveAbsoluteIri(string, true);
		});
	}
	
	/**
	 * Convenience method for obtaining serializations as Java strings.
	 * 
	 * @param writeAction a function that accepts a {@link Serializer} and produces
	 *                    a string
	 * @return serialization string
	 */
	public static String getSerialization(SerializationWriter writeAction) {
		final StringWriter stringWriter = new StringWriter();
		final Serializer serializer = new Serializer(stringWriter);
		try {
			writeAction.write(serializer);
		} catch (IOException e) {
			throw new RuntimeException("StringWriter should never throw an IOException.");
		}
		return stringWriter.toString();
	}
	
	/**
	 * Writes a serialization of the given {@link RPQConjunction} of {@link RegPathQuery}
	 * objects.
	 *
	 * @param literals a {@link RPQConjunction}
	 * @throws IOException
	 */
	public void writeRPQConjunction(final RPQConjunction<? extends RegPathQuery> rpqs) throws IOException {
		boolean first = true;
		for (final RegPathQuery rpq : rpqs.getRPQs()) {
			if (first) {
				first = false;
			} else {
				writer.write(" ");
			}
			writer.write(rpq.toString());
		}
	}
}
