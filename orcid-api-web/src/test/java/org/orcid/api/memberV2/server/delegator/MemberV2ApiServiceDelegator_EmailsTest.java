package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.test.helper.Utils;

/**
 * The email endpoint of the member v2 delegator, on mocks.
 *
 * <p>
 * {@code viewEmails} is the one read in this family with a real branch of its
 * own: it first asks for {@code /email/read-private}, and only if that is
 * refused with an {@link OrcidAccessControlException} does it fall back to
 * filtering the list through {@code checkAndFilter}. Both arms of that branch,
 * and the fact that the catch is narrow enough to let an
 * {@link OrcidUnauthorizedException} straight out, are asserted below.
 *
 * <p>
 * Which emails survive the fallback is not: {@code checkAndFilter} edits the
 * list in place and a mock does not, so the counts that the database version
 * asserted (four for the source client, two for any other) belong to
 * {@code OrcidSecurityManager_EmailTest}.
 */
public class MemberV2ApiServiceDelegator_EmailsTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String PRIVATE_ORCID = "4444-4444-4444-4497";
    private static final String OTHER_ORCID = "4444-4444-4444-4443";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmailsWrongToken() {
        // The delegator catches OrcidAccessControlException only. A token for a
        // different record fails with OrcidUnauthorizedException, which must come
        // straight out rather than falling through to the filtered read.
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkClientAccessAndScopes(ORCID,
                ScopePathType.EMAIL_READ_PRIVATE);

        try {
            serviceDelegator.viewEmails(ORCID);
        } finally {
            verify(emailManagerReadOnly, never()).getVerifiedEmails(anyString());
        }
    }

    @Test
    public void testViewEmailsReadPublic_withSourceClient() {
        Emails stored = emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1)),
                email(2L, "public_0000-0000-0000-0003@orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1)),
                email(3L, "limited_0000-0000-0000-0003@test.orcid.org", Visibility.LIMITED, clientSource(CLIENT_1)),
                email(4L, "private_0000-0000-0000-0003@test.orcid.org", Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(emailManagerReadOnly.getVerifiedEmails(ORCID)).thenReturn(stored);
        refuseReadPrivate(ORCID);

        Response r = serviceDelegator.viewEmails(ORCID);

        Emails element = (Emails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/email", element.getPath());
        // the cached list must be copied before it is handed to a filter that
        // edits in place
        ArgumentCaptor<List<Email>> filtered = emailListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), filtered.capture(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertNotSame(stored.getEmails(), filtered.getValue());
    }

    @Test
    public void testViewEmailsReadPublic_withOtherClient() {
        Emails stored = emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1)),
                email(2L, "public_0000-0000-0000-0003@orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(emailManagerReadOnly.getVerifiedEmails(ORCID)).thenReturn(stored);
        refuseReadPrivate(ORCID);

        Response r = serviceDelegator.viewEmails(ORCID);

        Emails element = (Emails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/email", element.getPath());
        // A client that is not the source sees fewer emails than one that is,
        // but that difference is produced by checkAndFilter, not here.
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testReadPublicScope_Emails() {
        Emails stored = emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1)),
                email(2L, "public_0000-0000-0000-0003@orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1)),
                email(3L, "limited_0000-0000-0000-0003@test.orcid.org", Visibility.LIMITED, clientSource(CLIENT_1)),
                email(4L, "private_0000-0000-0000-0003@test.orcid.org", Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(emailManagerReadOnly.getVerifiedEmails(ORCID)).thenReturn(stored);
        refuseReadPrivate(ORCID);

        Response r = serviceDelegator.viewEmails(ORCID);
        assertNotNull(r);
        assertEquals(Emails.class.getName(), r.getEntity().getClass().getName());
        Emails email = (Emails) r.getEntity();
        assertNotNull(email);
        assertEquals("/0000-0000-0000-0003/email", email.getPath());
        Utils.verifyLastModified(email.getLastModifiedDate());
        for (Email element : email.getEmails()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void testReadEmailPrivate() {
        Emails stored = emails(email(1L, "public_4444-4444-4444-4497@test.orcid.org", Visibility.PUBLIC, clientSource(CLIENT_1)),
                email(2L, "limited_4444-4444-4444-4497@test.orcid.org", Visibility.LIMITED, clientSource(CLIENT_1)),
                email(3L, "private_4444-4444-4444-4497@test.orcid.org", Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(emailManagerReadOnly.getVerifiedEmails(PRIVATE_ORCID)).thenReturn(stored);

        Response r = serviceDelegator.viewEmails(PRIVATE_ORCID);

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
        // This is the point of the test: a token holding /email/read-private is
        // not put through the visibility filter at all.
        verify(orcidSecurityManager).checkClientAccessAndScopes(PRIVATE_ORCID, ScopePathType.EMAIL_READ_PRIVATE);
        verify(orcidSecurityManager, never()).checkAndFilter(eq(PRIVATE_ORCID), anyList(), any(ScopePathType.class));
    }

    @Test
    public void testViewEmails() {
        Emails stored = emails(email(5L, "teddybass3private@semantico.com", Visibility.PRIVATE, clientSource(CLIENT_1)));
        stored.getEmails().get(0).setVerified(Boolean.TRUE);
        stored.getEmails().get(0).setPrimary(Boolean.FALSE);
        when(emailManagerReadOnly.getVerifiedEmails(OTHER_ORCID)).thenReturn(stored);
        refuseReadPrivate(OTHER_ORCID);

        Response response = serviceDelegator.viewEmails(OTHER_ORCID);

        assertNotNull(response);
        Emails emails = (Emails) response.getEntity();
        assertNotNull(emails);
        assertEquals("/4444-4444-4444-4443/email", emails.getPath());
        Utils.verifyLastModified(emails.getLastModifiedDate());
        assertNotNull(emails.getEmails());
        assertEquals(1, emails.getEmails().size());
        Email email = emails.getEmails().get(0);
        Utils.verifyLastModified(email.getLastModifiedDate());
        assertEquals(email.getEmail(), "teddybass3private@semantico.com");
        assertEquals(Visibility.PRIVATE, email.getVisibility());
        assertEquals("APP-5555555555555555", email.retrieveSourcePath());
        assertEquals(true, email.isVerified());
        assertEquals(false, email.isPrimary());
        verify(orcidSecurityManager).checkAndFilter(eq(OTHER_ORCID), anyList(), eq(ScopePathType.ORCID_BIO_READ_LIMITED));
    }

    @Test
    public void checkSourceOnEmail_EmailEndpointTest() {
        String orcid = "0000-0000-0000-0001";
        Emails stored = emails(email(1L, "limited_verified_0000-0000-0000-0001@test.orcid.org", Visibility.LIMITED, clientSource(CLIENT_1)),
                email(2L, "verified_non_professional@nonprofessional.org", Visibility.LIMITED, clientSource(CLIENT_1)));
        stored.getEmails().forEach(e -> e.setVerified(Boolean.TRUE));
        when(emailManagerReadOnly.getVerifiedEmails(orcid)).thenReturn(stored);
        refuseReadPrivate(orcid);

        Response r = serviceDelegator.viewEmails(orcid);

        Emails emails = (Emails) r.getEntity();
        checkEmails(emails);
    }

    // ------------------------------------------------------------- helpers

    /**
     * A token without {@code /email/read-private}: the delegator's fallback arm.
     */
    private void refuseReadPrivate(String orcid) {
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkClientAccessAndScopes(orcid, ScopePathType.EMAIL_READ_PRIVATE);
    }

    private void checkEmails(Emails emails) {
        assertEquals(2, emails.getEmails().size());
        for (Email e : emails.getEmails()) {
            if (e.getEmail().equals("limited_verified_0000-0000-0000-0001@test.orcid.org")) {
                assertTrue(e.isVerified());
                // The source and name on verified professional email addresses should change
                assertEquals("APP-5555555555555555", e.getSource().retrieveSourcePath());
                assertEquals("Source Client 1", e.getSource().getSourceName().getContent());
            } else if (e.getEmail().equals("verified_non_professional@nonprofessional.org")) {
                assertTrue(e.isVerified());
                // The source and name on non professional email addresses should not change
                assertEquals("APP-5555555555555555", e.getSource().retrieveSourcePath());
                assertEquals("Source Client 1", e.getSource().getSourceName().getContent());
            } else {
                fail("Unexpected email " + e.getEmail());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<Email>> emailListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private Email email(Long putCode, String address, Visibility visibility, Source source) {
        Email email = new Email();
        email.setPutCode(putCode);
        email.setEmail(address);
        email.setVisibility(visibility);
        email.setSource(source);
        email.setCreatedDate(createdDate());
        email.setLastModifiedDate(lastModified());
        return email;
    }

    private Emails emails(Email... elements) {
        Emails emails = new Emails();
        emails.setEmails(new ArrayList<>(Arrays.asList(elements)));
        emails.setLastModifiedDate(lastModified());
        return emails;
    }
}
