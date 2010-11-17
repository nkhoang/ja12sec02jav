package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;

import java.util.List;

public interface VocabularyService {
	public Word lookup(String word);

	public Word lookupENLongman(Word aWord, String word);

    public Word lookupENCambridge(Word aWord, String word);

	public Word save(String lookupWord);

	public List<Word> getAllWords();

	public List<Word> getAllWordsFromUser(List<Long> wordIds);

}
