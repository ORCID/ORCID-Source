package org.orcid.internal.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.internal.util.AccountRecoveryMatchRequest;
import org.orcid.internal.util.AccountRecoveryMatchResponse;
import org.orcid.internal.util.AccountRecoveryResetLinkRequest;
import org.orcid.internal.util.AccountRecoveryResetLinkResponse;
import org.orcid.internal.server.delegator.impl.InternalApiServiceDelegatorImpl;
import org.orcid.internal.util.EmailResponse;
import org.orcid.internal.util.LastModifiedResponse;
import org.orcid.internal.util.MemberInfo;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.utils.ExpiringLinkService;
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

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private ExpiringLinkService expiringLinkService;

    @Mock
    private RedisClient redisClient;

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @Before
    public void setUp() {
        internalApiServiceDelegator.setResetLinkExpirationInMinutes(1440L);
    }

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
        verify(orcidSecurityManager).checkScopes(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED, ScopePathType.INTERNAL);
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

    private static final String USER_EMAIL = "5555-5555-5555-5558@user.com";

    private AccountRecoveryMatchRequest matchRequest(String orcid, String email) {
        AccountRecoveryMatchRequest request = new AccountRecoveryMatchRequest();
        request.setOrcid(orcid);
        request.setEmail(email);
        return request;
    }

    private AccountRecoveryResetLinkRequest resetLinkRequest(String orcid) {
        AccountRecoveryResetLinkRequest request = new AccountRecoveryResetLinkRequest();
        request.setOrcid(orcid);
        return request;
    }

    @Test
    public void accountRecoveryMatchTest() {
        when(emailManagerReadOnly.findOrcidIdByEmail(USER_EMAIL)).thenReturn(USER_ORCID);
        Response response = internalApiServiceDelegator.accountRecoveryMatch(matchRequest(USER_ORCID, USER_EMAIL));
        assertNotNull(response);
        AccountRecoveryMatchResponse info = (AccountRecoveryMatchResponse) response.getEntity();
        assertTrue(info.isMatch());
        assertEquals(AccountRecoveryMatchResponse.RecordStatus.ACTIVE, info.getRecordStatus());
    }

    /**
     * Every kind of non match has to look the same from the outside, otherwise the endpoint tells a
     * caller whether an address is registered.
     */
    @Test
    public void accountRecoveryMatchIsNotAnEmailOracleTest() {
        when(emailManagerReadOnly.findOrcidIdByEmail(USER_EMAIL)).thenReturn(USER_ORCID);
        when(emailManagerReadOnly.findOrcidIdByEmail("nobody@user.com")).thenReturn(null);

        // A registered email, paired with the wrong iD
        Response wrongPair = internalApiServiceDelegator.accountRecoveryMatch(matchRequest("0000-0000-0000-0000", USER_EMAIL));
        AccountRecoveryMatchResponse wrongPairInfo = (AccountRecoveryMatchResponse) wrongPair.getEntity();

        // An address nobody has registered
        Response unknownEmail = internalApiServiceDelegator.accountRecoveryMatch(matchRequest(USER_ORCID, "nobody@user.com"));
        AccountRecoveryMatchResponse unknownEmailInfo = (AccountRecoveryMatchResponse) unknownEmail.getEntity();

        assertEquals(wrongPair.getStatus(), unknownEmail.getStatus());
        assertFalse(wrongPairInfo.isMatch());
        assertFalse(unknownEmailInfo.isMatch());
        assertNull(wrongPairInfo.getRecordStatus());
        assertNull(unknownEmailInfo.getRecordStatus());
    }

    @Test(expected = OrcidAccessControlException.class)
    public void accountRecoveryMatchWrongScopeTest() {
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkScopes(ScopePathType.INTERNAL_ACCOUNT_RECOVERY);
        internalApiServiceDelegator.accountRecoveryMatch(matchRequest(USER_ORCID, USER_EMAIL));
    }

    @Test
    public void accountRecoveryResetLinkTest() throws Exception {
        when(profileEntityManager.orcidExists(USER_ORCID)).thenReturn(true);
        when(expiringLinkService.generateExpiringToken(USER_ORCID, 1440L, ExpiringLinkService.ExpiringLinkType.PASSWORD_RESET)).thenReturn("jwt-token");
        when(orcidUrlManager.getBaseUrl()).thenReturn("https://orcid.org");
        Response response = internalApiServiceDelegator.accountRecoveryResetLink(resetLinkRequest(USER_ORCID));
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        AccountRecoveryResetLinkResponse info = (AccountRecoveryResetLinkResponse) response.getEntity();
        assertNotNull(info.getResetLink());
        assertTrue(info.getResetLink().contains("/reset-password-email/"));
        assertNotNull(info.getIssueDate());
        assertNotNull(info.getExpiryDate());
        // Proves the expiry is wired from the Spring context rather than left at zero.
        assertTrue(info.getExpiryDate().after(info.getIssueDate()));
    }

    @Test
    public void accountRecoveryResetLinkUnknownRecordTest() {
        when(profileEntityManager.orcidExists("0000-0000-0000-0000")).thenReturn(false);
        Response response = internalApiServiceDelegator.accountRecoveryResetLink(resetLinkRequest("0000-0000-0000-0000"));
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test(expected = OrcidAccessControlException.class)
    public void accountRecoveryResetLinkWrongScopeTest() {
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkScopes(ScopePathType.INTERNAL_ACCOUNT_RECOVERY);
        internalApiServiceDelegator.accountRecoveryResetLink(resetLinkRequest(USER_ORCID));
    }
}
