package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.model.WordItem;

import java.util.List;

public interface WordItemDao extends BaseDao<WordItem, Long> {
    List<WordItem> getAllInRange(int offset, int size);
List<WordItem> getAllInRangeWithOrder(int offset, int size, String field);

}
