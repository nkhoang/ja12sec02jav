package com.nkhoang.common.util.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.nkhoang.common.util.URLProtocolHandlerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classpath resource URLHandler provides easy access to resources via the
 * classloader system. URL's take the form of <PRE>
 * resource:/com/company/path/to/file.txt
 * </PRE>
 *
 * @see java.net.URL
 */
public class Handler extends URLStreamHandler {

	private static final Log    LOG      = LogFactory.getLog(Handler.class);
	private static final String PROTOCOL = "resource";

	static {
		try {
			URLProtocolHandlerUtil.registerProtocolHandler(PROTOCOL, new Handler());
		}
		catch (Error e) {
			LOG.warn("Can't register protocol handler", e);
			throw e;
		}
		catch (RuntimeException e) {
			LOG.warn("Can't register protocol handler", e);
			throw e;
		}
	}

	/**
	 * Classes that need to handle resource-style URLs can call this message to
	 * ensure that this class is initialized.
	 */
	public static void init() {
		// Don't do anything.  This method is just called to initialize class.
	}

	/**
	 * Parse a string into a URL object. Host and port are not changed. Only
	 * the file attribute is changed.
	 */
	@Override
	protected void parseURL(
		URL u, String spec, int start, int limit) {

		// Args to setURL are:
		//   URL u,
		//   String protocol,
		//   String host,
		//   int port,
		//   String authority,
		//   String userInfo,
		//   String file,
		//   String query,
		//   String ref
		super.setURL(
			u, PROTOCOL, null, -1, null, null, spec.substring(start, limit), null, null);

	}


	/** Opens a connection to the object referenced by the URL argument. */
	@Override
	protected URLConnection openConnection(URL u) throws IOException {

		URL res = convertToSystemURL(u);

		if (res == null) {
			throw new FileNotFoundException(u + " not found");
		}
		return res.openConnection();
	}


	public URL convertToSystemURL(URL u) {
		String path = u.getFile();
		while (path.charAt(0) == '/') {
			path = path.substring(1);
			// skip preceding slashes
		}

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL res = ((loader != null) ? loader.getResource(path) : null);

		if (res == null) {
			res = u.getClass().getResource(path);
		}
		if (res == null) {
			res = ClassLoader.getSystemResource(path);
		}
		while (res == null && loader != null) {
			loader = loader.getParent();
			if (loader != null) {
				res = loader.getResource(path);
			}
		}

		return res;
	}

}