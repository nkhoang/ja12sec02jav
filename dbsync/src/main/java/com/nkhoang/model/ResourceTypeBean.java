package com.nkhoang.model;

import com.nkhoang.common.persistence.ResourceTypeDataService;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;

@Entity(name = "IResourceType")
@Table(name = "RESOURCE_TYPE")
@DynamicUpdate(value = true)
@SelectBeforeUpdate(value = true)
@DynamicInsert(value = true)
@NamedQueries(value = {
      @NamedQuery(name = ResourceTypeDataService.QUERY_FIND_COUNT, query = "select count(d."
            + IBookingType.KEY + ") from IResourceType d")
})
public class ResourceTypeBean implements IResourceType {

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

   @Column(name = NAME, length = 128, nullable = false, updatable = true)
   @Index(name = "IDX_RESOURCE_TYPE_NAME")
   public String getName() {
      return name;
   }

   public void setName(String value) {
      name = value;
   }
}
