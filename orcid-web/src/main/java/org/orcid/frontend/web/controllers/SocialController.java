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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.security.DeprecatedProfileException;
import org.orcid.core.security.SocialLoginException;
import org.orcid.core.security.UnclaimedProfileExistsException;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.UserOperations;
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
	
	@Value("${org.orcid.social_login.enabled}")
    private boolean enabled;
    
    @Resource
    private EmailDao emailDao;

    @Resource
    private AuthenticationManager authenticationManager;
    
    @Resource
    private UserConnectionDao userConnectionDao;
    
    @Resource
    private OrcidSecurityManager securityMgr;
    
    private String providerUserId;

    @RequestMapping(value = { "/access" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response) {
    	checkEnabled();
    	
    	String emailId = null;
    	SocialType connectionType = socialContext.isSignedIn(request, response);
    	if (connectionType != null) {
    		emailId = retrieveEmail(connectionType);
    	}
    	
    	if(emailId == null) {
    		//Possible that the account is not verified. Or the account does not contain a primary email.
    		throw new SocialLoginException("Did not receieve the login information.");
    	}
    	
        if(!emailManager.emailExists(emailId)) {
        	// redirect to registration screen
        	throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
        }
        EmailEntity emailEntity = emailDao.findCaseInsensitive(emailId);
        ProfileEntity profile = null;
        if (emailEntity == null) {
        	throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
        } else {
        	 profile = emailEntity.getProfile();
        	 if (profile == null) {
                 throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
             }
             if (profile.getPrimaryRecord() != null) {
     			throw new DeprecatedProfileException(
     					"orcid.frontend.security.deprecated_with_primary", profile
     							.getPrimaryRecord().getId(), profile.getId());
             }
             if (!profile.getClaimed() && !securityMgr.isAdmin()) {
                 throw new UnclaimedProfileExistsException("orcid.frontend.security.unclaimed_exists");
             }
             if (profile.getDeactivationDate() != null && !securityMgr.isAdmin()) {
                 throw new DisabledException("Account not active, please call helpdesk");
             }
        	 String userId = socialContext.getUserId();
        	 String providerId = connectionType.value();
        	 UserconnectionPK pk = new UserconnectionPK(userId, providerId, providerUserId);
        	 userConnectionDao.updateLoginInformation(emailId, profile.getId(), pk);
        }
  
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(profile.getId(), "");
		token.setDetails(new WebAuthenticationDetails(request));
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
        
        return new ModelAndView("redirect:/my-orcid");
    }

    private void checkEnabled() {
        if (!enabled) {
            throw new FeatureDisabledException();
        }
    }
    
	private String retrieveEmail(SocialType connectionType) {
		String email = null;
		if(SocialType.FACEBOOK.equals(connectionType)) {
			Facebook facebook = socialContext.getFacebook();
			UserOperations uo = facebook.userOperations();
			User user = uo.getUserProfile();
			providerUserId = user.getId();
			email = user.getEmail();
		} else if(SocialType.GOOGLE.equals(connectionType)) {
			Google google = socialContext.getGoogle();
			Person person = google.plusOperations().getGoogleProfile();
			providerUserId = person.getId();
			email = person.getAccountEmail();
		}
		
		return email;
	}
}
