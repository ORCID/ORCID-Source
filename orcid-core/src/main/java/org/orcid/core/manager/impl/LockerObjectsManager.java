/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

package org.orcid.core.manager.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockerObjectsManager {
    private ConcurrentMap<String, Object> locks = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(OrcidProfileCacheManagerImpl.class);
    
    public Object obtainLock(String string) {
        LOG.debug("About to obtain read lock: " + string);
        Object newLock = new Object();
        Object existingLock = locks.putIfAbsent(string, newLock);
        return existingLock == null ? newLock : existingLock;
    }

    public void releaseLock(String string) {
        LOG.debug("About to release read lock: " + string);
        locks.remove(string);
    }

}
