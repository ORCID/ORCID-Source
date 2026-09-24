package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.utils.Actors;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeveloperToolsControllerTest {

    private final static String USER_ORCID = "0000-0000-0000-0000";
    private final static String OTHER_USER_ORCID = "0000-0000-0000-0009";
    private final static String CLIENT_1 = "APP-000000001";
    private final static String CLIENT_2 = "APP-000000002";
    private final static String CLIENT_3 = "APP-000000003";

    private DeveloperToolsController developerToolsController;

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

    @Mock
    private ProfileEntityManager mockProfileEntityManager;

    @Mock
    private LocaleManager mockLocaleManager;

    @Before
    public void before() {
        developerToolsController = new DeveloperToolsController();

        // emailManagerReadOnly and profileEntityManager are re-declared by
        // DeveloperToolsController over the copies BaseController (and, for
        // profileEntityManager, BaseWorkspaceController) declare. Spring's
        // @Resource fills every copy; a single-field injection fills only the
        // most derived one and the inherited helpers then NPE. Set each name
        // once per declaring class.
        ReflectionTestUtils.setField(developerToolsController, DeveloperToolsController.class, "emailManagerReadOnly", mockEmailManagerReadOnly,
                EmailManagerReadOnly.class);
        ReflectionTestUtils.setField(developerToolsController, BaseController.class, "emailManagerReadOnly", mockEmailManagerReadOnly, EmailManagerReadOnly.class);
        ReflectionTestUtils.setField(developerToolsController, DeveloperToolsController.class, "profileEntityManager", mockProfileEntityManager,
                ProfileEntityManager.class);
        ReflectionTestUtils.setField(developerToolsController, BaseWorkspaceController.class, "profileEntityManager", mockProfileEntityManager,
                ProfileEntityManager.class);
        ReflectionTestUtils.setField(developerToolsController, BaseController.class, "profileEntityManager", mockProfileEntityManager, ProfileEntityManager.class);
        ReflectionTestUtils.setField(developerToolsController, BaseController.class, "localeManager", mockLocaleManager, LocaleManager.class);
        ReflectionTestUtils.setField(developerToolsController, BaseController.class, "sourceManager", mockSourceManager, SourceManager.class);
        ReflectionTestUtils.setField(developerToolsController, "profileEntityCacheManager", mockProfileEntityCacheManager);
        ReflectionTestUtils.setField(developerToolsController, "clientManager", mockClientManager);
        ReflectionTestUtils.setField(developerToolsController, "clientManagerReadOnly", mockClientManagerReadOnly);

        // getMessage() is compared against itself on both sides of every
        // assertion below, so echoing the key back is enough and keeps the test
        // independent of the message bundle.
        when(mockLocaleManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // getEffectiveUserOrcid() reads the static SecurityContextHolder through
        // BaseController's inline BaseControllerUtil, never through a mock.
        SecurityContextTestUtils.setupSecurityContextForWebUser(USER_ORCID, USER_ORCID + "@test.orcid.org");

        when(mockClientManager.createPublicClient(any(org.orcid.jaxb.model.v3.release.client.Client.class)))
                .thenAnswer(new Answer<org.orcid.jaxb.model.v3.release.client.Client>() {
                    @Override
                    public org.orcid.jaxb.model.v3.release.client.Client answer(InvocationOnMock invocation) throws Throwable {
                        org.orcid.jaxb.model.v3.release.client.Client c = (org.orcid.jaxb.model.v3.release.client.Client) invocation.getArguments()[0];
                        c.setId(CLIENT_1);
                        c.setClientType(ClientType.PUBLIC_CLIENT);
                        return c;
                    }
                });

        when(mockClientManager.edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), eq(false)))
                .thenAnswer(new Answer<org.orcid.jaxb.model.v3.release.client.Client>() {
                    @Override
                    public org.orcid.jaxb.model.v3.release.client.Client answer(InvocationOnMock invocation) throws Throwable {
                        org.orcid.jaxb.model.v3.release.client.Client c = (org.orcid.jaxb.model.v3.release.client.Client) invocation.getArguments()[0];
                        c.setId(CLIENT_2);
                        c.setClientType(ClientType.PUBLIC_CLIENT);
                        return c;
                    }
                });

        when(mockProfileEntityCacheManager.retrieve(USER_ORCID)).thenReturn(new ProfileEntity(USER_ORCID));
        Set<org.orcid.jaxb.model.v3.release.client.Client> clients = new HashSet<>();
        org.orcid.jaxb.model.v3.release.client.Client c = new org.orcid.jaxb.model.v3.release.client.Client();
        c.setId(CLIENT_3);
        c.setGroupProfileId(USER_ORCID);
        Set<org.orcid.jaxb.model.v3.release.client.ClientRedirectUri> rUris = new HashSet<>();
        org.orcid.jaxb.model.v3.release.client.ClientRedirectUri rUri1 = new org.orcid.jaxb.model.v3.release.client.ClientRedirectUri();
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
        when(mockClientManagerReadOnly.getClients(anyString())).thenReturn(clients);
        when(mockClientManagerReadOnly.get(anyString())).thenAnswer(new Answer<org.orcid.jaxb.model.v3.release.client.Client>() {
            @Override
            public org.orcid.jaxb.model.v3.release.client.Client answer(InvocationOnMock invocation) throws Throwable {
                String clientId = (String) invocation.getArguments()[0];
                org.orcid.jaxb.model.v3.release.client.Client c = new org.orcid.jaxb.model.v3.release.client.Client();
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
        // SecurityContextHolder is static process state; leaving an actor behind
        // silently authorises the next test in the same JVM.
        Actors.clear();
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
        rUri.setValue(Text.valueOf("https://orcid.org"));
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
        rUri.setValue(Text.valueOf("https://orcid.org"));
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
        rUri.setValue(Text.valueOf("https://orcid.org"));
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
        rUri.setValue(Text.valueOf("https://orcid.org"));
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
        rUri.setValue(Text.valueOf("https://orcid.org"));
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
        rUri.setValue(Text.valueOf("https://orcid.org"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);
        Client result = developerToolsController.createClient(client);
        verify(mockClientManager, times(1)).createPublicClient(any(org.orcid.jaxb.model.v3.release.client.Client.class));
        assertEquals(CLIENT_1, result.getClientId().getValue());
    }

    @Test
    public void updateClientTest() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Updated client name"));
        client.setShortDescription(Text.valueOf("Updated client description"));
        client.setWebsite(Text.valueOf("https://orcid.org/updated"));
        client.setType(Text.valueOf(ClientType.PUBLIC_CLIENT.value()));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("https://orcid.org"));
        redirectUris.add(rUri);
        RedirectUri rUri2 = new RedirectUri();
        rUri2.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri2.setValue(Text.valueOf("http://test2.com"));
        redirectUris.add(rUri2);
        client.setRedirectUris(redirectUris);
        client.setClientId(Text.valueOf(CLIENT_2));
        SecurityContextTestUtils.setupSecurityContextForWebUser(USER_ORCID, "test@email.com");
        Client updatedClient = developerToolsController.updateClient(client);
        verify(mockClientManager, times(1)).edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), eq(false));
        assertEquals(CLIENT_2, updatedClient.getClientId().getValue());
    }

    /**
     * The client id is supplied by the caller, so a client owned by somebody else must be
     * refused rather than edited - and the refusal must not hand back its secret.
     */
    @Test
    public void updateClientDoesNotEditAClientOwnedBySomebodyElseTest() throws Exception {
        String victimClientId = "APP-000000009";
        String victimOrcid = "0000-0000-0000-0009";
        when(mockClientManagerReadOnly.get(victimClientId)).thenAnswer(new Answer<org.orcid.jaxb.model.v3.release.client.Client>() {
            @Override
            public org.orcid.jaxb.model.v3.release.client.Client answer(InvocationOnMock invocation) throws Throwable {
                org.orcid.jaxb.model.v3.release.client.Client victim = new org.orcid.jaxb.model.v3.release.client.Client();
                victim.setId(victimClientId);
                victim.setGroupProfileId(victimOrcid);
                victim.setDecryptedSecret("victim-secret");
                return victim;
            }
        });

        Client client = new Client();
        client.setClientId(Text.valueOf(victimClientId));
        client.setDisplayName(Text.valueOf("Taken over"));
        client.setShortDescription(Text.valueOf("Taken over"));
        client.setWebsite(Text.valueOf("https://attacker.example"));
        client.setType(Text.valueOf(ClientType.PUBLIC_CLIENT.value()));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("https://attacker.example/steal"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);

        SecurityContextTestUtils.setupSecurityContextForWebUser(USER_ORCID, "test@email.com");
        Client result = developerToolsController.updateClient(client);

        assertFalse("the edit should be refused", result.getErrors().isEmpty());
        verify(mockClientManager, never()).edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), anyBoolean());
        assertNull("the refusal must not disclose the client secret", result.getClientSecret());
    }

    /**
     * memberId arrives in the request body. It must never decide who owns the new client,
     * or a caller could mint - or read back - a client belonging to another member.
     */
    @Test
    public void createClientIgnoresTheMemberIdInTheRequestTest() throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Client Name"));
        client.setShortDescription(Text.valueOf("This is a test"));
        client.setType(Text.valueOf(ClientType.PUBLIC_CLIENT.value()));
        client.setWebsite(Text.valueOf("http://client.com"));
        client.setMemberId(Text.valueOf("0000-0000-0000-0009"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.SSO_AUTHENTICATION.value()));
        rUri.setValue(Text.valueOf("https://orcid.org"));
        redirectUris.add(rUri);
        client.setRedirectUris(redirectUris);

        SecurityContextTestUtils.setupSecurityContextForWebUser(USER_ORCID, "test@email.com");
        developerToolsController.createClient(client);

        ArgumentCaptor<org.orcid.jaxb.model.v3.release.client.Client> captor = ArgumentCaptor
                .forClass(org.orcid.jaxb.model.v3.release.client.Client.class);
        verify(mockClientManager, times(1)).createPublicClient(captor.capture());
        assertEquals("the session, not the request body, decides the owner", USER_ORCID, captor.getValue().getGroupProfileId());
    }

    @Test
    public void resetClientSecretTest() throws Exception {
        String clientId = CLIENT_3;
        Client c = new Client();
        c.setClientId(Text.valueOf(clientId));
        SecurityContextTestUtils.setupSecurityContextForWebUser(USER_ORCID, "test@email.com");
        assertTrue(developerToolsController.resetClientSecret(c));
        verify(mockClientManager, times(1)).resetClientSecret(clientId);
    }

    /**
     * DeveloperToolsController.resetClientSecret compares the client's
     * groupProfileId against the signed in record before delegating. Nothing
     * exercised that branch negatively.
     */
    @Test
    public void resetClientSecret_notMyClientTest() throws Exception {
        org.orcid.jaxb.model.v3.release.client.Client someoneElses = new org.orcid.jaxb.model.v3.release.client.Client();
        someoneElses.setId(CLIENT_3);
        someoneElses.setGroupProfileId(OTHER_USER_ORCID);
        when(mockClientManagerReadOnly.get(CLIENT_3)).thenReturn(someoneElses);

        Actors.user(USER_ORCID);

        Client c = new Client();
        c.setClientId(Text.valueOf(CLIENT_3));

        assertFalse(developerToolsController.resetClientSecret(c));
        verify(mockClientManager, never()).resetClientSecret(anyString());
    }

    @Test
    public void resetClientSecret_unknownClientTest() throws Exception {
        when(mockClientManagerReadOnly.get(CLIENT_3)).thenReturn(null);

        Client c = new Client();
        c.setClientId(Text.valueOf(CLIENT_3));

        assertFalse(developerToolsController.resetClientSecret(c));
        verify(mockClientManager, never()).resetClientSecret(anyString());
    }
}
