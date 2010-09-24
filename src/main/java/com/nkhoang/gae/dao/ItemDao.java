package com.nkhoang.gae.dao;

import java.util.List;

import com.nkhoang.gae.model.Item;

/**
 * Item dao.
 * 
 * @author hnguyen93
 * 
 */
public interface ItemDao extends BaseDao<Item, Long> {
    public boolean markDeleted(Long id);

    /**
     * Get all item - deleted included.
     * 
     * @return
     */
    public List<Item> getAllWithDeleted();
}
