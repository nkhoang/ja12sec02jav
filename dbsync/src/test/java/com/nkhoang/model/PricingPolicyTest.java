package com.nkhoang.model;

import com.nkhoang.dao.PricingPolicyDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class PricingPolicyTest extends AbstractBeanTest<PricingPolicyBean> {
   @Autowired
   private PricingPolicyDataService pricingPolicyDataService;

   @Override
   protected List<PricingPolicyBean> doInsert(List<PricingPolicyBean> items) {
      return pricingPolicyDataService.insert(items);
   }

   @Override
   protected long doCount() {
      final Object obj =
            pricingPolicyDataService.executeQuery(getEntityClass(), getEntityName() + ".selectAll.count", true);

      return obj instanceof Number ? ((Number) obj).longValue() : 0;
   }

   @Override
   protected List<PricingPolicyBean> doCreate(int count) throws Exception {
      List<PricingPolicyBean> list = new ArrayList<PricingPolicyBean>();
      for (int i = 0; i < count; i++) {
         PricingPolicyBean bean = new PricingPolicyBean();

         bean.setName("PricingPolicy-" + System.nanoTime());

         list.add(bean);
      }
      return list;
   }

   @Override
   public Class<? extends PricingPolicyBean> getEntityClass() {
      return PricingPolicyBean.class;
   }

   @Override
   public String getEntityName() {
      return IPricingPolicy.class.getSimpleName();
   }
}
