package org.orcid.core.oauth;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class OrcidImplicitTokenGranter extends ImplicitTokenGranter {
    @Resource
    private ProfileEntityManager profileEntityManager;

    protected OrcidImplicitTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory);
    }

    /**
     * Note, client must have implicit scope in client_authorized_grant_type
     * table to get this far. Otherwise request will be rejected by
     * OrcidClientCredentialsChecker
     * 
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest clientToken) {
        Authentication userAuthSpring = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthSpring == null || !userAuthSpring.isAuthenticated()) {
            throw new InsufficientAuthenticationException("There is no currently logged in user");
        }
        OAuth2Request request = ((ImplicitTokenRequest) clientToken).getOAuth2Request();
        OrcidOauth2UserAuthentication userAuth = new OrcidOauth2UserAuthentication(profileEntityManager.findByOrcid(userAuthSpring.getName()),
                userAuthSpring.isAuthenticated());
        OAuth2Authentication result = new OAuth2Authentication(request, userAuth);
        return result;
    }

}
