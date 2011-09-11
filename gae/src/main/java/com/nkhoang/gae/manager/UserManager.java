package com.nkhoang.gae.manager;

import com.nkhoang.gae.dao.UserDao;
import com.nkhoang.gae.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class UserManager implements BaseManager<User, Long> {
	private static final Log log = LogFactory.getLog(UserManager.class);

	// user dao.
	private UserDao userDao;
	// password encoder.
	private PasswordEncoder passwordEncoder;

	/**
	 * Finds a user by their username.
	 * 
	 * @param username
	 *            the user's username used to login
	 * @return User a populated user object
	 * @throws UsernameNotFoundException
	 *             exception thrown when user not found
	 */
	public User getUserByUsername(String username) throws UsernameNotFoundException {
		return (User) userDao.loadUserByUsername(username);
	}

	public List<User> listAll() {
		return userDao.getAll();
	}

	public boolean clearAll() {
		List<User> users = listAll();
		boolean result = false;
		for (User user : users) {
			try {
				userDao.delete(user.getId());
			} catch (Exception e) {
				log.error(e);
			}
		}

		int size = userDao.getAll().size();
		if (size == 0) {
			result = true;
		}

		return result;
	}

	public User save(User o) {
		return userDao.save(o);
	}

	public User update(User o) {
		return userDao.update(o);
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
