package com.nkhoang.dao.impl;

import com.nkhoang.dao.IWordDataService;
import com.nkhoang.model.criteria.IWordCriteria;
import com.nkhoang.model.dictionary.IWord;
import com.nkhoang.model.dictionary.Word;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.List;

@Transactional
public class WordDataServiceImpl extends AbstractDataService<IWord, Long, IWordCriteria> implements IWordDataService {
   private static final Logger LOGGER = LoggerFactory.getLogger(WordDataServiceImpl.class.getCanonicalName());

   public List<IWord> find(IWordCriteria criteria) throws PersistenceException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Searching by criteria using criteria = " + criteria);
      }

      final Criteria hbnCriteria = getPersistenceSession().createCriteria(Word.class);

      if (criteria == null) {

      } else {
         if (criteria.getKey() != null) {
            hbnCriteria.add(Restrictions.eq("key", criteria.getKey()));
         } else {
            Conjunction conj = Restrictions.conjunction();
            if (StringUtils.isNotEmpty(criteria.getDictName())) {
               hbnCriteria.createAlias(IWord.DICTIONARY, "dict");
               conj.add(Restrictions.eq("dict.name", criteria.getDictName()));
            }

            if (StringUtils.isNotEmpty(criteria.getWord())) {
               conj.add(Restrictions.eq("word", criteria.getWord()));
            }

            if (conj.conditions().iterator().hasNext()) {
               hbnCriteria.add(conj);
            }
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
