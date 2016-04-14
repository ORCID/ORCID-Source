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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.ApplicationSummary;
import org.orcid.jaxb.model.record_rc2.Applications;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.CreditName;
import org.orcid.jaxb.model.record_rc2.Delegation;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.History;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.Preferences;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.ScopePath;
import org.orcid.jaxb.model.record_rc2.Deprecated;

public class ValidateV2RC2SamplesTest {
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
        assertEquals("http://www.orcid.org/8888-8888-8888-8880", rUrls.getResearcherUrls().get(0).getSource().retriveSourceUri());
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
        assertEquals("biography V2.0_rc2", bio.getContent());
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
        PersonExternalIdentifiers externalIdentifiers = (PersonExternalIdentifiers) unmarshallFromPath("/record_2.0_rc2/samples/external-identifiers-2.0_rc2.xml",
                PersonExternalIdentifiers.class);
        assertNotNull(externalIdentifiers);
        assertNotNull(externalIdentifiers.getExternalIdentifier());
        assertEquals(2, externalIdentifiers.getExternalIdentifier().size());
        for (PersonExternalIdentifier extId : externalIdentifiers.getExternalIdentifier()) {
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

        PersonExternalIdentifier extId = (PersonExternalIdentifier) unmarshallFromPath("/record_2.0_rc2/samples/external-identifier-2.0_rc2.xml", PersonExternalIdentifier.class);
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

        Email email = (Email) unmarshallFromPath("/record_2.0_rc2/samples/email-2.0_rc2.xml", Email.class);
        assertNotNull(email);
        assertNotNull(email.getPutCode());
        assertNotNull(email.getCreatedDate());
        assertNotNull(email.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, email.getVisibility());
        assertEquals("user1@email.com", email.getEmail());
    }

    @Test
    public void testUnmarshallDelegation() {
        Delegation delegation = (Delegation) unmarshallFromPath("/record_2.0_rc2/samples/delegation-2.0_rc2.xml", Delegation.class);
        assertNotNull(delegation);
        assertNotNull(delegation.getGivenPermissionBy());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue());
        assertEquals(2015, delegation.getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue().getYear());
        assertEquals(12, delegation.getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue().getMonth());
        assertEquals(31, delegation.getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue().getDay());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getCreditName());
        assertEquals("given-by-credit-name", delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getVisibility());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue());
        assertEquals(2016, delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getYear());
        assertEquals(1, delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getMonth());
        assertEquals(1, delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getDay());
        assertNotNull(delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier());
        assertEquals("8888-8888-8888-8880", delegation.getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier().getPath());
        assertNotNull(delegation.getGivenPermissionTo());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue());
        assertEquals(2015, delegation.getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue().getYear());
        assertEquals(12, delegation.getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue().getMonth());
        assertEquals(31, delegation.getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue().getDay());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getCreditName());
        assertEquals("given-to-credit-name", delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getVisibility());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue());
        assertEquals(2016, delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getYear());
        assertEquals(1, delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getMonth());
        assertEquals(1, delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getDay());
        assertNotNull(delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier());
        assertEquals("8888-8888-8888-8880", delegation.getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier().getPath());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnmarshallApplications() {
        Applications applications = (Applications) unmarshallFromPath("/record_2.0_rc2/samples/applications-2.0_rc2.xml", Applications.class);
        assertNotNull(applications);
        assertEquals(Visibility.PUBLIC, applications.getVisibility());
        assertNotNull(applications.getApplicationSummary());
        assertEquals(1, applications.getApplicationSummary().size());
        ApplicationSummary summary = applications.getApplicationSummary().get(0);
        assertNotNull(summary.getApplicationOrcid());
        assertEquals("8888-8888-8888-8880", summary.getApplicationOrcid().getPath());
        assertEquals("application-name", summary.getApplicationName());
        assertNotNull(summary.getApplicationWebsite());
        assertEquals("http://application.com", summary.getApplicationWebsite().getValue());
        assertNotNull(summary.getApprovalDate());
        assertEquals(2015, summary.getApprovalDate().getValue().getYear());
        assertEquals(12, summary.getApprovalDate().getValue().getMonth());
        assertEquals(31, summary.getApprovalDate().getValue().getDay());
        assertNotNull(summary.getScopePaths());
        assertNotNull(summary.getScopePaths().getScopePath());
        assertEquals(2, summary.getScopePaths().getScopePath().size());
        for (ScopePath scope : summary.getScopePaths().getScopePath()) {
            assertThat(scope.getContent(), anyOf(is("/authenticate"), is("/read-limited")));
        }
        assertNotNull(summary.getGroupOrcid());
        assertEquals("8888-8888-8888-8880", summary.getGroupOrcid().getPath());
        assertEquals("application-group-name", summary.getGroupName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnmarshallPerson() {
        Person person = (Person) unmarshallFromPath("/record_2.0_rc2/samples/person-2.0_rc2.xml", Person.class);
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
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifier().size());
        PersonExternalIdentifier extId = person.getExternalIdentifiers().getExternalIdentifier().get(0);
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
        assertNotNull(person.getDelegation());
        assertNotNull(person.getDelegation().getGivenPermissionBy());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue());
        assertEquals(2001, person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue().getYear());
        assertEquals(12, person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue().getMonth());
        assertEquals(31, person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getApprovalDate().getValue().getDay());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getCreditName());
        assertEquals("credit-name", person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getVisibility());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue());
        assertEquals(2001, person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getYear());
        assertEquals(12, person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getMonth());
        assertEquals(31, person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getDay());
        assertNotNull(person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier());
        assertEquals("8888-8888-8888-8880", person.getDelegation().getGivenPermissionBy().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier().getPath());
        assertNotNull(person.getDelegation().getGivenPermissionTo());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue());
        assertEquals(2001, person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue().getYear());
        assertEquals(12, person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue().getMonth());
        assertEquals(31, person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getApprovalDate().getValue().getDay());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getCreditName());
        assertEquals("credit-name", person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getContent());
        assertEquals(Visibility.PUBLIC, person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getCreditName().getVisibility());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue());
        assertEquals(2001, person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getYear());
        assertEquals(12, person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getMonth());
        assertEquals(31, person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getLastModifiedDate().getValue().getDay());
        assertNotNull(person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier());
        assertEquals("8888-8888-8888-8880", person.getDelegation().getGivenPermissionTo().get(0).getDelegationDetails().getDelegateSummary().getOrcidIdentifier().getPath());
        assertNotNull(person.getApplications());
        assertNotNull(person.getApplications().getApplicationSummary());
        assertEquals(Visibility.PUBLIC, person.getApplications().getVisibility());
        assertEquals(1, person.getApplications().getApplicationSummary().size());

        ApplicationSummary application = person.getApplications().getApplicationSummary().get(0);
        assertEquals("application-name", application.getApplicationName());
        assertNotNull(application.getApplicationOrcid());
        assertEquals("8888-8888-8888-8880", application.getApplicationOrcid().getPath());
        assertNotNull(application.getApplicationWebsite());
        assertEquals("http://application.com", application.getApplicationWebsite().getValue());
        assertNotNull(application.getApprovalDate());
        assertNotNull(application.getApprovalDate().getValue());
        assertEquals(2001, application.getApprovalDate().getValue().getYear());
        assertEquals(12, application.getApprovalDate().getValue().getMonth());
        assertEquals(31, application.getApprovalDate().getValue().getDay());
        assertNotNull(application.getScopePaths());
        assertNotNull(application.getScopePaths().getScopePath());
        assertEquals(2, application.getScopePaths().getScopePath().size());
        assertThat(application.getScopePaths().getScopePath().get(0).getContent(), anyOf(is("/authenticate"), is("/read-limited")));
        assertThat(application.getScopePaths().getScopePath().get(1).getContent(), anyOf(is("/authenticate"), is("/read-limited")));
        assertNotNull(application.getGroupOrcid());
        assertEquals("8888-8888-8888-8880", application.getGroupOrcid().getPath());
        assertEquals("application-group-name", application.getGroupName());
    }

    
    
    @Test
    public void testUnmarshallDeprecated() {
        Deprecated deprecated = (Deprecated) unmarshallFromPath("/record_2.0_rc2/samples/deprecated-2.0_rc2.xml", Deprecated.class);
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
    public void testUnmarshallPreferences() {
        Preferences preferences = (Preferences) unmarshallFromPath("/record_2.0_rc2/samples/preferences-2.0_rc2.xml", Preferences.class);
        assertNotNull(preferences);   
        assertNotNull(preferences.getLocale());
        assertEquals(Locale.EN, preferences.getLocale());
    }
    
    @Test
    public void testUnmarshallHistory() {
        History history = (History) unmarshallFromPath("/record_2.0_rc2/samples/history-2.0_rc2.xml", History.class);
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
            } else if (Delegation.class.equals(type)) {
                result = (Delegation) obj;
            } else if (Applications.class.equals(type)) {
                result = (Applications) obj;
            } else if (Person.class.equals(type)) {
                result = (Person) obj;
            } else if (Deprecated.class.equals(type)) {
                result = (Deprecated) obj;
            } else if (Preferences.class.equals(type)) {
                result = (Preferences) obj;
            } else if (History.class.equals(type)) {
                result = (History) obj;
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
