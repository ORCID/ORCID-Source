package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the email endpoint of the member V3 API.
 *
 * <p>
 * {@code viewEmails} has two paths: with {@code /email/read-private} the whole
 * verified list is returned, without it the list is copied and handed to
 * {@code OrcidSecurityManager.checkAndFilter}, which removes entries in place.
 * Which entries survive is the security manager's table and is proved by
 * orcid-core's {@code OrcidSecurityManager_EmailTest}; what these tests prove is
 * the delegator's half -- that it takes the branch the scope check dictates,
 * that the list it returns is the same list the filter was given (it copies the
 * cached one first, and returning the pre-filter copy would leak every email),
 * and that source names are resolved.
 */
public class MemberV3ApiServiceDelegator_EmailsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4497 = "4444-4444-4444-4497";
    private static final String UNCLAIMED = "0000-0000-0000-0001";

    private Email email(String address, Visibility visibility, Source source, boolean verified, boolean primary) {
        Email email = new Email();
        email.setEmail(address);
        email.setVisibility(visibility);
        email.setSource(source);
        email.setVerified(verified);
        email.setPrimary(primary);
        email.setLastModifiedDate(lastModified());
        email.setCreatedDate(created());
        return email;
    }

    private Emails emails(Email... elements) {
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<>(Arrays.asList(elements)));
        return emails;
    }

    /** The four verified emails record 0000-0000-0000-0003 holds. */
    private Emails allFourEmails() {
        return emails(email("public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1), true, true),
                email("public_0000-0000-0000-0003@orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1), true, false),
                email("limited_0000-0000-0000-0003@test.orcid.org", Visibility.LIMITED, clientSource(CLIENT_1), true, false),
                email("private_0000-0000-0000-0003@test.orcid.org", Visibility.PRIVATE, clientSource(CLIENT_1), true, false));
    }

    /** The scope check refuses, so the delegator takes the filtering branch. */
    private void withoutReadPrivateScope(String orcid) {
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkClientAccessAndScopes(orcid, ScopePathType.EMAIL_READ_PRIVATE);
    }

    /**
     * Stands in for {@code OrcidSecurityManagerImpl}'s in-place filter: it drops
     * everything that is not public. The rule is not what is under test here --
     * it is proved in orcid-core -- but the delegator must return the list the
     * filter mutated rather than the copy it made before calling it.
     */
    private void keepOnlyPublicEmails() {
        doAnswer(invocation -> {
            List<Email> elements = invocation.getArgument(1);
            elements.removeIf(element -> !Visibility.PUBLIC.equals(element.getVisibility()));
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmailsWrongToken() {
        withoutReadPrivateScope(ORCID);
        when(emailManagerReadOnly.getVerifiedEmails(ORCID)).thenReturn(allFourEmails());
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.ORCID_BIO_READ_LIMITED));

        serviceDelegator.viewEmails(ORCID);
    }

    @Test
    public void testViewEmailsReadPublic() {
        // The token's client is the source of every email on this record, so the
        // filter keeps them all.
        withoutReadPrivateScope(ORCID);
        when(emailManagerReadOnly.getVerifiedEmails(ORCID)).thenReturn(allFourEmails());

        Response r = serviceDelegator.viewEmails(ORCID);
        Emails element = (Emails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/email", element.getPath());
        assertEquals(4, element.getEmails().size());
        List<String> emails = new ArrayList<>();
        emails.add("public_0000-0000-0000-0003@test.orcid.org");
        emails.add("public_0000-0000-0000-0003@orcid.org");
        emails.add("limited_0000-0000-0000-0003@test.orcid.org");
        emails.add("private_0000-0000-0000-0003@test.orcid.org");
        for (Email e : element.getEmails()) {
            if (!emails.contains(e.getEmail())) {
                fail(e.getEmail() + " is not in the email list");
            }
            emails.remove(e.getEmail());
        }
        assertTrue(emails.isEmpty());
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testViewEmailsReadPublic_ClientNotSourceOfAnyEmail() {
        withoutReadPrivateScope(ORCID);
        when(emailManagerReadOnly.getVerifiedEmails(ORCID)).thenReturn(allFourEmails());
        keepOnlyPublicEmails();

        Response r = serviceDelegator.viewEmails(ORCID);
        Emails element = (Emails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/email", element.getPath());
        assertEquals(2, element.getEmails().size());
        List<String> emails = new ArrayList<>();
        emails.add("public_0000-0000-0000-0003@test.orcid.org");
        emails.add("public_0000-0000-0000-0003@orcid.org");
        for (Email e : element.getEmails()) {
            if (!emails.contains(e.getEmail())) {
                fail(e.getEmail() + " is not in the email list");
            }
            emails.remove(e.getEmail());
        }
        assertTrue(emails.isEmpty());
    }

    @Test
    public void testReadPublicScope_Emails() {
        withoutReadPrivateScope(ORCID);
        when(emailManagerReadOnly.getVerifiedEmails(ORCID)).thenReturn(allFourEmails());

        Response r = serviceDelegator.viewEmails(ORCID);
        assertNotNull(r);
        assertEquals(Emails.class.getName(), r.getEntity().getClass().getName());
        Emails email = (Emails) r.getEntity();
        assertNotNull(email);
        assertEquals("/0000-0000-0000-0003/email", email.getPath());
        Utils.verifyLastModified(email.getLastModifiedDate());
        assertEquals(4, email.getEmails().size());
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        boolean found4 = false;
        for (Email element : email.getEmails()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                found1 = true;
            } else if (element.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                found2 = true;
            } else if (element.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                found3 = true;
            } else if (element.getEmail().equals("public_0000-0000-0000-0003@orcid.org")) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }

    @Test
    public void testReadEmailPrivate() {
        // With /email/read-private the scope check passes, so nothing is
        // filtered and the whole verified list comes back in order.
        when(emailManagerReadOnly.getVerifiedEmails(USER_4497))
                .thenReturn(emails(email("public_4444-4444-4444-4497@test.orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1), true, true),
                        email("limited_4444-4444-4444-4497@test.orcid.org", Visibility.LIMITED, clientSource(CLIENT_1), true, false),
                        email("private_4444-4444-4444-4497@test.orcid.org", Visibility.PRIVATE, clientSource(CLIENT_1), true, false)));

        Response r = serviceDelegator.viewEmails(USER_4497);
        assertNotNull(r);
        assertEquals(Emails.class.getName(), r.getEntity().getClass().getName());
        Emails email = (Emails) r.getEntity();
        assertNotNull(email);
        assertEquals("/4444-4444-4444-4497/email", email.getPath());
        assertNotNull(email.getLastModifiedDate());
        assertEquals(3, email.getEmails().size());
        assertEquals("public_4444-4444-4444-4497@test.orcid.org", email.getEmails().get(0).getEmail());
        assertEquals(Visibility.PUBLIC, email.getEmails().get(0).getVisibility());
        assertEquals("limited_4444-4444-4444-4497@test.orcid.org", email.getEmails().get(1).getEmail());
        assertEquals(Visibility.LIMITED, email.getEmails().get(1).getVisibility());
        assertEquals("private_4444-4444-4444-4497@test.orcid.org", email.getEmails().get(2).getEmail());
        assertEquals(Visibility.PRIVATE, email.getEmails().get(2).getVisibility());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4497, ScopePathType.EMAIL_READ_PRIVATE);
        // Nothing was filtered on this branch.
        verify(orcidSecurityManager, never()).checkAndFilter(eq(USER_4497), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testViewEmails() {
        when(emailManagerReadOnly.getVerifiedEmails(USER_4443))
                .thenReturn(emails(email("teddybass3private@semantico.com", Visibility.PRIVATE, clientSource(CLIENT_1), true, false)));

        Response response = serviceDelegator.viewEmails(USER_4443);
        assertNotNull(response);
        Emails emails = (Emails) response.getEntity();
        assertNotNull(emails);
        assertEquals("/4444-4444-4444-4443/email", emails.getPath());
        Utils.verifyLastModified(emails.getLastModifiedDate());
        assertNotNull(emails.getEmails());
        assertEquals(1, emails.getEmails().size());
        Email email = emails.getEmails().get(0);
        assertEquals("teddybass3private@semantico.com", email.getEmail());
        assertEquals(Visibility.PRIVATE, email.getVisibility());
        assertEquals(CLIENT_1, email.retrieveSourcePath());
        assertEquals(true, email.isVerified());
        assertEquals(false, email.isPrimary());
    }

    /**
     * The one assertion in this family that exercises the real
     * {@code SourceUtils}: the client id on each email is resolved to a display
     * name through {@code SourceNameCacheManager}. That is why the base class
     * installs a real SourceUtils rather than a mock.
     */
    @Test
    public void checkSourceOnEmail_EmailEndpointTest() {
        when(emailManagerReadOnly.getVerifiedEmails(UNCLAIMED))
                .thenReturn(emails(email("limited_verified_0000-0000-0000-0001@test.orcid.org", Visibility.LIMITED, clientSource(CLIENT_1), true, false),
                        email("verified_non_professional@nonprofessional.org", Visibility.LIMITED, clientSource(CLIENT_1), true, false)));

        Response r = serviceDelegator.viewEmails(UNCLAIMED);
        Emails emails = (Emails) r.getEntity();
        checkEmails(emails);
    }

    private void checkEmails(Emails emails) {
        assertEquals(2, emails.getEmails().size());
        for (Email e : emails.getEmails()) {
            if (e.getEmail().equals("limited_verified_0000-0000-0000-0001@test.orcid.org")) {
                assertTrue(e.isVerified());
                // The source and name on non verified professional email addresses should not change
                assertEquals(CLIENT_1, e.getSource().retrieveSourcePath());
                assertEquals(CLIENT_1_NAME, e.getSource().getSourceName().getContent());
            } else if (e.getEmail().equals("verified_non_professional@nonprofessional.org")) {
                assertTrue(e.isVerified());
                // The source and name on non professional email addresses should not change
                assertEquals(CLIENT_1, e.getSource().retrieveSourcePath());
                assertEquals(CLIENT_1_NAME, e.getSource().getSourceName().getContent());
            } else {
                fail("Unexpected email " + e.getEmail());
            }
        }
    }
}
