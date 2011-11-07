package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.UserTagDao;
import com.nkhoang.gae.dao.WordTagDao;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.UserTag;
import com.nkhoang.gae.model.WordTag;
import com.nkhoang.gae.service.TagService;
import com.nkhoang.gae.service.UserService;
import com.nkhoang.gae.service.VocabularyService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public class TagServiceImpl implements TagService {
    private UserService userService;
    private VocabularyService vocabularyService;
    private WordTagDao wordTagDao;
    private UserTagDao userTagDao;

    @PreAuthorize("hasRole('ROLE_USER')")
    public WordTag save(String tagName, Long wordId) {
        if (StringUtils.isNotBlank(tagName)) {
            User currentUser = userService.getCurrentUser();
            UserTag userTag = userTagDao.save(currentUser.getId(), tagName);
            if (userTag != null) {
                WordTag wordTag = wordTagDao.save(wordId, userTag.getId(), currentUser.getId());
                return wordTag;
            }
        }
        return null;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public boolean delete(Long userTagId, Long wordId) {
        return wordTagDao.delete(wordId, userTagId);
    }

    public List<UserTag> getTagsByWord(Long wordId) {
        User user = userService.getCurrentUser();
        if (user != null) {
            List<Long> userTagIds = wordTagDao.getTagsByWord(wordId, user.getId());
            if (CollectionUtils.isNotEmpty(userTagIds)) {
                return userTagDao.getAll(userTagIds);
            }
        }
        return null;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public List<UserTag> getAllUserTags(Long userId) {
        User currentUser = userService.getCurrentUser();
        return userTagDao.getAllUserTags(userId);
    }

    public void setWordTagDao(WordTagDao wordTagDao) {
        this.wordTagDao = wordTagDao;
    }

    public void setUserTagDao(UserTagDao userTagDao) {
        this.userTagDao = userTagDao;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }
}
