package org.orcid.frontend.spring;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.frontend.spring.web.social.config.SocialSignInUtils;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SocialAjaxAuthenticationSuccessHandler extends AjaxAuthenticationSuccessHandlerBase {

    @Resource
    private UserConnectionManager userConnectionManager;

    @Resource
    private SocialSignInUtils socialSignInUtils;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        linkSocialAccount(request, response);
        String targetUrl = getTargetUrl(request, response, authentication);
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");
    }

    public void linkSocialAccount(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> signedInData = socialSignInUtils.getSignedInData(request, response);
        if (signedInData != null) {
            UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserId(signedInData.get(OrcidOauth2Constants.PROVIDER_USER_ID),
                    signedInData.get(OrcidOauth2Constants.PROVIDER_ID));
            if (userConnectionEntity != null) {
                if (!userConnectionEntity.isLinked()) {
                    userConnectionEntity.setLinked(true);
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
}
