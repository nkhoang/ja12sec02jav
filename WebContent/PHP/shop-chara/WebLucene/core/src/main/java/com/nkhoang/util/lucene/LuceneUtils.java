package com.nkhoang.util.lucene;

import com.nkhoang.constant.LuceneSearchField;
import com.nkhoang.model.Word;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.LoggerFactory;

import javax.swing.text.StyledEditorKit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 1/24/11
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class LuceneUtils {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LuceneUtils.class);

    private static IndexSearcher _luceneIndexSearcher;
    private static IndexWriter _luceneIndexWriter;

    private static final int FUZZY_PREFIX_LENGTH = 0;
    private static final float FUZZY_EDIT_DISTANCE = .75f;
    private static final int STARTS_WITH_MAX_LENGTH = 3;
    private static final float BOOST_TERM_QUERY = 1.25f;
    private static final float BOOST_FUZZY_QUERY = .75f;
    private static final float BOOST_WILDCARD_QUERY = .5f;
    private static final float BOOST_INVERSE_QUERY = .95f;

    public static final String WILDCARD = "*";

    private static final String INDEX_DIR = "D:/lucene/index";
    private static final int DEFAULT_MAXIMUM_SEARCH_RESULTS = 100;

    public static IndexSearcher getLuceneSearcher() throws IOException {
        synchronized (LuceneUtils.class) {
            if (_luceneIndexSearcher == null) {
                File indexDir = new File(INDEX_DIR);
                //NIOFS is supposed to be faster on unix based system
                Directory dir = NIOFSDirectory.open(indexDir);
                //RAMDirectory dir = new RAMDirectory(NIOFSDirectory.open(indexDir));
                _luceneIndexSearcher = new IndexSearcher(dir, true);
            }
        }
        return _luceneIndexSearcher;
    }


    /**
     * perform search with query specified.
     *
     * @param query    query used to search.
     * @param searcher IndexSearcher instance.
     * @return list of found items.
     * @throws IOException may throw.
     */
    public static List<String> performSearch(Query query, IndexSearcher searcher) throws IOException {
        List<String> ids = new ArrayList<String>();
        long start = System.currentTimeMillis();
        int maxDoc = DEFAULT_MAXIMUM_SEARCH_RESULTS;
        // perform search.
        TopDocs searchResults = searcher.search(query, maxDoc);
        ScoreDoc[] hits = searchResults.scoreDocs;

        LOGGER.info("Total hits: " + searchResults.totalHits);
        // don't need to limit the output because the maximum search result is set.
        for (ScoreDoc hit : hits) {
            Document found = searcher.doc(hit.doc);
            ids.add(found.get(LuceneSearchField.ID));
        }

        return ids;
    }

    public static Query buildQuery(Word w) {
        BooleanQuery query = new BooleanQuery();
        // buiil query base on 2 main fields.
        if (StringUtils.isNotBlank(w.getDescription())) {
            BooleanQuery descriptionQuery = new BooleanQuery();
            String term = w.getDescription();
            descriptionQuery.add(createFuzzyClause(LuceneSearchField.DESCRIPTION, term, BooleanClause.Occur.SHOULD));
            descriptionQuery.add(createTermClause(LuceneSearchField.DESCRIPTION, term, BooleanClause.Occur.SHOULD));
            descriptionQuery.add(createWildcardClause(LuceneSearchField.DESCRIPTION, term, BooleanClause.Occur.SHOULD));

            query.add(descriptionQuery, BooleanClause.Occur.SHOULD);
        }
        if (StringUtils.isNotBlank(w.getContent())) {
            BooleanQuery contentQuery = new BooleanQuery();
            String term = w.getContent();

            contentQuery.add(createFuzzyClause(LuceneSearchField.CONTENT, term, BooleanClause.Occur.SHOULD));
            contentQuery.add(createTermClause(LuceneSearchField.CONTENT, term, BooleanClause.Occur.SHOULD));
            contentQuery.add(createWildcardClause(LuceneSearchField.CONTENT, term, BooleanClause.Occur.SHOULD));
            query.add(contentQuery, BooleanClause.Occur.SHOULD);
        }
        return query;
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

    public static void deleteWordFromIndex(String id) throws IOException {
        IndexWriter writer = LuceneUtils.getLuceneWriter();
        if (writer == null) {
            throw new IOException("Could not open writer.");
        }

        // id is number so don't worry about character uppercase.
        Term key = new Term(LuceneSearchField.ID, id.toLowerCase());
        writer.deleteDocuments(key);
    }

    public static void writeWordToIndex(Word word) throws IOException {
        IndexWriter writer = LuceneUtils.getLuceneWriter();
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