package com.nkhoang.model;

import org.joda.time.DateTime;

public interface ITrackableObject {
  public static final String CREATION_DATE = "creationDate";
  public static final String MODIFICATION_DATE = "modificationDate";

  public DateTime getCreationDate();

  public void setCreationDate(DateTime value);

  public DateTime getModificationDate();

  public void setModificationDate(DateTime value);
}
