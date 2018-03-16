package org.orcid.core.oauth;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;

/**
 * @author Declan Newman (declan) Date: 11/05/2012
 */
public class OrcidClientCredentialsCheckerTest {

    @Mock
    private ClientDetailsService clientDetailsService;
    
    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Mock 
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    
    private OAuth2RequestFactory oAuth2RequestFactory;

    private OrcidClientCredentialsChecker checker;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        oAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsService); 
        checker = new OrcidClientCredentialsChecker(oAuth2RequestFactory);
        checker.setClientDetailsEntityCacheManager(clientDetailsEntityCacheManager);
        checker.setOrcidOAuth2RequestValidator(orcidOAuth2RequestValidator);
    }

    @Test(expected = InvalidScopeException.class)
    public void testInvalidCredentialsScopes() throws Exception {
        String memberId = "2875-8158-1475-6194";
        String clientId = "APP-1";
        setupMocks(clientId, memberId);
        Set<String> requestedScopes = new HashSet<String>(Arrays.asList(ScopePathType.FUNDING_CREATE.value()));
        Map<String, String> requestParams = new HashMap <String, String>();
        requestParams.put(OrcidOauth2Constants.SCOPE_PARAM, ScopePathType.FUNDING_CREATE.value());
        
        checker.validateCredentials("client_credentials", new TokenRequest(requestParams, clientId, requestedScopes, "client_credentials"));
    }

    @Test
    public void testValidCredentialsScopes() throws Exception {
        String memberId = "2875-8158-1475-6194";
        String clientId = "APP-1";
        setupMocks(clientId, memberId);
        Set<String> requestedScopes = new HashSet<String>(Arrays.asList(ScopePathType.READ_PUBLIC.value()));
        Map<String, String> requestParams = new HashMap <String, String>();
        requestParams.put(OrcidOauth2Constants.SCOPE_PARAM, ScopePathType.READ_PUBLIC.value());
        checker.validateCredentials("client_credentials", new TokenRequest(requestParams, clientId, requestedScopes, "client_credentials"));
    }

    @Test
    public void testValidCredentialsScopesForClientOnly() throws Exception {
        String memberId = "2875-8158-1475-6194";
        String clientId = "APP-1";
        setupMocks(clientId, memberId);
        Set<String> requestedScopes = new HashSet<String>(Arrays.asList(ScopePathType.READ_PUBLIC.value()));
        Map<String, String> requestParams = new HashMap <String, String>();
        requestParams.put(OrcidOauth2Constants.SCOPE_PARAM, ScopePathType.READ_PUBLIC.value());        
        checker.validateCredentials("client_credentials", new TokenRequest(requestParams, clientId, requestedScopes, "client_credentials"));
    }
    
    private void setupMocks(String clientId, String memberId) {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        Set<ClientScopeEntity> scopes = new HashSet<ClientScopeEntity>(3);
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_WORKS_UPDATE.value()));
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_BIO_READ_LIMITED.value()));
        scopes.add(new ClientScopeEntity(ScopePathType.ORCID_PROFILE_CREATE.value()));
        clientDetailsEntity.setClientScopes(scopes);
        clientDetailsEntity.setGroupProfileId(memberId);
        ProfileEntity profile = new ProfileEntity(memberId);
        profile.setRecordLocked(false);
        when(clientDetailsService.loadClientByClientId(clientId)).thenReturn(clientDetailsEntity);
        when(clientDetailsEntityCacheManager.retrieve(clientId)).thenReturn(clientDetailsEntity);
    }
}
