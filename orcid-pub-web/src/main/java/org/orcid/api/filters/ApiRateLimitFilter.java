package org.orcid.api.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.LocaleUtils;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.utils.OrcidRequestUtil;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;
import org.orcid.utils.email.MailGunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.orcid.core.togglz.Features;

@Component
public class ApiRateLimitFilter extends OncePerRequestFilter {
    private static Logger LOG = LoggerFactory.getLogger(ApiRateLimitFilter.class);

    @Autowired
    private PublicApiDailyRateLimitDao papiRateLimitingDao;

    @Autowired
    private OrcidSecurityManager orcidSecurityManager;

    @Autowired
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Autowired
    private MailGunManager mailGunManager;

    @Autowired
    private ProfileDao profileDao;

    @Autowired
    private OrcidUrlManager orcidUrlManager;

    @Autowired
    private RecordNameManager recordNameManager;

    @Autowired
    private TemplateManager templateManager;

    @Autowired
    private EmailManager emailManager;

    @Value("${rate.limit.anonymous.requests:1}")
    private int anonymousRequestLimit;

    @Value("${rate.limit.known.requests:2}")
    private int knownRequestLimit;

    @Value("${rate.limit.enabled:false}")
    private boolean enableRateLimiting;

    private static final String TOO_MANY_REQUESTS_MSG = "Too Many Requests - You have exceeded the daily allowance of API calls.\\n"
            + "You can increase your daily quota by registering for and using Public API client credentials "
            + "(https://info.orcid.org/documentation/integration-guide/registering-a-public-api-client/ )";

    private static final String SUBJECT = "[ORCID] You have exceeded the daily Public API Usage Limit - ";
    private static final String FROM_ADDRESS = "\"Engagement Team, ORCID\" <c.dumitru@orcid.org>";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!   ApiRateLimitFilter starts, rate limit is : " + enableRateLimiting);
        LOG.info("ApiRateLimitFilter starts, rate limit is : " + enableRateLimiting);
        if (enableRateLimiting) {
            String clientId = orcidSecurityManager.getClientIdFromAPIRequest();
            String ipAddress = OrcidRequestUtil.getIpAddress(httpServletRequest);
            boolean isAnonymous = (clientId == null);
            LocalDate today = LocalDate.now();

            if (isAnonymous) {
                LOG.info("ApiRateLimitFilter anonymous request");
                this.rateLimitAnonymousRequest(ipAddress, today, httpServletResponse);

            } else {
                LOG.info("ApiRateLimitFilter client request with clientId: " + clientId);
                this.rateLimitClientRequest(clientId, today);
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private void rateLimitAnonymousRequest(String ipAddress, LocalDate today, HttpServletResponse httpServletResponse) throws IOException {
        PublicApiDailyRateLimitEntity rateLimitEntity = papiRateLimitingDao.findByIpAddressAndRequestDate(ipAddress, today);
        if (rateLimitEntity != null) {
            // update the request count only when limit not exceeded ?
            rateLimitEntity.setRequestCount(rateLimitEntity.getRequestCount() + 1);
            papiRateLimitingDao.updatePublicApiDailyRateLimit(rateLimitEntity, false);
            if (Features.ENABLE_PAPI_RATE_LIMITING.isActive()) {
                if (rateLimitEntity.getRequestCount() >= anonymousRequestLimit) {
                    httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                 // Use only one writer call
                    if (!httpServletResponse.isCommitted()) {
                        try (PrintWriter writer = httpServletResponse.getWriter()) {
                            writer.write(TOO_MANY_REQUESTS_MSG);
                            writer.flush();
                        }
                        return;
                    }
                }
            }
        } else {
            // create
            rateLimitEntity = new PublicApiDailyRateLimitEntity();
            rateLimitEntity.setIpAddress(ipAddress);
            rateLimitEntity.setRequestCount(1L);
            rateLimitEntity.setRequestDate(today);
            papiRateLimitingDao.persist(rateLimitEntity);

        }
        return;

    }

    private void rateLimitClientRequest(String clientId, LocalDate today) {
        PublicApiDailyRateLimitEntity rateLimitEntity = papiRateLimitingDao.findByClientIdAndRequestDate(clientId, today);
        if (rateLimitEntity != null) {
            if (Features.ENABLE_PAPI_RATE_LIMITING.isActive()) {
                // email the client first time the limit is reached
                if (rateLimitEntity.getRequestCount() == knownRequestLimit) {
                    sendEmail(clientId);
                }
            }
            // update the request count
            rateLimitEntity.setRequestCount(rateLimitEntity.getRequestCount() + 1);
            papiRateLimitingDao.updatePublicApiDailyRateLimit(rateLimitEntity,true);

        } else {
            // create
            rateLimitEntity = new PublicApiDailyRateLimitEntity();
            rateLimitEntity.setClientId(clientId);
            rateLimitEntity.setRequestCount(0L);
            rateLimitEntity.setRequestDate(today);
            papiRateLimitingDao.persist(rateLimitEntity);
        }

    }

    private Map<String, Object> createTemplateParams(String clientId, String clientName, String emailName, String orcidId) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("clientId", clientId);
        templateParams.put("clientId", clientName);
        templateParams.put("emailName", emailName);
        templateParams.put("locale", LocaleUtils.toLocale("en"));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", SUBJECT + orcidId);
        return templateParams;
    }

    private void sendEmail(String clientId) {
        ClientDetailsEntity clientDetailsEntity = clientDetailsEntityCacheManager.retrieve(clientId);
        ProfileEntity profile = profileDao.find(clientDetailsEntity.getGroupProfileId());
        String emailName = recordNameManager.deriveEmailFriendlyName(profile.getId());
        Map<String, Object> templateParams = this.createTemplateParams(clientId, clientDetailsEntity.getClientName(), emailName, profile.getId());
        // Generate body from template
        String body = templateManager.processTemplate("bad_orgs_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("bad_orgs_email_html.ftl", templateParams);

        LOG.info("text email={}", body);
        LOG.info("html email={}", html);

        // Send the email
        boolean mailSent = mailGunManager.sendEmail(FROM_ADDRESS, emailManager.findPrimaryEmail(profile.getId()).getEmail(), SUBJECT, body, html);
        if (!mailSent) {
            throw new RuntimeException("Failed to send email for papi limits, orcid=" + profile.getId());
        }
    }

}
