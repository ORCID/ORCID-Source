package org.orcid.frontend.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.UserConnectionManager;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SocialAjaxAuthenticationSuccessHandler extends AjaxAuthenticationSuccessHandlerBase {    
    
    @Resource
    private UserConnectionManager userConnectionManager;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        linkSocialAccount(request, response);
        String targetUrl = getTargetUrl(request, response, authentication);
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");
    }

    public void linkSocialAccount(HttpServletRequest request, HttpServletResponse response) {
        //TODO: is signed in?
        SocialType connectionType = null;
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
        //TODO: How to handle this?
        if (SocialType.FACEBOOK.equals(connectionType)) {
            userMap.put("providerUserId", null);
            userMap.put("userName", null);
            userMap.put("email", null);
        } else if (SocialType.GOOGLE.equals(connectionType)) {
            userMap.put("providerUserId", null);
            userMap.put("userName", null);
            userMap.put("email", null);
            userMap.put("firstName", null);
            userMap.put("lastName", null);            
        }

        return userMap;
    }
}
