package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Source;
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
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the person endpoint of the member V3 API.
 *
 * <p>
 * This class used to assert a filtered person graph produced by the real
 * {@code OrcidSecurityManager} against a DBUnit fixture. With the security
 * manager mocked, {@code checkAndFilter(orcid, person)} filters nothing, so
 * every "only public elements came back" assertion would hold without the filter
 * having run. Those tables are proved in orcid-core by
 * {@code OrcidSecurityManager_PersonTest} and {@code _generalTest}. What is
 * asserted here is what the delegator itself does: ask
 * {@code PersonDetailsManagerReadOnly}, hand the graph to the security manager,
 * stamp the paths, resolve source names through the real {@code SourceUtils},
 * and return it.
 */
public class MemberV3ApiServiceDelegator_ReadPersonTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final String UNCLAIMED = "0000-0000-0000-0001";

    private Person fullPerson(Source source) {
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
        otherNames.setOtherNames(new ArrayList<>(Arrays.asList(otherName(13L, "Other Name PUBLIC", Visibility.PUBLIC, source),
                otherName(14L, "Other Name LIMITED", Visibility.LIMITED, source), otherName(15L, "Other Name PRIVATE", Visibility.PRIVATE, source))));
        person.setOtherNames(otherNames);

        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<>(Arrays.asList(keyword(9L, "PUBLIC", Visibility.PUBLIC, source), keyword(10L, "LIMITED", Visibility.LIMITED, source),
                keyword(11L, "PRIVATE", Visibility.PRIVATE, source))));
        person.setKeywords(keywords);

        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>(Arrays.asList(address(9L, Visibility.PUBLIC, source), address(10L, Visibility.LIMITED, source),
                address(11L, Visibility.PRIVATE, source))));
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

    private OtherName otherName(long putCode, String content, Visibility visibility, Source source) {
        OtherName element = new OtherName();
        element.setPutCode(putCode);
        element.setContent(content);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Keyword keyword(long putCode, String content, Visibility visibility, Source source) {
        Keyword element = new Keyword();
        element.setPutCode(putCode);
        element.setContent(content);
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
     * person graph: it drops everything that is not public. The rule is proved in
     * orcid-core; what it lets this class assert is that the delegator returns
     * the graph the filter mutated, and stamps paths on what survives.
     */
    private void keepOnlyPublicElements() {
        doAnswer(invocation -> {
            Person person = invocation.getArgument(1);
            person.getOtherNames().getOtherNames().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getKeywords().getKeywords().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getAddresses().getAddress().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getExternalIdentifiers().getExternalIdentifiers().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getResearcherUrls().getResearcherUrls().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getEmails().getEmails().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Person.class));
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonWrongToken() {
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID, false)).thenReturn(fullPerson(clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID),
                any(Person.class));

        serviceDelegator.viewPerson(ORCID);
    }

    @Test
    public void testViewPersonReadPublic() {
        // The token's client is the source of every element, so the filter keeps
        // them all; the delegator must return the whole graph with paths set.
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID, false)).thenReturn(fullPerson(clientSource(CLIENT_1)));

        Response r = serviceDelegator.viewPerson(ORCID);
        Person person = (Person) r.getEntity();
        assertNotNull(person);
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        assertEquals("/0000-0000-0000-0003/other-names", person.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/other-names/13", person.getOtherNames().getOtherNames().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/keywords", person.getKeywords().getPath());
        assertEquals("/0000-0000-0000-0003/address", person.getAddresses().getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers", person.getExternalIdentifiers().getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls", person.getResearcherUrls().getPath());
        assertEquals("/0000-0000-0000-0003/email", person.getEmails().getPath());
        assertEquals("/0000-0000-0000-0003/biography", person.getBiography().getPath());
        assertEquals(3, person.getOtherNames().getOtherNames().size());
        verify(orcidSecurityManager).checkAndFilter(ORCID, person);
    }

    @Test
    public void testViewPersonReadPublic_ClientNotSourceOfAnyEmail() {
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID, false)).thenReturn(fullPerson(clientSource(CLIENT_2)));
        keepOnlyPublicElements();

        Response r = serviceDelegator.viewPerson(ORCID);
        Person person = (Person) r.getEntity();
        assertNotNull(person);
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        // Only the public email survives the filter, and it is the filtered list
        // that comes back, not the copy the delegator started from.
        assertEquals(1, person.getEmails().getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", person.getEmails().getEmails().get(0).getEmail());
        assertEquals(1, person.getOtherNames().getOtherNames().size());
        assertEquals(1, person.getKeywords().getKeywords().size());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(1, person.getResearcherUrls().getResearcherUrls().size());
    }

    @Test
    public void testReadPublicScope_Person() {
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID, false)).thenReturn(fullPerson(clientSource(CLIENT_1)));
        keepOnlyPublicElements();

        Response r = serviceDelegator.viewPerson(ORCID);
        assertNotNull(r);
        assertEquals(Person.class.getName(), r.getEntity().getClass().getName());
        Person person = (Person) r.getEntity();
        assertNotNull(person);
        Utils.verifyLastModified(person.getLastModifiedDate());

        List<Long> otherNamePutCodes = new ArrayList<>();
        for (OtherName element : person.getOtherNames().getOtherNames()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            otherNamePutCodes.add(element.getPutCode());
        }
        assertEquals(1, otherNamePutCodes.size());
        assertTrue(otherNamePutCodes.contains(13L));

        for (Keyword element : person.getKeywords().getKeywords()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        for (Address element : person.getAddresses().getAddress()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        for (PersonExternalIdentifier element : person.getExternalIdentifiers().getExternalIdentifiers()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
        for (ResearcherUrl element : person.getResearcherUrls().getResearcherUrls()) {
            assertEquals(Visibility.PUBLIC, element.getVisibility());
        }
    }

    @Test
    public void testViewPerson() {
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID, false)).thenReturn(fullPerson(clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        assertNotNull(person);
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        Utils.verifyLastModified(person.getLastModifiedDate());

        assertNotNull(person.getName());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());

        assertNotNull(person.getBiography());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());

        assertEquals(3, person.getOtherNames().getOtherNames().size());
        assertEquals(3, person.getKeywords().getKeywords().size());
        assertEquals(3, person.getAddresses().getAddress().size());
        assertEquals(3, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(3, person.getResearcherUrls().getResearcherUrls().size());
        assertEquals(2, person.getEmails().getEmails().size());
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkAndFilter(ORCID, person);
    }

    @Test
    public void testReadPrivateEmails_OtherThingsJustPublic_Person() {
        // The scope combination that lets a client read every email while seeing
        // only public everything else is decided by the security manager, so this
        // stands in for that decision and asserts the delegator returns exactly
        // what the filter left.
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID, false)).thenReturn(fullPerson(clientSource(CLIENT_1)));
        doAnswer(invocation -> {
            Person person = invocation.getArgument(1);
            person.getOtherNames().getOtherNames().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getKeywords().getKeywords().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getAddresses().getAddress().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getExternalIdentifiers().getExternalIdentifiers().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            person.getResearcherUrls().getResearcherUrls().removeIf(e -> !Visibility.PUBLIC.equals(e.getVisibility()));
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Person.class));

        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        assertNotNull(person);
        assertEquals(2, person.getEmails().getEmails().size());
        assertEquals(1, person.getOtherNames().getOtherNames().size());
        assertEquals(1, person.getKeywords().getKeywords().size());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(1, person.getResearcherUrls().getResearcherUrls().size());
    }

    /**
     * The one assertion in this class that exercises the real
     * {@code SourceUtils}: each element's client id is resolved to a display name
     * through {@code SourceNameCacheManager}.
     */
    @Test
    public void checkSourceOnEmail_PersonEndpointTest() {
        Person stored = fullPerson(clientSource(CLIENT_1));
        when(personDetailsManagerReadOnly.getPersonDetails(UNCLAIMED, false)).thenReturn(stored);

        Response r = serviceDelegator.viewPerson(UNCLAIMED);
        Person person = (Person) r.getEntity();
        assertNotNull(person);
        for (Email e : person.getEmails().getEmails()) {
            assertTrue(e.isVerified());
            assertEquals(CLIENT_1, e.getSource().retrieveSourcePath());
            assertEquals(CLIENT_1_NAME, e.getSource().getSourceName().getContent());
        }
        for (OtherName o : person.getOtherNames().getOtherNames()) {
            assertEquals(CLIENT_1_NAME, o.getSource().getSourceName().getContent());
        }
        if (person.getEmails().getEmails().isEmpty()) {
            fail("No emails to check the source of");
        }
    }
}
