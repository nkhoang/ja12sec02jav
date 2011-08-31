package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.UserDao;
import com.nkhoang.gae.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class UserDaoImpl extends GeneralDaoImpl<User, Long> implements UserDao, UserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    public User get(Long id) {
        LOGGER.debug("Get user ID: " + id);
        Query query = entityManager.createQuery("Select from " + User.class.getName() + " t where t.id=:userID");
        query.setParameter("userID", id);

        User user = (User) query.getSingleResult();
        if (user != null) {
            return user;
        }

        return null;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getUserPassword(String username) {
        String password = null;
        try {
            Query query = entityManager.createQuery("Select u.password from " + User.class.getName()
                    + " u where u.username = :username");
            query.setParameter("username", username.trim());

            List<String> result = query.getResultList();
            if (result != null && result.size() > 0) {
                password = result.get(0);
            }
        } catch (Exception ex) {
            LOGGER.error("Error", ex);
        }

        return password;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Query query = entityManager
                .createQuery("select from " + User.class.getName() + " u where u.username=:username");
        query.setParameter("username", username.trim());

        List<User> result = query.getResultList();
        if (result != null && result.size() > 0) {
            return result.get(0);
        } else {
            throw new UsernameNotFoundException("user '" + username + "' not found...");
        }
    }

    public List<User> getAll() {
        Query query = getEntityManager().createQuery("Select from " + User.class.getName());
        List<User> users = new ArrayList<User>(0);
        List result = query.getResultList();
        for (Object aResult : result) {
            users.add((User) aResult);
        }
        return users;
    }

    public boolean delete(Long id) {
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + User.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
        return result;
    }

}
