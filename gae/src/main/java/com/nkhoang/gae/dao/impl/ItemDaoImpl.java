package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.ItemDao;
import com.nkhoang.gae.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Calendar;
import java.util.List;

/**
 * Item dao implementation.
 * 
 * @author hnguyen93
 * 
 */
@Transactional
public class ItemDaoImpl extends GeneralDaoImpl<Item, Long> implements ItemDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDaoImpl.class);

    // @off
    /**
     * Get an item from DB.
     * @param id: Item id.
     * @return an object 
     *         or
     *         null value.
     */
    // @on
    public Item get(Long id) {
        LOGGER.info("Get item [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + Item.class.getName() + " t where t.id=:itemID");
            query.setParameter("itemID", id);

            Item item = (Item) query.getSingleResult();
            if (item != null) {
                return item;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to get item from DB.");
            LOGGER.error("Error", e);
        }
        return null;
    }

    // @off
    /**
     * Get all items includes deleted ones.
     * @return a list 
     *         or
     *         null value.
     */
    // @on
    public List<Item> getAllWithDeleted() {
        LOGGER.info("Get all item [DELETED included]...");
        List<Item> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Item.class.getName());

            query.setParameter("deletedN", "N");
            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.info("Failed to load items from DB.");
            LOGGER.error("Error", ex);
        }
        return result;
    }

    // @off
    /**
     * Get all items from DB.
     * @return a list 
     *         or
     *         null value.
     */
    // @on
    public List<Item> getAll() {
        LOGGER.info("Get all item ...");
        List<Item> result = null;
        try {
            Query query = entityManager.createQuery("Select from " + Item.class.getName()
                    + " t where t.deletedFlag=:deletedN");
            query.setParameter("deletedN", "N");
            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.info("Failed to load items from DB.");
            LOGGER.error("Error", ex);
        }
        return result;
    }

    // @off
    /**
     * Mark an item as deleted.
     * @param id: item id.
     * @return true
     *         or
     *         false.
     */
    // @on
    public boolean markDeleted(Long id) {
        LOGGER.info("Mark deleted with ID: " + id);
        boolean result = false;
        try {
            Item item = get(id);
            if (item != null) {
                item.setDeletedFlag(Item.DELETED_Y);
                Calendar calendar = Calendar.getInstance();

                item.setDeletedDate(calendar.getTime());
            }

            save(item);
            result = true;
        } catch (Exception ex) {
            LOGGER.info("Failed to mark item as deleted.");
            LOGGER.error("Error", ex);
        }
        return result;
    }

    // @off
    /**
     * Delete an item from DB.
     * @param id: item id.
     */
    // @on
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean delete(Long id) {
        LOGGER.info("Delete item with [id: " + id + "].");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + Item.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();

            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete item with [id:" + id + "]");
            LOGGER.error("Error", e);
        }
        return result;
    }
}
