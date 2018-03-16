package org.orcid.core.cron;
/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AuthorizationCodeCleanerCronJob {
    void cleanExpiredAuthorizationCodes();
}
