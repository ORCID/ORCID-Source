package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.test.helper.Utils;

/**
 * The person endpoint of the member v2 delegator, on mocks.
 *
 * <p>
 * {@code viewPerson} fetches the composed person, hands the whole object to
 * {@code checkAndFilter(String, Person)}, then walks it setting a path on every
 * sub-element and resolving every source name. The walk is the delegator's own
 * behaviour and is what is asserted here, sub-element by sub-element.
 *
 * <p>
 * The filtering is not asserted. That overload of {@code checkAndFilter} strips
 * elements from the object it is given and returns nothing, so against a mock
 * every "only the public parts came back" assertion would hold trivially. Those
 * belong to {@code OrcidSecurityManager_PersonTest}.
 */
public class MemberV2ApiServiceDelegator_ReadPersonTest extends MemberV2ApiServiceDelegatorMockBase {

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonWrongToken() {
        Person person = person();
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID)).thenReturn(person);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, person);

        try {
            serviceDelegator.viewPerson(ORCID);
        } finally {
            assertNull("nothing must be decorated once the guard has refused", person.getPath());
        }
    }

    @Test
    public void testViewPersonReadPublic() {
        Person person = person();
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID)).thenReturn(person);

        Response r = serviceDelegator.viewPerson(ORCID);

        Person element = (Person) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/person", element.getPath());
        assertEquals("/0000-0000-0000-0003/address", element.getAddresses().getPath());
        assertEquals("/0000-0000-0000-0003/biography", element.getBiography().getPath());
        assertEquals("/0000-0000-0000-0003/email", element.getEmails().getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers", element.getExternalIdentifiers().getPath());
        assertEquals("/0000-0000-0000-0003/keywords", element.getKeywords().getPath());
        assertEquals("/0000-0000-0000-0003/other-names", element.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls", element.getResearcherUrls().getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, person);
    }

    @Test
    public void testReadPublicScope_Person() {
        Person person = person();
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID)).thenReturn(person);

        Response r = serviceDelegator.viewPerson(ORCID);

        assertNotNull(r);
        assertEquals(Person.class.getName(), r.getEntity().getClass().getName());
        Person returned = (Person) r.getEntity();
        assertEquals("/0000-0000-0000-0003/person", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        verify(orcidSecurityManager).checkAndFilter(ORCID, person);
    }

    @Test
    public void testViewPerson() {
        Person person = person();
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID)).thenReturn(person);

        Response response = serviceDelegator.viewPerson(ORCID);

        assertNotNull(response);
        Person returned = (Person) response.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003/person", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());

        // every sub-element must carry its own path, not just the container
        assertEquals("/0000-0000-0000-0003/address/9", returned.getAddresses().getAddress().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", returned.getExternalIdentifiers().getExternalIdentifiers().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/keywords/9", returned.getKeywords().getKeywords().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/other-names/13", returned.getOtherNames().getOtherNames().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", returned.getResearcherUrls().getResearcherUrls().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/biography", returned.getBiography().getPath());

        // and every source name must have been resolved through SourceUtils
        assertEquals(CLIENT_1_NAME, returned.getAddresses().getAddress().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, returned.getKeywords().getKeywords().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, returned.getOtherNames().getOtherNames().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, returned.getResearcherUrls().getResearcherUrls().get(0).getSource().getSourceName().getContent());
        assertEquals(CLIENT_1_NAME, returned.getExternalIdentifiers().getExternalIdentifiers().get(0).getSource().getSourceName().getContent());

        assertTrue(returned.getEmails().getEmails().size() > 0);
        verify(orcidSecurityManager).checkAndFilter(ORCID, person);
    }

    @Test
    public void testReadPrivateEmails_OtherThingsJustPublic_Person() {
        // With an /email/read-private token the security manager keeps every
        // email and drops the non-public rest. It does that by editing the Person
        // in place, so what is modelled here is the object it would have left
        // behind; the filtering itself is OrcidSecurityManager_PersonTest's.
        Person filtered = new Person();
        Emails emails = emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC),
                email(2L, "limited_0000-0000-0000-0003@test.orcid.org", Visibility.LIMITED),
                email(3L, "private_0000-0000-0000-0003@test.orcid.org", Visibility.PRIVATE));
        filtered.setEmails(emails);
        filtered.setAddresses(new Addresses());
        filtered.getAddresses().setAddress(new ArrayList<>());
        when(personDetailsManagerReadOnly.getPersonDetails(ORCID)).thenReturn(filtered);

        Response r = serviceDelegator.viewPerson(ORCID);

        Person returned = (Person) r.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003/person", returned.getPath());
        assertEquals("/0000-0000-0000-0003/email", returned.getEmails().getPath());
        assertEquals(3, returned.getEmails().getEmails().size());
        assertTrue(returned.getAddresses().getAddress().isEmpty());
        assertNull(returned.getKeywords());
        verify(orcidSecurityManager).checkAndFilter(ORCID, filtered);
    }

    @Test
    public void checkSourceOnEmail_PersonEndpointTest() {
        String orcid = "0000-0000-0000-0001";
        Person person = new Person();
        person.setEmails(emails(email(1L, "limited_verified_0000-0000-0000-0001@test.orcid.org", Visibility.LIMITED),
                email(2L, "verified_non_professional@nonprofessional.org", Visibility.LIMITED)));
        person.getEmails().getEmails().forEach(e -> e.setVerified(Boolean.TRUE));
        when(personDetailsManagerReadOnly.getPersonDetails(orcid)).thenReturn(person);

        Response r = serviceDelegator.viewPerson(orcid);

        Person returned = (Person) r.getEntity();
        assertEquals(2, returned.getEmails().getEmails().size());
        for (Email e : returned.getEmails().getEmails()) {
            assertTrue(e.isVerified());
            assertEquals("APP-5555555555555555", e.getSource().retrieveSourcePath());
            assertEquals("Source Client 1", e.getSource().getSourceName().getContent());
        }
    }

    // ------------------------------------------------------------- fixtures

    private Person person() {
        Person person = new Person();

        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setVisibility(Visibility.PUBLIC);
        name.setCreatedDate(createdDate());
        name.setLastModifiedDate(lastModified());
        person.setName(name);

        Biography biography = new Biography();
        biography.setContent("Biography for 0000-0000-0000-0003");
        biography.setVisibility(Visibility.PUBLIC);
        biography.setCreatedDate(createdDate());
        biography.setLastModifiedDate(lastModified());
        person.setBiography(biography);

        Address address = new Address();
        address.setPutCode(9L);
        address.setCountry(new Country(Iso3166Country.US));
        address.setVisibility(Visibility.PUBLIC);
        address.setSource(clientSource(CLIENT_1));
        address.setCreatedDate(createdDate());
        address.setLastModifiedDate(lastModified());
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>(Arrays.asList(address)));
        addresses.setLastModifiedDate(lastModified());
        person.setAddresses(addresses);

        Keyword keyword = new Keyword();
        keyword.setPutCode(9L);
        keyword.setContent("Keyword PUBLIC");
        keyword.setVisibility(Visibility.PUBLIC);
        keyword.setSource(clientSource(CLIENT_1));
        keyword.setCreatedDate(createdDate());
        keyword.setLastModifiedDate(lastModified());
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<>(Arrays.asList(keyword)));
        keywords.setLastModifiedDate(lastModified());
        person.setKeywords(keywords);

        OtherName otherName = new OtherName();
        otherName.setPutCode(13L);
        otherName.setContent("Other Name PUBLIC");
        otherName.setVisibility(Visibility.PUBLIC);
        otherName.setSource(clientSource(CLIENT_1));
        otherName.setCreatedDate(createdDate());
        otherName.setLastModifiedDate(lastModified());
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<>(Arrays.asList(otherName)));
        otherNames.setLastModifiedDate(lastModified());
        person.setOtherNames(otherNames);

        ResearcherUrl researcherUrl = new ResearcherUrl();
        researcherUrl.setPutCode(13L);
        researcherUrl.setUrl(new Url("http://www.researcherurl.com/13"));
        researcherUrl.setUrlName("Researcher url PUBLIC");
        researcherUrl.setVisibility(Visibility.PUBLIC);
        researcherUrl.setSource(clientSource(CLIENT_1));
        researcherUrl.setCreatedDate(createdDate());
        researcherUrl.setLastModifiedDate(lastModified());
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<>(Arrays.asList(researcherUrl)));
        researcherUrls.setLastModifiedDate(lastModified());
        person.setResearcherUrls(researcherUrls);

        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setPutCode(13L);
        extId.setType("type-13");
        extId.setValue("value-13");
        extId.setUrl(new Url("http://extId.com/13"));
        extId.setRelationship(Relationship.SELF);
        extId.setVisibility(Visibility.PUBLIC);
        extId.setSource(clientSource(CLIENT_1));
        extId.setCreatedDate(createdDate());
        extId.setLastModifiedDate(lastModified());
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<>(Arrays.asList(extId)));
        extIds.setLastModifiedDate(lastModified());
        person.setExternalIdentifiers(extIds);

        person.setEmails(emails(email(1L, "public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC)));

        return person;
    }

    private Email email(Long putCode, String address, Visibility visibility) {
        return email(putCode, address, visibility, clientSource(CLIENT_1));
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
