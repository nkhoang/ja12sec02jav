package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.UserWordDao;
import com.nkhoang.gae.model.UserWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@Transactional
public class UserWordDaoImpl extends GeneralDaoImpl<UserWord, Long> implements UserWordDao {
    private static final Logger LOG = LoggerFactory.getLogger(UserWordDaoImpl.class.getCanonicalName());

    public List<UserWord> getWordFromUser(Long userId) {
        Query query = entityManager.createQuery("Select from " + UserWord.class.getName() + " t where t.wordId=:wordId");
        query.setParameter("wordId", userId);

        List<UserWord> result = query.getResultList();
        return result;
    }

    public UserWord get(Long id) {
        try {
            LOG.debug("Get userword ID: " + id);
            Query query = entityManager.createQuery("Select from " + UserWord.class.getName() + " t where t.id=:userwordID");
            query.setParameter("userwordID", id);

            UserWord userWord = (UserWord) query.getSingleResult();
            if (userWord != null) {
                return userWord;
            }
        } catch (NoResultException e) {
        }
        return null;
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


    public List<UserWord> getAll() {
        List<UserWord> result = null;
        try {
            Query query = getEntityManager().createQuery("Select from " + UserWord.class.getName());
            result = query.getResultList();
        } catch (NoResultException ex) {
        }
        return result;
    }


    public List<UserWord> getRecentUserWords(Long startDate, Long endDate, int offset, int size) {
        LOG.info(String.format("Get user's recent words by date [%s-%s]..", startDate, endDate));
        List<UserWord> result = null;
        try {
            Query query = getEntityManager().createQuery("Select from " + UserWord.class.getName() + " t where t.time >=:startTime and t.time <=:endTime order by t.time desc");
            query.setParameter("startTime", startDate);
            query.setParameter("endTime", endDate);
            query.setFirstResult(offset);
            query.setMaxResults(size);
            result = query.getResultList();
        } catch (NoResultException ex) {
        }
        return result;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public boolean delete(Long id) {
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + UserWord.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOG.error("Error", e);
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
