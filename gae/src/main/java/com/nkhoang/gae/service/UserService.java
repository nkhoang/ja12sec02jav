package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Dictionary;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.UserWord;

import java.util.List;

/**
 * User related service.
 */
public interface UserService {
    List<String> getUserIdWordByDate(String date, int offset, Integer size);

    /**
     * Add a new word to the current user.
     *
     * @param wordId the word id.
     */
    UserWord addWord(Long wordId);


    User getCurrentUser();

	Dictionary addNewDictionary(String dictName, String dictDescription);
}
