package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.MeaningDao;
import com.nkhoang.gae.model.Meaning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeaningDaoImpl extends BaseDaoImpl<Meaning, Long> implements MeaningDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeaningDaoImpl.class);

    public String getClassName() {
        return "Meaning";
    }
}
