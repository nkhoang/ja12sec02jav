package com.nkhoang.search;

import generated.Meaning;
import generated.Word;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneUtils {

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LuceneUtils.class);

	private static IndexSearcher _luceneIndexSearcher;
	private static IndexWriter   _luceneIndexWriter;

	private static final int   FUZZY_PREFIX_LENGTH    = 0;
	private static final float FUZZY_EDIT_DISTANCE    = .75f;
	private static final int   STARTS_WITH_MAX_LENGTH = 3;
	private static final float BOOST_TERM_QUERY       = 1.25f;
	private static final float BOOST_FUZZY_QUERY      = .75f;
	private static final float BOOST_WILDCARD_QUERY   = .5f;
	private static final float BOOST_INVERSE_QUERY    = .95f;

	public static final String WILDCARD = "*";

	private static final String INDEX_DIR                      = "D:/lucene/index";
	private static final int    DEFAULT_MAXIMUM_SEARCH_RESULTS = 100;

	public static IndexSearcher getLuceneSearcher() throws IOException {
		synchronized (LuceneUtils.class) {
			if (_luceneIndexSearcher == null) {
				File indexDir = new File(INDEX_DIR);
				//NIOFS is supposed to be faster on unix based system
				Directory dir = FSDirectory.open(indexDir);
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
	 *
	 * @return list of found items.
	 *
	 * @throws IOException may throw.
	 */
	public static List<String> performSearch(Query query, IndexSearcher searcher) throws IOException {
		List<String> ids = new ArrayList<String>();
		long start = System.currentTimeMillis();
		int maxDoc = DEFAULT_MAXIMUM_SEARCH_RESULTS;
		// perform search.
		TopDocs searchResults = searcher.search(query, maxDoc);
		LOGGER.info("Total hits: " + searchResults.totalHits);
		ScoreDoc[] hits = searchResults.scoreDocs;


		// don't need to limit the output because the maximum search result is set.
		for (ScoreDoc hit : hits) {
			Document found = searcher.doc(hit.doc);
			ids.add(found.get(LuceneSearchFields.ID));
		}

		return ids;
	}

	public static Query buildQuery(Word w) throws Exception {

		BooleanQuery query = new BooleanQuery();
		// buiil query base on 2 main fields.
		if (StringUtils.isNotBlank(w.getDescription())) {
			QueryParser parser = new QueryParser(
				Version.LUCENE_34, LuceneSearchFields.WORD_DESCRIPTION, new StandardAnalyzer(Version.LUCENE_34));
			BooleanQuery descriptionQuery = new BooleanQuery();
			String term = w.getDescription();
			descriptionQuery
				.add(createFuzzyClause(LuceneSearchFields.WORD_DESCRIPTION, term, BooleanClause.Occur.SHOULD));
			descriptionQuery
				.add(createTermClause(LuceneSearchFields.WORD_DESCRIPTION, term, BooleanClause.Occur.SHOULD));
			descriptionQuery
				.add(createWildcardClause(LuceneSearchFields.WORD_DESCRIPTION, term, BooleanClause.Occur.SHOULD));

			query.add(descriptionQuery, BooleanClause.Occur.MUST);
		}
		if (CollectionUtils.isNotEmpty(w.getMeanings())) {
			for (Meaning m : w.getMeanings()) {
				if (StringUtils.isNotEmpty(m.getContent())) {
					QueryParser parser = new QueryParser(
						Version.LUCENE_34, LuceneSearchFields.WORD_CONTENT, new StandardAnalyzer(Version.LUCENE_34));
					BooleanQuery contentQuery = new BooleanQuery();
					String term = m.getContent();

					contentQuery
						.add(createFuzzyClause(LuceneSearchFields.WORD_CONTENT, term, BooleanClause.Occur.SHOULD));
					contentQuery
						.add(createTermClause(LuceneSearchFields.WORD_CONTENT, term, BooleanClause.Occur.SHOULD));
					contentQuery
						.add(createWildcardClause(LuceneSearchFields.WORD_CONTENT, term, BooleanClause.Occur.SHOULD));
					parser.setDefaultOperator(QueryParser.Operator.AND);
					query.add(parser.parse(contentQuery.toString()), BooleanClause.Occur.MUST);
				}
			}
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
	 *
	 * @throws IOException may throw.
	 */
	public static IndexWriter getLuceneWriter() throws IOException {
		synchronized (LuceneUtils.class) {
			if (_luceneIndexWriter == null) {
				File indexDir = new File(INDEX_DIR);
				//NIOFS is supposed to be faster on unix based system
				Directory dir = NIOFSDirectory.open(indexDir);
				//RAMDirectory dir = new RAMDirectory(NIOFSDirectory.open(indexDir));
				Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_34);
				IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, analyzer);
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
				}
				finally {
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
		Term key = new Term(LuceneSearchFields.ID, Long.toString(word.getId()));

		Document document = new Document();

		// add id.
		document.add(new Field(LuceneSearchFields.ID, word.getId().toString(), Field.Store.YES, Field.Index.ANALYZED));
		// add description
		document.add(
			new Field(
				LuceneSearchFields.WORD_DESCRIPTION, word.getDescription(), Field.Store.NO, Field.Index.ANALYZED));
		// add content
		if (CollectionUtils.isNotEmpty(word.getMeanings())) {
			document
			.add(new Field(LuceneSearchFields.WORD_CONTENT, word.(), Field.Store.NO, Field.Index.ANALYZED));
		}

		// update Document
		writer.updateDocument(key, document);
	}

}