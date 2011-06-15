// Copyright (c) 2005 Health Market Science, Inc.

package com.nkhoang.common.abtab;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.List;

import com.nkhoang.common.db.SerializableResultSetMetaData;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Convenience implementation of ResultSetMetaData for RawDataParsers to use.
 */
public class RawMetadata implements SerializableResultSetMetaData
{
  private static final long serialVersionUID = 8842337288504076390L;
  
  private int _columnCount;
  private DefaultMap<Integer, Integer> _displaySizes = new DefaultMap<Integer, Integer>();
  private DefaultMap<Integer, String> _names = new DefaultMap<Integer, String>();
  private DefaultMap<Integer, Integer> _nullables = new DefaultMap<Integer, Integer>();
  private DefaultMap<Integer, Integer> _precisions = new DefaultMap<Integer, Integer>();
  private DefaultMap<Integer, Integer> _scales = new DefaultMap<Integer, Integer>();
  private DefaultMap<Integer, Integer> _types = new DefaultMap<Integer, Integer>();
  
  public int getColumnCount() {
    return _columnCount;
  }
  public void setColumnCount(int count) {
    _columnCount = count;
  }
  
  public int isNullable(int column) {
    return _nullables.get(column, ResultSetMetaData.columnNullableUnknown);
  }
  public void setNullable(int column, boolean nullable) {
    _nullables.put(column, nullable ? ResultSetMetaData.columnNullable :
        ResultSetMetaData.columnNoNulls);
  }
  public void setNullable(int column, int nullable) {
    _nullables.put(column, nullable);
  }
  
  public int getColumnDisplaySize(int column) {
    return _displaySizes.get(column, 0);
  }
  public void setColumnDisplaySize(int column, int displaySize) {
    _displaySizes.put(column, displaySize);
  }
  
  public String getColumnName(int column) {
    return _names.get(column);
  }
  public void setColumnName(int column, String name) {
    _names.put(column, name);
  }
  public void setColumnNames(String... names) {
    for (int i=0; i<names.length; i++) {
      _names.put(i+1, names[i]);
    }
  }
  public void setColumnNames(List<String> names) {
    for (int i=0; i<names.size(); i++) {
      _names.put(i+1, names.get(i));
    }
  }
  
  public int getPrecision(int column) {
    return _precisions.get(column, 0);
  }
  public void setPrecision(int column, int precision) {
    _precisions.put(column, precision);
  }
  
  public int getScale(int column) {
    return _scales.get(column, 0);
  }
  public void setScale(int column, int scale) {
    _scales.put(column, scale);
  }
  
  public int getColumnType(int column) {
    return _types.get(column, Types.VARCHAR);
  }
  public void setColumnType(int column, int type) {
    _types.put(column, type);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
  
  public boolean isAutoIncrement(int column) { return false; }
  public boolean isCaseSensitive(int column) { return false; }
  public boolean isSearchable(int column) { return false; }
  public boolean isCurrency(int column) { return false; }
  public boolean isSigned(int column) { return false; }
  public boolean isReadOnly(int column) { return false; }
  public boolean isWritable(int column) { return false; }
  public boolean isDefinitelyWritable(int column) { return false; }
  public String getColumnLabel(int column) { return getColumnName(column); }
  public String getSchemaName(int column) { return null; }
  public String getTableName(int column) { return null; }
  public String getCatalogName(int column) { return null; }
  public String getColumnClassName(int column) { return null; }
  public String getColumnTypeName(int column) {
    throw new UnsupportedOperationException("Use getColumnType to ensure portability.");
  }
  
  /**
   * @param srcMd the source ResultSetMetaData
   * @return a copy of the given ResultSetMetaData (which is standalone and
   *         suitable for Serialization).
   */
  public static RawMetadata copy(ResultSetMetaData srcMd)
    throws SQLException
  {
    return copy(srcMd, 1, 1, srcMd.getColumnCount());
  }

  /**
   * @param srcMd the source ResultSetMetaData
   * @param srcPos starting index of source meta data to copy (1 based)
   * @param destPos starting index for destination meta data (1 based)
   * @param length number of columns to copy from source to destination
   * 
   * @return a (partial) copy of the given ResultSetMetaData (which is
   *         standalone and suitable for Serialization).
   */
  public static RawMetadata copy(ResultSetMetaData srcMd,
                                 int srcPos, int destPos, int length)
    throws SQLException
  {
    int srcLength = srcMd.getColumnCount();
    if((srcPos < 1) || (srcPos > srcLength) || (destPos < 1) || (length < 0) ||
       ((srcPos + length - 1) > srcLength)) {
      throw new IllegalArgumentException(
          "Invalid srcPos, destPos, or srcLength");
    }

    RawMetadata destMd = new RawMetadata();

    destMd.setColumnCount(destPos + length - 1);
    
    for(int i = srcPos; i <= (srcPos + length - 1); ++i) {
      destMd.setNullable(destPos, srcMd.isNullable(i));
      destMd.setColumnDisplaySize(destPos, srcMd.getColumnDisplaySize(i));
      destMd.setColumnName(destPos, srcMd.getColumnName(i));
      destMd.setPrecision(destPos, srcMd.getPrecision(i));
      destMd.setScale(destPos, srcMd.getScale(i));
      destMd.setColumnType(destPos, srcMd.getColumnType(i));
      ++destPos;
    }

    return destMd;
  }

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	private static class DefaultMap<KeyType, ValueType>
  extends Hashtable<KeyType, ValueType>
  {
    private static final long serialVersionUID = 7762920027327445697L;
    public ValueType get(Object key, ValueType defaultValue) {
      ValueType rtn = get(key);
      if (rtn == null) {
        return defaultValue;
      }
      return rtn;
    }
  }
  
}
