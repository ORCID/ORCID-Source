package org.orcid.internal.server.delegator.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.oauth.impl.OrcidClientCredentialEndPointDelegatorImpl;
import org.orcid.jaxb.model.message.ScopePathType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class InternalClientCredentialEndPointDelegatorImpl extends OrcidClientCredentialEndPointDelegatorImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidClientCredentialEndPointDelegatorImpl.class);
    
    @Resource
    protected LocaleManager localeManager;
    
    @Override
    @Transactional
    public Response obtainOauth2Token(String authorization, MultivaluedMap<String, String> formParams) {
        String clientId = formParams.getFirst("client_id");                             
        String scopeList = formParams.getFirst("scope");
        String grantType = formParams.getFirst("grant_type");
        
        // Verify it is a client_credentials grant type request
        if(!OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS.equals(grantType)) {
            Object params[] = {grantType};
            throw new UnsupportedGrantTypeException(localeManager.resolveMessage("apiError.unsupported_client_type.exception", params));
        }
        
        Authentication client = getClientAuthentication();
        if (!client.isAuthenticated()) {
            LOGGER.info("Not authenticated for OAuth2: clientId={}, grantType={}, scope={}", new Object[] {
                    clientId, grantType, scopeList });
            throw new InsufficientAuthenticationException(localeManager.resolveMessage("apiError.client_not_authenticated.exception"));
        } 
        
        Set<String> scopes = new HashSet<String>();
        if (StringUtils.isNotEmpty(scopeList)) {
            scopes = OAuth2Utils.parseParameterList(scopeList);
        }
        
        // Verify it is requesting an internal scope
        HashSet <String> filteredScopes = new HashSet<String>();
        for(String scope : scopes) {
            ScopePathType scopeType = ScopePathType.fromValue(scope);
            if(scopeType.isInternalScope()) {
                filteredScopes.add(scope);
            }            
        }
        
        if(filteredScopes.isEmpty()) {
            String message = localeManager.resolveMessage("apiError.9015.developerMessage", new Object[]{});
            throw new OrcidInvalidScopeException(message);
        }
        
        OAuth2AccessToken token = generateToken(client, scopes, null, null, grantType, null, null, null, false, 0L);
        return getResponse(token);
    }
}
