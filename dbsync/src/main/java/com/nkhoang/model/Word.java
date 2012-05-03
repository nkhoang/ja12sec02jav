package com.nkhoang.model;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.*;

@Entity(name = "IWord")
@Table(name = "Word")
public class Word implements IWord {
   private String pron;
   private String soundSource;
   private String description;
   private Set<Phrase> phraseList = new HashSet<Phrase>();

   private Map<String, List<Sense>> meaningMap = new HashMap<String, List<Sense>>(0);
   private Map<String, List<Phrase>> phraseMap = new HashMap<String, List<Phrase>>();
   private List<Sense> meanings = new ArrayList<Sense>();
   private List<String> kindIdList = new ArrayList();

   private Long timeStamp;
   private String sourceName;
   private Long id;


   @Column(name = IWord.ID)
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public Long getKey() {
      return id;
   }

   public void setKey(Long value) {
      id = value;
   }


   public Word() {
   }

   public void addPhrase(String phraseName, Phrase phrase) {
      if (phraseMap.get(phraseName) == null) {
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
   public void addMeaning(String kind, Sense meaning) {
      // add to the meaning list.
      meanings.add(meaning);
      List<Sense> meaningList = meaningMap.get(kind);
      if (meaningList == null) {
         meaningList = new ArrayList<Sense>(0);
         meaningMap.put(kind, meaningList);
      }
      meaningMap.get(kind).add(meaning);
   }


   public List<Sense> getMeaning(Long kind) {
      return meaningMap.get(kind);
   }

   @Column(name = IWord.DESCRIPTION)
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setPron(String pron) {
      this.pron = pron;
   }

   @Column(name = IWord.PRON)
   public String getPron() {
      return pron;
   }

   public void setSoundSource(String soundSource) {
      this.soundSource = soundSource;
   }

   @Column(name = IWord.SOUND_SOURCE)
   public String getSoundSource() {
      return soundSource;
   }


   @Transient
   public Long getTimeStamp() {
      return timeStamp;
   }

   public void setTimeStamp(Long timeStamp) {
      this.timeStamp = timeStamp;
   }

   @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = Sense.class)
   @JoinColumn(name = IWord.ID, referencedColumnName = IWord.ID)
   @ForeignKey(name = "FK_WORD_SENSES")
   public List<Sense> getMeanings() {
      return meanings;
   }

   public void setMeanings(List<Sense> meanings) {
      this.meanings = meanings;
   }

   @OneToMany(targetEntity = Phrase.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
   @JoinColumn(name = IWord.ID, referencedColumnName = IWord.ID)
   @ForeignKey(name = "FK_WORD_PHRASES")
   public Set<Phrase> getPhraseList() {
      return phraseList;
   }

   public void setPhraseList(Set<Phrase> phraseList) {
      this.phraseList = phraseList;
   }

   @Column(name = IWord.SOURCE_NAME)
   public String getSourceName() {
      return sourceName;
   }

   public void setSourceName(String sourceName) {
      this.sourceName = sourceName;
   }

   @Transient
   public Map<String, List<Sense>> getMeaningMap() {
      return meaningMap;
   }

   public void setMeaningMap(Map<String, List<Sense>> meaningMap) {
      this.meaningMap = meaningMap;
   }

   @Transient
   public Map<String, List<Phrase>> getPhraseMap() {
      return phraseMap;
   }

   public void setPhraseMap(Map<String, List<Phrase>> phraseMap) {
      this.phraseMap = phraseMap;
   }

   @Transient
   public List<String> getKindIdList() {
      return kindIdList;
   }

   public void setKindIdList(List<String> kindIdList) {
      this.kindIdList = kindIdList;
   }

   public String toString() {
      return "Word: " + description + " with sound [" + soundSource + "]";
   }
}

