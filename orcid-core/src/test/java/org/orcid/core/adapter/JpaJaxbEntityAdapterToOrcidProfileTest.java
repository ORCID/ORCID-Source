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
package org.orcid.core.adapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.ApplicationSummary;
import org.orcid.jaxb.model.message.Applications;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidGrant;
import org.orcid.jaxb.model.message.OrcidGrants;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidPatent;
import org.orcid.jaxb.model.message.OrcidPatents;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PatentContributors;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * orcid-persistence - Dec 7, 2011 - JpaJaxbEntityAdapterTest
 * 
 * @author Declan Newman (declan)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbEntityAdapterToOrcidProfileTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml",
            "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml");

    @Autowired
    private GenericDao<ProfileEntity, String> profileDao;

    @Autowired
    private JpaJaxbEntityAdapter adapter;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles, null);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testToOrcidProfile() throws SAXException, IOException {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4443");
        long start = System.currentTimeMillis();
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        System.out.println("Took: " + Long.toString(System.currentTimeMillis() - start));

        checkOrcidProfile(orcidProfile);
        validateAgainstSchema(new OrcidMessage(orcidProfile));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testToOrcidProfileWithNewWayOfDoingEmails() throws SAXException, IOException {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4445");
        long start = System.currentTimeMillis();
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        System.out.println("Took: " + Long.toString(System.currentTimeMillis() - start));

        ContactDetails contactDetails = orcidProfile.getOrcidBio().getContactDetails();

        Email primaryEmail = contactDetails.retrievePrimaryEmail();
        assertNotNull(primaryEmail);
        assertTrue(primaryEmail.isPrimary());
        assertTrue(primaryEmail.isCurrent());
        assertFalse(primaryEmail.isVerified());
        assertEquals(Visibility.PRIVATE, primaryEmail.getVisibility());
        assertEquals("andrew@timothy.com", primaryEmail.getValue());
        assertEquals("4444-4444-4444-4441", primaryEmail.getSource());

        Email nonPrimaryEmail = contactDetails.getEmailByString("andrew2@timothy.com");
        assertNotNull(nonPrimaryEmail);
        assertFalse(nonPrimaryEmail.isPrimary());
        assertFalse(nonPrimaryEmail.isCurrent());
        assertFalse(nonPrimaryEmail.isVerified());
        assertEquals(Visibility.PRIVATE, nonPrimaryEmail.getVisibility());
        assertEquals("andrew2@timothy.com", nonPrimaryEmail.getValue());
        assertEquals("4444-4444-4444-4441", nonPrimaryEmail.getSource());

        assertEquals(2, contactDetails.getEmail().size());

        validateAgainstSchema(new OrcidMessage(orcidProfile));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testToOrcidProfileWithNewWayOfDoingWorkContributors() {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4445");
        long start = System.currentTimeMillis();
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        System.out.println("Took: " + Long.toString(System.currentTimeMillis() - start));

        List<OrcidWork> worksList = orcidProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertEquals(1, worksList.size());
        OrcidWork orcidWork = worksList.get(0);
        WorkContributors contributors = orcidWork.getWorkContributors();
        assertNotNull(contributors);
        List<Contributor> contributorList = contributors.getContributor();
        assertEquals(1, contributorList.size());
        Contributor contributor = contributorList.get(0);
        assertEquals("Jason Contributor", contributor.getCreditName().getContent());
    }

    private void checkOrcidProfile(OrcidProfile orcidProfile) {
        assertNotNull(orcidProfile);
        assertNotNull(orcidProfile.getOrcidBio());
        checkOrcidProfile(orcidProfile.getOrcidBio());

        assertNotNull(orcidProfile.getOrcidHistory());
        checkOrcidHistory(orcidProfile.getOrcidHistory());

        assertNotNull(orcidProfile.retrieveOrcidWorks());
        checkOrcidWorks(orcidProfile.retrieveOrcidWorks());
        checkOrcidGrants(orcidProfile.retrieveOrcidGrants());
        checkOrcidPatents(orcidProfile.retrieveOrcidPatents());
        assertEquals("4444-4444-4444-4443", orcidProfile.getOrcid().getValue());

        assertNotNull(orcidProfile.getOrcidInternal());
        checkOrcidInternal(orcidProfile.getOrcidInternal());
    }

    private void checkOrcidWorks(OrcidWorks orcidWorks) {
        assertNotNull(orcidWorks);
        List<OrcidWork> orcidWorkList = orcidWorks.getOrcidWork();
        assertEquals(2, orcidWorkList.size());
        OrcidWork orcidWork = orcidWorkList.get(0);
        assertEquals(Visibility.LIMITED, orcidWork.getVisibility());
        assertEquals("1", orcidWork.getPutCode());
        Citation workCitation = orcidWork.getWorkCitation();
        assertNotNull(workCitation);
        assertEquals("Bobby Ewing, ", workCitation.getCitation());
        assertEquals(CitationType.FORMATTED_IEEE, workCitation.getWorkCitationType());
        WorkContributors contributors = orcidWork.getWorkContributors();
        assertNotNull(contributors);
        assertEquals(2, contributors.getContributor().size());
        assertEquals("Jaylen Kessler", contributors.getContributor().get(0).getCreditName().getContent());
        assertEquals(Visibility.LIMITED, contributors.getContributor().get(0).getCreditName().getVisibility());
    }

    private void checkOrcidGrants(OrcidGrants orcidGrants) {
        assertNotNull(orcidGrants);
        List<OrcidGrant> orcidGrantList = orcidGrants.getOrcidGrant();
        assertEquals(2, orcidGrantList.size());
    }

    private void checkOrcidPatents(OrcidPatents orcidPatents) {
        assertNotNull(orcidPatents);
        List<OrcidPatent> orcidPatentList = orcidPatents.getOrcidPatent();
        assertEquals(2, orcidPatentList.size());
        OrcidPatent orcidPatent1 = orcidPatentList.get(0);
        assertEquals("1", orcidPatent1.getPutCode());
        PatentContributors patentContributors = orcidPatent1.getPatentContributors();
        assertNotNull(patentContributors);
        List<Contributor> contributors = patentContributors.getContributor();
        assertNotNull(contributors);
        assertEquals(2, contributors.size());
        Contributor contributor1 = contributors.get(0);
        assertEquals("Jaylen Kessler", contributor1.getCreditName().getContent());
    }

    private void checkOrcidHistory(OrcidHistory orcidHistory) {
        assertNotNull(orcidHistory);
        Source sponsor = orcidHistory.getSource();
        assertNotNull(sponsor);
        assertEquals("S. Milligan", sponsor.getSourceName().getContent());
        assertEquals("4444-4444-4444-4441", sponsor.getSourceOrcid().getValue());
        assertEquals(DateUtils.convertToDate("2012-06-29T15:31:00"), orcidHistory.getDeactivationDate().getValue().toGregorianCalendar().getTime());
        assertEquals(DateUtils.convertToDate("2011-07-02T15:31:00"), orcidHistory.getLastModifiedDate().getValue().toGregorianCalendar().getTime());
    }

    private void checkOrcidProfile(OrcidBio orcidBio) {
        assertNotNull(orcidBio.getAffiliations());
        checkAffiliations(orcidBio.getAffiliations());

        checkPersonalDetails(orcidBio.getPersonalDetails());
        assertNotNull(orcidBio.getContactDetails());
        checkContactDetails(orcidBio.getContactDetails());

        assertNotNull(orcidBio.getExternalIdentifiers());
        checkExternalIdentifiers(orcidBio.getExternalIdentifiers());

        assertNotNull(orcidBio.getDelegation());
        checkDelegation(orcidBio.getDelegation());

        assertNull(orcidBio.getScope());

        assertNotNull(orcidBio.getApplications());
        checkApplications(orcidBio.getApplications());

        ResearcherUrls researcherUrls = orcidBio.getResearcherUrls();
        List<ResearcherUrl> urls = researcherUrls.getResearcherUrl();
        Collections.sort(urls);
        assertEquals(2, urls.size());
        Url url1 = urls.get(0).getUrl();
        String url1Name = urls.get(0).getUrlName().getContent();
        assertEquals(url1Name, "443_1");
        assertEquals("http://www.researcherurl2.com?id=1", url1.getValue());

        Url url2 = urls.get(1).getUrl();
        String url2Name = urls.get(1).getUrlName().getContent();
        assertEquals(url2Name, "443_2");
        assertEquals("http://www.researcherurl2.com?id=2", url2.getValue());

        checkKeywords(orcidBio.getKeywords());

    }

    private void checkKeywords(Keywords keywords) {
        assertNotNull(keywords);
        assertEquals(Visibility.LIMITED, keywords.getVisibility());
    }

    private void checkPersonalDetails(PersonalDetails personalDetails) {
        assertNotNull(personalDetails);
        FamilyName familyName = personalDetails.getFamilyName();
        assertNotNull(familyName);
        assertEquals("Sellers", familyName.getContent());

        GivenNames givenNames = personalDetails.getGivenNames();
        assertNotNull(givenNames);
        assertEquals("Peter", givenNames.getContent());

        CreditName creditName = personalDetails.getCreditName();
        assertNotNull(creditName);
        assertEquals("P. Sellers III", creditName.getContent());
        assertEquals("LIMITED", creditName.getVisibility().name());

        OtherNames otherNames = personalDetails.getOtherNames();
        assertNotNull(otherNames);
        List<String> otherNameList = otherNames.getOtherNamesAsStrings();
        Collections.sort(otherNameList);
        assertEquals(2, otherNameList.size());
        assertEquals("Flibberdy Flabinah", otherNameList.get(0));
        assertEquals("Slibberdy Slabinah", otherNameList.get(1));

    }

    private void checkAffiliations(List<Affiliation> affiliations) {
        assertNotNull(affiliations);
        assertEquals(1, affiliations.size());
        Affiliation affiliation = affiliations.get(0);
        assertEquals(Visibility.LIMITED, affiliation.getVisibility());
        assertEquals("An institution", affiliation.getAffiliationName());
        assertEquals("A Department", affiliation.getDepartmentName());
        assertEquals("2010-07-02", affiliation.getStartDate().getValue().toXMLFormat());
        assertEquals("2011-07-02", affiliation.getEndDate().getValue().toXMLFormat());
        assertEquals("Primary Researcher", affiliation.getRoleTitle());
        checkAddress(affiliation.getAddress());

    }

    private void checkExternalIdentifiers(ExternalIdentifiers externalIdentifiers) {
        assertNotNull(externalIdentifiers);
        assertEquals(Visibility.LIMITED, externalIdentifiers.getVisibility());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());
        ExternalIdentifier externalIdentifier = externalIdentifiers.getExternalIdentifier().get(0);
        assertEquals("4444-4444-4444-4441", externalIdentifier.getExternalIdOrcid().getValue());
        assertEquals("d3clan", externalIdentifier.getExternalIdReference().getContent());
        assertEquals("Facebook", externalIdentifier.getExternalIdCommonName().getContent());
    }

    private void checkDelegation(Delegation delegation) {
        assertNotNull(delegation);
        assertEquals(1, delegation.getGivenPermissionTo().getDelegationDetails().size());
        DelegationDetails givenPermssionToDetails = delegation.getGivenPermissionTo().getDelegationDetails().iterator().next();
        assertEquals("4444-4444-4444-4446", givenPermssionToDetails.getDelegateSummary().getOrcid().getValue());
        assertTrue(givenPermssionToDetails.getApprovalDate().getValue().toXMLFormat().startsWith("2011-10-17T16:59:32.000"));
        assertEquals(1, delegation.getGivenPermissionBy().getDelegationDetails().size());
        DelegationDetails givenPermissionByDetails = delegation.getGivenPermissionBy().getDelegationDetails().iterator().next();
        assertEquals("4444-4444-4444-4441", givenPermissionByDetails.getDelegateSummary().getOrcid().getValue());
        assertTrue(givenPermissionByDetails.getApprovalDate().getValue().toXMLFormat().startsWith("2011-08-24T11:28:16.000"));
    }

    private void checkContactDetails(ContactDetails contactDetails) {
        assertNotNull(contactDetails);

        List<Email> emails = contactDetails.getEmail();
        assertEquals(4, emails.size());
        Map<String, Email> emailMap = new HashMap<>();
        for (Email email : emails) {
            emailMap.put(email.getValue(), email);
        }

        Email primaryEmail = emailMap.get("peter@sellers.com");
        assertNotNull(primaryEmail);
        assertEquals("PRIVATE", primaryEmail.getVisibility().name());
        assertTrue(primaryEmail.isPrimary());
        assertTrue(primaryEmail.isCurrent());
        assertFalse(primaryEmail.isVerified());

        String[] alternativeEmails = new String[] { "teddybass@semantico.com", "teddybass2@semantico.com" };
        for (String alternativeEmail : alternativeEmails) {
            Email email = emailMap.get(alternativeEmail);
            assertNotNull(email);
            assertEquals("PRIVATE", email.getVisibility().name());
            assertFalse(email.isPrimary());
            assertTrue(email.isCurrent());
            assertFalse(email.isVerified());
        }

        Address contactDetailsAddress = contactDetails.getAddress();
        assertNotNull(contactDetailsAddress);
        assertEquals(Visibility.LIMITED, contactDetailsAddress.getCountry().getVisibility());

    }

    private void checkAddress(Address address) {
        assertNotNull(address);
        assertEquals("England", address.getCountry().getContent());
    }

    private void checkApplications(Applications applications) {
        assertEquals(2, applications.getApplicationSummary().size());
        Map<String, ApplicationSummary> applicationsMappedByOrcid = Maps.uniqueIndex(applications.getApplicationSummary(), new Function<ApplicationSummary, String>() {
            public String apply(ApplicationSummary applicationSummary) {
                return applicationSummary.getApplicationOrcid().getValue();
            }
        });
        ApplicationSummary application1 = applicationsMappedByOrcid.get("4444-4444-4444-4441");
        assertNotNull(application1);
        assertEquals("S. Milligan", application1.getApplicationName().getContent());
        assertEquals("www.4444-4444-4444-4441.com", application1.getApplicationWebsite().getValue());
        assertEquals(DateUtils.convertToDate("2012-07-23T08:16:00"), application1.getApprovalDate().getValue().toGregorianCalendar().getTime());
    }

    private void checkOrcidInternal(OrcidInternal orcidInternal) {
        SecurityDetails securityDetails = orcidInternal.getSecurityDetails();
        assertNotNull(securityDetails);
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=", securityDetails
                .getEncryptedPassword().getContent());
        assertEquals(1, securityDetails.getSecurityQuestionId().getValue());
        assertEquals("iTlIoR2JsFl5guE56cazmg==", securityDetails.getEncryptedSecurityAnswer().getContent());
        assertEquals("1vLkD2Lm8c24TyALcW0Brg==", securityDetails.getEncryptedVerificationCode().getContent());
        Preferences preferences = orcidInternal.getPreferences();
        assertNotNull(preferences);
        assertTrue(preferences.getSendChangeNotifications().isValue());
        assertFalse(preferences.getSendOrcidNews().isValue());
    }

    private void validateAgainstSchema(OrcidMessage orcidMessage) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(getClass().getResource("/orcid-message-" + OrcidMessage.DEFAULT_VERSION + ".xsd"));
        Validator validator = schema.newValidator();
        validator.validate(orcidMessage.toSource());
    }

}
