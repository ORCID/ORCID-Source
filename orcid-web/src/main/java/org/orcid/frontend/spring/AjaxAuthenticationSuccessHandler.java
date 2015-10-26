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
package org.orcid.frontend.spring;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.utils.OrcidRequestUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/*
 * Trying to make spring login for
 * http://stackoverflow.com/questions/10811623/spring-security-programatically-logging-in
 * 
 * @author Robert Peters (rcpeters)
 */
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private InternalSSOManager internalSSOManager;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineFullTargetUrlFromSavedRequest(request, response);
        if (authentication != null) {
            String orcidId = authentication.getName();
            checkLocale(request, response, orcidId);
            if(internalSSOManager.enableCookie()) {
                internalSSOManager.writeCookie(orcidId, request, response);
            }            
            profileDao.updateIpAddress(orcidId, OrcidRequestUtil.getIpAddress(request));
        }
        if (targetUrl == null) {
            targetUrl = determineFullTargetUrl(request, response);
        }
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");        
    }

    // new method - persist which ever local they logged in with
    private void checkLocale(HttpServletRequest request, HttpServletResponse response, String orcidId) {
        Locale lastKnownLocale = profileDao.retrieveLocale(orcidId);
        if (lastKnownLocale != null) {

            // have to read the cookie directly since spring has
            // populated the request locale yet
            CookieLocaleResolver clr = new CookieLocaleResolver();
            // must match <property name="cookieName" value="locale_v3"
            // />
            clr.setCookieName("locale_v3");
            Locale cookieLocale = org.orcid.jaxb.model.message.Locale.fromValue(clr.resolveLocale(request).toString());

            // update the users preferences, so that
            // send out emails in their last chosen language
            if (!lastKnownLocale.equals(cookieLocale)) {
                profileDao.updateLocale(orcidId, cookieLocale);
            }
        }
    }
    
    private String determineFullTargetUrlFromSavedRequest(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String url = null;
        if (savedRequest != null) {
            url = savedRequest.getRedirectUrl();
        }

        // this next section is a hack, we should refactor to us
        // some of kind of configuration file
        String contextPath =  request.getContextPath();
        if (url != null) {
        	if (orcidUrlManager.getBasePath().equals("/") && !contextPath.equals("/"))
        		url = url.replaceFirst(contextPath.replace("/", "\\/"), "");
            if (url.contains("/signin/auth"))
                url = null;
            else if (url.contains(".json"))
                url = null;
        }

        return url;
    }

    private String determineFullTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return orcidUrlManager.getServerStringWithContextPath(request) + determineTargetUrl(request, response);
    }

}
