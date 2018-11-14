package org.orcid.record;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import org.orcid.jaxb.model.common_rc3.Iso3166Country;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Address;
import org.orcid.jaxb.model.record_rc3.Addresses;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.CreditName;
import org.orcid.jaxb.model.record_rc3.Deprecated;
import org.orcid.jaxb.model.record_rc3.Email;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.jaxb.model.record_rc3.History;
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.Keywords;
import org.orcid.jaxb.model.record_rc3.Name;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.OtherNames;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Preferences;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.jaxb.model.record_rc3.Relationship;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.ResearcherUrls;
import org.orcid.jaxb.model.record_rc3.WorkType;
import org.xml.sax.SAXException;

public class ValidateV2RC3SamplesTest {
    @Test
    public void testUnmarshallPersonalDetails() throws SAXException, URISyntaxException {
        PersonalDetails personalDetails = (PersonalDetails) unmarshallFromPath("/record_2.0_rc3/samples/personal-details-2.0_rc3.xml", PersonalDetails.class,
                "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
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
        PersonalDetails object = (PersonalDetails) unmarshallFromPath("/record_2.0_rc3/samples/personal-details-2.0_rc3.xml", PersonalDetails.class);        
        marshall(object, "/record_2.0_rc3/personal-details-2.0_rc3.xsd");                        
    }

    @Test
    public void testUnmarshallResearcherUrl() throws SAXException, URISyntaxException {
        ResearcherUrls rUrls = (ResearcherUrls) unmarshallFromPath("/record_2.0_rc3/samples/researcher-urls-2.0_rc3.xml", ResearcherUrls.class,
                "/record_2.0_rc3/researcher-url-2.0_rc3.xsd");
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
        assertEquals("http://www.orcid.org/8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retriveSourceUri());
        assertEquals("8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retrieveSourcePath());

        ResearcherUrl rUrl = (ResearcherUrl) unmarshallFromPath("/record_2.0_rc3/samples/researcher-url-2.0_rc3.xml", ResearcherUrl.class);
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
        ResearcherUrls object = (ResearcherUrls) unmarshallFromPath("/record_2.0_rc3/samples/researcher-urls-2.0_rc3.xml", ResearcherUrls.class);
        marshall(object, "/record_2.0_rc3/researcher-url-2.0_rc3.xsd");
    }

    @Test
    public void testUnmarshallAddress() throws SAXException, URISyntaxException {
        Addresses addresses = (Addresses) unmarshallFromPath("/record_2.0_rc3/samples/addresses-2.0_rc3.xml", Addresses.class, "/record_2.0_rc3/address-2.0_rc3.xsd");
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

        Address address = (Address) unmarshallFromPath("/record_2.0_rc3/samples/address-2.0_rc3.xml", Address.class);
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
        Addresses object = (Addresses) unmarshallFromPath("/record_2.0_rc3/samples/addresses-2.0_rc3.xml", Addresses.class);
        marshall(object, "/record_2.0_rc3/address-2.0_rc3.xsd");
    }

    @Test
    public void testUnmarshallBiography() throws SAXException, URISyntaxException {
        Biography bio = (Biography) unmarshallFromPath("/record_2.0_rc3/samples/biography-2.0_rc3.xml", Biography.class, "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
        assertNotNull(bio);
        assertEquals("biography V2.0_rc3", bio.getContent());
        assertEquals(Visibility.PUBLIC.value(), bio.getVisibility().value());
    }

    @Test
    public void testMarshallBiography() throws JAXBException, SAXException, URISyntaxException {
        Biography object = (Biography) unmarshallFromPath("/record_2.0_rc3/samples/biography-2.0_rc3.xml", Biography.class);
        marshall(object, "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallCreditName() throws SAXException, URISyntaxException {
        CreditName creditName = (CreditName) unmarshallFromPath("/record_2.0_rc3/samples/credit-name-2.0_rc3.xml", CreditName.class,
                "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
        assertNotNull(creditName);
        assertEquals("credit-name", creditName.getContent());
        assertEquals(Visibility.PUBLIC.value(), creditName.getVisibility().value());
    }

    @Test
    public void testMarshallCreditName() throws JAXBException, SAXException, URISyntaxException {
        CreditName object = (CreditName) unmarshallFromPath("/record_2.0_rc3/samples/credit-name-2.0_rc3.xml", CreditName.class);
        marshall(object, "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallExternalIdentifiers() throws SAXException, URISyntaxException {
        PersonExternalIdentifiers externalIdentifiers = (PersonExternalIdentifiers) unmarshallFromPath("/record_2.0_rc3/samples/external-identifiers-2.0_rc3.xml",
                PersonExternalIdentifiers.class, "/record_2.0_rc3/person-external-identifier-2.0_rc3.xsd");
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

        PersonExternalIdentifier extId = (PersonExternalIdentifier) unmarshallFromPath("/record_2.0_rc3/samples/external-identifier-2.0_rc3.xml", PersonExternalIdentifier.class);
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
        PersonExternalIdentifiers object = (PersonExternalIdentifiers) unmarshallFromPath("/record_2.0_rc3/samples/external-identifiers-2.0_rc3.xml",
                PersonExternalIdentifiers.class);
        marshall(object, "/record_2.0_rc3/person-external-identifier-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallKeyword() throws SAXException, URISyntaxException {
        Keywords keywords = (Keywords) unmarshallFromPath("/record_2.0_rc3/samples/keywords-2.0_rc3.xml", Keywords.class, "/record_2.0_rc3/keyword-2.0_rc3.xsd");
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

        Keyword keyword = (Keyword) unmarshallFromPath("/record_2.0_rc3/samples/keyword-2.0_rc3.xml", Keyword.class);
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
        Keywords object = (Keywords) unmarshallFromPath("/record_2.0_rc3/samples/keywords-2.0_rc3.xml", Keywords.class);
        marshall(object, "/record_2.0_rc3/keyword-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallName() throws SAXException, URISyntaxException {
        Name name = (Name) unmarshallFromPath("/record_2.0_rc3/samples/name-2.0_rc3.xml", Name.class, "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
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
        Name object = (Name) unmarshallFromPath("/record_2.0_rc3/samples/name-2.0_rc3.xml", Name.class);
        marshall(object, "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallOtherNames() throws SAXException, URISyntaxException {
        OtherNames otherNames = (OtherNames) unmarshallFromPath("/record_2.0_rc3/samples/other-names-2.0_rc3.xml", OtherNames.class,
                "/record_2.0_rc3/personal-details-2.0_rc3.xsd");
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

        OtherName otherName = (OtherName) unmarshallFromPath("/record_2.0_rc3/samples/other-name-2.0_rc3.xml", OtherName.class);
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
        OtherNames object = (OtherNames) unmarshallFromPath("/record_2.0_rc3/samples/other-names-2.0_rc3.xml", OtherNames.class);
        marshall(object, "/record_2.0_rc3/other-name-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallEmails() throws SAXException, URISyntaxException {
        Emails emails = (Emails) unmarshallFromPath("/record_2.0_rc3/samples/emails-2.0_rc3.xml", Emails.class, "/record_2.0_rc3/email-2.0_rc3.xsd");
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

        Email email = (Email) unmarshallFromPath("/record_2.0_rc3/samples/email-2.0_rc3.xml", Email.class);
        assertNotNull(email);
        assertNotNull(email.getPutCode());
        assertNotNull(email.getCreatedDate());
        assertNotNull(email.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, email.getVisibility());
        assertEquals("user1@email.com", email.getEmail());
    }

    @Test
    public void testMarshallEmails() throws JAXBException, SAXException, URISyntaxException {
        Emails object = (Emails) unmarshallFromPath("/record_2.0_rc3/samples/emails-2.0_rc3.xml", Emails.class);
        marshall(object, "/record_2.0_rc3/email-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallPerson() throws SAXException, URISyntaxException {
        Person person = (Person) unmarshallFromPath("/record_2.0_rc3/samples/person-2.0_rc3.xml", Person.class, "/record_2.0_rc3/person-2.0_rc3.xsd");
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
        Person object = (Person) unmarshallFromPath("/record_2.0_rc3/samples/person-2.0_rc3.xml", Person.class);
        marshall(object, "/record_2.0_rc3/person-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallDeprecated() throws SAXException, URISyntaxException {
        Deprecated deprecated = (Deprecated) unmarshallFromPath("/record_2.0_rc3/samples/deprecated-2.0_rc3.xml", Deprecated.class,
                "/record_2.0_rc3/deprecated-2.0_rc3.xsd");
        assertNotNull(deprecated);
        assertNotNull(deprecated.getPrimaryRecord());
        assertNotNull(deprecated.getPrimaryRecord().getOrcidIdentifier());
        assertEquals("http://orcid.org/8888-8888-8888-8880", deprecated.getPrimaryRecord().getOrcidIdentifier().getUri());
        assertEquals("8888-8888-8888-8880", deprecated.getPrimaryRecord().getOrcidIdentifier().getPath());
        assertEquals("orcid.org", deprecated.getPrimaryRecord().getOrcidIdentifier().getHost());
        assertNotNull(deprecated.getDeprecatedDate());
        assertEquals(2001, deprecated.getDeprecatedDate().getValue().getYear());
        assertEquals(12, deprecated.getDeprecatedDate().getValue().getMonth());
        assertEquals(31, deprecated.getDeprecatedDate().getValue().getDay());        
    }
    
    @Test
    public void testMarshallDeprecated() throws JAXBException, SAXException, URISyntaxException {
        Deprecated object = (Deprecated) unmarshallFromPath("/record_2.0_rc3/samples/deprecated-2.0_rc3.xml", Deprecated.class);
        marshall(object, "/record_2.0_rc3/deprecated-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallPreferences() throws SAXException, URISyntaxException {
        Preferences preferences = (Preferences) unmarshallFromPath("/record_2.0_rc3/samples/preferences-2.0_rc3.xml", Preferences.class,
                "/record_2.0_rc3/preferences-2.0_rc3.xsd");
        assertNotNull(preferences);   
        assertNotNull(preferences.getLocale());
        assertEquals(Locale.EN, preferences.getLocale());
    }
    
    @Test
    public void testMarshallPreferences() throws JAXBException, SAXException, URISyntaxException {
        Preferences object = (Preferences) unmarshallFromPath("/record_2.0_rc3/samples/preferences-2.0_rc3.xml", Preferences.class);
        marshall(object, "/record_2.0_rc3/preferences-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallHistory() throws SAXException, URISyntaxException {
        History history = (History) unmarshallFromPath("/record_2.0_rc3/samples/history-2.0_rc3.xml", History.class, "/record_2.0_rc3/history-2.0_rc3.xsd");
        assertNotNull(history);           
        assertNotNull(history.getSource());
        assertEquals("http://orcid.org/8888-8888-8888-8880", history.getSource().retriveSourceUri());
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
        History object = (History) unmarshallFromPath("/record_2.0_rc3/samples/history-2.0_rc3.xml", History.class);
        marshall(object, "/record_2.0_rc3/history-2.0_rc3.xsd");        
    }
    
    @Test
    public void testUnmarshallActivities() throws SAXException, URISyntaxException {
        ActivitiesSummary activities = (ActivitiesSummary) unmarshallFromPath("/record_2.0_rc3/samples/activities-2.0_rc3.xml", ActivitiesSummary.class,
                "/record_2.0_rc3/activities-2.0_rc3.xsd");
        assertNotNull(activities);
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
        assertNotNull(activities.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
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
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
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
        ActivitiesSummary object = (ActivitiesSummary) unmarshallFromPath("/record_2.0_rc3/samples/activities-2.0_rc3.xml", ActivitiesSummary.class);
        marshall(object, "/record_2.0_rc3/activities-2.0_rc3.xsd");
    }
    
    @Test
    public void testUnmarshallRecord() throws SAXException, URISyntaxException {
        Record record = (Record) unmarshallFromPath("/record_2.0_rc3/samples/record-2.0_rc3.xml", Record.class, "/record_2.0_rc3/record-2.0_rc3.xsd");
        assertNotNull(record);
        //Check activities
        assertNotNull(record.getActivitiesSummary());
        ActivitiesSummary activities = record.getActivitiesSummary();
        assertNotNull(activities.getLastModifiedDate());
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
        assertEquals("1948", education.getEndDate().getYear().getValue());
        assertNotNull(education.getStartDate());
        assertEquals("02", education.getStartDate().getDay().getValue());
        assertEquals("02", education.getStartDate().getMonth().getValue());
        assertEquals("1948", education.getStartDate().getYear().getValue());
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
        assertEquals("1948", employment.getEndDate().getYear().getValue());
        assertNotNull(employment.getStartDate());
        assertEquals("02", employment.getStartDate().getDay().getValue());
        assertEquals("02", employment.getStartDate().getMonth().getValue());
        assertEquals("1948", employment.getStartDate().getYear().getValue());
        assertNotNull(employment.getOrganization());
        assertEquals("common:name", employment.getOrganization().getName());
        assertEquals("common:city", employment.getOrganization().getAddress().getCity());        
        assertEquals("common:region", employment.getOrganization().getAddress().getRegion());
        assertEquals(Iso3166Country.AF, employment.getOrganization().getAddress().getCountry());
        
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
        assertEquals("1948", funding.getEndDate().getYear().getValue());
        assertNotNull(funding.getStartDate());
        assertEquals("02", funding.getStartDate().getDay().getValue());
        assertEquals("02", funding.getStartDate().getMonth().getValue());
        assertEquals("1948", funding.getStartDate().getYear().getValue());
        
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
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getUrl());
        assertNotNull(activities.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
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
        assertEquals("1948", work.getPublicationDate().getYear().getValue());
        
        //Check biography
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
        Record object = (Record) unmarshallFromPath("/record_2.0_rc3/samples/record-2.0_rc3.xml", Record.class);
        marshall(object, "/record_2.0_rc3/record-2.0_rc3.xsd");
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
            } else if(Record.class.equals(type)) {
                result = (Record) obj;
            } else if(ActivitiesSummary.class.equals(type)) {
                result = (ActivitiesSummary) obj;
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
