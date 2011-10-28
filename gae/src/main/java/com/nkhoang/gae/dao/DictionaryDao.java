package com.nkhoang.gae.dao;


import com.nkhoang.gae.model.Dictionary;

public interface DictionaryDao extends BaseDao<Dictionary, Long> {

    public Dictionary getDictionaryByName(String dictName);
}
