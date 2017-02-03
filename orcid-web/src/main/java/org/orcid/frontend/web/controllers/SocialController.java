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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Shobhit Tyagi
 */
@Controller
@RequestMapping("/social")
public class SocialController extends BaseController {

    @Autowired
    private SocialContext socialContext;

    @Resource
    private EmailDao emailDao;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private UserConnectionDao userConnectionDao;

    @RequestMapping(value = { "/access" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response) {

        SocialType connectionType = socialContext.isSignedIn(request, response);
        if (connectionType != null) {
            Map<String, String> userMap = retrieveUserDetails(connectionType);

            String providerId = connectionType.value();
            String userId = socialContext.getUserId();
            UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(userMap.get("providerUserId"), providerId);
            if (userConnectionEntity != null) {
                if (userConnectionEntity.isLinked()) {
                    UserconnectionPK pk = new UserconnectionPK(userId, providerId, userMap.get("providerUserId"));
                    userConnectionDao.updateLoginInformation(pk);
                    String aCredentials = new StringBuffer(providerId).append(":").append(userMap.get("providerUserId")).toString();
                    PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), aCredentials);
                    token.setDetails(new WebAuthenticationDetails(request));
                    Authentication authentication = authenticationManager.authenticate(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return new ModelAndView("redirect:" + calculateRedirectUrl(request, response));
                } else {
                    ModelAndView mav = new ModelAndView();
                    mav.setViewName("social_link_signin");
                    mav.addObject("providerId", providerId);
                    mav.addObject("accountId", getAccountIdForDisplay(userMap));
                    mav.addObject("linkType", "social");
                    mav.addObject("emailId", (userMap.get("email") == null) ? "" : userMap.get("email"));
                    mav.addObject("firstName", (userMap.get("firstName") == null) ? "" : userMap.get("firstName"));
                    mav.addObject("lastName", (userMap.get("lastName") == null) ? "" : userMap.get("lastName"));
                    return mav;
                }
            } else {
                throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
            }
        } else {
            throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
        }
    }

    private Map<String, String> retrieveUserDetails(SocialType connectionType) {

        Map<String, String> userMap = new HashMap<String, String>();
        if (SocialType.FACEBOOK.equals(connectionType)) {
            Facebook facebook = socialContext.getFacebook();
            User user = facebook.fetchObject("me", User.class, "id", "email", "name", "first_name", "last_name");
            userMap.put("providerUserId", user.getId());
            userMap.put("userName", user.getName());
            userMap.put("email", user.getEmail());
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
        } else if (SocialType.GOOGLE.equals(connectionType)) {
            Google google = socialContext.getGoogle();
            Person person = google.plusOperations().getGoogleProfile();
            userMap.put("providerUserId", person.getId());
            userMap.put("userName", person.getDisplayName());
            userMap.put("email", person.getAccountEmail());
            userMap.put("firstName", person.getGivenName());
            userMap.put("lastName", person.getFamilyName());
        }

        return userMap;
    }

    private String getAccountIdForDisplay(Map<String, String> userMap) {
        if (userMap.get("email") != null) {
            return userMap.get("email");
        }
        if (userMap.get("userName") != null) {
            return userMap.get("userName");
        }
        return userMap.get("providerUserId");
    }
}
