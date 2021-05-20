package org.semanticweb.rulewerk.rpq.converter;

import org.semanticweb.rulewerk.core.exceptions.RulewerkRuntimeException;

public class RpqConvertException extends RulewerkRuntimeException {

	/**
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -3228005099627492816L;

	public RpqConvertException(final String message) {
		super(message);
	}

	public RpqConvertException(final String message, final Throwable exception) {
		super(message, exception);
	}
}
