package com.nkhoang.common.persistence.impl;

import com.nkhoang.common.persistence.IDataService;
import com.nkhoang.model.IDataObject;
import com.nkhoang.model.criteria.ISearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AbstractDataService<L extends IDataObject<K>,T extends L,K extends Serializable,C extends ISearchCriteria>
      implements IDataService<L,T,K,C> {
   @PersistenceContext
   protected EntityManager entityManager;

   private static Logger LOGGER = LoggerFactory.getLogger(AbstractDataService.class.getCanonicalName());

   public long find(final C criteria,
                    final IVisitor<Collection<L>, Object> processor, final int batchSize)
         throws PersistenceException {
      final boolean isDebugEnabled = LOGGER.isDebugEnabled();

      if (isDebugEnabled) {
         LOGGER.debug("Searching and processing data using session:" + session + " criteria: "
               + criteria + ", batchSize:" + batchSize);
      }

      long retrieved = 0;

      if (criteria instanceof IExtendedSearchCriteria) {
         final IExtendedSearchCriteria extendedCriteria = (IExtendedSearchCriteria) criteria;
         final List<L> results = new ArrayList<L>(batchSize);
         long count;

         extendedCriteria.setPageStart(Long.valueOf(0));
         extendedCriteria.setPageSize(Integer.valueOf(batchSize));

         try {
            do {
               results.clear();
               count = find(session, results, criteria);

               if (isDebugEnabled) {
                  LOGGER.debug("Found " + results.size() + " from " + extendedCriteria.getPageStart());
               }

               retrieved += results.size();
               extendedCriteria.setPageStart(Long.valueOf(retrieved));

               if (isDebugEnabled) {
                  LOGGER.debug("Processing " + results.size() + " result(s)");
               }

               processor.visit(results);

               if (isDebugEnabled) {
                  LOGGER.debug("Processing completed");
               }
            }
            while (!results.isEmpty() && retrieved < count);
         } catch (final Throwable e) {
            handle(getEntityClass(), null, e);
         }
      } else {
         final List<L> results = new ArrayList<L>();

         try {
            retrieved = find(session, results, criteria);

            if (isDebugEnabled) {
               LOGGER.debug("Processing " + results.size() + " result(s)");
            }

            processor.visit(results);

            if (isDebugEnabled) {
               LOGGER.debug("Processing completed");
            }
         } catch (final Throwable e) {
            handle(IDataObject.class, null, e);
         }
      }

      return retrieved;
   }


   public EntityManager getEntityManager() {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager) {
      this.entityManager = entityManager;
   }
}
