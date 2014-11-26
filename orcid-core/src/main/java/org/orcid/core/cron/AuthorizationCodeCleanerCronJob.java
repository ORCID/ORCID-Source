package org.orcid.core.cron;

public interface AuthorizationCodeCleanerCronJob {
    void cleanExpiredAuthorizationCodes();
}
