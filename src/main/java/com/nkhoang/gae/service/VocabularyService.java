package com.nkhoang.gae.service;

import java.util.List;

import com.nkhoang.gae.model.Word;

public interface VocabularyService {
	public Word lookup(String word);

	public Word lookupEN(Word aWord, String word);

	public Word save(String lookupWord);

	public List<Word> getAllWords();

	public List<Word> getAllWordsFromUser(List<Long> wordIds);

}
