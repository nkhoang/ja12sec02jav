package com.nkhoang.model.criteria.impl;


import com.nkhoang.model.criteria.ISortCriteria;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hnguyen
 */
public class SortCriteriaImpl extends ExtendedCriteriaImpl implements ISortCriteria {

   public class ColumnOrder implements IColumnOrder {
      private String column = null;
      private boolean ascending = true;

      /*
      * (non-Javadoc)
      * @see ISortCriteria#getColumn
      */
      public String getColumn() {
         return column;
      }

      /*
      * (non-Javadoc)
      * @see ISortCriteria#isAscending
      */
      public boolean isAscending() {
         return ascending;
      }

      /*
      * (non-Javadoc)
      * @see ISortCriteria#setAscending
      */
      public void setAscending(final boolean flag) {
         ascending = flag;
      }

      /*
      * (non-Javadoc)
      * @see ISortCriteria#setColumn
      */
      public void setColumn(final String value) {
         column = value;
      }

      /*
      * (non-Javadoc)
      * @see java.lang.Object#toString()
      */
      @Override
      public String toString() {
         final StringBuilder builder = new StringBuilder();

         builder.append(super.toString());
         builder.append("[column=");
         builder.append(column);
         builder.append(", ascending=");
         builder.append(ascending);
         builder.append("]");

         return builder.toString();
      }
   }

   /**
    * The columns ordering.
    */
   private final Map<String, IColumnOrder> columnsOrdering = new HashMap<String, IColumnOrder>();

   /**
    * The Constructor.
    */
   public SortCriteriaImpl() {
      super();
   }

   /*
   * (non-Javadoc)
   * @see ISortCriteria#getColumnsOrdering
   */
   public Collection<IColumnOrder> getColumnsOrdering() {
      return columnsOrdering.values();
   }

   /*
   * (non-Javadoc)
   * @see ISortCriteria#addColumnOrder
   */
   public IColumnOrder addColumnOrder(final String column, final boolean ascending) {
      final IColumnOrder result = new ColumnOrder();

      result.setColumn(column);
      result.setAscending(ascending);
      columnsOrdering.put(result.getColumn(), result);

      return result;
   }

   /*
   * (non-Javadoc)
   * @see ISortCriteria#removeColumnOrder
   */
   public IColumnOrder removeColumnOrder(final String column) {
      return columnsOrdering.remove(column);
   }

   /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
   @Override
   public String toString() {
      final StringBuilder builder = new StringBuilder();

      builder.append(super.toString());
      builder.append("[columnsOrdering=");
      builder.append(Arrays.toString(columnsOrdering.values().toArray()));
      builder.append("]");

      return builder.toString();
   }
}
