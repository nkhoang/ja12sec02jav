package com.nkhoang.dao;

import com.nkhoang.wybness.model.ProductBean;
import com.nkhoang.wybness.model.criteria.IProductCriteria;

public interface ProductDataService extends IDataService<ProductBean, Long, IProductCriteria> {
   public static final String QUERY_FIND_COUNT = "IProduct.selectAll.count";
}
