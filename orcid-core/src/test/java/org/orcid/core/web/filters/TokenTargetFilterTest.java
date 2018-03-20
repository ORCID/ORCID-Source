package org.orcid.core.web.filters;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import com.sun.jersey.spi.container.ContainerRequest;

public class TokenTargetFilterTest {

    private static final String ORCID1 = "0000-0000-0000-0001";
    private static final String ORCID2 = "0000-0000-0000-0002";
    private static final String CLIENT_ID = "APP-0000000000000001";    
    
    @Before
    public void before() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }
    
    @After
    public void after() {
        RequestContextHolder.resetRequestAttributes();
    }
    
    @Test
    public void tokenUsedOnTheRightUserTest() {
        setUpSecurityContext(ORCID1, CLIENT_ID, ScopePathType.READ_LIMITED);
        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getPath()).thenReturn("http://api.test.orcid.org/v2.0/" + ORCID1);
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);
    }
    
    @Test(expected = AccessControlException.class)
    public void tokenUsedOnTheWrongUser12ApiTest() {
        setUpSecurityContext(ORCID1, CLIENT_ID, ScopePathType.READ_LIMITED);
        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getPath()).thenReturn("http://api.test.orcid.org/v1.2/" + ORCID2);        

        RequestAttributes sra = Mockito.mock(RequestAttributes.class);
        Mockito.when(sra.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST)).thenReturn("1.2");
        RequestContextHolder.setRequestAttributes(sra);
        
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);
        fail();
    }
    
    @Test(expected = OrcidUnauthorizedException.class)
    public void tokenUsedOnTheWrongUser20ApiTest() {
        setUpSecurityContext(ORCID1, CLIENT_ID, ScopePathType.READ_LIMITED);        
        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getPath()).thenReturn("http://api.test.orcid.org/v2.0/" + ORCID2);        
                
        RequestAttributes sra = Mockito.mock(RequestAttributes.class);
        Mockito.when(sra.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST)).thenReturn("2.0");
        RequestContextHolder.setRequestAttributes(sra);
        
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);
        fail();
    }
    
    @Test
    public void filterInvokedOnNoOrcidEndpointTest() {        
        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getPath()).thenReturn("http://api.test.orcid.org/oauth/token");
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);        
    }
    
    @Test
    public void readPublicTokenTest() {
        setUpSecurityContext(null, CLIENT_ID, ScopePathType.READ_PUBLIC);
        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getPath()).thenReturn("http://api.test.orcid.org/v2.0/" + ORCID2);
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);       
    }
    
    private void setUpSecurityContext(String userOrcid, String clientId, ScopePathType... scopePathTypes) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        OrcidOAuth2Authentication mockedAuthentication = mock(OrcidOAuth2Authentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        if(userOrcid != null) {
            ProfileEntity userProfileEntity = new ProfileEntity(userOrcid);
            when(mockedAuthentication.getPrincipal()).thenReturn(userProfileEntity);
            Authentication userAuthentication = mock(Authentication.class);
            when(userAuthentication.getPrincipal()).thenReturn(userProfileEntity);
            when(mockedAuthentication.getUserAuthentication()).thenReturn(userAuthentication);            
        } else {
            when(mockedAuthentication.getPrincipal()).thenReturn(clientId);
        }
        
        Set<String> scopes = new HashSet<String>();
        if (scopePathTypes != null) {
            for (ScopePathType scopePathType : scopePathTypes) {
                scopes.add(scopePathType.value());
            }
        }
        OAuth2Request authorizationRequest = new OAuth2Request(Collections.<String, String> emptyMap(), clientId,
                Collections.<GrantedAuthority> emptyList(), true, scopes, Collections.<String> emptySet(), null, Collections.<String> emptySet(),
                Collections.<String, Serializable> emptyMap());
        when(mockedAuthentication.getOAuth2Request()).thenReturn(authorizationRequest);
        when(mockedAuthentication.isAuthenticated()).thenReturn(true);
    }
}
