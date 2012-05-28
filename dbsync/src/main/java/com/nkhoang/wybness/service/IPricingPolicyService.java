package com.nkhoang.wybness.service;

import com.nkhoang.wybness.dao.PricingPolicyDataService;
import com.nkhoang.wybness.model.IPricingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



public interface IPricingPolicyService extends IService<Long, IPricingPolicy>{
}
