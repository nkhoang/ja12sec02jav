package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Word;

import java.util.List;

public interface VocabularyDao extends BaseDao<Word, Long> {
  public Word lookup(String word);

  public List<Word> get(List<Long> ids);

  public List<Word> getAllInRange(int offset, int size);
}
