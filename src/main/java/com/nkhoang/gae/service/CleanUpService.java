package com.nkhoang.gae.service;

import javax.cache.CacheException;

public interface CleanUpService {
    void clearSessions();

    void clearCache() throws CacheException;
}
