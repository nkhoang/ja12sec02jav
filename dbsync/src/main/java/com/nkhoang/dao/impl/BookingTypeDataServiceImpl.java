package com.nkhoang.dao.impl;

import com.nkhoang.dao.BookingTypeDataService;
import com.nkhoang.model.BookingTypeBean;
import com.nkhoang.model.IBookingType;
import com.nkhoang.model.criteria.IBookingTypeCriteria;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.List;

@Transactional
public class BookingTypeDataServiceImpl extends AbstractDataService<BookingTypeBean, Long, IBookingTypeCriteria> implements BookingTypeDataService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookingTypeDataService.class.getCanonicalName());

  public List<BookingTypeBean> find(IBookingTypeCriteria criteria) throws PersistenceException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Searching by criteria using criteria = " + criteria);
    }

    final Criteria hbnCriteria = getPersistenceSession().createCriteria(BookingTypeBean.class);

    if (criteria == null) {

    } else {
      final String name = criteria.getNamePattern();

      if (!(name == null || name.trim().length() == 0)) {
        hbnCriteria.add(Restrictions.like(IBookingType.NAME, name));
      }

    }

    try {
      getPersistenceSession().flush();
      return hbnCriteria.list();
    } finally {
    }
  }

}
