package org.orcid.core.manager.read_only;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.manager.read_only.impl.EmailManagerReadOnlyImpl;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;

/**
 * Unit stage cover for the v2 {@link EmailManagerReadOnlyImpl}, which is what the member v2
 * API email endpoint reads through. The rule under test is that the endpoint only ever hands
 * out addresses the record holder has verified.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailManagerReadOnlyTest {

    private static final String ORCID = "0000-0000-0000-0001";
    private static final long LAST_MODIFIED = 1500000000000L;

    private static final String UNVERIFIED_EMAIL = "unverified_0000-0000-0000-0001@test.orcid.org";
    private static final String VERIFIED_EMAIL_1 = "public_0000-0000-0000-0001@test.orcid.org";
    private static final String VERIFIED_EMAIL_2 = "limited_0000-0000-0000-0001@test.orcid.org";

    @Mock
    private EmailDao emailDao;

    @Mock
    private JpaJaxbEmailAdapter jpaJaxbEmailAdapter;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    @InjectMocks
    private EmailManagerReadOnlyImpl emailManagerReadOnly = new EmailManagerReadOnlyImpl();

    @Before
    public void setUp() {
        when(profileLastModifiedAspect.retrieveLastModifiedDate(ORCID)).thenReturn(new Date(LAST_MODIFIED));

        // One row on this record is unverified. The DAO hands back all three, every time:
        // whatever filtering the endpoint does, it does it in the manager.
        when(emailDao.findByOrcid(ORCID, LAST_MODIFIED)).thenReturn(threeEmailRows());

        // Stand in for the real adapter: carry the address and the verified flag across, so the
        // assertions below read the entities the manager actually decided to convert.
        when(jpaJaxbEmailAdapter.toEmailList(anyCollection())).thenAnswer(invocation -> {
            Collection<EmailEntity> entities = invocation.getArgument(0);
            return entities.stream().map(entity -> {
                Email email = new Email();
                email.setEmail(entity.getEmail());
                email.setVerified(entity.getVerified());
                return email;
            }).collect(Collectors.toList());
        });
    }

    /*
     * Mutation this catches: deleting the `.filter(e -> e.getVerified())` in
     * EmailManagerReadOnlyImpl.getVerifiedEmails -- the unverified address would leak into the
     * member v2 email endpoint's response and the size would be three.
     */
    @Test
    public void getVerifiedEmailsReturnsOnlyTheVerifiedAddresses() {
        Emails emails = emailManagerReadOnly.getVerifiedEmails(ORCID);

        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(2, emails.getEmails().size());

        Set<String> addresses = addressesOf(emails);
        assertEquals(new HashSet<>(Arrays.asList(VERIFIED_EMAIL_1, VERIFIED_EMAIL_2)), addresses);
        assertFalse("an unverified address must never reach the caller", addresses.contains(UNVERIFIED_EMAIL));

        for (Email email : emails.getEmails()) {
            assertTrue(Boolean.TRUE.equals(email.isVerified()));
        }

        // the manager read the whole record, so the two above are its own choice and not the DAO's
        verify(emailDao, times(1)).findByOrcid(ORCID, LAST_MODIFIED);
    }

    /*
     * Mutation this catches: narrowing EmailDao.findByOrcid so the unverified row never
     * reaches the manager at all. That would keep the test above green while the manager's own
     * filter did nothing, so this test pins the unfiltered read at all three addresses.
     */
    @Test
    public void getEmailsReturnsEveryAddressIncludingTheUnverifiedOne() {
        Emails emails = emailManagerReadOnly.getEmails(ORCID);

        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(3, emails.getEmails().size());

        Set<String> addresses = addressesOf(emails);
        assertEquals(new HashSet<>(Arrays.asList(UNVERIFIED_EMAIL, VERIFIED_EMAIL_1, VERIFIED_EMAIL_2)), addresses);
        assertTrue("the unfiltered read keeps the unverified address", addresses.contains(UNVERIFIED_EMAIL));

        verify(emailDao, times(1)).findByOrcid(ORCID, LAST_MODIFIED);
    }

    private Set<String> addressesOf(Emails emails) {
        return emails.getEmails().stream().map(Email::getEmail).collect(Collectors.toSet());
    }

    private List<EmailEntity> threeEmailRows() {
        List<EmailEntity> entities = new ArrayList<>();
        entities.add(emailRow(UNVERIFIED_EMAIL, false));
        entities.add(emailRow(VERIFIED_EMAIL_1, true));
        entities.add(emailRow(VERIFIED_EMAIL_2, true));
        return entities;
    }

    private EmailEntity emailRow(String email, boolean verified) {
        EmailEntity entity = new EmailEntity();
        entity.setId(email);
        entity.setEmail(email);
        entity.setOrcid(ORCID);
        entity.setVerified(verified);
        entity.setCurrent(true);
        entity.setPrimary(false);
        return entity;
    }
}
