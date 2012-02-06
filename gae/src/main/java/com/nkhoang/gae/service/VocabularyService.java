package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.model.WordEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VocabularyService {
   /**
    * Get all WordEntitys in range with specific direction. The flag {@code isPopulated} indicates that
    * whether the result should be the WordEntity with fully populated meanings or the WordEntity with no meaning.
    *
    * @param startingIndex the starting index.
    * @param size          the number of WordEntitys should be returned.
    * @param direction     the sorting direction.
    * @return a list of found WordEntitys.
    */
   List<WordEntity> getAllWordEntitiesByRange(int startingIndex, int size, String direction);


   /**
    * Lookup WordEntity using online VN dictionary.
    *
    * @param w WordEntity to be updated.
    * @return WordEntity with VN meanings.
    * @throws IOException
    * @throws IllegalArgumentException
    */
   public Word lookupVN(String w) throws IOException;

   boolean checkConfiguredDicts();


   /**
    * Update the WordEntity with something worth to be updated (pron or soundsource).
    *
    * @param w WordEntity to be updated.
    */
   void update(WordEntity w);

   Map<String, Word> lookup(String requestWordEntity);

   /**
    * Get all WordEntitys in specified range. Starting from <code>startingIndex</code> and end with <code>startingIndex + size</code>
    *
    * @param startingIndex index in DS to start retrieving. (not the WordEntity <code>id</code>)
    * @param size          total WordEntity to be returned.
    * @return a list of WordEntity in range.
    */
   List<WordEntity> getAllWordEntitiesByRange(int startingIndex, int size);
}
