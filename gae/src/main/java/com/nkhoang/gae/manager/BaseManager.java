package com.nkhoang.gae.manager;

import java.io.Serializable;
import java.util.List;

/**
 * General Interface.
 * 
 * @author hnguyen93
 * 
 * @param <T>
 *            Object o.
 * @param <PK>
 *            Primary Key.
 */
public interface BaseManager<T, PK extends Serializable> {
    T save(T o);

    T update(T o);

    List<T> listAll();

    boolean clearAll();
}
