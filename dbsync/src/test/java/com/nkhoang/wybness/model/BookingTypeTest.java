package com.nkhoang.wybness.model;

import com.nkhoang.wybness.dao.BookingTypeDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class BookingTypeTest extends AbstractBeanTest<IBookingType> {
   @Autowired
   private BookingTypeDataService bookingTypeDataService;

   @Override
   protected List<IBookingType> doInsert(List<IBookingType> items) {
      return bookingTypeDataService.insert(items);
   }

   @Override
   protected long doCount() {
      final Object obj =
            bookingTypeDataService.executeQuery(getEntityClass(), getEntityName() + ".selectAll.count", true);

      return obj instanceof Number ? ((Number) obj).longValue() : 0;
   }

   @Override
   protected List<IBookingType> doCreate(int count) throws Exception {
      List<IBookingType> list = new ArrayList<IBookingType>();
      for (int i = 0; i < count; i++) {
         IBookingType bean = new BookingTypeBean();

         bean.setName("bookingType-" + System.nanoTime());
         bean.setProducts(new ArrayList<IProduct>());

         list.add(bean);
      }
      return list;
   }

   @Override
   public Class<? extends BookingTypeBean> getEntityClass() {
      return BookingTypeBean.class;
   }

   @Override
   public String getEntityName() {
      return IBookingType.class.getSimpleName();
   }
}
