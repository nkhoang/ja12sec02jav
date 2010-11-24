package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;

import java.io.IOException;
import java.util.List;

public interface VocabularyService {
    public Word lookup(String word) throws IOException;

    public Word save(String lookupWord) throws IOException;    

    public List<Word> getAllWordsInRange(int startingIndex, int size);

    public List<Word> getAllWordsFromUser(List<Long> wordIds);

    public int getWordSize();

}
