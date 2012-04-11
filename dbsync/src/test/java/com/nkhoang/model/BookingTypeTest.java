package com.nkhoang.model;

import com.nkhoang.common.persistence.BookingTypeDataService;
import com.nkhoang.common.persistence.impl.AbstractDataService;
import com.nkhoang.model.criteria.IBookingTypeCriteria;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class BookingTypeTest extends AbstractBeanTest<BookingTypeBean> {
   @Autowired
   private BookingTypeDataService bookingTypeDataService;

   @Override
   protected List<BookingTypeBean> doInsert(List<BookingTypeBean> items) {
      return bookingTypeDataService.insert(items);
   }

   @Override
   protected long doCount() {
      final Object obj =
            bookingTypeDataService.executeQuery(getEntityClass(), getEntityName() + ".selectAll.count", true);

      return obj instanceof Number ? ((Number) obj).longValue() : 0;
   }

   @Override
   protected List<BookingTypeBean> doCreate(int count) throws Exception {
      List<BookingTypeBean> list = new ArrayList<BookingTypeBean>();
      for (int i = 0; i < count; i++) {
         BookingTypeBean bean = new BookingTypeBean();

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
