package com.nkhoang.common.abtab.handler;

import com.nkhoang.common.abtab.RawDataParseException;

import java.sql.ResultSetMetaData;
import java.util.List;

/**
 * Listens to events from RawDataParsers that are parsing raw data into tabular
 * formats.
 */
public interface RawDataHandler {

	/**
	 * Called exactly once before any other methods.  This should not be called by
	 * a RawDataParser.  It is called by the calling application.  This allows
	 * a single handler to be fed data from multiple RawDataParsers sequentially.
	 * If you are going to be feeding this handler to multiple RawDataParsers,
	 * then this must be called prior to each parse.
	 */
	void setUp() throws RawDataParseException;

	/**
	 * Called by a RawDataParser when the parsing of a new table starts.
	 *
	 * @param tableName Name of the table being parsed.
	 * @param md        Metadata about the table.
	 */
	void startTable(String tableName, ResultSetMetaData md) throws RawDataParseException;

	/**
	 * Called by a RawDataParser once for each logical row parsed.
	 *
	 * @param values Values parsed out of the row.
	 *
	 * @return Whether or not to continue adding rows within this table.
	 */
	boolean addRow(List<?> values) throws RawDataParseException;

	/** Called by a RawDataParser at the end of each table. */
	void endTable() throws RawDataParseException;

	/**
	 * May be called by the parser at any time after setUp and before tearDown
	 * to indicate a questionable situation encountered by the parser.
	 */
	void addWarning(String msg) throws RawDataParseException;

	/**
	 * Called exactly once at the end of the parsing.  This should not be called by
	 * a RawDataParser.  It is called by the calling application.  This allows
	 * a single handler to be fed data from multiple RawDataParsers sequentially.
	 * If you are going to be feeding this handler to multiple RawDataParsers,
	 * then this must be called after each parse.
	 */
	void tearDown() throws RawDataParseException;

}
