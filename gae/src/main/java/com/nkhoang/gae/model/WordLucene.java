package com.nkhoang.gae.model;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.BitSet;

@Entity
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(name = "wordLucene", propOrder = {
        "id",
        "word"
})
@XmlSeeAlso({javax.jdo.identity.LongIdentity.class, BitSet.class})
@XmlRootElement
public class WordLucene implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic
    private String word;

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
}
