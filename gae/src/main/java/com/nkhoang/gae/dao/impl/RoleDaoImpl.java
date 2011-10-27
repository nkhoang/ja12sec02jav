package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.RoleDao;
import com.nkhoang.gae.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoleDaoImpl extends BaseDaoImpl<Role, Long> implements RoleDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleDaoImpl.class);

    public String getClassName() {
        return Role.class.getName();
    }
}
