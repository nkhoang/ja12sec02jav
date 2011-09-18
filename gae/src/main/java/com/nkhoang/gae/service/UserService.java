package com.nkhoang.gae.service;

import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.UserWord;
import com.nkhoang.gae.model.Word;

import java.util.List;
import java.util.Map;

/**
 * User related service.
 */
public interface UserService {
    List<String> getUserIdWordByDate(String date, int offset, int size);

    /**
     * Add a new word to the current user.
     *
     * @param wordId the word id.
     */
    UserWord addWord(Long wordId);


    User getCurrentUser();
}
