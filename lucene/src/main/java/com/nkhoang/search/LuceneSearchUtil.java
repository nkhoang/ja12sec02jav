package com.nkhoang.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import com.hmsonline.common.hms.HmsEntityType;
import com.hmsonline.geocode.geog.Address;
import com.hmsonline.geocode.geog.PostalCode;
import com.hmsonline.pol.POLException;
import com.hmsonline.pol.TooManyResultsException;
import com.hmsonline.pol.ejb.entity.practitioner.PractDpsId;
import com.hmsonline.pol.ejb.util.IdTypeEnum;
import com.hmsonline.pol.ejb.util.LmsUtil;
import com.hmsonline.pol.search.vo.IdentifierVO;
import com.hmsonline.pol.search.vo.IndividualSearchVO;
import com.hmsonline.pol.search.vo.SearchVO;
import com.hmsonline.pol.util.SearchConfigurationUtil;
import com.hmsonline.searchutil.ejb.SearchUtil;

/**
 * The class provides all of utility functionalities in order to search with the
 * lucene file.
 * 
 * @author jplater
 */
public class LuceneSearchUtil {

  private static final Log LOG = LogFactory.getLog(LuceneSearchUtil.class.getCanonicalName());

  public static final String WILDCARD = "*";
  public static final String QUESTION_MARK = "?";
  public static final String EMPTY_STRING = "";
  public static final String LUCENE_ESCAPE_CHAR = "\\";
  // DPS prefix.
  private static final int DPS_FUZZY_PREFIX_LENGTH = 0; 
  // DPS fuzzy search distance.
  private static final float DPS_FUZZY_EDIT_DISTANCE = .85f; 
  private static final String IO_ERROR = "Unable to search due to I/O error";
  // thread safe
  private static IndexSearcher _practIndexSearcher;
  private static IndexSearcher _orgIndexSearcher;
  /** Default maximum number of results before an error is returned. */
  private static final int DEFAULT_MAXIMUM_SEARCH_RESULTS = 100;
  
  private static final int FUZZY_PREFIX_LENGTH = 0;
  private static final float FUZZY_EDIT_DISTANCE = .75f;
  private static final int STARTS_WITH_MAX_LENGTH = 3;
  private static final float BOOST_TERM_QUERY = 1.25f;
  private static final float BOOST_FUZZY_QUERY = .75f;
  private static final float BOOST_WILDCARD_QUERY = .5f;
  private static final float BOOST_INVERSE_QUERY = .95f;
  private static final int END_WITH_MAX_LENGTH = 7;


  public static IndexSearcher getPractIndexSearcher() throws IOException {
    return getPractIndexSearcher(LuceneSearchFields.PRACT_SEARCH_INDEX_DIR);
  }


  public static IndexSearcher getOrgIndexSearcher() throws IOException {
    return getOrgIndexSearcher(LuceneSearchFields.ORG_SEARCH_INDEX_DIR);
  }


  public static IndexSearcher getPractIndexSearcher(String luceneDir)
          throws IOException {
    if (_practIndexSearcher == null) {
      synchronized (LuceneSearchUtil.class) {
        if (_practIndexSearcher == null) {
          File indexDir = new File(luceneDir);
          // NIOFS is supposed to be faster on unix based system
          Directory dir = NIOFSDirectory.open(indexDir);
          // RAMDirectory dir = new RAMDirectory(NIOFSDirectory.open(indexDir));
          _practIndexSearcher = new IndexSearcher(dir, true);
        }
      }
    }
    return _practIndexSearcher;
  }


  public static IndexSearcher getOrgIndexSearcher(String luceneDir)
          throws IOException {
    if (_orgIndexSearcher == null) {
      synchronized (LuceneSearchUtil.class) {
        if (_orgIndexSearcher == null) {
          File indexDir = new File(luceneDir);
          // NIOFS is supposed to be faster on unix based system
          Directory dir = NIOFSDirectory.open(indexDir);
          // RAMDirectory dir = new RAMDirectory(NIOFSDirectory.open(indexDir));
          _orgIndexSearcher = new IndexSearcher(dir, true);
        }
      }
    }
    return _orgIndexSearcher;
  }


  /**
   * Sets the index searcher to null so that the next time it is grabbed it will
   * be re-opened. Otherwise, it will continue to use the old snapshot instead
   * of the updated version.
   */
  public static void refreshBothIndexSearchers() {
    refreshPractIndexSearcher();
    refreshOrgIndexSearcher();
  }


  /**
   * Sets the index searcher to null so that the next time it is grabbed it will
   * be re-opened. Otherwise, it will continue to use the old snapshot instead
   * of the updated version.
   */
  public static void refreshPractIndexSearcher() {
    // should close indexSearcher otherwise files are not released
    if (_practIndexSearcher != null) {
      try {
        _practIndexSearcher.close();
      }
      catch (Exception e) {
        LOG.error(e.getStackTrace());
      }
      _practIndexSearcher = null;
    }
  }


  /**
   * Sets the index searcher to null so that the next time it is grabbed it will
   * be re-opened. Otherwise, it will continue to use the old snapshot instead
   * of the updated version.
   */
  public static void refreshOrgIndexSearcher() {
    // should close indexSearcher otherwise files are not released
    if (_orgIndexSearcher != null) {
      try {
        _orgIndexSearcher.close();
      }
      catch (Exception e) {
        LOG.error(e.getStackTrace());
      }
      _orgIndexSearcher = null;
    }
  }
  
  private static Query buildIdQueryByType(String type,
          Set<IdentifierVO> idSet, String luceneField) {
    Query query = null;
    String searchValue = EMPTY_STRING;
    if (CollectionUtils.isNotEmpty(idSet)) {
      for (IdentifierVO id : idSet) {
        // process the matching type only.
        if (StringUtils.equalsIgnoreCase(id.getIdType(), type)) {
          // get the id value.
          searchValue = id.getIdNumber();
          // if it is empty then build WildcardQuery
          if (StringUtils.isEmpty(searchValue)) {
            query = new WildcardQuery(new Term(luceneField, LuceneSearchUtil.WILDCARD));
          }
          else {
            Term idTerm;
            if (StringUtils.equals(IdTypeEnum.STATE_LICENSE.getNodeName(), type)) {
              String state = StringUtils.isNotBlank(id.getIdState()) ? id
                      .getIdState() : WILDCARD;
              idTerm = new Term(LuceneSearchFields.STLIC, searchValue
                      + LuceneSearchFields.LUCENE_DELIM + state);
              // wrap the WilcardQuery to BooleanQuery because WildcardQuery falls into the MUST_NOT part of the main query.
              BooleanQuery stlicQuery = new BooleanQuery();
              stlicQuery.add(new WildcardQuery(idTerm), Occur.MUST);
              query = stlicQuery;
            }
            else {
              searchValue = id.getIdNumber();
              idTerm = new Term(luceneField, searchValue);
              query = new TermQuery(idTerm);
            }
          }
        }
      }
    }
    if (query == null) {
      query = new WildcardQuery(new Term(luceneField, LuceneSearchUtil.WILDCARD));
    }
    return query;
  }
  

  public static Query buildIdQuery(Set<IdentifierVO> idList) {
    Term idTerm = null;
    if (idList != null && idList.size() > 0) {
      IdentifierVO id = idList.iterator().next();
      try {
        IdTypeEnum idType = IdTypeEnum.valueOf(id.getIdType().toUpperCase());

        switch (idType) {
          case POID:
          case PIID:
            idTerm = new Term(LuceneSearchFields.GUID, id.getIdNumber());
            break;
          case MIGRATED_PIID:
            idTerm = new Term(LuceneSearchFields.MIGRATED_PIID,
                    id.getIdNumber());
            break;
          case DEA:
            idTerm = new Term(LuceneSearchFields.DEA, id.getIdNumber());
            break;
          case NPI:
            idTerm = new Term(LuceneSearchFields.NPI, id.getIdNumber());
            break;
          case TAX_ID:
            idTerm = new Term(LuceneSearchFields.TIN, id.getIdNumber());
            break;
          case STATE_LICENSE:
            String state = StringUtils.isNotBlank(id.getIdState()) ? id
                    .getIdState() : WILDCARD;
            idTerm = new Term(LuceneSearchFields.STLIC, id.getIdNumber()
                    + LuceneSearchFields.LUCENE_DELIM + state);
            return new WildcardQuery(idTerm);
          case UPIN:
            idTerm = new Term(LuceneSearchFields.UPIN, id.getIdNumber());
            break;
          case SSN:
            idTerm = new Term(LuceneSearchFields.SSN, id.getIdNumber());
            break;
          case SURESCRIPTS_ID:
            idTerm = new Term(LuceneSearchFields.SURESCRIPTS_ID,
                    id.getIdNumber());
            break;
          case HIN:
            idTerm = new Term(LuceneSearchFields.HIN, id.getIdNumber());
            break;
          case POS:
            idTerm = new Term(LuceneSearchFields.POS, id.getIdNumber());
            break;
          case DPS:
            idTerm = new Term(PractDpsId.ID_TYPE, id.getIdNumber());
            return new FuzzyQuery(idTerm, DPS_FUZZY_EDIT_DISTANCE,
                    DPS_FUZZY_PREFIX_LENGTH);
        }
      }
      catch (IllegalArgumentException iaex) {
        // just do nothing
      }
    }
    if (idTerm != null) {
      return new TermQuery(idTerm);
    }
    return null;
  }


  public static Query buildLocationQuery(SearchVO vo, SearchUtil su,
          boolean useRadius) {
    BooleanQuery locationQuery = new BooleanQuery();

    // If the user entered Addr1 info, attempt to standardize it
    if (vo.canSearchByAddress()) {
      Address address = getStandardizedAddress(vo);
    
      //Address was able to be standardized so perform the search on that address
      if (address != null) {
        String city = StringUtils.isNotBlank(address.getCity()) ? address
                .getCity().toLowerCase() : WILDCARD;
        String addr1 = StringUtils.isNotBlank((String) address.getStreetLines()
                .get(0)) ? StringUtils.lowerCase((String) address
                .getStreetLines().get(0)) : WILDCARD;
        String state = StringUtils.isNotBlank(address.getState()) ? address
                .getState().toLowerCase() : WILDCARD;

        locationQuery.add(new BooleanClause(new TermQuery(new Term(
                LuceneSearchFields.STATE_CITY_ADDR1, state
                        + LuceneSearchFields.LUCENE_DELIM + city
                        + LuceneSearchFields.LUCENE_DELIM + addr1)),
                Occur.MUST));
      }
    }
    else if (StringUtils.isNotBlank(vo.getPostalCode())) {
      Set<String> eligibleZips = new HashSet<String>();
      if (useRadius) {
        Collection<PostalCode> postalCodes = su.findZipsWithinRadius(vo
                .getPostalCode(), vo.getRadius());
        eligibleZips.addAll(PolUtil.getZips(postalCodes));
      }
      // Add the postal code specified by the user
      eligibleZips.add(vo.getPostalCode());

      locationQuery.add(new BooleanClause(buildZipQuery(eligibleZips),
              Occur.MUST));
    }
    else if (CollectionUtils.isNotEmpty(vo.getStates())) {
      Collection<String> eligibleZips = null;

      // If a single state and city has been specified, load the zip codes
      // that correspond to that combo
      if (StringUtils.isNotBlank(vo.getCity()) && vo.getStates().size() == 1) {
        eligibleZips = su.getZipsFromCityState(vo.getCity(), vo.getStates()
                .iterator().next());
      }

      if (CollectionUtils.isNotEmpty(eligibleZips)) {
        locationQuery.add(new BooleanClause(buildZipQuery(eligibleZips),
                Occur.MUST));
      }
      else {
        for (String state : vo.getStates()) {
          locationQuery.add(new BooleanClause(new WildcardQuery(new Term(
                  LuceneSearchFields.STATE_CITY_ADDR1, state
                          + LuceneSearchFields.LUCENE_DELIM + WILDCARD
                          + LuceneSearchFields.LUCENE_DELIM + WILDCARD)),
                  Occur.SHOULD));

        }
      }
    }
    else if (CollectionUtils.isNotEmpty(vo.getFips5Codes())) {
      for (String fips5 : vo.getFips5Codes()) {
        locationQuery.add(new BooleanClause(new TermQuery(new Term(
                LuceneSearchFields.FIPS5, fips5)), Occur.SHOULD));
      }
    }
    else if (CollectionUtils.isNotEmpty(vo.getCbsas())) {
      for (String cbsa : vo.getCbsas()) {
        locationQuery.add(new BooleanClause(new TermQuery(new Term(
                LuceneSearchFields.CBSA, cbsa)), Occur.SHOULD));
      }
    }

    return locationQuery;
  }


  public static BooleanQuery buildCityStateQuery(String city, String state) {
    BooleanQuery stateQuery = new BooleanQuery();
    if (StringUtils.isBlank(city) && StringUtils.isBlank(state)) {
      city = EMPTY_STRING;
      state = EMPTY_STRING;
    }
    else {
      city = StringUtils.isNotBlank(city) ? city.toLowerCase() : WILDCARD;
      state = StringUtils.isNotBlank(state) ? state.toLowerCase() : EMPTY_STRING;
    }
    stateQuery.add(new BooleanClause(new WildcardQuery(new Term(
            LuceneSearchFields.STATE_CITY_ADDR1, state
                    + LuceneSearchFields.LUCENE_DELIM + city
                    + LuceneSearchFields.LUCENE_DELIM + WILDCARD)),
            Occur.MUST));
    return stateQuery;
  }


  private static BooleanQuery buildZipQuery(Collection<String> zips) {
    // TODO: Add filtering by state to the zip search
    BooleanQuery zipQuery = new BooleanQuery();
    if (CollectionUtils.isNotEmpty(zips)) {
      for (String zip : zips) {
        zipQuery
                .add(new BooleanClause(new TermQuery(new Term(
                        LuceneSearchFields.ZIP_CODE, zip)),
                        Occur.SHOULD));
      }
    }
    return zipQuery;
  }


  public static List<String> performSearch(Query query, SearchVO searchParams,
          HmsEntityType idType) throws POLException, TooManyResultsException {

    List<String> guids = new ArrayList<String>();

    try {
      if (query != null) {
        IndexSearcher searcher = null;
        switch (idType) {
          case PRACTITIONER:
            searcher = getPractIndexSearcher();
            break;
          case ORGANIZATION:
            searcher = getOrgIndexSearcher();
            break;
          default:
            throw new POLException("Unable to find a searcher for id type of "
                    + idType);
        }

        guids = performSearch(searcher, query, searchParams, idType);
      }
    }
    catch (IOException ioe) {
      throw new POLException(IO_ERROR, ioe);
    }
    return guids;
  }


  public static List<String> performSearch(Query query, SearchVO searchParams,
          String luceneDir, HmsEntityType idType) throws POLException,
          TooManyResultsException {

    List<String> guids = new ArrayList<String>();
    try {
      if (query != null) {
        IndexSearcher searcher = null;
        switch (idType) {
          case PRACTITIONER:
            searcher = getPractIndexSearcher(luceneDir);
            break;
          case ORGANIZATION:
            searcher = getOrgIndexSearcher(luceneDir);
            break;
          default:
            throw new POLException("Unable to find a searcher for id type of "
                    + idType);
        }

        guids = performSearch(searcher, query, searchParams, idType);
      }
    }
    catch (IOException ioe) {
      throw new POLException(IO_ERROR, ioe);
    }

    return guids;
  }


  private static List<String> performSearch(IndexSearcher searcher,
          Query query, SearchVO searchParams, HmsEntityType idType)
          throws POLException, TooManyResultsException, IOException {

    List<String> guids = new ArrayList<String>();
    long start = System.currentTimeMillis();

    searchParams.setTooManyExceptionThrown(false);
    Integer maxResults = searchParams.getMaxResults();
    int maxDoc = (maxResults == null ? DEFAULT_MAXIMUM_SEARCH_RESULTS
            : maxResults.intValue());
    if (maxDoc > searcher.maxDoc()) {
      // Set the largest possible document number to search method to avoid
      // of memory issue when Lucene pre-allocates a full array of length.
      maxDoc = searcher.maxDoc();
    }
    TopDocs searchResults = searcher.search(query, maxDoc);
    if (!searchParams.isReturnIfTooMany() && searchResults.totalHits > maxDoc) {
      throw new TooManyResultsException(searchResults.totalHits);
    }
    int hitCounts;
    if (searchResults.totalHits > maxDoc) {
      hitCounts = maxDoc;
      searchParams.setTooManyExceptionThrown(true);
    }
    else {
      hitCounts = searchResults.totalHits;
    }

    ScoreDoc[] hits = searchResults.scoreDocs;
    for (int index = 0; index < hitCounts; index++) {
      ScoreDoc hit = hits[index];
      Document found = searcher.doc(hit.doc);
      guids.add(found.get(LuceneSearchFields.GUID));
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("Search took: " + (System.currentTimeMillis() - start) / 1000.0
              + "secs");
    }
    return guids;
  }
  
  
  private static Query buildLastNameQuery(String lastName) {
    lastName = StringUtils.trim(lastName);
    if (StringUtils.isBlank(lastName)) {
      return new WildcardQuery(new Term(LuceneSearchFields.LAST_NAME, LuceneSearchUtil.WILDCARD));
    }
    else {
      // Build query for last name.
      BooleanQuery lastNameQuery = new BooleanQuery();
      lastNameQuery.add(createFuzzyClause(LuceneSearchFields.LAST_NAME,
              lastName, Occur.SHOULD));
      lastNameQuery.add(createWildcardClause(LuceneSearchFields.LAST_NAME,
              lastName, Occur.SHOULD));

      BooleanQuery nameQuery = new BooleanQuery();
      nameQuery.add(new BooleanClause(lastNameQuery, Occur.MUST));

      return nameQuery;
    }
  }


  private static Query buildFirstNameQuery(String firstName) {
    firstName = StringUtils.trim(firstName);

    // Build query for first.
    if (StringUtils.isBlank(firstName)) {
      return new WildcardQuery(
              new Term(LuceneSearchFields.FIRST_NAME, LuceneSearchUtil.WILDCARD));
    }
    else {
      BooleanQuery firstNameQuery = new BooleanQuery();
      firstNameQuery.add(createFuzzyClause(LuceneSearchFields.FIRST_NAME,
              firstName, Occur.SHOULD));
      firstNameQuery.add(createWildcardClause(LuceneSearchFields.FIRST_NAME,
              firstName, Occur.SHOULD));

      BooleanQuery nameQuery = new BooleanQuery();
      nameQuery
              .add(new BooleanClause(firstNameQuery, Occur.MUST));

      return nameQuery;
    }
  }


  private static Query buildNameQuery(IndividualSearchVO vo) {
    BooleanQuery nameQuery = new BooleanQuery();

    // Build query for last name
    if (StringUtils.isNotBlank(vo.getLastName())) {
      BooleanQuery lastNameQuery = new BooleanQuery();
      lastNameQuery.add(createFuzzyClause(LuceneSearchFields.LAST_NAME, vo
              .getLastName(), Occur.SHOULD));
      lastNameQuery.add(createWildcardClause(LuceneSearchFields.LAST_NAME, vo
              .getLastName(), Occur.SHOULD));
      nameQuery.add(new BooleanClause(lastNameQuery, Occur.MUST));
    }

    // Build query for first and middle name
    if (vo.isCompareFirstAndMiddleNameTogether()) {
      if (StringUtils.isNotBlank(vo.getFirstName())
              || StringUtils.isNotBlank(vo.getMiddleName())) {
        BooleanQuery firstMiddleQuery = new BooleanQuery();

        if (StringUtils.isBlank(vo.getFirstName())
                || StringUtils.isBlank(vo.getMiddleName())) {
          String term = StringUtils.isBlank(vo.getFirstName()) ? vo
                  .getMiddleName() : vo.getFirstName();
          firstMiddleQuery.add(createFuzzyClause(LuceneSearchFields.FIRST_NAME,
                  term, Occur.SHOULD));
          firstMiddleQuery.add(createWildcardClause(
                  LuceneSearchFields.FIRST_NAME, term,
                  Occur.SHOULD));
          firstMiddleQuery.add(createTermClause(LuceneSearchFields.MIDDLE_NAME,
                  term, Occur.SHOULD));
        }
        else { // Neither first name nor middle name are blank
          // Clause 1
          BooleanQuery firstNameQuery1 = new BooleanQuery();
          firstNameQuery1.add(createFuzzyClause(LuceneSearchFields.FIRST_NAME,
                  vo.getFirstName(), Occur.SHOULD));
          firstNameQuery1.add(createWildcardClause(
                  LuceneSearchFields.FIRST_NAME, vo.getFirstName(),
                  Occur.SHOULD));

          BooleanQuery firstMiddleQuery1 = new BooleanQuery();
          firstMiddleQuery1.add(new BooleanClause(firstNameQuery1,
                  Occur.MUST));
          firstMiddleQuery1.add(createTermClause(
                  LuceneSearchFields.MIDDLE_NAME, vo.getMiddleName(),
                  Occur.MUST));
          firstMiddleQuery.add(new BooleanClause(firstMiddleQuery1,
                  Occur.SHOULD));

          // Clause 2
          BooleanQuery firstNameQuery2 = new BooleanQuery();
          firstNameQuery2.add(createFuzzyClause(LuceneSearchFields.FIRST_NAME,
                  vo.getMiddleName(), Occur.SHOULD));
          firstNameQuery2.add(createWildcardClause(
                  LuceneSearchFields.FIRST_NAME, vo.getMiddleName(),
                  Occur.SHOULD));

          BooleanQuery firstMiddleQuery2 = new BooleanQuery();
          firstMiddleQuery2.setBoost(BOOST_INVERSE_QUERY);
          firstMiddleQuery2.add(new BooleanClause(firstNameQuery2,
                  Occur.MUST));
          firstMiddleQuery2.add(createTermClause(
                  LuceneSearchFields.MIDDLE_NAME, vo.getFirstName(),
                  Occur.MUST));
          firstMiddleQuery.add(new BooleanClause(firstMiddleQuery2,
                  Occur.SHOULD));
        }

        nameQuery.add(new BooleanClause(firstMiddleQuery,
                Occur.MUST));
      }
    }
    else { // Not compare first and middle name together
      if (StringUtils.isNotBlank(vo.getFirstName())) {
        BooleanQuery firstNameQuery = new BooleanQuery();
        firstNameQuery.add(createFuzzyClause(LuceneSearchFields.FIRST_NAME, vo
                .getFirstName(), Occur.SHOULD));
        firstNameQuery.add(createWildcardClause(LuceneSearchFields.FIRST_NAME,
                vo.getFirstName(), Occur.SHOULD));
        nameQuery.add(new BooleanClause(firstNameQuery,
                Occur.MUST));
      }

      if (StringUtils.isNotBlank(vo.getMiddleName())) {
        nameQuery.add(createTermClause(LuceneSearchFields.MIDDLE_NAME, vo
                .getMiddleName(), Occur.MUST));
      }
    }

    return nameQuery;
  }
  
  
  /**
   * Build query from search round expression.
   * 
   * @return null if failed to build or a query object.
   */
  public static Query buildQueryByConfiguration(IndividualSearchVO vo, String searchConfig) {
    BooleanQuery query = null;
    if (StringUtils.isNotBlank(searchConfig)) {
      List<String> searchExpression = SearchConfigurationUtil.buildSearchExpression(searchConfig);
      LinkedList<Query> queryExpression = new LinkedList<Query>();
      LinkedList<String> operators = new LinkedList<String>();
      if (searchExpression != null && searchExpression.size()>0) {
        int index = 0;
        do {
          String s = searchExpression.get(index);
          try {
            // starting to build query expression.
            SearchConfigurationEnum op = SearchConfigurationEnum.valueOf(s.toUpperCase());
            Set<IdentifierVO> idetifiers = vo.getIdentifiers();
            Query idQuery = null;
  
            switch (op) {
              case LASTNAME:
                Query lastNameQuery = buildLastNameQuery(vo.getLastName());                
                queryExpression.add(lastNameQuery);
                break;
              case FIRSTNAME:
                Query firstNameQuery = buildFirstNameQuery(vo.getFirstName());
                queryExpression.add(firstNameQuery);
                break;
              case DEA:
                idQuery = buildIdQueryByType(IdTypeEnum.DEA.getNodeName(), idetifiers, LuceneSearchFields.DEA);
                queryExpression.add(idQuery);
                break;
              case NPI:
                idQuery = buildIdQueryByType(IdTypeEnum.NPI.getNodeName(), idetifiers, LuceneSearchFields.NPI);
                queryExpression.add(idQuery);
                break;
              case STATELICENSE:               
                idQuery = buildIdQueryByType(IdTypeEnum.STATE_LICENSE.getNodeName(), idetifiers, LuceneSearchFields.STLIC);
                queryExpression.add(idQuery);
                break;
              case PHONE:
                // if null then set EMPTY_STRING.
                if (StringUtils.isEmpty(vo.getContactNumber())) {
                  vo.setContactNumber(EMPTY_STRING);
                }
                Query phoneQuery = buildPhoneQuery(vo);

                queryExpression.add(phoneQuery);
                break;
              case ZIP:
                String zipCode = StringUtils.isBlank(vo.getPostalCode()) ? EMPTY_STRING : vo.getPostalCode();
                TermQuery zipQuery = new TermQuery(new Term(LuceneSearchFields.ZIP_CODE, zipCode));
                queryExpression.add(zipQuery);
                break;
              case PIID:
                idQuery = buildIdQueryByType(IdTypeEnum.PIID.getNodeName() , idetifiers, LuceneSearchFields.GUID);
                queryExpression.add(idQuery);
                break;
              case MIGRATED_PIID:
                idQuery = buildIdQueryByType(IdTypeEnum.MIGRATED_PIID.getNodeName(), idetifiers, LuceneSearchFields.MIGRATED_PIID);
                queryExpression.add(idQuery);
                break;
              case CITY:
              case STATE:
                  Address address = LuceneSearchUtil.getStandardizedAddress(vo);
                  
                  String state = EMPTY_STRING;
                  String city = EMPTY_STRING;
                  if (address != null) {
                    state = address.getState() != null ? address.getState() : EMPTY_STRING;
                    city = address.getCity() != null ? vo.getCity() : WILDCARD;
                  }
                    // Build state and city query.
                  BooleanQuery cityStateQuery = LuceneSearchUtil.buildCityStateQuery(city, state);
                  
                  queryExpression.add(cityStateQuery);
                  String nextOp = searchExpression.get(index + 1);
                  if (StringUtils.equals(nextOp.toUpperCase(), SearchConfigurationEnum.STATE.getNodeName())
                          || StringUtils.equals(nextOp.toUpperCase(), SearchConfigurationEnum.CITY.getNodeName())) {
                    if (searchExpression.size() > index + 2) {
                      searchExpression.remove(index + 1);
                      searchExpression.remove(index + 1);
                    }
                  }
                break;
            }
          }
          catch (IllegalArgumentException iaex) {
            // expected if op is '&' or '|'
            if (StringUtils.equals(SearchConfigurationUtil.AND, s)
                    || StringUtils.equals(SearchConfigurationUtil.OR, s)) {
              operators.add(s);
            }
          }
          ++index;
        } while (index < searchExpression.size());
        
        while (!operators.isEmpty()) {
          Occur occur = null;
          String operator = operators.poll();
          if (StringUtils.equals(operator, SearchConfigurationUtil.AND)) {
            occur = Occur.MUST;
          } else if (StringUtils.equals(operator, SearchConfigurationUtil.OR)) {
            occur = Occur.SHOULD;
          }
          // initialize query.
          if (query == null) {
            query = new BooleanQuery();
            
            Occur occur1 = occur;
            Occur occur2 = occur;
            Query queryExpression1 = queryExpression.removeLast();
            if (queryExpression1 instanceof WildcardQuery) {
              occur1 = Occur.MUST_NOT;
            }
            Query queryExpression2 = queryExpression.removeLast();
            if (queryExpression2 instanceof WildcardQuery) {
              occur2 = Occur.MUST_NOT;
            }
            query.add(new BooleanClause(queryExpression1, occur1));
            query.add(new BooleanClause(queryExpression2, occur2));
          }
          else {
            BooleanQuery newQuery = new BooleanQuery();
            newQuery.add(new BooleanClause(query, occur));
            Query queryExpression1 = queryExpression.removeLast();
            if (queryExpression1 instanceof WildcardQuery) {
              occur = Occur.MUST_NOT;
            }
            newQuery.add(new BooleanClause(queryExpression1, occur));

            query = newQuery;
          }
        }
        if (query ==  null && CollectionUtils.isNotEmpty(queryExpression)) {
          query = new BooleanQuery();
          query.add(new BooleanClause(queryExpression.get(0), Occur.MUST));
        }
      }
    }
    return query;
  }


  public static Query buildQuery(IndividualSearchVO vo, SearchUtil su, boolean radiusSearch) {
    BooleanQuery query = new BooleanQuery();

    if (vo.canSearchById()) {
      query.add(new BooleanClause(LuceneSearchUtil.buildIdQuery(vo
              .getIdentifiers()), Occur.MUST));
    }

    if (vo.canSearchByName()) {
      query.add(new BooleanClause(buildNameQuery(vo),
              Occur.MUST));
    }

    if (vo.canSearchByLocation()) {
      query.add(new BooleanClause(LuceneSearchUtil.buildLocationQuery(vo,
              su, radiusSearch), Occur.MUST));
    }

    if (CollectionUtils.isNotEmpty(vo.getPractTypes())) {
      BooleanQuery practTypeQuery = new BooleanQuery();
      for (String practType : vo.getPractTypes()) {
        practTypeQuery.add(new BooleanClause(new TermQuery(new Term(
                LuceneSearchFields.PRACT_TYPE, practType)),
                Occur.SHOULD));
      }
      query.add(new BooleanClause(practTypeQuery, Occur.MUST));
    }

    if (CollectionUtils.isNotEmpty(vo.getSpecialties())) {
      BooleanQuery specQuery = new BooleanQuery();
      for (String spec : vo.getSpecialties()) {
        specQuery.add(new BooleanClause(new TermQuery(new Term(
                LuceneSearchFields.SPECIALTY, spec)),
                Occur.SHOULD));
      }
      query.add(new BooleanClause(specQuery, Occur.MUST));
    }
    if (StringUtils.isNotBlank(vo.getTaxonomyCode())) {
      query.add(new BooleanClause(new TermQuery(new Term(
              LuceneSearchFields.TAXONOMY, vo.getTaxonomyCode())),
              Occur.MUST));
    }
    if (StringUtils.isNotBlank(vo.getContactNumber())) {
      // POL-1204: search a phone with fixed length of 7 and 10 digits as well
      // as ending with provided 7 digits.
      if (vo.getContactNumber().length() == 7) {
        query.add(new BooleanClause(buildPhoneQuery(vo),
                Occur.MUST));
      }
      else {
        query.add(new BooleanClause(new TermQuery(new Term(
                LuceneSearchFields.CONTACT_NUMBER, vo.getContactNumber())),
                Occur.MUST));
      }
    }

    if (StringUtils.isNotBlank(vo.getDob())) {
      query.add(new BooleanClause(new TermQuery(new Term(
              LuceneSearchFields.DOB, vo.getDob())), Occur.MUST));
    }

    // If we don't want to show non-vendible guys, then we need to filter on
    // vendibility. Otherwise
    // just return everyone.
    if (!vo.isShowNonVendible()) {
      query.add(new BooleanClause(new TermQuery(new Term(
              LuceneSearchFields.IS_VENDIBLE, Boolean.TRUE.toString())),
              Occur.MUST));
    }

    return query;
  }
  

  private static Query buildPhoneQuery(IndividualSearchVO vo) {
    if (StringUtils.isNotEmpty(vo.getContactNumber())) {
    BooleanQuery phoneQuery = new BooleanQuery();
    BooleanQuery partialPhoneQuery = new BooleanQuery();
    partialPhoneQuery.add(createReserveWildcardClause(
            LuceneSearchFields.CONTACT_NUMBER, vo.getContactNumber(),
            Occur.SHOULD));
    phoneQuery.add(new BooleanClause(partialPhoneQuery,
            Occur.MUST));
    return phoneQuery;
    } else {
      return new WildcardQuery(new Term(LuceneSearchFields.CONTACT_NUMBER, LuceneSearchUtil.WILDCARD));
    }
  }


  private static BooleanClause createReserveWildcardClause(String field,
          String term, Occur occur) {
    if (term.length() > END_WITH_MAX_LENGTH) {
      return createTermClause(field, term, occur);
    }

    Query query = new WildcardQuery(new Term(field, LuceneSearchUtil.WILDCARD
            + term));
    query.setBoost(BOOST_WILDCARD_QUERY);
    return new BooleanClause(query, occur);
  }


  private static BooleanClause createTermClause(String field, String term,
          Occur occur) {
    Query query = new TermQuery(new Term(field, term));
    query.setBoost(BOOST_TERM_QUERY);
    return new BooleanClause(query, occur);
  }


  private static BooleanClause createFuzzyClause(String field, String term,
          Occur occur) {
    Query query = new FuzzyQuery(new Term(field, term), FUZZY_EDIT_DISTANCE,
            FUZZY_PREFIX_LENGTH);
    query.setBoost(BOOST_FUZZY_QUERY);
    return new BooleanClause(query, occur);
  }


  private static BooleanClause createWildcardClause(String field, String term,
          Occur occur) {
    if (term.length() > STARTS_WITH_MAX_LENGTH) {
      return createTermClause(field, term, occur);
    }

    Query query = new WildcardQuery(new Term(field, term
            + LuceneSearchUtil.WILDCARD));
    query.setBoost(BOOST_WILDCARD_QUERY);
    return new BooleanClause(query, occur);
  }


  public static Address getStandardizedAddress(SearchVO vo) {
    Address address = new Address();
    address.setCity(vo.getCity());
    if (CollectionUtils.isNotEmpty(vo.getStates())) {
      address.setState(vo.getStates().iterator().next());
    }
    if (StringUtils.isNotBlank(vo.getPostalCode())) {
      address.setPostalCode(new PostalCode(vo.getPostalCode()));
    }
    if (StringUtils.isNotBlank(vo.getAddr1())) {
      address.addStreetLine(vo.getAddr1());
    }
    if (LmsUtil.standardizeAddress(address)) {
      return address;
    }
    return null;
  }
}
