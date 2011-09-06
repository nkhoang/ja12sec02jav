package com.nkhoang.gae.model;

import javax.persistence.*;

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
