package org.orcid.core.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.Assert;

/**
 * @author Declan Newman (declan) Date: 10/05/2012
 */
public class OrcidOboOAuth2Authentication extends OrcidOAuth2Authentication {
    private static final long serialVersionUID = 4582917463028491537L;
    private String oboClientId;

    /**
     * Construct an OAuth 2 authentication. Since some grant types don't require
     * user authentication, the user authentication may be null. The active
     * token cannot be null as this is needed for subsequent changes to the
     * scopes when writing.
     *
     * @param oboClientId
     *      The client id of the OBO client
     * @param authorizationRequest
     *      The authorization request (must not be null).
     * @param userAuthentication
     *      The user authentication (possibly null).
     * @param activeToken
     *      The token that has been used to authenticate the client
     */

    public OrcidOboOAuth2Authentication(String oboClientId, AuthorizationRequest authorizationRequest, Authentication userAuthentication, String activeToken) {
        super(authorizationRequest, userAuthentication, activeToken);
        Assert.hasText(oboClientId, "The oboClientId must have a value.");
        this.oboClientId = oboClientId;
    }

    public String getOboClientId() {
        return oboClientId;
    }

}
