package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;

import org.junit.After;
import org.junit.AfterClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.ClientAlreadyActiveException;
import org.orcid.core.exception.ClientAlreadyDeactivatedException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.v3.ClientDetailsManager;
import org.orcid.core.manager.v3.ClientManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.security.OrcidRoles;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
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
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ManageMembersControllerTest {

    @Mock(name = "membersManagerV3")
    private MembersManager membersManager;

    @Mock(name = "clientManagerV3")
    private ClientManager clientManager;

    @Mock(name = "clientManagerReadOnlyV3")
    private ClientManagerReadOnly clientManagerReadOnly;

    @Mock(name = "clientDetailsManagerReadOnlyV3")
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Mock(name = "clientDetailsManagerV3")
    private ClientDetailsManager clientDetailsManager;

    @Mock(name = "emailManagerV3")
    private EmailManager emailManager;

    @Mock
    private ClientsController groupAdministratorController;

    @Mock
    private LocaleManager localeManager;

    @InjectMocks
    private ManageMembersController manageMembers;

    @Before
    public void setUp() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        // Mock localeManager to return the message code as the translated message
        when(localeManager.resolveMessage(anyString(), any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private Authentication getAuthentication() {
        String orcid = "4444-4444-4444-4440";
        UserDetails details = new User(orcid, "password", List.of(new SimpleGrantedAuthority(OrcidRoles.ROLE_ADMIN.name())));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(orcid, "password", details.getAuthorities());
        auth.setDetails(details);
        return auth;
    }

    @Test
    public void testGetManageMembersPage() {
        ModelAndView mav = manageMembers.getManageMembersPage();
        assertEquals("/admin/manage_members", mav.getViewName());
    }

    @Test
    public void testGetEmptyGroup() {
        Member member = manageMembers.getEmptyGroup();
        assertNotNull(member);
        assertEquals("", member.getEmail().getValue());
        assertEquals("", member.getGroupName().getValue());
        assertEquals("", member.getGroupOrcid().getValue());
        assertEquals("", member.getSalesforceId().getValue());
        assertEquals(MemberType.BASIC.value(), member.getType().getValue());
    }

    @Test
    public void testFind_ClientBranch() {
        String id = "APP-123";
        when(clientDetailsManagerReadOnly.exists(id)).thenReturn(true);
        
        // Mock findClient behavior
        org.orcid.jaxb.model.v3.release.client.Client modelClient = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(modelClient.getId()).thenReturn(id);
        when(modelClient.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        when(clientManagerReadOnly.get(id)).thenReturn(modelClient);
        
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        when(clientDetailsManagerReadOnly.findByClientId(id)).thenReturn(clientDetails);

        Object resultObj = manageMembers.find(id);
        assertTrue((Boolean) reflectGet(resultObj, "isClient"));
        Object clientObj = reflectGet(resultObj, "clientObject");
        assertNotNull(clientObj);
        assertEquals(id, ((Client) clientObj).getClientId().getValue());
    }

    private Object reflectGet(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testFind_MemberBranch() {
        String id = "0000-0000-0000-0001";
        when(clientDetailsManagerReadOnly.exists(id)).thenReturn(false);
        
        Member member = new Member();
        member.setGroupOrcid(Text.valueOf(id));
        when(membersManager.getMember(id)).thenReturn(member);

        Object resultObj = manageMembers.find(id);
        assertFalse((Boolean) reflectGet(resultObj, "isClient"));
        Object memberObj = reflectGet(resultObj, "memberObject");
        assertNotNull(memberObj);
        assertEquals(id, ((Member) memberObj).getGroupOrcid().getValue());
    }

    @Test
    public void testFindMember_Empty() {
        Member result = manageMembers.findMember("");
        assertEquals(1, result.getErrors().size());
        assertEquals("manage_member.not_blank", result.getErrors().get(0));
    }

    @Test
    public void testFindMember_Success() {
        String id = "some-id";
        Member member = new Member();
        when(membersManager.getMember(id)).thenReturn(member);
        Member result = manageMembers.findMember(id);
        assertSame(member, result);
    }

    @Test
    public void testCreateMember_Validation_EmailEmpty() {
        Member member = createValidMember();
        member.setEmail(Text.valueOf(""));
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("NotBlank.group.email"));
    }

    @Test
    public void testCreateMember_Validation_EmailInvalid() {
        Member member = createValidMember();
        member.setEmail(Text.valueOf("invalid-email"));
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("group.email.invalid_email"));
    }

    @Test
    public void testCreateMember_Validation_EmailExists() {
        Member member = createValidMember();
        when(emailManager.emailExists(member.getEmail().getValue())).thenReturn(true);
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("group.email.already_used"));
    }

    @Test
    public void testCreateMember_Validation_GroupNameEmpty() {
        Member member = createValidMember();
        member.setGroupName(Text.valueOf(""));
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("NotBlank.group.name"));
    }

    @Test
    public void testCreateMember_Validation_GroupNameTooLong() {
        Member member = createValidMember();
        member.setGroupName(Text.valueOf("a".repeat(151)));
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("group.name.too_long"));
    }

    @Test
    public void testCreateMember_Validation_TypeEmpty() {
        Member member = createValidMember();
        member.setType(Text.valueOf(""));
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("NotBlank.group.type"));
    }

    @Test
    public void testCreateMember_Validation_TypeInvalid() {
        Member member = createValidMember();
        member.setType(Text.valueOf("INVALID_TYPE"));
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("group.type.invalid"));
    }

    @Test
    public void testCreateMember_Validation_SalesforceIdInvalidLength() {
        Member member = createValidMember();
        member.setSalesforceId(Text.valueOf("123"));
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("group.salesforce_id.invalid_length"));
    }

    @Test
    public void testCreateMember_Validation_SalesforceIdInvalidPattern() {
        Member member = createValidMember();
        member.setSalesforceId(Text.valueOf("12345678901234!")); // 15 chars but with '!'
        
        Member result = manageMembers.createMember(member);
        assertTrue(result.getErrors().contains("group.salesforce_id.invalid"));
    }

    @Test
    public void testCreateMember_Success() {
        Member member = createValidMember();
        when(membersManager.createMember(any(Member.class))).thenReturn(member);
        
        Member result = manageMembers.createMember(member);
        assertEquals(0, result.getErrors().size());
        verify(membersManager).createMember(member);
    }

    @Test
    public void testUpdateMember_Success() {
        Member member = createValidMember();
        member.setGroupOrcid(Text.valueOf("0000-0000-0000-0001"));
        when(membersManager.updateMemeber(any(Member.class))).thenReturn(member);
        
        Member result = manageMembers.updateMember(member);
        assertEquals(0, result.getErrors().size());
        verify(membersManager).updateMemeber(member);
    }

    @Test
    public void testUpdateMember_EmailOwnership_SameOwner() {
        Member member = createValidMember();
        String orcid = "0000-0000-0000-0001";
        member.setGroupOrcid(Text.valueOf(orcid));
        String email = "test@orcid.org";
        member.setEmail(Text.valueOf(email));
        
        when(emailManager.emailExists(email)).thenReturn(true);
        Map<String, String> owners = new HashMap<>();
        owners.put(email, orcid);
        when(emailManager.findOricdIdsByCommaSeparatedEmails(email)).thenReturn(owners);
        when(membersManager.updateMemeber(any(Member.class))).thenReturn(member);

        Member result = manageMembers.updateMember(member);
        assertEquals(0, result.getErrors().size());
    }

    @Test
    public void testUpdateMember_EmailOwnership_DifferentOwner() {
        Member member = createValidMember();
        String orcid = "0000-0000-0000-0001";
        member.setGroupOrcid(Text.valueOf(orcid));
        String email = "test@orcid.org";
        member.setEmail(Text.valueOf(email));
        
        when(emailManager.emailExists(email)).thenReturn(true);
        Map<String, String> owners = new HashMap<>();
        owners.put(email, "0000-0000-0000-0002"); // different owner
        when(emailManager.findOricdIdsByCommaSeparatedEmails(email)).thenReturn(owners);

        Member result = manageMembers.updateMember(member);
        assertTrue(result.getErrors().contains("group.email.already_used"));
    }

    @Test
    public void testFindClient_Empty() {
        Client result = manageMembers.findClient("");
        assertEquals(1, result.getErrors().size());
        assertEquals("manage_member.not_blank", result.getErrors().get(0));
    }

    @Test
    public void testFindClient_Success() {
        String clientId = "APP-123";
        org.orcid.jaxb.model.v3.release.client.Client model = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(model.getId()).thenReturn(clientId);
        when(model.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        
        when(clientManagerReadOnly.get(clientId)).thenReturn(model);
        
        ClientDetailsEntity details = new ClientDetailsEntity();
        details.setDeactivatedDate(new Date());
        when(clientDetailsManagerReadOnly.findByClientId(clientId)).thenReturn(details);
        
        Client result = manageMembers.findClient(clientId);
        assertEquals(clientId, result.getClientId().getValue());
        assertTrue(result.isDeactivated());
    }

    @Test
    public void testUpdateClient_Success() {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Name"));
        client.setWebsite(Text.valueOf("http://website.com"));
        client.setShortDescription(Text.valueOf("Desc"));
        client.setRedirectUris(new ArrayList<>());
        
        org.orcid.jaxb.model.v3.release.client.Client model = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(model.getId()).thenReturn("APP-123");
        when(model.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        
        when(clientManager.edit(any(), eq(true))).thenReturn(model);
        
        Client result = manageMembers.updateClient(client);
        assertEquals(0, result.getErrors().size());
        verify(clientManager).edit(any(), eq(true));
    }

    @Test
    public void testUpdateClient_WithIdP_NoInstitutionalRedirectUri() {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Name"));
        client.setAuthenticationProviderId(Text.valueOf("IDP-123"));
        client.setRedirectUris(new ArrayList<>());
        
        Client result = manageMembers.updateClient(client);
        assertTrue(result.getErrors().contains("manage.developer_tools.client.idp.error.no_redirect_uri_found"));
    }

    @Test
    public void testUpdateClient_WithIdP_WithInstitutionalRedirectUri() {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("Name"));
        client.setAuthenticationProviderId(Text.valueOf("IDP-123"));
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf(RedirectUriType.INSTITUTIONAL_SIGN_IN.value()));
        rUri.setValue(Text.valueOf("http://redirect.uri"));
        client.setRedirectUris(List.of(rUri));
        
        org.orcid.jaxb.model.v3.release.client.Client model = mock(org.orcid.jaxb.model.v3.release.client.Client.class);
        when(model.getId()).thenReturn("APP-123");
        when(model.getClientType()).thenReturn(org.orcid.jaxb.model.clientgroup.ClientType.CREATOR);
        when(clientManager.edit(any(), eq(true))).thenReturn(model);

        Client result = manageMembers.updateClient(client);
        assertFalse(result.getErrors().contains("manage.developer_tools.client.idp.error.no_redirect_uri_found"));
    }

    @Test
    public void testGetEmptyRedirectUri() {
        RedirectUri result = manageMembers.getEmptyRedirectUri();
        assertEquals(RedirectUriType.DEFAULT.value(), result.getType().getValue());
        assertEquals("", result.getValue().getValue());
    }

    @Test
    public void testDeactivateClient_NotFound() {
        ClientActivationRequest request = new ClientActivationRequest();
        request.setClientId("APP-MISSING");
        when(clientDetailsManager.exists("APP-MISSING")).thenReturn(false);
        
        ClientActivationRequest result = manageMembers.deactivateClient(request);
        assertEquals("Client not found", result.getError());
    }

    @Test
    public void testDeactivateClient_AlreadyDeactivated() throws ClientAlreadyDeactivatedException {
        ClientActivationRequest request = new ClientActivationRequest();
        request.setClientId("APP-123");
        when(clientDetailsManager.exists("APP-123")).thenReturn(true);
        doThrow(new ClientAlreadyDeactivatedException("already deactivated"))
            .when(clientDetailsManager).deactivateClientDetails(eq("APP-123"), anyString());
        
        ClientActivationRequest result = manageMembers.deactivateClient(request);
        assertEquals("already deactivated", result.getError());
    }

    @Test
    public void testDeactivateClient_Success() throws ClientAlreadyDeactivatedException {
        ClientActivationRequest request = new ClientActivationRequest();
        request.setClientId("APP-123");
        when(clientDetailsManager.exists("APP-123")).thenReturn(true);
        
        ClientActivationRequest result = manageMembers.deactivateClient(request);
        assertNull(result.getError());
        verify(clientDetailsManager).deactivateClientDetails(eq("APP-123"), eq("4444-4444-4444-4440"));
    }

    @Test
    public void testActivateClient_NotFound() {
        ClientActivationRequest request = new ClientActivationRequest();
        request.setClientId("APP-MISSING");
        when(clientDetailsManager.exists("APP-MISSING")).thenReturn(false);
        
        ClientActivationRequest result = manageMembers.activateClient(request);
        assertEquals("Client not found", result.getError());
    }

    @Test
    public void testActivateClient_AlreadyActive() throws ClientAlreadyActiveException {
        ClientActivationRequest request = new ClientActivationRequest();
        request.setClientId("APP-123");
        when(clientDetailsManager.exists("APP-123")).thenReturn(true);
        doThrow(new ClientAlreadyActiveException("already active"))
            .when(clientDetailsManager).activateClientDetails("APP-123");
        
        ClientActivationRequest result = manageMembers.activateClient(request);
        assertEquals("already active", result.getError());
    }

    @Test
    public void testActivateClient_Success() throws ClientAlreadyActiveException {
        ClientActivationRequest request = new ClientActivationRequest();
        request.setClientId("APP-123");
        when(clientDetailsManager.exists("APP-123")).thenReturn(true);
        
        ClientActivationRequest result = manageMembers.activateClient(request);
        assertNull(result.getError());
        verify(clientDetailsManager).activateClientDetails("APP-123");
    }

    @Test
    public void testGetRedirectUriTypes() {
        Map<String, String> types = manageMembers.getRedirectUriTypes();
        assertFalse(types.containsKey(RedirectUriType.SSO_AUTHENTICATION.value()));
        assertTrue(types.containsKey(RedirectUriType.DEFAULT.value()));
    }

    @Test
    public void testRetrieveGroupTypes() {
        Map<String, String> types = manageMembers.retrieveGroupTypes();
        assertFalse(types.containsKey(MemberType.BASIC_INSTITUTION.value()));
        assertFalse(types.containsKey(MemberType.PREMIUM_INSTITUTION.value()));
        assertTrue(types.containsKey(MemberType.BASIC.value()));
        assertEquals("basic", types.get(MemberType.BASIC.value()));
    }

    private Member createValidMember() {
        Member member = new Member();
        member.setEmail(Text.valueOf("test@orcid.org"));
        member.setGroupName(Text.valueOf("Test Group"));
        member.setType(Text.valueOf(MemberType.BASIC.value()));
        member.setSalesforceId(Text.valueOf("1234567890abcde"));
        return member;
    }
}
