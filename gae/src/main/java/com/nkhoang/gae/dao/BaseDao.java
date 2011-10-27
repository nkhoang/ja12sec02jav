package com.nkhoang.gae.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Define basic CRUD.
 *
 * @param <T>  the model class.
 * @param <PK> the id type.
 */
public interface BaseDao<T, PK extends Serializable> {
    T get(PK id);

    T save(T e);

    List<T> getAll();

    T update(T e);

    boolean delete(PK id);
}
