package com.nkhoang.common.abtab.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.nkhoang.common.abtab.RawDataParseException;

/** Describe class AbstractRawDataHandler here. */
public abstract class AbstractRawDataHandler implements RawDataHandler {

	/** number of rows added to the current table */
	private long _numTableRows;
	/** any warnings received during parsing */
	private List<String> _warnings = new ArrayList<String>();


	/** Resets the current table row count, should be called in startTable. */
	protected void resetNumTableRows() {
		_numTableRows = 0;
	}

	/** Increments the current table row count, should be called in addRow. */
	protected void incNumTableRows() {
		++_numTableRows;
	}

	public void addWarning(String msg) throws RawDataParseException {
		_warnings.add(msg);
	}

	/** @return the number of rows added to the current table */
	public long getNumTableRows() {
		return _numTableRows;
	}

	/** Clears any currently accumulated warnings. */
	public void clearWarnings() {
		_warnings.clear();
	}

	/** @return any warnings generated during parsing */
	public Collection<String> getWarnings() {
		return _warnings;
	}

}
