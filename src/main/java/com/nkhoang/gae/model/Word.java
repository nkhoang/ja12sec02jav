package com.nkhoang.gae.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"JpaAttributeTypeInspection"})
@Entity
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
    private final Map<String, Long> kindIdMap = new HashMap<String, Long>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic
    private String pron;
    @Basic
    private String soundSource;
    @Basic
    private List<Long> meaningIds = new ArrayList<Long>(0);

    @Basic
    private String description;
    @Transient
    private final Map<Long, List<Meaning>> meanings = new HashMap<Long, List<Meaning>>(0);
    @Transient
    private List<Long> kindIdList = new ArrayList();
    public static final String SKIP_FIELDS[] = {"jdoDetachedState", "kindIdMap", "meaningIds"};
    @Basic
    private Long timeStamp;

    {
        try {
            WORD_KIND_NOUN = new String(NOUN_BYTES, "UTF-8");
            WORD_KIND_VERB_IN = new String(VERB_IN, "UTF-8");
            WORD_KIND_VERB_TR = new String(VERB_TR, "UTF-8");
            WORD_KIND_ADV = new String(VERB_ADV, "UTF-8");
            WORD_KIND_ADJ = new String(VERB_ADJ, "UTF-8");
            WORD_KIND_VERB = new String(VERB_BYTES, "UTF-8");

            WORD_KINDS = new String[] {WORD_KIND_VERB, WORD_KIND_ADJ, WORD_KIND_NOUN, WORD_KIND_VERB_IN, WORD_KIND_VERB_TR,
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

    public void addMeaning(Long kind, Meaning meaning) {
        List<Meaning> meaningList = this.meanings.get(kind);
        if (meaningList == null) {
            meaningList = new ArrayList<Meaning>(0);
            this.meanings.put(kind, meaningList);
            this.kindIdList.add(kind);
        }
        this.meanings.get(kind).add(meaning);
    }

    public void addMeaningId(Long meaningId) {
        this.getMeaningIds().add(meaningId);
    }

    public void addKind(Long kind) {
        this.meanings.put(kind, new ArrayList<Meaning>(0));
        this.kindIdList.add(kind);
    }

    public List<Meaning> getMeaning(Long kind) {
        return this.meanings.get(kind);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getMeaningIds() {
        return meaningIds;
    }

    public void setMeaningIds(List<Long> meaningIds) {
        this.meaningIds = meaningIds;
    }

    public Map<String, Long> getKindidmap() {
        return kindIdMap;
    }

    public Map<Long, List<Meaning>> getMeanings() {
        return this.meanings;
    }

    public void setKindIdList(List<Long> kindIdList) {
        this.kindIdList = kindIdList;
    }

    public List<Long> getKindIdList() {
        return kindIdList;
    }

    public void setPron(String pron) {
        this.pron = pron;
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
}
