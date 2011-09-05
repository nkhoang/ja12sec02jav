package com.nkhoang.gae.model;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: 9/3/11
 * Time: 9:44 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class WordItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Basic
    private String word;
    @Basic
    private String haveEnglish;
    @Basic
    private String haveVietnamese;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getHaveEnglish() {
        return haveEnglish;
    }

    public void setHaveEnglish(String haveEnglish) {
        this.haveEnglish = haveEnglish;
    }

    public String getHaveVietnamese() {
        return haveVietnamese;
    }

    public void setHaveVietnamese(String haveVietnamese) {
        this.haveVietnamese = haveVietnamese;
    }
}
