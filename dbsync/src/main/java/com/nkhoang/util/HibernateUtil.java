package com.nkhoang.util;

import com.nkhoang.model.criteria.IExtendedSearchCriteria;
import com.nkhoang.model.criteria.IQueryParameter;
import com.nkhoang.model.criteria.IQueryParameter.EParameterType;
import com.nkhoang.model.criteria.ISearchCriteria;
import com.nkhoang.model.criteria.ISortCriteria;
import com.nkhoang.model.criteria.impl.QueryParameterImpl;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hnguyen
 */
public class HibernateUtil {
   private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class.getCanonicalName());

   public static final Integer MAX_PAGE_SIZE = Integer.valueOf(100);

   /**
    * Apply criteria.
    *
    * @param criteria the criteria
    * @return
    */
   public static List<IQueryParameter> applyCriteria(final ISearchCriteria criteria) {
      List<IQueryParameter> params = new ArrayList<IQueryParameter>();
      if (criteria == null) {
         LOGGER.debug("No criteria provided, criteria skipped");
      } else {
         params = applyPagination(criteria);

         if (criteria instanceof ISortCriteria) {
            applySorting(params, (ISortCriteria) criteria);
         }
      }

      return params;
   }

   /**
    * Apply pagination.
    *
    * @param criteria the criteria
    * @return
    */
   public static List<IQueryParameter> applyPagination(final ISearchCriteria criteria) {
      List<IQueryParameter> params = new ArrayList<IQueryParameter>();
      if (criteria instanceof IExtendedSearchCriteria) {
         LOGGER.debug("Applying pagination using criteria = " + criteria + ", parameters = " + params);

         final IExtendedSearchCriteria extendedCriteria = (IExtendedSearchCriteria) criteria;

         IQueryParameter parameter = null;

         if (extendedCriteria.getPageStart() == null) {
            parameter = new QueryParameterImpl();
            parameter.setType(IQueryParameter.EParameterType.FIRST_ROW).setValue(Long.valueOf(0));
            params.add(parameter);
         } else {
            parameter = new QueryParameterImpl();
            parameter.setType(IQueryParameter.EParameterType.FIRST_ROW).setValue(
                  extendedCriteria.getPageStart());
            params.add(parameter);
         }

         if (extendedCriteria.getPageSize() == null) {
            parameter = new QueryParameterImpl();
            parameter.setType(IQueryParameter.EParameterType.MAX_ROWS).setValue(MAX_PAGE_SIZE);
            params.add(parameter);
         } else {
            parameter = new QueryParameterImpl();
            parameter.setType(IQueryParameter.EParameterType.MAX_ROWS).setValue(
                  extendedCriteria.getPageSize());
            params.add(parameter);
         }
      } else {
         LOGGER.debug("No criteria provided, pagination skipped");
      }
      return params;
   }

   /**
    * Apply sorting.
    *
    * @param params   the parameters
    * @param criteria the criteria
    */
   public static void applySorting(final List<IQueryParameter> params, final ISortCriteria criteria) {
      if (criteria == null) {
         LOGGER.debug("No criteria provided, sorting skipped");
      } else {
         LOGGER.debug("Applying sorting using criteria = " + criteria + ", parameters = " + params);

         IQueryParameter parameter = new QueryParameterImpl();

         for (final ISortCriteria.IColumnOrder item : criteria.getColumnsOrdering()) {
            if (item.getColumn() == null || item.getColumn().trim().length() == 0) {
               LOGGER.warn("No valid column specified in the column ordering of criteria: " + criteria);
            } else {
               parameter.setColumnName(item.getColumn());

               if (item.isAscending()) {
                  parameter.setType(IQueryParameter.EParameterType.ORDER_ASCENDING);
               } else {
                  parameter.setType(IQueryParameter.EParameterType.ORDER_DESCENDING);
               }

               params.add(parameter);
            }
         }
      }
   }

   /**
    * Apply parameters.
    *
    * @param params   the parameters
    * @param criteria the criteria
    * @return a ProjectList if need.
    * @throws javax.persistence.PersistenceException
    *          the exception
    */
   public static ProjectionList applyParameters(final List<IQueryParameter> params, final Criteria criteria) {
      ProjectionList projectionList = Projections.projectionList();
      if (params == null || params.isEmpty()) {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No parameter to apply");
         }
      } else {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Applying parameter(s): " + params.size());
         }

         for (final IQueryParameter param : params) {
            if (EParameterType.FIRST_ROW == param.getType()) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("Applying first row parameter: " + param.getValue());
               }

               criteria.setFirstResult(((Number) param.getValue()).intValue());
            } else if (IQueryParameter.EParameterType.MAX_ROWS == param.getType()) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("Applying max rows parameter: " + param.getValue());
               }

               int pageSize = ((Number) param.getValue()).intValue();
               criteria.setFetchSize(pageSize);
               criteria.setMaxResults(pageSize);
            } else {
               final IQueryParameter.EParameterType type = param.getType();
               final String name = param.getColumnName();

               try {
                  if (type == EParameterType.ORDER_ASCENDING) {
                     if (name == null || name.trim().length() == 0) {
                        throw new IllegalArgumentException("Parameter name is null or invalid: " + name);
                     }

                     projectionList.add(Projections.property(name));
                     criteria.addOrder(Order.asc(name));
                  } else if (type == EParameterType.ORDER_DESCENDING) {
                     if (name == null || name.trim().length() == 0) {
                        throw new IllegalArgumentException("Parameter name is null or invalid: " + name);
                     }
                     projectionList.add(Projections.property(name));
                     criteria.addOrder(Order.desc(name));
                  } else {
                     try {
                        criteria.add(Restrictions.eq(param.getName(), param.getValue()));
                     } catch (final Exception e) {
                        LOGGER.warn("Invalid query parameter: " + param.getName(), e);
                     }
                  }
               } catch (final Exception e) {
                  throw new IllegalArgumentException("Invalid query parameter: " + param.getName(), e);
               }
            }
         }
      }
      return projectionList;
   }


   /**
    * Apply parameters.
    *
    * @param params the parameters
    * @param query  the query
    */
   public static void applyParameters(final List<IQueryParameter> params, final Query query) {
      if (params == null || params.isEmpty()) {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No parameter to apply");
         }
      } else {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Applying parameter(s): " + params.size());
         }

         for (final IQueryParameter param : params) {
            if (EParameterType.FIRST_ROW == param.getType()) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("Applying first row parameter: " + param.getValue());
               }

               query.setFirstResult((int) Double.parseDouble(param.getValue().toString()));
            } else if (EParameterType.MAX_ROWS == param.getType()) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("Applying max rows parameter: " + param.getValue());
               }

               query.setMaxResults((int) Double.parseDouble(param.getValue().toString()));
            } else if (EParameterType.ORDER_ASCENDING == param.getType()) {
               LOGGER.warn("Order specified for a JPA query: " + param);
            } else if (EParameterType.ORDER_DESCENDING == param.getType()) {
               LOGGER.warn("Order specified for a JPA query: " + param);
            } else {
               try {
                  query.setParameter(param.getName(), param.getValue());
               } catch (final Exception e) {
                  LOGGER.warn("Invalid query parameter: " + param.getName(), e);
               }
            }
         }
      }
   }


   /**
    * Initialize query parameters.
    *
    * @return the list
    */
   public static List<IQueryParameter> initializeQueryParameters() {
      return initializeQueryParameters(null);
   }


   /**
    * Initialize query parameters.
    *
    * @param parameters the parameters
    * @return the list
    */
   public static List<IQueryParameter> initializeQueryParameters(
         final List<IQueryParameter> parameters) {
      final List<IQueryParameter> params;

      if (parameters == null) {
         params = new ArrayList<IQueryParameter>();
      } else {
         params = parameters;
      }

      return params;
   }
}
