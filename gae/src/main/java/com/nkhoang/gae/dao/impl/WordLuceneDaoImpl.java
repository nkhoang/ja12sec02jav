package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.WordLuceneDao;
import com.nkhoang.gae.model.WordLucene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class WordLuceneDaoImpl extends BaseDaoImpl<WordLucene, Long> implements WordLuceneDao {
    private static final Logger LOG = LoggerFactory.getLogger(WordLuceneDaoImpl.class);

    public String getClassName() {
        return WordLucene.class.getName();
    }
}
