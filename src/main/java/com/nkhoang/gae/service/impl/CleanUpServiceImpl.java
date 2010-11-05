package com.nkhoang.gae.service.impl;

import com.google.appengine.api.datastore.*;
import com.nkhoang.gae.service.CleanUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.*;
import java.util.Collections;

public class CleanUpServiceImpl implements CleanUpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanUpServiceImpl.class);

    /**
     * deleting all session objects from the datastore
     */
    public void clearSessions() {
        DatastoreService datastore =
                DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("_ah_SESSION");
        PreparedQuery results = datastore.prepare(query);

        LOGGER.debug("Deleting " + results.countEntities() + " sessions from data store");

        for (Entity session : results.asIterable()) {
            datastore.delete(session.getKey());
        }
    }

    /**
     * clearing everything in the cache, because sessions are also kept in memcache
     */

    public void clearCache() throws CacheException {
        CacheFactory cacheFactory = CacheManager.getInstance
                ().getCacheFactory();
        Cache cache = cacheFactory.createCache(Collections.emptyMap());

        CacheStatistics stats = cache.getCacheStatistics();
        LOGGER.debug("Clearing " + stats.getObjectCount() + " objects in cache");
        cache.clear();
    }
}
