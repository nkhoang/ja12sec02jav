package com.nkhoang.gae.model;

import javax.persistence.*;

@Entity
public class AppConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic
    private String label;
    @Basic
    private String value;

    public static final String[] SKIP_FIELDS = {"jdoDetachedState"};


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
