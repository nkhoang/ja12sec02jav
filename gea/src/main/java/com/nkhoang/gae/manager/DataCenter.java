package com.nkhoang.gae.manager;

import com.nkhoang.gae.model.CacheData;

import java.util.HashMap;
import java.util.Map;

// @off
/**
 * Cache all the necessary data to minimize datastore calls. 
 * @author hnguyen93
 *
 */
// @on
public class DataCenter {
    private static final Map<String, CacheData> cache = new HashMap<String, CacheData>();

    /**
     * Get cache data from datacache.
     * 
     * @param identifier
     *            data identifier.
     * @return Cache obj.
     */
    public static Object getData(String identifier) {
        Object result = null;
        if (cache.get(identifier) != null) {
            result = cache.get(identifier).getData();
        }
        return result;
    }

    /**
     * Update datacache by identifier.
     * 
     * @param identifier
     *            identifier.
     * @param cacheData
     *            the new datacache.
     */
    public static void updateCache(String identifier, Object cacheData) {
        CacheData data = new CacheData(cacheData);
        cache.put(identifier, data);
    }

    /**
     * Change the status of the datacache so datacache should be updated.
     * 
     * @param identifier
     *            identifier.
     */
    public static void statusChanged(String identifier) {
        if (cache.get(identifier) != null) {
            cache.get(identifier).setModified();
        }
    }

    // @off
    /**
     * Get the status of the datacache.
     * 
     * @param identifier identifier.
     * @return true
     *         or
     *         false
     */
    // @on
    public static boolean getModifiedStatus(String identifier) {
        return cache.get(identifier).isModified();
    }

}
