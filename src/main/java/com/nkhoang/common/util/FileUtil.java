package com.nkhoang.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Collection of file utility functions.
 *
 * @see Jakarta's Commons IO:  http://jakarta.apache.org/commons/io/api-release/index.html
 */
public class FileUtil {
	private static final Log LOG = LogFactory.getLog(FileUtil.class);

	private static final String TMPFILE_PFX = "futil.";
	private static final String TMPFILE_SFX = "dat";

	/**
	 * You're not supposed to construct one, only use the static
	 * methods...
	 */
	protected FileUtil() {
	}

	/**
	 * Append String buff to File fileName.
	 *
	 * @param out  the File to append to
	 * @param buff the string to append to the file
	 *
	 * @throws java.io.FileNotFoundException if the file exists but is a directory
	 *                                       rather than a regular file, does not exist but cannot be created, or
	 *                                       cannot be opened for any other reason.
	 * @throws java.io.IOException           if an IO error occurs.
	 */
	public static void appendToFile(File out, String buff) throws FileNotFoundException, IOException {
		appendToFile(out, buff.getBytes());
	}

	/**
	 * Append String buff to file named by fileName.
	 *
	 * @param fileName the name of the file to append to
	 * @param buff     the string to append to the file
	 *
	 * @throws java.io.FileNotFoundException if the file exists but is a directory
	 *                                       rather than a regular file, does not exist but cannot be created, or
	 *                                       cannot be opened for any other reason.
	 * @throws java.io.IOException           if an IO error occurs.
	 */
	public static void appendToFile(String fileName, String buff) throws FileNotFoundException, IOException {
		appendToFile(fileName, buff.getBytes());
	}

	/**
	 * Append bytes to the file named by fileName.
	 *
	 * @param fileName the name of the file to append to
	 * @param bytes    the array of bytes to append to the file
	 *
	 * @throws java.io.FileNotFoundException if the file exists but is a directory
	 *                                       rather than a regular file, does not exist but cannot be created, or
	 *                                       cannot be opened for any other reason
	 * @throws java.io.IOException           if an IO error occurs.
	 */
	public static void appendToFile(String fileName, byte[] bytes) throws FileNotFoundException, IOException {
		appendToFile(new File(fileName), bytes);
	}

	/**
	 * Append bytes to File fileName.
	 *
	 * @param out   the File to append to
	 * @param bytes the array of bytes to append to the file
	 *
	 * @throws java.io.FileNotFoundException if the file exists but is a directory
	 *                                       rather than a regular file, does not exist but cannot be created, or
	 *                                       cannot be opened for any other reason
	 * @throws java.io.IOException           if an IO error occurs.
	 */
	public static void appendToFile(File out, byte[] bytes) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(out, true /* append? */);
		try {
			fos.write(bytes);
		}
		finally {
			fos.close();
		}
	}


	/**
	 * Creates a temporary file, with content from a URL. This is useful
	 * for testing, where the test content is available as a resource but
	 * you are testing something that needs a file.
	 */
	public static File createTempFile(URL source) throws IOException {
		File file = File.createTempFile("test", null);
		file.deleteOnExit();
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = source.openStream();
			out = new FileOutputStream(file);
			IOUtils.copy(in, out);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
		return file;
	}


	/**
	 * Incredibly overprotective filename escaper.  It allows numbers,
	 * letters, dashes, underscores, and periods.  Everything else is
	 * truncated out. These seems to be common characters suppored by
	 * all operating systems.
	 * <p/>
	 * Note: it does not allow unix or windows style slashes.  This
	 * method is meant to accept a filename, not a full path name.
	 *
	 * @return a string that replaces all non-safe filename characters with _s.
	 */
	public static String createVerySafeFileName(String fileName) {
		if (fileName == null) {
			return null;
		}
		return fileName.replaceAll("[^\\w\\.-]", "_");
	}


	/**
	 * Maintain the life-cycle of a FileReader over a given resource.
	 * Creates the FileReader using the platform default charset.
	 */
	public static <T> T withFileReader(ThrowingFunctor<T, FileReader, IOException> func, File file) throws IOException {
		FileReader fr = null;
		try {
			return func.execute(fr = new FileReader(file));
		}
		finally {
			IOUtil.closeQuietly(fr);
		}
	}

	/** Override for fileName as a string. */
	public static <T> T withFileReader(
		ThrowingFunctor<T, FileReader, IOException> func, String fileName) throws IOException {
		return withFileReader(func, new File(fileName));
	}

	/** Map a function over the lines in a file. */
	public static void foreachLine(
		final ThrowingFunctor<Boolean, String, IOException> func, File file) throws IOException {
		withFileReader(
			new ThrowingFunctor<Boolean, FileReader, IOException>() {
				public Boolean execute(FileReader fr) throws IOException {
					BufferedReader reader = new BufferedReader(fr);
					String line = null;
					while (null != (line = reader.readLine())) {
						func.execute(line);
					}
					return true;
				}
			}, file);
	}

	/**
	 * Find all the lines matching a pattern in a file.
	 *
	 * @param fileName the file name to search
	 * @param pattern  the pattern to search for
	 * @param flags    regex flags to pass on to java.util.regex.Pattern.compile()
	 *
	 * @return the array of lines
	 *
	 * @see java.util.regex.Pattern
	 */
	public static List<String> grep(String fileName, String pattern, int flags) throws IOException {
		return grep(fileName, Pattern.compile(pattern, flags));
	}

	/**
	 * Find all the lines matching a pattern in a file.
	 *
	 * @param fileName the file name to search
	 * @param pattern  the pattern to search for
	 *
	 * @return the array of lines
	 *
	 * @see java.util.regex.Pattern
	 */
	public static List<String> grep(String fileName, String pattern) throws IOException {
		return grep(fileName, Pattern.compile(pattern));

	}

	/**
	 * Grep a file for a pattern.
	 *
	 * @param fileName the file to search
	 * @param pattern  the pattern to search for
	 *
	 * @return true/false
	 *
	 * @see java.util.regex.Pattern
	 */
	public static boolean grepMatches(String fileName, final String pattern) throws IOException {
		return withFileReader(
			new ThrowingFunctor<Boolean, FileReader, IOException>() {
				public Boolean execute(FileReader fr) throws IOException {
					BufferedReader reader = new BufferedReader(fr);
					Pattern pat = Pattern.compile(pattern);
					String line = null;
					while (null != (line = reader.readLine())) {
						if (pat.matcher(line).find()) {
							return true;
						}
					}
					return false;
				}
			}, fileName);
	}

	/**
	 * Find all the lines matching a pattern in a file.
	 *
	 * @param fileName the file to search
	 * @param pat      the pattern to search for
	 *
	 * @return the array of lines
	 *
	 * @see java.util.regex.Pattern
	 */
	public static List<String> grep(String fileName, final Pattern pat) throws IOException {
		final List<String> results = new ArrayList<String>();
		foreachLine(
			new ThrowingFunctor<Boolean, String, IOException>() {
				public Boolean execute(String line) {
					if (pat.matcher(line).find()) {
						results.add(line);
					}
					return true;
				}
			}, new File(fileName));

		return results;
	}

	/**
	 * Grep a file for series of patterns, return true for the first pattern
	 * that succeeds, otherwise return false.
	 *
	 * @param fileName
	 * @param patterns
	 *
	 * @return true/false
	 *
	 * @see java.util.regex.Pattern
	 */
	public static boolean grepMatches(String fileName, String... patterns) throws IOException {
		for (String pat : patterns) {
			if (grepMatches(fileName, pat)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes the given file, handling <code>null</code> values.  Swallows any
	 * exception from deletion attempt (logs a warning).
	 */
	public static boolean deleteQuietly(File file) {
		try {
			return null == file ? false : file.delete();
		}
		catch (SecurityException e) {
			LogUtil.warnDebug(LOG, "Failed deleting file", e);
		}
		return false;
	}

	/**
	 * Read the first numLines from the file.
	 *
	 * @param numLines the number of lines to read
	 * @param file     the file to read from
	 */
	public static List<String> head(Reader reader, int numLines) throws IOException {
		BufferedReader rdr = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;
		while (null != (line = rdr.readLine()) && numLines-- > 0) {
			lines.add(line);
		}

		return lines;
	}

	/** Read the first numLines from a file. */
	public static List<String> head(File file, final int numLines) throws IOException {
		return withFileReader(
			new ThrowingFunctor<List<String>, FileReader, IOException>() {
				public List<String> execute(FileReader fr) throws IOException {
					return head(fr, numLines);
				}
			}, file);
	}


	/** Read the first numLines from the file. */
	public static List<String> head(String file, int numLines) throws IOException {
		return head(new File(file), numLines);
	}

	/** Read the first line from a file, returns null if the file is empty. */
	public static String head1(File file) throws IOException {
		List<String> lines = head(file, 1);
		return lines.size() > 0 ? lines.get(0) : null;
	}

	/** Read the first line from a file, returns null if the file is empty. */
	public static String head1(String file) throws IOException {
		return head1(new File(file));
	}


	/**
	 * Execute a functor with a temporary file name.  Once the functor
	 * has completed, delete the file if it still exists.  Return the
	 * returned value from executing the functor.
	 *
	 * @param tfp the throwing functor that will be called to process the file
	 */
	public static <T> T withTempFile(ThrowingFunctor<T, File, IOException> tfp) throws IOException {
		File tmpfile = File.createTempFile(TMPFILE_PFX, TMPFILE_SFX);
		try {
			return tfp.execute(tmpfile);
		}
		finally {
			FileUtil.deleteQuietly(tmpfile);
		}
	}

	/**
	 * Like Jakarta IO FileUtils, but uses the default nio.charset
	 * encoding as a default.
	 *
	 * @param f the file that will be written to
	 * @param d the data to write to the file
	 */
	public static void writeStringToFile(File f, String d) throws IOException {
		FileUtils.writeStringToFile(f, d, Charset.defaultCharset().name());
	}

	/**
	 * Similar to {@link java.io.File#createTempFile}, except that it creates a
	 * temporary directory instead of a temporary file.
	 */
	public static File createTempDir(String prefix) throws IOException {
		return createTempDir(prefix, "");
	}

	/**
	 * Similar to {@link java.io.File#createTempFile}, except that it creates a
	 * temporary directory instead of a temporary file.
	 */
	public static File createTempDir(String prefix, String suffix) throws IOException {
		return createTempDir(prefix, suffix, null);
	}

	/**
	 * Similar to {@link java.io.File#createTempFile}, except that it creates a
	 * temporary directory instead of a temporary file.
	 */
	public static File createTempDir(
		String prefix, String suffix, File parentDirectory) throws IOException {
		File tempDir = File.createTempFile(prefix, suffix, parentDirectory);
		tempDir.delete();
		tempDir.mkdir();
		return tempDir;
	}

	/**
	 * Based on {@link org.apache.commons.io.FileUtils#listFiles(java.io.File, org.apache.commons.io.filefilter.IOFileFilter, org.apache.commons.io.filefilter.IOFileFilter)}
	 * but allows the "files" you are seeking to be directories.
	 */
	public static Collection<File> listFilesIncludingDirectories(
		File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
				"Parameter 'directory' is not a directory");
		}
		if (fileFilter == null) {
			throw new NullPointerException("Parameter 'fileFilter' is null");
		}

		//Setup effective directory filter
		IOFileFilter effDirFilter;
		if (dirFilter == null) {
			effDirFilter = FalseFileFilter.INSTANCE;
		} else {
			effDirFilter = FileFilterUtils.andFileFilter(
				dirFilter, DirectoryFileFilter.INSTANCE);
		}

		//Find files
		Collection<File> files = new LinkedList<File>();
		innerListFilesIncludingDirectories(files, directory, fileFilter, effDirFilter);
		return files;
	}

	private static void innerListFilesIncludingDirectories(
		Collection<File> files, File directory, IOFileFilter filter, IOFileFilter subDirFilter) {
		IOFileFilter orFilter = FileFilterUtils.orFileFilter(filter, subDirFilter);
		File[] found = directory.listFiles((FileFilter) orFilter);
		if (found != null) {
			for (int i = 0; i < found.length; i++) {
				if (found[i].isDirectory()) {
					innerListFilesIncludingDirectories(files, found[i], filter, subDirFilter);
					if (filter.accept(found[i])) {
						files.add(found[i]);
					}
				} else {
					files.add(found[i]);
				}
			}
		}
	}

	public static int countLines(File file) throws IOException {
		final int[] numLines = new int[1];
		foreachLine(
			new ThrowingFunctor<Boolean, String, IOException>() {
				public Boolean execute(String arg) throws IOException {
					numLines[0]++;
					return null;
				}
			}, file);
		return numLines[0];
	}

	public static boolean isDirectoryWritable(File directory, String tempFileName) {
		boolean isWritable = false;

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
				"There is no directory at " + directory);
		}

		File testFile = new File(directory, tempFileName);
		testFile.delete();
		try {
			testFile.createNewFile();
			isWritable = true;
		}
		catch (IOException e) {
		}
		testFile.delete();
		return isWritable;
	}

	public static boolean isDirectoryWritable(File directory) {

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
				"There is no directory at " + directory);
		}

		String fileName = "checkWriteAccess_";
		Random random = new Random();
		Boolean isWritable = null;

		for (int n = 0, count = 0; count < 1000; n = random.nextInt(), count++) {
			File testFile = new File(directory, fileName + n);
			if (testFile.exists()) {
				continue;
			} else {
				isWritable = isDirectoryWritable(directory, testFile.getName());
				break;
			}
		}

		if (isWritable == null) {
			throw new RuntimeException(
				"Unable to determine if the directory is " +
				"writable. Could not find a file name that didn't exist in the directory.");
		} else {
			return isWritable;
		}
	}
}
