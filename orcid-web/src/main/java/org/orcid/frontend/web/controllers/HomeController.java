package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.security.OrcidRoles;
import org.orcid.core.stats.StatisticsManager;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.UTF8Control;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PublicRecordPersonDetails;
import org.orcid.pojo.UserStatus;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class HomeController extends BaseController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    
    private static final Locale DEFAULT_LOCALE = Locale.US;
    
    private static final String JSESSIONID = "JSESSIONID";

    @Value("${org.orcid.core.aboutUri:http://about.orcid.org}")
    private String aboutUri;
    
    @Value("${org.orcid.recaptcha.web_site_key:}")
    private String recaptchaWebKey;
    
    @Value("${org.orcid.frontend.web.googleAnalyticsTrackingId:}")
    private String googleAnalyticsTrackingId;
    
    @Value("${org.orcid.frontend.web.maintenanceMessage:}")
    private String maintenanceMessage;
    
    @Value("${org.orcid.frontend.web.maintenanceHeaderUrl:}")
    private URL maintenanceHeaderUrl;
    
    @Resource
    private LocaleManager localeManager;        
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private StatisticsManager statisticsManager;

    @Resource(name = "emailManagerReadOnlyV3")
    protected EmailManagerReadOnly emailManagerReadOnly;

    @RequestMapping(value = "/")
    public ModelAndView homeHandler(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("home");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }
    
    @RequestMapping(value = "/home")
    public ModelAndView homeRedirect(HttpServletRequest request) {
        return new ModelAndView("redirect:" + orcidUrlManager.getBaseUrl());

    }

    @RequestMapping(value = "/robots.txt")
    public String dynamicRobots(HttpServletRequest request) throws NoHandlerFoundException {
        String requestedDomain = request.getServerName();
        if (domainsAllowingRobots.contains(requestedDomain)) {
        	HttpHeaders headers = new HttpHeaders();
            throw new NoHandlerFoundException(request.getMethod(), request.getRequestURL().toString(), headers);
        }
        return "robots";
    }

    @RequestMapping(value = "/lang.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    org.orcid.pojo.Local langJson(HttpServletRequest request, @RequestParam(value = "lang", required = false) String lang) {
        if (lang != null) {
            String orcid = getRealUserOrcid();
            if (orcid != null) {
                profileEntityManager.updateLocale(orcid, AvailableLocales.fromValue(lang));
            }
        }

        Locale locale = RequestContextUtils.getLocale(request);
        return localeManager.getJavascriptMessages(locale);

    }
    
    @RequestMapping(value = "/userStatus.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    Object getUserStatusJson(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "logUserOut", required = false) Boolean logUserOut) {

        String orcid = getCurrentUserOrcid();
        
        if(!Boolean.TRUE.equals(logUserOut)) {
            request.setAttribute("skipAccessLog", true);
            request.setAttribute("isUserStatus", true);
        }
        
        if (logUserOut != null && logUserOut.booleanValue()) {
            removeJSessionIdCookie(request, response);
            SecurityContextHolder.clearContext();
            if(request.getSession(false) != null) {
                request.getSession().invalidate();
            }   
            
            logoutCurrentUser(request, response);
            
            UserStatus us = new UserStatus();
            us.setLoggedIn(false);
            return us;
        } else {
            UserStatus us = new UserStatus();
            us.setLoggedIn((orcid != null));            
            return us;
        }                                            
    }
    
    private void removeJSessionIdCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        // Delete cookie and token associated with that cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (JSESSIONID.equals(cookie.getName())) {
                    cookie.setValue(StringUtils.EMPTY);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    @RequestMapping(value = "/userInfo.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getUserInfo(HttpServletRequest request) {
        Map<String, String> info = new HashMap<String, String>();        
        UserDetails userDetails = getCurrentUser();
        if(userDetails != null) {
            String effectiveOrcid = getEffectiveUserOrcid();
            String realUserOrcid = getRealUserOrcid();
            info.put("REAL_USER_ORCID", realUserOrcid);
            // REAL_USER_ORCID = EFFECTIVE_USER_ORCID unless it is in delegation mode
            info.put("EFFECTIVE_USER_ORCID", effectiveOrcid);
            info.put("IN_DELEGATION_MODE", String.valueOf(!effectiveOrcid.equals(realUserOrcid)));
            //TODO: Do we need the primary email in the user info?
            info.put("PRIMARY_EMAIL", emailManagerReadOnly.findPrimaryEmailValueFromCache(effectiveOrcid));
            info.put("HAS_VERIFIED_EMAIL", String.valueOf(emailManagerReadOnly.haveAnyEmailVerified(effectiveOrcid)));
            info.put("IS_PRIMARY_EMAIL_VERIFIED", String.valueOf(emailManagerReadOnly.isPrimaryEmailVerified(effectiveOrcid)));
            for(GrantedAuthority role : userDetails.getAuthorities()) {
                OrcidRoles orcidRole = OrcidRoles.valueOf(role.getAuthority());
                switch (orcidRole) {
                case ROLE_USER:
                    break;
                case ROLE_ADMIN:
                    info.put("ADMIN_MENU", String.valueOf(true));
                    break;
                case ROLE_GROUP:
                case ROLE_BASIC:
                case ROLE_PREMIUM:
                case ROLE_BASIC_INSTITUTION:
                case ROLE_PREMIUM_INSTITUTION:
                    info.put("MEMBER_MENU", String.valueOf(true));
                    break;
                case ROLE_CREATOR:
                case ROLE_PREMIUM_CREATOR:
                case ROLE_UPDATER:
                case ROLE_PREMIUM_UPDATER:
                    info.put("CLIENT_MENU", String.valueOf(true));
                    break;
                case ROLE_SELF_SERVICE:
                    info.put("SELF_SERVICE_MENU", String.valueOf(true));
                }                
            }
            if(sourceManager.isDelegatedByAnAdmin()) {
                info.put("DELEGATED_BY_ADMIN", String.valueOf(true));
            }   
            ProfileEntity p = profileEntityCacheManager.retrieve(effectiveOrcid);
            info.put("LAST_MODIFIED", String.valueOf(p.getLastModified()));
            info.put("DEVELOPER_TOOLS_ENABLED", String.valueOf(p.getEnableDeveloperTools()));
            info.put("LOCKED", String.valueOf(!p.isAccountNonLocked()));
            info.put("CLAIMED", String.valueOf(p.getClaimed()));
            if(p.getPrimaryRecord() != null) {
                info.put("PRIMARY_RECORD", p.getPrimaryRecord().getId());                
            }
            if(!PojoUtil.isEmpty(p.getGroupType())) {
                info.put("MEMBER_TYPE", p.getGroupType());
            }            
        }
        return info;
    }
    
    @RequestMapping(value = "/person.json", method = RequestMethod.GET)
    public @ResponseBody PublicRecordPersonDetails getPersonDetails() {
        return getPersonDetails(getCurrentUserOrcid(), true);        
    }
    
    @RequestMapping(value = "/config.json", method = RequestMethod.GET)
    public @ResponseBody ConfigDetails getConfigDetails(HttpServletRequest request) {
        ConfigDetails configDetails = new ConfigDetails();
        configDetails.setMessage("RECAPTCHA_WEB_KEY", recaptchaWebKey);
        configDetails.setMessage("BASE_DOMAIN_RM_PROTOCALL", orcidUrlManager.getBaseDomainRmProtocall());
        configDetails.setMessage("PUB_BASE_URI", orcidUrlManager.getPubBaseUrl());             
        configDetails.setMessage("STATIC_PATH", getStaticContentPath(request));
        configDetails.setMessage("SHIBBOLETH_ENABLED", String.valueOf(isShibbolethEnabled()));
        configDetails.setMessage("ABOUT_URI", aboutUri);
        configDetails.setMessage("GA_TRACKING_ID", googleAnalyticsTrackingId);
        configDetails.setMessage("MAINTENANCE_MESSAGE", getMaintenanceMessage());
        configDetails.setMessage("LIVE_IDS", statisticsManager.getFormattedLiveIds(localeManager.getLocale()));   
        configDetails.setMessage("SEARCH_BASE", getSearchBaseUrl());
        // Add features
        for(Features f : Features.values()) {
            configDetails.setMessage(f.name(), String.valueOf(f.isActive()));
        }
        return configDetails;        
    }
    
    @RequestMapping(value = "/messages.json", method = RequestMethod.GET)
    public @ResponseBody org.orcid.pojo.Local getJavascriptMessagesEndpoint(HttpServletRequest request) {
        Locale locale = RequestContextUtils.getLocale(request);
        org.orcid.pojo.Local lPojo = new org.orcid.pojo.Local();
        lPojo.setLocale(locale.toString());
        
        ResourceBundle resources = ResourceBundle.getBundle("i18n/javascript", locale, new UTF8Control());
        Map<String, String> localPropertyMap = OrcidStringUtils.resourceBundleToMap(resources);
        
        if (!DEFAULT_LOCALE.equals(locale)) {
            ResourceBundle definitiveProperties = ResourceBundle.getBundle("i18n/javascript", DEFAULT_LOCALE, new UTF8Control());
            Map<String, String> definitivePropertyMap = OrcidStringUtils.resourceBundleToMap(definitiveProperties);
            
            for (String propertyKey : definitivePropertyMap.keySet()) {
                String property = localPropertyMap.get(propertyKey);
                if (StringUtils.isBlank(property)) {
                    localPropertyMap.put(propertyKey, definitivePropertyMap.get(propertyKey));
                }
            }
        }

        lPojo.setMessages(localPropertyMap);
        return lPojo;
    }

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
    
    protected String getSearchBaseUrl() {
        return orcidUrlManager.getPubBaseUrl() + "/v3.0/search/";                 
    }
    
    class ConfigDetails {
        private Map<String, String> messages = new HashMap<String, String>();
        
        public Map<String, String> getMessages() {
            return messages;
        }

        public void setMessage(String key, String value) {
            this.messages.put(key, value);
        }
    }

}
