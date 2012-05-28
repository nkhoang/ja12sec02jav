package com.nkhoang.wybness.service.impl;

import com.nkhoang.wybness.dao.ResourceTypeDataService;
import com.nkhoang.wybness.model.IResourceType;
import com.nkhoang.wybness.service.IResourceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional()

public class ResourceTypeServiceImpl extends AbstractServiceImpl<Long, IResourceType, ResourceTypeDataService>
    implements IResourceTypeService {
  @Autowired
  private ResourceTypeDataService resourceTypeDataService;

  ResourceTypeDataService getService() {
    return resourceTypeDataService;
  }
}
