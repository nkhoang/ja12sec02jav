package com.nkhoang.model.dictionary;

import com.nkhoang.model.dictionary.impl.SoundImpl;

import javax.persistence.*;

@Entity(name = "ISound")
@Table(name = "Sound")
public class Sound extends SoundImpl {
  @Column(name = ISound.ID)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long getKey() {
    return super.getKey();
  }

  @Lob
  @Column(name = ISound.SOUND)
  public byte[] getSound() {
    return super.getSound();
  }

  @Column(nullable = false, name = ISound.DESCRIPTION)
  public String getDescription() {
    return super.getDescription();
  }
}
