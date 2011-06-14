package com.nkhoang.common.abtab.parser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.nkhoang.common.abtab.RawDataParseException;
import com.nkhoang.common.util.FileUtil;
import com.nkhoang.common.util.IOUtil;
import org.apache.commons.compress.bzip2.CBZip2InputStream;
import org.apache.commons.compress.tar.TarEntry;
import org.apache.commons.compress.tar.TarInputStream;
import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Creates RawDataParsers. */
public class ParserFactory {

	/**
	 * the default charset to use for delimited data which looks like it is
	 * using and extended charset
	 */
	public static final String DEFAULT_EXTENDED_CHARSET = "ISO-8859-1";

	private static final String DEFAULT_CHARSET = "US-ASCII";

	/** Candidate delimiters to try, in order of preference. */
	private static final List<Character> DELIMITERS = Arrays.asList('\t', ',', '|');

	private static final Log LOG = LogFactory.getLog(ParserFactory.class);

	private String _extendedCharset;

	public ParserFactory() {
		this(null);
	}

	public ParserFactory(String extendedCharset) {
		_extendedCharset = ((extendedCharset != null) ? extendedCharset : DEFAULT_EXTENDED_CHARSET);
	}

	public String getExtendedCharset() {
		return _extendedCharset;
	}

	public void setExtendedCharset(String newExtendedCharset) {
		_extendedCharset = newExtendedCharset;
	}

	/**
	 * Attempts to infer an appropriate parser for an input stream.
	 *
	 * @throws java.io.IOException   Thrown if there was a problem reading from the stream.
	 * @throws RawDataParseException Thrown if the factory was unable to infer
	 *                               an appropriate parser for the stream.
	 */
	public RawDataStreamParser inferParser(InputStream is) throws IOException, RawDataParseException {
		BufferedInputStream in = new BufferedInputStream(is);
		in.mark(8096);
		if (isGZip(in)) {
			return buildGZipParser(in);
		} else if (isBZip(in)) {
			return buildBZipParser(in);
		} else if (isZip(in)) {
			return buildZipParser(in);
		} else if (isTar(in)) {
			return buildTarParser(in);
		/*} else if (isAccess(in)) {
			return new AccessParser();
		} else if (isExcel(in)) {
			return new ExcelParser();*/
		} else {
			return buildDelimitedParser(in);
		}
	}

	static boolean isGZip(InputStream in) throws IOException {
		try {
			return in.read() == 0x1f && in.read() == 0x8b;
		}
		finally {
			in.reset();
		}
	}

	static boolean isBZip(InputStream in) throws IOException {
		try {
			return in.read() == 'B' && in.read() == 'Z' && in.read() == 'h';
		}
		finally {
			in.reset();
		}
	}

	private boolean isZip(InputStream in) throws IOException {
		try {
			return in.read() == 'P' && in.read() == 'K' && in.read() == 0x03 && in.read() == 0x04;
		}
		finally {
			in.reset();
		}
	}

	private boolean isTar(InputStream in) throws IOException {
		try {
			for (int i = 0; i < 257; i++) {
				if (in.read() == -1) {
					return false;
				}
			}
			return in.read() == 'u' && in.read() == 's' && in.read() == 't' && in.read() == 'a' && in.read() == 'r';
		}
		finally {
			in.reset();
		}
	}

	private boolean isAccess(InputStream in) throws IOException {
		try {
			for (int i = 0; i < 4; i++) {
				if (in.read() == -1) {
					return false;
				}
			}
			return in.read() == 'S' && in.read() == 't' && in.read() == 'a' && in.read() == 'n' && in.read() == 'd' &&
			       in.read() == 'a' && in.read() == 'r' && in.read() == 'd' && in.read() == ' ' && in.read() == 'J' &&
			       in.read() == 'e' && in.read() == 't' && in.read() == ' ' && in.read() == 'D' && in.read() == 'B';
		}
		finally {
			in.reset();
		}
	}

	private boolean isExcel(InputStream in) throws IOException {
		try {
			return in.read() == 0xd0 && in.read() == 0xcf && in.read() == 0x11 && in.read() == 0xe0 &&
			       in.read() == 0xa1 && in.read() == 0xb1 && in.read() == 0x1a && in.read() == 0xe1;
		}
		finally {
			in.reset();
		}
	}

	private RawDataStreamParser buildGZipParser(InputStream in) throws IOException, RawDataParseException {
		RawDataStreamParser rtn = inferParser(new GZIPInputStream(in));
		rtn.setCompression("gzip");
		return rtn;
	}

	private RawDataStreamParser buildBZipParser(InputStream in) throws IOException, RawDataParseException {
		in.read();
		in.read();
		RawDataStreamParser rtn = inferParser(new CBZip2InputStream(in));
		rtn.setCompression("bzip2");
		return rtn;
	}

	private ArchiveParser buildZipParser(InputStream in) throws IOException, RawDataParseException {
		ArchiveParser rtn = new ArchiveParser();
		ZipInputStream zipIn = new ZipInputStream(in);
		ZipEntry zipEntry;
		while ((zipEntry = zipIn.getNextEntry()) != null) {
			ArchiveParser.ArchiveEntry entry = new ArchiveParser.ArchiveEntry();
			entry.setName(zipEntry.getName());
			entry.setSource(zipEntry.getName());
			entry.setParser(inferParser(zipIn));
			rtn.addEntry(entry);
		}
		return rtn;
	}

	private ArchiveParser buildTarParser(InputStream in) throws IOException, RawDataParseException {
		ArchiveParser rtn = new ArchiveParser();
		TarInputStream tarIn = new TarInputStream(in);
		TarEntry tarEntry;
		while ((tarEntry = tarIn.getNextEntry()) != null) {
			ArchiveParser.ArchiveEntry entry = new ArchiveParser.ArchiveEntry();
			entry.setName(tarEntry.getName());
			entry.setSource(tarEntry.getName());
			entry.setParser(inferParser(tarIn));
			rtn.addEntry(entry);
		}
		return rtn;
	}

	private DelimitedDataParser buildDelimitedParser(InputStream in) throws RawDataParseException {
		File tempFile = null;
		BufferedReader reader = null;
		try {

			// copy input stream to temp file cause we need to make a few passes
			// through the data
			tempFile = File.createTempFile("delparse", ".txt");
			FileOutputStream ostream = new FileOutputStream(tempFile);
			try {
				CopyUtils.copy(in, ostream);
			}
			finally {
				IOUtils.closeQuietly(ostream);
			}

			// attempt to determine whether file uses extended charset
			String charset = (IOUtil.is7BitClean(tempFile) ? DEFAULT_CHARSET : _extendedCharset);

			reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(tempFile), charset));
			String firstLine = reader.readLine();
			// Skip first line in case they're headers so we can look for quote
			// chars.
			String line = reader.readLine();
			if (line == null) {
				// oops, let's use the first line anyway
				line = firstLine;
			}
			if (line != null) {

				// Figure out which (if any) of our candidate delimiters occurs the
				// most on this line.
				int maxCount = 0;
				Character delimiter = null;
				for (Character c : DELIMITERS) {
					int count = countChar(line, c);
					if (count > maxCount) {
						maxCount = count;
						delimiter = c;
					}
				}

				if (maxCount > 0) {
					DelimitedDataParser rtn = new DelimitedDataParser();
					rtn.setCompression("none");
					rtn.setDelimiter(delimiter);
					if (isQuoted(line, delimiter, "\"")) {
						rtn.setQuote('\"');
					} else if (isQuoted(line, delimiter, "'")) {
						rtn.setQuote('\'');
					}
					if (charset == _extendedCharset) {
						rtn.setCharsetName(_extendedCharset);
					}
					return rtn;
				}
			}
		}
		catch (IOException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Couldn't parse input as text", e);
			}
		}
		finally {
			IOUtil.closeQuietly(reader);
			FileUtil.deleteQuietly(tempFile);
		}
		throw new RawDataParseException("Could not infer appropriate parser from input stream");
	}

	/**
	 * @param s String whose characters will be inspected.
	 * @param c Character to count.
	 *
	 * @return Number of times that the character c appears in the String s.
	 */
	private int countChar(String s, char c) {
		int rtn = 0;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == c) {
				rtn++;
			}
		}
		return rtn;
	}

	/**
	 * @param line      Line to inspect
	 * @param delimiter Field delimiter on that line
	 * @param quote     Quote string for which to test
	 *
	 * @return Whether or not the quote string is likely to act as a quote character
	 *         on that line
	 */
	private boolean isQuoted(String line, char delimiter, String quote) {
		return line.indexOf(quote + delimiter) > -1 && line.indexOf(delimiter + quote) > -1;
	}

}
