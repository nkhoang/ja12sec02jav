package com.nkhoang.wybness.dao.impl;

import com.nkhoang.dao.impl.AbstractDataService;
import com.nkhoang.wybness.dao.PricingPolicyDataService;
import com.nkhoang.wybness.model.BookingTypeBean;
import com.nkhoang.wybness.model.IPricingPolicy;
import com.nkhoang.wybness.model.PricingPolicyBean;
import com.nkhoang.wybness.model.criteria.IPricingPolicyCriteria;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.List;

public class PricingPolicyDataServiceImpl extends AbstractDataService<IPricingPolicy, Long, IPricingPolicyCriteria> implements PricingPolicyDataService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PricingPolicyDataService.class.getCanonicalName());

  public List<IPricingPolicy> find(IPricingPolicyCriteria criteria) throws PersistenceException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Searching by criteria using criteria = " + criteria);
    }

    final Criteria hbnCriteria = getPersistenceSession().createCriteria(PricingPolicyBean.class);

    if (criteria == null) {

    } else {
      final String name = criteria.getNamePattern();

      if (!(name == null || name.trim().length() == 0)) {
        hbnCriteria.add(Restrictions.like(IPricingPolicy.NAME, name));
      }

    }

    try {
      getPersistenceSession().flush();
      return hbnCriteria.list();
    } finally {
    }
  }

  public Class getPersistenceClass() {
    return PricingPolicyBean.class;
  }
}
