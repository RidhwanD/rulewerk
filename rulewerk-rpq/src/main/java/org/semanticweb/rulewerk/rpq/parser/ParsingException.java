package org.semanticweb.rulewerk.rpq.parser;

import org.semanticweb.rulewerk.core.exceptions.RulewerkException;

public class ParsingException extends RulewerkException {
	private static final long serialVersionUID = 2849123381757026724L;

	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(Throwable cause) {
		super(cause);
	}

	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
