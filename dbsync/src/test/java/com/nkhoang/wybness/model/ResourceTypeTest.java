package com.nkhoang.wybness.model;

import com.nkhoang.wybness.dao.ResourceTypeDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ResourceTypeTest extends AbstractBeanTest<IResourceType> {
   @Autowired
   private ResourceTypeDataService resourceTypeDataService;

   protected List<IResourceType> doInsert(List<IResourceType> items) {
      return resourceTypeDataService.insert(items);
   }

   @Override
   protected long doCount() {
      final Object obj =
            resourceTypeDataService.executeQuery(getEntityClass(), getEntityName() + ".selectAll.count", true);

      return obj instanceof Number ? ((Number) obj).longValue() : 0;
   }

   @Override
   protected List<IResourceType> doCreate(int count) throws Exception {
      List<IResourceType> list = new ArrayList<IResourceType>();
      for (int i = 0; i < count; i++) {
         IResourceType bean = new ResourceTypeBean();

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
