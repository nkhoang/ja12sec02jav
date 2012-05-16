package com.nkhoang.model.dictionary;

import com.nkhoang.model.IDataObject;

public interface ISound extends IDataObject<Long> {
  public static final String ID = "soundKey";
  public static final String SOUND = "sound";
  public static final String DESCRIPTION = "description";

  byte[] getSound();

  void setSound(byte[] value);

  String getDescription();

  void setDescription(String value);
}
