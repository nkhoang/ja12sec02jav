package com.nkhoang.gae.dao;

import java.io.Serializable;
import java.util.List;

/**
 * General Interface for DAO Object.
 * 
 * @author hnguyen93
 * 
 * @param <T>
 *            Object.
 * @param <PK>
 *            PK.
 */
public interface BaseDao<T, PK extends Serializable> {
    T get(PK id);

    T save(T e);

    List<T> getAll();

    T update(T e);

    boolean delete(PK id);
}
