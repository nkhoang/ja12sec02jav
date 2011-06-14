package com.nkhoang.common.abtab.parser;

import java.nio.charset.Charset;

/** Interface for parsers which handle text based data. */
public interface TextParser {

	public void setCharset(Charset charset);

	public Charset getCharset();

}
