package org.orcid.frontend.spring.web.social;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.web.controllers.BaseController;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.UserOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Shobhit Tyagi
 *
 */
@Controller
@RequestMapping("/fb")
public class FacebookController extends BaseController {

	@Autowired
	private SocialContext socialContext;
	
	@Value("${org.orcid.facebook.enabled:true}")
    private boolean enabled;
    
    @Resource
    private EmailDao emailDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookController.class);
    
    @Resource
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = { "/access" }, method = RequestMethod.GET)
    public @ResponseBody void signinHandler(HttpServletRequest request, HttpServletResponse response) {
    	checkEnabled();
    	
    	String emailId = null;
    	if (socialContext.isSignedIn(request, response)) {
    		emailId = retrieveEmail();
    	}
    	
        if(!emailManager.emailExists(emailId)) {
        	return;
        }
        EmailEntity emailEntity = emailDao.findCaseInsensitive(emailId);
        ProfileEntity profile = null;
        if (emailEntity != null) {
            profile = emailEntity.getProfile();
        }
        try {
            PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(profile.getId(), "social:facebook:["+emailId+"]");
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.warn("User {0} should have been logged-in via Facebook, but was unable to due to a problem", "", e);
        }
    }

    private void checkEnabled() {
        if (!enabled) {
            throw new FeatureDisabledException();
        }
    }
    
	private String retrieveEmail() {
		Facebook facebook = socialContext.getFacebook();
		String email;
		UserOperations uo = facebook.userOperations();
		email = uo.getUserProfile().getEmail();
		return email;
	}
}
