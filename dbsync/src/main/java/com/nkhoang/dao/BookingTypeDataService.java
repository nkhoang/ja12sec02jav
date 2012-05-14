package com.nkhoang.dao;

import com.nkhoang.model.BookingTypeBean;
import com.nkhoang.model.criteria.IBookingTypeCriteria;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingTypeDataService extends IDataService<BookingTypeBean, Long, IBookingTypeCriteria> {
   public static final String QUERY_FIND_COUNT = "IBookingType.selectAll.count";
}
