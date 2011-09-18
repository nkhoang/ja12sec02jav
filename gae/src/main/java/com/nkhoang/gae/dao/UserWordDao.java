package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.UserWord;

import java.util.List;

public interface UserWordDao extends BaseDao<UserWord, Long> {

    /**
     * Get the list of userWord entities ranging from <i>startDate</i> to <i>endDate</i>
     *
     * @param startDate the start Date in ms.
     * @param endDate   the end Date in ms.
     * @return a list of userword entities.
     */

    List<UserWord> getRecentUserWords(Long startDate, Long endDate, int offset, int size);

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
