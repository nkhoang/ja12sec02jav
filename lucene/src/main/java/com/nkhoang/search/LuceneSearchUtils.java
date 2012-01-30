package com.nkhoang.search;

import com.nkhoang.common.collections.CollectionUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lucene search Utils.
 */
public class LuceneSearchUtils {
  private static final Logger LOG = LoggerFactory.getLogger(LuceneSearchUtils.class.getCanonicalName());

  private static final String PACKAGE_PATH = "/com/nkhoang/search/index";
  private static final int DEFAULT_MAXIMUM_SEARCH_RESULTS = 100;

  /**
   * Perform searching using default LuceneSearcher.
   *
   * @param query the query used to perform the search.
   * @return a list of found documents.
   * @throws java.io.IOException if the default path to the index is wrong.
   */
  public static List<Document> performSearch(Query query) throws IOException {
    return performSearch(query, LuceneUtils.getLuceneSearcher());
  }


  /**
   * Perform search with query specified.
   *
   * @param query    query used to search.
   * @param searcher IndexSearcher instance.
   * @return list of found documents.
   * @throws IOException may throw.
   */
  public static List<Document> performSearch(Query query, IndexSearcher searcher) throws IOException {
    List<Document> docs = new ArrayList<Document>();
    long start = System.currentTimeMillis();
    int maxDoc = DEFAULT_MAXIMUM_SEARCH_RESULTS;
    // perform search.
    TopDocs searchResults = searcher.search(query, maxDoc);
    LOG.info("Total hits: " + searchResults.totalHits);
    ScoreDoc[] hits = searchResults.scoreDocs;

    // don't need to limit the output because the maximum search result is set.
    for (ScoreDoc hit : hits) {
      Document found = searcher.doc(hit.doc);
      docs.add(found);
    }

    return docs;
  }


  /**
   * Perform searching for word by word content.
   *
   * @param content       the content used to search.
   * @param luceneDirPath the lucene index path.
   * @return a list of found documents.
   * @throws IOException if the path to the lucene index is incorrect.
   */
  public static List<Document> performSearchByContent(String content, String luceneDirPath) throws IOException {
    String indexDirPath = luceneDirPath + PACKAGE_PATH;
    return performSearch(QueryUtils.buildPharseQuery(content), LuceneUtils.getLuceneSearcher(indexDirPath));
  }

  /**
   * Perform search for word by id. (word description)
   *
   * @param id the id to search.
   * @return the returned document.
   * @throws IOException the IOException.
   */
  public static List<Document> performSearchById(String id) throws IOException {
    TermQuery query = new TermQuery(new Term(LuceneSearchFields.ID, id.trim().toLowerCase()));
    return performSearch(query, LuceneUtils.getLuceneSearcher());
  }

  /**
   * Perform searching for word by word content.
   *
   * @param content the content used to search.
   * @return a list of found documents.
   * @throws IOException if the path to the lucene index is incorrect.
   */
  public static List<Document> performSearchByContent(String content) throws IOException {
    List<Document> foundDocs = performSearch(QueryUtils.buildPharseQuery(content), LuceneUtils.getLuceneSearcher());
    if (org.apache.commons.collections.CollectionUtils.isEmpty(foundDocs)) {
      foundDocs = performSearch(QueryUtils.buildFuzzyQuery(content), LuceneUtils.getLuceneSearcher());
    }
    return foundDocs;
  }

}
