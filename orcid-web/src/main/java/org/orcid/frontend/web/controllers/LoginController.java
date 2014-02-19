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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.frontend.web.forms.LoginForm;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller("loginController")
public class LoginController extends BaseController {
    Pattern clientIdPattern = Pattern.compile("client_id=([^&]*)");
    
    @ModelAttribute("yesNo")
    public Map<String, String> retrieveYesNoMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("true", "Yes");
        map.put("false", "No");
        return map;
    }

    @RequestMapping(value = { "/signin", "/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler(ModelAndView mav) {
        // in case have come via a link that requires them to be signed out
        logoutCurrentUser();
        mav.setViewName("login");
        return mav;
    }

    @RequestMapping(value = { "/oauth/signin", "/oauth/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler2(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
        // find client name if available 
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String client_name = "";
        String client_group_name = "";
        if (savedRequest != null) {
            String url = savedRequest.getRedirectUrl();
            Matcher matcher = clientIdPattern.matcher(url);
            if (matcher.find()) {
                String client_id = matcher.group(1);
                if (client_id != null) {
                    OrcidProfile clientProfile = orcidProfileManager.retrieveOrcidProfile(client_id);
                    if (clientProfile.getOrcidBio() != null && clientProfile.getOrcidBio().getPersonalDetails() != null
                            && clientProfile.getOrcidBio().getPersonalDetails().getCreditName() != null)
                        client_name = clientProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent();
                    if (clientProfile.getOrcidInternal() != null && clientProfile.getOrcidInternal().getGroupOrcidIdentifier() != null) {
                        String client_group_id = clientProfile.getOrcidInternal().getGroupOrcidIdentifier().getPath();
                        OrcidProfile clientGroupProfile = orcidProfileManager.retrieveOrcidProfile(client_group_id);
                        if (clientGroupProfile.getOrcidBio() != null && clientGroupProfile.getOrcidBio().getPersonalDetails() != null
                                && clientGroupProfile.getOrcidBio().getPersonalDetails().getCreditName() != null)
                            client_group_name = clientGroupProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent();
                    }
                }
            }
        }
        mav.addObject("client_name", client_name);
        mav.addObject("client_group_name", client_name);
        mav.setViewName("oauth_login");
        mav.addObject("hideUserVoiceScript", true);
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

}
