package com.nkhoang.common.xml;

import java.net.MalformedURLException;

import java.net.URL;
import javax.xml.validation.Schema;

/**
 * Simple class for managing a Schema as a singleton instance, lazily loaded
 * from a URL.
 */
public class SchemaSingleton {

	private String _url;
	private Schema _schema;

	public SchemaSingleton(String url) {
		_url = url;
	}

	/**
	 * @return the Schema loaded from the configured URL.
	 *
	 * @throws IllegalArgumentException if the url is invalid
	 * @throws XMLParsingException      if the schema document cannot be parsed
	 */
	public synchronized Schema get() {
		if (_schema == null) {
			try {
				_schema = XMLUtil.parseAsSchema(new URL(_url));
			}
			catch (MalformedURLException e) {
				throw new IllegalArgumentException("cannot load schema", e);
			}
		}
		return _schema;
	}

}
