package com.nkhoang.search;

import com.nkhoang.model.Meaning;
import com.nkhoang.model.Sense;
import com.nkhoang.model.Word;
import com.nkhoang.search.analyzer.LowerCaseAnalyzer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class LuceneUtils {

  public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LuceneUtils.class);

  private static IndexSearcher _luceneIndexSearcher;
  private static IndexReader _luceneIndexReader;
  private static IndexWriter _luceneIndexWriter;

  private static final int FUZZY_PREFIX_LENGTH = 0;
  private static final float FUZZY_EDIT_DISTANCE = .75f;
  private static final int STARTS_WITH_MAX_LENGTH = 3;
  private static final float BOOST_TERM_QUERY = 1.25f;
  private static final float BOOST_FUZZY_QUERY = .75f;
  private static final float BOOST_WILDCARD_QUERY = .5f;
  private static final float BOOST_INVERSE_QUERY = .95f;

  public static final String WILDCARD = "*";

  private static final String INDEX_DIR = "gae/src/main/resources/com/nkhoang/search/index";

  public static IndexSearcher getLuceneSearcher() throws IOException {
    return getLuceneSearcher(INDEX_DIR);
  }

  /**
   * Get a Lucene index searcher.
   *
   * @param indexDirPath the path to the index directory.
   * @return the opened index searcher.
   * @throws IOException if the path to the index is wrong.
   */
  public static IndexSearcher getLuceneSearcher(String indexDirPath) throws IOException {
    synchronized (LuceneUtils.class) {
      if (_luceneIndexSearcher == null) {
        File indexDir = new File(indexDirPath);
        // work great with GAE.
        Directory dir = new SimpleFSDirectory(indexDir);
        _luceneIndexSearcher = new IndexSearcher(dir, true);
      }
    }
    return _luceneIndexSearcher;
  }

  /**
   * Get the index reader for Lucene index.
   *
   * @return the index reader.
   * @throws IOException if the path to the Lucene index is wrong.
   */
  public static IndexReader getIndexReader() throws IOException {
    synchronized (LuceneUtils.class) {
      if (_luceneIndexReader == null) {
        _luceneIndexReader = getLuceneSearcher().getIndexReader();
      }
    }

    return _luceneIndexReader;
  }


  /**
   * Close the opened index searcher.
   *
   * @throws IOException if the default path to the lucene index is wrong.
   */
  public static void closeSearcher() throws IOException {
    getLuceneSearcher().close();
  }



  /**
   * Get total docs saved into the index.
   *
   * @return the number of documents in the index.
   * @throws IOException if the path to the lucene index is wrong.
   */
  public static int getTotalDocs() throws IOException {
    return getLuceneSearcher().maxDoc();
  }


  /**
   * Open writer.
   *
   * @return Lucene writer.
   * @throws IOException may throw.
   */
  public static IndexWriter getLuceneWriter() throws IOException {
    synchronized (LuceneUtils.class) {
      if (_luceneIndexWriter == null) {
        File indexDir = new File(INDEX_DIR);
        //NIOFS is supposed to be faster on unix based system
        Directory dir = NIOFSDirectory.open(indexDir);
        //RAMDirectory dir = new RAMDirectory(NIOFSDirectory.open(indexDir));
        // Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_35);
        // Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_35);
        Analyzer analyzer = new LowerCaseAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_35, analyzer);
        _luceneIndexWriter = new IndexWriter(dir, indexWriterConfig);
      }
      _luceneIndexWriter.setInfoStream(System.out);
    }
    return _luceneIndexWriter;
  }

  /**
   * Close Lucene writer.
   *
   * @throws IOException may throw when cannot close writer.
   */
  public static void closeLuceneWriter() throws IOException {
    if (_luceneIndexWriter != null) {
      synchronized (LuceneUtils.class) {
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

  /**
   * Delete word from Lucene Index by the word id.
   *
   * @param id the word id.
   * @throws IOException if the default path to the index is wrong.
   */
  public static void deleteWordFromIndex(String id) throws IOException {
    IndexWriter writer = LuceneUtils.getLuceneWriter();
    if (writer == null) {
      throw new IOException("Could not open writer.");
    }

    // id is number so don't worry about character uppercase.
    Term key = new Term(LuceneSearchFields.ID, id.toLowerCase());
    writer.deleteDocuments(key);
  }

  /**
   * Write word to the index.
   * <br/>
   * <ul>
   * <li>ID: the word id is the only property stored in the index.</li>
   * <li>Description: the word description is analyzed and stored.</li>
   * <li>Meaning: the word meanings are analyzed but not stored.</li>
   * </ul>
   *
   * @param word the {@link Word} entity to be stored.
   * @throws IOException
   */
  public static void writeWordToIndex(Word word) throws IOException {
    IndexWriter writer = LuceneUtils.getLuceneWriter();
    if (writer == null) {
      throw new IOException("Could not get a proper IndexWriter");
    }
    // create term: is a id of lucene Document. term will later be added to document as key.
    Term key = new Term(LuceneSearchFields.ID, word.getDescription());
    Document document = new Document();
    // add id.
    document.add(new Field(LuceneSearchFields.ID, word.getDescription(), Field.Store.YES, Field.Index.ANALYZED));
    // add content
    if (CollectionUtils.isNotEmpty(word.getMeanings())) {
      for (Sense s : word.getMeanings()) {
        document.add(
            new Field(
                LuceneSearchFields.WORD_CONTENT,
                s.getDefinition().trim().toLowerCase(),
                Field.Store.NO,
                Field.Index.ANALYZED));
      }

    }

    // update Document
    writer.updateDocument(key, document);
  }

  private static BooleanClause createTermClause(String field, String term, BooleanClause.Occur occur) {
    Query query = new TermQuery(new Term(field, term));
    query.setBoost(BOOST_TERM_QUERY);
    return new BooleanClause(query, occur);
  }


  private static BooleanClause createFuzzyClause(String field, String term, BooleanClause.Occur occur) {
    Query query = new FuzzyQuery(new Term(field, term), FUZZY_EDIT_DISTANCE, FUZZY_PREFIX_LENGTH);
    query.setBoost(BOOST_FUZZY_QUERY);
    return new BooleanClause(query, occur);
  }


  private static BooleanClause createWildcardClause(String field, String term, BooleanClause.Occur occur) {
    if (term.length() > STARTS_WITH_MAX_LENGTH) {
      return createTermClause(field, term, occur);
    }

    Query query = new WildcardQuery(new Term(field, term + WILDCARD));
    query.setBoost(BOOST_WILDCARD_QUERY);
    return new BooleanClause(query, occur);
  }

}