package com.nkhoang.common.persistence.impl;

import com.nkhoang.common.persistence.PricingPolicyDataService;
import com.nkhoang.model.IPricingPolicy;
import com.nkhoang.model.PricingPolicyBean;
import com.nkhoang.model.criteria.IPricingPolicyCriteria;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.util.List;

@Transactional
public class PricingPolicyDataServiceImpl extends AbstractDataService<PricingPolicyBean, Long, IPricingPolicyCriteria> implements PricingPolicyDataService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PricingPolicyDataService.class.getCanonicalName());

  public List<PricingPolicyBean> find(IPricingPolicyCriteria criteria) throws PersistenceException {
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

}
