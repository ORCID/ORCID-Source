package org.orcid.frontend.web.controllers;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.persistence.dao.MiscDao;
import org.orcid.pojo.UserStatus;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class HomeController extends BaseController {
    
    private static final String OVERALL_OK = "overallOk";
    private static final String READ_ONLY_DB_CONNECTION_OK = "readOnlyDbConnectionOk";
    private static final String DB_CONNECTION_OK = "dbConnectionOk";
    private static final String TOMCAT_UP = "tomcatUp";
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private InternalSSOManager internalSSOManager;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name= "miscDao")
    private MiscDao miscDao;
    
    @Resource(name= "miscDaoReadOnly")
    private MiscDao miscDaoReadOnly;
    
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

    @RequestMapping(value = "/tomcatUp.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    String tomcatUp(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        request.setAttribute("isMonitoring", true);
        return "{tomcatUp:true}";
    }
    
    @RequestMapping(value = "/webStatus.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody Map<String, Boolean> webStatus(HttpServletRequest request) {
        request.setAttribute("isMonitoring", true);
        Map<String, Boolean> result = new LinkedHashMap<>();
        result.put(TOMCAT_UP, true);
        result.put(DB_CONNECTION_OK, isConnectionOk(miscDao));
        result.put(READ_ONLY_DB_CONNECTION_OK, isConnectionOk(miscDaoReadOnly));
        Boolean overall = result.values().stream().filter(v -> !v).findAny().orElse(true);
        result.put(OVERALL_OK, overall);
        return result;
    }

    private boolean isConnectionOk(MiscDao miscDao) {
        try {
            Date dbDate = miscDao.retrieveDatabaseDatetime();
            if (dbDate != null) {
                return true;
            }
        } catch (PersistenceException e) {
            return false;
        }
        return false;
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
                profileEntityManager.updateLocale(orcid, org.orcid.jaxb.model.v3.rc1.common.Locale.fromValue(lang));
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

}
