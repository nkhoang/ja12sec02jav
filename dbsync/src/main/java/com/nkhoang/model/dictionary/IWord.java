package com.nkhoang.model.dictionary;

import com.nkhoang.model.IDataObject;
import com.nkhoang.model.ITrackableObject;

public interface IWord extends IDataObject<Long>, ITrackableObject {
  public static final String ID = "wordKey";

  public static final String DATA = "data";

  IDictionary getDictionary();

  void setDictionary(IDictionary value);
}
