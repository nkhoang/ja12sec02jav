package com.nkhoang.common.abtab.parser;

/**
 * Interface for parsers which parse "simple" datasources, which generally do
 * not have embedded table names, etc.
 */
public interface SimpleParser {

	public void setTableNameFromUrl(String urlStr, boolean override);

	public void setTableName(String tableName);

	public String getTableName();

}
