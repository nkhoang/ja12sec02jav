package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.UserTagDao;
import com.nkhoang.gae.model.UserTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class UserTagDaoImpl extends BaseDaoImpl<UserTag, Long> implements UserTagDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTagDaoImpl.class);

    public String getClassName() {
        return UserTag.class.getName();
    }

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

}
