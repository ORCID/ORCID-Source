package org.orcid.scheduler.cleanup;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.cron.AuthorizationCodeCleanerCronJob;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class AuthorizationCodeCleanerCronJobImpl implements AuthorizationCodeCleanerCronJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCodeCleanerCronJobImpl.class);
    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    @Value("${org.orcid.scheduler.cleanup.AuthorizationCodeCleanerCronJob.archive_days:60}")
    private int authorizationCodeArchivedDays;

    public void removeArchivedAuthorizationCodes() {
        // Safety check, we should keep them for at least one month
        if(authorizationCodeArchivedDays < 30){
            LOGGER.warn("Authorization code archive days is less than 30 days, setting to 30 days");
            authorizationCodeArchivedDays = 30;
        }

        LOGGER.info("About to remove authorization codes older than {} days", authorizationCodeArchivedDays);
        // Get the max archive instant
        Instant maxArchive = Instant.now().minus(authorizationCodeArchivedDays, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);

        orcidOauth2AuthoriziationCodeDetailDao.removeArchivedAuthorizationCodes(Date.from(maxArchive));

        LOGGER.info("Finished removing authorization codes older than {} days", authorizationCodeArchivedDays);
    }
}
