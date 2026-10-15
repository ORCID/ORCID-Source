package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the record endpoint of the member V3 API.
 *
 * <p>
 * The record endpoint returns the largest graph in the API, and the old versions
 * of these tests asserted on that graph after the real
 * {@code OrcidSecurityManager} had filtered it against a DBUnit fixture. With
 * the security manager mocked, {@code checkAndFilter(orcid, record)} filters
 * nothing, so the graph asserted on would be the one the test itself built --
 * circular. The visibility tables are proved in orcid-core by
 * {@code OrcidSecurityManager_FullRecordTest}; what these tests prove is the
 * delegator's contract: it asks {@code RecordManagerReadOnly}, hands the record
 * to the security manager, stamps every path, resolves source names through the
 * real {@code SourceUtils} and returns 200 with the graph the filter left.
 */
public class MemberV3ApiServiceDelegator_ReadRecordTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final String UNCLAIMED = "0000-0000-0000-0001";

    private Record fullRecord(Source source) {
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier(ORCID));
        record.setPerson(person(source));
        record.setActivitiesSummary(activities(source));
        return record;
    }

    private Person person(Source source) {
        Person person = new Person();

        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setVisibility(Visibility.PUBLIC);
        name.setLastModifiedDate(lastModified());
        person.setName(name);

        Biography biography = new Biography();
        biography.setContent("Biography for 0000-0000-0000-0003");
        biography.setVisibility(Visibility.PUBLIC);
        biography.setLastModifiedDate(lastModified());
        person.setBiography(biography);

        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<>(Arrays.asList(otherName(13L, Visibility.PUBLIC, source), otherName(14L, Visibility.LIMITED, source),
                otherName(15L, Visibility.PRIVATE, source))));
        person.setOtherNames(otherNames);

        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<>(
                Arrays.asList(keyword(9L, Visibility.PUBLIC, source), keyword(10L, Visibility.LIMITED, source), keyword(11L, Visibility.PRIVATE, source))));
        person.setKeywords(keywords);

        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>(
                Arrays.asList(address(9L, Visibility.PUBLIC, source), address(10L, Visibility.LIMITED, source), address(11L, Visibility.PRIVATE, source))));
        person.setAddresses(addresses);

        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<>(Arrays.asList(externalIdentifier(13L, Visibility.PUBLIC, source),
                externalIdentifier(14L, Visibility.LIMITED, source), externalIdentifier(15L, Visibility.PRIVATE, source))));
        person.setExternalIdentifiers(extIds);

        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<>(Arrays.asList(researcherUrl(13L, Visibility.PUBLIC, source),
                researcherUrl(14L, Visibility.LIMITED, source), researcherUrl(15L, Visibility.PRIVATE, source))));
        person.setResearcherUrls(researcherUrls);

        Emails emails = new Emails();
        emails.setEmails(new ArrayList<>(Arrays.asList(email("public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC, source),
                email("limited_0000-0000-0000-0003@test.orcid.org", Visibility.LIMITED, source))));
        person.setEmails(emails);

        return person;
    }

    private ActivitiesSummary activities(Source source) {
        ActivitiesSummary activities = emptyActivitiesSummary();
        Works works = new Works();
        WorkGroup group = new WorkGroup();
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(11L);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("PUBLIC"));
        summary.setTitle(title);
        summary.setType(WorkType.JOURNAL_ARTICLE);
        summary.setVisibility(Visibility.PUBLIC);
        summary.setSource(source);
        summary.setLastModifiedDate(lastModified());
        group.getWorkSummary().add(summary);
        works.getWorkGroup().add(group);
        activities.setWorks(works);
        return activities;
    }

    private OtherName otherName(long putCode, Visibility visibility, Source source) {
        OtherName element = new OtherName();
        element.setPutCode(putCode);
        element.setContent("Other Name " + visibility.value());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Keyword keyword(long putCode, Visibility visibility, Source source) {
        Keyword element = new Keyword();
        element.setPutCode(putCode);
        element.setContent("Keyword " + visibility.value());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Address address(long putCode, Visibility visibility, Source source) {
        Address element = new Address();
        element.setPutCode(putCode);
        element.setCountry(new Country(Iso3166Country.US));
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private PersonExternalIdentifier externalIdentifier(long putCode, Visibility visibility, Source source) {
        PersonExternalIdentifier element = new PersonExternalIdentifier();
        element.setPutCode(putCode);
        element.setType("Facebook");
        element.setValue("value-" + putCode);
        element.setUrl(new Url("http://www.facebook.com/" + putCode));
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private ResearcherUrl researcherUrl(long putCode, Visibility visibility, Source source) {
        ResearcherUrl element = new ResearcherUrl();
        element.setPutCode(putCode);
        element.setUrl(new Url("http://www.researcherurl.com?id=" + putCode));
        element.setUrlName("url-" + putCode);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Email email(String address, Visibility visibility, Source source) {
        Email element = new Email();
        element.setEmail(address);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setVerified(true);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    /**
     * Stands in for {@code OrcidSecurityManagerImpl}'s in-place filter over a
     * record: it drops everything on the person that is not public. The rule is
     * proved in orcid-core; what it lets this class assert is that the delegator
     * returns the graph the filter mutated.
     */
    private void keepOnlyPublicElements(String orcid) {
        doAnswer(invocation -> {
            Record record = invocation.getArgument(1);
            Person person = record.getPerson();
            person.getOtherNames().getOtherNames().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getKeywords().getKeywords().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getAddresses().getAddress().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getExternalIdentifiers().getExternalIdentifiers().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getResearcherUrls().getResearcherUrls().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getEmails().getEmails().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(orcid), any(Record.class));
    }

    @Test
    public void testViewRecordReadPublic() {
        when(recordManagerReadOnly.getRecord(ORCID, false)).thenReturn(fullRecord(clientSource(CLIENT_1)));

        Response r = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) r.getEntity();
        assertNotNull(record);
        assertEquals("/0000-0000-0000-0003", record.getPath());
        assertNotNull(record.getPerson());
        assertEquals("/0000-0000-0000-0003/person", record.getPerson().getPath());
        assertNotNull(record.getActivitiesSummary());
        assertEquals("/0000-0000-0000-0003/activities", record.getActivitiesSummary().getPath());
        assertEquals("/0000-0000-0000-0003/work/11", record.getActivitiesSummary().getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
    }

    @Test
    public void testViewRecordReadPublic_ClientNotSourceOfAnyEmail() {
        when(recordManagerReadOnly.getRecord(ORCID, false)).thenReturn(fullRecord(clientSource(CLIENT_2)));
        keepOnlyPublicElements(ORCID);

        Response r = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) r.getEntity();
        assertNotNull(record);
        assertNotNull(record.getPerson());
        // Only the public email survives, and it is the filtered list that comes
        // back rather than the one the delegator read.
        assertEquals(1, record.getPerson().getEmails().getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", record.getPerson().getEmails().getEmails().get(0).getEmail());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewRecordWrongToken() {
        when(recordManagerReadOnly.getRecord(ORCID, false)).thenReturn(fullRecord(clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID),
                any(Record.class));

        serviceDelegator.viewRecord(ORCID);
    }

    @Test
    public void testReadPublicScope_Record() {
        when(recordManagerReadOnly.getRecord(ORCID, false)).thenReturn(fullRecord(clientSource(CLIENT_1)));
        keepOnlyPublicElements(ORCID);

        Response r = serviceDelegator.viewRecord(ORCID);
        assertNotNull(r);
        assertEquals(Record.class.getName(), r.getEntity().getClass().getName());
        Record record = (Record) r.getEntity();
        assertNotNull(record.getPerson());
        Utils.verifyLastModified(record.getPerson().getLastModifiedDate());
        for (OtherName element : record.getPerson().getOtherNames().getOtherNames()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        for (Keyword element : record.getPerson().getKeywords().getKeywords()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        for (Address element : record.getPerson().getAddresses().getAddress()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        for (PersonExternalIdentifier element : record.getPerson().getExternalIdentifiers().getExternalIdentifiers()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        for (ResearcherUrl element : record.getPerson().getResearcherUrls().getResearcherUrls()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        assertEquals(1, record.getPerson().getOtherNames().getOtherNames().size());
    }

    @Test
    public void testViewRecord() {
        when(recordManagerReadOnly.getRecord(ORCID, false)).thenReturn(fullRecord(clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        assertEquals(ORCID, record.getOrcidIdentifier().getPath());
        assertEquals("/0000-0000-0000-0003", record.getPath());

        assertNotNull(record.getPerson());
        Utils.verifyLastModified(record.getPerson().getLastModifiedDate());
        assertEquals("Credit Name", record.getPerson().getName().getCreditName().getContent());
        assertEquals("Biography for 0000-0000-0000-0003", record.getPerson().getBiography().getContent());
        assertEquals("/0000-0000-0000-0003/biography", record.getPerson().getBiography().getPath());
        assertEquals(3, record.getPerson().getOtherNames().getOtherNames().size());
        assertEquals(3, record.getPerson().getKeywords().getKeywords().size());
        assertEquals(3, record.getPerson().getAddresses().getAddress().size());
        assertEquals(3, record.getPerson().getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(3, record.getPerson().getResearcherUrls().getResearcherUrls().size());
        assertEquals(2, record.getPerson().getEmails().getEmails().size());

        assertNotNull(record.getActivitiesSummary());
        Utils.verifyLastModified(record.getActivitiesSummary().getLastModifiedDate());
        assertEquals(1, record.getActivitiesSummary().getWorks().getWorkGroup().size());

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkAndFilter(ORCID, record);
        verify(recordManagerReadOnly).getRecord(ORCID, false);
    }

    @Test
    public void testReadPrivateEmails_OtherThingsJustPublic_Record() {
        when(recordManagerReadOnly.getRecord(ORCID, false)).thenReturn(fullRecord(clientSource(CLIENT_1)));
        doAnswer(invocation -> {
            Record record = invocation.getArgument(1);
            Person person = record.getPerson();
            person.getOtherNames().getOtherNames().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getKeywords().getKeywords().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getAddresses().getAddress().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getExternalIdentifiers().getExternalIdentifiers().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getResearcherUrls().getResearcherUrls().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Record.class));

        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        assertEquals(2, record.getPerson().getEmails().getEmails().size());
        assertEquals(1, record.getPerson().getOtherNames().getOtherNames().size());
        assertEquals(1, record.getPerson().getKeywords().getKeywords().size());
        assertEquals(1, record.getPerson().getAddresses().getAddress().size());
        assertEquals(1, record.getPerson().getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(1, record.getPerson().getResearcherUrls().getResearcherUrls().size());
    }

    /**
     * The one assertion in this class that exercises the real
     * {@code SourceUtils}: the client id on every element of the record is
     * resolved to a display name through {@code SourceNameCacheManager}.
     */
    @Test
    public void checkSourceOnEmail_RecordEndpointTest() {
        when(recordManagerReadOnly.getRecord(UNCLAIMED, false)).thenReturn(fullRecord(clientSource(CLIENT_1)));

        Response r = serviceDelegator.viewRecord(UNCLAIMED);
        Record record = (Record) r.getEntity();
        assertNotNull(record);
        assertTrue(!record.getPerson().getEmails().getEmails().isEmpty());
        for (Email e : record.getPerson().getEmails().getEmails()) {
            assertTrue(e.isVerified());
            assertEquals(CLIENT_1, e.getSource().retrieveSourcePath());
            assertEquals(CLIENT_1_NAME, e.getSource().getSourceName().getContent());
        }
        assertEquals(CLIENT_1_NAME,
                record.getActivitiesSummary().getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getSource().getSourceName().getContent());
    }
}
