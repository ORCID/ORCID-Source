package org.orcid.record.v3;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Locale;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.CreditName;
import org.orcid.jaxb.model.v3.rc1.record.Deprecated;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.History;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Keywords;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.Name;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc1.record.PersonalDetails;
import org.orcid.jaxb.model.v3.rc1.record.Preferences;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Record;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Educations;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Employments;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Fundings;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Memberships;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Services;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.xml.sax.SAXException;

public class ValidateV3_rc1SamplesTest {
    @Test
    public void testUnmarshallPersonalDetails() throws SAXException, URISyntaxException {
        PersonalDetails personalDetails = (PersonalDetails) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/personal-details-3.0_rc1.xml",
                PersonalDetails.class, "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
        assertNotNull(personalDetails);
        // Check bio
        assertNotNull(personalDetails.getBiography());
        assertEquals("Biography", personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        // Check names
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getCreditName());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertNotNull(personalDetails.getName().getGivenNames());
        assertEquals("Give Names", personalDetails.getName().getGivenNames().getContent());
        assertNotNull(personalDetails.getName().getFamilyName());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
        // Check other names
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getOtherNames());
        assertEquals(2, personalDetails.getOtherNames().getOtherNames().size());
        assertEquals("Other Name #1", personalDetails.getOtherNames().getOtherNames().get(0).getContent());
        assertEquals("Other Name #2", personalDetails.getOtherNames().getOtherNames().get(1).getContent());
        assertEquals(Visibility.PUBLIC, personalDetails.getOtherNames().getOtherNames().get(0).getVisibility());
        assertEquals(Visibility.LIMITED, personalDetails.getOtherNames().getOtherNames().get(1).getVisibility());
    }

    @Test
    public void testMarshallPersonalDetails() throws JAXBException, SAXException, URISyntaxException {
        PersonalDetails object = (PersonalDetails) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/personal-details-3.0_rc1.xml", PersonalDetails.class);
        marshall(object, "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallResearcherUrl() throws SAXException, URISyntaxException {
        ResearcherUrls rUrls = (ResearcherUrls) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/researcher-urls-3.0_rc1.xml", ResearcherUrls.class,
                "/record_3.0_rc1/researcher-url-3.0_rc1.xsd");
        assertNotNull(rUrls);
        assertNotNull(rUrls.getResearcherUrls());
        assertEquals(1, rUrls.getResearcherUrls().size());
        assertNotNull(rUrls.getResearcherUrls().get(0).getCreatedDate());
        assertNotNull(rUrls.getResearcherUrls().get(0).getLastModifiedDate());
        assertEquals("Site # 1", rUrls.getResearcherUrls().get(0).getUrlName());
        assertEquals("http://site1.com/", rUrls.getResearcherUrls().get(0).getUrl().getValue());
        assertEquals(Long.valueOf(1248), rUrls.getResearcherUrls().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC.value(), rUrls.getResearcherUrls().get(0).getVisibility().value());
        assertNotNull(rUrls.getResearcherUrls().get(0).getSource());
        assertEquals("https://www.orcid.org/8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retriveSourceUri());
        assertEquals("8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retrieveSourcePath());

        ResearcherUrl rUrl = (ResearcherUrl) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/researcher-url-3.0_rc1.xml", ResearcherUrl.class);
        assertNotNull(rUrl);
        assertEquals("Site # 1", rUrl.getUrlName());
        assertNotNull(rUrl.getUrl());
        assertEquals("http://site1.com/", rUrl.getUrl().getValue());
        assertNotNull(rUrl.getCreatedDate());
        assertNotNull(rUrl.getLastModifiedDate());
        assertNotNull(rUrl.getSource());
        assertEquals("8888-8888-8888-8880", rUrl.getSource().retrieveSourcePath());
    }

    @Test
    public void testMarshallResearcherUrl() throws JAXBException, SAXException, URISyntaxException {
        ResearcherUrls object = (ResearcherUrls) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/researcher-urls-3.0_rc1.xml", ResearcherUrls.class);
        marshall(object, "/record_3.0_rc1/researcher-url-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallAddress() throws SAXException, URISyntaxException {
        Addresses addresses = (Addresses) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/addresses-3.0_rc1.xml", Addresses.class,
                "/record_3.0_rc1/address-3.0_rc1.xsd");
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(2, addresses.getAddress().size());
        for (Address address : addresses.getAddress()) {
            assertNotNull(address.getPutCode());
            assertNotNull(address.getCreatedDate());
            assertNotNull(address.getLastModifiedDate());
            assertNotNull(address.getCountry());
            if (address.getPutCode().equals(new Long(1))) {
                assertEquals(Iso3166Country.US, address.getCountry().getValue());
                assertEquals(Visibility.PUBLIC, address.getVisibility());
            } else {
                assertEquals(Iso3166Country.CR, address.getCountry().getValue());
                assertEquals(Visibility.LIMITED, address.getVisibility());
            }
        }

        Address address = (Address) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/address-3.0_rc1.xml", Address.class);
        assertNotNull(address);
        assertNotNull(address.getPutCode());
        assertNotNull(address.getCreatedDate());
        assertNotNull(address.getLastModifiedDate());
        assertNotNull(address.getCountry());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }

    @Test
    public void testMarshallAddress() throws JAXBException, SAXException, URISyntaxException {
        Addresses object = (Addresses) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/addresses-3.0_rc1.xml", Addresses.class);
        marshall(object, "/record_3.0_rc1/address-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallBiography() throws SAXException, URISyntaxException {
        Biography bio = (Biography) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/biography-3.0_rc1.xml", Biography.class,
                "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
        assertNotNull(bio);
        assertEquals("biography V3.0_rc1", bio.getContent());
        assertEquals(Visibility.PUBLIC.value(), bio.getVisibility().value());
    }

    @Test
    public void testMarshallBiography() throws JAXBException, SAXException, URISyntaxException {
        Biography object = (Biography) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/biography-3.0_rc1.xml", Biography.class);
        marshall(object, "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallCreditName() throws SAXException, URISyntaxException {
        CreditName creditName = (CreditName) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/credit-name-3.0_rc1.xml", CreditName.class,
                "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
        assertNotNull(creditName);
        assertEquals("credit-name", creditName.getContent());
    }

    @Test
    public void testMarshallCreditName() throws JAXBException, SAXException, URISyntaxException {
        CreditName object = (CreditName) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/credit-name-3.0_rc1.xml", CreditName.class);
        marshall(object, "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallExternalIdentifiers() throws SAXException, URISyntaxException {
        PersonExternalIdentifiers externalIdentifiers = (PersonExternalIdentifiers) unmarshallFromPath(
                "/record_3.0_rc1/samples/read_samples/external-identifiers-3.0_rc1.xml", PersonExternalIdentifiers.class,
                "/record_3.0_rc1/person-external-identifier-3.0_rc1.xsd");
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifiers());
        assertEquals(2, externalIdentifiers.getExternalIdentifiers().size());
        for (PersonExternalIdentifier extId : externalIdentifiers.getExternalIdentifiers()) {
            assertThat(extId.getPutCode(), anyOf(is(1L), is(2L)));
            assertThat(extId.getType(), anyOf(is("common-name-1"), is("common-name-2")));
            assertThat(extId.getValue(), anyOf(is("id-reference-1"), is("id-reference-2")));
            assertNotNull(extId.getUrl());
            assertThat(extId.getUrl().getValue(), anyOf(is("http://url/1"), is("http://url/2")));
            assertNotNull(extId.getCreatedDate());
            assertNotNull(extId.getLastModifiedDate());
            assertNotNull(extId.getSource());
            assertEquals("8888-8888-8888-8880", extId.getSource().retrieveSourcePath());
        }

        PersonExternalIdentifier extId = (PersonExternalIdentifier) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/external-identifier-3.0_rc1.xml",
                PersonExternalIdentifier.class);
        assertNotNull(extId);
        assertEquals("A-0003", extId.getType());
        assertEquals(Long.valueOf(1), extId.getPutCode());
        assertEquals("A-0003", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://ext-id/A-0003", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getSource());
        assertEquals("8888-8888-8888-8880", extId.getSource().retrieveSourcePath());
    }

    @Test
    public void testMarshallExternalIdentifiers() throws JAXBException, SAXException, URISyntaxException {
        PersonExternalIdentifiers object = (PersonExternalIdentifiers) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/external-identifiers-3.0_rc1.xml",
                PersonExternalIdentifiers.class);
        marshall(object, "/record_3.0_rc1/person-external-identifier-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallKeyword() throws SAXException, URISyntaxException {
        Keywords keywords = (Keywords) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/keywords-3.0_rc1.xml", Keywords.class,
                "/record_3.0_rc1/keyword-3.0_rc1.xsd");
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(2, keywords.getKeywords().size());

        for (Keyword keyword : keywords.getKeywords()) {
            assertThat(keyword.getContent(), anyOf(is("keyword1"), is("keyword2")));
            assertThat(keyword.getPutCode(), anyOf(is(Long.valueOf(1)), is(Long.valueOf(2))));
            assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
            assertNotNull(keyword.getCreatedDate());
            assertNotNull(keyword.getLastModifiedDate());
            assertNotNull(keyword.getSource());
            assertEquals("8888-8888-8888-8880", keyword.getSource().retrieveSourcePath());
        }

        Keyword keyword = (Keyword) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/keyword-3.0_rc1.xml", Keyword.class);
        assertNotNull(keyword);
        assertEquals("keyword1", keyword.getContent());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getSource());
        assertEquals("8888-8888-8888-8880", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testMarshallKeyword() throws JAXBException, SAXException, URISyntaxException {
        Keywords object = (Keywords) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/keywords-3.0_rc1.xml", Keywords.class);
        marshall(object, "/record_3.0_rc1/keyword-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallName() throws SAXException, URISyntaxException {
        Name name = (Name) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/name-3.0_rc1.xml", Name.class, "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
        assertNotNull(name);
        assertNotNull(name.getCreditName());
        assertEquals("credit-name", name.getCreditName().getContent());
        assertNotNull(name.getFamilyName());
        assertEquals("family-name", name.getFamilyName().getContent());
        assertNotNull(name.getGivenNames());
        assertEquals("given-names", name.getGivenNames().getContent());
        assertNotNull(name.getVisibility());
        assertEquals(Visibility.PUBLIC, name.getVisibility());
    }

    @Test
    public void testMarshallName() throws JAXBException, SAXException, URISyntaxException {
        Name object = (Name) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/name-3.0_rc1.xml", Name.class);
        marshall(object, "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallOtherNames() throws SAXException, URISyntaxException {
        OtherNames otherNames = (OtherNames) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/other-names-3.0_rc1.xml", OtherNames.class,
                "/record_3.0_rc1/personal-details-3.0_rc1.xsd");
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(2, otherNames.getOtherNames().size());

        for (OtherName otherName : otherNames.getOtherNames()) {
            assertThat(otherName.getContent(), anyOf(is("Other Name #1"), is("Other Name #2")));
            assertThat(otherName.getPutCode(), anyOf(is(1L), is(2L)));
            assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
            assertNotNull(otherName.getCreatedDate());
            assertNotNull(otherName.getLastModifiedDate());
            assertNotNull(otherName.getSource());
            assertEquals("8888-8888-8888-8880", otherName.getSource().retrieveSourcePath());
        }

        OtherName otherName = (OtherName) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/other-name-3.0_rc1.xml", OtherName.class);
        assertNotNull(otherName);
        assertEquals("Other Name #1", otherName.getContent());
        assertEquals(Long.valueOf(1), otherName.getPutCode());
        assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
        assertNotNull(otherName.getCreatedDate());
        assertNotNull(otherName.getLastModifiedDate());
        assertNotNull(otherName.getSource());
        assertEquals("8888-8888-8888-8880", otherName.getSource().retrieveSourcePath());
    }

    @Test
    public void testMarshallOtherNames() throws JAXBException, SAXException, URISyntaxException {
        OtherNames object = (OtherNames) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/other-names-3.0_rc1.xml", OtherNames.class);
        marshall(object, "/record_3.0_rc1/other-name-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallEmails() throws SAXException, URISyntaxException {
        Emails emails = (Emails) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/emails-3.0_rc1.xml", Emails.class, "/record_3.0_rc1/email-3.0_rc1.xsd");
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(2, emails.getEmails().size());

        for (Email email : emails.getEmails()) {
            assertNotNull(email.getPutCode());
            assertNotNull(email.getCreatedDate());
            assertNotNull(email.getLastModifiedDate());
            if (email.getPutCode().equals(Long.valueOf(1))) {
                assertEquals(Visibility.PUBLIC, email.getVisibility());
                assertEquals("user1@email.com", email.getEmail());
            } else {
                assertEquals(Visibility.PUBLIC, email.getVisibility());
                assertEquals("user2@email.com", email.getEmail());
            }
        }

        Email email = (Email) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/email-3.0_rc1.xml", Email.class);
        assertNotNull(email);
        assertNotNull(email.getPutCode());
        assertNotNull(email.getCreatedDate());
        assertNotNull(email.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, email.getVisibility());
        assertEquals("user1@email.com", email.getEmail());
    }

    @Test
    public void testMarshallEmails() throws JAXBException, SAXException, URISyntaxException {
        Emails object = (Emails) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/emails-3.0_rc1.xml", Emails.class);
        marshall(object, "/record_3.0_rc1/email-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallPerson() throws SAXException, URISyntaxException {
        Person person = (Person) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/person-3.0_rc1.xml", Person.class, "/record_3.0_rc1/person-3.0_rc1.xsd");
        assertNotNull(person);
        assertNotNull(person.getName());
        assertEquals("give-names", person.getName().getGivenNames().getContent());
        assertEquals("family-name", person.getName().getFamilyName().getContent());
        assertEquals("credit-name", person.getName().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(1, person.getOtherNames().getOtherNames().size());
        OtherName otherName = person.getOtherNames().getOtherNames().get(0);
        assertEquals("other-name-1", otherName.getContent());
        assertNotNull(otherName.getCreatedDate());
        assertNotNull(otherName.getCreatedDate().getValue());
        assertEquals(2001, otherName.getCreatedDate().getValue().getYear());
        assertEquals(12, otherName.getCreatedDate().getValue().getMonth());
        assertEquals(31, otherName.getCreatedDate().getValue().getDay());
        assertNotNull(otherName.getLastModifiedDate().getValue());
        assertEquals(2001, otherName.getLastModifiedDate().getValue().getYear());
        assertEquals(12, otherName.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, otherName.getLastModifiedDate().getValue().getDay());
        assertNotNull(otherName.getSource());
        assertEquals("8888-8888-8888-8880", otherName.getSource().retrieveSourcePath());
        assertNotNull(person.getBiography());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());
        assertEquals("biography", person.getBiography().getContent());
        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(1, person.getResearcherUrls().getResearcherUrls().size());
        ResearcherUrl rUrl = person.getResearcherUrls().getResearcherUrls().get(0);
        assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
        assertEquals(Long.valueOf(1248), rUrl.getPutCode());
        assertEquals("url-name-1", rUrl.getUrlName());
        assertNotNull(rUrl.getUrl());
        assertEquals("http://url.com/", rUrl.getUrl().getValue());
        assertNotNull(rUrl.getCreatedDate());
        assertEquals(2001, rUrl.getCreatedDate().getValue().getYear());
        assertEquals(12, rUrl.getCreatedDate().getValue().getMonth());
        assertEquals(31, rUrl.getCreatedDate().getValue().getDay());
        assertNotNull(rUrl.getLastModifiedDate());
        assertEquals(2001, rUrl.getLastModifiedDate().getValue().getYear());
        assertEquals(12, rUrl.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, rUrl.getLastModifiedDate().getValue().getDay());
        assertNotNull(rUrl.getSource());
        assertEquals("8888-8888-8888-8880", rUrl.getSource().retrieveSourcePath());
        assertNotNull(person.getEmails());
        assertNotNull(person.getEmails().getEmails());
        assertEquals(1, person.getEmails().getEmails().size());
        Email email = person.getEmails().getEmails().get(0);
        assertEquals(Visibility.PUBLIC, email.getVisibility());
        assertEquals("user1@email.com", email.getEmail());
        assertNotNull(email.getCreatedDate());
        assertNotNull(email.getCreatedDate().getValue());
        assertEquals(2001, email.getCreatedDate().getValue().getYear());
        assertEquals(12, email.getCreatedDate().getValue().getMonth());
        assertEquals(31, email.getCreatedDate().getValue().getDay());
        assertNotNull(email.getLastModifiedDate());
        assertNotNull(email.getLastModifiedDate().getValue());
        assertEquals(2001, email.getLastModifiedDate().getValue().getYear());
        assertEquals(12, email.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, email.getLastModifiedDate().getValue().getDay());
        assertNotNull(email.getSource());
        assertEquals("8888-8888-8888-8880", email.retrieveSourcePath());
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        Address address = person.getAddresses().getAddress().get(0);
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertEquals(Long.valueOf(1), address.getPutCode());
        assertNotNull(address.getCountry());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertNotNull(address.getCreatedDate());
        assertNotNull(address.getCreatedDate().getValue());
        assertEquals(2001, address.getCreatedDate().getValue().getYear());
        assertEquals(12, address.getCreatedDate().getValue().getMonth());
        assertEquals(31, address.getCreatedDate().getValue().getDay());
        assertNotNull(address.getLastModifiedDate());
        assertNotNull(address.getLastModifiedDate().getValue());
        assertEquals(2001, address.getLastModifiedDate().getValue().getYear());
        assertEquals(12, address.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, address.getLastModifiedDate().getValue().getDay());
        assertNotNull(address.getSource());
        assertEquals("8888-8888-8888-8880", address.getSource().retrieveSourcePath());
        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(1, person.getKeywords().getKeywords().size());
        Keyword keyword = person.getKeywords().getKeywords().get(0);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertEquals("keyword1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertEquals(2001, keyword.getCreatedDate().getValue().getYear());
        assertEquals(12, keyword.getCreatedDate().getValue().getMonth());
        assertEquals(31, keyword.getCreatedDate().getValue().getDay());
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(2001, keyword.getLastModifiedDate().getValue().getYear());
        assertEquals(12, keyword.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, keyword.getLastModifiedDate().getValue().getDay());
        assertNotNull(keyword.getSource());
        assertEquals("8888-8888-8888-8880", keyword.getSource().retrieveSourcePath());
        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        PersonExternalIdentifier extId = person.getExternalIdentifiers().getExternalIdentifiers().get(0);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
        assertEquals(Long.valueOf(1), extId.getPutCode());
        assertEquals("type-1", extId.getType());
        assertEquals("value-1", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://url.com/1", extId.getUrl().getValue());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getCreatedDate().getValue());
        assertEquals(2001, extId.getCreatedDate().getValue().getYear());
        assertEquals(12, extId.getCreatedDate().getValue().getMonth());
        assertEquals(31, extId.getCreatedDate().getValue().getDay());
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getLastModifiedDate().getValue());
        assertEquals(2001, extId.getLastModifiedDate().getValue().getYear());
        assertEquals(12, extId.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, extId.getLastModifiedDate().getValue().getDay());
        assertNotNull(extId.getSource());
        assertEquals("8888-8888-8888-8880", extId.getSource().retrieveSourcePath());
    }

    @Test
    public void testMarshallPerson() throws JAXBException, SAXException, URISyntaxException {
        Person object = (Person) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/person-3.0_rc1.xml", Person.class);
        marshall(object, "/record_3.0_rc1/person-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallPreferences() throws SAXException, URISyntaxException {
        Preferences preferences = (Preferences) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/preferences-3.0_rc1.xml", Preferences.class,
                "/record_3.0_rc1/preferences-3.0_rc1.xsd");
        assertNotNull(preferences);
        assertNotNull(preferences.getLocale());
        assertEquals(Locale.EN, preferences.getLocale());
    }

    @Test
    public void testMarshallPreferences() throws JAXBException, SAXException, URISyntaxException {
        Preferences object = (Preferences) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/preferences-3.0_rc1.xml", Preferences.class);
        marshall(object, "/record_3.0_rc1/preferences-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallHistory() throws SAXException, URISyntaxException {
        History history = (History) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/history-3.0_rc1.xml", History.class,
                "/record_3.0_rc1/history-3.0_rc1.xsd");
        assertNotNull(history);
        assertNotNull(history.getSource());
        assertEquals("https://orcid.org/8888-8888-8888-8880", history.getSource().retriveSourceUri());
        assertNotNull(history.getCreationMethod());
        assertEquals(CreationMethod.API, history.getCreationMethod());
        assertNotNull(history.getClaimed());
        assertTrue(history.getClaimed());
        assertNotNull(history.isVerifiedEmail());
        assertTrue(history.isVerifiedEmail());
        assertNotNull(history.isVerifiedPrimaryEmail());
        assertTrue(history.isVerifiedPrimaryEmail());
        assertNotNull(history.getCompletionDate());
        assertNotNull(history.getCompletionDate().getValue());
        assertEquals(2001, history.getCompletionDate().getValue().getYear());
        assertEquals(12, history.getCompletionDate().getValue().getMonth());
        assertEquals(31, history.getCompletionDate().getValue().getDay());
        assertNotNull(history.getDeactivationDate());
        assertNotNull(history.getDeactivationDate().getValue());
        assertEquals(2001, history.getDeactivationDate().getValue().getYear());
        assertEquals(12, history.getDeactivationDate().getValue().getMonth());
        assertEquals(31, history.getDeactivationDate().getValue().getDay());
        assertNotNull(history.getLastModifiedDate());
        assertNotNull(history.getLastModifiedDate().getValue());
        assertEquals(2001, history.getLastModifiedDate().getValue().getYear());
        assertEquals(12, history.getLastModifiedDate().getValue().getMonth());
        assertEquals(31, history.getLastModifiedDate().getValue().getDay());
        assertNotNull(history.getSubmissionDate());
        assertNotNull(history.getSubmissionDate().getValue());
        assertEquals(2001, history.getSubmissionDate().getValue().getYear());
        assertEquals(12, history.getSubmissionDate().getValue().getMonth());
        assertEquals(31, history.getSubmissionDate().getValue().getDay());
    }

    @Test
    public void testMarshallHistory() throws JAXBException, SAXException, URISyntaxException {
        History object = (History) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/history-3.0_rc1.xml", History.class);
        marshall(object, "/record_3.0_rc1/history-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallActivities() throws SAXException, URISyntaxException {
        ActivitiesSummary activities = (ActivitiesSummary) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/activities-3.0_rc1.xml", ActivitiesSummary.class,
                "/record_3.0_rc1/activities-3.0_rc1.xsd");
        assertNotNull(activities);
        assertNotNull(activities.getDistinctions());
        assertNotNull(activities.getDistinctions());
        assertNotNull(activities.getDistinctions().getLastModifiedDate());
        assertNotNull(activities.getDistinctions().getSummaries());
        assertEquals(1, activities.getDistinctions().getSummaries().size());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getCreatedDate());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getDepartmentName());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getEndDate());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getEndDate().getDay());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getEndDate().getMonth());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getEndDate().getYear());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getOrganization());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getOrganization().getAddress());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getOrganization().getName());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getPutCode());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getRoleTitle());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getSource());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getStartDate());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getStartDate().getDay());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getStartDate().getMonth());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getStartDate().getYear());
        assertNotNull(activities.getDistinctions().getSummaries().get(0).getVisibility());

        assertNotNull(activities.getEducations());
        assertNotNull(activities.getEducations());
        assertNotNull(activities.getEducations().getLastModifiedDate());
        assertNotNull(activities.getEducations().getSummaries());
        assertEquals(1, activities.getEducations().getSummaries().size());
        assertNotNull(activities.getEducations().getSummaries().get(0).getCreatedDate());
        assertNotNull(activities.getEducations().getSummaries().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getEducations().getSummaries().get(0).getDepartmentName());
        assertNotNull(activities.getEducations().getSummaries().get(0).getEndDate());
        assertNotNull(activities.getEducations().getSummaries().get(0).getEndDate().getDay());
        assertNotNull(activities.getEducations().getSummaries().get(0).getEndDate().getMonth());
        assertNotNull(activities.getEducations().getSummaries().get(0).getEndDate().getYear());
        assertNotNull(activities.getEducations().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(activities.getEducations().getSummaries().get(0).getOrganization());
        assertNotNull(activities.getEducations().getSummaries().get(0).getOrganization().getAddress());
        assertNotNull(activities.getEducations().getSummaries().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getEducations().getSummaries().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getEducations().getSummaries().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getEducations().getSummaries().get(0).getOrganization().getName());
        assertNotNull(activities.getEducations().getSummaries().get(0).getPutCode());
        assertNotNull(activities.getEducations().getSummaries().get(0).getRoleTitle());
        assertNotNull(activities.getEducations().getSummaries().get(0).getSource());
        assertNotNull(activities.getEducations().getSummaries().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getEducations().getSummaries().get(0).getStartDate());
        assertNotNull(activities.getEducations().getSummaries().get(0).getStartDate().getDay());
        assertNotNull(activities.getEducations().getSummaries().get(0).getStartDate().getMonth());
        assertNotNull(activities.getEducations().getSummaries().get(0).getStartDate().getYear());
        assertNotNull(activities.getEducations().getSummaries().get(0).getVisibility());

        assertNotNull(activities.getEmployments());
        assertNotNull(activities.getEmployments().getLastModifiedDate());
        assertNotNull(activities.getEmployments().getLastModifiedDate().getValue());
        assertNotNull(activities.getEmployments().getSummaries());
        assertEquals(1, activities.getEmployments().getSummaries().size());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getCreatedDate());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getDepartmentName());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getEndDate());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getEndDate().getYear());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getEndDate().getMonth());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getEndDate().getDay());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getOrganization());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getOrganization().getAddress());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getOrganization().getName());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getPutCode());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getRoleTitle());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getSource());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getStartDate());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getStartDate().getDay());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getStartDate().getMonth());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getStartDate().getYear());
        assertNotNull(activities.getEmployments().getSummaries().get(0).getVisibility());

        assertNotNull(activities.getInvitedPositions());
        assertNotNull(activities.getInvitedPositions());
        assertNotNull(activities.getInvitedPositions().getLastModifiedDate());
        assertNotNull(activities.getInvitedPositions().getSummaries());
        assertEquals(1, activities.getInvitedPositions().getSummaries().size());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getCreatedDate());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getDepartmentName());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getEndDate());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getEndDate().getDay());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getEndDate().getMonth());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getEndDate().getYear());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getOrganization());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getOrganization().getAddress());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getOrganization().getName());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getPutCode());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getRoleTitle());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getSource());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getStartDate());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getStartDate().getDay());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getStartDate().getMonth());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getStartDate().getYear());
        assertNotNull(activities.getInvitedPositions().getSummaries().get(0).getVisibility());

        assertNotNull(activities.getFundings());
        assertNotNull(activities.getFundings().getLastModifiedDate());
        assertNotNull(activities.getFundings().getFundingGroup());
        assertEquals(1, activities.getFundings().getFundingGroup().size());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getIdentifiers());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, activities.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getCreatedDate());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getDisplayIndex());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getEndDate());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getEndDate().getDay());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getEndDate().getMonth());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getEndDate().getYear());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().size());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(
                activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getSource());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getStartDate());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getStartDate().getDay());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getStartDate().getMonth());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getStartDate().getYear());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTranslatedTitle());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTranslatedTitle().getContent());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTranslatedTitle().getLanguageCode());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getType());
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility());

        assertNotNull(activities.getMemberships());
        assertNotNull(activities.getMemberships());
        assertNotNull(activities.getMemberships().getLastModifiedDate());
        assertNotNull(activities.getMemberships().getSummaries());
        assertEquals(1, activities.getMemberships().getSummaries().size());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getCreatedDate());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getDepartmentName());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getEndDate());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getEndDate().getDay());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getEndDate().getMonth());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getEndDate().getYear());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getOrganization());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getOrganization().getAddress());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getOrganization().getName());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getPutCode());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getRoleTitle());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getSource());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getStartDate());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getStartDate().getDay());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getStartDate().getMonth());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getStartDate().getYear());
        assertNotNull(activities.getMemberships().getSummaries().get(0).getVisibility());

        assertNotNull(activities.getPeerReviews());
        assertNotNull(activities.getPeerReviews().getLastModifiedDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().size());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate().getDay());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate().getMonth());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate().getYear());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCreatedDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getDisplayIndex());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().size());
        assertNotNull(
                activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(
                activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(
                activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0)
                .getRelationship());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getName());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getSource());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

        assertNotNull(activities.getQualifications());
        assertNotNull(activities.getQualifications());
        assertNotNull(activities.getQualifications().getLastModifiedDate());
        assertNotNull(activities.getQualifications().getSummaries());
        assertEquals(1, activities.getQualifications().getSummaries().size());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getCreatedDate());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getDepartmentName());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getEndDate());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getEndDate().getDay());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getEndDate().getMonth());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getEndDate().getYear());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getOrganization());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getOrganization().getAddress());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getOrganization().getName());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getPutCode());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getRoleTitle());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getSource());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getStartDate());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getStartDate().getDay());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getStartDate().getMonth());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getStartDate().getYear());
        assertNotNull(activities.getQualifications().getSummaries().get(0).getVisibility());

        assertNotNull(activities.getServices());
        assertNotNull(activities.getServices());
        assertNotNull(activities.getServices().getLastModifiedDate());
        assertNotNull(activities.getServices().getSummaries());
        assertEquals(1, activities.getServices().getSummaries().size());
        assertNotNull(activities.getServices().getSummaries().get(0).getCreatedDate());
        assertNotNull(activities.getServices().getSummaries().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getServices().getSummaries().get(0).getDepartmentName());
        assertNotNull(activities.getServices().getSummaries().get(0).getEndDate());
        assertNotNull(activities.getServices().getSummaries().get(0).getEndDate().getDay());
        assertNotNull(activities.getServices().getSummaries().get(0).getEndDate().getMonth());
        assertNotNull(activities.getServices().getSummaries().get(0).getEndDate().getYear());
        assertNotNull(activities.getServices().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(activities.getServices().getSummaries().get(0).getOrganization());
        assertNotNull(activities.getServices().getSummaries().get(0).getOrganization().getAddress());
        assertNotNull(activities.getServices().getSummaries().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getServices().getSummaries().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getServices().getSummaries().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getServices().getSummaries().get(0).getOrganization().getName());
        assertNotNull(activities.getServices().getSummaries().get(0).getPutCode());
        assertNotNull(activities.getServices().getSummaries().get(0).getRoleTitle());
        assertNotNull(activities.getServices().getSummaries().get(0).getSource());
        assertNotNull(activities.getServices().getSummaries().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getServices().getSummaries().get(0).getStartDate());
        assertNotNull(activities.getServices().getSummaries().get(0).getStartDate().getDay());
        assertNotNull(activities.getServices().getSummaries().get(0).getStartDate().getMonth());
        assertNotNull(activities.getServices().getSummaries().get(0).getStartDate().getYear());
        assertNotNull(activities.getServices().getSummaries().get(0).getVisibility());

        assertNotNull(activities.getWorks());
        assertNotNull(activities.getWorks().getLastModifiedDate());
        assertNotNull(activities.getWorks().getWorkGroup());
        assertEquals(1, activities.getWorks().getWorkGroup().size());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getIdentifiers());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0));
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getCreatedDate());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getDisplayIndex());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getExternalIdentifiers());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().size());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPublicationDate());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPublicationDate().getDay());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPublicationDate().getMonth());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPublicationDate().getYear());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getSource());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getSubtitle());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getSubtitle().getContent());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getContent());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle().getLanguageCode());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getType());
        assertNotNull(activities.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility());
        assertNotNull(activities.getLastModifiedDate());
    }

    @Test
    public void testmarshallActivities() throws JAXBException, SAXException, URISyntaxException {
        ActivitiesSummary object = (ActivitiesSummary) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/activities-3.0_rc1.xml", ActivitiesSummary.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallRecord() throws SAXException, URISyntaxException {
        Record record = (Record) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/record-3.0_rc1.xml", Record.class, "/record_3.0_rc1/record-3.0_rc1.xsd");
        assertNotNull(record);
        // Check activities
        assertNotNull(record.getActivitiesSummary());
        ActivitiesSummary activities = record.getActivitiesSummary();
        assertNotNull(activities.getLastModifiedDate());

        assertNotNull(activities.getDistinctions());
        Distinctions distinctions = activities.getDistinctions();
        assertNotNull(distinctions.getLastModifiedDate());
        assertEquals(1, distinctions.getSummaries().size());
        DistinctionSummary distinction = distinctions.getSummaries().get(0);
        assertEquals(Long.valueOf(0), distinction.getPutCode());
        assertEquals(Visibility.PRIVATE, distinction.getVisibility());
        assertEquals("distinction:department-name", distinction.getDepartmentName());
        assertEquals("distinction:role-title", distinction.getRoleTitle());
        assertNotNull(distinction.getEndDate());
        assertEquals("02", distinction.getEndDate().getDay().getValue());
        assertEquals("02", distinction.getEndDate().getMonth().getValue());
        assertEquals("1848", distinction.getEndDate().getYear().getValue());
        assertNotNull(distinction.getStartDate());
        assertEquals("02", distinction.getStartDate().getDay().getValue());
        assertEquals("02", distinction.getStartDate().getMonth().getValue());
        assertEquals("1848", distinction.getStartDate().getYear().getValue());
        assertNotNull(distinction.getOrganization());
        assertEquals("common:name", distinction.getOrganization().getName());
        assertEquals("common:city", distinction.getOrganization().getAddress().getCity());
        assertEquals("common:region", distinction.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, distinction.getOrganization().getAddress().getCountry());

        assertNotNull(activities.getEducations());
        Educations educations = activities.getEducations();
        assertNotNull(educations.getLastModifiedDate());
        assertEquals(1, educations.getSummaries().size());
        EducationSummary education = educations.getSummaries().get(0);
        assertEquals(Long.valueOf(0), education.getPutCode());
        assertEquals(Visibility.PRIVATE, education.getVisibility());
        assertEquals("education:department-name", education.getDepartmentName());
        assertEquals("education:role-title", education.getRoleTitle());
        assertNotNull(education.getEndDate());
        assertEquals("02", education.getEndDate().getDay().getValue());
        assertEquals("02", education.getEndDate().getMonth().getValue());
        assertEquals("1848", education.getEndDate().getYear().getValue());
        assertNotNull(education.getStartDate());
        assertEquals("02", education.getStartDate().getDay().getValue());
        assertEquals("02", education.getStartDate().getMonth().getValue());
        assertEquals("1848", education.getStartDate().getYear().getValue());
        assertNotNull(education.getOrganization());
        assertEquals("common:name", education.getOrganization().getName());
        assertEquals("common:city", education.getOrganization().getAddress().getCity());
        assertEquals("common:region", education.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, education.getOrganization().getAddress().getCountry());

        assertNotNull(activities.getEmployments());
        Employments employments = activities.getEmployments();
        assertNotNull(employments.getLastModifiedDate());
        assertEquals(1, employments.getSummaries().size());
        EmploymentSummary employment = employments.getSummaries().get(0);
        assertEquals(Long.valueOf(0), employment.getPutCode());
        assertEquals(Visibility.PRIVATE, employment.getVisibility());
        assertEquals("employment:department-name", employment.getDepartmentName());
        assertEquals("employment:role-title", employment.getRoleTitle());
        assertNotNull(employment.getEndDate());
        assertEquals("02", employment.getEndDate().getDay().getValue());
        assertEquals("02", employment.getEndDate().getMonth().getValue());
        assertEquals("1848", employment.getEndDate().getYear().getValue());
        assertNotNull(employment.getStartDate());
        assertEquals("02", employment.getStartDate().getDay().getValue());
        assertEquals("02", employment.getStartDate().getMonth().getValue());
        assertEquals("1848", employment.getStartDate().getYear().getValue());
        assertNotNull(employment.getOrganization());
        assertEquals("common:name", employment.getOrganization().getName());
        assertEquals("common:city", employment.getOrganization().getAddress().getCity());
        assertEquals("common:region", employment.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, employment.getOrganization().getAddress().getCountry());

        assertNotNull(activities.getInvitedPositions());
        InvitedPositions invitedPositions = activities.getInvitedPositions();
        assertNotNull(invitedPositions.getLastModifiedDate());
        assertEquals(1, invitedPositions.getSummaries().size());
        InvitedPositionSummary invitedPosition = invitedPositions.getSummaries().get(0);
        assertEquals(Long.valueOf(0), invitedPosition.getPutCode());
        assertEquals(Visibility.PRIVATE, invitedPosition.getVisibility());
        assertEquals("invited-position:department-name", invitedPosition.getDepartmentName());
        assertEquals("invited-position:role-title", invitedPosition.getRoleTitle());
        assertNotNull(invitedPosition.getEndDate());
        assertEquals("02", invitedPosition.getEndDate().getDay().getValue());
        assertEquals("02", invitedPosition.getEndDate().getMonth().getValue());
        assertEquals("1848", invitedPosition.getEndDate().getYear().getValue());
        assertNotNull(invitedPosition.getStartDate());
        assertEquals("02", invitedPosition.getStartDate().getDay().getValue());
        assertEquals("02", invitedPosition.getStartDate().getMonth().getValue());
        assertEquals("1848", invitedPosition.getStartDate().getYear().getValue());
        assertNotNull(invitedPosition.getOrganization());
        assertEquals("common:name", invitedPosition.getOrganization().getName());
        assertEquals("common:city", invitedPosition.getOrganization().getAddress().getCity());
        assertEquals("common:region", invitedPosition.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, invitedPosition.getOrganization().getAddress().getCountry());

        assertNotNull(activities.getFundings());
        Fundings fundings = activities.getFundings();
        assertNotNull(fundings.getLastModifiedDate());
        assertEquals(1, fundings.getFundingGroup().size());
        assertNotNull(fundings.getFundingGroup().get(0).getLastModifiedDate());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(1, fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("grant_number", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("external-id-value", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        FundingSummary funding = fundings.getFundingGroup().get(0).getFundingSummary().get(0);
        assertEquals(Long.valueOf(0), funding.getPutCode());
        assertEquals(Visibility.PRIVATE, funding.getVisibility());
        assertNotNull(funding.getTitle());
        assertEquals("common:title", funding.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", funding.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", funding.getTitle().getTranslatedTitle().getLanguageCode());
        assertNotNull(funding.getExternalIdentifiers());
        assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(Relationship.SELF, funding.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("grant_number", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://tempuri.org", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("external-id-value", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(funding.getEndDate());
        assertEquals("02", funding.getEndDate().getDay().getValue());
        assertEquals("02", funding.getEndDate().getMonth().getValue());
        assertEquals("1848", funding.getEndDate().getYear().getValue());
        assertNotNull(funding.getStartDate());
        assertEquals("02", funding.getStartDate().getDay().getValue());
        assertEquals("02", funding.getStartDate().getMonth().getValue());
        assertEquals("1848", funding.getStartDate().getYear().getValue());

        assertNotNull(activities.getMemberships());
        Memberships memberships = activities.getMemberships();
        assertNotNull(memberships.getLastModifiedDate());
        assertEquals(1, memberships.getSummaries().size());
        MembershipSummary membership = memberships.getSummaries().get(0);
        assertEquals(Long.valueOf(0), membership.getPutCode());
        assertEquals(Visibility.PRIVATE, membership.getVisibility());
        assertEquals("membership:department-name", membership.getDepartmentName());
        assertEquals("membership:role-title", membership.getRoleTitle());
        assertNotNull(membership.getEndDate());
        assertEquals("02", membership.getEndDate().getDay().getValue());
        assertEquals("02", membership.getEndDate().getMonth().getValue());
        assertEquals("1848", membership.getEndDate().getYear().getValue());
        assertNotNull(membership.getStartDate());
        assertEquals("02", membership.getStartDate().getDay().getValue());
        assertEquals("02", membership.getStartDate().getMonth().getValue());
        assertEquals("1848", membership.getStartDate().getYear().getValue());
        assertNotNull(membership.getOrganization());
        assertEquals("common:name", membership.getOrganization().getName());
        assertEquals("common:city", membership.getOrganization().getAddress().getCity());
        assertEquals("common:region", membership.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, membership.getOrganization().getAddress().getCountry());

        assertNotNull(funding.getOrganization());
        assertEquals("common:name", funding.getOrganization().getName());
        assertEquals("common:city", funding.getOrganization().getAddress().getCity());
        assertEquals("common:region", funding.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, funding.getOrganization().getAddress().getCountry());

        assertNotNull(activities.getPeerReviews());
        assertNotNull(activities.getPeerReviews().getLastModifiedDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().size());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate().getDay());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate().getMonth());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCompletionDate().getYear());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCreatedDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getCreatedDate().getValue());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getDisplayIndex());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().size());
        assertNotNull(
                activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(
                activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(
                activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0)
                .getRelationship());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress().getCity());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress().getCountry());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getAddress().getRegion());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getOrganization().getName());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getSource());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getSource().retrieveSourcePath());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

        assertNotNull(activities.getQualifications());
        Qualifications qualifications = activities.getQualifications();
        assertNotNull(qualifications.getLastModifiedDate());
        assertEquals(1, qualifications.getSummaries().size());
        QualificationSummary qualification = qualifications.getSummaries().get(0);
        assertEquals(Long.valueOf(0), qualification.getPutCode());
        assertEquals(Visibility.PRIVATE, qualification.getVisibility());
        assertEquals("qualification:department-name", qualification.getDepartmentName());
        assertEquals("qualification:role-title", qualification.getRoleTitle());
        assertNotNull(qualification.getEndDate());
        assertEquals("02", qualification.getEndDate().getDay().getValue());
        assertEquals("02", qualification.getEndDate().getMonth().getValue());
        assertEquals("1848", qualification.getEndDate().getYear().getValue());
        assertNotNull(qualification.getStartDate());
        assertEquals("02", qualification.getStartDate().getDay().getValue());
        assertEquals("02", qualification.getStartDate().getMonth().getValue());
        assertEquals("1848", qualification.getStartDate().getYear().getValue());
        assertNotNull(qualification.getOrganization());
        assertEquals("common:name", qualification.getOrganization().getName());
        assertEquals("common:city", qualification.getOrganization().getAddress().getCity());
        assertEquals("common:region", qualification.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, qualification.getOrganization().getAddress().getCountry());

        assertNotNull(activities.getServices());
        Services services = activities.getServices();
        assertNotNull(services.getLastModifiedDate());
        assertEquals(1, services.getSummaries().size());
        ServiceSummary service = services.getSummaries().get(0);
        assertEquals(Long.valueOf(0), service.getPutCode());
        assertEquals(Visibility.PRIVATE, service.getVisibility());
        assertEquals("service:department-name", service.getDepartmentName());
        assertEquals("service:role-title", service.getRoleTitle());
        assertNotNull(service.getEndDate());
        assertEquals("02", service.getEndDate().getDay().getValue());
        assertEquals("02", service.getEndDate().getMonth().getValue());
        assertEquals("1848", service.getEndDate().getYear().getValue());
        assertNotNull(service.getStartDate());
        assertEquals("02", service.getStartDate().getDay().getValue());
        assertEquals("02", service.getStartDate().getMonth().getValue());
        assertEquals("1848", service.getStartDate().getYear().getValue());
        assertNotNull(service.getOrganization());
        assertEquals("common:name", service.getOrganization().getName());
        assertEquals("common:city", service.getOrganization().getAddress().getCity());
        assertEquals("common:region", service.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, service.getOrganization().getAddress().getCountry());

        assertNotNull(activities.getWorks());
        Works works = activities.getWorks();
        assertNotNull(works.getLastModifiedDate());
        assertEquals(1, works.getWorkGroup().size());
        assertNotNull(works.getWorkGroup().get(0).getIdentifiers());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals(Relationship.PART_OF, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("agr", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://orcid.org", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("external-id-value", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, works.getWorkGroup().get(0).getWorkSummary().size());
        WorkSummary work = works.getWorkGroup().get(0).getWorkSummary().get(0);
        assertEquals(Long.valueOf(0), work.getPutCode());
        assertEquals(Visibility.PRIVATE, work.getVisibility());
        assertNotNull(work.getTitle());
        assertEquals("common:title", work.getTitle().getTitle().getContent());
        assertEquals("common:translated-title", work.getTitle().getTranslatedTitle().getContent());
        assertEquals("en", work.getTitle().getTranslatedTitle().getLanguageCode());
        assertNotNull(work.getExternalIdentifiers());
        assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(Relationship.SELF, work.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("agr", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://tempuri.org", work.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("external-id-value", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(WorkType.ARTISTIC_PERFORMANCE, work.getType());
        assertNotNull(work.getPublicationDate());
        assertEquals("02", work.getPublicationDate().getDay().getValue());
        assertEquals("02", work.getPublicationDate().getMonth().getValue());
        assertEquals("1848", work.getPublicationDate().getYear().getValue());

        // Check biography
        Person person = record.getPerson();
        assertNotNull(person);
        assertNotNull(person.getLastModifiedDate().getValue());
        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getLastModifiedDate());
        assertEquals(1, person.getAddresses().getAddress().size());
        Address address = person.getAddresses().getAddress().get(0);
        assertEquals(Long.valueOf(1), address.getPutCode());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Long.valueOf(0), address.getDisplayIndex());
        assertNotNull(address.getLastModifiedDate());

        assertNotNull(person.getBiography());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());
        assertEquals("biography", person.getBiography().getContent());

        assertNotNull(person.getEmails());
        assertTrue(StringUtils.isNotBlank(person.getEmails().getPath()));
        assertNotNull(person.getEmails().getLastModifiedDate());
        assertNotNull(person.getEmails().getEmails());
        assertEquals(1, person.getEmails().getEmails().size());
        Email email = person.getEmails().getEmails().get(0);
        assertNotNull(email.getCreatedDate().getValue());
        assertEquals("user1@email.com", email.getEmail());
        assertNotNull(email.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(0), email.getPutCode());
        assertNotNull(email.getSource());
        assertEquals(Visibility.PUBLIC, email.getVisibility());

        assertNotNull(person.getExternalIdentifiers());
        assertTrue(StringUtils.isNotBlank(person.getExternalIdentifiers().getPath()));
        assertNotNull(person.getExternalIdentifiers().getLastModifiedDate().getValue());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        PersonExternalIdentifier extId = person.getExternalIdentifiers().getExternalIdentifiers().get(0);
        assertNotNull(extId.getCreatedDate().getValue());
        assertNotNull(extId.getLastModifiedDate());
        assertEquals(Long.valueOf(0), extId.getDisplayIndex());
        assertEquals(Long.valueOf(1), extId.getPutCode());
        assertEquals(Relationship.PART_OF, extId.getRelationship());
        assertNotNull(extId.getSource());
        assertEquals("type-1", extId.getType());
        assertEquals("http://url.com/1", extId.getUrl().getValue());
        assertEquals("value-1", extId.getValue());
        assertEquals(Visibility.PUBLIC, extId.getVisibility());

        assertNotNull(person.getKeywords());
        assertTrue(StringUtils.isNotBlank(person.getKeywords().getPath()));
        assertNotNull(person.getKeywords().getLastModifiedDate().getValue());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(1, person.getKeywords().getKeywords().size());
        Keyword keyword = person.getKeywords().getKeywords().get(0);
        assertEquals("keyword1", keyword.getContent());
        assertNotNull(keyword.getCreatedDate().getValue());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(0), keyword.getDisplayIndex());
        assertEquals(Long.valueOf(1), keyword.getPutCode());
        assertNotNull(keyword.getSource());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        assertNotNull(person.getOtherNames());
        assertTrue(StringUtils.isNotBlank(person.getOtherNames().getPath()));
        assertNotNull(person.getOtherNames().getLastModifiedDate().getValue());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(1, person.getOtherNames().getOtherNames().size());
        OtherName otherName = person.getOtherNames().getOtherNames().get(0);
        assertEquals("other-name-1", otherName.getContent());
        assertNotNull(otherName.getCreatedDate().getValue());
        assertNotNull(otherName.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
        assertEquals(Long.valueOf(1), otherName.getPutCode());
        assertNotNull(otherName.getSource());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        assertNotNull(person.getResearcherUrls());
        assertTrue(StringUtils.isNotBlank(person.getResearcherUrls().getPath()));
        assertNotNull(person.getResearcherUrls().getLastModifiedDate().getValue());
        assertNotNull(person.getResearcherUrls().getResearcherUrls().size());
        ResearcherUrl rUrl = person.getResearcherUrls().getResearcherUrls().get(0);
        assertNotNull(rUrl.getCreatedDate().getValue());
        assertEquals(Long.valueOf(0), rUrl.getDisplayIndex());
        assertNotNull(rUrl.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(1248), rUrl.getPutCode());
        assertNotNull(rUrl.getSource());
        assertEquals("http://url.com/", rUrl.getUrl().getValue());
        assertEquals("url-name-1", rUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, rUrl.getVisibility());

        assertNotNull(person.getName());
        Name name = person.getName();
        assertTrue(StringUtils.isNotBlank(name.getPath()));
        assertEquals("credit-name", name.getCreditName().getContent());
        assertEquals("family-name", name.getFamilyName().getContent());
        assertEquals("give-names", name.getGivenNames().getContent());
        assertNotNull(name.getLastModifiedDate().getValue());
        assertEquals(Visibility.PUBLIC, name.getVisibility());
    }

    @Test
    public void testMarshallRecord() throws JAXBException, SAXException, URISyntaxException {
        Record object = (Record) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/record-3.0_rc1.xml", Record.class);
        marshall(object, "/record_3.0_rc1/record-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallWorks() throws JAXBException, SAXException, URISyntaxException {
        Works works = (Works) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/works-3.0_rc1.xml", Works.class, "/record_3.0_rc1/activities-3.0_rc1.xsd");
        assertNotNull(works);
        assertNotNull(works.getLastModifiedDate());
        assertNotNull(works.getLastModifiedDate().getValue());
        assertEquals(3, works.getWorkGroup().size());
        boolean foundWorkWithNoExtIds = false;
        for (WorkGroup group : works.getWorkGroup()) {
            assertNotNull(group.getLastModifiedDate().getValue());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            if (group.getIdentifiers().getExternalIdentifier().isEmpty()) {
                WorkSummary summary = group.getWorkSummary().get(0);
                assertEquals("1", summary.getDisplayIndex());
                assertEquals(1, summary.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("https://doi.org/123456", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
                assertEquals("123456", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/8888-8888-8888-8880/work/3356", summary.getPath());
                assertEquals("03", summary.getPublicationDate().getDay().getValue());
                assertEquals("03", summary.getPublicationDate().getMonth().getValue());
                assertEquals("2017", summary.getPublicationDate().getYear().getValue());
                assertEquals("Work # 0", summary.getTitle().getTitle().getContent());
                assertEquals(WorkType.CONFERENCE_PAPER, summary.getType());
                assertEquals(Visibility.PUBLIC, summary.getVisibility());
                foundWorkWithNoExtIds = true;
            } else {
                assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
                ExternalID extId = group.getIdentifiers().getExternalIdentifier().get(0);
                if (extId.getType().equals("arxiv")) {
                    assertEquals(Relationship.SELF, extId.getRelationship());
                    assertEquals("http://arxiv.org/abs/123456", extId.getUrl().getValue());
                    assertEquals("123456", extId.getValue());
                } else if (extId.getType().equals("bibcode")) {
                    assertEquals(Relationship.SELF, extId.getRelationship());
                    assertEquals("http://adsabs.harvard.edu/abs/4567", extId.getUrl().getValue());
                    assertEquals("4567", extId.getValue());
                } else {
                    fail("Invalid ext id type " + extId.getType());
                }

                assertEquals(1, group.getWorkSummary().size());
                WorkSummary summary = group.getWorkSummary().get(0);
                if (summary.getPutCode().equals(Long.valueOf(3357))) {
                    assertEquals("1", summary.getDisplayIndex());
                    assertEquals(1, summary.getExternalIdentifiers().getExternalIdentifier().size());
                    assertEquals("arxiv", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                    assertEquals("http://arxiv.org/abs/123456", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
                    assertEquals("123456", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                    assertEquals("/8888-8888-8888-8880/work/3357", summary.getPath());
                    assertEquals("02", summary.getPublicationDate().getDay().getValue());
                    assertEquals("02", summary.getPublicationDate().getMonth().getValue());
                    assertEquals("2017", summary.getPublicationDate().getYear().getValue());
                    assertEquals("Work # 1", summary.getTitle().getTitle().getContent());
                    assertEquals(WorkType.CONFERENCE_PAPER, summary.getType());
                    assertEquals(Visibility.PUBLIC, summary.getVisibility());
                } else if (summary.getPutCode().equals(Long.valueOf(3358))) {
                    assertEquals("1", summary.getDisplayIndex());
                    assertEquals(1, summary.getExternalIdentifiers().getExternalIdentifier().size());
                    assertEquals("bibcode", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                    assertEquals("http://adsabs.harvard.edu/abs/4567", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
                    assertEquals("4567", summary.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                    assertEquals("/8888-8888-8888-8880/work/3358", summary.getPath());
                    assertEquals("03", summary.getPublicationDate().getDay().getValue());
                    assertEquals("03", summary.getPublicationDate().getMonth().getValue());
                    assertEquals("2017", summary.getPublicationDate().getYear().getValue());
                    assertEquals("Work # 2", summary.getTitle().getTitle().getContent());
                    assertEquals(WorkType.JOURNAL_ARTICLE, summary.getType());
                    assertEquals(Visibility.PUBLIC, summary.getVisibility());
                } else {
                    fail("Invalid put code " + summary.getPutCode());
                }
            }
        }
        assertTrue(foundWorkWithNoExtIds);
    }

    @Test
    public void testUnmarshallEducation() throws SAXException, URISyntaxException {
        Education object = (Education) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/education-3.0_rc1.xml", Education.class);
        validateAffiliation(object, false);
        object = (Education) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/education-3.0_rc1.xml", Education.class);
        validateAffiliation(object, true);
    }
    
    @Test
    public void testMarshallEducation() throws JAXBException, SAXException, URISyntaxException {
        Education object = (Education) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/education-3.0_rc1.xml", Education.class);
        marshall(object, "/record_3.0_rc1/education-3.0_rc1.xsd");    
        object = (Education) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/education-3.0_rc1.xml", Education.class);
        marshall(object, "/record_3.0_rc1/education-3.0_rc1.xsd");
    }

    @Test
    public void testMarshallEducations() throws JAXBException, SAXException, URISyntaxException {
        Educations object = (Educations) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/educations-3.0_rc1.xml", Educations.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallEmployment() throws SAXException, URISyntaxException {
        Employment object = (Employment) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/employment-3.0_rc1.xml", Employment.class);
        validateAffiliation(object, false);
        object = (Employment) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/employment-3.0_rc1.xml", Employment.class);
        validateAffiliation(object, true);
    }
    
    @Test
    public void testMarshallEmployment() throws JAXBException, SAXException, URISyntaxException {
        Employment object = (Employment) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/employment-3.0_rc1.xml", Employment.class);
        marshall(object, "/record_3.0_rc1/employment-3.0_rc1.xsd");        
        object = (Employment) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/employment-3.0_rc1.xml", Employment.class);
        marshall(object, "/record_3.0_rc1/employment-3.0_rc1.xsd");
    }

    @Test
    public void testMarshallEmployments() throws JAXBException, SAXException, URISyntaxException {
        Employments object = (Employments) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/employments-3.0_rc1.xml", Employments.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallDistinction() throws SAXException, URISyntaxException {
        Distinction object = (Distinction) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/distinction-3.0_rc1.xml", Distinction.class);
        validateAffiliation(object, false);
        object = (Distinction) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/distinction-3.0_rc1.xml", Distinction.class);
        validateAffiliation(object, true);
    }
    
    @Test
    public void testMarshallDistinction() throws JAXBException, SAXException, URISyntaxException {
        Distinction object = (Distinction) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/distinction-3.0_rc1.xml", Distinction.class);
        marshall(object, "/record_3.0_rc1/distinction-3.0_rc1.xsd");        
        object = (Distinction) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/distinction-3.0_rc1.xml", Distinction.class);
        marshall(object, "/record_3.0_rc1/distinction-3.0_rc1.xsd");
    }

    @Test
    public void testMarshallDistinctions() throws JAXBException, SAXException, URISyntaxException {
        Distinctions object = (Distinctions) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/distinctions-3.0_rc1.xml", Distinctions.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallInvitedPosition() throws SAXException, URISyntaxException {
        InvitedPosition object = (InvitedPosition) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/invited-position-3.0_rc1.xml", InvitedPosition.class);
        validateAffiliation(object, false);
        object = (InvitedPosition) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/invited-position-3.0_rc1.xml", InvitedPosition.class);
        validateAffiliation(object, true);
    }
    
    @Test
    public void testMarshallInvitedPosition() throws JAXBException, SAXException, URISyntaxException {
        InvitedPosition object = (InvitedPosition) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/invited-position-3.0_rc1.xml", InvitedPosition.class);
        marshall(object, "/record_3.0_rc1/invited-position-3.0_rc1.xsd");   
        object = (InvitedPosition) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/invited-position-3.0_rc1.xml", InvitedPosition.class);
        marshall(object, "/record_3.0_rc1/invited-position-3.0_rc1.xsd");
    }

    @Test
    public void testMarshallInvitedPositions() throws JAXBException, SAXException, URISyntaxException {
        InvitedPositions object = (InvitedPositions) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/invited-positions-3.0_rc1.xml", InvitedPositions.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallMembership() throws SAXException, URISyntaxException {
        Membership object = (Membership) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/membership-3.0_rc1.xml", Membership.class);
        validateAffiliation(object, false);
        object = (Membership) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/membership-3.0_rc1.xml", Membership.class);
        validateAffiliation(object, true);
    }
    
    @Test
    public void testMarshallMembership() throws JAXBException, SAXException, URISyntaxException {
        Membership object = (Membership) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/membership-3.0_rc1.xml", Membership.class);
        marshall(object, "/record_3.0_rc1/membership-3.0_rc1.xsd");
        object = (Membership) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/membership-3.0_rc1.xml", Membership.class);
        marshall(object, "/record_3.0_rc1/membership-3.0_rc1.xsd");
    }

    @Test
    public void testMarshallMemberships() throws JAXBException, SAXException, URISyntaxException {
        Memberships object = (Memberships) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/memberships-3.0_rc1.xml", Memberships.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallQualification() throws SAXException, URISyntaxException {
        Qualification object = (Qualification) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/qualification-3.0_rc1.xml", Qualification.class);
        validateAffiliation(object, false);
        object = (Qualification) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/qualification-3.0_rc1.xml", Qualification.class);
        validateAffiliation(object, true);
    }
    
    @Test
    public void testMarshallQualification() throws JAXBException, SAXException, URISyntaxException {
        Qualification object = (Qualification) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/qualification-3.0_rc1.xml", Qualification.class);
        marshall(object, "/record_3.0_rc1/qualification-3.0_rc1.xsd");
        object = (Qualification) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/qualification-3.0_rc1.xml", Qualification.class);
        marshall(object, "/record_3.0_rc1/qualification-3.0_rc1.xsd");
    }

    @Test
    public void testMarshallQualifications() throws JAXBException, SAXException, URISyntaxException {
        Qualifications object = (Qualifications) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/qualifications-3.0_rc1.xml", Qualifications.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");
    }

    @Test
    public void testUnmarshallService() throws SAXException, URISyntaxException {
        Service object = (Service) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/service-3.0_rc1.xml", Service.class);
        validateAffiliation(object, false);
        object = (Service) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/service-3.0_rc1.xml", Service.class);
        validateAffiliation(object, true);
    }
    
    @Test
    public void testMarshallService() throws JAXBException, SAXException, URISyntaxException {
        Service object = (Service) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/service-3.0_rc1.xml", Service.class);
        marshall(object, "/record_3.0_rc1/service-3.0_rc1.xsd");
        object = (Service) unmarshallFromPath("/record_3.0_rc1/samples/write_samples/service-3.0_rc1.xml", Service.class);
        marshall(object, "/record_3.0_rc1/service-3.0_rc1.xsd");
    }

    @Test
    public void testMarshallServices() throws JAXBException, SAXException, URISyntaxException {
        Services object = (Services) unmarshallFromPath("/record_3.0_rc1/samples/read_samples/services-3.0_rc1.xml", Services.class);
        marshall(object, "/record_3.0_rc1/activities-3.0_rc1.xsd");   
    }

    private void validateAffiliation(Affiliation object, boolean writeSample) {
        assertNotNull(object);
        if(!writeSample) {
            assertNotNull(object.getCreatedDate());
            assertNotNull(object.getLastModifiedDate());
            assertEquals(Long.valueOf(0), object.getPutCode());
            assertEquals(Visibility.PRIVATE, object.getVisibility());
            assertEquals("8888-8888-8888-8880", object.getSource().retrieveSourcePath());
            assertEquals("https://orcid.org/8888-8888-8888-8880", object.getSource().retriveSourceUri());            
        }
        assertEquals("department-name", object.getDepartmentName());
        assertEquals("role-title", object.getRoleTitle());
        assertEquals("1848", object.getStartDate().getYear().getValue());
        assertEquals("02", object.getStartDate().getMonth().getValue());
        assertEquals("02", object.getStartDate().getDay().getValue());
        assertEquals("1848", object.getEndDate().getYear().getValue());
        assertEquals("02", object.getEndDate().getMonth().getValue());
        assertEquals("02", object.getEndDate().getDay().getValue());
        assertEquals("common:name", object.getOrganization().getName());
        assertEquals("common:city", object.getOrganization().getAddress().getCity());
        assertEquals("common:region", object.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, object.getOrganization().getAddress().getCountry());
        assertEquals("http://dx.doi.org/10.13039/100000001", object.getOrganization().getDisambiguatedOrganization().getDisambiguatedOrganizationIdentifier());
        assertEquals("FUNDREF", object.getOrganization().getDisambiguatedOrganization().getDisambiguationSource());
        assertEquals("http://tempuri.org", object.getUrl().getValue());
        assertEquals(2, object.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(Relationship.SELF, object.getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("grant_number", object.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("http://tempuri.org", object.getExternalIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("external-identifier-value", object.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Relationship.SELF, object.getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("grant_number", object.getExternalIdentifiers().getExternalIdentifier().get(1).getType());
        assertEquals("http://tempuri.org/2", object.getExternalIdentifiers().getExternalIdentifier().get(1).getUrl().getValue());
        assertEquals("external-identifier-value2", object.getExternalIdentifiers().getExternalIdentifier().get(1).getValue());
    }
    
    private Object unmarshallFromPath(String path, Class<?> type) throws SAXException, URISyntaxException {
        return unmarshallFromPath(path, type, null);
    }

    private Object unmarshallFromPath(String path, Class<?> type, String schemaPath) throws SAXException, URISyntaxException {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type, schemaPath);
            Object result = null;
            if (ResearcherUrls.class.equals(type)) {
                result = (ResearcherUrls) obj;
            } else if (ResearcherUrl.class.equals(type)) {
                result = (ResearcherUrl) obj;
            } else if (PersonalDetails.class.equals(type)) {
                result = (PersonalDetails) obj;
            } else if (PersonExternalIdentifier.class.equals(type)) {
                result = (PersonExternalIdentifier) obj;
            } else if (PersonExternalIdentifiers.class.equals(type)) {
                result = (PersonExternalIdentifiers) obj;
            } else if (Biography.class.equals(type)) {
                result = (Biography) obj;
            } else if (Name.class.equals(type)) {
                result = (Name) obj;
            } else if (CreditName.class.equals(type)) {
                result = (CreditName) obj;
            } else if (OtherName.class.equals(type)) {
                result = (OtherName) obj;
            } else if (OtherNames.class.equals(type)) {
                result = (OtherNames) obj;
            } else if (Keywords.class.equals(type)) {
                result = (Keywords) obj;
            } else if (Keyword.class.equals(type)) {
                result = (Keyword) obj;
            } else if (Addresses.class.equals(type)) {
                result = (Addresses) obj;
            } else if (Address.class.equals(type)) {
                result = (Address) obj;
            } else if (Emails.class.equals(type)) {
                result = (Emails) obj;
            } else if (Email.class.equals(type)) {
                result = (Email) obj;
            } else if (Person.class.equals(type)) {
                result = (Person) obj;
            } else if (Deprecated.class.equals(type)) {
                result = (Deprecated) obj;
            } else if (Preferences.class.equals(type)) {
                result = (Preferences) obj;
            } else if (History.class.equals(type)) {
                result = (History) obj;
            } else if (Record.class.equals(type)) {
                result = (Record) obj;
            } else if (ActivitiesSummary.class.equals(type)) {
                result = (ActivitiesSummary) obj;
            } else if (Works.class.equals(type)) {
                result = (Works) obj;
            } else if (Education.class.equals(type)) {
                result = (Education) obj;
            } else if (Educations.class.equals(type)) {
                result = (Educations) obj;
            } else if (Employment.class.equals(type)) {
                result = (Employment) obj;
            } else if (Employments.class.equals(type)) {
                result = (Employments) obj;
            } else if (Distinction.class.equals(type)) {
                result = (Distinction) obj;
            } else if (Distinctions.class.equals(type)) {
                result = (Distinctions) obj;
            } else if (InvitedPosition.class.equals(type)) {
                result = (InvitedPosition) obj;
            } else if (InvitedPositions.class.equals(type)) {
                result = (InvitedPositions) obj;
            } else if (Membership.class.equals(type)) {
                result = (Membership) obj;
            } else if (Memberships.class.equals(type)) {
                result = (Memberships) obj;
            } else if (Qualification.class.equals(type)) {
                result = (Qualification) obj;
            } else if (Qualifications.class.equals(type)) {
                result = (Qualifications) obj;
            } else if (Service.class.equals(type)) {
                result = (Service) obj;
            } else if (Services.class.equals(type)) {
                result = (Services) obj;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    private Object unmarshall(Reader reader, Class<?> type, String schemaPath) throws SAXException, URISyntaxException {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            if (schemaPath != null) {
                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = sf.newSchema(new File(getClass().getResource(schemaPath).toURI()));
                unmarshaller.setSchema(schema);
            }

            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }

    private void marshall(Object object, String path) throws JAXBException, SAXException, URISyntaxException {
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File(getClass().getResource(path).toURI()));

        marshaller.setSchema(schema);
        marshaller.marshal(object, System.out);
    }
}
