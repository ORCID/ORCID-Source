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
package org.orcid.core.oauth;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class OrcidImplicitTokenGranter extends ImplicitTokenGranter{
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    protected OrcidImplicitTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(tokenServices, clientDetailsService, requestFactory);
    }

    public static enum ImplicitScopes {
            OPENID("openid");
        private String value;
        ImplicitScopes(String v){
            value=v;
        }
        public static boolean isValid(Set<String> test) {
            for (String t : test){
                boolean found = false;
                for (ImplicitScopes scope : ImplicitScopes.values()) {
                    if (scope.value.equals(t)) {
                        found = true;
                    }
                }
                if (!found)
                    return false;
            }
            return true;
        }
    };
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidImplicitTokenGranter.class);
    
    /** Note, client must have implicit scope in client_authorized_grant_type table to get this far.  Otherwise request will be rejected by OrcidClientCredentialsChecker
     * 
     */
    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest clientToken) {
        if (!ImplicitScopes.isValid(clientToken.getScope())){
            //possibly move to OrcidOAuth2RequestValidator
            LOGGER.info("Invalid scope for implict token: " +clientToken.getScope()); 
            throw new InvalidGrantException("Invalid grant_type/scope combination.  Implicit scope only valid for 'openid' scope: "+ clientToken.getScope());           
        }
        Authentication userAuthSpring = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthSpring==null || !userAuthSpring.isAuthenticated()) {
            throw new InsufficientAuthenticationException("There is no currently logged in user");
        }
        OAuth2Request request = ((ImplicitTokenRequest)clientToken).getOAuth2Request();
        OrcidOauth2UserAuthentication userAuth = new OrcidOauth2UserAuthentication(profileEntityManager.findByOrcid(userAuthSpring.getName()), userAuthSpring.isAuthenticated());
        OAuth2Authentication result = new OAuth2Authentication(request, userAuth);
        return result;
    }

}
