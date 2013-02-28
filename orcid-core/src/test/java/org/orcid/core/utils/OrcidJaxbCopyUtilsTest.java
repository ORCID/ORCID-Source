/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AlternativeEmail;
import org.orcid.jaxb.model.message.AlternativeEmails;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 13/03/2012
 */
public class OrcidJaxbCopyUtilsTest {

    private Unmarshaller unmarshaller;

    private OrcidMessage protectedOrcidMessage;
    private OrcidMessage publicOrcidMessage;

    public OrcidJaxbCopyUtilsTest() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Before
    public void init() throws JAXBException {
        protectedOrcidMessage = getOrcidMessage("/orcid-protected-full-message-latest.xml");
        publicOrcidMessage = getOrcidMessage("/orcid-full-message-no-visibility-latest.xml");
    }

    @Test
    public void testUpdateOtherNamePreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        PersonalDetails existingOrcidPersonalDetails = protectedOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();
        PersonalDetails updatedOrcidPersonalDetails = publicOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();

        OtherNames existingOtherNames = existingOrcidPersonalDetails.getOtherNames();
        assertEquals(Visibility.LIMITED, existingOtherNames.getVisibility());
        assertTrue(existingOtherNames.getOtherName().size() == 2);
        assertEquals("Josiah S Carberry", existingOtherNames.getOtherName().get(0).getContent());
        assertEquals("Josiah Carberry", existingOtherNames.getOtherName().get(1).getContent());

        // check content and visibility update updates the content
        OtherNames updatedOtherNames = new OtherNames();
        updatedOtherNames.getOtherName().clear();
        updatedOtherNames.addOtherName("Another 1");
        updatedOtherNames.addOtherName("Another 2");
        updatedOtherNames.setVisibility(Visibility.PRIVATE);
        updatedOrcidPersonalDetails.setOtherNames(updatedOtherNames);

        Address existingContactDetailsAddress = existingOrcidBioProtected.getContactDetails().getAddress();
        assertEquals("US", existingContactDetailsAddress.getCountry().getContent());
        existingContactDetailsAddress.getCountry().setVisibility(Visibility.LIMITED);

        Address nullVisibilityContactAddress = new Address();
        nullVisibilityContactAddress.setCountry(new Country("Updated Country"));
        nullVisibilityContactAddress.getCountry().setVisibility(null);
        updatedOrcidBioPublic.getContactDetails().setAddress(nullVisibilityContactAddress);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingOtherNames = existingOrcidPersonalDetails.getOtherNames();
        assertTrue(existingOtherNames.getOtherName().size() == 2);
        assertEquals("Another 1", existingOtherNames.getOtherName().get(0).getContent());
        assertEquals("Another 2", existingOtherNames.getOtherName().get(1).getContent());
        assertEquals(Visibility.PRIVATE, existingOtherNames.getVisibility());

        // check content and visibility update of null
        updatedOtherNames = new OtherNames();
        updatedOtherNames.getOtherName().clear();
        updatedOtherNames.addOtherName("Yet Another 1");
        updatedOtherNames.addOtherName("Yet Another 2");
        updatedOtherNames.setVisibility(null);
        updatedOrcidPersonalDetails.setOtherNames(updatedOtherNames);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);

        assertTrue(existingOtherNames.getOtherName().size() == 2);
        assertEquals("Another 1", existingOtherNames.getOtherName().get(0).getContent());
        assertEquals("Another 2", existingOtherNames.getOtherName().get(1).getContent());
        assertEquals(Visibility.PRIVATE, existingOtherNames.getVisibility());
        existingContactDetailsAddress = existingOrcidBioProtected.getContactDetails().getAddress();
        assertEquals(Visibility.LIMITED, existingContactDetailsAddress.getCountry().getVisibility());
        assertEquals("Updated Country", existingContactDetailsAddress.getCountry().getContent());

    }

    @Test
    public void testUpdateCreditNamePreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        PersonalDetails existingOrcidPersonalDetails = protectedOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();
        PersonalDetails updatedOrcidPersonalDetails = publicOrcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails();

        // set default of credit to limited on existing and null on updated
        // -should remain as limited and not 'downgrade'
        assertEquals(existingOrcidPersonalDetails.getCreditName().getContent(), "J. S. Carberry");
        assertEquals(Visibility.LIMITED, existingOrcidPersonalDetails.getCreditName().getVisibility());

        updatedOrcidPersonalDetails.setCreditName(new CreditName("Don"));
        updatedOrcidPersonalDetails.getCreditName().setVisibility(Visibility.PRIVATE);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertEquals(existingOrcidPersonalDetails.getCreditName().getContent(), "Don");
        assertEquals(Visibility.PRIVATE, existingOrcidPersonalDetails.getCreditName().getVisibility());

        updatedOrcidPersonalDetails.setCreditName(new CreditName("Jimmy"));
        updatedOrcidPersonalDetails.getCreditName().setVisibility(null);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertEquals(existingOrcidBioProtected.getPersonalDetails().getCreditName().getContent(), "Jimmy");
        assertEquals(Visibility.PRIVATE, existingOrcidPersonalDetails.getCreditName().getVisibility());

    }

    @Test
    public void testUpdatedBiographyToExistingPreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();
        assertEquals(Visibility.LIMITED, existingOrcidBioProtected.getBiography().getVisibility());
        String existingContent = "Josiah Stinkney Carberry is a fictional professor.";
        assertEquals(existingContent, existingOrcidBioProtected.getBiography().getContent().trim());
        assertNull(updatedOrcidBioPublic.getBiography().getVisibility());
        updatedOrcidBioPublic.setBiography(new Biography("A new bio"));
        updatedOrcidBioPublic.getBiography().setVisibility(Visibility.PRIVATE);
        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);

        // check that changes have propogated
        assertEquals(Visibility.PRIVATE, existingOrcidBioProtected.getBiography().getVisibility());
        assertEquals("A new bio", existingOrcidBioProtected.getBiography().getContent());

        // reset the variable and attempt to override the content and visibility
        updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();
        updatedOrcidBioPublic.setBiography(new Biography("A new and impoved bio"));

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        // check that the old values have been retained
        assertEquals(Visibility.PRIVATE, existingOrcidBioProtected.getBiography().getVisibility());
        assertEquals("A new and impoved bio", existingOrcidBioProtected.getBiography().getContent());

    }

    @Test
    public void testUpdatedResearcherUrlToExistingPreservingVisibility() throws Exception {
        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        ResearcherUrls existingResearcherUrls = existingOrcidBioProtected.getResearcherUrls();
        assertEquals(Visibility.PUBLIC, existingResearcherUrls.getVisibility());
        assertTrue(existingResearcherUrls.getResearcherUrl().size() == 3);
        assertEquals("http://library.brown.edu/about/hay/carberry.php", existingResearcherUrls.getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("http://en.wikipedia.org/wiki/Josiah_S._Carberry", existingResearcherUrls.getResearcherUrl().get(1).getUrl().getValue());
        assertEquals("http://www.brown.edu/Administration/News_Bureau/Databases/Encyclopedia/search.php?serial=C0070", existingResearcherUrls.getResearcherUrl().get(2)
                .getUrl().getValue());

        ResearcherUrls updatedResearcherUrls = new ResearcherUrls();
        ResearcherUrl onlyUrl = new ResearcherUrl(new Url("http://library.brown.edu/about/hay/carberry.html"));
        updatedResearcherUrls.getResearcherUrl().add(onlyUrl);
        updatedResearcherUrls.setVisibility(Visibility.LIMITED);
        updatedOrcidBioPublic.setResearcherUrls(updatedResearcherUrls);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertTrue(existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().size() == 1);
        assertEquals("http://library.brown.edu/about/hay/carberry.html", existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(existingOrcidBioProtected.getResearcherUrls(), updatedOrcidBioPublic.getResearcherUrls());
        assertEquals(Visibility.LIMITED, existingOrcidBioProtected.getResearcherUrls().getVisibility());

        updatedResearcherUrls = new ResearcherUrls();
        onlyUrl = new ResearcherUrl(new Url("http://library.brown.edu/about/hay/carberry.jsp"));
        updatedResearcherUrls.getResearcherUrl().add(onlyUrl);
        updatedResearcherUrls.setVisibility(null);
        updatedOrcidBioPublic.setResearcherUrls(updatedResearcherUrls);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertTrue(existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().size() == 1);
        assertEquals("http://library.brown.edu/about/hay/carberry.jsp", existingOrcidBioProtected.getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(existingOrcidBioProtected.getResearcherUrls(), updatedOrcidBioPublic.getResearcherUrls());
        assertEquals(Visibility.LIMITED, existingOrcidBioProtected.getResearcherUrls().getVisibility());

    }

    @Test
    public void testUpdatedAffilationsToExistingPreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        // create a copy of the bio data for doing a merge
        OrcidBio updatedOrcidBioPublic = getOrcidMessage("/orcid-public-full-message-latest.xml").getOrcidProfile().getOrcidBio();

        List<Affiliation> existingAffilations = existingOrcidBioProtected.getAffiliations();
        List<Affiliation> updatedAffilations = updatedOrcidBioPublic.getAffiliations();

        assertEquals("Brown University", existingAffilations.get(0).getAffiliationName());
        assertEquals("Yale University", existingAffilations.get(1).getAffiliationName());
        assertEquals(Visibility.PUBLIC, existingAffilations.get(0).getVisibility());
        assertNull(existingAffilations.get(1).getAddress().getCountry().getVisibility());
        assertEquals(Visibility.PUBLIC, existingAffilations.get(1).getVisibility());
        assertEquals(Visibility.LIMITED, updatedAffilations.get(1).getAddress().getCountry().getVisibility());
        assertTrue(existingAffilations.size() == 2);
        assertTrue(updatedAffilations.size() == 2);

        // to test:
        // updating affiliations retains visibility when null - changes content
        updatedAffilations.get(0).setAffiliationName("new affiliation name");
        updatedAffilations.get(0).setVisibility(null);
        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        updatedAffilations = updatedOrcidBioPublic.getAffiliations();
        assertEquals("new affiliation name", existingAffilations.get(0).getAffiliationName());
        assertEquals(Visibility.LIMITED, existingAffilations.get(1).getAddress().getCountry().getVisibility());
        assertEquals(Visibility.PUBLIC, existingAffilations.get(0).getVisibility());

        // updating affiliations changes visibility when populated - changes
        // content
        updatedAffilations.get(0).setAffiliationName("a seperate affiliation name");
        updatedAffilations.get(0).setVisibility(Visibility.PRIVATE);
        updatedAffilations.get(0).getAddress().getCountry().setVisibility(null);
        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingAffilations = existingOrcidBioProtected.getAffiliations();
        assertEquals("a seperate affiliation name", existingAffilations.get(0).getAffiliationName());
        assertEquals(Visibility.LIMITED, existingAffilations.get(0).getAddress().getCountry().getVisibility());
        assertEquals(Visibility.PRIVATE, existingAffilations.get(0).getVisibility());
        // adding new affiliations with a null visibility adds an extra element
        // with the def

        Affiliation extraAffiliation = new Affiliation();
        extraAffiliation.setAffiliationName("extra affiliation");
        updatedAffilations.add(extraAffiliation);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingAffilations = existingOrcidBioProtected.getAffiliations();
        assertTrue(existingAffilations.size() == 3);

        assertEquals("a seperate affiliation name", existingAffilations.get(0).getAffiliationName());
        assertEquals("Yale University", existingAffilations.get(1).getAffiliationName());
        assertEquals("extra affiliation", existingAffilations.get(2).getAffiliationName());

        assertEquals(Visibility.PRIVATE, existingAffilations.get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, existingAffilations.get(1).getVisibility());
        assertEquals(Visibility.PUBLIC, existingAffilations.get(2).getVisibility());

    }

    @Test
    public void testUpdatedContactDetailsToExistingPreservingVisibility() throws Exception {

        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        ContactDetails existingContactDetails = existingOrcidBioProtected.getContactDetails();
        ContactDetails updatedContactDetails = updatedOrcidBioPublic.getContactDetails();

        assertEquals("josiah_carberry@brown.edu", existingContactDetails.retrievePrimaryEmail().getValue());
        assertEquals(Visibility.LIMITED, existingContactDetails.retrievePrimaryEmail().getVisibility());
        String[] alternativeEmails = new String[] { "josiah_carberry_1@brown.edu", "josiah_carberry_2@brown.edu" };
        for (String alternativeEmail : alternativeEmails) {
            Email email = existingContactDetails.getEmailByString(alternativeEmail);
            assertNotNull(email);
            assertEquals(Visibility.LIMITED, email.getVisibility());
        }
        assertEquals(3, existingContactDetails.getEmail().size());

        Address existingAddress = existingContactDetails.getAddress();
        assertTrue("US".equals(existingAddress.getCountry().getContent()) && existingAddress.getCountry().getVisibility() == null);

        Address updatedAddress = new Address();
        Country country = new Country("UK");
        country.setVisibility(Visibility.LIMITED);
        updatedAddress.setCountry(country);
        updatedOrcidBioPublic.getContactDetails().setAddress(updatedAddress);

        List<Email> updatedEmailList = new ArrayList<>();
        Email updatedMainEmail = new Email("jimmyb@semantico.com");
        updatedMainEmail.setVisibility(Visibility.PUBLIC);
        updatedEmailList.add(updatedMainEmail);
        String[] updatedAlternativeEmails = new String[] { "jimmyb1@semantico.com", "jimmyb2@semantico.com" };
        for (String alternativeEmail : updatedAlternativeEmails) {
            Email email = new Email(alternativeEmail);
            email.setPrimary(false);
            email.setVerified(false);
            email.setVisibility(Visibility.PRIVATE);
            updatedEmailList.add(email);
        }
        updatedContactDetails.getEmail().clear();
        updatedContactDetails.getEmail().addAll(updatedEmailList);

        updatedContactDetails.setAddress(updatedAddress);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingContactDetails = existingOrcidBioProtected.getContactDetails();
        assertEquals("jimmyb@semantico.com", existingContactDetails.retrievePrimaryEmail().getValue());
        assertEquals(Visibility.PUBLIC, existingContactDetails.retrievePrimaryEmail().getVisibility());

        for (String alternativeEmail : updatedAlternativeEmails) {
            Email email = existingContactDetails.getEmailByString(alternativeEmail);
            assertNotNull(email);
            assertEquals(Visibility.PRIVATE, email.getVisibility());
        }
        assertEquals(3, existingContactDetails.getEmail().size());
        assertEquals("UK", existingContactDetails.getAddress().getCountry().getContent());
        assertEquals(Visibility.LIMITED, existingContactDetails.getAddress().getCountry().getVisibility());

        updatedContactDetails = new ContactDetails();
        updatedOrcidBioPublic.setContactDetails(updatedContactDetails);
        updatedAddress = new Address();
        country = new Country("AU");
        country.setVisibility(null);
        updatedAddress.setCountry(country);
        updatedContactDetails.setAddress(updatedAddress);

        updatedEmailList = new ArrayList<>();
        updatedMainEmail = new Email("jimmyb2@semantico.com");
        updatedMainEmail.setVisibility(Visibility.PUBLIC);
        updatedEmailList.add(updatedMainEmail);

        String[] moreAlternativeEmails = new String[] { "jimmyb3@semantico.com", "jimmyb4@semantico.com" };
        for (String alternativeEmail : moreAlternativeEmails) {
            Email email = new Email(alternativeEmail);
            email.setPrimary(false);
            email.setVisibility(Visibility.PRIVATE);
            updatedEmailList.add(email);
        }
        updatedContactDetails.getEmail().clear();
        updatedContactDetails.getEmail().addAll(updatedEmailList);

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);

        existingContactDetails = existingOrcidBioProtected.getContactDetails();
        assertEquals("jimmyb2@semantico.com", existingContactDetails.retrievePrimaryEmail().getValue());
        assertEquals(Visibility.PUBLIC, existingContactDetails.retrievePrimaryEmail().getVisibility());

        for (String alternativeEmail : moreAlternativeEmails) {
            Email email = existingContactDetails.getEmailByString(alternativeEmail);
            assertNotNull(email);
            assertEquals(Visibility.PRIVATE, email.getVisibility());
        }
        // There's now 4 because can't remove private emails
        assertEquals(4, existingContactDetails.getEmail().size());

        assertEquals("AU", existingContactDetails.getAddress().getCountry().getContent());
    }

    @Test
    public void testUpdatedKeywordsPreservingVisibility() throws Exception {
        OrcidBio existingOrcidBioProtected = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio updatedOrcidBioPublic = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        Keywords existingKeywords = existingOrcidBioProtected.getKeywords();
        Keywords updatedKeywords = updatedOrcidBioPublic.getKeywords();

        // set keywords to limited on existing and null on updated
        // -should remain as limited and not 'downgrade'
        // -should remain as limited and not 'downgrade'

        assertEquals("Bilocation", existingKeywords.getKeyword().get(0).getContent());
        assertEquals("Pavement Studies", updatedKeywords.getKeyword().get(0).getContent());
        assertEquals(Visibility.PUBLIC, existingKeywords.getVisibility());
        assertNull(updatedKeywords.getVisibility());

        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        existingKeywords = existingOrcidBioProtected.getKeywords();
        assertEquals("Pavement Studies", existingKeywords.getKeyword().get(0).getContent());
        assertEquals(Visibility.PUBLIC, existingKeywords.getVisibility());

        assertEquals("Pavement Studies", existingKeywords.getKeyword().get(0).getContent());
        updatedKeywords.getKeyword().get(0).setContent("Toast Studies");
        updatedKeywords.setVisibility(Visibility.LIMITED);
        OrcidJaxbCopyUtils.copyUpdatedBioToExistingWithVisibility(existingOrcidBioProtected, updatedOrcidBioPublic);
        assertEquals("Toast Studies", existingKeywords.getKeyword().get(0).getContent());
        assertEquals(Visibility.LIMITED, existingKeywords.getVisibility());

    }

    @Test
    public void testCopyUpdatedExternalIdentifiersToExistingPreservingVisibility() throws Exception {
        OrcidBio protectedOrcidBio = protectedOrcidMessage.getOrcidProfile().getOrcidBio();
        OrcidBio publicOrcidBio = publicOrcidMessage.getOrcidProfile().getOrcidBio();

        assertNull(publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PUBLIC, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // first time save upgrades null to public
        OrcidJaxbCopyUtils.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.PUBLIC, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PUBLIC, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // now changing the updated one propogates change
        protectedOrcidBio.getExternalIdentifiers().setVisibility(Visibility.LIMITED);
        OrcidJaxbCopyUtils.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.LIMITED, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.LIMITED, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // when existing is null - set to the value if existing
        publicOrcidBio.getExternalIdentifiers().setVisibility(null);
        protectedOrcidBio.getExternalIdentifiers().setVisibility(Visibility.LIMITED);
        OrcidJaxbCopyUtils.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.LIMITED, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.LIMITED, protectedOrcidBio.getExternalIdentifiers().getVisibility());

        // when existing is null - defaults to public and protected update
        // ignored?
        protectedOrcidBio.getExternalIdentifiers().setVisibility(Visibility.PRIVATE);
        OrcidJaxbCopyUtils.copyUpdatedExternalIdentifiersToExistingPreservingVisibility(publicOrcidBio, protectedOrcidBio);
        assertEquals(Visibility.PRIVATE, publicOrcidBio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PRIVATE, protectedOrcidBio.getExternalIdentifiers().getVisibility());

    }

    @Test
    public void testCopyUpdatedWorksToExistingWithVisibility() throws Exception {
        OrcidWorks worksToUpdate = publicOrcidMessage.getOrcidProfile().retrieveOrcidWorks();
        OrcidWorks existingWorks = protectedOrcidMessage.getOrcidProfile().retrieveOrcidWorks();
        checkWorksVisibility(null, worksToUpdate.getOrcidWork());

        // update is private, so the update should persist and alter the
        // existing as well

        assertEquals("Work title 1", existingWorks.getOrcidWork().get(0).getWorkTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC, existingWorks.getOrcidWork().get(0).getVisibility());
        assertEquals("Work title 2", existingWorks.getOrcidWork().get(1).getWorkTitle().getTitle().getContent());
        assertNull(existingWorks.getOrcidWork().get(1).getVisibility());
        assertEquals("Work Title 3", existingWorks.getOrcidWork().get(2).getWorkTitle().getTitle().getContent());
        assertNull(existingWorks.getOrcidWork().get(2).getVisibility());

        worksToUpdate.getOrcidWork().get(0).getWorkTitle().getTitle().setContent("updated-work-title-1");
        worksToUpdate.getOrcidWork().get(0).setVisibility(Visibility.PRIVATE);
        worksToUpdate.getOrcidWork().get(1).getWorkTitle().getTitle().setContent("updated-work-title-2");
        worksToUpdate.getOrcidWork().get(2).getWorkTitle().getTitle().setContent("updated-work-title-3");

        OrcidJaxbCopyUtils.copyUpdatedWorksVisibilityInformationOnlyPreservingVisbility(existingWorks, worksToUpdate);
        assertEquals("updated-work-title-1", existingWorks.getOrcidWork().get(0).getWorkTitle().getTitle().getContent());
        assertEquals("updated-work-title-2", existingWorks.getOrcidWork().get(1).getWorkTitle().getTitle().getContent());
        assertEquals("updated-work-title-3", existingWorks.getOrcidWork().get(2).getWorkTitle().getTitle().getContent());
        // check the update was copied across
        assertEquals(Visibility.PRIVATE, existingWorks.getOrcidWork().get(0).getVisibility());
        // check that as null was supplied as an update, the works default was
        // used
        assertEquals(Visibility.PUBLIC, existingWorks.getOrcidWork().get(1).getVisibility());
    }

    private void checkWorksVisibility(Visibility expectedVisibility, Collection<OrcidWork> works) {
        for (OrcidWork work : works) {
            assertEquals("Wrong visibility for work : " + work.getWorkTitle().getTitle().getContent(), expectedVisibility, work.getVisibility());
        }
    }

    private OrcidMessage getOrcidMessage(String s) throws JAXBException {
        InputStream inputStream = OrcidJaxbCopyUtilsTest.class.getResourceAsStream(s);
        return (OrcidMessage) unmarshaller.unmarshal(inputStream);

    }

}
