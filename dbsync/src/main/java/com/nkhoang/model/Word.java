package com.nkhoang.model;

import java.util.*;

public class Word {
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

  public List<Sense> getMeanings() {
    return meanings;
  }

  public void setMeanings(List<Sense> meanings) {
    this.meanings = meanings;
  }

  public Set<Phrase> getPhraseList() {
    return phraseList;
  }

  public void setPhraseList(Set<Phrase> phraseList) {
    this.phraseList = phraseList;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public Map<String, List<Sense>> getMeaningMap() {
    return meaningMap;
  }

  public void setMeaningMap(Map<String, List<Sense>> meaningMap) {
    this.meaningMap = meaningMap;
  }

  public Map<String, List<Phrase>> getPhraseMap() {
    return phraseMap;
  }

  public void setPhraseMap(Map<String, List<Phrase>> phraseMap) {
    this.phraseMap = phraseMap;
  }

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

