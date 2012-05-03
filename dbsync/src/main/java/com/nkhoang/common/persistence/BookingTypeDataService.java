package com.nkhoang.common.persistence;

import com.nkhoang.model.BookingTypeBean;
import com.nkhoang.model.criteria.IBookingTypeCriteria;

public interface BookingTypeDataService extends IDataService<BookingTypeBean, Long, IBookingTypeCriteria> {
   public static final String QUERY_FIND_COUNT = "IBookingType.selectAll.count";
}
