package com.nkhoang.gae.model;

import java.util.List;

/**
 * Word representer used by FreeMarker.
 */
public class IVocabularyWord {
    private Word _word;
    private List<String> _meanings;
    private List<String> _comment;

    public Word getWord() {
        return _word;
    }

    public void setWord(Word word) {
        _word = word;
    }

    public List<String> getMeanings() {
        return _meanings;
    }

    public void setMeanings(List<String> meanings) {
        _meanings = meanings;
    }

    public List<String> getComment() {
        return _comment;
    }

    public void setComment(List<String> comment) {
        _comment = comment;
    }
}
