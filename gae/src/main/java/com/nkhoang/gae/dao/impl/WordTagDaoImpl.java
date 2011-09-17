package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.WordTagDao;
import com.nkhoang.gae.model.WordTag;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class WordTagDaoImpl extends GeneralDaoImpl<WordTag, Long> implements WordTagDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordTagDaoImpl.class);

    public List<Long> getTagsByWord(Long wordId, Long userId) {
        try {
            Query query = entityManager.createQuery("Select from " + WordTag.class.getName()
                    + " t where t.wordId=:wordId and t.userId=:userId");
            query.setParameter("wordId", wordId);
            query.setParameter("userId", userId);

            List<WordTag> result = query.getResultList();
            if (CollectionUtils.isNotEmpty(result)) {
                List<Long> userTagIds = new ArrayList<Long>();
                for (WordTag wordTag : result) {
                    userTagIds.add(wordTag.getUserTagId());
                }
	            return userTagIds;
            }

        } catch (NoResultException nre) {
        }
        return null;
    }

    public boolean delete(Long wordId, Long userTagId) {
        WordTag wordTag = get(wordId, userTagId);
        if (wordTag != null) {
            return delete(wordTag.getId());
        }
        return false;
    }

    public WordTag get(Long wordId, Long userTagId) {
        try {
            Query query = entityManager.createQuery("Select from " + WordTag.class.getName()
                    + " t where t.wordId=:wordId and t.userTagId=:userTagId");
            query.setParameter("wordId", wordId);
            query.setParameter("userTagId", userTagId);

            WordTag userTag = (WordTag) query.getSingleResult();

            return userTag;
        } catch (NoResultException nre) {
        }
        return null;
    }

    public WordTag save(Long wordId, Long userTagId, Long userId) {
        if (get(wordId, userTagId) == null) {
            WordTag wordTag = new WordTag();
            wordTag.setTime(System.currentTimeMillis());
            wordTag.setUserTagId(userTagId);
            wordTag.setWordId(wordId);
            wordTag.setUserId(userId);

            return save(wordTag);
        }
        return null;
    }

    public List<WordTag> getAllWords(Long userTagId) {
        try {
            Query query = entityManager.createQuery("Select from " + WordTag.class.getName()
                    + " t where t.userTagId=:userTagId");
            query.setParameter("userTagId", userTagId);

            List<WordTag> wordTags = query.getResultList();

            return wordTags;
        } catch (NoResultException nre) {
        }
        return null;
    }

    public boolean delete(Long id) {
        LOGGER.info("Delete wordTag with [id:" + id + "].");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + WordTag.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete wordTag with [id:" + id + "].");
            LOGGER.error("Error", e);
        }
        return result;
    }

    public WordTag get(Long id) {
        try {
            Query query = entityManager.createQuery("Select from " + WordTag.class.getName()
                    + " t where t.id=:wordTagId");
            query.setParameter("wordTagId", id);

            WordTag userTag = (WordTag) query.getSingleResult();
            if (userTag != null) {
                return userTag;
            }
        } catch (NoResultException e) {
        }
        return null;
    }

    public List<WordTag> getAll() {
        LOGGER.info("Get all wordTags ...");
        List<WordTag> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + WordTag.class.getName());
            result = query.getResultList();

        } catch (NoResultException ex) {
        }
        return result;
    }
}
