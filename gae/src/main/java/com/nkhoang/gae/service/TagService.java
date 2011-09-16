package com.nkhoang.gae.service;

import com.nkhoang.gae.model.UserTag;
import com.nkhoang.gae.model.UserWord;
import com.nkhoang.gae.model.Word;

import java.util.List;

/** User related service. */
public interface TagService {
	List<Word> getAllWordsByTagName(String tagName);

	boolean save(String tagName, Long wordId);

	List<UserTag> getAllUserTags(Long userId);

}