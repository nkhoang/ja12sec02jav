package com.nkhoang.common.util.parser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parses a readable byte channel, 1 line at a time.  When using a FileChannel,
 * the paser uses <code>map</code> to read from the file, which yields a roughly
 * 15% performance boost for very large files.  For non-FileChannels, performance
 * is about the same as traditional Java I/O.  Note that running Java with the
 * <code>-server</code> switch yields a roughly 30% performance boost over
 * <code>-client</code> (the default JVM.)
 *
 * @see java.nio.channels.FileChannel#map
 */
public abstract class LineParser {

	private static final Log LOG = LogFactory.getLog(LineParser.class.getCanonicalName());

	/** Defaults to US-ASCII if no charset is supplied */
	private static final String DEFAULT_CHARSET = "US-ASCII";

	/** For non-FileChannel input, use this buffer for temporary storage */
	private ByteBuffer _byteBuffer;
	/** Characters that start comment lines */
	private char[]     _commentChars;
	/** The main buffer that everything works from */
	CharBuffer _buffer;
	/** iff <code>true</code>, the channel is empty */
	private boolean     _gotEOF;
	/** The character set to use when parsing */
	private Charset     _charset;
	/** For FileChannel input, a reference to the channel */
	private FileChannel _fileChannel;
	/** Current line # being parsed */
	private int _lineNumber = 0;
	/** Buffer index of the beginning of the next line */
	private int _lineStart  = 0;
	/**
	 * Buffer index of the end of the next line.  First number is the beginning
	 * index of the new line, second number is the end index of the new line
	 * (i.e. \r\n vs \n)
	 */
	int[] _eol = new int[2];
	/** Block size to use when parsing, defaults to 8 MB. */
	private int  _blockSize       = 8388608;
	/** For FileChannel input, the next offset to read from in the file */
	private long _channelStart    = 0L;
	private int  _startLineNumber = 1;
	/** Listener to send parse events to */
	private ParseListener       _listener;
	/** Pattern to start parsing at (parse this line as the first line) */
	private Pattern             _startAt;
	/** Pattern to start parsing after (parse the line after this as the first line ) */
	private Pattern             _startAfter;
	/** whether or not to skip empty lines (default is false) */
	private boolean             _skipEmptyLines;
	/**
	 * whether or not to ignore a single control character at the end of the
	 * file (default is false).  a control character is any character with
	 * ASCII value <= 32 (these are the same characters removed by the
	 * {@link String#trim} method)
	 */
	private boolean             _ignoreEOFChar;
	/** The channel being parsed */
	private ReadableByteChannel _channel;
	/**
	 * indicate whether or not warnings should be generated during parsing,
	 * default is <code>false</code>)
	 */
	private boolean             _generateWarnings;
	/** whether or not to skip error lines (default is false) */
	private boolean _skipErrorLines = false;

	/**
	 * Construct a new parser.
	 *
	 * @param input    Channel to parse.  To use with an InputStream, use
	 *                 <code>Channels.newChannel(stream)</code>.
	 * @param listener Receives parse events
	 */
	public LineParser(ReadableByteChannel input, ParseListener listener) {
		this(input, listener, false, false);
	}

	/**
	 * Construct a new parser.
	 *
	 * @param input          Channel to parse.  To use with an InputStream, use
	 *                       <code>Channels.newChannel(stream)</code>.
	 * @param listener       Receives parse events
	 * @param skipEmptyLines whether or not to skip empty lines
	 * @param ignoreEOFChar  whether or not to ignore single control character at
	 *                       the end of the file
	 */
	public LineParser(
		ReadableByteChannel input, ParseListener listener, boolean skipEmptyLines, boolean ignoreEOFChar) {
		_channel = input;
		_listener = listener;
		_skipEmptyLines = skipEmptyLines;
		_ignoreEOFChar = ignoreEOFChar;
	}

	/** @param blockSize Block size to use when parsing */
	public void setBlockSize(int blockSize) {
		_blockSize = blockSize;
	}

	/** @param charset Character set to use when parsing */
	public void setCharset(Charset charset) {
		_charset = charset;
	}

	/** @param chars Characters that start comment lines */
	public void setCommentChars(char[] chars) {
		_commentChars = chars;
	}

	public void setStartAt(String regex) {
		if (_startAfter != null) {
			throw new IllegalArgumentException("startAfter already set.  Can't set startAt.");
		}
		_startAt = Pattern.compile(regex);
	}

	public void setStartAfter(String regex) {
		if (_startAt != null) {
			throw new IllegalArgumentException("startAt already set.  Can't set startAfter.");
		}
		_startAfter = Pattern.compile(regex);
	}

	public void setStartLineNumber(int number) {
		_startLineNumber = number;
	}

	/**
	 * @param skipEmptyLines iff <code>true</code>, a line with no characters on
	 *                       it will be ignored (not passed to the
	 *                       {@link #parseLine} method).
	 */
	public void setSkipEmptyLines(boolean skipEmptyLines) {
		_skipEmptyLines = skipEmptyLines;
	}

	/**
	 * @param ignoreEOFChar iff <code>true</code>, a single control character at
	 *                      the end of the will be ignored.  a control character
	 *                      is any character with ASCII value <= 32 (these are
	 *                      the same characters removed by the
	 *                      {@link String#trim} method)
	 */
	public void setIgnoreEOFChar(boolean ignoreEOFChar) {
		_ignoreEOFChar = ignoreEOFChar;
	}

	public boolean getGenerateWarnings() {
		return _generateWarnings;
	}

	public void setGenerateWarnings(boolean newGenerateWarnings) {
		_generateWarnings = newGenerateWarnings;
	}

	/**
	 * Sets skip error lines indicator.
	 *
	 * @param skipErrorLines Indicating whether or not to skip error lines
	 */
	public void setSkipErrorLines(boolean skipErrorLines) {
		_skipErrorLines = skipErrorLines;
	}

	/** Parse the data */
	public void parse() throws LineParseException, IOException {
		beginParse();
		if (_channel instanceof FileChannel) {
			_fileChannel = (FileChannel) _channel;
		} else {
			_byteBuffer = ByteBuffer.allocateDirect(_blockSize);
		}
		_listener.startData();
		_eol[0] = -1;
		_eol[1] = -1;
		fillBuffer();
		if (_buffer != null && _buffer.hasRemaining()) {
			_lineStart = 0;
			boolean startDefined = _startAt != null || _startAfter != null;
			boolean parseThisLine = !startDefined;
			Matcher matcher;
			for (int i = 0; i < _startLineNumber - 1; i++) {
				findNextLine();
				_lineNumber++;
			}
			do {
				findNextLine();
				if (_lineStart != -1 && !isComment()) {
					_buffer.position(_lineStart);
					if (startDefined && !parseThisLine && _eol[0] > _lineStart) {
						CharSequence line = _buffer.subSequence(0, _eol[0] - _lineStart);
						if (_startAt != null) {
							matcher = _startAt.matcher(line);
							if (matcher.matches()) {
								parseThisLine = true;
							}
						} else {
							matcher = _startAfter.matcher(line);
							if (matcher.matches()) {
								parseThisLine = true;
								continue;
							}
						}
					}
					if (parseThisLine) {
						int lineLength = _eol[0] - _lineStart;
						boolean skippedEOFChar = false;
						boolean skipLine = (((lineLength == 0) && _skipEmptyLines) ||
						                    (_gotEOF && (lineLength == 1) && _ignoreEOFChar &&
						                     (_buffer.get(_lineStart) <= ' ') && (skippedEOFChar = true)));

						if (skipLine) {
							if (skippedEOFChar && getGenerateWarnings()) {
								addWarning("Skipped control character at end of file");
							}
						} else {
							// Parse line and handle error
							try {
								parseLine(_buffer, lineLength, _listener);
							}
							catch (LineParseException e) {
								if (_skipErrorLines) {
									LOG.error("Failed to parse line " + (_lineNumber + 1), e);
									_listener.addErrorLine(_lineNumber + 1);
									_listener.initTokens();
									skipLine = true;
								} else {
									e.setLineNumber(_lineNumber + 1);
									throw e;
								}
							}

							// Call listener to process the parsed line
							if (!skipLine) {
								if (!_listener.endLine(_lineNumber + 1)) {
									break;
								}
							}
						}
					}
				}
				_lineNumber++;
			} while (_lineStart != -1);
		}
		_listener.endData();
		_channel.close();
	}

	protected void addWarning(String msg) throws LineParseException {
		if (getGenerateWarnings()) {
			_listener.addWarning(_lineNumber + 1, msg);
		}
	}

	private boolean isComment() {
		if (_commentChars != null) {
			for (int i = 0; i < _commentChars.length; i++) {
				if (_buffer.get(_lineStart) == _commentChars[i]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Fill the buffer with more input.  If EOF has been reached, the buffer
	 * will be set to null.
	 */
	private void fillBuffer() throws IOException {
		if (_fileChannel != null) {
			long fileSize = _fileChannel.size();
			if (fileSize > _channelStart) {
				_buffer = _charset.decode(
					_fileChannel.map(
						FileChannel.MapMode.READ_ONLY, _channelStart,
						Math.min((long) _blockSize, fileSize - _channelStart)));
			}
			if ((_buffer == null) || ((_channelStart + _buffer.remaining()) >= fileSize)) {
				_gotEOF = true;
			}
		} else {
			_byteBuffer.clear();
			if ((_buffer != null) && _buffer.hasRemaining()) {
				//Copy left-over characters back to head of byte buffer
				_charset.newEncoder().encode(_buffer, _byteBuffer, true);
			}
			if (_channel.read(_byteBuffer) == -1) {
				_gotEOF = true;
			}
			_byteBuffer.flip();
			_buffer = _charset.decode(_byteBuffer);
		}
	}

	/**
	 * Set _lineStart to the beginning of the next line, and _eol to the end of
	 * the next line.
	 */
	private void findNextLine() throws IOException, LineParseException {

		// we do not generally need to loop here, but we occassionally do not find
		// the end of the line in the current buffer, in which case we get more
		// data and try again.
		while (true) {

			if ((_buffer == null) || (_gotEOF && ((_eol[1] + 1) >= _buffer.limit()))) {
				//EOF
				_lineStart = -1;
				return;
			}
			_lineStart = _eol[1] + 1;
			_buffer.position(_lineStart);
			findNewLineStartAndEnd();
			if (_eol[0] == -1) {
				//No end of line found

				if ((_lineStart == 0) && (_buffer.length() == _blockSize) && !_gotEOF) {
					//Buffer is not big enough to hold this line.
					throw new LineParseException("Block size too small");
				}

				if (_gotEOF) {
					_eol[0] = _buffer.position();
					_eol[1] = _eol[0];
				} else {
					//Read more from the channel
					_buffer.position(_lineStart);
					_channelStart += (long) _lineStart;
					_lineStart = 0;
					fillBuffer();

					// need to retry finding line with more data
					continue;
				}
			}

			// found the end of line, one way or another
			return;
		}
	}

	/** Find the beginning and end indexes of the next EOL */
	void findNewLineStartAndEnd() {
		boolean gotCarriageReturn = false;
		while (_buffer.hasRemaining()) {
			char c = _buffer.get();
			if (c == '\r') {
				// possibly Windows
				gotCarriageReturn = true;
			} else if (c == '\n') {
				_eol[0] = _buffer.position() - 1;
				_eol[1] = _eol[0];
				if (gotCarriageReturn) {
					//Windows
					--_eol[0];
				}
				return;
			} else {
				gotCarriageReturn = false;
			}
		}
		_eol[0] = -1;
		_eol[1] = -1;
	}

	/**
	 * Called at the beginning of the parse operation, allows sub-classes to do
	 * per-parse setup.
	 */
	protected void beginParse() {
		if (_charset == null) {
			_charset = Charset.forName(DEFAULT_CHARSET);
		}
	}

	/**
	 * Parse an individual line
	 *
	 * @param buffer   Buffer whose position is set to the start of the line
	 * @param length   Length of the line to parse
	 * @param listener Listener to call <code>nextToken</code> on for each token
	 *                 in the line
	 */
	protected abstract void parseLine(
		CharBuffer buffer, int length, ParseListener listener) throws LineParseException;

}
