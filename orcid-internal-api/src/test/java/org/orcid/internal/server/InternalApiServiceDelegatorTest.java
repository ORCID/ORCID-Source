package org.orcid.internal.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.internal.server.delegator.impl.InternalApiServiceDelegatorImpl;
import org.orcid.internal.util.EmailResponse;
import org.orcid.internal.util.LastModifiedResponse;
import org.orcid.internal.util.MemberInfo;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class InternalApiServiceDelegatorTest {

    private static final String USER_ORCID = "5555-5555-5555-5558";

    @InjectMocks
    private InternalApiServiceDelegatorImpl internalApiServiceDelegator = new InternalApiServiceDelegatorImpl();

    @Mock
    private MembersManager memberManager;

    @Mock
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Test
    public void viewStatusTextTest() {
        Response response = internalApiServiceDelegator.viewStatusText();
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals("OK I am here", String.valueOf(response.getEntity()));
    }

    @Test
    public void viewLastModifiedTest() {
        Date lastModified = new Date();
        when(profileEntityManagerReadOnly.getLastModifiedDate(USER_ORCID)).thenReturn(lastModified);

        Response response = internalApiServiceDelegator.viewPersonLastModified(USER_ORCID);

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity().getClass().isAssignableFrom(LastModifiedResponse.class));
        LastModifiedResponse obj = (LastModifiedResponse) response.getEntity();
        assertNotNull(obj);
        assertEquals(USER_ORCID, obj.getOrcid());
        assertEquals(lastModified.toString(), obj.getLastModified());
        verify(orcidSecurityManager).checkScopes(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED);
    }

    @Test
    public void viewMemberInfoTest() {
        String memberId = USER_ORCID;
        String memberName = "Test member";
        Member member = new Member();
        member.setGroupOrcid(Text.valueOf(memberId));
        member.setGroupName(Text.valueOf(memberName));
        Client client = new Client();
        client.setClientId(Text.valueOf("APP-5555555555555555"));
        client.setDisplayName(Text.valueOf("Client name"));
        member.setClients(Collections.singletonList(client));
        when(memberManager.getMember(memberId)).thenReturn(member);
        when(memberManager.getMember(memberName)).thenReturn(member);
        when(memberManager.getMember("invalid name")).thenReturn(null);

        Response response = internalApiServiceDelegator.viewMemberInfo(memberId);
        assertNotNull(response);
        MemberInfo info = (MemberInfo) response.getEntity();
        assertNotNull(info);
        assertEquals(memberId, info.getId());
        assertNotNull(info.getName());
        assertNotNull(info.getClients());
        assertFalse(info.getClients().isEmpty());

        response = internalApiServiceDelegator.viewMemberInfo(memberName);
        assertNotNull(response);
        MemberInfo infoByName = (MemberInfo) response.getEntity();
        assertNotNull(infoByName);
        assertEquals(memberId, infoByName.getId());
        assertEquals(info, infoByName);

        response = internalApiServiceDelegator.viewMemberInfo("invalid name");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        OrcidError error = (OrcidError) response.getEntity();
        assertNotNull(error);
        assertEquals(Integer.valueOf(0), error.getErrorCode());
        assertEquals("Member id or name not found for: invalid name", error.getDeveloperMessage());
    }

    @Test
    public void findOrcidByEmailTest() {
        when(emailManagerReadOnly.emailExists("5555-5555-5555-5558@user.com")).thenReturn(true);
        when(emailManagerReadOnly.findOrcidByVerifiedEmail("5555-5555-5555-5558@user.com")).thenReturn(USER_ORCID);

        Response response = internalApiServiceDelegator.findOrcidByEmail("5555-5555-5555-5558@user.com");
        assertNotNull(response);
        EmailResponse info = (EmailResponse) response.getEntity();
        assertEquals(HttpStatus.FOUND, info.getStatus());
        assertEquals("5555-5555-5555-5558@user.com", info.getEmail());
        assertEquals(USER_ORCID, info.getOrcid());
        verify(orcidSecurityManager).checkScopes(ScopePathType.INTERNAL);
        verify(orcidSecurityManager).checkProfile(USER_ORCID);

        response = internalApiServiceDelegator.findOrcidByEmail("invalid@email.com");
        assertNotNull(response);
        info = (EmailResponse) response.getEntity();
        assertEquals(HttpStatus.NOT_FOUND, info.getStatus());
        assertEquals("invalid@email.com", info.getEmail());
        assertEquals("", info.getOrcid());
    }

    @Test(expected = OrcidAccessControlException.class)
    public void findOrcidByEmailWrongScopeTest() {
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkScopes(ScopePathType.INTERNAL);
        internalApiServiceDelegator.findOrcidByEmail("5555-5555-5555-5558@user.com");
    }
}
