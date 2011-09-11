package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.WordItemStat;

import java.util.List;

public interface WordItemStatDao extends BaseDao<WordItemStat, Long> {
	List<WordItemStat> getAllInRange(int offset, int size);
}
