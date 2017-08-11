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
package org.orcid.core.utils;

import javax.annotation.Resource;

import org.orcid.core.manager.WorkEntityCacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class OrcidCacheEvictionUtil {

    @Resource
    private WorkEntityCacheManager workEntityCacheManager;
    
    @Scheduled(fixedDelay = 5000)
    public void evictExpiredElementsOnWorksCache() {
        System.out.println("------------->Cleaning cache");
        workEntityCacheManager.evictExpiredElements();
    }
}
