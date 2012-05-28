package com.nkhoang.wybness.dao.impl;

import com.nkhoang.dao.impl.AbstractDataService;
import com.nkhoang.wybness.dao.BookingTypeDataService;
import com.nkhoang.wybness.model.BookingTypeBean;
import com.nkhoang.wybness.model.IBookingType;
import com.nkhoang.wybness.model.criteria.IBookingTypeCriteria;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.List;

public class BookingTypeDataServiceImpl extends AbstractDataService<IBookingType, Long, IBookingTypeCriteria> implements BookingTypeDataService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BookingTypeDataService.class.getCanonicalName());

  public List<IBookingType> find(IBookingTypeCriteria criteria) throws PersistenceException {
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

  public Class getPersistenceClass() {
    return BookingTypeBean.class;
  }
}
