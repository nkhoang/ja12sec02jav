package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.PhraseDao;
import com.nkhoang.gae.model.Phrase;

public class PhraseDaoImpl extends BaseDaoImpl<Phrase, Long> implements PhraseDao {
    public String getClassName() {
        return "Phrase";
    }
}
