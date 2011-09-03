package com.nkhoang.gae.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

/**
 * Word entity.
 */
@SuppressWarnings({"JpaAttributeTypeInspection"})
@Entity
public class Word {
    // is used for detecting vietnamese kind of word.
    public static final byte[] NOUN_BYTES = {100, 97, 110, 104, 32, 116, -31, -69, -85};
    private static final byte[] VERB_IN = {110, -31, -69, -103, 105, 32, -60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    private static final byte[] VERB_TR = {110, 103, 111, -31, -70, -95, 105, 32, -60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    private static final byte[] VERB_BYTES = {-60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    private static final byte[] VERB_ADJ = {116, -61, -83, 110, 104, 32, 116, -31, -69, -85};
    private static final byte[] VERB_ADV = {112, 104, -61, -77, 32, 116, -31, -69, -85};

    public static final String SKIP_FIELDS[] = {"jdoDetachedState", "kindIdMap", "meaningIds", "timeStamp", "meanings"};

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
    @XmlTransient
    private String currentTime;
    @Transient
    @XmlTransient
    private Map<Long, List<Meaning>> meaningMap = new HashMap<Long, List<Meaning>>(0);
    @Transient
    @XmlTransient
    private List<Meaning> meanings = new ArrayList<Meaning>();
    @Transient
    @XmlTransient
    private List<Long> kindIdList = new ArrayList();
    @Transient
    @XmlTransient
    private Map<String, Long> kindIdMap = new HashMap<String, Long>();

    @Basic
    private Long timeStamp;

    // initialize block.
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
            e.printStackTrace();
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

    /**
     * Add a new meaning identified by the meaning kind to the current word.
     *
     * @param kind    the meaning kind.
     * @param meaning the new meaning.
     */
    public void addMeaning(Long kind, Meaning meaning) {
        // add to the meaning list.
        meanings.add(meaning);
        List<Meaning> meaningList = meaningMap.get(kind);
        if (meaningList == null) {
            meaningList = new ArrayList<Meaning>(0);
            meaningMap.put(kind, meaningList);
            kindIdList.add(kind);
        }
        meaningMap.get(kind).add(meaning);
    }

    /**
     * Optimize word structure by removing meanings and examples which
     * are not selected in the {@code filteredMeaningIds} and the {@code filteredExampledIds}
     *
     * @param filteredMeaningIds        filtered meaning ids.
     * @param filteredMeaningExampleIds filtered example ids by meaning id.
     */
    public void optimizeStructure(List<Long> filteredMeaningIds, Map<String, List<Integer>> filteredMeaningExampleIds) {
        // filter the meaning by the meaning id.
        CollectionUtils.filter(getMeanings(), new MeaningPredicate(filteredMeaningIds));
        // filter the example by the example index.
        if (CollectionUtils.isNotEmpty(getMeanings())) {
            for (Meaning m : getMeanings()) {
                List<String> examples = m.getExamples();
                List<String> filteredExamples = new ArrayList<String>();
                if (CollectionUtils.isNotEmpty(filteredMeaningExampleIds.get(m.getId().toString()))) {
                    for (Integer i : filteredMeaningExampleIds.get(m.getId().toString())) {
                        filteredExamples.add(examples.get(i));
                    }
                }
                // now we can retain the needed one.
                examples.retainAll(filteredExamples);
            }
        }
    }

    /**
     * Add a new meaning id.
     *
     * @param meaningId new meaning id.
     */
    public void addMeaningId(Long meaningId) {
        getMeaningIds().add(meaningId);
    }

    /**
     * Add a new kind to the current word.
     *
     * @param kind the new word kind.
     */
    public void addKind(Long kind) {
        meaningMap.put(kind, new ArrayList<Meaning>(0));
        kindIdList.add(kind);
    }

    public List<Meaning> getMeaning(Long kind) {
        return meaningMap.get(kind);
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

    public Map<Long, List<Meaning>> getMeaningMap() {
        return meaningMap;
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

    public String getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }
}

class MeaningPredicate implements Predicate {
    private List<Long> _filteredList = new ArrayList<Long>();

    public MeaningPredicate(List<Long> filteredList) {
        _filteredList = filteredList;
    }

    public boolean evaluate(Object o) {
        Meaning m = (Meaning) o;

        return _filteredList.contains(m.getId());
    }
}