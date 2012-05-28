package com.nkhoang.wybness.dao;

import com.nkhoang.dao.IDataService;
import com.nkhoang.wybness.model.BookingTypeBean;
import com.nkhoang.wybness.model.IBookingType;
import com.nkhoang.wybness.model.criteria.IBookingTypeCriteria;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingTypeDataService extends IDataService<IBookingType, Long, IBookingTypeCriteria> {
   public static final String QUERY_FIND_COUNT = "IBookingType.selectAll.count";
}
