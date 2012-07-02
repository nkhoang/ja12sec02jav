package com.nkhoang.wybness.model;

import com.nkhoang.wybness.dao.BookingTypeDataService;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity(name = "IBookingType")
@Table(name = "BOOKING_TYPE")
@NamedQueries(value = {
      @NamedQuery(name = BookingTypeDataService.QUERY_FIND_COUNT, query = "select count(d."
            + IBookingType.KEY + ") from IBookingType d")
})
public class BookingTypeBean implements IBookingType {

   private List<IProduct> products;

   /**
    * The bookingType key property
    */
   private Long key;

   /**
    * The bookingType name property
    */
   private String name;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = ID, nullable = false)
   public Long getKey() {
      return key;
   }

   public void setKey(Long value) {
      key = value;
   }

   @Column(name = IBookingType.NAME, length = 128, nullable = false, updatable = true)
   @Index(name = "IDX_BOOKING_TYPE_NAME")
   public String getName() {
      return name;
   }

   public void setName(String value) {
      name = value;
   }


   @OneToMany(targetEntity = ProductBean.class,
         fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "bookingType")
   public List<IProduct> getProducts() {
      if (CollectionUtils.isEmpty(products)) {
         products = new ArrayList<IProduct>();
      }
      return products;
   }

   public void setProducts(List<IProduct> value) {
      products = value;
   }
}
