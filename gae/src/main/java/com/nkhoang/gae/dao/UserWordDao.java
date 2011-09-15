package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Role;
import com.nkhoang.gae.model.UserWord;

import java.util.List;

public interface UserWordDao extends BaseDao<UserWord, Long> {
    /**
     * Get all a list of entities showing the relationship between the user with <i>userId</i> and words.
     *
     * @param userId the user id to get the list of word relationship entities.
     * @return a list of word relationship entities.
     */
    List<UserWord> getWordFromUser(Long userId);

    /**
     * Save a new record to DS.
     *
     * @param wordId the word id.
     * @param userId the user id.
     * @return saved object.
     */
    UserWord save(Long wordId, Long userId);

    /**
     * Check if a relationship of a word and the current user is existing.
     *
     * @param userId the user id.
     * @param wordId the word id.
     * @return true if yes, false if no.
     */
    boolean checkExist(Long userId, Long wordId);

}
