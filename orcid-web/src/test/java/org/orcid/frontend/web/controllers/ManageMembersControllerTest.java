package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.v3.ClientDetailsManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.security.OrcidRoles;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ClientActivationRequest;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManageMembersControllerTest {

    @Mock(name = "membersManagerV3")
    MembersManager membersManager;

    @Mock(name = "emailManagerV3")
    EmailManager emailManager;

    @Mock(name = "clientManagerV3")
    private ClientManager clientManager;

    @Mock(name = "clientManagerReadOnlyV3")
    private ClientManagerReadOnly clientManagerReadOnly;

    @Mock(name = "clientDetailsManagerReadOnlyV3")
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Mock(name = "clientDetailsManagerV3")
    private ClientDetailsManager clientDetailsManager;

    @Mock
    ClientsController groupAdministratorController;

    @Mock
    protected LocaleManager localeManager;

    @InjectMocks
    ManageMembersController manageMembers;

    @Before
    public void beforeInstance() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });
    }

    protected Authentication getAuthentication() {
        String orcid = "4444-4444-4444-4440";
        UserDetails details = new User(orcid, "password", List.of(new SimpleGrantedAuthority(OrcidRoles.ROLE_ADMIN.name())));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(orcid, "password", details.getAuthorities());
        auth.setDetails(details);
        return auth;
    }

    @Test
    public void createMemberProfileWithInvalidEmailsTest() throws Exception {
        String existingEmail = "premium_institution@group.com";

        Member group = new Member();
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("basic"));
        group.setSalesforceId(Text.valueOf(""));

        // Validate already existing email address
        group.setEmail(Text.valueOf(existingEmail));
        when(emailManager.emailExists(eq(existingEmail))).thenReturn(true);
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.email.already_used", group.getErrors().get(0));

        // Validate empty email address
        group.setEmail(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("NotBlank.group.email", group.getErrors().get(0));

        // Validate invalid email address
        group.setEmail(Text.valueOf("invalidemail"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.email.invalid_email", group.getErrors().get(0));
    }

    @Test
    public void createMemberProfileWithInvalidGroupNameTest() throws Exception {
        Member group = new Member();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setType(Text.valueOf("basic"));
        group.setSalesforceId(Text.valueOf(""));

        // Validate empty group name
        group.setGroupName(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("NotBlank.group.name", group.getErrors().get(0));

        // validate too long group name - 151 chars
        group.setGroupName(Text.valueOf(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.name.too_long", group.getErrors().get(0));
    }

    @Test
    public void createMemberProfileWithInvalidTypeTest() throws Exception {
        Member group = new Member();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setSalesforceId(Text.valueOf(""));

        // Validate empty type
        group.setType(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("NotBlank.group.type", group.getErrors().get(0));

        // Validate invalid type
        group.setType(Text.valueOf("invalid"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.type.invalid", group.getErrors().get(0));
    }

    @Test
    public void createMemberProfileWithInvalidSalesforceIdTest() throws Exception {
        Member group = new Member();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("basic"));

        // Validate empty type
        group.setSalesforceId(Text.valueOf("1"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.salesforce_id.invalid_length", group.getErrors().get(0));

        // Validate invalid type
        group.setSalesforceId(Text.valueOf("1234567890abcd!"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.salesforce_id.invalid", group.getErrors().get(0));
    }

    @Test
    public void createMemberProfileTest() throws Exception {
        Member group = new Member();
        String email = "group@email.com";
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf(""));

        Member createdMember = new Member();
        createdMember.setGroupOrcid(Text.valueOf("5555-5555-5555-5555"));

        when(membersManager.createMember(any(Member.class))).thenReturn(createdMember);

        group = manageMembers.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertEquals("5555-5555-5555-5555", group.getGroupOrcid().getValue());
    }

    @Test
    public void findMemberByOrcidTest() throws Exception {
        String email = "group@email.com";
        String orcid = "5555-5555-5555-5555";
        Member member = new Member();
        member.setEmail(Text.valueOf(email));
        member.setGroupName(Text.valueOf("Group Name"));
        member.setSalesforceId(Text.valueOf("1234567890abcde"));
        member.setGroupOrcid(Text.valueOf(orcid));

        when(membersManager.getMember(orcid)).thenReturn(member);
        when(membersManager.getMember(email)).thenReturn(member);

        // Test find by orcid
        Member newGroup = manageMembers.findMember(orcid);
        assertNotNull(newGroup);
        assertEquals(email, newGroup.getEmail().getValue());
        assertEquals("Group Name", newGroup.getGroupName().getValue());
        assertEquals("1234567890abcde", newGroup.getSalesforceId().getValue());
        assertEquals(orcid, newGroup.getGroupOrcid().getValue());

        // Test find by email
        Member newGroup2 = manageMembers.findMember(email);
        assertNotNull(newGroup2);
        assertEquals(email, newGroup2.getEmail().getValue());
        assertEquals(orcid, newGroup2.getGroupOrcid().getValue());

        // Test: Find member by ORCID with clients and check deactivated status
        Member memberWithClients = new Member();
        List<Client> clients = new ArrayList<>();
        clients.add(createClient("APP-0000000000000001", false));
        clients.add(createClient("APP-0000000000000002", false));
        clients.add(createClient("APP-0000000000000003", true));
        memberWithClients.setClients(clients);

        when(membersManager.getMember("5555-5555-5555-0000")).thenReturn(memberWithClients);

        Member newGroup3 = manageMembers.findMember("5555-5555-5555-0000");
        assertNotNull(newGroup3);
        List<Client> clientsResult = newGroup3.getClients();
        assertEquals(3, clientsResult.size());
        assertFalse(findClientById(clientsResult, "APP-0000000000000001").isDeactivated());
        assertFalse(findClientById(clientsResult, "APP-0000000000000002").isDeactivated());
        assertTrue(findClientById(clientsResult, "APP-0000000000000003").isDeactivated());
    }

    private Client createClient(String id, boolean deactivated) {
        Client c = new Client();
        c.setClientId(Text.valueOf(id));
        c.setDeactivated(deactivated);
        return c;
    }

    @Test
    public void editMemberTest() throws Exception {
        String orcid = "5555-5555-5555-5555";
        Member group = new Member();
        group.setGroupOrcid(Text.valueOf(orcid));
        group.setEmail(Text.valueOf("new_email@user.com"));
        group.setGroupName(Text.valueOf("Updated Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));

        when(membersManager.getMember(orcid)).thenReturn(group);

        manageMembers.updateMember(group);
        Member updatedGroup = manageMembers.findMember(orcid);
        assertNotNull(updatedGroup);
        assertEquals(orcid, updatedGroup.getGroupOrcid().getValue());
        assertEquals("Updated Group Name", updatedGroup.getGroupName().getValue());
    }

    @Test
    public void editMemberWithInvalidEmailTest() throws Exception {
        String email = "group1@email.com";
        when(emailManager.emailExists(email)).thenReturn(true);

        Member group = new Member();
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));

        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.email.already_used", group.getErrors().get(0));
    }

    @Test
    public void editMemberWithInvalidSalesforceIdTest() throws Exception {
        Member group = new Member();
        group.setEmail(Text.valueOf("group2@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcd!"));

        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(1, group.getErrors().size());
        assertEquals("group.salesforce_id.invalid", group.getErrors().get(0));
    }

    @Test
    public void findClientTest() throws Exception {
        String clientId2 = "APP-0000000000000002";
        
        ClientDetailsEntity clientDetails2 = new ClientDetailsEntity();
        clientDetails2.setDeactivatedDate(null);

        org.orcid.jaxb.model.v3.release.client.Client modelClient2 = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(modelClient2.getId()).thenReturn(clientId2);
        when(modelClient2.getName()).thenReturn("Client # 2");
        when(modelClient2.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        
        when(clientManagerReadOnly.get(clientId2)).thenReturn(modelClient2);
        when(clientDetailsManagerReadOnly.findByClientId(clientId2)).thenReturn(clientDetails2);

        Client result2 = manageMembers.findClient(clientId2);
        assertNotNull(result2);
        assertEquals("Client # 2", result2.getDisplayName().getValue());
        assertFalse(result2.isDeactivated());

        String clientId3 = "APP-0000000000000003";
        org.orcid.jaxb.model.v3.release.client.Client modelClient3 = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(modelClient3.getId()).thenReturn(clientId3);
        when(modelClient3.getName()).thenReturn("Client # 3");
        when(modelClient3.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        
        ClientDetailsEntity clientDetails3 = new ClientDetailsEntity();
        clientDetails3.setDeactivatedDate(new Date());

        when(clientManagerReadOnly.get(clientId3)).thenReturn(modelClient3);
        when(clientDetailsManagerReadOnly.findByClientId(clientId3)).thenReturn(clientDetails3);

        Client result3 = manageMembers.findClient(clientId3);
        assertNotNull(result3);
        assertTrue(result3.isDeactivated());
    }

    @Test
    public void editClientWithInvalidRedirectUriTest() throws Exception {
        String clientId = "APP-0000000000000002";
        org.orcid.jaxb.model.v3.release.client.Client modelClient = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(modelClient.getId()).thenReturn(clientId);
        when(modelClient.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        
        when(clientManagerReadOnly.get(clientId)).thenReturn(modelClient);
        when(clientDetailsManagerReadOnly.findByClientId(clientId)).thenReturn(clientDetails);
        
        Client clientToUpdate = manageMembers.findClient(clientId);
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://érm.com"));
        clientToUpdate.getRedirectUris().add(rUri);
        
        when(groupAdministratorController.validateRedirectUris(any(Client.class), eq(true))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            c.getErrors().add("manage.developer_tools.invalid_redirect_uri");
            return c;
        });

        Client result = manageMembers.updateClient(clientToUpdate);
        
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("manage.developer_tools.invalid_redirect_uri", result.getErrors().get(0));
    }

    @Test
    public void editMemberDoesntChangePersistentTokenEnabledValueTest() throws Exception {
        String clientId = "APP-0000000000000001";
        org.orcid.jaxb.model.v3.release.client.Client modelClient = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(modelClient.getId()).thenReturn(clientId);
        when(modelClient.getName()).thenReturn("Client # 1");
        when(modelClient.isPersistentTokensEnabled()).thenReturn(true);
        when(modelClient.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        
        when(clientManagerReadOnly.get(clientId)).thenReturn(modelClient);
        when(clientDetailsManagerReadOnly.findByClientId(clientId)).thenReturn(clientDetails);
        
        Client clientFromFind = manageMembers.findClient(clientId);
        clientFromFind.getDisplayName().setValue("Updated Name");
        
        org.orcid.jaxb.model.v3.release.client.Client updatedModel = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(updatedModel.getId()).thenReturn(clientId);
        when(updatedModel.getName()).thenReturn("Updated Name");
        when(updatedModel.isPersistentTokensEnabled()).thenReturn(true);
        when(updatedModel.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        
        when(clientManager.edit(any(org.orcid.jaxb.model.v3.release.client.Client.class), eq(true))).thenReturn(updatedModel);

        Client result = manageMembers.updateClient(clientFromFind);
        
        assertEquals("Updated Name", result.getDisplayName().getValue());
        assertTrue(result.getPersistentTokenEnabled().getValue());
    }

    @Test
    public void editGroupTypeTest() throws Exception {
        String orcid = "5555-5555-5555-0000";
        Member member = new Member();
        member.setGroupOrcid(Text.valueOf(orcid));
        member.setType(Text.valueOf(MemberType.PREMIUM_INSTITUTION.value()));
        member.setEmail(Text.valueOf("group@email.com"));
        member.setGroupName(Text.valueOf("Group Name"));
        member.setSalesforceId(Text.valueOf("1234567890abcde"));
        
        when(membersManager.getMember(orcid)).thenReturn(member);
        
        Member group = manageMembers.findMember(orcid);
        assertEquals(MemberType.PREMIUM_INSTITUTION.value(), group.getType().getValue());
        
        group.setType(Text.valueOf(MemberType.BASIC.value()));
        manageMembers.updateMember(group);
        
        Member updatedGroup = manageMembers.findMember(orcid);
        assertEquals(MemberType.BASIC.value(), updatedGroup.getType().getValue());
    }

    @Test
    public void testDeactivateClient() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        when(clientDetailsManager.exists("test")).thenReturn(true);
        
        ClientActivationRequest clientDeactivation = new ClientActivationRequest();
        clientDeactivation.setClientId("test");
        clientDeactivation = manageMembers.deactivateClient(clientDeactivation);
        
        assertNull(clientDeactivation.getError());
        Mockito.verify(clientDetailsManager, Mockito.times(1)).deactivateClientDetails(eq("test"), eq("4444-4444-4444-4440"));
    }

    @Test
    public void testActivateClient() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        when(clientDetailsManager.exists("test")).thenReturn(true);
        
        ClientActivationRequest clientActivation = new ClientActivationRequest();
        clientActivation.setClientId("test");
        clientActivation = manageMembers.activateClient(clientActivation);
        
        assertNull(clientActivation.getError());
        Mockito.verify(clientDetailsManager, Mockito.times(1)).activateClientDetails(eq("test"));
    }

    @Test
    public void testDeactivateClientAlreadyDeactivated() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        when(clientDetailsManager.exists("test")).thenReturn(true);
        Mockito.doThrow(new org.orcid.core.exception.ClientAlreadyDeactivatedException("already-deactivated")).when(clientDetailsManager).deactivateClientDetails(eq("test"), eq("4444-4444-4444-4440"));
        
        ClientActivationRequest clientDeactivation = new ClientActivationRequest();
        clientDeactivation.setClientId("test");
        clientDeactivation = manageMembers.deactivateClient(clientDeactivation);
        
        assertNotNull(clientDeactivation.getError());
        assertEquals("already-deactivated", clientDeactivation.getError());
    }

    @Test
    public void testActivateClientAlreadyActive() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        when(clientDetailsManager.exists("test")).thenReturn(true);
        Mockito.doThrow(new org.orcid.core.exception.ClientAlreadyActiveException("already-active")).when(clientDetailsManager).activateClientDetails(eq("test"));
        
        ClientActivationRequest clientActivation = new ClientActivationRequest();
        clientActivation.setClientId("test");
        clientActivation = manageMembers.activateClient(clientActivation);
        
        assertNotNull(clientActivation.getError());
        assertEquals("already-active", clientActivation.getError());
    }

    private Client findClientById(List<Client> clients, String id) {
        return clients.stream().filter(c -> id.equals(c.getClientId().getValue())).findFirst().orElse(null);
    }
}
