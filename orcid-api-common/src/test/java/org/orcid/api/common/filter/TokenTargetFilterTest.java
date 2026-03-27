package org.orcid.api.common.filter;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

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
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.glassfish.jersey.server.ContainerRequest;

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
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }
    
    @Test
    public void tokenUsedOnTheRightUserTest() {
        setUpSecurityContext(ORCID1, CLIENT_ID, ScopePathType.READ_LIMITED);
        ContainerRequest request = Mockito.mock(ContainerRequest.class,RETURNS_DEEP_STUBS);
        Mockito.when(request.getUriInfo().getPath()).thenReturn("http://api.test.orcid.org/v2.0/" + ORCID1);
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);
    }
    
    @Test(expected = AccessControlException.class)
    public void tokenUsedOnTheWrongUser12ApiTest() {
        setUpSecurityContext(ORCID1, CLIENT_ID, ScopePathType.READ_LIMITED);
        ContainerRequest request = Mockito.mock(ContainerRequest.class,RETURNS_DEEP_STUBS);
        Mockito.when(request.getUriInfo().getPath()).thenReturn("http://api.test.orcid.org/v1.2/" + ORCID2);        

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
        ContainerRequest request = Mockito.mock(ContainerRequest.class,RETURNS_DEEP_STUBS);
        Mockito.when(request.getUriInfo().getPath()).thenReturn("http://api.test.orcid.org/v2.0/" + ORCID2);        
                
        RequestAttributes sra = Mockito.mock(RequestAttributes.class);
        Mockito.when(sra.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST)).thenReturn("2.0");
        RequestContextHolder.setRequestAttributes(sra);
        
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);
        fail();
    }
    
    @Test
    public void filterInvokedOnNoOrcidEndpointTest() {        
        ContainerRequest request = Mockito.mock(ContainerRequest.class,RETURNS_DEEP_STUBS);
        Mockito.when(request.getUriInfo().getPath()).thenReturn("http://api.test.orcid.org/oauth/token");
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);        
    }
    
    @Test
    public void readPublicTokenTest() {
        setUpSecurityContext(null, CLIENT_ID, ScopePathType.READ_PUBLIC);
        ContainerRequest request = Mockito.mock(ContainerRequest.class, RETURNS_DEEP_STUBS);
        Mockito.when(request.getUriInfo().getPath()).thenReturn("http://api.test.orcid.org/v2.0/" + ORCID2);
        TokenTargetFilter filter = new TokenTargetFilter();
        filter.filter(request);       
    }
    
    private void setUpSecurityContext(String userOrcid, String clientId, ScopePathType... scopePathTypes) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        OrcidBearerTokenAuthentication mockedAuthentication = mock(OrcidBearerTokenAuthentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        if(userOrcid != null) {
            when(mockedAuthentication.getPrincipal()).thenReturn(clientId);
            when(mockedAuthentication.getUserOrcid()).thenReturn(userOrcid);
        } else {
            when(mockedAuthentication.getPrincipal()).thenReturn(clientId);
        }

        when(mockedAuthentication.isAuthenticated()).thenReturn(true);
    }
}
