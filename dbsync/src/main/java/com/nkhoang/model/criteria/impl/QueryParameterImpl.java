package com.nkhoang.model.criteria.impl;

import com.nkhoang.model.criteria.IQueryParameter;

/**
 * @author hnguyen
 */
public class QueryParameterImpl implements IQueryParameter {
   private String _name = null;

   private String _columnName = null;

   private Object _value = null;

   private EParameterType _type = null;


   public QueryParameterImpl() {
   }


   public QueryParameterImpl(final String pName) {
      _name = pName;
   }

   /*
   * (non-Javadoc)
   * @see IQueryParameter#getName()
   */
   public String getName() {
      final String result;

      if (_name == null) {
         result = _columnName;
      } else {
         result = _name;
      }

      return result;
   }

   /*
   * (non-Javadoc)
   * @see IQueryParameter#getValue()
   */
   public Object getValue() {
      return _value;
   }

   /*
   * (non-Javadoc)
   * @see IQueryParameter#getColumnName()
   */
   public String getColumnName() {
      final String result;

      if (_columnName == null) {
         result = _name;
      } else {
         result = _columnName;
      }

      return result;
   }

   /*
   * (non-Javadoc)
   * @see IQueryParameter#getType()
   */
   public EParameterType getType() {
      return _type;
   }


   /*
   * (non-Javadoc)
   * @see IQueryParameter#setValue(java.lang.Object)
   */
   public IQueryParameter setValue(final Object pValue) {
      _value = pValue;

      return this;
   }

   /*
   * (non-Javadoc)
   * @see IQueryParameter#setName(java.lang.String)
   */
   public IQueryParameter setName(final String pValue) {
      _name = pValue;

      return this;
   }

   /*
   * (non-Javadoc)
   * @see IQueryParameter#setColumnName(java.lang.String)
   */
   public IQueryParameter setColumnName(final String pValue) {
      _columnName = pValue;

      return this;
   }

   /*
   * (non-Javadoc)
   * @see IQueryParameter#setType(IQueryParameter.EParameterType)
   */
   public IQueryParameter setType(final EParameterType pValue) {
      _type = pValue;

      return this;
   }

   public String toString() {
      final StringBuffer buffer = new StringBuffer();

      buffer.append(super.toString());
      buffer.append("[");
      buffer.append("name = ").append(_name);
      buffer.append(",value = ").append(_value);
      buffer.append("]");

      return buffer.toString();
   }
}
