package com.nkhoang.common.xml;

/**
 * Exception class to wrap different XML parsing exceptions and make them into
 * runtime exceptions
 */
public class XMLParsingException extends RuntimeException {

	private static final long serialVersionUID = 6508898331282558212L;

	public XMLParsingException(Throwable cause) {
		super(cause);
	}

	public XMLParsingException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
