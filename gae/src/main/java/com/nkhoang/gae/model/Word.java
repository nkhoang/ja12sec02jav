package com.nkhoang.gae.model;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * Word entity.
 */
@SuppressWarnings({"JpaAttributeTypeInspection"})
@Entity
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(name = "word", propOrder = {
        "id",
        "description",
        "meanings",
        "pron",
        "soundSource",
        "timeStamp"
})
@XmlSeeAlso({javax.jdo.identity.LongIdentity.class, BitSet.class})
@XmlRootElement
public class Word {

    // used for Vietnamese language.
    public static final byte[] NOUN_BYTES = {100, 97, 110, 104, 32, 116, -31, -69, -85};
    public static final byte[] VERB_IN = {110, -31, -69, -103, 105, 32, -60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    public static final byte[] VERB_TR = {110, 103, 111, -31, -70, -95, 105, 32, -60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    public static final byte[] VERB_BYTES = {-60, -111, -31, -69, -103, 110, 103, 32, 116, -31, -69, -85};
    public static final byte[] VERB_ADJ = {116, -61, -83, 110, 104, 32, 116, -31, -69, -85};
    public static final byte[] VERB_ADV = {112, 104, -61, -77, 32, 116, -31, -69, -85};

    public static String WORD_KIND_NOUN = "";
    public static String WORD_KIND_VERB_IN = "";
    public static String WORD_KIND_VERB_TR = "";
    public static String WORD_KIND_ADJ = "";
    public static String WORD_KIND_VERB = "";
    public static String WORD_KIND_ADV = "";
    public static final String WORD_KIND_NOUN_EN = "noun";
    public static final String WORD_KIND_VERB_EN = "verb";
    public static final String WORD_KIND_ADJ_EN = "adjective";
    public static final String WORD_KIND_ADV_EN = "adverb";
    public static final String WORD_KIND_EXCLAIM_EN = "exclamation";
    public static final String WORD_KIND_PROPOSITION_EN = "preposition";
    public static String[] WORD_KINDS = {};

    // skipped fields.
    public static final String[] SKIP_FIELDS = {"jdoDetachedState", "kindIdMap", "meaningIds", "timeStamp", "meanings"};

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
                    WORD_KIND_ADV, WORD_KIND_ADV_EN, WORD_KIND_ADJ_EN, WORD_KIND_NOUN_EN, WORD_KIND_VERB_EN, WORD_KIND_EXCLAIM_EN,
                    WORD_KIND_PROPOSITION_EN};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic
    private String pron;
    @Basic
    private String soundSource;
    @Basic
    @XmlTransient
    private List<Long> meaningIds = new ArrayList<Long>(0);
    @Basic
    private String description;
    @Basic
    @XmlTransient
    private List<Long> phraseIds = new ArrayList<Long>(0);
	@Transient
	private List<Phrase> phraseList = new ArrayList<Phrase>(0);

    @Transient
    @XmlTransient
    private Map<Long, List<Sense>> meaningMap = new HashMap<Long, List<Sense>>(0);
	@Transient
	@XmlTransient
	private Map<String, List<Phrase>> phraseMap = new HashMap<String, List<Phrase>>();
    @Transient
    private List<Sense> meanings = new ArrayList<Sense>();
    @Transient
    @XmlTransient
    private List<Long> kindIdList = new ArrayList();
    @Transient
    @XmlTransient
    private Map<String, Long> kindIdMap = new HashMap<String, Long>();

    @Basic
    private Long timeStamp;
    @Basic
    private String sourceName;


    public Word() {
        // init
        int i = 0;
        for (String kind : WORD_KINDS) {
            getKindidmap().put(kind, Long.parseLong(i + ""));
            i++;
        }
    }

	public void addPhrase(String phraseName, Phrase phrase) {
		if (phraseMap.get(phraseName) == null ) {
			phraseMap.put(phraseName, new ArrayList<Phrase>());
		}

		phraseMap.get(phraseName).add(phrase);
		phraseList.add(phrase);
	}

    /**
     * Add a new meaning identified by the meaning kind to the current
     *
     * @param kind    the meaning kind.
     * @param meaning the new meaning.
     */
    public void addMeaning(Long kind, Sense meaning) {
        // add to the meaning list.
        meanings.add(meaning);
        List<Sense> meaningList = meaningMap.get(kind);
        if (meaningList == null) {
            meaningList = new ArrayList<Sense>(0);
            meaningMap.put(kind, meaningList);
            kindIdList.add(kind);
        }
        meaningMap.get(kind).add(meaning);
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
     * Add a new kind to the current
     *
     * @param kind the new word kind.
     */
    public void addKind(Long kind) {
        meaningMap.put(kind, new ArrayList<Sense>(0));
        kindIdList.add(kind);
    }

    public List<Sense> getMeaning(Long kind) {
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


    public Map<String, Long> getKindidmap() {
        return kindIdMap;
    }

    public Map<Long, List<Sense>> getMeaningMap() {
        return meaningMap;
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

    public List<Sense> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Sense> meanings) {
        this.meanings = meanings;
    }

    public List<Long> getPhraseIds() {
        return phraseIds;
    }

    public void setPhraseIds(List<Long> phraseIds) {
        this.phraseIds = phraseIds;
    }

	public List<Phrase> getPhraseList() {
		return phraseList;
	}

	public void setPhraseList(List<Phrase> phraseList) {
		this.phraseList = phraseList;
	}

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}

