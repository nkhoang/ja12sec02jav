package com.nkhoang.model;

import org.apache.commons.lang.StringEscapeUtils;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Word meaning can have 1 or more examples
 *
 * @author hoangnk
 */
@Entity
@Table(name = "Meaning")
@Searchable
public class Meaning {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SearchableId
    private Long meaningId;
    @Column(nullable = true)
    @SearchableProperty
    private String content;
    @Column(nullable = true)
    @SearchableProperty
    private Long kindId;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "meaning")
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE})
    private Set<Example> examples = new HashSet<Example>(0);
    @Column(nullable = true)
    private String type;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wordId", referencedColumnName = "wordId", updatable = true)
    private Word word;

    public Meaning(String content, Long kindId) {
        this.content = content;
        this.kindId = kindId;
    }

    public Meaning() {

    }


    public Set<Example> getExamples() {
        return this.examples;
    }

    public void addExample(String ex) {
        Example example = new Example(StringEscapeUtils.escapeSql(ex));
        example.setMeaning(this);
        this.examples.add(example);
    }

    @Override
    public String toString() {
        return content + "\n" + examples.toString();
    }


    public Long getMeaningId() {
        return meaningId;
    }

    public void setMeaningId(Long id) {
        this.meaningId = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = StringEscapeUtils.escapeXml(content);
    }


    public void setExamples(Set<Example> examples) {
        this.examples = examples;
    }

    public void setKindId(Long kindId) {
        this.kindId = kindId;
    }


    public Long getKindId() {
        return kindId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}
