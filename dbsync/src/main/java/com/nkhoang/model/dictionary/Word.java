package com.nkhoang.model.dictionary;

import com.nkhoang.model.ITrackableObject;
import com.nkhoang.model.dictionary.impl.WordImpl;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity(name = "IWord")
@Table(name = "Word")
public class Word extends WordImpl {
  @Column(name = IWord.ID)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long getKey() {
    return super.getKey();
  }

  @Column(name = IWord.DATA, columnDefinition = "TEXT")
  public String getData() {
    return super.getData();
  }



  @OneToOne(targetEntity = Dictionary.class, fetch = FetchType.LAZY)
  @JoinColumn(name = IDictionary.ID, referencedColumnName = IDictionary.ID)
  public IDictionary getDictionary() {
    return super.getDictionary();
  }

  @Column(name = ITrackableObject.CREATION_DATE, nullable = true)
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getCreationDate() {
    return super.getCreationDate();
  }

  @Column(name = ITrackableObject.MODIFICATION_DATE, nullable = true)
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  public DateTime getModificationDate() {
    return super.getModificationDate();
  }
}

