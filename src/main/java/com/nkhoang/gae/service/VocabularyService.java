package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;

import java.io.IOException;
import java.util.List;

public interface VocabularyService {

    public void lookupPron(Word aWord, String word) throws IOException;

    public void lookupENLongman(Word aWord, String word) throws IOException;

    public Word lookupVN(String word) throws IOException;

    public Word save(String lookupWord) throws IOException;

    public List<Word> getAllWordsInRange(int startingIndex, int size);

    public List<Word> getAllWordsFromUser(List<Long> wordIds);

    public List<Word> getAllWordsInRangeWithoutMeanings(int startingIndex, int size);

    public Word populateWord(Long id);

    public int getWordSize();

}
