package com.nkhoang.wybness.service.impl;

import com.nkhoang.wybness.dao.PricingPolicyDataService;
import com.nkhoang.wybness.model.IPricingPolicy;
import com.nkhoang.wybness.service.IPricingPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()
public class PricingPolicyServiceImpl extends AbstractServiceImpl<Long, IPricingPolicy, PricingPolicyDataService>
    implements IPricingPolicyService {
  @Autowired
  PricingPolicyDataService pricingPolicyDataService;

  PricingPolicyDataService getService() {
    return pricingPolicyDataService;
  }
}
