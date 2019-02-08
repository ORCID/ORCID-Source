package org.orcid.frontend.web.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.impl.StatisticsCacheManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.frontend.web.forms.LoginForm;
import org.orcid.frontend.web.forms.validate.OrcidUrlValidator;
import org.orcid.frontend.web.forms.validate.RedirectUriValidator;
import org.orcid.frontend.web.util.CommonPasswords;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ajaxForm.VisibilityForm;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.ReleaseNameUtils;
import org.orcid.utils.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseController {
    
    private static final Locale defaultLocale = Locale.US;

    String[] urlValschemes = { "http", "https", "ftp" }; // DEFAULT schemes =
                                                         // "http", "https",
                                                         // "ftp"
    UrlValidator urlValidator = new OrcidUrlValidator(urlValschemes);
    
    String[] redirectUriSchemes = { "http", "https" };
    UrlValidator redirectUriValidator = new RedirectUriValidator(redirectUriSchemes);

    private String devSandboxUrl;

    private BaseControllerUtil baseControllerUtil = new BaseControllerUtil();
    
    private String aboutUri;    

    private boolean reducedFunctionalityMode;

    private String maintenanceMessage;

    private URL maintenanceHeaderUrl;

    private String googleAnalyticsTrackingId;

    protected List<String> domainsAllowingRobots;

    protected static final String STATIC_FOLDER_PATH = "/static/" + ReleaseNameUtils.getReleaseName();

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);    

    private String staticContentPath;

    private String staticCdnPath;

    @Resource
    private String cdnConfigFile;

    @Resource
    protected LocaleManager localeManager;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource(name = "emailManagerV3")
    protected EmailManager emailManager;
    
    @Resource
    private StatisticsCacheManager statisticsCacheManager;

    @Resource
    protected OrcidUrlManager orcidUrlManager;

    @Resource(name = "sourceManagerV3")
    protected SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    protected OrcidSecurityManager orcidSecurityManager;

    @Resource
    private InternalSSOManager internalSSOManager;
    
    @Resource
    private SecurityQuestionManager securityQuestionManager;
    
    protected static final String EMPTY = "empty";

    @Value("${org.orcid.recaptcha.web_site_key:}")
    private String recaptchaWebKey;
    
    @Value("${org.orcid.shibboleth.enabled:false}")
    private boolean shibbolethEnabled;

    @Resource(name = "profileEntityManagerV3")
    protected ProfileEntityManager profileEntityManager;
    
    @Resource(name = "emailManagerReadOnlyV3")
    protected EmailManagerReadOnly emailManagerReadOnly;
    
    @ModelAttribute("recaptchaWebKey")
    public String getRecaptchaWebKey() {
        return recaptchaWebKey;
    }

    public void setRecaptchaWebKey(String recaptchaWebKey) {
        this.recaptchaWebKey = recaptchaWebKey;
    }
    
    @ModelAttribute("shibbolethEnabled")
    public boolean isShibbolethEnabled() {
        return shibbolethEnabled;
    }

    public void setShibbolethEnabled(boolean shibbolethEnabled) {
        this.shibbolethEnabled = shibbolethEnabled;
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

    protected OrcidProfileUserDetails getCurrentUser() {
        return baseControllerUtil.getCurrentUser(SecurityContextHolder.getContext());
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
        Locale locale = RequestContextUtils.getLocale(request);
        org.orcid.pojo.Local lPojo = new org.orcid.pojo.Local();
        lPojo.setLocale(locale.toString());
        
        ResourceBundle definitiveProperties = ResourceBundle.getBundle("i18n/javascript", defaultLocale, new UTF8Control());
        Map<String, String> definitivePropertyMap = OrcidStringUtils.resourceBundleToMap(definitiveProperties);
        
        ResourceBundle resources = ResourceBundle.getBundle("i18n/javascript", locale, new UTF8Control());
        Map<String, String> localPropertyMap = OrcidStringUtils.resourceBundleToMap(resources);
        
        if (!defaultLocale.equals(locale)) {
            for (String propertyKey : definitivePropertyMap.keySet()) {
                String property = localPropertyMap.get(propertyKey);
                if (StringUtils.isBlank(property)) {
                    localPropertyMap.put(propertyKey, definitivePropertyMap.get(propertyKey));
                }
            }
        }

        lPojo.setMessages(localPropertyMap);
        String messages = "";
        try {
            messages = StringEscapeUtils.escapeEcmaScript(new ObjectMapper().writeValueAsString(lPojo));
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
                if (profileEntityManager.isProfileClaimedByEmail(email)) {
                    String[] codes = null;
                    String[] args = { email };
                    if (isRegisterRequest) {
                        if (profileEntityManager.isDeactivated(emailManager.findOrcidIdByEmail(email))) {
                            codes = new String[] { "orcid.frontend.verify.deactivated_email" };
                        } else {
                            codes = new String[] { "orcid.frontend.verify.duplicate_email" };
                        }
                        bindingResult.addError(new FieldError("email", "email", email, false, codes, args, "Email already exists"));
                    } else {
                        bindingResult.addError(new FieldError("email", "email", getVerifyClaimedMessage(email)));
                    }
                } else {
                    String resendUrl = createResendClaimUrl(email, request);
                    String message = getVerifyUnclaimedMessage(email, resendUrl);
                    bindingResult.addError(new FieldError("email", "email", message));
                }
            }
        }
    }
    
    protected String getVerifyUnclaimedMessage(String email, String resendUrl) {
        String message = getMessage("orcid.frontend.verify.unclaimed_email_1", email);
        message += "<a href='" + resendUrl + "'>";
        message += getMessage("orcid.frontend.verify.unclaimed_email_2");
        message += "</a>";
        message += getMessage("orcid.frontend.verify.unclaimed_email_3");
        return message;
    }
    
    private String getVerifyClaimedMessage(String email) {
        String message = getMessage("orcid.frontend.verify.claimed_email_1", email);
        message += "<a href=\"" + orcidUrlManager.getBaseUrl() + "/account#editDeprecate\" target=\"deprecate\">";
        message += getMessage("orcid.frontend.verify.claimed_email_2");
        message += "</a>" + getMessage("orcid.frontend.verify.claimed_email_3");
        return message;
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
        	return EmailValidator.getInstance().isValid(email);
        }
        return false;
    }

    protected String createResendClaimUrl(String email, HttpServletRequest request) {
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
            this.staticContentPath = generatedStaticContentPath + STATIC_FOLDER_PATH;
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
                if (uri != null) {
                    String releaseVersion = ReleaseNameUtils.getReleaseName();
                    if(!uri.contains(releaseVersion)) {
                        if(!uri.endsWith("/")) {
                            uri += '/';
                        }
                        uri += releaseVersion;
                    }
                    this.staticCdnPath = uri;
                }
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
        validateUrl(url, "common.invalid_url");
    }
    
    protected void validateUrl(Text urlString, String errorCode) {
        urlString.setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(urlString.getValue())) {
            urlString.setValue(urlString.getValue().trim());
            validateNoLongerThan(SiteConstants.URL_MAX_LENGTH, urlString);


            // add protocol if missing
            boolean valid = false;
            try {
                valid = urlValidator.isValid(encodeUrl(urlString.getValue()));
                if (!valid) {
                    String tempUrl = encodeUrl("http://" + urlString.getValue());
                    // test validity again
                    valid = urlValidator.isValid(tempUrl);
                    if (valid) {
                        urlString.setValue("http://" + urlString.getValue());
                    } 
                }
            } catch (Exception e) {
            }

            if (!valid) {
                setError(urlString, errorCode);
            }
        }
    }
    
    private String encodeUrl(String urlString) throws MalformedURLException, URISyntaxException {
        URL url = new URL(urlString);
        URI encoded = new URI(url.getProtocol(), url.getHost(), url.getPath(), null);
        return encoded.toASCIIString();
    }
    
    /**
     * Checks if a redirect uri contains a valid URI associated to it
     * 
     * @param redirectUri
     * @return null if there are no errors, an List of strings containing error
     *         messages if any error happens
     * */
    protected RedirectUri validateRedirectUri(RedirectUri redirectUri) {
        redirectUri.setErrors(new ArrayList<String>());
        if (!PojoUtil.isEmpty(redirectUri.getValue())) {
            try {
                String redirectUriString = redirectUri.getValue().getValue();
                if (!redirectUriValidator.isValid(redirectUriString)) {
                    redirectUriString = "http://" + redirectUriString;
                    if (redirectUriValidator.isValid(redirectUriString)) {
                        redirectUri.getValue().setValue(redirectUriString);
                    } else {
                        redirectUri.getErrors().add(getMessage("manage.developer_tools.invalid_redirect_uri"));
                    }
                }
            } catch (NullPointerException npe) {
                redirectUri.getErrors().add(getMessage("manage.developer_tools.empty_redirect_uri"));
            }
        } else {
            redirectUri.getErrors().add(getMessage("manage.developer_tools.empty_redirect_uri"));
        }

        return redirectUri;
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
        if(Features.HTTPS_IDS.isActive()) {
            return getPubBaseUri() + "/v2.1/search/";
        } else {
            return getPubBaseUri() + "/v1.2/search/orcid-bio/";
        }  
        
    }

    protected String calculateRedirectUrl(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = null;
        Boolean isOauth2ScreensRequest = (Boolean) request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_2SCREENS);
        if(isOauth2ScreensRequest != null && isOauth2ScreensRequest) {
            // Just redirect to the authorization screen
            String queryString = (String) request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_QUERY_STRING);
            targetUrl = orcidUrlManager.getBaseUrl() + "/oauth/authorize?" + queryString;
            request.getSession().removeAttribute(OrcidOauth2Constants.OAUTH_2SCREENS);
        } else {
            targetUrl = orcidUrlManager.determineFullTargetUrlFromSavedRequest(request, response);            
        }
        
        return targetUrl == null ? getBaseUri() + "/my-orcid" : targetUrl;
    }

    protected void passwordConfirmValidate(Text passwordConfirm, Text password) {
        passwordConfirm.setErrors(new ArrayList<String>());
        // validate passwords match
        if (passwordConfirm.getValue() == null || !passwordConfirm.getValue().equals(password.getValue())) {
            setError(passwordConfirm, "FieldMatch.registrationForm");
        }
    }

    protected void passwordValidate(Text passwordConfirm, Text password) {
        password.setErrors(new ArrayList<String>());
        // validate password regex
        if (password.getValue() == null || !password.getValue().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(password, "Pattern.registrationForm.password");
        }
        
        if (CommonPasswords.passwordIsCommon(password.getValue())) {
            setError(password, "password.too_common", password.getValue());
        }

        if (passwordConfirm.getValue() != null) {
            passwordConfirmValidate(passwordConfirm, password);
        }
        
    }
    
    void activitiesVisibilityDefaultValidate(Visibility activitiesVisibilityDefault) {
        // validate given name isn't blank
        activitiesVisibilityDefault.setErrors(new ArrayList<String>());
        if (activitiesVisibilityDefault.getVisibility() == null) {
            setError(activitiesVisibilityDefault, "NotBlank.registrationForm.defaultVisibility");
        }
    }
    
    protected void termsOfUserValidate(Checkbox termsOfUser) {
        termsOfUser.setErrors(new ArrayList<String>());
        if (termsOfUser.getValue() != true) {
            setError(termsOfUser, "validations.acceptTermsAndConditions");
        }
    }
    
    @ModelAttribute("securityQuestions")
    public Map<String, String> retrieveSecurityQuestionsAsMap() {
        Map<String, String> securityQuestions = securityQuestionManager.retrieveSecurityQuestionsAsInternationalizedMap();
        Map<String, String> securityQuestionsWithMessages = new LinkedHashMap<String, String>();

        for (String key : securityQuestions.keySet()) {
            securityQuestionsWithMessages.put(key, getMessage(securityQuestions.get(key)));
        }

        return securityQuestionsWithMessages;
    }

    protected Map<String, String> generateSalesForceRoleMap() {
        Map<String, String> roleMap = new HashMap<>();
        for(ContactRoleType roleType : ContactRoleType.values()){
            roleMap.put(roleType.name(), getMessage(buildInternationalizationKey(ContactRoleType.class, roleType.name())));
        }
        return roleMap;
    }
    
    @ModelAttribute("FEATURE")
    public Map<String, Boolean> getFeatures() {
        Map<String, Boolean> features = new HashMap<String, Boolean>();
        for(Features f : Features.values()) {
            features.put(f.name(), f.isActive());
        }
        return features;
    }
    
    @ModelAttribute("featuresJson")
    public String getFeaturesJson() {
        Map<String, Boolean> features = new HashMap<String, Boolean>();
        for(Features f : Features.values()) {
            features.put(f.name(), f.isActive());
        }
        
        String featuresJson = "";
        try {
            featuresJson = StringEscapeUtils.escapeEcmaScript(new ObjectMapper().writeValueAsString(features));
        } catch (IOException e) {
            LOGGER.error("getFeaturesJson error:" + e.toString(), e);
        }

        return featuresJson;
    }
    
    protected void validateVisibility(VisibilityForm form) {
        if(form == null) {
            return;
        }
        if(form.getVisibility() == null) {
            setError(form, "common.visibility.not_blank");           
        } else if(form.getVisibility().getVisibility() == null) {
            setError(form.getVisibility(), "common.visibility.not_blank"); 
        }
    }
    
    public void additionalEmailsValidateOnRegister(HttpServletRequest request, Registration reg) {
        if(reg.getEmailsAdditional() != null && !reg.getEmailsAdditional().isEmpty()) {
            Iterator<Text> it = reg.getEmailsAdditional().iterator();
            while(it.hasNext()) {
                Text additionalEmail = it.next();
                if(PojoUtil.isEmpty(additionalEmail)) {
                    it.remove();                    
                } else {
                    additionalEmailValidateOnRegister(request, reg, additionalEmail); 
                }
            }            
        }
    }
    
    private void additionalEmailValidateOnRegister(HttpServletRequest request, Registration reg, Text email) {
        email.setErrors(new ArrayList<String>());
        additionalEmailValidate(reg, email);
        if(email.getErrors().isEmpty()) {
            String emailValue = email.getValue();
            if(emailManager.emailExists(email.getValue())) {
                String orcid = emailManager.findOrcidIdByEmail(emailValue);
                //If it is claimed, should return a duplicated exception
                if(profileEntityManager.isDeactivated(orcid)) {
                    email.getErrors().add("orcid.frontend.verify.deactivated_email");
                } else if(profileEntityManager.isProfileClaimedByEmail(emailValue)) {                                                                        
                    email.getErrors().add("orcid.frontend.verify.duplicate_email");
                } else if(!emailManager.isAutoDeprecateEnableForEmail(emailValue)) {
                    //If the email is not eligible for auto deprecate, we should show an email duplicated exception                        
                    String resendUrl = createResendClaimUrl(emailValue, request);
                    String message = getVerifyUnclaimedMessage(emailValue, resendUrl);
                    email.getErrors().add(message);                                    
                } else {
                    LOGGER.info("Email " + emailValue + " belongs to a unclaimed record and can be auto deprecated");
                }
            }
        }
    }
    
    public void additionalEmailValidateOnReactivate(HttpServletRequest request, Registration reg, Text email, String userOrcid) {
        email.setErrors(new ArrayList<String>());
        additionalEmailValidate(reg, email);
        if(email.getErrors().isEmpty()) {
            String emailValue = email.getValue();
            if(emailManager.emailExists(email.getValue())) {
                String orcid = emailManager.findOrcidIdByEmail(emailValue);
                if(!userOrcid.contentEquals(orcid)) {
                    email.getErrors().add("unavailable");
                }
            }
        }
    }
    
    private void additionalEmailValidate(Registration reg, Text email) {
        String emailAddressAdditional = email.getValue();
        // Validate the email address is ok        
        if(!validateEmailAddress(emailAddressAdditional)) {
            email.getErrors().add(getMessage("Email.personalInfoForm.email", emailAddressAdditional));
        } else if(emailAddressAdditional.equalsIgnoreCase(reg.getEmail().getValue())){
            email.getErrors().add(getMessage("Email.personalInfoForm.additionalEmailCannotMatchPrimary"));
        } else if (duplicateAdditionalEmails(reg, emailAddressAdditional)){
            email.getErrors().add(getMessage("Email.personalInfoForm.additionalEmailCannotMatchAdditional"));
        }
    }
        
    public boolean duplicateAdditionalEmails(Registration reg, String emailAddressAdditional) {
        int count = 0;
        for(Text emailCheckAdditional : reg.getEmailsAdditional()){
            if(emailAddressAdditional.equalsIgnoreCase(emailCheckAdditional.getValue())){
                count++;
            }
        }
        if(count > 1){
            return true;
        } else {
            return false;
        }
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
    
    protected String getCurrentUserOrcid() {
        return getEffectiveUserOrcid();
    }

}
