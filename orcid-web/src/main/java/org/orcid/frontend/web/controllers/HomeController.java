package org.orcid.frontend.web.controllers;

import java.io.IOException;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.StatusManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PublicRecordPersonDetails;
import org.orcid.pojo.UserStatus;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.UTF8Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class HomeController extends BaseController {
    
    private static final Locale DEFAULT_LOCALE = Locale.US;
    
    @Value("${org.orcid.recaptcha.web_site_key:}")
    private String recaptchaWebKey;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private InternalSSOManager internalSSOManager;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private StatusManager statusManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @RequestMapping(value = "/")
    public ModelAndView homeHandler(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("showSecondaryMenu", true);
        return mav; 
    }
    
    @RequestMapping(value = "/home")
    public ModelAndView homeRedirect(HttpServletRequest request) {
        return new ModelAndView("redirect:/");

    }

    @RequestMapping(value = "/robots.txt")
    public String dynamicRobots(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        String requestedDomain = request.getServerName();
        if (domainsAllowingRobots.contains(requestedDomain)) {
            throw new NoSuchRequestHandlingMethodException(request);
        }
        return "robots";
    }

    @RequestMapping(value = "/lang.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    org.orcid.pojo.Local langJson(HttpServletRequest request, @RequestParam(value = "lang", required = false) String lang) throws NoSuchRequestHandlingMethodException {
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
    Object getUserStatusJson(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "logUserOut", required = false) Boolean logUserOut)
            throws NoSuchRequestHandlingMethodException {

        String orcid = getCurrentUserOrcid();
        
        if (logUserOut != null && logUserOut.booleanValue()) {
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
            if(internalSSOManager.enableCookie()) {
                Cookie [] cookies = request.getCookies();
                //Update cookie 
                if(cookies != null) {
                    for(Cookie cookie : cookies) {
                        if(InternalSSOManager.COOKIE_NAME.equals(cookie.getName())) {
                            //If there are no user, just delete the cookie and token
                            if(PojoUtil.isEmpty(orcid)) {
                                cookie.setMaxAge(0);
                                cookie.setValue(StringUtils.EMPTY);
                                response.addCookie(cookie);
                            } else if(internalSSOManager.verifyToken(orcid, cookie.getValue())) {
                                internalSSOManager.updateCookie(orcid, request, response);
                            } 
                            break;
                        }                    
                    }
                }
            }
            return us;
        }                                            
    }
    
    @RequestMapping(value = "/userInfo.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getUserInfo(HttpServletRequest request) {
        Map<String, String> info = new HashMap<String, String>();        
        OrcidProfileUserDetails userDetails = getCurrentUser();
        if(userDetails != null) {
            String effectiveOrcid = getEffectiveUserOrcid();
            String realUserOrcid = getRealUserOrcid();
            info.put("REAL_USER_ORCID", realUserOrcid);
            // REAL_USER_ORCID = EFFECTIVE_USER_ORCID unless it is in delegation mode
            info.put("EFFECTIVE_USER_ORCID", effectiveOrcid);
            info.put("IN_DELEGATION_MODE", String.valueOf(!effectiveOrcid.equals(realUserOrcid)));
            info.put("PRIMARY_EMAIL", userDetails.getPrimaryEmail());
            info.put("HAS_VERIFIED_EMAIL", String.valueOf(emailManagerReadOnly.haveAnyEmailVerified(effectiveOrcid)));
            info.put("IS_PRIMARY_EMAIL_VERIFIED", String.valueOf(emailManagerReadOnly.isPrimaryEmailVerified(effectiveOrcid)));
            for(OrcidWebRole role : userDetails.getAuthorities()) {
                switch (role) {
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
            if(isDelegatedByAdmin()) {
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
        return configDetails;        
    }
    
    @RequestMapping(value = "/messages.json", method = RequestMethod.GET)
    public @ResponseBody org.orcid.pojo.Local getJavascriptMessages(HttpServletRequest request) {
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
