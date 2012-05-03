package com.nkhoang.common.persistence.impl;

import com.nkhoang.common.persistence.BookingTypeDataService;
import com.nkhoang.common.persistence.WordDataService;
import com.nkhoang.model.BookingTypeBean;
import com.nkhoang.model.IWord;
import com.nkhoang.model.Word;
import com.nkhoang.model.criteria.IWordCriteria;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctResultTransformer;
import org.hibernate.transform.ResultTransformer;
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
