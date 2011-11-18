package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * User dao.
 *
 * @author hnguyen93
 */
public interface UserDao extends BaseDao<User, Long> {
    /**
     * Gets users information based on login name.
     *
     * @param username the user's username
     * @return userDetails populated userDetails object
     * @throws UsernameNotFoundException thrown when user not found in database
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Retrieves the password in DB for a user
     *
     * @param username the user's username
     * @return the password in DB, if the user's already persisted
     */
    String getUserPassword(String username);

    boolean checkEmail(String email);

    boolean checkUsername(String username);
}
