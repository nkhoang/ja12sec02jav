package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.DictionaryDao;
import com.nkhoang.gae.model.Dictionary;


public class DictionaryDaoImpl extends BaseDaoImpl<Dictionary, Long> implements DictionaryDao{
	public String getClassName() {
		return Dictionary.class.getName();
	}
}
