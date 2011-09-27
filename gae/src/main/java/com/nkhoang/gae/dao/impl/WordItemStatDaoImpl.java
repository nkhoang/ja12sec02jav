package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.WordItemStatDao;
import com.nkhoang.gae.model.WordItemStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Transactional
public class WordItemStatDaoImpl extends GeneralDaoImpl<WordItemStat, Long> implements WordItemStatDao {
    private static final Logger LOG = LoggerFactory.getLogger(WordItemStatDaoImpl.class);

    public WordItemStat get(Long id) {
        LOG.debug("Get word stat entity with [id: " + id + "].");
        try {
            Query query = entityManager.createQuery(
                    "Select from " + WordItemStat.class.getName() + " t where t.id=:wordStatItemId");
            query.setParameter("wordStatItemId", id);

            WordItemStat wis = (WordItemStat) query.getSingleResult();
            if (wis != null) {
                return wis;
            }
        } catch (Exception e) {
            LOG.info("Failed to get word stat entity with [id: " + id + "].");
            LOG.error("Error", e);
        }

        return null;
    }

    public List<WordItemStat> getAll() {
        List<WordItemStat> result = null;
        LOG.info("Get all word stat entities from DB.");
        try {
            Query query = getEntityManager().createQuery("Select from " + WordItemStat.class.getName());
            result = query.getResultList();
        } catch (Exception ex) {
            LOG.info("Failed to get all word stat entities from DB.");
            LOG.error("Error", ex);
        }
        return result;
    }

    public boolean delete(Long id) {
        boolean result = false;
        LOG.info("Delete word stat entity with [id:" + id + "].");
        try {
            Query query = entityManager
                    .createQuery("Delete from " + WordItemStat.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOG.info("Failed to delete word stat entity with [id:" + id + "].");
            LOG.error("Error", e);
        }
        return result;
    }

    public List<WordItemStat> getAllInRange(int offset, int size) {
        LOG.info("Get all word stat entities starting from " + offset + " with size=[" + size + "]...");
        List<WordItemStat> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + WordItemStat.class.getName());
            query.setFirstResult(offset);
            query.setMaxResults(size);

            result = query.getResultList();
            LOG.info("Found: " + result.size());
        } catch (Exception ex) {
            LOG.info("Failed to get all word items...");
            LOG.error("Error", ex);
        }
        return result;
    }

}