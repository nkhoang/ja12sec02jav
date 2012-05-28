package com.nkhoang.wybness.dao;

import com.nkhoang.dao.IDataService;
import com.nkhoang.wybness.model.IResourceType;
import com.nkhoang.wybness.model.ResourceTypeBean;
import com.nkhoang.wybness.model.criteria.IResourceTypeCriteria;

public interface ResourceTypeDataService extends IDataService<IResourceType , Long, IResourceTypeCriteria> {
   public static final String QUERY_FIND_COUNT = "IResourceType.selectAll.count";
}
