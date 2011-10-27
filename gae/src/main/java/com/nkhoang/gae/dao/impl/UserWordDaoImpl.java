package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.UserWordDao;
import com.nkhoang.gae.model.UserWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Transactional
public class UserWordDaoImpl extends BaseDaoImpl<UserWord, Long> implements UserWordDao {
    private static final Logger LOG = LoggerFactory.getLogger(UserWordDaoImpl.class.getCanonicalName());

    public String getClassName() {
        return UserWord.class.getName();
    }

    public List<UserWord> getWordFromUser(Long userId) {
        Query query = entityManager.createQuery("Select from " + UserWord.class.getName() + " t where t.wordId=:wordId");
        query.setParameter("wordId", userId);

        List<UserWord> result = query.getResultList();
        return result;
    }


    public boolean checkExist(Long userId, Long wordId) {
        try {
            Query query = entityManager.createQuery("Select from " + UserWord.class.getName() + " t where t.wordId=:wordId and t.userId=:userId");
            query.setParameter("userId", userId);
            query.setParameter("wordId", wordId);
            UserWord userWord = (UserWord) query.getSingleResult();
            if (userWord != null) {
                LOG.debug("Check wordId-userId: [" + wordId + "-" + userId + "]...  yes");
                return true;
            }

        } catch (NoResultException e) {
        }
        LOG.debug("Check wordId-userId: [" + wordId + "-" + userId + "]...  no");
        return false;
    }


    public List<UserWord> getRecentUserWords(Long startDate, Long endDate, int offset, Integer size) {
        LOG.info(String.format("Get user's recent words by date [%s-%s]..", startDate, endDate));
        List<UserWord> result = null;
        try {
            Query query = getEntityManager().createQuery("Select from " + UserWord.class.getName() + " t where t.time >=:startTime and t.time <=:endTime order by t.time desc");
            query.setParameter("startTime", startDate);
            query.setParameter("endTime", endDate);
            query.setFirstResult(offset);
            if (size != null) {
                query.setMaxResults(size);
            }
            result = query.getResultList();
        } catch (NoResultException ex) {
        }
        return result;
    }

    public UserWord save(Long wordId, Long userId) {
        if (!checkExist(userId, wordId)) {
            UserWord userWord = new UserWord();
            userWord.setUserId(userId);
            userWord.setWordId(wordId);
            userWord.setTime(System.currentTimeMillis());

            return save(userWord);
        }
        return null;
    }

}
