package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.WordTag;

import java.util.List;

public interface WordTagDao extends BaseDao<WordTag, Long> {
    WordTag save(Long wordId, Long userTagId);

    WordTag get(Long wordId, Long userTagId);

    List<WordTag> getAllWords(Long userTagId);

}
