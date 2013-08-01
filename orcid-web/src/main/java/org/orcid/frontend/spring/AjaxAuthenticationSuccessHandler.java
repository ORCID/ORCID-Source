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
package org.orcid.frontend.spring;

import java.io.IOException;
import java.security.Principal;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidPreferences;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.utils.OrcidWebUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

/*
 * Trying to make spring login for
 * http://stackoverflow.com/questions/10811623/spring-security-programatically-logging-in
 * 
 * @author Robert Peters (rcpeters)
 */
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Resource
    protected OrcidProfileManager orcidProfileManager;


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineFullTargetUrlFromSavedRequest(request, response);
        if (authentication != null) {
            String orcidId = authentication.getName();
            checkLocale(request,response, orcidId);
        }
        if (targetUrl == null) {
            targetUrl = determineFullTargetUrl(request, response);
        }
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");
    }
    
    
    
//    old method would switch the current locale to user's preferred locale
//    private void checkLocale(HttpServletRequest request, HttpServletResponse response, String orcidId) {
//        OrcidProfile op = orcidProfileManager.retrieveOrcidProfile(orcidId);
//        if (op != null) {
//            if (op.getOrcidInternal() != null 
//                    && op.getOrcidInternal().getPreferences() != null
//                    && op.getOrcidInternal().getPreferences().getLocale() != null 
//                    && op.getOrcidInternal().getPreferences().getLocale().value() != null) {
//                String preferedLocale = op.getOrcidInternal().getPreferences().getLocale().value();
//                // hack to write/rewrite correct locale cookie on login.
//                CookieLocaleResolver clr = new CookieLocaleResolver();
//                clr.setCookieName("locale_v2"); /* must match <property name="cookieName" value="locale_v2" /> */
//                clr.setLocale(request, response, StringUtils.parseLocaleString(preferedLocale));
//            }            
//        }
//    }
    
    // new method - persist which ever local they logged in with
    private void checkLocale(HttpServletRequest request, HttpServletResponse response, String orcidId) {
        OrcidProfile op = orcidProfileManager.retrieveOrcidProfile(orcidId);
        if (op != null) {
            if (op.getOrcidPreferences() != null) {
                OrcidPreferences prefs = op.getOrcidPreferences();
                if (prefs.getLocale() != null 
                        && prefs.getLocale().value() != null) {
                    String localeStr = request.getLocale().toString(); 
                    
                    // have to read the cookie directly since spring has populated the request locale yet
                    CookieLocaleResolver clr = new CookieLocaleResolver();
                    clr.setCookieName("locale_v2"); /* must match <property name="cookieName" value="locale_v2" /> */
                    Locale cookieLocale = org.orcid.jaxb.model.message.Locale.fromValue(clr.resolveLocale(request).toString());
                    
                    Locale lastKnownLocale = prefs.getLocale();
                    
                    // update the users preferences, so that
                    // send out emails in their last chosen language 
                    if (!lastKnownLocale.equals(cookieLocale)) {
                        prefs.setLocale(cookieLocale);
                        op.setOrcidPreferences(prefs);
                        orcidProfileManager.updatePreferences(op);
                    }
                }
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
        if (url != null) {
            if (url.contains("/signin/auth"))
                url = null;
            else if (url.contains(".json"))
                url = null;
        }

        return url;
    }

    private String determineFullTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return OrcidWebUtils.getServerStringWithContextPath(request) + determineTargetUrl(request, response);
    }

}
