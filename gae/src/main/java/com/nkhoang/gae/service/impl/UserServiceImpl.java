package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.UserWordDao;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.UserWord;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.UserService;
import com.nkhoang.gae.utils.WebUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserServiceImpl implements UserService {
    private static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class.getCanonicalName());
    @Autowired
    private VocabularyDao vocabularyDao;
    @Autowired
    private UserWordDao userWordDao;


    public List<String> getUserIdWordByDate(String date, int offset, Integer size) {
        List<String> wordList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(date)) {
            try {
                Date startDate = WebUtils.parseDate(date + " 00:00:01", WebUtils.DEFAULT_CLIENT_DATE_FORMAT + " HH:mm:ss");
                Date endDate = WebUtils.parseDate(date + " 23:59:59", WebUtils.DEFAULT_CLIENT_DATE_FORMAT + " HH:mm:ss");
                List<UserWord> userWords = userWordDao.getRecentUserWords(startDate.getTime(), endDate.getTime(), offset, size);
                if (CollectionUtils.isNotEmpty(userWords)) {
                    List<Word> words = new ArrayList<Word>();
                    for (UserWord userWord : userWords) {
                        words.add(vocabularyDao.get(userWord.getWordId()));
                    }

                    if (CollectionUtils.isNotEmpty(words)) {
                        for (Word w : words) {
                            wordList.add(w.getDescription());
                        }
                    }
                }
            } catch (ParseException pare) {
                LOG.error(String.format("Could not parse date %s with format [%s]", date, WebUtils.DEFAULT_CLIENT_DATE_FORMAT));
            }
        }
        return wordList;
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = null;
        if (principal != null) {
            currentUser = (User) principal;
        }

        return currentUser;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public UserWord addWord(Long wordId) {
        // check word existence.
        Word w = vocabularyDao.get(wordId);
        if (w != null) {
            // ok to proceed.
            User currentUser = getCurrentUser();
            if (currentUser != null) {
                return userWordDao.save(wordId, currentUser.getId());
            }
        }
        return null;
    }


    public void setVocabularyDao(VocabularyDao vocabularyDao) {
        this.vocabularyDao = vocabularyDao;
    }

    public void setUserWordDao(UserWordDao userWordDao) {
        this.userWordDao = userWordDao;
    }
}
