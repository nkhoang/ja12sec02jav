package com.nkhoang.lucene;

import com.nkhoang.common.lucene.analysis.LowerCaseAnalyzer;
import com.nkhoang.common.lucene.analysis.NicknameAnalyzer;
import com.nkhoang.common.lucene.constants.LuceneSearchFields;
import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Word;
import com.thoughtworks.xstream.core.util.Fields;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LuceneWriterUtil {
	private static IndexWriter _wordIndexWriter;

	public static void openWordIndexWriter(String luceneDir) throws IOException {
		if (_wordIndexWriter == null) {
			synchronized (LuceneWriterUtil.class) {
				File indexDir = new File(luceneDir);
				// NIOFS is supposed to be faster on unix based system.
				Directory dir = NIOFSDirectory.open(indexDir);
				IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_31, getAnalyzer());

				_wordIndexWriter = new IndexWriter(dir, indexConfig);
			}
		}
	}

	/** Method expose to use. */
	public static IndexWriter getWordIndexWriter() {
		return _wordIndexWriter;
	}

	/**
	 * Close the writer.
	 *
	 * @throws IOException
	 */
	public static void closeWordIndexWriter() throws IOException {
		if (_wordIndexWriter != null) {
			synchronized (LuceneWriterUtil.class) {
				if (_wordIndexWriter != null) {
					try {
						_wordIndexWriter.commit();
						_wordIndexWriter.optimize();
					}
					finally {
						_wordIndexWriter.close();
						_wordIndexWriter = null;
					}
				}
			}
		}
	}

	/**
	 * Write Word to the index.
	 *
	 * @param w word to be proccessed.
	 *
	 * @throws LuceneWriterNotOpenedException index not open.
	 */
	public static void writeWordToIndex(
		Word w) throws LuceneWriterNotOpenedException, CorruptIndexException, IOException {
		IndexWriter writer = LuceneWriterUtil.getWordIndexWriter();

		if (writer == null) {
			throw new LuceneWriterNotOpenedException(
				"Please open the Index Writer by calling openIndexWriter() before trying to write to it.");
		}

		// write the word id as the main key.
		Term key = new Term(LuceneSearchFields.WORD_ID, w.getId().toString());

		Document doc = new Document();
		// id field.
		doc.add(new Field(LuceneSearchFields.WORD_ID, w.getId().toString(), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(
			new Field(
				LuceneSearchFields.WORD_DESCRIPTION, w.getDescription().toLowerCase(), Field.Store.YES,
				Field.Index.ANALYZED));
		// loop through meanings to add the meaning to the index.
		for (Map.Entry<Long, List<Meaning>> meanings : w.getMeanings().entrySet()) {
			for (Meaning m : meanings.getValue()) {
				if (StringUtils.isNotBlank(m.getContent())) {
					doc.add(
						new Field(
							LuceneSearchFields.WORD_MEANING, m.getContent(), Field.Store.NO, Field.Index.ANALYZED));
				}
			}
		}

		writer.updateDocument(key, doc);
	}

	public static void deleteWordFromIndex(
		String id) throws LuceneWriterNotOpenedException, CorruptIndexException, IOException {
		IndexWriter writer = LuceneWriterUtil.getWordIndexWriter();
		if (writer == null) {
			throw new LuceneWriterNotOpenedException(
				"Please open the Index Writer by calling openIndexWriter() " + "before trying to write to it.");
		}

		Term key = new Term(LuceneSearchFields.WORD_ID, id);
		writer.deleteDocuments(key);
	}

	private static Analyzer getAnalyzer() {
		// Default to LwerCaseAnalyzer and specify analyzers needed for any specific fields
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new LowerCaseAnalyzer());

		// optional
		//analyzer.addAnalyzer(LuceneSearchFields.WORD_DESCRIPTION, new NicknameAnalyzer());;
		return analyzer;
	}
}
