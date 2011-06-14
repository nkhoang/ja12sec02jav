package com.nkhoang.common.lucene;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

/**
 * This class contains some utility methods for interacting with Lucene
 * 
 */
public class LuceneUtils {

  // Reserved chars = +-&|!(){}[]^"~*?:\
  private static final String[] LUCENE_RESERVED_CHARS = new String[] {
      " ", "+", "-", "&", "|", "!", "(", ")", "{", "}", "[", "]", "^", "\"",
      "~", "*", "?", ":", "\\"
  };

  private static final Set<String> LUCENE_RESERVED_WORDS = new HashSet<String>(
          Arrays.asList(new String[] {
              "AND", "OR", "NOT"
          }));


  /**
   * Parses the provided query using the provided default field and Analyzer
   * 
   * @param query
   *          Lucene query to parse
   * @param defaultField
   *          name of the default field for query terms
   * @param analyzer
   *          Analyzer to use when parsing
   * @return A Query representation of the query string
   * @throws ParseException
   *           thrown if parsing fails
   */
  public static Query parseQuery(String query, String defaultField,
          Analyzer analyzer) throws ParseException {
    QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, defaultField,
            analyzer);
    return parser.parse(query);
  }


  /**
   * Parses the provided query using the provided Analyzer and no default field
   * 
   * @param query
   *          Lucene query to parse
   * @param analyzer
   *          Analyzer to use when parsing
   * @return A Query representation of the query string
   * @throws ParseException
   *           thrown if parsing fails
   */
  public static Query parseQuery(String query, Analyzer analyzer)
          throws ParseException {
    return parseQuery(query, "", analyzer);
  }


  /**
   * Parses the provided query using no default field and the StandardAnalyzer
   * 
   * @param query
   *          Lucene query to parse
   * @return A Query representation of the query string
   * @throws ParseException
   *           thrown if parsing fails
   */
  public static Query parseQuery(String query) throws ParseException {
    return parseQuery(query, "", new StandardAnalyzer(Version.LUCENE_CURRENT));
  }


  /**
   * Escapes Lucene special characters (including whitespace) as well as
   * reserved words. Since wild cards are special characters, all wild cards in
   * the search term will be escaped. So wild cards should be added after
   * escaping the search term.
   * <p>
   * The {@code QueryParser.escape} method does not escape whitespace. Otherwise
   * we could just use that.
   * @param text
   *          the search text to escape
   * @param excludeEscapeStr
   *          array of characters not to escape
   * @return the escaped search text.
   * @see QueryParser#escape(String)
   */
  public static String escapeSearchText(String text, String...excludeEscapeStr) {
    // add escape character if the word is reserved word i.e end becomes /end
    if (LUCENE_RESERVED_WORDS.contains(text.toUpperCase().trim())) {
      text = "\\" + text;
      return text;
    }

    Set<String> luceneReservedToEscape = new HashSet<String>(Arrays
            .asList(LUCENE_RESERVED_CHARS));
    // remove excluded characters from the list of reserved character because we
    // don't want to escape them
    if (excludeEscapeStr.length> 0) {
      luceneReservedToEscape.removeAll(Arrays.asList(excludeEscapeStr));
    }

    // now we have the final list of reserved chrs that we want to escape.
    String reservedCharRegx = "\\Q"
            + StringUtils.join(luceneReservedToEscape.toArray(), "\\E|\\Q")
            + "\\E";
    return text == null ? null : text.replaceAll(reservedCharRegx, "\\\\$0");
  }
}
