package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class VocabularyDaoImpl extends BaseDaoImpl<Word, Long> implements VocabularyDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyDaoImpl.class);

    public String getClassName() {
        return "Word";
    }

    public List<Word> lookup(String word) {
        LOGGER.info("Looking up word : " + word);
        List<Word> results = new ArrayList<Word>();
        try {
            Query query = entityManager.createQuery("Select from " + Word.class.getName()
                    + " t where t.description=:wordDescription");
            query.setParameter("wordDescription", word);

            results = (List<Word>) query.getResultList();
        } catch (NoResultException nre) {

        }
        return results;
    }


    /**
     * get all word in range from {@code offset} to {@code offset + direction}
     *
     * @param offset    the offset to start from.
     * @param size      the number of words to get.
     * @param direction the sorting direction.
     * @return a list of words in specified range.
     */
    public List<Word> getAllInRange(int offset, int size, String direction) {
        LOGGER.info("Get all words starting from " + offset + " with size=[" + size + "]...");
        List<Word> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Word.class.getName() + " order by timeStamp " + direction);
            query.setFirstResult(offset);
            query.setMaxResults(size);

            result = query.getResultList();
            LOGGER.info("Found: " + result.size());
        } catch (NoResultException ex) {
        }
        return result;
    }


    public List<Word> getAllInRange(int offset, int size) {
        LOGGER.info("Get all words starting from " + offset + " with size=[" + size + "]...");
        List<Word> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Word.class.getName() + " order by timeStamp desc");
            query.setFirstResult(offset);
            query.setMaxResults(size);

            result = query.getResultList();
            LOGGER.info("Found: " + result.size());
        } catch (NoResultException ex) {
        }
        return result;
    }
}