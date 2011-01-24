package com.nkhoang.model;

import org.apache.commons.lang.StringEscapeUtils;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: 1/23/11
 * Time: 9:12 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "Example")
public class Example {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SearchableId
    private Long exampleId;
    @Column(nullable = false)
    @SearchableProperty
    private String content;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meaningId")
    private Meaning meaning;

    public Example(){

    }

    public Example(String ex) {
        this.content = ex;
    }

    public Long getExampleId() {
        return exampleId;
    }

    public void setExampleId(Long id) {
        this.exampleId = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = StringEscapeUtils.escapeSql(content);
    }

    public Meaning getMeaning() {
        return meaning;
    }

    public void setMeaning(Meaning meaning) {
        this.meaning = meaning;
    }
}
