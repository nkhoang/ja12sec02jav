package com.nkhoang.common.util.parser;

import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.nkhoang.common.util.StringUtil;

/** Parses a character-delimited readable byte channel, line by line. */
public class DelimitedLineParser extends LineParser {

	/**
	 * enum used when parsing tokens to indicate what separated this token from
	 * the following data
	 */
	private enum SeparatorType {
		DELIMITER, END_OF_LINE, END_OF_BUFFER;
	}

	private boolean _returnQuotes = false;
	private char _delimiter;
	private char _escapeQuote = '\\';
	private Character _quote;
	private boolean   _trimTokens;
	private boolean   _removeNonAscii;

	// per-parse state (setup in beginParse)
	private boolean _handleQuotes;
	private Pattern _escapeQuotePattern;
	private String  _escapeQuoteReplacement;
	private char    _workingQuoteChar;
	private boolean _escapeQuoteIsQuote;
	private boolean _escapeCrlf;
	private boolean _escapeDelimiter;

	/**
	 * Construct a new parser.
	 *
	 * @param input     Channel to parse.  To use with an InputStream, use
	 *                  <code>Channels.newChannel(stream)</code>.
	 * @param listener  Receives parse events
	 * @param delimiter Delimiter character
	 */
	public DelimitedLineParser(
		ReadableByteChannel input, ParseListener listener, char delimiter) {
		super(input, listener, true, true);
		_delimiter = delimiter;
	}

	public void setEscapeDelimiter(boolean escapeDelimiter) {
		_escapeDelimiter = escapeDelimiter;
	}

	public boolean getEscapeDelimiter() {
		return _escapeDelimiter;
	}

	public void setEscapeCrlf(boolean escapeCrlf) {
		_escapeCrlf = escapeCrlf;
	}

	public boolean getEscapeCrlf() {
		return _escapeCrlf;
	}

	public void setQuote(Character quote) {
		_quote = quote;
	}

	public void setEscapeQuote(char escapeQuote) {
		_escapeQuote = escapeQuote;
	}

	public void setReturnQuotes(boolean returnQuotes) {
		_returnQuotes = returnQuotes;
	}

	/** @param b Whether or not to trim white space off of tokens. */
	public void setTrimTokens(boolean b) {
		_trimTokens = b;
	}

	/** @param b Whether or not to remove non ascii chars in tokens. */
	public void setRemoveNonAscii(boolean b) {
		_removeNonAscii = b;
	}

	@Override
	protected void beginParse() {
		super.beginParse();

		_handleQuotes = (_quote != null);
		if (_handleQuotes) {
			_workingQuoteChar = _quote;
			_escapeQuoteIsQuote = (_workingQuoteChar == _escapeQuote);
			// setup escape quote replacement
			_escapeQuotePattern = Pattern.compile(
				Pattern.quote(new String(new char[]{_escapeQuote, (char) _quote})));
			_escapeQuoteReplacement = Matcher.quoteReplacement(_quote.toString());
		} else {
			_workingQuoteChar = 0;
			_escapeQuoteIsQuote = false;
		}
	}

	//Javadoc copied from LineParser
	@Override
	protected void parseLine(
		CharBuffer buffer, int length, ParseListener listener) throws LineParseException {
		if (length > 0) {
			int maxPos = buffer.position() + length;
			Status status = new Status();
			int numEmbeddedQuotesFound = 0;
			do {
				buffer.mark();
				status.reset();
				findToken(buffer, maxPos, status);

				//End of token.  Process it.
				int nextPosition = buffer.position();
				buffer.reset();
				processToken(
					buffer.subSequence(0, status.charCount), listener, status);

				// keep track of embedded quotes
				numEmbeddedQuotesFound += status.numQuotesFound;

				//Move buffer position to start of next token
				if (nextPosition <= buffer.limit()) {
					buffer.position(nextPosition);
				} else {
					break;
				}
			} while (status.separatorType == SeparatorType.DELIMITER);

			// only generate the warning once for the line
			if ((numEmbeddedQuotesFound > 0) && (getGenerateWarnings())) {
				addWarning(
					"Found " + numEmbeddedQuotesFound + " embedded un-escaped quotes in line");
			}

		}
	}

	/**
	 * Process a single token, performing any cleanup that needs to be done
	 * with respect to quotes.
	 *
	 * @param token    Token to process
	 * @param listener Listener to call nextToken on
	 * @param status   the state of the token that was found
	 */
	private void processToken(
		CharSequence token, ParseListener listener, Status status) throws LineParseException {
		if ((status.numQuotesFound > 0) && !_returnQuotes) {
			// note, quotes may have been found within the token, in which case they
			// are left as is.  only quotes on the ends are stripped
			int start = 0;
			if (token.charAt(start) == _workingQuoteChar) {
				++start;
				--status.numQuotesFound;
			}
			int end = token.length();
			if (token.charAt(end - 1) == _workingQuoteChar) {
				--end;
				--status.numQuotesFound;
			}
			token = token.subSequence(start, end);
		}
		if (status.escapeFound) {
			token = _escapeQuotePattern.matcher(token.toString()).replaceAll(_escapeQuoteReplacement);
		}
		if (_trimTokens) {
			token = token.toString().trim();
		}
		if (_removeNonAscii) {
			token = token.toString().replaceAll("[^\\p{ASCII}]", "");
		}
		if (_escapeCrlf) {
			token = StringUtil.escapeCrlfs(token.toString());
		}
		if (_escapeDelimiter) {
			try {
				Character delim = Character.valueOf(_delimiter);
				token = StringUtil.hexEncodeChars(token.toString(), delim);
			}
			catch (UnsupportedEncodingException e) {
				throw new LineParseException(e);
			}
		}
		listener.nextToken(token);
	}

	@Override
	void findNewLineStartAndEnd() {
		Status status = new Status();
		do {
			status.separatorType = null;
			findToken(_buffer, _buffer.limit(), status);
		} while (status.separatorType == SeparatorType.DELIMITER);
		if (status.separatorType == SeparatorType.END_OF_BUFFER) {
			_eol[0] = -1;
			_eol[1] = -1;
		}
	}

	private void findToken(CharBuffer buffer, int maxPos, Status status) {
		boolean escape = false;
		boolean inQuote = false;
		boolean gotCarriageReturn = false;
		while (buffer.hasRemaining() && (buffer.position() < maxPos)) {
			char c = buffer.get();
			++status.charCount;
			if (!inQuote) {
				if (c == '\r') {
					// possibly Windows
					gotCarriageReturn = true;
				} else if (c == '\n') {
					_eol[0] = buffer.position() - 1;
					_eol[1] = _eol[0];
					--status.charCount;
					if (gotCarriageReturn) {
						//Windows
						--_eol[0];
						--status.charCount;
					}
					status.separatorType = SeparatorType.END_OF_LINE;
					return;
				} else if (c == _delimiter) {
					--status.charCount;
					status.separatorType = SeparatorType.DELIMITER;
					return;
				} else {
					gotCarriageReturn = false;
				}
			}
			if (_handleQuotes) {
				if (c == _workingQuoteChar) {
					if (!escape) {
						if (!_escapeQuoteIsQuote || !inQuote) {
							// the sane case
							inQuote = !inQuote;
							++status.numQuotesFound;
						} else {
							// if the escape quote is a quote, life gets really complicated.
							// need to peek at next char and see if it too is quote.  note,
							// the get(int) method does not advance the current position.
							if (buffer.hasRemaining() && (buffer.position() < maxPos) &&
							    (buffer.get(buffer.position()) == _workingQuoteChar)) {
								// this is an escape quote
								escape = true;
							} else {
								// this is the end quote
								inQuote = !inQuote;
								++status.numQuotesFound;
							}
						}
					} else {
						status.escapeFound = true;
						escape = false;
					}
				} else if (c == _escapeQuote) {
					escape = true;
				} else {
					escape = false;
				}
			}
		}
		status.separatorType = SeparatorType.END_OF_BUFFER;
	}

	/** Utility struct used to return results from the token parsing method. */
	private static class Status {
		public int           charCount;
		public SeparatorType separatorType;
		public boolean       escapeFound;
		public int           numQuotesFound;

		public void reset() {
			charCount = 0;
			separatorType = null;
			escapeFound = false;
			numQuotesFound = 0;
		}
	}


}
