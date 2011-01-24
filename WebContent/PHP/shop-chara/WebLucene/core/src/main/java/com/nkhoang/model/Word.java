package com.nkhoang.model;

import org.apache.commons.lang.StringEscapeUtils;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Word")
@Searchable
public class Word {
    public static final byte[] NOUN_BYTES = {100, 97, 110, 104, 32, 116, -31, -69, -85};
    private static final byte[] VERB_IN = {110, -31, -69, -103, 105, 32, -60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    private static final byte[] VERB_TR = {110, 103, 111, -31, -70, -95, 105, 32, -60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    private static final byte[] VERB_BYTES = {-60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    private static final byte[] VERB_ADJ = {116, -61, -83, 110, 104, 32, 116, -31, -69, -85};
    private static final byte[] VERB_ADV = {112, 104, -61, -77, 32, 116, -31, -69, -85};
    public static String WORD_KIND_NOUN = "";
    public static String WORD_KIND_VERB_IN = "";
    public static String WORD_KIND_VERB_TR = "";
    public static String WORD_KIND_ADV = "";
    public static String WORD_KIND_ADJ = "";
    public static String WORD_KIND_VERB = "";
    public static final String WORD_KIND_NOUN_EN = "noun";
    public static final String WORD_KIND_VERB_EN = "verb";
    public static final String WORD_KIND_ADJ_EN = "adjective";
    public static final String WORD_KIND_ADV_EN = "adverb";
    public static String[] WORD_KINDS = {};
    @Transient
    private final Map<String, Long> kindIdMap = new HashMap<String, Long>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SearchableId
    private Long wordId;
    @Column(nullable = true)
    @SearchableProperty
    private String pron;
    @Column(nullable = true)
    @SearchableProperty
    private String soundSource;
    @Transient
    private Set<Meaning> meanings = new HashSet<Meaning>(0);
    @Column(nullable = true)
    @SearchableProperty
    private String description;
    @Column(nullable = true)
    @SearchableProperty
    private String content;
    @Transient
    private Map<Long, List<Meaning>> meaningMap = new HashMap<Long, List<Meaning>>(0);

    public static final String SKIP_FIELDS[] = {"jdoDetachedState", "kindIdMap", "meaningIds", "timeStamp"};
    @Column(name = "timestamp", nullable = true)
    @SearchableProperty
    private Long timeStamp;

    {
        try {
            WORD_KIND_NOUN = new String(NOUN_BYTES, "UTF-8");
            WORD_KIND_VERB_IN = new String(VERB_IN, "UTF-8");
            WORD_KIND_VERB_TR = new String(VERB_TR, "UTF-8");
            WORD_KIND_ADV = new String(VERB_ADV, "UTF-8");
            WORD_KIND_ADJ = new String(VERB_ADJ, "UTF-8");
            WORD_KIND_VERB = new String(VERB_BYTES, "UTF-8");

            WORD_KINDS = new String[]{WORD_KIND_VERB, WORD_KIND_ADJ, WORD_KIND_NOUN, WORD_KIND_VERB_IN, WORD_KIND_VERB_TR,
                    WORD_KIND_ADV, WORD_KIND_ADV_EN, WORD_KIND_ADJ_EN, WORD_KIND_NOUN_EN, WORD_KIND_VERB_EN};

        } catch (Exception e) {
        }
    }

    public Word() {
        // init
        int i = 0;
        for (String kind : WORD_KINDS) {
            getKindidmap().put(kind, Long.parseLong(i + ""));
            i++;
        }
    }

    public void addMeaning(Meaning meaning) {
        //meaning.setWord(this);
        this.meanings.add(meaning);
    }


    public void addKind(Long kind) {
        this.meaningMap.put(kind, new ArrayList<Meaning>(0));

    }


    public List<Meaning> getMeaningMap(Long kind) {
        return this.meaningMap.get(kind);
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long id) {
        this.wordId = id;
    }


    @Transient
    public Map<String, Long> getKindidmap() {
        return kindIdMap;
    }





    public void setPron(String pron) {
        this.pron = StringEscapeUtils.escapeSql(pron);
    }


    public String getPron() {
        return pron;
    }


    public void setSoundSource(String soundSource) {
        this.soundSource = soundSource;
    }

    public String getSoundSource() {
        return soundSource;
    }

    public String toString() {
        return "Word: " + description + " with sound [" + soundSource + "]";
    }


    public Long getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public Set<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(Set<Meaning> meanings) {
        this.meanings = meanings;
    }


    public Map<Long, List<Meaning>> getMeaningMap() {
        return meaningMap;
    }

    public void setMeaningMap(Map<Long, List<Meaning>> meaningMap) {
        this.meaningMap = meaningMap;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = StringEscapeUtils.escapeSql(content);
    }
}
