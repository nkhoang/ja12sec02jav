package com.nkhoang.common.persistence;

import com.nkhoang.model.ResourceTypeBean;
import com.nkhoang.model.criteria.IResourceTypeCriteria;

public interface ResourceTypeDataService extends IDataService<ResourceTypeBean, Long, IResourceTypeCriteria> {
   public static final String QUERY_FIND_COUNT = "IResourceType.selectAll.count";
}
