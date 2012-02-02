package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;

/**
 * Interface for any lookup services.
 * <p/>
 * <p>For example: </p>
 * <ul>
 * <li>{@link com.nkhoang.gae.service.impl.OxfordLookupServiceImpl}</li>
 * <p/>
 * </ul>
 */
public interface LookupService {
    /**
     * Common method of Lookup Interface.
     *
     * @param word word to be searched.
     * @return the general {@link Word} entity.
     */
    Word lookup(String word);


    /**
     * Each service has to define their service name.
     *
     * @return the service name.
     */
    String getServiceName();
}
