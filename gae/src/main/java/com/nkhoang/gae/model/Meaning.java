package com.nkhoang.gae.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Word meaning can have 1 or more examples
 *
 * @author hoangnk
 */

@SuppressWarnings({"JpaAttributeTypeInspection"})
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "meaning", propOrder = {
        "content",
        "examples",
        "id",
        "kind",
        "kindId",
        "type",
        "jdoDetachedState"
})
@XmlRootElement
public class Meaning {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic
    private String content;
    @Basic
    private Long kindId;
    @Basic
    private List<String> examples = new ArrayList<String>(0);
    private String kind;
    @Basic
    private String type;
    public static final String SKIP_FIELDS[] = {"jdoDetachedState"};

    public Meaning(String content, Long kindId) {
        this.content = content;
        this.kindId = kindId;
    }

    public Meaning() {

    }

    public void addExample(String example) {
        this.examples.add(example);
    }

    public List<String> getExamples() {
        return this.examples;
    }

    @Override
    public String toString() {
        return content + "\n" + examples.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }

    public void setKindId(Long kindId) {
        this.kindId = kindId;
    }

    public Long getKindId() {
        return kindId;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
