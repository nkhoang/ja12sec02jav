package com.nkhoang.gae.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
    private String middleName;
    @Basic
    private String username;
    @Basic
    private String password;
    @Basic
    private String email;
    @Basic
    private String phoneNumber;
    @Basic
    private Date birthDate;
    @Basic
    private CustomerGender gender;
    @Basic
    private Long personalId;
    @Basic
    private PersonalIdType personalIdType;
    @Basic
    private String issuePlace;
    @Basic
    private Date issueDate;
    @Basic
    private float customerValue;

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

    public enum PersonalIdType {
        CIVIL,
        VISA
    }

    public enum CustomerGender {
        MALE,
        FEMALE
    }

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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public CustomerGender getGender() {
        return gender;
    }

    public void setGender(CustomerGender gender) {
        this.gender = gender;
    }

    public Long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(Long personalId) {
        this.personalId = personalId;
    }

    public PersonalIdType getPersonalIdType() {
        return personalIdType;
    }

    public void setPersonalIdType(PersonalIdType personalIdType) {
        this.personalIdType = personalIdType;
    }

    public String getIssuePlace() {
        return issuePlace;
    }

    public void setIssuePlace(String issuePlace) {
        this.issuePlace = issuePlace;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public boolean isCredentialExpired() {
        return credentialExpired;
    }

    public void setCredentialExpired(boolean credentialExpired) {
        this.credentialExpired = credentialExpired;
    }

    public float getCustomerValue() {
        return customerValue;
    }

    public void setCustomerValue(float customerValue) {
        this.customerValue = customerValue;
    }
}
