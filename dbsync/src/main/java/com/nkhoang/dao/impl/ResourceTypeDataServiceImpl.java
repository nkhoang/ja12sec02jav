package com.nkhoang.dao.impl;

import com.nkhoang.dao.ResourceTypeDataService;
import com.nkhoang.model.IResourceType;
import com.nkhoang.model.ResourceTypeBean;
import com.nkhoang.model.criteria.IResourceTypeCriteria;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.List;

public class ResourceTypeDataServiceImpl extends AbstractDataService<ResourceTypeBean, Long, IResourceTypeCriteria> implements ResourceTypeDataService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceTypeDataService.class.getCanonicalName());

  public List<ResourceTypeBean> find(IResourceTypeCriteria criteria) throws PersistenceException {
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

}
