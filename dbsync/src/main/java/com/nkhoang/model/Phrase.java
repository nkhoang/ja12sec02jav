package com.nkhoang.model;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "IPhrase")
@Table(name = "Phrase")
public class Phrase implements IPhrase {
   private Long key;
   private String description;
   private List<Sense> senseList = new ArrayList<Sense>(0);

   @Column(name = IPhrase.CONTENT)
   public String getDescription() {
      return description;
   }


   @OneToMany(targetEntity = Sense.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
   @JoinColumn(name = IPhrase.ID, referencedColumnName = IPhrase.ID)
   @ForeignKey(name = "FK_PHRASE_SENSES")
   public List<Sense> getSenseList() {
      return senseList;
   }

   @Column(name = IPhrase.ID)
   @GeneratedValue(strategy = GenerationType.AUTO)
   @Id
   public Long getKey() {
      return key;
   }

   public void setKey(Long value) {
      key = value;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setSenseList(List<Sense> senseList) {
      this.senseList = senseList;
   }

}
