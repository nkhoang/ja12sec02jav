package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.model.Word;
import org.apache.log4j.Logger;

import javax.persistence.Query;
import java.util.List;

public class VocabularyDaoImpl extends GeneralDaoImpl<Word, Long> implements VocabularyDao {
    private static final Logger LOGGER = Logger.getLogger(VocabularyDaoImpl.class);

    // @off
    /**
     * Look up a word from DB.
     * @return an obj
     *         or
     *         null.
     */
    // @on
    public Word lookup(String word) {
        LOGGER.info("Looking up word : " + word);
        Word result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Word.class.getName()
                    + " t where t.description=:wordDescription");
            query.setParameter("wordDescription", word);

            result = (Word) query.getSingleResult();
        } catch (Exception e) {
            LOGGER.info("Failed to lookup word : " + word);
            LOGGER.error(e);
        }
        return result;
    }

    public boolean delete(Long id) {
        LOGGER.info("Delete word with ID: " + id);
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + Word.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();

            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete word with ID: " + id);
            LOGGER.error(e);
        }
        return result;
    }

    public Word get(Long id) {
        LOGGER.info("Get word ID: " + id);
        try {
            Query query = entityManager.createQuery("Select from " + Word.class.getName() + " t where t.id=:wordID");
            query.setParameter("wordID", id);

            Word word = (Word) query.getSingleResult();
            if (word != null) {
                return word;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to get word ID: " + id);
            LOGGER.error(e);
        }
        return null;
    }

    public List<Word> getAll() {
        LOGGER.info("Get all words ...");
        List<Word> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Word.class.getName());
            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.info("Failed to get all words ...");
            LOGGER.error(ex);
        }
        return result;
    }

}
