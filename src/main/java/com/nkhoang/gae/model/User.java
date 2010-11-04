package com.nkhoang.gae.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"JpaAttributeTypeInspection"})
@Entity
public class User implements Serializable, UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@Basic
	private String firstName;
	@Basic
	private String lastName;
	@Basic
	private String username;
	@Basic
	private String password;
	@Basic
	private List<String> roleNames;
	// Spring required properties
	@Basic
	private boolean enabled;
	@Basic
	private List<Long> wordList = new ArrayList<Long>();
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialExpired;

	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(0);

		for (String s : roleNames) {
			authorities.add(new GrantedAuthorityImpl(s));
		}

		return authorities;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {
		return !accountLocked;
	}

	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	public boolean isCredentialsNonExpired() {
		return !credentialExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public List<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	public Long getId() {
		return id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setWordList(List<Long> wordList) {
		this.wordList = wordList;
	}

	public List<Long> getWordList() {
		return wordList;
	}

}
