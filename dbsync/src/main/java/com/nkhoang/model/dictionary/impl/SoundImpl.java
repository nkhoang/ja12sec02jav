package com.nkhoang.model.dictionary.impl;

import com.nkhoang.model.dictionary.ISound;

public class SoundImpl implements ISound {
   private Long id;
   private byte[] sound;

   public byte[] getSound() {
      return sound;
   }

   public void setSound(byte[] sound) {
      this.sound = sound;
   }

   public Long getKey() {
      return id;
   }

   public void setKey(Long key) {
      id = key;
   }
}
