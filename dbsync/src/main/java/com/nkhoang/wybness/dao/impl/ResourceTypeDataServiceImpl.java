package com.nkhoang.wybness.dao.impl;

import com.nkhoang.dao.impl.AbstractDataService;
import com.nkhoang.wybness.dao.ResourceTypeDataService;
import com.nkhoang.wybness.model.BookingTypeBean;
import com.nkhoang.wybness.model.IResourceType;
import com.nkhoang.wybness.model.ResourceTypeBean;
import com.nkhoang.wybness.model.criteria.IResourceTypeCriteria;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.List;

public class ResourceTypeDataServiceImpl extends AbstractDataService<IResourceType, Long, IResourceTypeCriteria> implements ResourceTypeDataService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceTypeDataService.class.getCanonicalName());

  public List<IResourceType> find(IResourceTypeCriteria criteria) throws PersistenceException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Searching by criteria using criteria = " + criteria);
    }

    final Criteria hbnCriteria = getPersistenceSession().createCriteria(ResourceTypeBean.class);

    if (criteria == null) {

    } else {
      final String name = criteria.getNamePattern();

      if (!(name == null || name.trim().length() == 0)) {
        hbnCriteria.add(Restrictions.like(IResourceType.NAME, name));
      }

    }

    try {
      getPersistenceSession().flush();
      return hbnCriteria.list();
    } finally {
    }
  }

  public Class getPersistenceClass() {
    return ResourceTypeBean.class;
  }
}
