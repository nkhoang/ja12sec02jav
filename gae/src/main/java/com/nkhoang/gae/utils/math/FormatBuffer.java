package com.nkhoang.gae.utils.math;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FormatBuffer extends FastStringBuffer {

	/*---------------------------------------------------------------------------
		Size constants
	  ---------------------------------------------------------------------------*/

	public static final int DefaultBufferSize = 2048;
	public static final int ReportThreshold   = 50000;

	/*---------------------------------------------------------------------------
		Constructors
	  ---------------------------------------------------------------------------*/

	/**
	 * Make a FormatBuffer.  Allocate the underlying string buffer to
	 * a default size.
	 */
	public FormatBuffer() {
		this(DefaultBufferSize);
	}

	/**
	 * Make a FormatBuffer.  Allocate the underlying string buffer to
	 * a specified size.
	 *
	 * @param size the number of characters the FormatBuffer can hold
	 *             without growing.
	 */
	public FormatBuffer(int size) {
		super(size);
		setDoublesCapacityWhenGrowing(true);
	}

	/*---------------------------------------------------------------------------
		Methods
	  ---------------------------------------------------------------------------*/

	/**
	 * Append a character to the FormatBuffer.
	 *
	 * @param c the character to append
	 */
	public void append(char c) {
		super.append(c);
	}

	/**
	 * Append an integer to the FormatBuffer.
	 *
	 * @param i the integer to append
	 */
	public void append(int i) {
		append(i, 10);
	}

	/**
	 * Append an integer in a radix format to the FormatBuffer.
	 *
	 * @param i     the integer to append
	 * @param radix the radix to use for encoding the integer
	 *              <b>i</b>. The radix must be between <b>Character.MIN_RADIX</b>
	 *              and <b>Character.MAX_RADIX</b>
	 *
	 * @see java.lang.Character#MAX_RADIX
	 * @see java.lang.Character#MIN_RADIX
	 */
	public void append(int i, int radix) {
		makeRoomFor(radix >= 8 ? 12 : 33);

		if (i < 0) {
			buffer[length++] = '-';
		} else {
			i = -i;
		}

		int front = length;

		while (i <= -radix) {
			buffer[length++] = Character.forDigit(-(i % radix), radix);
			i = i / radix;
		}

		buffer[length++] = Character.forDigit(-i, radix);

		// reverse answer
		int back = length - 1;
		while (front < back) {
			char temp = buffer[front];
			buffer[front] = buffer[back];
			buffer[back] = temp;
			front++;
			back--;
		}
	}

	/**
	 * Append a long to the FormatBuffer.
	 *
	 * @param l the long to append
	 */
	public void append(long l) {
		if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
			append((int) l, 10);
		} else {
			append(Long.toString(l));
		}
	}

	/**
	 * Append a double to the FormatBuffer.
	 *
	 * @param d the double to append
	 */
	public void append(double d) {
		append(Double.toString(d));
	}

	/**
	 * Append a one FormatBuffer to another FormatBuffer. This method
	 * is faster than converting the second FormatBuffer buffer to a
	 * string first.
	 *
	 * @param that a FormatBuffer to append to this FormatBuffer
	 */
	public void append(FormatBuffer that) {
		int len = that.length;
		makeRoomFor(len);
		for (int i = 0; i < len; i++) {
			this.buffer[length++] = that.buffer[i];
		}
	}

	/*---------------------------------------------------------------------------
		Supporting old interface
	  ---------------------------------------------------------------------------*/


	/** Return the number of bytes used in buffer. */
	public int size() {
		return length;
	}

	/**
	 * Determine if the FormatBuffer is empty.
	 *
	 * @return <b>true</b> if the FormatBuffer is empty, <b>false</b>
	 *         otherwise
	 */
	public boolean isEmpty() {
		return length == 0;
	}

	/**
	 * Return the buffer holding the state of this FormatBuffer.  The
	 * effect on the returned array of operations performed on this
	 * FormatBuffer is not defined.
	 */
	public char[] getBuffer() {
		return buffer;
	}

	/*-----------------------------------------------------------------------
			output
		  -----------------------------------------------------------------------*/

	/**
	 * Write the contents of this FormatBuffer to a PrintWriter.
	 *
	 * @param out the PrintWriter to write the output into.
	 */
	public void print(PrintWriter out) {
		out.write(buffer, 0, length);
	}

	/**
	 * Write the contents of this FormatBuffer to an OutputStream.
	 *
	 * @param out the OutputStream to write the output into.
	 *
	 * @throws IOException if an error occurs in writing to
	 *                     <b>out</b>
	 */
	public void print(OutputStream out) throws IOException {
		// Originally for PrintStreams, changed it to be for
		// Streams in general. The algorithm is just as inefficent
		// as PrintStream was so don't feel like you are missing
		// out on performance or anything.
		for (int i = 0; i < length; i++) {
			out.write(buffer[i]);
		}
	}

	/*-----------------------------------------------------------------------
			resize
		  -----------------------------------------------------------------------*/

	void setCapacity(int newCapacity) {
		super.setCapacity(newCapacity);
		if (newCapacity > ReportThreshold) {
			/*Log.util.log(
							Level.WARNING,
							"Grew FormatBuffer to {0}",
							Constants.getInteger(newCapacity));*/
		}
	}
}
