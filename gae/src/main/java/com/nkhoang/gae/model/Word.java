package com.nkhoang.gae.model;

import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(name = "word", propOrder = {
      "description",
      "meanings",
      "pron",
      "soundSource",
      "timeStamp"
})
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
   public static final String WORD_KIND_PLURAL_NOUN_EN = "plural noun";
   public static final String WORD_KIND_EXCLAIM_EN = "exclamation";
   public static final String WORD_KIND_PROPOSITION_EN = "preposition";
   public static final String WORD_KIND_CARDINAL_NUMBER_EN = "cardinal number";
   public static final String WORD_KIND_ORDINAL_NUMBER_EN = "ordinal number";
   public static final String WORD_KIND_PRONOUN_EN = "pronoun";
   public static final String WORD_KIND_CONJUNCTION_EN = "conjunction";
   public static String[] WORD_KINDS = {};

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
               WORD_KIND_PROPOSITION_EN, WORD_KIND_PLURAL_NOUN_EN, WORD_KIND_CARDINAL_NUMBER_EN, WORD_KIND_PRONOUN_EN,
               WORD_KIND_CONJUNCTION_EN, WORD_KIND_ORDINAL_NUMBER_EN};
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private String pron;
   private String soundSource;
   private String description;
   private List<Phrase> phraseList = new ArrayList<Phrase>(0);

   @XmlTransient
   private Map<Long, List<Sense>> meaningMap = new HashMap<Long, List<Sense>>(0);
	@XmlTransient
	private Map<String, List<Phrase>> phraseMap = new HashMap<String, List<Phrase>>();
    @Transient
   private List<Sense> meanings = new ArrayList<Sense>();
   @XmlTransient
   private List<Long> kindIdList = new ArrayList();
   @XmlTransient
   private Map<String, Long> kindIdMap = new HashMap<String, Long>();

   private Long timeStamp;
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

