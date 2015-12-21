/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.record;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.CreditName;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;

public class ValidateV2RC2Samples {
    @Test
    public void testUnmarshallPersonalDetails() {
        PersonalDetails personalDetails = (PersonalDetails) unmarshallFromPath("/record_2.0_rc2/samples/personal-details-2.0_rc2.xml", PersonalDetails.class);
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
    public void testUnmarshallResearcherUrl() {
        ResearcherUrls rUrls = (ResearcherUrls) unmarshallFromPath("/record_2.0_rc2/samples/researcher-urls-2.0_rc2.xml", ResearcherUrls.class);
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
        assertEquals("http://orcid.org/8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retriveSourceUri());
        assertEquals("8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retrieveSourcePath());

        ResearcherUrl rUrl = (ResearcherUrl) unmarshallFromPath("/record_2.0_rc2/samples/researcher-url-2.0_rc2.xml", ResearcherUrl.class);
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
    public void testUnmarshallAddress() {
        Addresses addresses = (Addresses) unmarshallFromPath("/record_2.0_rc2/samples/addresses-2.0_rc2.xml", Addresses.class);
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(2, addresses.getAddress().size());
        for(Address address : addresses.getAddress()) {
            assertNotNull(address.getPutCode());
            assertNotNull(address.getCreatedDate());
            assertNotNull(address.getLastModifiedDate());
            assertNotNull(address.getCountry());
            if(address.getPutCode().equals(new Long(1))) {                
                assertEquals(Iso3166Country.US, address.getCountry().getValue());
                assertEquals(Visibility.PUBLIC, address.getVisibility());
            } else {
                assertEquals(Iso3166Country.CR, address.getCountry().getValue());
                assertEquals(Visibility.LIMITED, address.getVisibility());
            }            
        }
        
        Address address = (Address) unmarshallFromPath("/record_2.0_rc2/samples/address-2.0_rc2.xml", Address.class);
        assertNotNull(address);
        assertNotNull(address.getPutCode());
        assertNotNull(address.getCreatedDate());
        assertNotNull(address.getLastModifiedDate());
        assertNotNull(address.getCountry());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
    }

    @Test
    public void testUnmarshallBiography() {
        Biography bio = (Biography) unmarshallFromPath("/record_2.0_rc2/samples/biography-2.0_rc2.xml", Biography.class);
        assertNotNull(bio);
        assertEquals("biography", bio.getContent());
        assertEquals(Visibility.PUBLIC.value(), bio.getVisibility().value());
    }

    @Test
    public void testUnmarshallCreditName() {
        CreditName creditName = (CreditName) unmarshallFromPath("/record_2.0_rc2/samples/credit-name-2.0_rc2.xml", CreditName.class);
        assertNotNull(creditName);
        assertEquals("credit-name", creditName.getContent());
        assertEquals(Visibility.PUBLIC.value(), creditName.getVisibility().value());
    }    

    @SuppressWarnings("unchecked")
    @Test
    public void testUnmarshallExternalIdentifiers() {
        ExternalIdentifiers externalIdentifiers = (ExternalIdentifiers) unmarshallFromPath("/record_2.0_rc2/samples/external-identifiers-2.0_rc2.xml",
                ExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(2, externalIdentifiers.getExternalIdentifier().size());
        for (ExternalIdentifier extId : externalIdentifiers.getExternalIdentifier()) {
            assertThat(extId.getPutCode(), anyOf(is(1L), is(2L)));
            assertThat(extId.getCommonName(), anyOf(is("common-name-1"), is("common-name-2")));
            assertThat(extId.getReference(), anyOf(is("id-reference-1"), is("id-reference-2")));
            assertNotNull(extId.getUrl());
            assertThat(extId.getUrl().getValue(), anyOf(is("http://url/1"), is("http://url/2")));
            assertNotNull(extId.getCreatedDate());
            assertNotNull(extId.getLastModifiedDate());
            assertNotNull(extId.getSource());
            assertEquals("8888-8888-8888-8880", extId.getSource().retrieveSourcePath());
        }

        ExternalIdentifier extId = (ExternalIdentifier) unmarshallFromPath("/record_2.0_rc2/samples/external-identifier-2.0_rc2.xml", ExternalIdentifier.class);
        assertNotNull(extId);
        assertEquals("A-0003", extId.getCommonName());
        assertEquals(Long.valueOf(1), extId.getPutCode());
        assertEquals("A-0003", extId.getReference());
        assertNotNull(extId.getUrl());
        assertEquals("http://ext-id/A-0003", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getSource());
        assertEquals("8888-8888-8888-8880", extId.getSource().retrieveSourcePath());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnmarshallKeyword() {
        Keywords keywords = (Keywords) unmarshallFromPath("/record_2.0_rc2/samples/keywords-2.0_rc2.xml", Keywords.class);
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

        Keyword keyword = (Keyword) unmarshallFromPath("/record_2.0_rc2/samples/keyword-2.0_rc2.xml", Keyword.class);
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
    public void testUnmarshallName() {
        Name name = (Name) unmarshallFromPath("/record_2.0_rc2/samples/name-2.0_rc2.xml", Name.class);
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

    @SuppressWarnings("unchecked")
    @Test
    public void testUnmarshallOtherNames() {
        OtherNames otherNames = (OtherNames) unmarshallFromPath("/record_2.0_rc2/samples/other-names-2.0_rc2.xml", OtherNames.class);
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

        OtherName otherName = (OtherName) unmarshallFromPath("/record_2.0_rc2/samples/other-name-2.0_rc2.xml", OtherName.class);
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
    public void testUnmarshallEmails() {
        Emails emails = (Emails) unmarshallFromPath("/record_2.0_rc2/samples/emails-2.0_rc2.xml", Emails.class);
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(2, emails.getEmails().size());
        
        for(Email email : emails.getEmails()) {
            assertNotNull(email.getPutCode());
            assertNotNull(email.getCreatedDate());
            assertNotNull(email.getLastModifiedDate());
            if(email.getPutCode().equals(Long.valueOf(1))) {
                assertEquals(Visibility.PUBLIC, email.getVisibility());
                assertEquals("user1@email.com", email.getEmail());
            } else {
                assertEquals(Visibility.PUBLIC, email.getVisibility());
                assertEquals("user2@email.com", email.getEmail());
            }
        }
        
        Email email= (Email) unmarshallFromPath("/record_2.0_rc2/samples/email-2.0_rc2.xml", Email.class);
        assertNotNull(email);
        assertNotNull(email.getPutCode());
        assertNotNull(email.getCreatedDate());
        assertNotNull(email.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, email.getVisibility());
        assertEquals("user1@email.com", email.getEmail());
    }
    
    @Test
    public void testUnmarshallPerson() {
        fail();
    }    
    
    private Object unmarshallFromPath(String path, Class<?> type) {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream(path))) {
            Object obj = unmarshall(reader, type);
            Object result = null;
            if (ResearcherUrls.class.equals(type)) {
                result = (ResearcherUrls) obj;
            } else if (ResearcherUrl.class.equals(type)) {
                result = (ResearcherUrl) obj;
            } else if (PersonalDetails.class.equals(type)) {
                result = (PersonalDetails) obj;
            } else if (ExternalIdentifier.class.equals(type)) {
                result = (ExternalIdentifier) obj;
            } else if (ExternalIdentifiers.class.equals(type)) {
                result = (ExternalIdentifiers) obj;
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
            } else if(Address.class.equals(type)) {
                result = (Address) obj;
            } else if (Emails.class.equals(type)) {
                result = (Emails) obj;
            } else if(Email.class.equals(type)) {
                result = (Email) obj;
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error reading notification from classpath", e);
        }
    }

    private Object unmarshall(Reader reader, Class<?> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to unmarshall orcid message" + e);
        }
    }
}
