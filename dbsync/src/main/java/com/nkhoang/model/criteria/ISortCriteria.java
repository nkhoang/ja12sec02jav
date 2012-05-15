package com.nkhoang.model.criteria;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author hnguyen
 */
public interface ISortCriteria extends Serializable {

  interface IColumnOrder extends Serializable {

    String getColumn();

    void setColumn(String value);

    boolean isAscending();

    void setAscending(boolean flag);
  }

  IColumnOrder addColumnOrder(String column, boolean ascending);

  IColumnOrder removeColumnOrder(String column);

  Collection<IColumnOrder> getColumnsOrdering();
}
