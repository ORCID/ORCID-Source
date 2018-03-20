package org.orcid.core.utils;

import javax.annotation.Resource;

import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class OrcidCacheEvictionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidCacheEvictionUtil.class);
    
    @Resource
    private WorkEntityCacheManager workEntityCacheManager;
    
    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;
    
    @Scheduled(cron = "${org.orcid.core.utils.evictExpiredElementsOnWorksCache:0 0 */3 * * *}")
    public void evictExpiredElementsOnWorksCache() {
        LOGGER.info("Running evictExpiredElementsOnWorksCache");
        workEntityCacheManager.evictExpiredElements();
        orcidProfileCacheManager.evictExpiredElements();
    }
}
