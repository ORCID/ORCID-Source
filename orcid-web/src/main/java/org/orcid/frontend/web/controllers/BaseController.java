/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.codehaus.jackson.map.ObjectMapper;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.StatisticsManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.frontend.web.forms.LoginForm;
import org.orcid.frontend.web.forms.validate.OrcidUrlValidator;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.OrcidWebUtils;
import org.orcid.utils.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.RequestContextUtils;

public class BaseController {
    
    String[] urlValschemes = {"http","https", "ftp"}; // DEFAULT schemes = "http", "https", "ftp"
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

    static Pattern fileNamePattern = Pattern.compile("https{0,1}:\\/\\/[^\\/]*(.*){0,1}");

    @Value("${org.orcid.core.baseUri:http://orcid.org}")
    private String baseUri;

    @Value("${org.orcid.core.pubBaseUri:https://pub.orcid.org}")
    private String pubBaseUri;
    
    @Resource
    private String cdnConfigFile;

    @Resource
    private LocaleManager localeManager;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource
    protected EmailManager emailManager;

    @Resource
    private StatisticsManager statisticsManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private SourceManager sourceManager;

    protected static final String EMPTY = "empty";

    
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
        return startupDate;
    }

    protected OrcidProfileUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken && authentication.getPrincipal() instanceof OrcidProfileUserDetails) {
            return ((OrcidProfileUserDetails) authentication.getPrincipal());
        } else {
            return null;
        }
    }

    protected String getCurrentUserOrcid() {
        return getEffectiveUserOrcid();
    }

    protected void logoutCurrentUser() {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
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
        return sourceManager.retrieveSourceOrcid();
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
        validateEmailAddress(email, true, request, bindingResult);
    }

    protected void validateEmailAddress(String email, boolean ignoreCurrentUser, HttpServletRequest request, BindingResult bindingResult) {
        if (StringUtils.isNotBlank(email)) {
            if (!validateEmailAddress(email)) {
                String[] codes = { "Email.personalInfoForm.email" };
                String[] args = { email };
                bindingResult.addError(new FieldError("email", "email", email, false, codes, args, "Not vaild"));
            }
            if (!(ignoreCurrentUser && emailMatchesCurrentUser(email)) && emailManager.emailExists(email)) {
                OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfileByEmail(email);
                if (orcidProfile.getOrcidHistory().isClaimed()) {
                    String[] codes = { "orcid.frontend.verify.duplicate_email" };
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
     * */
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
        resendUrl.append(OrcidWebUtils.getServerStringWithContextPath(request));
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
        return statisticsManager.getLiveIds(localeManager.getLocale());
    }

    @ModelAttribute("baseUri")
    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }
    
    @ModelAttribute("pubBaseUri")
    public String getPubBaseUri() {
        return pubBaseUri;
    }

    public void setPubBaseUri(String pubBaseUri) {
        this.pubBaseUri = pubBaseUri;
    }

    /**
     * 
     * CDN Configuration
     * 
     * */
    public String getCdnConfigFile() {
        return this.cdnConfigFile;
    }

    public void setCdnConfigFile(String cdnConfigFile) {
        this.cdnConfigFile = cdnConfigFile;
    }

    /**
     * @return the path to the static content on local project
     * */
    @ModelAttribute("staticLoc")
    public String getStaticContentPath() {
        if (StringUtils.isBlank(this.staticContentPath)) {
            this.staticContentPath = this.baseUri + STATIC_FOLDER_PATH;
            this.staticContentPath = this.staticContentPath.replace("https:", "");
            this.staticContentPath = this.staticContentPath.replace("http:", "");
        }
        return this.staticContentPath;
    }

    /**
     * Return the path where the static content will be. If there is a cdn path
     * configured, it will return the cdn path; if it is not a cdn path it will
     * return a reference to the static folder "/static"
     * 
     * @return the path to the CDN or the path to the local static content
     * */
    @ModelAttribute("staticCdn")
    @Cacheable("staticContent")
    public String getStaticCdnPath() {
        if (StringUtils.isEmpty(this.cdnConfigFile)) {
            return getStaticContentPath();
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
            this.staticCdnPath = this.getStaticContentPath();
        return staticCdnPath;
    }

    @ModelAttribute("baseUriHttp")
    public String getBaseUriHttp() {
        return baseUri.replace("https", "http");
    }

    @ModelAttribute("basePath")
    public String getBasePath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(baseUri);
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1) + "/";
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
     * */
    protected String buildInternationalizationKey(Class theClass, String key) {
        return theClass.getName() + '.' + key;
    }

    protected static void copyErrors(ErrorsInterface from, ErrorsInterface into) {
        for (String s : from.getErrors()) {
            into.getErrors().add(s);
        }
    }

    protected void setError(ErrorsInterface ei, String msg) {
        ei.getErrors().add(getMessage(msg));
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
        url.setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(url.getValue())) {
           // trim if required
           if (!url.getValue().equals(url.getValue().trim())) 
               url.setValue(url.getValue().trim());
           
           // check length
           if (url.getValue().length() > 350)
              setError(url, "manualWork.length_less_350");
           
           // add protocall if missing
           if (!urlValidator.isValid(url.getValue())) {
              String tempUrl = "http://" + url.getValue();   
              // test validity again
              if (urlValidator.isValid(tempUrl))
                  url.setValue("http://" + url.getValue());
              else
                  setError(url, "common.invalid_url");
                  
           }
        }
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
        String baseUrlWithCorrectedProtocol = orcidUrlManager.getBaseUrl().replaceAll("^https?:", "");
        String baseUrlWithCorrectedContext = baseUrlWithCorrectedProtocol.replaceAll("/orcid-web$", "/orcid-pub-web");
        return baseUrlWithCorrectedContext + "/v1.1/search/orcid-bio/?q=";
    }

}
