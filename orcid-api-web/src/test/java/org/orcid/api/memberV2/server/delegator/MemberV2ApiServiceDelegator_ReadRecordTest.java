package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.CompletionDate;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.History;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.Preferences;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.SubmissionDate;
import org.orcid.test.helper.Utils;

/**
 * The whole-record endpoint of the member v2 delegator, on mocks.
 *
 * <p>
 * {@code viewRecord} fetches the composed record, hands it to
 * {@code checkAndFilter(String, Record)}, resolves source names on the person and
 * the activities, cleans empty fields, and then walks the whole tree setting
 * paths. The walk is what these tests assert.
 *
 * <p>
 * What they no longer assert is which parts of the record survive. That overload
 * of {@code checkAndFilter} strips elements from the record in place and returns
 * void, so against a mock nothing is stripped and every "only the public parts
 * came back" assertion would hold for the wrong reason. Those assertions live in
 * {@code OrcidSecurityManager_FullRecordTest}.
 */
public class MemberV2ApiServiceDelegator_ReadRecordTest extends MemberV2ApiServiceDelegatorMockBase {

    @Test
    public void testViewRecordWrongScope() {
        Record record = record();
        when(recordManagerReadOnly.getRecord(ORCID)).thenReturn(record);

        Response response = serviceDelegator.viewRecord(ORCID);

        Record returned = (Record) response.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003", returned.getPath());
        assertEquals("/0000-0000-0000-0003/activities", returned.getActivitiesSummary().getPath());
        assertEquals("/0000-0000-0000-0003/person", returned.getPerson().getPath());
        // whether a read-public token may see more than the public parts is
        // decided by the guard, not here
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
    }

    @Test
    public void testViewRecordReadPublic() {
        Record record = record();
        when(recordManagerReadOnly.getRecord(ORCID)).thenReturn(record);

        Response r = serviceDelegator.viewRecord(ORCID);

        Record returned = (Record) r.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003", returned.getPath());
        assertEquals("/0000-0000-0000-0003/activities", returned.getActivitiesSummary().getPath());
        assertEquals("/0000-0000-0000-0003/person", returned.getPerson().getPath());
        assertNotNull(returned.getPerson().getEmails());
        assertEquals("/0000-0000-0000-0003/email", returned.getPerson().getEmails().getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
    }

    @Test
    public void testViewRecordReadPublic_clientThatIsNotTheSourceOfEmails() {
        // A client that is not the source of an email sees only the public ones.
        // The filtering itself is OrcidSecurityManager_EmailTest's; what is
        // modelled here is the record it would have left behind, so that the
        // decoration of a partly-emptied record is still covered.
        Record record = record();
        record.getPerson().setEmails(emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC),
                email(2L, "public_0000-0000-0000-0003@orcid.org", Visibility.PUBLIC)));
        when(recordManagerReadOnly.getRecord(ORCID)).thenReturn(record);

        Response r = serviceDelegator.viewRecord(ORCID);

        Record returned = (Record) r.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003", returned.getPath());
        assertEquals("/0000-0000-0000-0003/person", returned.getPerson().getPath());
        assertEquals(2, returned.getPerson().getEmails().getEmails().size());
        assertEquals("/0000-0000-0000-0003/email", returned.getPerson().getEmails().getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewRecordWrongToken() {
        Record record = record();
        when(recordManagerReadOnly.getRecord(ORCID)).thenReturn(record);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, record);

        try {
            serviceDelegator.viewRecord(ORCID);
        } finally {
            assertNull("nothing must be decorated once the guard has refused", record.getPath());
        }
    }

    @Test
    public void testReadPublicScope_Record() {
        Record record = record();
        when(recordManagerReadOnly.getRecord(ORCID)).thenReturn(record);

        Response response = serviceDelegator.viewRecord(ORCID);

        assertNotNull(response);
        Record returned = (Record) response.getEntity();
        assertEquals("/0000-0000-0000-0003", returned.getPath());
        assertEquals("/0000-0000-0000-0003/person", returned.getPerson().getPath());
        assertEquals("/0000-0000-0000-0003/activities", returned.getActivitiesSummary().getPath());
        // the parts of a record the delegator passes straight through
        assertNotNull(returned.getHistory());
        assertEquals(OrcidType.USER, returned.getOrcidType());
        assertNotNull(returned.getPreferences());
        assertEquals(Locale.EN, returned.getPreferences().getLocale());
        History history = returned.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        Utils.verifyLastModified(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());
        assertNotNull(returned.getOrcidIdentifier());
        OrcidIdentifier id = returned.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
    }

    @Test
    public void testViewRecord() {
        Record record = record();
        when(recordManagerReadOnly.getRecord(ORCID)).thenReturn(record);

        Response response = serviceDelegator.viewRecord(ORCID);

        assertNotNull(response);
        Record returned = (Record) response.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003", returned.getPath());

        // the person half of the walk
        Person person = returned.getPerson();
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        assertEquals("/0000-0000-0000-0003/email", person.getEmails().getPath());
        assertEquals("/0000-0000-0000-0003/biography", person.getBiography().getPath());

        // the activities half of the walk, down to each summary
        assertEquals("/0000-0000-0000-0003/activities", returned.getActivitiesSummary().getPath());
        assertEquals("/0000-0000-0000-0003/educations", returned.getActivitiesSummary().getEducations().getPath());
        assertEquals("/0000-0000-0000-0003/education/20", returned.getActivitiesSummary().getEducations().getSummaries().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/employments", returned.getActivitiesSummary().getEmployments().getPath());
        assertEquals("/0000-0000-0000-0003/employment/17", returned.getActivitiesSummary().getEmployments().getSummaries().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/works", returned.getActivitiesSummary().getWorks().getPath());
        assertEquals("/0000-0000-0000-0003/work/11", returned.getActivitiesSummary().getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/fundings", returned.getActivitiesSummary().getFundings().getPath());
        assertEquals("/0000-0000-0000-0003/funding/10",
                returned.getActivitiesSummary().getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/peer-reviews", returned.getActivitiesSummary().getPeerReviews().getPath());
        assertEquals("/0000-0000-0000-0003/peer-review/9",
                returned.getActivitiesSummary().getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());

        // and source names resolved on both halves
        assertEquals(CLIENT_1_NAME, returned.getActivitiesSummary().getEducations().getSummaries().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, person.getEmails().getEmails().get(0).getSource().getSourceName().getContent());

        Utils.verifyLastModified(returned.getActivitiesSummary().getLastModifiedDate());
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
    }

    @Test
    public void testReadPrivateEmails_OtherThingsJustPublic_Record() {
        // With an /email/read-private token every email survives and the rest of
        // the person is reduced to what is public. Modelled as the record the
        // security manager would have left behind; see
        // OrcidSecurityManager_FullRecordTest for the reduction itself.
        Record record = record();
        record.getPerson().setEmails(emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC),
                email(2L, "limited_0000-0000-0000-0003@test.orcid.org", Visibility.LIMITED),
                email(3L, "private_0000-0000-0000-0003@test.orcid.org", Visibility.PRIVATE)));
        record.getPerson().setKeywords(null);
        when(recordManagerReadOnly.getRecord(ORCID)).thenReturn(record);

        Response r = serviceDelegator.viewRecord(ORCID);

        Record returned = (Record) r.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003", returned.getPath());
        assertEquals(3, returned.getPerson().getEmails().getEmails().size());
        assertEquals("/0000-0000-0000-0003/email", returned.getPerson().getEmails().getPath());
        assertNull(returned.getPerson().getKeywords());
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
    }

    @Test
    public void checkSourceOnEmail_RecordEndpointTest() {
        String orcid = "0000-0000-0000-0001";
        Record record = record();
        record.setOrcidIdentifier(new OrcidIdentifier(orcid));
        record.getPerson().setEmails(emails(email(1L, "limited_verified_0000-0000-0000-0001@test.orcid.org", Visibility.LIMITED),
                email(2L, "verified_non_professional@nonprofessional.org", Visibility.LIMITED)));
        record.getPerson().getEmails().getEmails().forEach(e -> e.setVerified(Boolean.TRUE));
        when(recordManagerReadOnly.getRecord(orcid)).thenReturn(record);

        Response r = serviceDelegator.viewRecord(orcid);

        Record returned = (Record) r.getEntity();
        assertEquals(2, returned.getPerson().getEmails().getEmails().size());
        for (Email e : returned.getPerson().getEmails().getEmails()) {
            assertTrue(e.isVerified());
            assertEquals("APP-5555555555555555", e.getSource().retrieveSourcePath());
            assertEquals("Source Client 1", e.getSource().getSourceName().getContent());
        }
    }

    // ------------------------------------------------------------- fixtures

    private Record record() {
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier(ORCID));
        record.setOrcidType(OrcidType.USER);

        Preferences preferences = new Preferences();
        preferences.setLocale(Locale.EN);
        record.setPreferences(preferences);

        History history = new History();
        history.setClaimed(Boolean.TRUE);
        history.setCompletionDate(new CompletionDate(createdDate().getValue()));
        history.setCreationMethod(CreationMethod.INTEGRATION_TEST);
        history.setSubmissionDate(new SubmissionDate(createdDate().getValue()));
        history.setLastModifiedDate(lastModified());
        history.setSource(clientSource(CLIENT_1));
        record.setHistory(history);

        Person person = new Person();
        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setVisibility(Visibility.PUBLIC);
        name.setCreatedDate(createdDate());
        name.setLastModifiedDate(lastModified());
        person.setName(name);

        org.orcid.jaxb.model.record_v2.Biography biography = new org.orcid.jaxb.model.record_v2.Biography();
        biography.setContent("Biography for 0000-0000-0000-0003");
        biography.setVisibility(Visibility.PUBLIC);
        biography.setCreatedDate(createdDate());
        biography.setLastModifiedDate(lastModified());
        person.setBiography(biography);

        org.orcid.jaxb.model.record_v2.Keyword keyword = new org.orcid.jaxb.model.record_v2.Keyword();
        keyword.setPutCode(9L);
        keyword.setContent("Keyword PUBLIC");
        keyword.setVisibility(Visibility.PUBLIC);
        keyword.setSource(clientSource(CLIENT_1));
        keyword.setCreatedDate(createdDate());
        keyword.setLastModifiedDate(lastModified());
        org.orcid.jaxb.model.record_v2.Keywords keywords = new org.orcid.jaxb.model.record_v2.Keywords();
        keywords.setKeywords(new ArrayList<>(Arrays.asList(keyword)));
        keywords.setLastModifiedDate(lastModified());
        person.setKeywords(keywords);

        person.setEmails(emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC)));
        record.setPerson(person);

        record.setActivitiesSummary(activitiesSummary());
        return record;
    }

    private Email email(Long putCode, String address, Visibility visibility) {
        Email email = new Email();
        email.setPutCode(putCode);
        email.setEmail(address);
        email.setVisibility(visibility);
        email.setSource(clientSource(CLIENT_1));
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
