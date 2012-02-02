package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.WordEntity;

import java.util.List;

public interface VocabularyDao extends BaseDao<WordEntity, Long> {
  WordEntity lookup(String word);

  WordEntity lookupByDict(String word, String dictType);

  List<WordEntity> getAllInRange(int offset, int size);

  List<WordEntity> getAllInRange(int offset, int size, String direction);
}
