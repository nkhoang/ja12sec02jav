package com.nkhoang.dao.impl;

import com.nkhoang.dao.IDictionaryDataService;
import com.nkhoang.model.criteria.IDictionaryCriteria;
import com.nkhoang.model.criteria.IQueryParameter;
import com.nkhoang.model.criteria.impl.QueryParameterImpl;
import com.nkhoang.model.dictionary.Dictionary;
import com.nkhoang.model.dictionary.IDictionary;
import com.nkhoang.util.HibernateUtil;
import com.nkhoang.wybness.model.BookingTypeBean;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

public class DictionaryDataServiceImpl extends AbstractDataService<IDictionary, Long, IDictionaryCriteria>
      implements IDictionaryDataService {
   private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryDataServiceImpl.class.getCanonicalName());

   public Dictionary findByName(String dictName) throws PersistenceException {
      try {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Searching by name = " + dictName);
         }
         Query query = entityManager.createNamedQuery(QUERY_FIND_BY_NAME);
         List<IQueryParameter> queryParams = HibernateUtil.initializeQueryParameters();
         IQueryParameter queryParam = new QueryParameterImpl();
         queryParam.setName("name");
         queryParam.setValue(dictName);

         queryParams.add(queryParam);

         HibernateUtil.applyParameters(queryParams, query);

         Object result = query.getSingleResult();
         if (result != null) {
            return (Dictionary) result;
         }
      } catch (NoResultException NREx) {
      } catch (Throwable t) {
         throw new PersistenceException("An persistence exception occurred.", t);
      }
      return null;
   }

   public List<IDictionary> find(IDictionaryCriteria criteria) throws PersistenceException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Searching by criteria using criteria = " + criteria);
      }
      final Criteria hbnCriteria = getPersistenceSession().createCriteria(Dictionary.class);

      if (criteria == null) {

      } else {
         if (criteria.getKey() != null) {
            hbnCriteria.add(Restrictions.eq("key", criteria.getKey()));
         }

         hbnCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

         return hbnCriteria.list();
      }

      try {
         getPersistenceSession().flush();
         return hbnCriteria.list();
      } finally {
      }
   }

  public Class getPersistenceClass() {
    return Dictionary.class;
  }

}
