package com.nkhoang.gae.dao;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<T, PK extends Serializable> {
    T get(PK id);

    T save(T e);

    List<T> getAll();

    T update(T e);

    boolean delete(PK id);
}
