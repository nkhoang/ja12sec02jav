package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.WordItemDao;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.model.WordItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Transactional
public class WordItemDaoImpl extends GeneralDaoImpl<WordItem, Long> implements WordItemDao {
    private static final Logger LOG = LoggerFactory.getLogger(WordItemDaoImpl.class);

    /**
     * Get a category.
     *
     * @return an object
     *         or
     *         null.
     */
    public WordItem get(Long id) {
        LOG.debug("Get word item [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + WordItem.class.getName()
                    + " t where t.id=:wordItemId");
            query.setParameter("wordItemId", id);

            WordItem wi = (WordItem) query.getSingleResult();
            if (wi != null) {
                return wi;
            }
        } catch (Exception e) {
            LOG.info("Failed to get word item with [id: " + id + "].");
            LOG.error("Error", e);
        }

        return null;
    }

    /**
     * Get all categories.
     *
     * @return a list
     *         or
     *         null.
     */
    public List<WordItem> getAll() {
        List<WordItem> result = null;
        LOG.info("Get all word items from DB.");
        try {
            Query query = getEntityManager().createQuery("Select from " + WordItem.class.getName());
            result = query.getResultList();
        } catch (Exception ex) {
            LOG.info("Failed to get all word items from DB.");
            LOG.error("Error", ex);
        }
        return result;
    }

    /**
     * Delete category with id.
     *
     * @return true
     *         or
     *         false.
     */
    public boolean delete(Long id) {
        boolean result = false;
        LOG.info("Delete word item with [id:" + id + "].");
        try {
            Query query = entityManager.createQuery("Delete from " + WordItem.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOG.info("Failed to delete word item with [id:" + id + "].");
            LOG.error("Error", e);
        }
        return result;
    }


    /**
     * Get word items in range.
     *
     * @param offset the starting offset.
     * @param size   is the number of items to be returned.
     * @return a list of found word items.
     */
    public List<WordItem> getAllInRange(int offset, int size) {
        LOG.info("Get all word items starting from " + offset + " with size=[" + size + "]...");
        List<WordItem> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + WordItem.class.getName());
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

    /**
     * Get word items in range order by specified field.
     *
     * @param offset the starting offset.
     * @param size   is the number of items to be returned.
     * @param field  ordered-by field.
     * @return a list of found word items.
     */
    public List<WordItem> getAllInRangeWithOrder(int offset, int size, String field, String direction) {
        LOG.info("Get all word items starting from " + offset + " with size=[" + size + "]...");
        List<WordItem> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + WordItem.class.getName() + " order by " + field + " " + direction);
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
