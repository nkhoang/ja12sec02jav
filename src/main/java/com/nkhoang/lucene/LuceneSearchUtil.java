package com.nkhoang.lucene;

import com.google.appengine.api.datastore.PreparedQuery;
import com.nkhoang.common.lucene.constants.LuceneSearchFields;
import com.nkhoang.gae.model.Word;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryParser.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.search.*;
import org.apache.lucene.search.function.FieldScoreQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 6/14/11
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class LuceneSearchUtil {
	private static final Log LOG = LogFactory.getLog(LuceneSearchUtil.class);

	private static IndexSearcher _wordIndexSearcher;
	private static final int DEFAULT_MAXIMUM_SEARCH_RESULTS = 100;

	public static IndexSearcher getWordIndexSearcher(String luceneDir) throws IOException {
		if (_wordIndexSearcher == null) {
			synchronized (LuceneSearchUtil.class) {
				if (_wordIndexSearcher == null) {
					File indexDir = new File(luceneDir);
					Directory dir = NIOFSDirectory.open(indexDir);
					_wordIndexSearcher = new IndexSearcher(dir, true);
				}
			}
		}
		return _wordIndexSearcher;
	}

	public static void refreshWordIndexSearcher() {
		if (_wordIndexSearcher != null) {
			try {
				_wordIndexSearcher.close();
			}
			catch (Exception e) {
				LOG.error(e.getStackTrace());
			}
			_wordIndexSearcher = null;
		}
	}


	private static List<String> performSearch(
		IndexSearcher searcher, Query query, Integer maxResults) throws TooManyResultsException, IOException {

		List<Document> documents = performSearchForDocument(searcher, query, maxResults);
		List<String> wids = new ArrayList<String>();
		for (Document found : documents) {
			wids.add(found.get(LuceneSearchFields.WORD_ID));
		}
		return wids;
	}

	private static List<Document> performSearchForDocument(
		IndexSearcher searcher, Query query, Integer maxResults) throws TooManyResultsException, IOException {

		List<Document> docs = new ArrayList<Document>();

		long start = System.currentTimeMillis();

		int maxDoc = (maxResults == null ? DEFAULT_MAXIMUM_SEARCH_RESULTS : maxResults.intValue());
		if (maxDoc > searcher.maxDoc()) {
			maxDoc = searcher.maxDoc();
		}

		TopDocs searchResults = searcher.search(query, maxDoc);

		if (searchResults.totalHits > maxDoc) {
			throw new TooManyResultsException(searchResults.totalHits);
		}

		ScoreDoc[] hits = searchResults.scoreDocs;
		for (ScoreDoc hit : hits) {
			Document found = searcher.doc(hit.doc);
			docs.add(found);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(
				"Search took: " + (System.currentTimeMillis() - start) / 1000.0 + "secs");
		}

		return docs;
	}

	public static String escapeSpecialChar(String s) {
		EscapeQuerySyntax escaper = new EscapeQuerySyntaxImpl();
		return escaper.escape(s, Locale.getDefault(), null).toString();
	}

	public static Query buildQuery(String keyword) {
		if (StringUtils.isBlank(keyword)) {
			return null;
		}
		BooleanQuery query = new BooleanQuery();

		// first just check the word description only.
		query.add(
			new BooleanClause(
				new TermQuery(new Term(LuceneSearchFields.WORD_DESCRIPTION, keyword)), BooleanClause.Occur.MUST));

		return query;
	}
}
