package com.nkhoang.gae.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.nkhoang.gae.dao.ItemPictureDao;
import com.nkhoang.gae.model.ItemPicture;

/**
 * Item Picture Dao Implementation.
 * 
 * @author hoangnk
 * 
 */
@Transactional
public class ItemPictureDaoImpl extends GeneralDaoImpl<ItemPicture, Long> implements ItemPictureDao {
    private static final Logger LOGGER = Logger.getLogger(ItemPictureDaoImpl.class);

    // @off
    /**
     * Get all item pictures.
     * @return a list
     *         or
     *         null.
     */
    // @on
    public List<ItemPicture> getAll() {
        LOGGER.info("Load all item pictures...");
        List<ItemPicture> result = null;
        try {
            Query query = getEntityManager().createQuery("Select from " + ItemPicture.class.getName());
            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.info("Failed to get all item pictures.");
            LOGGER.error(ex);
        }
        return result;
    }

    // @off
    /**
     * Get an item picture.
     * @param id: item picture id.
     * @return an object
     *         or
     *         null.
     */
    // @on
    public ItemPicture get(Long id) {
        LOGGER.info("Get item picture with [id:" + id + "]");
        ItemPicture ip = null;
        try {
            Query query = getEntityManager().createQuery(
                    "Select from " + ItemPicture.class.getName() + " where id = " + id);

            List<ItemPicture> ips = query.getResultList();
            if (ips != null && ips.size() > 0) {
                ip = ips.get(0);
            }
        } catch (Exception ex) {
            LOGGER.info("Failed to load item picture [id:" + id + "].");
            LOGGER.error(ex);
        }

        return ip;
    }

    // @off
    /**
     * Delete an item picture.
     * @param id: item picture id.
     */
    // @on
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean delete(Long id) {
        LOGGER.info("Delete item picture with [id:" + id + "]");
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + ItemPicture.class.getName() + " i where i.id="
                    + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete item picture [id:" + id + "]");
            LOGGER.error(e);
        }
        return result;
    }
}
