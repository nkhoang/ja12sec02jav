package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Word;

public interface VocabularyDao extends BaseDao<Word, Long> {
	public Word lookup(String word);
    boolean find(String w);
}
