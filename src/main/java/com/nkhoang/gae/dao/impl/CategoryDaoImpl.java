package com.nkhoang.gae.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.nkhoang.gae.dao.CategoryDao;
import com.nkhoang.gae.model.Category;

@Transactional
public class CategoryDaoImpl extends GeneralDaoImpl<Category, Long> implements CategoryDao {
    private static final Logger LOGGER = Logger.getLogger(CategoryDaoImpl.class);

    // @off
    /**
     * Get a category.
     * @return an object
     *         or
     *         null.
     */
    // @on
    public Category get(Long id) {
        LOGGER.debug("Get category [id: " + id + "].");
        try {
            Query query = entityManager.createQuery("Select from " + Category.class.getName()
                    + " t where t.id=:categoryId");
            query.setParameter("categoryId", id);

            Category cat = (Category) query.getSingleResult();
            if (cat != null) {
                return cat;
            }
        } catch (Exception e) {
            LOGGER.info("Failed to get category [id: " + id + "].");
            LOGGER.error(e);
        }

        return null;
    }

    // @off
    /**
     * Get all categories.
     * @return a list
     *         or
     *         null.
     */
    // @on
    public List<Category> getAll() {
        List<Category> result = null;
        LOGGER.info("Get all categories from DB.");
        try {
            Query query = getEntityManager().createQuery("Select from " + Category.class.getName());
            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.info("Failed to get all categories from DB.");
            LOGGER.error(ex);
        }
        return result;
    }

    // @off
    /**
     * Delete category with id.
     * @return true
     *         or
     *         false.
     */
    // @on
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean delete(Long id) {
        boolean result = false;
        LOGGER.info("Delete category [id:" + id + "].");
        try {
            Query query = entityManager.createQuery("Delete from " + Category.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.info("Failed to delete category [id:" + id + "].");
            LOGGER.error(e);
        }
        return result;
    }

}
