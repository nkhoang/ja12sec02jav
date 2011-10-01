package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Word;

import java.util.List;

public interface VocabularyDao extends BaseDao<Word, Long> {
  List<Word> lookup(String word);

  List<Word> getAllInRange(int offset, int size);

  List<Word> getAllInRange(int offset, int size, String direction);
}
