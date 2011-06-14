package com.nkhoang.common.lucene.constants;

/** The lucene search fields for individual and organization. */
public class LuceneSearchFields {

	//Don't want to include a jboss dependency
	private static String jbossDataDir = System.getProperty("jboss.server.data.dir");

	//The individuals lucene index location for searching.
	public static final String PRACT_SEARCH_INDEX_DIR = jbossDataDir + "/pol-lucene/practitioner/index";

	//The individuals lucene index location for updating.
	//NOTE: This should not be the same as the search.  This is the index that
	//will be used by the updater during the update process.
	public static final String PRACT_UPDATE_INDEX_DIR = jbossDataDir + "/pol-lucene/update/practitioner/index";

	//The organizations lucene index location for searching.
	public static final String ORG_SEARCH_INDEX_DIR = jbossDataDir + "/pol-lucene/organization/index";

	//The organizations lucene index location for updating.
	//NOTE: This should not be the same as the search.  This is the index that
	//will be used by the updater during the update process.
	public static final String ORG_UPDATE_INDEX_DIR = jbossDataDir + "/pol-lucene/update/organization/index";

	//Delimiter used to concatenate multiple search fields together
	public static final String LUCENE_DELIM = "~~";

	//Name fields
	public static final String LAST_NAME   = "last_name";
	public static final String FIRST_NAME  = "first_name";
	public static final String MIDDLE_NAME = "middle_name";
	public static final String SUFFIX_NAME = "suffix_name";
	public static final String ORG_NAME    = "org_name";

	//Indiv fields
	public static final String DOB = "dob";

	//Address fields
	public static final String ADDR1            = "addr1";
	public static final String CITY             = "city";
	public static final String STATE            = "state";
	public static final String ZIP_CODE         = "zip";
	public static final String CBSA             = "cbsa";
	public static final String FIPS5            = "fips5";
	public static final String STAT_AREA        = "statistical_area";
	public static final String STATE_CITY_ADDR1 = STATE + LUCENE_DELIM + CITY + LUCENE_DELIM + ADDR1;
	public static final String ADDR_DISPLAY     = "addr_display";

	//Id fields
	public static final String GUID           = "guid";
	public static final String HMS_PIID       = "piid";
	public static final String HMS_POID       = "poid";
	public static final String DEA            = "dea";
	public static final String STLIC          = "stlic";
	public static final String NPI            = "npi";
	public static final String SSN            = "ssn";
	public static final String SURESCRIPTS_ID = "surescripts_id";
	public static final String TIN            = "tax_id";
	public static final String UPIN           = "upin";
	public static final String HIN            = "hin";
	public static final String POS            = "pos";
	public static final String OLDPIID        = "oldPiid";
	public static final String OLDPOID        = "oldPoid";

	//Other fields
	public static final String PRACT_TYPE                        = "pract_type";
	public static final String SPECIALTY                         = "specialty";
	public static final String STATE_LICENSE_IDS                 = "state_license_ids";
	public static final String TAXONOMY                          = "taxonomy";
	public static final String TAXONOMY_DISPLAY                  = "taxonomy_display";
	public static final String SANCTIONS                         = "sanctions";
	public static final String CONTACT_NUMBER                    = "contact_number";
	public static final String ORG_TYPE                          = "org_type";
	public static final String ORG_PHONE                         = "org_phone";
	public static final String ORG_FAX                           = "org_fax";
	public static final String IS_VENDIBLE                       = "vendible";
	public static final String IS_ACTIVE                         = "active";
	public static final String IS_COREPRACTICE                   = "core_practice";
	public static final String ACTIVE_AFFIL_NUM_DISPLAY          = "active_affil_num_display";
	public static final String ACTIVE_PRACTICE_AFFIL_NUM_DISPLAY = "active_practice_affil_num_display";
}
