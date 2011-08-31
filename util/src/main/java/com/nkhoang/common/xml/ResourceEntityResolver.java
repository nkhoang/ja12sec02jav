package com.nkhoang.common.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Entity resolver that will look first in the class path for
 * a resource, and then look in the file system for the resource.
 *
 * @version $Date$ $Revision$
 */
public class ResourceEntityResolver implements EntityResolver {
	/** The logger to use. */
	private static final Log     LOG                  = LogFactory.getLog(ResourceEntityResolver.class);
	/** Attribute useRelativeResource. */
	private              boolean _useRelativeResource = true;

	/**
	 * Allow the application to resolve external entities from
	 * resources in the class path.
	 *
	 * @param publicId The public identifier of the external entity
	 *                 being referenced, or null if none was supplied.
	 * @param systemId The system identifier of the external entity
	 *                 being referenced.
	 *
	 * @return An InputSource object describing the new input source,
	 *         or null to request that the parser open a regular
	 *         URI connection to the system identifier.
	 *
	 * @throws org.xml.sax.SAXException Any SAX exception, possibly
	 *                                  wrapping another exception.
	 * @throws java.io.IOException      A Java-specific IO exception,
	 *                                  possibly the result of creating a new InputStream
	 *                                  or Reader for the InputSource.
	 * @see org.xml.sax.InputSource
	 */
	public InputSource resolveEntity(
		String publicId, String systemId) throws SAXException, IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Looking for entity as system resource with system id " + systemId);
		}
		InputStream in = getClass().getClassLoader().getResourceAsStream(systemId);
		if (in != null) {
			return new InputSource(in);
		}

		int idx = systemId.lastIndexOf(File.separator);
		if (_useRelativeResource && idx > -1 && idx != systemId.length()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Looking for entity as system resource with system id " + systemId.substring(idx + 1));
			}
			in = getClass().getClassLoader().getResourceAsStream(systemId.substring(idx + 1));
			if (in != null) {
				return new InputSource(in);
			}
		}


		if (LOG.isDebugEnabled()) {
			LOG.debug("Looking for entity as file with system id " + systemId);
		}
		File f = new File(systemId);
		if (f.isFile()) {
			return new InputSource(new FileInputStream(f));
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Could resolve system id " + systemId);
		}
		return null;
	}

	/** Getter method for useRelativeResource. */
	public boolean getUseRelativeResource() {
		return _useRelativeResource;
	}

	/** Setter method for useRelativeResource. */
	public void setUseRelativeResource(boolean useRelativeResource) {
		_useRelativeResource = useRelativeResource;
	}


} 
