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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
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
import org.orcid.core.manager.v3.ClientDetailsManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.security.OrcidRoles;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The member whose clients these are, previously supplied by
 * ClientsControllerTest.getAuthentication()'s hard coded encrypted password
 * blob against /data/SourceClientDetailsEntityData.xml. Only the orcid and the
 * ROLE_PREMIUM_INSTITUTION authority ever mattered.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ClientsControllerTest {

    private static final String MEMBER_ORCID = "5555-5555-5555-5558";
    private static final String OTHER_MEMBER_ORCID = "5555-5555-5555-5559";
    private static final String CLIENT_1 = "APP-5555555555555555";
    private static final String CLIENT_2 = "APP-5555555555555556";
    private static final String CLIENT_3 = "APP-5555555555555557";
    private static final String CLIENT_4 = "APP-5555555555555558";

    private ClientsController controller;

    @Mock
    private ClientDetailsManager clientDetailsManager;

    @Mock
    private ClientManager clientManager;

    @Mock
    private ClientManagerReadOnly clientManagerReadOnly;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private LocaleManager localeManager;

    @Before
    public void before() {
        controller = new ClientsController();
        ReflectionTestUtils.setField(controller, "clientDetailsManager", clientDetailsManager);
        ReflectionTestUtils.setField(controller, "clientManager", clientManager);
        ReflectionTestUtils.setField(controller, "clientManagerReadOnly", clientManagerReadOnly);
        ReflectionTestUtils.setField(controller, "profileEntityCacheManager", profileEntityCacheManager);
        // ClientsController does not re-declare localeManager, so BaseController
        // holds the only copy; getMessage() NPEs without it.
        ReflectionTestUtils.setField(controller, BaseController.class, "localeManager", localeManager, LocaleManager.class);

        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        Actors.user(MEMBER_ORCID, OrcidRoles.ROLE_PREMIUM_INSTITUTION);
    }

    @After
    public void after() {
        Actors.clear();
    }

    @Test
    public void emptyClientTest() {
        Client client = controller.getEmptyClient();
        client = controller.createClient(client);
        assertNotNull(client);
        List<String> errors = client.getErrors();
        assertEquals(4, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.website.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.short_description.empty")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
        verify(clientManager, never()).create(any(org.orcid.jaxb.model.v3.release.client.Client.class));
    }

    @Test
    public void testInvalidName() {
        Client client = controller.getEmptyClient();
        client.setRedirectUris(new ArrayList<RedirectUri>());
        client.setDisplayName(Text.valueOf("This is a <a>invalid</a> name"));
        client.setShortDescription(Text.valueOf("This is a valid description"));
        client.setWebsite(Text.valueOf("http://www.orcid.org"));
        client = controller.createClient(client);
        assertNotNull(client);
        assertEquals(1, client.getErrors().size());
        assertEquals(controller.getMessage("manage.developer_tools.group.error.display_name.html"), client.getErrors().get(0));
    }

    @Test
    public void testInvalidDescription() {
        Client client = controller.getEmptyClient();
        client.setRedirectUris(new ArrayList<RedirectUri>());
        client.setDisplayName(Text.valueOf("This is a valid name"));
        client.setShortDescription(Text.valueOf("This is a <a>invalid</a> description"));
        client.setWebsite(Text.valueOf("http://www.orcid.org"));
        client = controller.createClient(client);
        assertNotNull(client);
        assertEquals(1, client.getErrors().size());
        assertEquals(controller.getMessage("manage.developer_tools.group.error.short_description.html"), client.getErrors().get(0));
    }

    @Test
    public void testInvalidWebsite() {
        Client client = controller.getEmptyClient();

        // check empty website causes an issue
        client = controller.getEmptyClient();
        client.setRedirectUris(new ArrayList<RedirectUri>());
        client.setDisplayName(Text.valueOf("This is a valid name"));
        client.setShortDescription(Text.valueOf("This is a valid description"));
        client = controller.createClient(client);
        assertNotNull(client);
        assertEquals(1, client.getErrors().size());

        client.setRedirectUris(new ArrayList<RedirectUri>());
        client.setDisplayName(Text.valueOf("This is a valid name"));
        client.setShortDescription(Text.valueOf("This is a valid description"));
        client.setWebsite(Text.valueOf("http:://orcid.org"));
        client = controller.createClient(client);
        assertNotNull(client);
        assertEquals(1, client.getErrors().size());
        assertEquals(controller.getMessage("common.invalid_url"), client.getErrors().get(0));
    }

    @Test
    public void createInvalidClientTest() {
        // Test invalid fields
        Client client = controller.getEmptyClient();
        String _151chars = RandomStringUtils.randomAlphanumeric(151);
        client.setDisplayName(Text.valueOf(_151chars));
        client.setShortDescription(Text.valueOf("description"));
        client.setWebsite(Text.valueOf("http://site.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(Text.valueOf(""));
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);

        client = controller.createClient(client);
        List<String> errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.150")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));

        // Test invalid redirect uris
        client = controller.getEmptyClient();
        client.setDisplayName(Text.valueOf("Name"));
        client.setShortDescription(Text.valueOf("Description"));
        client.setWebsite(Text.valueOf("http://mysite.com"));

        redirectUris = new ArrayList<RedirectUri>();
        one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(new Text());
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);
        client = controller.createClient(client);
        errors = client.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));

        RedirectUri two = new RedirectUri();
        two.setType(Text.valueOf("grant-read-wizard"));
        two.setValue(new Text());
        redirectUris = new ArrayList<RedirectUri>();
        redirectUris.add(two);
        client.setRedirectUris(redirectUris);

        client = controller.createClient(client);
        errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.empty_scopes")));

        verify(clientManager, never()).create(any(org.orcid.jaxb.model.v3.release.client.Client.class));
    }

    @Test
    public void editInvalidClientTest() {
        // Test invalid fields
        Client client = controller.getEmptyClient();
        String _151chars = RandomStringUtils.randomAlphanumeric(151);
        client.setDisplayName(Text.valueOf(_151chars));
        client.setShortDescription(Text.valueOf("description"));
        client.setWebsite(Text.valueOf("http://site.com"));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(Text.valueOf(""));
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);

        client = controller.editClient(client);
        List<String> errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.display_name.150")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));

        // Test invalid redirect uris
        client = controller.getEmptyClient();
        client.setDisplayName(Text.valueOf("Name"));
        client.setShortDescription(Text.valueOf("Description"));
        client.setWebsite(Text.valueOf("http://mysite.com"));

        redirectUris = new ArrayList<RedirectUri>();
        one = new RedirectUri();
        one.setType(Text.valueOf("default"));
        one.setValue(new Text());
        redirectUris.add(one);
        client.setRedirectUris(redirectUris);
        client = controller.editClient(client);
        errors = client.getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));

        RedirectUri two = new RedirectUri();
        two.setType(Text.valueOf("grant-read-wizard"));
        two.setValue(new Text());
        redirectUris = new ArrayList<RedirectUri>();
        redirectUris.add(two);
        client.setRedirectUris(redirectUris);

        client = controller.editClient(client);
        errors = client.getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.empty_redirect_uri")));
        assertTrue(errors.contains(controller.getMessage("manage.developer_tools.group.error.empty_scopes")));

        verify(clientManager, never()).edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), eq(false));
    }

    /**
     * The four clients arrive from the manager in an arbitrary order; the sort
     * into display-name order is the controller's own work. That
     * clientManagerReadOnly.getClients(memberId) returns this member's clients
     * and not another member's is ClientDetailsDaoImpl.findByGroupId's
     * groupProfileId predicate, and stays a database concern.
     */
    @Test
    public void getClientsTest() {
        when(clientManagerReadOnly.getClients(MEMBER_ORCID)).thenReturn(scrambledClientsOfMember());

        List<Client> clients = controller.getClients();
        assertNotNull(clients);
        assertEquals(4, clients.size());
        Client client1 = clients.get(0);
        assertEquals("APP-5555555555555555", client1.getClientId().getValue());

        Client client2 = clients.get(1);
        assertEquals("APP-5555555555555556", client2.getClientId().getValue());

        Client client3 = clients.get(2);
        assertEquals("APP-5555555555555557", client3.getClientId().getValue());

        Client client4 = clients.get(3);
        assertEquals("APP-5555555555555558", client4.getClientId().getValue());

        verify(clientManagerReadOnly).getClients(MEMBER_ORCID);
    }

    @Test
    public void addClientTest() {
        when(clientManagerReadOnly.getClients(MEMBER_ORCID)).thenReturn(scrambledClientsOfMember());
        when(clientManager.create(any(org.orcid.jaxb.model.v3.release.client.Client.class)))
                .thenAnswer(new Answer<org.orcid.jaxb.model.v3.release.client.Client>() {
                    @Override
                    public org.orcid.jaxb.model.v3.release.client.Client answer(InvocationOnMock invocation) {
                        org.orcid.jaxb.model.v3.release.client.Client submitted = invocation.getArgument(0);
                        submitted.setId("APP-9999999999999999");
                        submitted.setClientType(ClientType.PREMIUM_CREATOR);
                        submitted.setDecryptedSecret("a-new-secret");
                        return submitted;
                    }
                });

        List<Client> clients = controller.getClients();
        int clientsSoFar = clients.size();
        assertTrue(clientsSoFar > 0);

        Client client = new Client();
        client.setAllowAutoDeprecate(Checkbox.valueOf(true));
        client.setType(Text.valueOf(ClientType.CREATOR.name()));
        client.setDisplayName(Text.valueOf("My client name"));
        client.setMemberName(Text.valueOf("My member name"));
        client.setPersistentTokenEnabled(Checkbox.valueOf(true));
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUri r1 = new RedirectUri();
        r1.setValue(Text.valueOf("http://orcid.org"));
        r1.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        redirectUris.add(r1);
        client.setRedirectUris(redirectUris);
        client.setShortDescription(Text.valueOf("My short description"));
        client.setWebsite(Text.valueOf("http://orcid.org"));
        client = controller.createClient(client);
        assertTrue(client.getErrors().isEmpty());
        assertNotNull(client);
        assertNotNull(client.getClientId());
        assertTrue(client.getClientId().getValue().startsWith("APP-"));
        assertFalse(PojoUtil.isEmpty(client.getClientSecret()));

        // The submitted form reached the manager unchanged apart from the
        // validation clean up.
        ArgumentCaptor<org.orcid.jaxb.model.v3.release.client.Client> created = ArgumentCaptor
                .forClass(org.orcid.jaxb.model.v3.release.client.Client.class);
        verify(clientManager).create(created.capture());
        assertEquals("My client name", created.getValue().getName());
        assertEquals("My short description", created.getValue().getDescription());
        assertEquals("http://orcid.org", created.getValue().getWebsite());
        assertEquals(1, created.getValue().getClientRedirectUris().size());
    }

    /**
     * The group path has the same shape as the individual one: the client id comes from the
     * request body, so a client belonging to another member must be refused.
     *
     * <p>
     * main proves this over DBUnit by signing in as another member and re-reading the row. On mocks
     * the equivalent proof (TESTING.md R2) is that the guard refuses AND that nothing reaches
     * ClientManager.edit: the mutation this catches is deleting the clientBelongsToCurrentUser check
     * in ClientsController.editClient.
     * </p>
     */
    @Test
    public void editClientOfAnotherMemberIsRefusedTest() {
        when(clientManagerReadOnly.getClients(MEMBER_ORCID)).thenReturn(scrambledClientsOfMember());
        // the client carried by the form is owned by somebody else
        when(clientManagerReadOnly.get(CLIENT_1)).thenReturn(modelClient(CLIENT_1, "Source Client 1", OTHER_MEMBER_ORCID));

        Client client = controller.getClients().get(0);
        assertEquals(CLIENT_1, client.getClientId().getValue());
        client.getDisplayName().setValue("Taken over");
        // an attacker constructs the body themselves, so it carries no secret
        client.setClientSecret(null);

        Client result = controller.editClient(client);

        assertFalse("the edit should be refused", result.getErrors().isEmpty());
        assertNull("the refusal must not hand back the client secret", result.getClientSecret());
        verify(clientManager, never()).edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), anyBoolean());
    }

    /** The other half of the same guard: a client id that loads nothing is refused too. */
    @Test
    public void editClientThatCannotBeLoadedIsRefusedTest() {
        when(clientManagerReadOnly.getClients(MEMBER_ORCID)).thenReturn(scrambledClientsOfMember());
        when(clientManagerReadOnly.get(CLIENT_1)).thenReturn(null);

        Client client = controller.getClients().get(0);
        client.getDisplayName().setValue("Taken over");

        Client result = controller.editClient(client);

        assertFalse("the edit should be refused", result.getErrors().isEmpty());
        verify(clientManager, never()).edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), anyBoolean());
    }

    @Test
    public void editClientTest() {
        when(clientManagerReadOnly.getClients(MEMBER_ORCID)).thenReturn(scrambledClientsOfMember());
        when(clientManagerReadOnly.get(CLIENT_1)).thenReturn(modelClient(CLIENT_1, "Source Client 1", MEMBER_ORCID));
        when(clientManager.edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), eq(false)))
                .thenAnswer(new Answer<org.orcid.jaxb.model.v3.release.client.Client>() {
                    @Override
                    public org.orcid.jaxb.model.v3.release.client.Client answer(InvocationOnMock invocation) {
                        org.orcid.jaxb.model.v3.release.client.Client submitted = invocation.getArgument(0);
                        submitted.setClientType(ClientType.PREMIUM_CREATOR);
                        submitted.setDecryptedSecret("client-1-secret");
                        return submitted;
                    }
                });

        List<Client> clients = controller.getClients();
        assertNotNull(clients);
        assertEquals(4, clients.size());
        Client client = clients.get(0);
        assertEquals("APP-5555555555555555", client.getClientId().getValue());

        String random = RandomStringUtils.randomAlphanumeric(20);

        client.getDisplayName().setValue("Source Client 1 Updated");
        client.getShortDescription().setValue("Updated client description");
        client.getWebsite().setValue("http://orcid.org/" + random);
        RedirectUri newRedirectUri = new RedirectUri();
        newRedirectUri.setValue(Text.valueOf("http://orcid.org/" + random));
        newRedirectUri.setType(Text.valueOf(RedirectUriType.DEFAULT.value()));
        client.getRedirectUris().add(newRedirectUri);

        client = controller.editClient(client);
        assertTrue(client.getErrors().isEmpty());

        // Everything the form carried was handed to the manager, including the
        // new redirect uri. Whether it survives a round trip is
        // ClientManagerImpl/ClientDetailsDao behaviour.
        ArgumentCaptor<org.orcid.jaxb.model.v3.release.client.Client> edited = ArgumentCaptor
                .forClass(org.orcid.jaxb.model.v3.release.client.Client.class);
        verify(clientManager).edit(edited.capture(), eq(false));
        assertEquals(CLIENT_1, edited.getValue().getId());
        assertEquals("Source Client 1 Updated", edited.getValue().getName());
        assertEquals("Updated client description", edited.getValue().getDescription());
        assertEquals("http://orcid.org/" + random, edited.getValue().getWebsite());
        assertEquals(2, edited.getValue().getClientRedirectUris().size());

        assertEquals("Source Client 1 Updated", client.getDisplayName().getValue());
        assertEquals("Updated client description", client.getShortDescription().getValue());
        assertEquals("http://orcid.org/" + random, client.getWebsite().getValue());
        boolean rUriFound = false;
        for (RedirectUri rUri : client.getRedirectUris()) {
            if (rUri.getValue().getValue().equals("http://orcid.org/" + random)) {
                assertEquals(RedirectUriType.DEFAULT.value(), rUri.getType().getValue());
                rUriFound = true;
            }
        }
        assertTrue(rUriFound);
    }

    /**
     * ClientsController.resetClientSecret compares the client's groupProfileId
     * against the signed in member before delegating; there was no coverage of
     * resetClientSecret at all.
     */
    @Test
    public void resetClientSecretTest() {
        when(clientManagerReadOnly.get(CLIENT_1)).thenReturn(modelClient(CLIENT_1, "Source Client 1", MEMBER_ORCID));
        when(clientManager.resetClientSecret(CLIENT_1)).thenReturn(true);

        assertTrue(controller.resetClientSecret(CLIENT_1));
        verify(clientManager).resetClientSecret(CLIENT_1);
    }

    @Test
    public void resetClientSecret_notMyClientTest() {
        when(clientManagerReadOnly.get(CLIENT_1)).thenReturn(modelClient(CLIENT_1, "Source Client 1", OTHER_MEMBER_ORCID));

        assertFalse(controller.resetClientSecret(CLIENT_1));
        verify(clientManager, never()).resetClientSecret(anyString());
    }

    @Test
    public void resetClientSecret_unknownClientTest() {
        when(clientManagerReadOnly.get(CLIENT_1)).thenReturn(null);

        assertFalse(controller.resetClientSecret(CLIENT_1));
        verify(clientManager, never()).resetClientSecret(anyString());
    }

    /**
     * Deliberately not in getClients() order, so the assertions on positions 0
     * to 3 test Collections.sort(clients) rather than the iteration order of
     * whatever the manager happened to hand back.
     */
    private Set<org.orcid.jaxb.model.v3.release.client.Client> scrambledClientsOfMember() {
        return new LinkedHashSet<>(Arrays.asList(modelClient(CLIENT_3, "Source Client 3", MEMBER_ORCID),
                modelClient(CLIENT_1, "Source Client 1", MEMBER_ORCID), modelClient(CLIENT_4, "Source Client 4", MEMBER_ORCID),
                modelClient(CLIENT_2, "Source Client 2", MEMBER_ORCID)));
    }

    private org.orcid.jaxb.model.v3.release.client.Client modelClient(String clientId, String name, String groupProfileId) {
        org.orcid.jaxb.model.v3.release.client.Client client = new org.orcid.jaxb.model.v3.release.client.Client();
        client.setId(clientId);
        client.setName(name);
        client.setDescription("A test source client");
        client.setWebsite("www." + clientId.substring(4) + ".com");
        client.setGroupProfileId(groupProfileId);
        client.setClientType(ClientType.PREMIUM_CREATOR);
        client.setDecryptedSecret("secret-" + clientId);
        Set<org.orcid.jaxb.model.v3.release.client.ClientRedirectUri> rUris = new LinkedHashSet<>();
        org.orcid.jaxb.model.v3.release.client.ClientRedirectUri rUri = new org.orcid.jaxb.model.v3.release.client.ClientRedirectUri();
        rUri.setRedirectUri("http://orcid.org/" + clientId);
        rUri.setRedirectUriType(RedirectUriType.DEFAULT.value());
        rUris.add(rUri);
        client.setClientRedirectUris(rUris);
        return client;
    }
}
