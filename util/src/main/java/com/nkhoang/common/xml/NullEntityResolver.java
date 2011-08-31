package com.nkhoang.common.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This EntityResolver always returns an empty stream. This is useful for
 * getting the parser to ignore &lt;!DOCTYPE&gt; declarations. Be careful in
 * ignoring these as things like IDs, IDREFs, and default attribute values won't
 * be picked up in your parsing.
 *
 */
public class NullEntityResolver implements EntityResolver {

	/** Resolves the entity to an empty stream */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		return new InputSource(new StringReader(""));
	}

}
