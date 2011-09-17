package com.nkhoang.gae.service;

import com.nkhoang.gae.model.UserTag;
import com.nkhoang.gae.model.UserWord;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.model.WordTag;

import java.util.List;

/**
 * User related service.
 */
public interface TagService {
    List<Word> getAllWordsByTagName(String tagName);

    boolean delete(Long userTagId, Long wordId);

    WordTag save(String tagName, Long wordId);

    List<UserTag> getAllUserTags(Long userId);

    List<UserTag> getTagsByWord(Long wordId);


}