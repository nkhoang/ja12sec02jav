package com.nkhoang.gae.service;

import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.UserWord;
import com.nkhoang.gae.model.Word;

import java.util.List;

/**
 * User related service.
 */
public interface UserService {
    /**
     * Add a new word to the current user.
     *
     * @param wordId the word id.
     */
    UserWord addWord(Long wordId);

    /**
     * Get all words with the current user.
     *
     * @return the list of user words.
     */
    List<Word> getWordsFromUser();

    User getCurrentUser();
}
