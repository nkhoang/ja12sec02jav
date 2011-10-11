package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;

import java.io.IOException;
import java.util.List;

public interface VocabularyService {

    List<Word> getAllWords();

    /**
     * Get all words in range with specific direction. The flag {@code isPopulated} indicates that
     * whether the result should be the word with fully populated meanings or the word with no meaning.
     *
     * @param startingIndex the starting index.
     * @param size          the number of words should be returned.
     * @param direction     the sorting direction.
     * @param isPopulated   the flag indicates that the status of the word.
     * @return a list of found words.
     */
    List<Word> getAllWordsByRange(int startingIndex, int size, String direction, boolean isPopulated);

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

    public Word save(String lookupWord) throws IOException;

	public void saveWordToDatastore(Word word);
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
    public List<Word> getAllWordsById(List<Long> wordIds, boolean isFull);

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
