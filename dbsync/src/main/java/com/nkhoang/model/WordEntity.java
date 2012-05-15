package com.nkhoang.model;

import java.util.*;

public class WordEntity {
  private String pron;
  private String soundSource;
  private String description;
  private Set<PhraseEntity> phraseList = new HashSet<PhraseEntity>();

  private Map<String, List<SenseEntity>> meaningMap = new HashMap<String, List<SenseEntity>>(0);
  private Map<String, List<PhraseEntity>> phraseMap = new HashMap<String, List<PhraseEntity>>();
  private List<SenseEntity> meanings = new ArrayList<SenseEntity>();
  private List<String> kindIdList = new ArrayList();

  private Long timeStamp;
  private String sourceName;
  private Long id;


  public Long getKey() {
    return id;
  }

  public void setKey(Long value) {
    id = value;
  }


  public WordEntity() {
  }

  public void addPhrase(String phraseName, PhraseEntity phrase) {
    if (phraseMap.get(phraseName) == null) {
      phraseMap.put(phraseName, new ArrayList<PhraseEntity>());
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
  public void addMeaning(String kind, SenseEntity meaning) {
    // add to the meaning list.
    meanings.add(meaning);
    List<SenseEntity> meaningList = meaningMap.get(kind);
    if (meaningList == null) {
      meaningList = new ArrayList<SenseEntity>(0);
      meaningMap.put(kind, meaningList);
    }
    meaningMap.get(kind).add(meaning);
  }


  public List<SenseEntity> getMeaning(Long kind) {
    return meaningMap.get(kind);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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


  public Long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public List<SenseEntity> getMeanings() {
    return meanings;
  }

  public void setMeanings(List<SenseEntity> meanings) {
    this.meanings = meanings;
  }

  public Set<PhraseEntity> getPhraseList() {
    return phraseList;
  }

  public void setPhraseList(Set<PhraseEntity> phraseList) {
    this.phraseList = phraseList;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public Map<String, List<SenseEntity>> getMeaningMap() {
    return meaningMap;
  }

  public void setMeaningMap(Map<String, List<SenseEntity>> meaningMap) {
    this.meaningMap = meaningMap;
  }

  public Map<String, List<PhraseEntity>> getPhraseMap() {
    return phraseMap;
  }

  public void setPhraseMap(Map<String, List<PhraseEntity>> phraseMap) {
    this.phraseMap = phraseMap;
  }

  public List<String> getKindIdList() {
    return kindIdList;
  }

  public void setKindIdList(List<String> kindIdList) {
    this.kindIdList = kindIdList;
  }

  public String toString() {
    return "WordEntity: " + description + " with sound [" + soundSource + "]";
  }
}

