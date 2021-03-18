/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */

package com.payoneer.checkout.localization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for caching the Localization files based on a cache id
 */
public class LocalizationCache {

    private String cacheId;
    private final Map<String, LocalizationHolder> cache;

    public LocalizationCache() {
        cache = new ConcurrentHashMap<>();
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public void clear() {
        this.cacheId = null;
        cache.clear();
    }

    public LocalizationHolder get(String key) {
        return cache.get(key);
    }

    public void put(String key, LocalizationHolder holder) {
        cache.put(key, holder);
    }
}
