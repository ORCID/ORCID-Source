package org.orcid.api.filters;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.oauth.service.OrcidTokenStore;
import org.orcid.core.utils.OrcidRequestUtil;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;
import org.orcid.utils.email.MailGunManager;
import org.orcid.utils.panoply.PanoplyPapiDailyRateExceededItem;
import org.orcid.utils.panoply.PanoplyRedshiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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

    @Resource
    private PanoplyRedshiftClient panoplyClient;

    @Autowired
    private OrcidTokenStore orcidTokenStore;

    @Autowired
    private MessageSource messageSource;

    @Value("${org.orcid.papi.rate.limit.anonymous.requests:10000}")
    private int anonymousRequestLimit;

    @Value("${org.orcid.papi.rate.limit.known.requests:40000}")
    private int knownRequestLimit;

    @Value("${org.orcid.papi.rate.limit.enabled:false}")
    private boolean enableRateLimiting;

    @Value("${org.orcid.persistence.panoply.papiExceededRate.production:false}")
    private boolean enablePanoplyPapiExceededRateInProduction;

    @Value("${org.orcid.papi.rate.limit.ip.whiteSpaceSeparatedWhiteList:127.0.0.1}")
    private String papiWhiteSpaceSeparatedWhiteList;

    private static final String TOO_MANY_REQUESTS_MSG = "Too Many Requests - You have exceeded the daily allowance of API calls.\\n"
            + "You can increase your daily quota by registering for and using Public API client credentials "
            + "(https://info.orcid.org/documentation/integration-guide/registering-a-public-api-client/ )";

    private static final String SUBJECT = "[ORCID] You have exceeded the daily Public API Usage Limit - ";
    
    @Value("${org.orcid.papi.rate.limit.fromEmail:notify@notify.orcid.org}")
    private String FROM_ADDRESS;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        LOG.trace("ApiRateLimitFilter starts, rate limit is : " + enableRateLimiting);
        if (enableRateLimiting) {
            String tokenValue = null;
            if (httpServletRequest.getHeader("Authorization") != null) {
                tokenValue = httpServletRequest.getHeader("Authorization").replaceAll("Bearer|bearer", "").trim();
            }
            String ipAddress = getClientIpAddress(httpServletRequest);

            String clientId = null;
            if (tokenValue != null) {
                try {
                    clientId = orcidTokenStore.readClientId(tokenValue);
                } catch (Exception ex) {
                    LOG.error("Exception when trying to get the client id from token value, ignoring and treating as anonymous client", ex);
                }
            }
            boolean isAnonymous = (clientId == null);
            LocalDate today = LocalDate.now();
            try {
                if (isAnonymous) {
                    if (!isWhiteListed(ipAddress)) {
                        LOG.info("ApiRateLimitFilter anonymous request for ip: " + ipAddress);
                        this.rateLimitAnonymousRequest(ipAddress, today, httpServletResponse);
                    }

                } else {
                    LOG.info("ApiRateLimitFilter client request with clientId: " + clientId);
                    this.rateLimitClientRequest(clientId, today);
                }
            } catch (Exception ex) {
                LOG.error("Papi Limiting Filter unexpected error, ignore and chain request.", ex);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void rateLimitAnonymousRequest(String ipAddress, LocalDate today, HttpServletResponse httpServletResponse) throws IOException {
        PublicApiDailyRateLimitEntity rateLimitEntity = papiRateLimitingDao.findByIpAddressAndRequestDate(ipAddress, today);
        if (rateLimitEntity != null) {
            // update the request count only when limit not exceeded ?
            rateLimitEntity.setRequestCount(rateLimitEntity.getRequestCount() + 1);
            papiRateLimitingDao.updatePublicApiDailyRateLimit(rateLimitEntity, false);
            if (rateLimitEntity.getRequestCount() == knownRequestLimit && enablePanoplyPapiExceededRateInProduction) {
                PanoplyPapiDailyRateExceededItem item = new PanoplyPapiDailyRateExceededItem();
                item.setIpAddress(ipAddress);
                item.setRequestDate(rateLimitEntity.getRequestDate());
                setPapiRateExceededItemInPanoply(item);
            }
            if (Features.ENABLE_PAPI_RATE_LIMITING.isActive()) {
                if (rateLimitEntity.getRequestCount() >= anonymousRequestLimit) {
                    httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
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
                    sendEmail(clientId, rateLimitEntity.getRequestDate());
                }
            }
            // update the request count
            rateLimitEntity.setRequestCount(rateLimitEntity.getRequestCount() + 1);
            papiRateLimitingDao.updatePublicApiDailyRateLimit(rateLimitEntity, true);

        } else {
            // create
            rateLimitEntity = new PublicApiDailyRateLimitEntity();
            rateLimitEntity.setClientId(clientId);
            rateLimitEntity.setRequestCount(1L);
            rateLimitEntity.setRequestDate(today);
            papiRateLimitingDao.persist(rateLimitEntity);
        }

    }

    private Map<String, Object> createTemplateParams(String clientId, String clientName, String emailName, String orcidId) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("messages", messageSource);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("clientId", clientId);
        templateParams.put("clientName", clientName);
        templateParams.put("emailName", emailName);
        templateParams.put("locale", LocaleUtils.toLocale("en"));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", SUBJECT + orcidId);
        return templateParams;
    }

    private void sendEmail(String clientId, LocalDate requestDate) {
        ClientDetailsEntity clientDetailsEntity = clientDetailsEntityCacheManager.retrieve(clientId);
        ProfileEntity profile = profileDao.find(clientDetailsEntity.getGroupProfileId());
        String emailName = recordNameManager.deriveEmailFriendlyName(profile.getId());
        Map<String, Object> templateParams = this.createTemplateParams(clientId, clientDetailsEntity.getClientName(), emailName, profile.getId());
        // Generate body from template
        String body = templateManager.processTemplate("papi_rate_limit_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("papi_rate_limit_email_html.ftl", templateParams);
        String email = emailManager.findPrimaryEmail(profile.getId()).getEmail();
        LOG.info("from address={}", FROM_ADDRESS);
        LOG.info("text email={}", body);
        LOG.info("html email={}", html);
        if (enablePanoplyPapiExceededRateInProduction) {
            PanoplyPapiDailyRateExceededItem item = new PanoplyPapiDailyRateExceededItem();
            item.setClientId(clientId);
            item.setOrcid(profile.getId());
            item.setEmail(email);
            item.setRequestDate(requestDate);
            setPapiRateExceededItemInPanoply(item);
        }

        // Send the email
        boolean mailSent = mailGunManager.sendEmail(FROM_ADDRESS, email, SUBJECT, body, html);
        if (!mailSent) {
            LOG.error("Failed to send email for papi limits, orcid=" + profile.getId() + " email: " + email);
        }
    }

    private void setPapiRateExceededItemInPanoply(PanoplyPapiDailyRateExceededItem item) {
        // Store the rate exceeded item in panoply Db without blocking
        CompletableFuture.supplyAsync(() -> {
            try {
                panoplyClient.addPanoplyPapiDailyRateExceeded(item);
                return true;
            } catch (Exception e) {
                LOG.error("Cannot store the rateExceededItem to panoply ", e);
                return false;
            }
        }).thenAccept(result -> {
            if (!result) {
                LOG.error("Async call to panoply for : " + item.toString() + " Stored: " + result);
            }

        });
    }

    // gets actual client IP address, using the headers that the proxy server
    // ads
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-REAL-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    private boolean isWhiteListed(String ipAddress) {
        List<String> papiIpWhiteList = null;
        if (StringUtils.isNotBlank(papiWhiteSpaceSeparatedWhiteList)) {
            papiIpWhiteList = Arrays.asList(papiWhiteSpaceSeparatedWhiteList.split("\\s"));
        }

        if (papiIpWhiteList != null) {
            return papiIpWhiteList.contains(ipAddress);

        }
        return false;
    }

}
