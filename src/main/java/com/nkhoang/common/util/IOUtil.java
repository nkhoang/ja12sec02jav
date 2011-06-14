package com.nkhoang.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

import com.nkhoang.common.util.resource.Handler;
import com.nkhoang.common.util.serial.SerializationUtil;
import org.apache.commons.io.IOUtils;

/**
 * This class contains static methods for working with the <code>java.io</code>
 * package, and which cannot be found in Jakarta Commons IO.
 */
public class IOUtil {

	static {
		// handle resource urls
		Handler.init();
	}

	private static final int SEVEN_BIT_MASK = ~0x007F;

	/**
	 * Reads all bytes from a passed <code>InputStream</code>, up to a specified
	 * limit. If the stream contains fewer bytes than the limit, returns the
	 * entire stream contents.
	 * <p/>
	 * This method does not close the input stream.
	 *
	 * @param in       The input stream.
	 * @param maxBytes The maximum number of bytes to read from that stream.
	 *                 Pass <code>Integer.MAX_VALUE</code> if you want to
	 *                 read the entire stream.
	 */
	public static byte[] toByteArray(InputStream in, int maxBytes) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int bytesRead = 0;
		while ((maxBytes > 0) && (bytesRead >= 0)) {
			int bytesToRead = (maxBytes < buf.length) ? maxBytes : buf.length;
			bytesRead = in.read(buf, 0, bytesToRead);
			if (bytesRead > 0) {
				out.write(buf, 0, bytesRead);
				maxBytes -= bytesRead;
			}
		}

		return out.toByteArray();
	}

	/**
	 * Tests a file to see if it is 7-bit clean.  This can be used to determine
	 * if a text stream is ASCII encoded (which would be 7-bit clean), or
	 * encoded using some extended char set (which would not be 7-bit clean).
	 *
	 * @return <code>true</code> if the given file is 7-bit clean (no byte has
	 *         the 8-th bit set), <code>false</code> otherwise.
	 */
	public static boolean is7BitClean(File file) throws IOException {
		return is7BitClean(new FileInputStream(file));
	}

	/**
	 * Tests a stream to see if it is 7-bit clean.  This can be used to
	 * determine if a text stream is ASCII encoded (which would be 7-bit clean),
	 * or encoded using some extended char set (which would not be 7-bit clean).
	 * <p/>
	 * This method closes the given input stream.
	 *
	 * @return <code>true</code> if the given stream is 7-bit clean (no byte has
	 *         the 8-th bit set), <code>false</code> otherwise.
	 */
	public static boolean is7BitClean(InputStream in) throws IOException {
		// add some buffering, if necessary
		if (!(in instanceof BufferedInputStream)) {
			in = new BufferedInputStream(in);
		}
		try {
			int c;
			while ((c = in.read()) != -1) {
				if ((c & SEVEN_BIT_MASK) != 0) {
					return false;
				}
			}
			return true;
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Just like Jakarta Commons' IOUtils.closeQuietly, but takes a
	 * <code>Closeable</code> so we can use it in many more places.
	 * <p/>
	 * Unnecessary because {@link #closeQuietly(java.io.Closeable[])} can handle
	 * the single case - this method is being kept around for binary
	 * compatability with other library jars.
	 * <p/>
	 * Since there is no non-heinous way to declare that you want to use
	 * the other method, this is not being deprecated.
	 */
	public static void closeQuietly(Closeable c) {
		//delegate to the one that has the logic
		closeQuietly(new Closeable[]{c});
	}

	/**
	 * Just like Jakarta Commons' IOUtils.closeQuietly, but takes
	 * <code>Closeable</code>s so we can use it in many more places.
	 */
	public static void closeQuietly(Closeable... c) {
		if (c != null) {
			for (Closeable closeable : c) {
				if (closeable != null) {
					try {
						closeable.close();
					}
					catch (IOException e) {
						//ignore
					}
				}
			}
		}
	}

	/**
	 * Just like Jakarta Commons' IOUtils.closeQuietly, but takes
	 * <code>Object</code>s so we can use it in many more places.  Only closes
	 * those objects which implement Closeable.
	 */
	public static void closeQuietly(Object... c) {
		if (c != null) {
			for (Object closeable : c) {
				if (closeable instanceof Closeable) {
					try {
						((Closeable) closeable).close();
					}
					catch (IOException e) {
						//ignore
					}
				}
			}
		}
	}

	/**
	 * Opens the passed URL and copies its contents into a string using the
	 * default platform character encoding.
	 */
	public static String toString(URL url) throws IOException {
		return toString(url, Charset.defaultCharset().name());
	}


	/**
	 * Opens the passed URL and copies its contents into a string using the
	 * specified character encoding.
	 */
	public static String toString(URL url, String encoding) throws IOException {
		InputStream in = null;
		try {
			in = url.openStream();
			return IOUtils.toString(in, encoding);
		}
		finally {
			closeQuietly(in);
		}
	}

	/** Loads the Properties from the given url. */
	public static Properties loadProperties(String url) {
		return loadProperties(url, new Properties());
	}

	/** Loads the Properties from the given url into the given Properties. */
	public static Properties loadProperties(String url, Properties props) {
		InputStream stream = null;
		try {
			props.load(stream = (new URL(url)).openStream());
			return props;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			closeQuietly(stream);
		}
	}

	/** Loads the Properties from the given String. */
	public static Properties loadPropertiesFromString(String propsStr) {
		return loadPropertiesFromString(propsStr, new Properties());
	}

	/** Loads the Properties from the given String into the given Properties. */
	public static Properties loadPropertiesFromString(
		String propsStr, Properties props) {
		if (propsStr != null) {
			try {
				// load the string as a properties file (the Properties load method
				// expects ISO-8859-1 encoding).
				props.load(new ByteArrayInputStream(propsStr.getBytes("ISO-8859-1")));
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return props;
	}

	/**
	 * Serializes and optionally GZIP compresses the given object to a byte
	 * array.
	 *
	 * @return the serialized, possibly compressed bytes
	 *
	 * @deprecated moved to {@link SerializationUtil}
	 */
	@Deprecated
	public static byte[] serialize(Serializable obj, boolean gzipCompress) throws IOException {
		return SerializationUtil.serialize(obj, gzipCompress);
	}

	/**
	 * Serializes and GZIP compresses the given object to a byte array.
	 *
	 * @return the serialized, compressed bytes
	 *
	 * @deprecated moved to {@link SerializationUtil}
	 */
	@Deprecated
	public static byte[] serializeGZIPCompressed(Serializable obj) throws IOException {
		return SerializationUtil.serializeGZIPCompressed(obj);
	}

	/**
	 * Serializes and GZIP compresses the given stream.  The given stream will
	 * be closed once the object is written.
	 *
	 * @return the given stream
	 *
	 * @deprecated moved to {@link SerializationUtil}
	 */
	@Deprecated
	public static <OStream extends OutputStream> OStream serializeGZIPCompressed(
		Serializable obj, OStream ostream) throws IOException {
		return SerializationUtil.serializeGZIPCompressed(obj, ostream);
	}

	/**
	 * Deserializes an object from the given, possibly GZIP compressed bytes.
	 *
	 * @return the deserialized object
	 *
	 * @deprecated moved to {@link SerializationUtil}
	 */
	@Deprecated
	public static Object deserialize(byte[] bytes, boolean gzipCompressed) throws IOException {
		return SerializationUtil.deserialize(bytes, gzipCompressed);
	}

	/**
	 * Deserializes an object from the given GZIP compressed bytes.
	 *
	 * @return the deserialized object
	 *
	 * @deprecated moved to {@link SerializationUtil}
	 */
	@Deprecated
	public static Object deserializeGZIPCompressed(byte[] bytes) throws IOException {
		return SerializationUtil.deserializeGZIPCompressed(bytes);
	}

	/**
	 * Deserializes an object from the given GZIP compressed stream.  The given
	 * stream will be closed once the object is read.
	 *
	 * @return the deserialized object
	 *
	 * @deprecated moved to {@link SerializationUtil}
	 */
	@Deprecated
	public static Object deserializeGZIPCompressed(InputStream istream) throws IOException {
		return SerializationUtil.deserializeGZIPCompressed(istream);
	}

}
