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
package org.orcid.frontend.web.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.impl.StatisticsCacheManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.utils.JsonUtils;
import org.orcid.frontend.web.forms.LoginForm;
import org.orcid.frontend.web.forms.validate.OrcidUrlValidator;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SendEmailFrequency;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseController {

    String[] urlValschemes = { "http", "https", "ftp" }; // DEFAULT schemes =
                                                         // "http", "https",
                                                         // "ftp"
    UrlValidator urlValidator = new OrcidUrlValidator(urlValschemes);

    private String devSandboxUrl;

    private String aboutUri;

    private String knowledgeBaseUri;

    private boolean reducedFunctionalityMode;

    private String maintenanceMessage;

    private URL maintenanceHeaderUrl;

    private String googleAnalyticsTrackingId;

    protected List<String> domainsAllowingRobots;

    protected static final String STATIC_FOLDER_PATH = "/static";

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    private Date startupDate = new Date();

    private String staticContentPath;

    private String staticCdnPath;

    @Resource
    private String cdnConfigFile;

    @Resource
    private LocaleManager localeManager;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource
    protected EmailManager emailManager;

    @Resource
    private StatisticsCacheManager statisticsCacheManager;

    @Resource
    protected OrcidUrlManager orcidUrlManager;

    @Resource
    protected SourceManager sourceManager;

    @Resource
    protected OrcidSecurityManager orcidSecurityManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private InternalSSOManager internalSSOManager;

    @Resource
    protected CsrfTokenRepository csrfTokenRepository;

    protected static final String EMPTY = "empty";

    @Value("${org.orcid.recaptcha.web_site_key:}")
    private String recaptchaWebKey;

    @ModelAttribute("recaptchaWebKey")
    public String getRecaptchaWebKey() {
        return recaptchaWebKey;
    }

    public void setRecaptchaWebKey(String recaptchaWebKey) {
        this.recaptchaWebKey = recaptchaWebKey;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public OrcidProfileManager getOrcidProfileManager() {
        return orcidProfileManager;
    }

    public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

    @ModelAttribute("devSandboxUrl")
    public String getDevSandboxUrl() {
        return devSandboxUrl;
    }

    @Value("${org.orcid.frontend.web.devSandboxUrl:}")
    public void setDevSandboxUrl(String devSandboxUrl) {
        this.devSandboxUrl = devSandboxUrl;
    }

    @ModelAttribute("aboutUri")
    public String getAboutUri() {
        return aboutUri;
    }

    @ModelAttribute("knowledgeBaseUri")
    public String getKnowledgeBaseUri() {
        return knowledgeBaseUri;
    }

    @Value("${org.orcid.core.knowledgeBaseUri:http://support.orcid.org/knowledgebase}")
    public void setKnowledgeBaseUri(String knowledgeBaseUri) {
        this.knowledgeBaseUri = knowledgeBaseUri;
    }

    @Value("${org.orcid.core.aboutUri:http://about.orcid.org}")
    public void setAboutUri(String aboutUri) {
        this.aboutUri = aboutUri;
    }

    @ModelAttribute("reducedFunctionalityMode")
    public boolean isReducedFunctionalityMode() {
        return reducedFunctionalityMode;
    }

    @Value("${org.orcid.frontend.web.reducedFunctionalityMode:false}")
    public void setReducedFunctionalityMode(boolean reducedFunctionalityMode) {
        this.reducedFunctionalityMode = reducedFunctionalityMode;
    }

    @ModelAttribute("googleAnalyticsTrackingId")
    public String getGoogleAnalyticsTrackingId() {
        return googleAnalyticsTrackingId;
    }

    @Value("${org.orcid.frontend.web.googleAnalyticsTrackingId:}")
    public void setGoogleAnalyticsTrackingId(String googleAnalyticsTrackingId) {
        this.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
    }

    @ModelAttribute("maintenanceMessage")
    public String getMaintenanceMessage() {
        if (maintenanceHeaderUrl != null) {
            try {
                String maintenanceHeader = IOUtils.toString(maintenanceHeaderUrl);
                if (StringUtils.isNotBlank(maintenanceHeader)) {
                    return maintenanceHeader;
                }
            } catch (IOException e) {
                LOGGER.debug("Error reading maintenance header", e);
            }
        }
        return maintenanceMessage;
    }

    @ModelAttribute("sendEmailFrequencies")
    public Map<String, String> retrieveEmailFrequenciesAsMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (SendEmailFrequency freq : SendEmailFrequency.values()) {
            map.put(String.valueOf(freq.value()), getMessage(buildInternationalizationKey(SendEmailFrequency.class, freq.name())));
        }
        return map;
    }

    /**
     * Use maintenanceHeaderUrl instead
     */
    @Deprecated
    @Value("${org.orcid.frontend.web.maintenanceMessage:}")
    public void setMaintenanceMessage(String maintenanceMessage) {
        this.maintenanceMessage = maintenanceMessage;
    }

    public URL getMaintenanceHeaderUrl() {
        return maintenanceHeaderUrl;
    }

    @Value("${org.orcid.frontend.web.maintenanceHeaderUrl:}")
    public void setMaintenanceHeaderUrl(URL maintenanceHeaderUrl) {
        this.maintenanceHeaderUrl = maintenanceHeaderUrl;
    }

    @Value("${org.orcid.frontend.web.domainsAllowingRobotsAsWhiteSpaceSeparatedList:orcid.org}")
    public void setDomainsAllowingRobots(String whitespaceSeparatedDomains) {
        domainsAllowingRobots = Arrays.asList(whitespaceSeparatedDomains.split("\\s"));
    }

    public void setDomainsAllowingRobots(List<String> domainsAllowingRobots) {
        this.domainsAllowingRobots = domainsAllowingRobots;
    }

    @ModelAttribute("visibilities")
    public Map<String, String> retrieveVisibilitiesAsMap() {
        Map<String, String> visibilities = new LinkedHashMap<String, String>();
        visibilities.put(Visibility.PUBLIC.value(), "Public");
        visibilities.put(Visibility.LIMITED.value(), "Limited");
        visibilities.put(Visibility.PRIVATE.value(), "Private");
        return visibilities;

    }

    @ModelAttribute("startupDate")
    public Date getStartupDate() {
        // If the cdn config file is missing, we are in development env and we
        // need to refresh the cache
        ClassPathResource configFile = new ClassPathResource(this.cdnConfigFile);
        if (!configFile.exists()) {
            return new Date();
        }

        return startupDate;
    }

    protected OrcidProfileUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof UsernamePasswordAuthenticationToken || authentication instanceof PreAuthenticatedAuthenticationToken)
                && authentication.getPrincipal() instanceof OrcidProfileUserDetails) {
            return ((OrcidProfileUserDetails) authentication.getPrincipal());
        } else {
            return null;
        }
    }

    protected String getCurrentUserOrcid() {
        return getEffectiveUserOrcid();
    }

    protected void logoutCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (internalSSOManager.enableCookie()) {
            Cookie[] cookies = request.getCookies();
            // Delete cookie and token associated with that cookie
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (InternalSSOManager.COOKIE_NAME.equals(cookie.getName())) {
                        try {
                            // If it is a valid cookie, extract the orcid value
                            // and
                            // remove the token and the cookie
                            @SuppressWarnings("unchecked")
                            HashMap<String, String> cookieValues = JsonUtils.readObjectFromJsonString(cookie.getValue(), HashMap.class);
                            if (cookieValues.containsKey(InternalSSOManager.COOKIE_KEY_ORCID)
                                    && !PojoUtil.isEmpty(cookieValues.get(InternalSSOManager.COOKIE_KEY_ORCID))) {
                                internalSSOManager.deleteToken(cookieValues.get(InternalSSOManager.COOKIE_KEY_ORCID), request, response);
                            } else {
                                // If it is not valid, just remove the cookie
                                cookie.setValue(StringUtils.EMPTY);
                                cookie.setMaxAge(0);
                                response.addCookie(cookie);
                            }
                        } catch (RuntimeException re) {
                            // If any exception happens, but, the cookie exists,
                            // remove the cookie
                            cookie.setValue(StringUtils.EMPTY);
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        }
                        break;
                    }
                }
            }
            // Delete token if exists
            if (authentication != null && !PojoUtil.isEmpty(authentication.getName())) {
                internalSSOManager.deleteToken(authentication.getName());
            }
        }
        if (authentication != null && authentication.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        CsrfToken token = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(token, request, response);
        request.setAttribute("_csrf", token);
    }

    protected boolean isEmailOkForCurrentUser(String decryptedEmail) {
        OrcidProfileUserDetails userDetails = getCurrentUser();
        if (userDetails == null) {
            return true;
        }
        OrcidProfile orcidProfile = getEffectiveProfile();
        if (orcidProfile == null) {
            return true;
        }
        List<Email> emails = orcidProfile.getOrcidBio().getContactDetails().getEmail();
        for (Email email : emails) {
            if (decryptedEmail.equalsIgnoreCase(email.getValue())) {
                return true;
            }
        }
        return false;
    }

    @ModelAttribute("realUserOrcid")
    public String getRealUserOrcid() {
        return sourceManager.retrieveRealUserOrcid();
    }

    @ModelAttribute("effectiveUserOrcid")
    public String getEffectiveUserOrcid() {
        OrcidProfileUserDetails currentUser = getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getOrcid();
    }

    @ModelAttribute("inDelegationMode")
    public boolean isInDelegationMode() {
        return sourceManager.isInDelegationMode();
    }

    @ModelAttribute("isDelegatedByAdmin")
    public boolean isDelegatedByAdmin() {
        return sourceManager.isDelegatedByAnAdmin();
    }

    @ModelAttribute("isPasswordConfirmationRequired")
    public boolean isPasswordConfirmationRequired() {
        return orcidSecurityManager.isPasswordConfirmationRequired();
    }

    @ModelAttribute("request")
    public HttpServletRequest getRequest(HttpServletRequest request) {
        return request;
    }

    @ModelAttribute("loginForm")
    public LoginForm getLoginForm() {
        return new LoginForm();
    }

    @ModelAttribute("jsMessagesJson")
    public String getJavascriptMessages(HttpServletRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        Locale locale = RequestContextUtils.getLocale(request);
        org.orcid.pojo.Local lPojo = new org.orcid.pojo.Local();
        lPojo.setLocale(locale.toString());

        ResourceBundle resources = ResourceBundle.getBundle("i18n/javascript", locale, new UTF8Control());
        lPojo.setMessages(OrcidStringUtils.resourceBundleToMap(resources));
        String messages = "";
        try {
            messages = StringEscapeUtils.escapeEcmaScript(mapper.writeValueAsString(lPojo));
        } catch (IOException e) {
            LOGGER.error("getJavascriptMessages error:" + e.toString(), e);
        }
        return messages;
    }

    protected void validateEmailAddress(String email, HttpServletRequest request, BindingResult bindingResult) {
        validateEmailAddress(email, true, false, request, bindingResult);
    }

    protected void validateEmailAddressOnRegister(String email, HttpServletRequest request, BindingResult bindingResult) {
        validateEmailAddress(email, true, true, request, bindingResult);
    }

    protected void validateEmailAddress(String email, boolean ignoreCurrentUser, boolean isRegisterRequest, HttpServletRequest request, BindingResult bindingResult) {
        if (StringUtils.isNotBlank(email)) {
            if (!validateEmailAddress(email)) {
                String[] codes = { "Email.personalInfoForm.email" };
                String[] args = { email };
                bindingResult.addError(new FieldError("email", "email", email, false, codes, args, "Not vaild"));
            }
            if (!(ignoreCurrentUser && emailMatchesCurrentUser(email)) && emailManager.emailExists(email)) {
                OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfileByEmail(email);
                if (orcidProfile.getOrcidHistory().isClaimed()) {
                    String[] codes = null;
                    if (isRegisterRequest) {
                        codes = new String[] { "orcid.frontend.verify.duplicate_email" };
                    } else {
                        codes = new String[] { "orcid.frontend.verify.claimed_email" };
                    }
                    String[] args = { email };
                    bindingResult.addError(new FieldError("email", "email", email, false, codes, args, "Email already exists"));
                } else {
                    String resendUrl = createResendClaimUrl(email, request);
                    String[] codes = { "orcid.frontend.verify.unclaimed_email" };
                    String[] args = { email, resendUrl };
                    bindingResult.addError(new FieldError("email", "email", email, false, codes, args, "Unclaimed record exists"));
                }
            }
        }
    }

    /**
     * Validates if the provided string matches an email address pattern.
     * 
     * @param email
     *            The string to evaluate
     * @return true if the provided string matches an email address pattern,
     *         false otherwise.
     */
    protected boolean validateEmailAddress(String email) {
        if (StringUtils.isNotBlank(email)) {
            try {
                InternetAddress addr = new InternetAddress(email);
                addr.validate();
                return true;
            } catch (AddressException ex) {

            }
        }
        return false;
    }

    private String createResendClaimUrl(String email, HttpServletRequest request) {
        String urlEncodedEmail = null;
        try {
            urlEncodedEmail = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("Unable to url encode email address: {}", email, e);
        }
        StringBuilder resendUrl = new StringBuilder();
        resendUrl.append(orcidUrlManager.getServerStringWithContextPath(request));
        resendUrl.append("/resend-claim");
        if (urlEncodedEmail != null) {
            resendUrl.append("?email=");
            resendUrl.append(urlEncodedEmail);
        }
        return resendUrl.toString();
    }

    private boolean emailMatchesCurrentUser(String email) {
        OrcidProfileUserDetails currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        boolean match = false;
        for (Email cuEmail : getEffectiveProfile().getOrcidBio().getContactDetails().getEmail()) {
            if (cuEmail.getValue() != null && cuEmail.getValue().equalsIgnoreCase(email))
                match = true;
        }
        return match;
    }

    public OrcidProfile getEffectiveProfile() {
        String effectiveOrcid = getEffectiveUserOrcid();
        return effectiveOrcid == null ? null : orcidProfileManager.retrieveOrcidProfile(effectiveOrcid);
    }

    public OrcidProfile getRealProfile() {
        String realOrcid = getRealUserOrcid();
        return realOrcid == null ? null : orcidProfileManager.retrieveOrcidProfile(realOrcid);
    }

    public String getMessage(String messageCode, Object... messageParams) {
        return localeManager.resolveMessage(messageCode, messageParams);
    }

    @ModelAttribute("locale")
    public String getLocaleAsString() {
        return localeManager.getLocale().toString();
    }

    public Locale getLocale() {
        return localeManager.getLocale();
    }

    @ModelAttribute("liveIds")
    public String getLiveIds() {
        return statisticsCacheManager.retrieveLiveIds(localeManager.getLocale());
    }

    @ModelAttribute("baseUri")
    public String getBaseUri() {
        return orcidUrlManager.getBaseUrl();
    }

    @ModelAttribute("pubBaseUri")
    public String getPubBaseUri() {
        return orcidUrlManager.getPubBaseUrl();
    }

    /**
     * 
     * CDN Configuration
     * 
     */
    public String getCdnConfigFile() {
        return this.cdnConfigFile;
    }

    public void setCdnConfigFile(String cdnConfigFile) {
        this.cdnConfigFile = cdnConfigFile;
    }

    /**
     * @return the path to the static content on local project
     */
    @ModelAttribute("staticLoc")
    public String getStaticContentPath(HttpServletRequest request) {
        if (StringUtils.isBlank(this.staticContentPath)) {
            String generatedStaticContentPath = orcidUrlManager.getBaseUrl();
            generatedStaticContentPath = generatedStaticContentPath.replace("https:", "");
            generatedStaticContentPath = generatedStaticContentPath.replace("http:", "");
            if (!request.isSecure()) {
                generatedStaticContentPath = generatedStaticContentPath.replace(":8443", ":8080");
            }
            return generatedStaticContentPath + STATIC_FOLDER_PATH;
        }
        return this.staticContentPath;
    }

    /**
     * Return the path where the static content will be. If there is a cdn path
     * configured, it will return the cdn path; if it is not a cdn path it will
     * return a reference to the static folder "/static"
     * 
     * @return the path to the CDN or the path to the local static content
     */
    @ModelAttribute("staticCdn")
    @Cacheable("staticContent")
    public String getStaticCdnPath(HttpServletRequest request) {
        if (StringUtils.isEmpty(this.cdnConfigFile)) {
            return getStaticContentPath(request);
        }

        ClassPathResource configFile = new ClassPathResource(this.cdnConfigFile);
        if (configFile.exists()) {
            try (InputStream is = configFile.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String uri = br.readLine();
                if (uri != null)
                    this.staticCdnPath = uri;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (StringUtils.isBlank(this.staticCdnPath))
            return getStaticContentPath(request);
        return staticCdnPath;
    }

    @ModelAttribute("baseDomainRmProtocall")
    public String getBaseDomainRmProtocall() {
        return orcidUrlManager.getBaseDomainRmProtocall();
    }

    @ModelAttribute("baseUriHttp")
    public String getBaseUriHttp() {
        return orcidUrlManager.getBaseUriHttp();
    }

    @ModelAttribute("basePath")
    public String getBasePath() {
        return orcidUrlManager.getBasePath();
    }

    /**
     * A method that will help us to internationalize enums For each enum, the
     * value can be internationalized by adding a key of the form
     * full.class.name.key and then using this method to build the string for
     * that key.
     * 
     * @param theClass
     * @param key
     * @return a String of the form full.class.name.with.package.key
     */
    @SuppressWarnings("rawtypes")
    protected String buildInternationalizationKey(Class theClass, String key) {
        return theClass.getName() + '.' + key;
    }

    protected static void copyErrors(ErrorsInterface from, ErrorsInterface into) {
        if (from != null && from.getErrors() != null) {
            for (String s : from.getErrors()) {
                into.getErrors().add(s);
            }
        }
    }

    protected void setError(ErrorsInterface ei, String msg) {
        ei.getErrors().add(getMessage(msg));
    }

    protected void setError(ErrorsInterface ei, String msg, Object... messageParams) {
        ei.getErrors().add(getMessage(msg, messageParams));
    }

    protected void validateBiography(Text text) {
        text.setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(text.getValue())) {
            // trim if required
            if (!text.getValue().equals(text.getValue().trim()))
                text.setValue(text.getValue().trim());

            // check length
            if (text.getValue().length() > 5000)
                setError(text, "Length.changePersonalInfoForm.biography");
        }
    }

    protected void validateUrl(Text url) {
        validateUrl(url, SiteConstants.URL_MAX_LENGTH);
    }

    protected void validateUrl(Text url, int maxLength) {
        url.setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(url.getValue())) {
            // trim if required
            if (!url.getValue().equals(url.getValue().trim()))
                url.setValue(url.getValue().trim());

            // check length
            validateNoLongerThan(maxLength, url);

            // add protocall if missing
            if (!validateUrl(url.getValue())) {
                String tempUrl = "http://" + url.getValue();
                // test validity again
                if (validateUrl(tempUrl))
                    url.setValue("http://" + url.getValue());
                else
                    setError(url, "common.invalid_url");
            }
        }
    }

    protected boolean validateUrl(String url) {
        return urlValidator.isValid(url);
    }

    protected void validateNoLongerThan(int maxLength, Text text) {
        if (PojoUtil.isEmpty(text)) {
            return;
        }

        if (text.getValue().length() > maxLength) {
            setError(text, "manualWork.length_less_X", maxLength);
        }
    }

    protected boolean isLongerThan(String value, int maxLength) {
        if (value == null)
            return false;
        return (value.length() > maxLength);
    }

    void givenNameValidate(Text givenName) {
        // validate given name isn't blank
        givenName.setErrors(new ArrayList<String>());
        if (givenName.getValue() == null || givenName.getValue().trim().isEmpty()) {
            setError(givenName, "NotBlank.registrationForm.givenNames");
        }
    }

    @ModelAttribute("searchBaseUrl")
    protected String createSearchBaseUrl() {
        return getPubBaseUri() + "/v1.2/search/orcid-bio/";
    }

    @ModelAttribute("locked")
    public boolean isLocked() {
        String orcid = getCurrentUserOrcid();
        if (PojoUtil.isEmpty(orcid)) {
            return false;
        }
        return orcidProfileManager.isLocked(orcid);
    }

    protected String calculateRedirectUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = orcidUrlManager.determineFullTargetUrlFromSavedRequest(request, response);
        return targetUrl != null ? targetUrl : getBaseUri() + "/my-orcid";
    }

}
