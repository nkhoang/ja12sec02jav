package com.nkhoang.model;

import com.nkhoang.common.persistence.BookingTypeDataService;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.List;


@Entity(name = "IBookingType")
@Table(name = "BOOKING_TYPE")
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
@DynamicInsert(value = true)
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


   @OneToMany(targetEntity = ProductBean.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "bookingType")
   public List<IProduct> getProducts() {
      return products;
   }

   public void setProducts(List<IProduct> value) {
      products = value;
   }
}
