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
package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class DeveloperToolsControllerTest {

    private final static String USER_ORCID = "0000-0000-0000-0000";
    private final static String CLIENT_1 = "APP-000000001";
    private final static String CLIENT_2 = "APP-000000002";
    private final static String CLIENT_3 = "APP-000000003";

    @Resource
    private DeveloperToolsController developerToolsController;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource
    ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "clientManagerV3")
    private ClientManager clientManager;

    @Resource(name = "clientManagerReadOnlyV3")
    private ClientManagerReadOnly clientManagerReadOnly;

    private SourceManager sourceManager;
    
    @Mock
    private EmailManagerReadOnly mockEmailManagerReadOnly;

    @Mock
    ProfileEntityCacheManager mockProfileEntityCacheManager;

    @Mock
    private ClientManager mockClientManager;

    @Mock
    private ClientManagerReadOnly mockClientManagerReadOnly;
    
    @Mock
    private SourceManager mockSourceManager;

    @SuppressWarnings("deprecation")
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "profileEntityCacheManager", mockProfileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "clientManager", mockClientManager);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "clientManagerReadOnly", mockClientManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "sourceManager", mockSourceManager);
        
        when(mockClientManager.createPublicClient(Matchers.any(org.orcid.jaxb.model.v3.dev1.client.Client.class))).thenAnswer(new Answer<org.orcid.jaxb.model.v3.dev1.client.Client>() {
            @Override
            public org.orcid.jaxb.model.v3.dev1.client.Client answer(InvocationOnMock invocation) throws Throwable {
                org.orcid.jaxb.model.v3.dev1.client.Client c = (org.orcid.jaxb.model.v3.dev1.client.Client) invocation.getArguments()[0];
                c.setId(CLIENT_1);
                c.setClientType(ClientType.PUBLIC_CLIENT);
                return c;
            }
        });

        when(mockClientManager.edit(Matchers.any(org.orcid.jaxb.model.v3.dev1.client.Client.class), Matchers.eq(false))).thenAnswer(new Answer<org.orcid.jaxb.model.v3.dev1.client.Client>() {
            @Override
            public org.orcid.jaxb.model.v3.dev1.client.Client answer(InvocationOnMock invocation) throws Throwable {
                org.orcid.jaxb.model.v3.dev1.client.Client c = (org.orcid.jaxb.model.v3.dev1.client.Client) invocation.getArguments()[0];
                c.setId(CLIENT_2);
                c.setClientType(ClientType.PUBLIC_CLIENT);
                return c;
            }
        });

        when(mockProfileEntityCacheManager.retrieve(USER_ORCID)).thenReturn(new ProfileEntity(USER_ORCID));
        Set<org.orcid.jaxb.model.v3.dev1.client.Client> clients = new HashSet<>();
        org.orcid.jaxb.model.v3.dev1.client.Client c = new org.orcid.jaxb.model.v3.dev1.client.Client();
        c.setId(CLIENT_3);
        c.setGroupProfileId(USER_ORCID);
        Set<org.orcid.jaxb.model.v3.dev1.client.ClientRedirectUri> rUris = new HashSet<>();
        org.orcid.jaxb.model.v3.dev1.client.ClientRedirectUri rUri1 = new org.orcid.jaxb.model.v3.dev1.client.ClientRedirectUri();
        rUri1.setRedirectUri("http://ruri1.com");
        rUri1.setRedirectUriType(RedirectUriType.SSO_AUTHENTICATION.value());
        rUris.add(rUri1);
        c.setClientType(ClientType.PUBLIC_CLIENT);
        c.setClientRedirectUris(rUris);
        c.setDescription("Client description");
        c.setName("Client name");
        c.setWebsite("http://ruri1.com");
        c.setDecryptedSecret("client-secret");
        clients.add(c);
        when(mockClientManagerReadOnly.getClients(Matchers.anyString())).thenReturn(clients);
        when(mockClientManagerReadOnly.get(Matchers.anyString())).thenAnswer(new Answer<org.orcid.jaxb.model.v3.dev1.client.Client>(){
            @Override
            public org.orcid.jaxb.model.v3.dev1.client.Client answer(InvocationOnMock invocation) throws Throwable {
                String clientId = (String) invocation.getArguments()[0];
                org.orcid.jaxb.model.v3.dev1.client.Client c = new org.orcid.jaxb.model.v3.dev1.client.Client();
                c.setId(clientId);
                c.setGroupProfileId(USER_ORCID);
                return c;
            }
        });
        when(mockSourceManager.isInDelegationMode()).thenReturn(false);
        when(mockClientManager.resetClientSecret(CLIENT_3)).thenReturn(true);
    }

    @After
    public void after() {
        // Reset mocks
        TargetProxyHelper.injectIntoProxy(developerToolsController, "emailManagerReadOnly", emailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "clientManager", clientManager);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "clientManagerReadOnly", clientManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(developerToolsController, "sourceManager", sourceManager);
    }

    @Test
    public void testCrossSiteScriptingOnClientName() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("<script>alert('name')</script>"));
        client.setShortDescription(Text.valueOf("This is a short description"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals(developerToolsController.getMessage("manage.developer_tools.name.html"), result.getErrors().get(0));
    }

    @Test
    public void testCrossSiteScriptingOnClientDescription() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a test to show that html is <script>alert('name')</script> throws an error"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals(developerToolsController.getMessage("manage.developer_tools.description.html"), result.getErrors().get(0));
    }

    @Test
    public void testClientValidation() throws Exception {
        // Test empty title
        Client client = new Client();
        client.setShortDescription(Text.valueOf("This is a description"));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.name_not_empty"));

        // Test empty description
        client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setWebsite(Text.valueOf("http://client.com"));
        redirectUris = new ArrayList<RedirectUri>();
        rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.description_not_empty"));

        // Test empty website
        client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a description"));
        redirectUris = new ArrayList<RedirectUri>();
        rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.website_not_empty"));

        // Test empty redirect uris
        client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a description"));
        client.setWebsite(Text.valueOf("http://client.com"));
        result = developerToolsController.createClient(client);
        assertNotNull(result.getErrors());
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getErrors().get(0), developerToolsController.getMessage("manage.developer_tools.at_least_one"));
    }

    @Test
    public void getClientTest() {
        Client c = developerToolsController.getClient();
        assertNotNull(c);
        assertEquals(CLIENT_3, c.getClientId().getValue());
        assertEquals("Client name", c.getDisplayName().getValue());
        assertEquals("client-secret", c.getClientSecret().getValue());
        assertEquals(USER_ORCID, c.getMemberId().getValue());
        assertEquals(1, c.getRedirectUris().size());
        assertEquals("http://ruri1.com", c.getRedirectUris().get(0).getValue().getValue());
        assertEquals("Client description", c.getShortDescription().getValue());
        assertEquals("http://ruri1.com", c.getWebsite().getValue());
    }

    @Test
    public void createClientTest() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a test"));
        client.setType(Text.valueOf(ClientType.PUBLIC_CLIENT.value()));
        client.setWebsite(Text.valueOf("http://client.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        verify(mockClientManager, times(1)).createPublicClient(Matchers.any(org.orcid.jaxb.model.v3.dev1.client.Client.class));
        assertEquals(CLIENT_1, result.getClientId().getValue());
    }

    @Test
    public void updateClientTest() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Updated client name"));
        client.setShortDescription(Text.valueOf("Updated client description"));
        client.setWebsite(Text.valueOf("http://updated.com"));
        client.setType(Text.valueOf(ClientType.PUBLIC_CLIENT.value()));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("http://test.com"));
        redirectUris.add(rUri);
        RedirectUri rUri2 = new RedirectUri();
        rUri2.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri2.setValue(Text.valueOf("http://test2.com"));
        redirectUris.add(rUri2);
        client.setRedirectUris(redirectUris);
        Client updatedClient = developerToolsController.updateClient(client);
        verify(mockClientManager, times(1)).edit(Matchers.any(org.orcid.jaxb.model.v3.dev1.client.Client.class), Matchers.eq(false));
        assertEquals(CLIENT_2, updatedClient.getClientId().getValue());
    }

    @Test
    public void resetClientSecretTest() throws Exception {
        String clientId = CLIENT_3;
        SecurityContextTestUtils.setupSecurityContextForWebUser(USER_ORCID, "test@email.com");
        assertTrue(developerToolsController.resetClientSecret(clientId));
        verify(mockClientManager, times(1)).resetClientSecret(clientId);
    }
}