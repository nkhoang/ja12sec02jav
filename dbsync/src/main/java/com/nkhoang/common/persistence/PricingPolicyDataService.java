package com.nkhoang.common.persistence;

import com.nkhoang.model.PricingPolicyBean;
import com.nkhoang.model.criteria.IPricingPolicyCriteria;

public interface PricingPolicyDataService extends IDataService<PricingPolicyBean, Long, IPricingPolicyCriteria> {
   public static final String QUERY_FIND_COUNT = "IPricingPolicy.selectAll.count";
}
