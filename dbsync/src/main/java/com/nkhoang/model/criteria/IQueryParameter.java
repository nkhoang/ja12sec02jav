package com.nkhoang.model.criteria;

import java.io.Serializable;


/**
 * @author hnguyen
 */
public interface IQueryParameter extends Serializable {
  enum EParameterType {
    /**
     * The first row.
     */
    FIRST_ROW,

    /**
     * The max rows.
     */
    MAX_ROWS,

    /**
     * The ascending order. The value and name are ignored.
     */
    ORDER_ASCENDING,

    /**
     * The descending order. The value and name are ignored.
     */
    ORDER_DESCENDING,
  }

  String ORDER_PARAM = "orderParam";

  String getName();

  String getColumnName();

  Object getValue();

  EParameterType getType();

  IQueryParameter setName(String value);

  IQueryParameter setColumnName(String value);

  IQueryParameter setValue(Object value);

  IQueryParameter setType(EParameterType value);
}
