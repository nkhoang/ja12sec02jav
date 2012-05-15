package com.nkhoang.model.dictionary;

import com.nkhoang.dao.IDictionaryDataService;
import com.nkhoang.model.dictionary.impl.DictionaryImpl;

import javax.persistence.*;

@Entity(name = "IDictionary")
@Table(name = "Dictionary", uniqueConstraints = @UniqueConstraint(columnNames = {IDictionary.NAME}))
@NamedQueries(value = {
      @NamedQuery(name = IDictionaryDataService.QUERY_FIND_BY_NAME, query = "select d from IDictionary d where d.name = :name")
})
public class Dictionary extends DictionaryImpl {

   @Column(name = IDictionary.ID)
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public Long getKey() {
      return super.getKey();
   }

   @Column(name = IDictionary.NAME)
   public String getName() {
      return super.getName();
   }
}
