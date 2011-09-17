package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.UserTagDao;
import com.nkhoang.gae.model.UserTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class UserTagDaoImpl extends GeneralDaoImpl<UserTag, Long> implements UserTagDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTagDaoImpl.class);

    public boolean checkExist(Long userId, String tagName) {
        try {
            Query query = entityManager.createQuery("Select from " + UserTag.class.getName()
                    + " t where t.userId=:userId and t.tagName=:tagName");
            query.setParameter("userId", userId);
            query.setParameter("tagName", tagName.toLowerCase());

            UserTag userTag = (UserTag) query.getSingleResult();
            if (userTag != null) {
                return true;
            }
        } catch (NoResultException e) {
        }
        return false;
    }

    public UserTag save(Long userId, String tagName) {
        UserTag result = get(userId, tagName);
        if (result == null) {
            UserTag userTag = new UserTag();
            userTag.setTime(System.currentTimeMillis());
            userTag.setUserId(userId);
            userTag.setTagName(tagName);
            result = save(userTag);
        }

        return result;
    }

    public List<UserTag> getAllUserTags(Long userId) {
        try {
            Query query = entityManager.createQuery("Select from " + UserTag.class.getName()
                    + " t where t.userId=:userId");
            query.setParameter("userId", userId);

            List<UserTag> userTags = query.getResultList();
            return userTags;
        } catch (NoResultException e) {
        }
        return null;
    }

    public boolean delete(Long id) {
        LOGGER.info("Delete userTag with [id:" + id + "].");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + UserTag.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete userTag with [id:" + id + "].");
            LOGGER.error("Error", e);
        }
        return result;
    }

    public UserTag get(Long id) {
        try {
            Query query = entityManager.createQuery("Select from " + UserTag.class.getName()
                    + " t where t.id=:userTagId");
            query.setParameter("userTagId", id);

            UserTag userTag = (UserTag) query.getSingleResult();
            if (userTag != null) {
                return userTag;
            }
        } catch (NoResultException e) {
        }
        return null;
    }

    public List<UserTag> getAll(List<Long> ids) {
        List<UserTag> userTags = new ArrayList<UserTag>();
        for (Long id : ids) {
            UserTag userTag = get(id);
            if (userTag != null) {
                userTags.add(userTag);
            }
        }
        return userTags;
    }


    public UserTag get(Long userId, String tagName) {
        try {
            Query query = entityManager.createQuery("Select from " + UserTag.class.getName()
                    + " t where t.userId=:userId and t.tagName=:tagName");
            query.setParameter("userId", userId);
            query.setParameter("tagName", tagName.toLowerCase());

            UserTag userTag = (UserTag) query.getSingleResult();
            return userTag;
        } catch (NoResultException nre) {
        }
        return null;
    }

    public List<UserTag> getAll() {
        LOGGER.info("Get all userTags ...");
        List<UserTag> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + UserTag.class.getName());
            result = query.getResultList();

        } catch (NoResultException ex) {
        }
        return result;
    }
}
