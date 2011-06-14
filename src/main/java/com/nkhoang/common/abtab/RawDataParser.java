package com.nkhoang.common.abtab;

import com.nkhoang.common.abtab.handler.RawDataHandler;

import java.io.Serializable;
import java.net.URI;

/**
 * Parses raw data into a tabular format in an event-driven fashion.
 * Implementations are not expected to be thread-safe.
 */
public interface RawDataParser extends Serializable {

	/**
	 * @param handler Handler with callback methods that implementations call into.
	 * @param input   Source of the data.
	 */
	public void parse(RawDataHandler handler, URI input) throws RawDataParseException;

}
