package org.orcid.core.manager.v3.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.util.ReflectionTestUtils;

public class SourceManagerImplTest {

    private static final String CLIENT_ID = "APP-6RTM54FDADENEKUK";

    private SourceManagerImpl sourceManager;

    @Mock
    private ClientDetailsManager clientDetailsManager;

    @Mock
    private SourceNameCacheManager sourceNameCacheManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sourceManager = new SourceManagerImpl();
        ReflectionTestUtils.setField(sourceManager, "clientDetailsManager", clientDetailsManager);
        ReflectionTestUtils.setField(sourceManager, "sourceNameCacheManager", sourceNameCacheManager);
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void retrieveActiveSourceDoesNotLookupUserOboNameWhenUserOrcidMissing() {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId(CLIENT_ID);
        clientDetails.setClientName("Test client");
        clientDetails.setUserOBOEnabled(true);
        when(clientDetailsManager.findByClientId(CLIENT_ID)).thenReturn(clientDetails);

        OrcidBearerTokenAuthentication auth = OrcidBearerTokenAuthentication.builder(CLIENT_ID, null, "token")
                .authenticated(true)
                .build();
        SecurityContextHolder.getContext().setAuthentication(auth);

        Source source = sourceManager.retrieveActiveSource();

        assertNotNull(source);
        assertNotNull(source.getSourceClientId());
        assertEquals(CLIENT_ID, source.getSourceClientId().getPath());
        assertNull(source.getAssertionOriginOrcid());
        assertNull(source.getAssertionOriginName());
        verify(sourceNameCacheManager, never()).retrieve(anyString());
    }

    @Test
    public void retrieveActiveSourceUsesUserOboNameWhenUserOrcidPresent() {
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId(CLIENT_ID);
        clientDetails.setClientName("Test client");
        clientDetails.setUserOBOEnabled(true);
        when(clientDetailsManager.findByClientId(CLIENT_ID)).thenReturn(clientDetails);
        when(sourceNameCacheManager.retrieve("0000-0001-2345-6789")).thenReturn("Test User");

        OrcidBearerTokenAuthentication auth = OrcidBearerTokenAuthentication.builder(CLIENT_ID, "0000-0001-2345-6789", "token")
                .authenticated(true)
                .build();
        SecurityContextHolder.getContext().setAuthentication(auth);

        Source source = sourceManager.retrieveActiveSource();

        assertNotNull(source);
        assertNotNull(source.getAssertionOriginOrcid());
        assertEquals("0000-0001-2345-6789", source.getAssertionOriginOrcid().getPath());
        assertNotNull(source.getAssertionOriginName());
        assertEquals("Test User", source.getAssertionOriginName().getContent());
        verify(sourceNameCacheManager).retrieve("0000-0001-2345-6789");
    }
}