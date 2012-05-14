package com.nkhoang.model.dictionary;

import com.nkhoang.model.dictionary.impl.DictionaryImpl;

import javax.persistence.*;

@Entity(name = "IDictionary")
@Table(name = "Dictionary")
public class Dictionary extends DictionaryImpl{

  @Column(name = IDictionary.ID)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long getKey() {
    return super.getKey();
  }
}
