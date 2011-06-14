package com.nkhoang.common.util.parser;

public class LineParseException extends Exception {

	private static final long serialVersionUID = 1L;

	private int _lineNumber = -1;

	public LineParseException(String message) {
		super(message);
	}

	public LineParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public LineParseException(Throwable cause) {
		super(cause);
	}

	public void setLineNumber(int number) {
		_lineNumber = number;
	}

	public int getLineNumber() {
		return _lineNumber;
	}

	@Override
	public String getMessage() {
		String rtn = super.getMessage();
		if (_lineNumber > -1) {
			rtn = "Line " + _lineNumber + ": " + rtn;
		}
		return rtn;
	}

}
