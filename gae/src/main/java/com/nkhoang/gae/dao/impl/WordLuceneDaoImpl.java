package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.WordLuceneDao;
import com.nkhoang.gae.model.WordLucene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Transactional
public class WordLuceneDaoImpl extends GeneralDaoImpl<WordLucene, Long> implements WordLuceneDao {
    private static final Logger LOG = LoggerFactory.getLogger(WordLuceneDaoImpl.class);

    public WordLucene get(Long id) {
        LOG.debug("Get WordLucene [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + WordLucene.class.getName()
                    + " t where t.id=:wordLuceneId");
            query.setParameter("wordLuceneId", id);

            WordLucene wl = (WordLucene) query.getSingleResult();
            if (wl != null) {
                return wl;
            }
        } catch (Exception e) {
            LOG.info("Failed to get WordLucene [id: " + id + "].");
            LOG.error("Error", e);
        }

        return null;
    }

    public List<WordLucene> getAll() {
        List<WordLucene> result = null;
        LOG.info("Get all wordLucene entities from DB.");
        try {
            Query query = getEntityManager().createQuery("Select from " + WordLucene.class.getName());
            result = query.getResultList();
        } catch (Exception ex) {
            LOG.info("Failed to get all wordLucene entities from DB.");
            LOG.error("Error", ex);
        }
        return result;
    }

    public boolean delete(Long id) {
        boolean result = false;
        LOG.info("Delete WordLucene [id:" + id + "].");
        try {
            Query query = entityManager.createQuery("Delete from " + WordLucene.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOG.info("Failed to delete WordLucene [id:" + id + "].");
            LOG.error("Error", e);
        }
        return result;
    }

}
