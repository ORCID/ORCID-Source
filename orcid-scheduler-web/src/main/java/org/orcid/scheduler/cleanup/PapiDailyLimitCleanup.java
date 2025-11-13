package org.orcid.scheduler.cleanup;

import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class PapiDailyLimitCleanup {

    private static final Logger LOG = LoggerFactory.getLogger(PapiDailyLimitCleanup.class);

    @Value("${org.orcid.core.papiLimitReport.daysToKeep:180}")
    private int daysToKeep;

    @Autowired
    private PublicApiDailyRateLimitDao papiRateLimitingDao;

    public void removeOldEvents() {
        LOG.info("About to remove old rate limit stats, days to keep {}", daysToKeep);
        papiRateLimitingDao.cleanup(daysToKeep);
    }

}
