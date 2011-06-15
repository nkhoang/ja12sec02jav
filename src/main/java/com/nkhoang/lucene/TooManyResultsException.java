package com.nkhoang.lucene;

public class TooManyResultsException extends Exception {

	private static final long serialVersionUID = 20110614L;

	private long _numberOfResults;

	public TooManyResultsException(long numberOfResults) {
		super();
		_numberOfResults = numberOfResults;
	}

	public long getNumberOfResults() {
		return _numberOfResults;
	}
}
