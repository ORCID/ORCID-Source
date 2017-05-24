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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.security.aop.LockedException;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller("loginController")
public class LoginController extends BaseController {
   
    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    protected OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    
    @Resource
    protected OrcidAuthorizationEndpoint authorizationEndpoint;
    
    @ModelAttribute("yesNo")
    public Map<String, String> retrieveYesNoMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("true", "Yes");
        map.put("false", "No");
        return map;
    }

    @RequestMapping(value = { "/signin", "/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getQueryString();
        if(!PojoUtil.isEmpty(query)) {
            if(query.contains("oauth")) {
                return handleOauthSignIn(request, response);
            }
        }
        // in case have come via a link that requires them to be signed out        
        return new ModelAndView("login");
    }

    // We should go back to regular spring sign out with CSRF protection
    @RequestMapping(value = { "/signout"}, method = RequestMethod.GET)
    public ModelAndView signout(HttpServletRequest request, HttpServletResponse response) {
        // in case have come via a link that requires them to be signed out
        logoutCurrentUser(request, response);    
        String redirectString = "redirect:" + orcidUrlManager.getBaseUrl()  + "/signin";
        ModelAndView mav = new ModelAndView(redirectString);
        return mav;
    }

    @RequestMapping("wrong-user")
    public String wrongUserHandler() {
        return "wrong_user";
    }

    @RequestMapping("/session-expired")
    public String sessionExpiredHandler() {
        return "session_expired";
    }
    
    private ModelAndView handleOauthSignIn(HttpServletRequest request, HttpServletResponse response) {
        String queryString = request.getQueryString();
        String redirectUri = null;
        Matcher redirectUriMatcher = redirectUriPattern.matcher(queryString);
        if (redirectUriMatcher.find()) {
            try {
                redirectUri = URLDecoder.decode(redirectUriMatcher.group(1), "UTF-8").trim();
            } catch (UnsupportedEncodingException e) {
            }
        }
        // Check that the client have the required permissions
        // Get client name
        Matcher clientIdMatcher = clientIdPattern.matcher(queryString);
        if (!clientIdMatcher.find()) {
            String redirectUriWithParams = redirectUri + "?error=invalid_client&error_description=invalid client_id";
            return new ModelAndView(new RedirectView(redirectUriWithParams));
        }
        // Validate client details
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientIdMatcher.group(1));
        try {            
            orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        } catch(LockedException e) {
            String redirectUriWithParams = redirectUri + "?error=client_locked&error_description=" + e.getMessage();                        
            return new ModelAndView(new RedirectView(redirectUriWithParams));
        }
        
        // validate client scopes
        try {
            Matcher scopeMatcher = scopesPattern.matcher(queryString);
            String scopesString = null;
            if (scopeMatcher.find()) {
                String scopes = scopeMatcher.group(1);
                scopesString = URLDecoder.decode(scopes, "UTF-8").trim();
                scopesString = scopesString.replaceAll(" +", " ");
            }
            authorizationEndpoint.validateScope(scopesString, clientDetails);            
        } catch (InvalidScopeException e) {
            String redirectUriWithParams = redirectUri + "?error=invalid_scope&error_description=" + e.getMessage(); 
            return new ModelAndView(new RedirectView(redirectUriWithParams));
        } catch(UnsupportedEncodingException e) {
            
        }
        ModelAndView mav = new ModelAndView("oauth/signin");
        mav.addObject("hideUserVoiceScript", true);
        mav.addObject("oauth2Screens", true);
        return mav;
    }
}
