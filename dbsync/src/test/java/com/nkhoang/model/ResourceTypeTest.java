package com.nkhoang.model;

import com.nkhoang.dao.ResourceTypeDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ResourceTypeTest extends AbstractBeanTest<ResourceTypeBean> {
   @Autowired
   private ResourceTypeDataService resourceTypeDataService;

   @Override
   protected List<ResourceTypeBean> doInsert(List<ResourceTypeBean> items) {
      return resourceTypeDataService.insert(items);
   }

   @Override
   protected long doCount() {
      final Object obj =
            resourceTypeDataService.executeQuery(getEntityClass(), getEntityName() + ".selectAll.count", true);

      return obj instanceof Number ? ((Number) obj).longValue() : 0;
   }

   @Override
   protected List<ResourceTypeBean> doCreate(int count) throws Exception {
      List<ResourceTypeBean> list = new ArrayList<ResourceTypeBean>();
      for (int i = 0; i < count; i++) {
         ResourceTypeBean bean = new ResourceTypeBean();

         bean.setName("ResourceType-" + System.nanoTime());

         list.add(bean);
      }
      return list;
   }

   @Override
   public Class<? extends ResourceTypeBean> getEntityClass() {
      return ResourceTypeBean.class;
   }

   @Override
   public String getEntityName() {
      return IResourceType.class.getSimpleName();
   }
}
