package org.orcid.frontend.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.UserConnectionManager;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;

public class SocialAjaxAuthenticationSuccessHandler extends AjaxAuthenticationSuccessHandlerBase {

    @Resource
    private SocialContext socialContext;
    
    @Resource
    private UserConnectionManager userConnectionManager;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        linkSocialAccount(request, response);
        String targetUrl = getTargetUrl(request, response, authentication);
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");
    }

    public void linkSocialAccount(HttpServletRequest request, HttpServletResponse response) {
        SocialType connectionType = socialContext.isSignedIn(request, response);
        if (connectionType != null) {
            Map<String, String> userMap = retrieveUserDetails(connectionType);
            String providerId = connectionType.value();
            UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserId(userMap.get("providerUserId"), providerId);
            if (userConnectionEntity != null) {
                if (!userConnectionEntity.isLinked()) {                    
                    userConnectionEntity.setLinked(true);
                    userConnectionEntity.setEmail(userMap.get("email"));
                    userConnectionEntity.setOrcid(getRealUserOrcid());
                    userConnectionManager.update(userConnectionEntity);
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
}
