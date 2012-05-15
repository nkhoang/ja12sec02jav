package com.nkhoang.model.dictionary;

import com.nkhoang.model.IDataObject;

public interface ISound extends IDataObject<Long> {
   public static final String ID = "soundKey";
   public static final String SOUND = "sound";

   byte[] getSound();

   void setSound(byte[] value);
}
