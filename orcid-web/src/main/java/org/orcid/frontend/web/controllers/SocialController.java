package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.UserOperations;
import org.springframework.social.google.api.Google;
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
	
	@Value("${org.orcid.facebook.enabled:true}")
    private boolean enabled;
    
    @Resource
    private EmailDao emailDao;

    @Resource
    private AuthenticationManager authenticationManager;

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
    		return new ModelAndView("redirect:/signin");
    	}
    	
        if(!emailManager.emailExists(emailId)) {
        	// redirect to registration screen
        	return new ModelAndView("redirect:/register");
        }
        EmailEntity emailEntity = emailDao.findCaseInsensitive(emailId);
        ProfileEntity profile = null;
        if (emailEntity == null) {
        	return new ModelAndView("redirect:/signin");
        } else {
        	 profile = emailEntity.getProfile();
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
			email = uo.getUserProfile().getEmail();
		} else if(SocialType.GOOGLE.equals(connectionType)) {
			Google google = socialContext.getGoogle();
			email = google.plusOperations().getGoogleProfile().getAccountEmail();
		}
		
		return email;
	}
}
