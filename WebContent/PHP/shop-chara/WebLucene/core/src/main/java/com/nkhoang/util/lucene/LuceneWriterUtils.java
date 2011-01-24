package com.nkhoang.util.lucene;

import com.nkhoang.constant.LuceneSearchField;
import com.nkhoang.model.Word;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.compass.core.lucene.engine.manager.LuceneIndexHolder;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 1/24/11
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class LuceneWriterUtils {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LuceneWriterUtils.class);

    private static IndexWriter _luceneIndexWriter;
    private static final String INDEX_DIR = "D:/lucene/index";

    /**
     * Open writer.
     * @return Lucene writer.
     * @throws IOException may throw.
     */
    public static IndexWriter openLuceneWriter() throws IOException {
        synchronized (LuceneWriterUtils.class) {
            if (_luceneIndexWriter == null) {
                File indexDir = new File(INDEX_DIR);
                //NIOFS is supposed to be faster on unix based system
                Directory dir = NIOFSDirectory.open(indexDir);
                //RAMDirectory dir = new RAMDirectory(NIOFSDirectory.open(indexDir));
                Analyzer analyzer = new WhitespaceAnalyzer();
                _luceneIndexWriter = new IndexWriter(dir, analyzer,
                        IndexWriter.MaxFieldLength.UNLIMITED);
            }
            _luceneIndexWriter.setInfoStream(System.out);
        }
        return _luceneIndexWriter;
    }

    /**
     * Close Lucene writer.
     * @throws IOException may throw when cannot close writer.
     */
    public static void closeLuceneWriter() throws IOException{
        if (_luceneIndexWriter != null) {
            synchronized (LuceneWriterUtils.class) {
                try {
                    _luceneIndexWriter.commit();
                    _luceneIndexWriter.optimize();
                } finally {
                    _luceneIndexWriter.close();
                    _luceneIndexWriter = null;
                }
            }
        }
    }

    public static void deleteWordFromIndex(String id) throws IOException{
        IndexWriter writer = LuceneWriterUtils.openLuceneWriter();
        if (writer == null) {
            throw new IOException("Could not open writer.");
        }

        // id is number so don't worry about character uppercase.
        Term key = new Term(LuceneSearchField.ID, id.toLowerCase());
        writer.deleteDocuments(key);
    }

    public static void writeWordToIndex(Word word) throws IOException{
        IndexWriter writer = LuceneWriterUtils.openLuceneWriter();
        if (writer == null) {
            throw new IOException("Could not get a proper IndexWriter");
        }
        // create term: is a id of lucene Document. term will later be added to document as key.
        Term key = new Term(LuceneSearchField.ID, word.getWordId() + "");

        Document document = new Document();

        // add id.
        document.add(new Field(LuceneSearchField.ID, word.getWordId().toString(), Field.Store.YES, Field.Index.ANALYZED));
        // add description
        document.add(new Field(LuceneSearchField.DESCRIPTION, word.getDescription(), Field.Store.NO, Field.Index.ANALYZED));
        // add content
        document.add(new Field(LuceneSearchField.CONTENT, word.getContent(), Field.Store.NO, Field.Index.ANALYZED));
        // update Document
        writer.updateDocument(key, document);
    }

}