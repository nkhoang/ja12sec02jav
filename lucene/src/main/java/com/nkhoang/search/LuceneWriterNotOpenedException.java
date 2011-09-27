package com.nkhoang.search;


public class LuceneWriterNotOpenedException extends Exception {

	private static final long serialVersionUID = 20091130L;

	public LuceneWriterNotOpenedException() {
		super();
	}

	public LuceneWriterNotOpenedException(String msg) {
		super(msg);
	}

	public LuceneWriterNotOpenedException(Exception t) {
		super(t);
	}

	public LuceneWriterNotOpenedException(String msg, Exception t) {
		super(msg, t);
	}

}
