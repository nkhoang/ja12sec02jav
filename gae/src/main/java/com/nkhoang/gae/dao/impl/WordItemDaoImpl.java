package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.WordItemDao;
import com.nkhoang.gae.model.WordItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Transactional
public class WordItemDaoImpl extends BaseDaoImpl<WordItem, Long> implements WordItemDao {
    private static final Logger LOG = LoggerFactory.getLogger(WordItemDaoImpl.class);


    public String getClassName() {
        return "WordItem";
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
