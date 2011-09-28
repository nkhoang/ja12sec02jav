package com.nkhoang.model;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.BitSet;


@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(name = "wordLucene", propOrder = {
        "id",
        "word"
})

@XmlRootElement
public class WordLucene implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
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
