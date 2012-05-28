package com.nkhoang.dao.impl;

import com.nkhoang.dao.ISoundDataService;
import com.nkhoang.model.criteria.ISoundCriteria;
import com.nkhoang.model.dictionary.ISound;
import com.nkhoang.model.dictionary.Sound;
import com.nkhoang.wybness.model.BookingTypeBean;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.List;

public class SoundDataServiceImpl extends AbstractDataService<ISound, Long, ISoundCriteria>
      implements ISoundDataService {
   private static final Logger LOGGER = LoggerFactory.getLogger(SoundDataServiceImpl.class.getCanonicalName());

   public List<ISound> find(ISoundCriteria criteria) throws PersistenceException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Searching by criteria using criteria = " + criteria);
      }
      final Criteria hbnCriteria = getPersistenceSession().createCriteria(Sound.class);

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
    return Sound.class;
  }

}
