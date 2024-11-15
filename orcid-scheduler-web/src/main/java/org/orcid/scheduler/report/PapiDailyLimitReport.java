package org.orcid.scheduler.report;

import java.time.LocalDate;

import javax.annotation.Resource;

import org.orcid.core.togglz.Features;
import org.orcid.core.togglz.OrcidTogglzConfiguration;
import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;

import org.orcid.utils.alerting.SlackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.togglz.core.context.ContextClassLoaderFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;

@Service
public class PapiDailyLimitReport {

    private static final Logger LOG = LoggerFactory.getLogger(PapiDailyLimitReport.class);

    @Resource
    private SlackManager slackManager;

    @Value("${org.orcid.core.papiLimitReport.slackChannel:collab-spam-reports}")
    private String slackChannel;

    @Value("${org.orcid.core.papiLimitReport.webhookUrl}")
    private String webhookUrl;

    @Value("${org.orcid.core.orgs.load.slackUser}")
    private String slackUser;

    @Value("${org.orcid.papi.rate.limit.anonymous.requests:10000}")
    private int anonymousRequestLimit;

    @Value("${org.orcid.papi.rate.limit.known.requests:40000}")
    private int knownRequestLimit;

    @Value("${org.orcid.papi.rate.limit.enabled:false}")
    private boolean enableRateLimiting;

    @Autowired
    private PublicApiDailyRateLimitDao papiRateLimitingDao;

    // for running spam manually
    public static void main(String[] args) {
        PapiDailyLimitReport dailyLimitReport = new PapiDailyLimitReport();
        try {
            dailyLimitReport.init();
            dailyLimitReport.papiDailyLimitReport();
        } catch (Exception e) {
            LOG.error("Exception when getting the report for daily limit", e);
            System.err.println(e.getMessage());
        } finally {
            System.exit(0);
        }

    }
    /**
     * Sends daily slack reports to dedicated slack channel.
     */
    public void papiDailyLimitReport() {
        LOG .info("start papi limit report the rate limiting is: " + enableRateLimiting);
        if (enableRateLimiting) {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String mode = Features.ENABLE_PAPI_RATE_LIMITING.isActive() ? "ENFORCEMENT" : "MONITORING";
            String SLACK_INTRO_MSG = "Public API Rate limit report - Date: " + yesterday.toString() + "\nCurrent Anonymous Requests Limit: " + anonymousRequestLimit
                    + "\nCurrent Public API Clients Limit: " + knownRequestLimit + "\nMode: " + mode;
            LOG .info(SLACK_INTRO_MSG);
            slackManager.sendAlert(SLACK_INTRO_MSG, slackChannel, webhookUrl, webhookUrl);
            
            String SLACK_STATS_MSG = "Count of Anonymous IPs blocked: " + papiRateLimitingDao.countAnonymousRequestsWithLimitExceeded(yesterday, anonymousRequestLimit)
                    + "\nCount of Public API clients that have exceeded the limit: "
                    + papiRateLimitingDao.countClientRequestsWithLimitExceeded(yesterday, knownRequestLimit);           
            LOG .info(SLACK_STATS_MSG);
            slackManager.sendAlert(SLACK_STATS_MSG, slackChannel, webhookUrl, webhookUrl);
        }

    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-scheduler-context.xml");
        papiRateLimitingDao = (PublicApiDailyRateLimitDao) context.getBean("papiRateLimitingDao");
        bootstrapTogglz(context.getBean(OrcidTogglzConfiguration.class));
    }

    private static void bootstrapTogglz(OrcidTogglzConfiguration togglzConfig) {
        FeatureManager featureManager = new FeatureManagerBuilder().togglzConfig(togglzConfig).build();
        ContextClassLoaderFeatureManagerProvider.bind(featureManager);
    }

}
