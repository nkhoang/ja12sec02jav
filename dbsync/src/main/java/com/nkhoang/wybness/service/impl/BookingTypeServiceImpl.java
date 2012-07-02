package com.nkhoang.wybness.service.impl;

import com.nkhoang.wybness.dao.BookingTypeDataService;
import com.nkhoang.wybness.model.IBookingType;
import com.nkhoang.wybness.service.IBookingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookingTypeServiceImpl extends AbstractServiceImpl<Long, IBookingType, BookingTypeDataService>
      implements IBookingTypeService {

   @Autowired
   private BookingTypeDataService bookingTypeDataService;

   BookingTypeDataService getService() {
      return bookingTypeDataService;
   }
}
