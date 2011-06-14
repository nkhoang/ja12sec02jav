package com.nkhoang.common.util.serial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;

/** Utility methods for serialization. */
public class SerializationUtil {

	private SerializationUtil() {
	}

	/**
	 * Serializes and optionally GZIP compresses the given object to a byte
	 * array.
	 *
	 * @return the serialized, possibly compressed bytes
	 */
	public static byte[] serialize(Serializable obj, boolean gzipCompress) throws IOException {
		return (gzipCompress ? serializeGZIPCompressed(obj) : SerializationUtils.serialize(obj));
	}

	/**
	 * Serializes and GZIP compresses the given object to a byte array.
	 *
	 * @return the serialized, compressed bytes
	 */
	public static byte[] serializeGZIPCompressed(Serializable obj) throws IOException {
		// the commons io byte stream impl is designed for better performace with
		// larger amounts of data.  note, ostream is closed by this method
		return serializeGZIPCompressed(
			obj, new org.apache.commons.io.output.ByteArrayOutputStream()).toByteArray();
	}

	/**
	 * Serializes and GZIP compresses the given stream.  The given stream will
	 * be closed once the object is written.
	 *
	 * @return the given stream
	 */
	public static <OStream extends OutputStream> OStream serializeGZIPCompressed(
		Serializable obj, OStream ostream) throws IOException {
		// ostream is closed by this method
		SerializationUtils.serialize(obj, new GZIPOutputStream(ostream));
		return ostream;
	}

	/**
	 * Deserializes an object from the given, possibly GZIP compressed bytes.
	 *
	 * @return the deserialized object
	 */
	public static Object deserialize(byte[] bytes, boolean gzipCompressed) throws IOException {
		return (gzipCompressed ? deserializeGZIPCompressed(bytes) : SerializationUtils.deserialize(bytes));
	}

	/**
	 * Deserializes an object from the given GZIP compressed bytes.
	 *
	 * @return the deserialized object
	 */
	public static Object deserializeGZIPCompressed(byte[] bytes) throws IOException {
		// istream is closed by this method
		return deserializeGZIPCompressed(new ByteArrayInputStream(bytes));
	}

	/**
	 * Deserializes an object from the given GZIP compressed stream.  The given
	 * stream will be closed once the object is read.
	 *
	 * @return the deserialized object
	 */
	public static Object deserializeGZIPCompressed(InputStream istream) throws IOException {
		// istream is closed by this method
		return SerializationUtils.deserialize(new GZIPInputStream(istream));
	}

	/**
	 * @return the result of a call to readObject on the given stream.  wraps
	 *         any ClassNotFoundExceptions as IOExceptions
	 */
	public static Object readObject(ObjectInput in) throws IOException {
		try {
			return in.readObject();
		}
		catch (ClassNotFoundException e) {
			throw (IOException) (new IOException(e.getMessage())).initCause(e);
		}
	}

	/**
	 * Utility code for writing a non-{@code null} collection to an ObjectOutput
	 * stream, where the caller does not need to record the type of the
	 * collection.  Writes an int size of the collection and then each element
	 * of said collection.
	 */
	public static void writeCollection(ObjectOutput out, Collection<?> col) throws IOException {
		out.writeInt(col.size());
		for (Object o : col) {
			out.writeObject(o);
		}
	}

	/**
	 * Utility code for reading a collection from an ObjectInput stream
	 * previously written by {@link #writeCollection}.  The deserialized
	 * elements will be added to the given collection.
	 *
	 * @return the given collection
	 */
	public static <T, CType extends Collection<? super T>> CType readCollection(
		ObjectInput in, CType col, Class<T> type) throws IOException {
		int size = in.readInt();
		if (size > 0) {
			// pre-size ArrayLists
			if (col instanceof ArrayList) {
				((ArrayList<?>) col).ensureCapacity(size);
			}

			for (int i = 0; i < size; ++i) {
				col.add(type.cast(readObject(in)));
			}
		}

		return col;
	}

	/**
	 * Utility code for writing a non-{@code null} map to an ObjectOutput
	 * stream, where the caller does not need to record the type of the map.
	 * Writes an int size of the collection and then each key/value pair of said
	 * map.
	 */
	public static void writeMap(ObjectOutput out, Map<?, ?> map) throws IOException {
		out.writeInt(map.size());
		for (Map.Entry<?, ?> e : map.entrySet()) {
			out.writeObject(e.getKey());
			out.writeObject(e.getValue());
		}
	}

	/**
	 * Utility code for reading a map from an ObjectInput stream previously
	 * written by {@link #writeMap}.  The deserialized key/value pairs will be
	 * added to the given map.
	 *
	 * @return the given map
	 */
	public static <K, V, MType extends Map<? super K, ? super V>> MType readMap(
		ObjectInput in, MType map, Class<K> keyType, Class<V> valueType) throws IOException {
		int size = in.readInt();
		if (size > 0) {
			for (int i = 0; i < size; ++i) {
				K key = keyType.cast(readObject(in));
				V value = valueType.cast(readObject(in));
				map.put(key, value);
			}
		}

		return map;
	}

}
