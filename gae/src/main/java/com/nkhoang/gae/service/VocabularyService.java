package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;

import java.io.IOException;
import java.util.List;

public interface VocabularyService {

    /**
     * Look up pronunciation for a word.
     *
     * @param w word to get pronunciation.
     */
    public void lookupPron(Word w) throws IOException;

    /**
     * Lookup English meanings using online Longman dictionary.
     *
     * @param w word to be updated with Longman English meanings.
     */
    public void lookupENLongman(Word w) throws IOException;

    /**
     * Lookup word idiom.
     *
     * @param w a word to get Idiom.
     * @return a word with idiom.
     */
    // TODO: to be expanded in the future.
    public Word lookupIdiom(Word w) throws IOException;

    /**
     * Lookup word using online VN dictionary.
     *
     * @param w word to be updated.
     * @return word with VN meanings.
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public Word lookupVN(String w) throws IOException;

    /**
     * Save a word to datastore.
     *
     * @param word word to be saved.
     */
    public void save(Word word);

    public Word save(String lookupWord) throws IOException;


    /**
     * Update the word with something worth to be updated (pron or soundsource).
     *
     * @param w word to be updated.
     */
    public void update(Word w);

    /**
     * Get all words in specified range. Starting from <code>startingIndex</code> and end with <code>startingIndex + size</code>
     *
     * @param startingIndex index in DS to start retrieving. (not the word <code>id</code>)
     * @param size          total word to be returned.
     * @return a list of word in range.
     */
    public List<Word> getAllWordsByRange(int startingIndex, int size);

    /**
     * Return a list of words with by providing a list of <code>word ID</code>.
     *
     * @param wordIds word id list.
     * @return a list of found word.
     */
    public List<Word> getAllWordsById(List<Long> wordIds);

    /**
     * Like {@link #getAllWordsByRange(int, int)} but without populating with meanings. This is useful in case we need to display word title only.
     *
     * @param startingIndex index in DS to start retrieving. (not the word <code>id</code>).
     * @param size          total word to be returned.
     * @return a list of word title in range.
     */
    public List<Word> getAllWordsByRangeWithoutMeanings(int startingIndex, int size);

    /**
     * Find word with ID = <code>id</code> then populate it with meanings (if any)
     *
     * @param id word id.
     * @return populated word or <b>null</b> if id is not existing.
     */
    public Word populateWord(Long id);
}
