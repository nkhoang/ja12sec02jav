package com.nkhoang.common.abtab.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.nkhoang.common.abtab.RawDataParseException;
import com.nkhoang.common.abtab.handler.RawDataHandler;
import org.apache.commons.compress.tar.TarEntry;
import org.apache.commons.compress.tar.TarInputStream;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parses archive files (zip files or tar files.)
 * You can either explicitly specify entries inside the archive
 * that should be parsed, or specify a single parser that will
 * parse all entries found in the archive.
 */
public class ArchiveParser extends RawDataStreamParser {

	private static final Log LOG = LogFactory.getLog(ArchiveParser.class);

	private static final long serialVersionUID = 1274003620757206001L;

	/** Pattern that recognizes tar files */
	private static final Pattern PAT_TAR = Pattern.compile(".*?((\\.tar(\\.\\w+)?)|(\\.tgz))");
	/** Pattern that recognizes zip files */
	private static final Pattern PAT_ZIP = Pattern.compile(".*\\.zip");

	/** Map of entry sources to entries */
	private Map<String, ArchiveEntry> _entries = new HashMap<String, ArchiveEntry>();
	/** User-specified parser that will be used to parse every entry in the archive */
	private RawDataStreamParser _parser;

	public void addEntry(ArchiveEntry entry) {
		_entries.put(entry.getSource(), entry);
	}

	public Map<String, ArchiveEntry> getEntries() {
		return _entries;
	}

	public void setParser(RawDataStreamParser parser) {
		_parser = parser;
	}

	//Javadoc copied from RawDataParser
	@Override
	public void doParse(RawDataHandler handler, InputStream input) throws RawDataParseException {
		if (!(_parser == null ^ _entries.isEmpty())) {
			throw new RawDataParseException(
				"You must either define a parser or a list of entries");
		}
		if (_inputURI != null) {
			String s = _inputURI.toString();
			try {
				Matcher m = PAT_ZIP.matcher(s);
				if (m.matches()) {
					parseZip(handler, input);
				} else {
					m = PAT_TAR.matcher(s);
					if (m.matches()) {
						parseTar(handler, input);
					} else {
						throw new RawDataParseException(
							"Can't determine how to decompress " + s + " from its file extension.");
					}
				}
			}
			catch (IOException e) {
				throw new RawDataParseException(e);
			}
		} else {
			try {
				parseZip(handler, input);
			}
			catch (IOException e) {
				try {
					parseTar(handler, input);
				}
				catch (IOException e2) {
					throw new RawDataParseException(e2);
				}
			}
		}
	}

	/**
	 * Parse a zip archive.
	 *
	 * @param handler Handler that listens for parse events.
	 * @param input   Stream to parse that contains the contents of a zip archive.
	 *
	 * @throws java.io.IOException Thrown if the zip archive couldn't be read.
	 */
	void parseZip(RawDataHandler handler, InputStream input) throws RawDataParseException, IOException {
		ZipInputStream zipIn = new ZipInputStream(new BufferedInputStream(input));
		ZipEntry zipEntry;
		while ((zipEntry = zipIn.getNextEntry()) != null) {
			processArchiveEntry(zipEntry.getName(), zipIn, handler);
		}
	}

	/**
	 * Parse a tar.
	 *
	 * @param handler Handler that listens for parse events.
	 * @param input   Stream to parse that contains the contents of a tar.
	 *
	 * @throws java.io.IOException Thrown if the tar couldn't be read.
	 */
	void parseTar(RawDataHandler handler, InputStream input) throws RawDataParseException, IOException {
		TarInputStream tarIn = new TarInputStream(new BufferedInputStream(input));
		TarEntry tarEntry;
		while ((tarEntry = tarIn.getNextEntry()) != null) {
			processArchiveEntry(tarEntry.getName(), tarIn, handler);
		}
	}

	/** Common processing of either a zip or a tar */
	private void processArchiveEntry(
		String entryName, InputStream input, RawDataHandler handler) throws RawDataParseException, IOException {
		ArchiveEntry entry = _entries.get(entryName);
		if (_parser != null || entry != null) {
			InputStream delegateInput = new CloseShieldInputStream(input);
			RawDataStreamParser delegateParser;
			if (_parser != null) {
				delegateParser = _parser;
				if (delegateParser instanceof SimpleParser) {
					((SimpleParser) delegateParser).setTableNameFromUrl(entryName, true);
				}
			} else {
				delegateParser = entry.getParser();
				if (delegateParser instanceof SimpleParser) {
					((SimpleParser) delegateParser).setTableName(entry.getName());
				}
			}
			delegateParser.parse(handler, delegateInput);
		}
	}

	/** User-defined explicit archive entry to be parsed from the archive */
	public static class ArchiveEntry {

		/** Parser to use to parse this entry */
		private RawDataStreamParser _parser;
		/** Table name to pass to startTable */
		private String              _name;
		/** Name of the entry inside the archive */
		private String              _source;

		public ArchiveEntry() {
		}

		public ArchiveEntry(String name, String source, RawDataStreamParser parser) {
			_name = name;
			_source = source;
			_parser = parser;
		}

		public String getName() {
			return _name;
		}

		public void setName(String name) {
			_name = name;
		}

		public String getSource() {
			return _source;
		}

		public void setSource(String source) {
			_source = source;
		}

		public RawDataStreamParser getParser() {
			return _parser;
		}

		public void setParser(RawDataStreamParser parser) {
			_parser = parser;
		}

	}

}
