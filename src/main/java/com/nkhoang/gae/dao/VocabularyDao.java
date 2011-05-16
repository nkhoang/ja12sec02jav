package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Word;

import java.util.List;

public interface VocabularyDao extends BaseDao<Word, Long> {
    public Word lookup(String word);

    public Word find(String w);

    public List<Word> getAllInRange(int offset, int size);
}
