package com.nkhoang.service;

import com.nkhoang.model.Word;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: 1/23/11
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface VocabularyService {
    Word save(String lookupWord) throws IOException, IllegalArgumentException;
    void removeAll();
}
