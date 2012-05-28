package com.nkhoang.wybness.dao;

import com.nkhoang.dao.IDataService;
import com.nkhoang.wybness.model.IPricingPolicy;
import com.nkhoang.wybness.model.PricingPolicyBean;
import com.nkhoang.wybness.model.criteria.IPricingPolicyCriteria;

public interface PricingPolicyDataService extends IDataService<IPricingPolicy, Long, IPricingPolicyCriteria> {
   public static final String QUERY_FIND_COUNT = "IPricingPolicy.selectAll.count";

}
