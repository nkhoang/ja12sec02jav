package com.nkhoang.dao.impl;

import com.nkhoang.dao.WordDataService;
import com.nkhoang.model.criteria.IWordCriteria;
import com.nkhoang.model.dictionary.Word;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.List;

@Transactional
public class WordDataServiceImpl extends AbstractDataService<Word, Long, IWordCriteria> implements WordDataService {
   private static final Logger LOGGER = LoggerFactory.getLogger(WordDataServiceImpl.class.getCanonicalName());

   public List<Word> find(IWordCriteria criteria) throws PersistenceException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Searching by criteria using criteria = " + criteria);
      }

      final Criteria hbnCriteria = getPersistenceSession().createCriteria(Word.class);

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

}
