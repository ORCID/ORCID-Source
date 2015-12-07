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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.utils.OrcidRequestUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

public class SocialAjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Resource
    private SocialContext socialContext;

    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private UserConnectionDao userConnectionDao;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private InternalSSOManager internalSSOManager;
    
    @Resource
    protected SourceManager sourceManager;
    
    @Resource
    protected OrcidProfileManager orcidProfileManager;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
    	SocialType connectionType = socialContext.isSignedIn(request, response);
        if (connectionType != null) {
        	Map<String, String> userMap = retrieveUserDetails(connectionType);
            String providerId = connectionType.value();
            UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(userMap.get("providerUserId"), providerId);
            if (userConnectionEntity != null) {
                if (!userConnectionEntity.isLinked()) {
                    OrcidProfile profile = getRealProfile();
                    userConnectionEntity.setLinked(true);
                    userConnectionEntity.setEmail(userMap.get("email"));
                    userConnectionEntity.setOrcid(profile.getOrcidIdentifier().getPath());
                    userConnectionDao.merge(userConnectionEntity);
                }
            } else {
                throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
            }
        } else {
            throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
        }
    	
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
    
    private OrcidProfile getRealProfile() {
        String realOrcid = getRealUserOrcid();
        return realOrcid == null ? null : orcidProfileManager.retrieveOrcidProfile(realOrcid);
    }
    
    private String getRealUserOrcid() {
        return sourceManager.retrieveRealUserOrcid();
    }
    
    private Map<String, String> retrieveUserDetails(SocialType connectionType) {
        
    	Map<String, String> userMap = new HashMap<String, String>();
    	if (SocialType.FACEBOOK.equals(connectionType)) {
            Facebook facebook = socialContext.getFacebook();
            User user = facebook.fetchObject("me", User.class, "id", "email", "name");
            userMap.put("providerUserId", user.getId());
            userMap.put("userName", user.getName());
            userMap.put("email", user.getEmail());
        } else if (SocialType.GOOGLE.equals(connectionType)) {
            Google google = socialContext.getGoogle();
            Person person = google.plusOperations().getGoogleProfile();
            userMap.put("providerUserId", person.getId());
            userMap.put("userName", person.getDisplayName());
            userMap.put("email", person.getAccountEmail());
        }
    	
    	return userMap;
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
