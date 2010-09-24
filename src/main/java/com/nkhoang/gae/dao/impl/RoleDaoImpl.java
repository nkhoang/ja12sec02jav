package com.nkhoang.gae.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.nkhoang.gae.dao.RoleDao;
import com.nkhoang.gae.model.Role;

@Transactional
public class RoleDaoImpl extends GeneralDaoImpl<Role, Long> implements RoleDao {
    private static final Logger LOGGER = Logger.getLogger(RoleDaoImpl.class);

    @Override
    public Role get(Long id) {
        LOGGER.debug("Get role ID: " + id);
        Query query = entityManager.createQuery("Select from " + Role.class.getName() + " t where t.id=:roleID");
        query.setParameter("roleID", id);

        Role role = (Role) query.getSingleResult();
        if (role != null) {
            return role;
        }

        return null;
    }

    public List<Role> getAll() {
        List<Role> result = null;
        try {
            Query query = getEntityManager().createQuery("Select from " + Role.class.getName());
            result = query.getResultList();
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        return result;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean delete(Long id) {
        boolean result = false;
        try {
            Query query = entityManager.createQuery("Delete from " + Role.class.getName() + " i where i.id=" + id);
            query.executeUpdate();
            entityManager.flush();
            result = true;
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return result;
    }

}
