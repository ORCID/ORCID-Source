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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;

import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;

public class OrcidEhCacheManagerFactoryBean extends EhCacheManagerFactoryBean {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidEhCacheManagerFactoryBean.class);

    /**
     * Set the custom name of the EhCache CacheManager
     * 
     * @param cacheManagerName
     *          The given name
     */
    @Override
    public void setCacheManagerName(String cacheManagerName) {
        int hashCode = OrcidEhcacheManagementService.class.getClassLoader().hashCode();
        String suffix = "_" + hashCode;
        String safeCacheManagerName = EhcacheHibernateMbeanNames.mbeanSafe(cacheManagerName + suffix);
        LOGGER.info("Cache manager name = {}", safeCacheManagerName);  
        super.setCacheManagerName(safeCacheManagerName);
    }
}
