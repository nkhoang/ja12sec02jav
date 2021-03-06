package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.model.WordEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class VocabularyDaoImpl extends BaseDaoImpl<WordEntity, Long> implements VocabularyDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyDaoImpl.class);

  public String getClassName() {
    return "WordEntity";
  }

  /**
   * Lookup word by Dictionary Type.
   *
   * @param word     the word to lookup.
   * @param dictType the dictionary type.
   * @return the {@link WordEntity}
   */
  public WordEntity lookupByDict(String word, String dictType) {
    word = word.trim().toLowerCase();
    LOGGER.info("Looking up word : " + word);
    WordEntity result = null;
    try {
      Query query = entityManager.createQuery("Select from " + WordEntity.class.getName()
          + " t where t.word=:wordDescription and t.dictType=:dictType");
      query.setParameter("wordDescription", word);
      query.setParameter("dictType", dictType);

      result = (WordEntity) query.getSingleResult();
    } catch (NoResultException nre) {
      // do not want to handle this type of exception.
    }
    return result;
  }

  /**
   * Lookup word.
   *
   * @param word word to lookup.
   * @return the {@link WordEntity}
   */
  public WordEntity lookup(String word) {
    word = word.trim().toLowerCase();
    LOGGER.info("Looking up word : " + word);
    WordEntity result = null;
    try {
      Query query = entityManager.createQuery("Select from " + WordEntity.class.getName()
          + " t where t.word=:wordDescription");
      query.setParameter("wordDescription", word);

      result = (WordEntity) query.getSingleResult();
    } catch (NoResultException nre) {

    }
    return result;
  }


  /**
   * Get all word in range from {@code offset} to {@code offset + direction}
   *
   * @param offset    the offset to start from.
   * @param size      the number of words to get.
   * @param direction the sorting direction.
   * @return a list of words in specified range.
   */
  public List<WordEntity> getAllInRange(int offset, int size, String direction) {
    LOGGER.info("Get all word entities starting from " + offset + " with size=[" + size + "]...");
    List<WordEntity> result = null;
    try {
      Query query = entityManager.createQuery("Select from " + WordEntity.class.getName() + " order by timeStamp " + direction);
      query.setFirstResult(offset);
      query.setMaxResults(size);

      result = query.getResultList();
      LOGGER.info("Found: " + result.size());
    } catch (NoResultException ex) {
    }
    return result;
  }


  public List<WordEntity> getAllInRange(int offset, int size) {
    LOGGER.info("Get all word entities starting from " + offset + " with size=[" + size + "]...");
    List<WordEntity> result = null;
    try {
      Query query = entityManager.createQuery("Select from " + WordEntity.class.getName() + " order by timeStamp desc");
      query.setFirstResult(offset);
      query.setMaxResults(size);

      result = query.getResultList();
      LOGGER.info("Found: " + result.size());
    } catch (NoResultException ex) {
    }
    return result;
  }
}