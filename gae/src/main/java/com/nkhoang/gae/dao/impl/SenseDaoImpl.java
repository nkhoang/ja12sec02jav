package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.SenseDao;
import com.nkhoang.gae.model.Sense;


public class SenseDaoImpl extends BaseDaoImpl<Sense, Long> implements SenseDao {
    public String getClassName() {
        return "Sense";
    }
}
