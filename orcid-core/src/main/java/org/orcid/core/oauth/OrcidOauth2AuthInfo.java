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
package org.orcid.core.oauth;

import java.util.Set;

import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationRequestHolder;

public class OrcidOauth2AuthInfo {

    private String clientId;

    private Set<String> scopes;

    private String userOrcid;

    public OrcidOauth2AuthInfo(AuthorizationRequestHolder authorizationRequestHolder) {
        if (authorizationRequestHolder != null) {
            init(authorizationRequestHolder.getAuthenticationRequest(), authorizationRequestHolder.getUserAuthentication());
        }
    }

    public OrcidOauth2AuthInfo(OAuth2Authentication oauth2Authentication) {
        if (oauth2Authentication != null) {
            init(oauth2Authentication.getAuthorizationRequest(), oauth2Authentication.getUserAuthentication());
        }
    }

    private void init(AuthorizationRequest authRequest, Authentication userAuthentication) {
        if (authRequest != null) {
            clientId = authRequest.getClientId();
            scopes = authRequest.getScope();
            if (userAuthentication != null) {
                Object principal = userAuthentication.getPrincipal();
                if (principal != null) {
                    if (ProfileEntity.class.isAssignableFrom(principal.getClass())) {
                        userOrcid = ((ProfileEntity) principal).getId();
                    } else if (OrcidProfileUserDetails.class.isAssignableFrom(principal.getClass())) {
                        userOrcid = ((OrcidProfileUserDetails) principal).getUsername();
                    }
                }
            }
        }
    }

    public OrcidOauth2AuthInfo(AuthorizationRequest authRequest) {

    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }

    public String getUserOrcid() {
        return userOrcid;
    }

    public void setUserOrcid(String userOrcid) {
        this.userOrcid = userOrcid;
    }

}
