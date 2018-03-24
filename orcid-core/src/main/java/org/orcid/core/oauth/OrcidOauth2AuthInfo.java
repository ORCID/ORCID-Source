package org.orcid.core.oauth;

import java.util.Set;

import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

public class OrcidOauth2AuthInfo {

    private String clientId;

    private Set<String> scopes;

    private String userOrcid;    

    public OrcidOauth2AuthInfo(String clientId, Set<String> scopes, String userOrcid) {
        this.clientId = clientId;
        this.scopes = scopes;
        this.userOrcid = userOrcid;
    }
    
    public OrcidOauth2AuthInfo(OAuth2Authentication oauth2Authentication) {
        if (oauth2Authentication != null) {
            init(oauth2Authentication.getOAuth2Request(), oauth2Authentication.getUserAuthentication());
        }
    }    
    
    private void init(OAuth2Request authRequest, Authentication userAuthentication) {
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
