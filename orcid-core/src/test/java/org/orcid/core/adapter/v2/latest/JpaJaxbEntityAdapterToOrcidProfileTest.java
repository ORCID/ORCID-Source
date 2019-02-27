package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Citation;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

/**
 * orcid-persistence - Dec 7, 2011 - JpaJaxbEntityAdapterTest
 * 
 * @author Declan Newman (declan)
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbEntityAdapterToOrcidProfileTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml",
            "/data/OrgAffiliationEntityData.xml");

    @Autowired
    private GenericDao<ProfileEntity, String> profileDao;

    @Autowired
    private JpaJaxbEntityAdapter adapter;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testToOrcidProfile() throws SAXException, IOException {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4443");
        long start = System.currentTimeMillis();
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, LoadOptions.ALL);
        assertNotNull(orcidProfile);
        assertNotNull(orcidProfile.getOrcidBio());
        checkOrcidProfile(orcidProfile.getOrcidBio());

        assertNotNull(orcidProfile.getOrcidHistory());
        checkOrcidHistory(orcidProfile.getOrcidHistory());

        checkAffiliations(orcidProfile.getOrcidActivities().getAffiliations().getAffiliation());
        assertNotNull(orcidProfile.retrieveOrcidWorks());
        checkOrcidWorks(orcidProfile.retrieveOrcidWorks());
        checkOrcidFundings(orcidProfile.retrieveFundings());
        assertEquals("4444-4444-4444-4443", orcidProfile.getOrcidIdentifier().getPath());

        assertNotNull(orcidProfile.getOrcidInternal());
        SecurityDetails securityDetails = orcidProfile.getOrcidInternal().getSecurityDetails();
        assertNotNull(securityDetails);
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=", securityDetails
                .getEncryptedPassword().getContent());
        Preferences preferences = orcidProfile.getOrcidInternal().getPreferences();
        assertNotNull(preferences);
        assertTrue(preferences.getSendChangeNotifications().isValue());
        assertTrue(preferences.getSendOrcidNews().isValue());
        
        validateAgainstSchema(new OrcidMessage(orcidProfile));
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testToOrcidProfileWithNewWayOfDoingEmails() throws SAXException, IOException {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4445");
        long start = System.currentTimeMillis();
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, LoadOptions.ALL);
        System.out.println("Took: " + Long.toString(System.currentTimeMillis() - start));

        ContactDetails contactDetails = orcidProfile.getOrcidBio().getContactDetails();

        Email primaryEmail = contactDetails.retrievePrimaryEmail();
        assertNotNull(primaryEmail);
        assertTrue(primaryEmail.isPrimary());
        assertTrue(primaryEmail.isCurrent());
        assertFalse(primaryEmail.isVerified());
        assertEquals(Visibility.PRIVATE, primaryEmail.getVisibility());
        assertEquals("aNdReW@tImOtHy.com", primaryEmail.getValue());
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
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, LoadOptions.ALL);
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

    public void testToOrcidProfileWithDeprecationMessage() throws SAXException, IOException {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-444X");
        long start = System.currentTimeMillis();
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity, LoadOptions.ALL);
        System.out.println("Took: " + Long.toString(System.currentTimeMillis() - start));

        assertNotNull(orcidProfile.getOrcidDeprecated());
        assertNotNull(orcidProfile.getOrcidDeprecated().getDate());
        assertNotNull(orcidProfile.getOrcidDeprecated().getPrimaryRecord());
        assertNotNull(orcidProfile.getOrcidDeprecated().getPrimaryRecord().getOrcid());
        assertNotNull(orcidProfile.getOrcidDeprecated().getPrimaryRecord().getOrcidId());
        assertEquals("4444-4444-4444-4441", orcidProfile.getOrcidDeprecated().getPrimaryRecord().getOrcidIdentifier().getPath());
        assertTrue(orcidProfile.getOrcidDeprecated().getPrimaryRecord().getOrcidId().getValue().endsWith("/4444-4444-4444-4441"));
        validateAgainstSchema(new OrcidMessage(orcidProfile));
    }    

    private void checkOrcidWorks(OrcidWorks orcidWorks) {
        assertNotNull(orcidWorks);
        List<OrcidWork> orcidWorkList = orcidWorks.getOrcidWork();
        assertEquals(3, orcidWorkList.size());

        boolean putCode1Found = false;
        boolean putCode4Found = false;

        for (OrcidWork orcidWork : orcidWorkList) {
            if (orcidWork.getPutCode().equals("1")) {
                putCode1Found = true;
                assertEquals("1", orcidWork.getPutCode());
                Citation workCitation = orcidWork.getWorkCitation();
                assertNotNull(workCitation);
                assertEquals("Bobby Ewing, ", workCitation.getCitation());
                assertEquals(CitationType.FORMATTED_IEEE, workCitation.getWorkCitationType());
                WorkContributors contributors = orcidWork.getWorkContributors();
                assertNotNull(contributors);
                assertEquals(2, contributors.getContributor().size());
                assertEquals("Jaylen Kessler", contributors.getContributor().get(0).getCreditName().getContent());
                assertEquals(Visibility.PUBLIC, contributors.getContributor().get(0).getCreditName().getVisibility());
                assertEquals(Visibility.PUBLIC, orcidWork.getVisibility());
                assertNull(orcidWork.getJournalTitle());
            } else if (orcidWork.getPutCode().equals("3")) {
                WorkContributors contributors = orcidWork.getWorkContributors();
                assertNotNull(contributors);
                assertEquals(1, contributors.getContributor().size());
                assertEquals("0000-0003-0172-7925", contributors.getContributor().get(0).getContributorOrcid().getPath());
            } else if (orcidWork.getPutCode().equals("4")) {
                putCode4Found = true;
                assertNotNull(orcidWork.getWorkTitle());
                assertNotNull(orcidWork.getWorkTitle().getTitle());
                assertEquals("A book with a Journal Title", orcidWork.getWorkTitle().getTitle().getContent());
                assertNotNull(orcidWork.getJournalTitle());
                assertEquals("My Journal Title", orcidWork.getJournalTitle().getContent());
                assertNotNull(orcidWork.getPublicationDate());
                assertNotNull(orcidWork.getPublicationDate().getDay());
                assertNotNull(orcidWork.getPublicationDate().getMonth());
                assertNotNull(orcidWork.getPublicationDate().getYear());
                assertEquals("01", orcidWork.getPublicationDate().getDay().getValue());
                assertEquals("02", orcidWork.getPublicationDate().getMonth().getValue());
                assertEquals("2011", orcidWork.getPublicationDate().getYear().getValue());
                assertNotNull(orcidWork.getWorkCitation());
                assertEquals("Sue Ellen ewing", orcidWork.getWorkCitation().getCitation());
                assertEquals(CitationType.FORMATTED_IEEE, orcidWork.getWorkCitation().getWorkCitationType());
                assertNotNull(orcidWork.getWorkType());
                assertEquals(WorkType.BOOK, orcidWork.getWorkType());
            }
        }

        assertTrue(putCode1Found);
        assertTrue(putCode4Found);
    }

    private void checkOrcidFundings(FundingList orcidFunding) {
        assertNotNull(orcidFunding);
        List<Funding> orcidFundingList = orcidFunding.getFundings();
        assertEquals(3, orcidFundingList.size());
    }

    private void checkOrcidHistory(OrcidHistory orcidHistory) {
        assertNotNull(orcidHistory);
        Source sponsor = orcidHistory.getSource();
        assertNotNull(sponsor);
        assertEquals("Credit Name", sponsor.getSourceName().getContent());
        assertEquals("4444-4444-4444-4441", sponsor.retrieveSourcePath());
        assertEquals(DateUtils.convertToDate("2011-07-02T15:31:00"), orcidHistory.getLastModifiedDate().getValue().toGregorianCalendar().getTime());
        assertEquals(DateUtils.convertToDate("2011-06-29T15:31:00"), orcidHistory.getSubmissionDate().getValue().toGregorianCalendar().getTime());
        assertEquals(DateUtils.convertToDate("2011-07-02T15:31:00"), orcidHistory.getCompletionDate().getValue().toGregorianCalendar().getTime());
    }

    private void checkOrcidProfile(OrcidBio orcidBio) {

        checkPersonalDetails(orcidBio.getPersonalDetails());
        assertNotNull(orcidBio.getContactDetails());
        checkContactDetails(orcidBio.getContactDetails());

        assertNotNull(orcidBio.getExternalIdentifiers());
        checkExternalIdentifiers(orcidBio.getExternalIdentifiers());

        assertNotNull(orcidBio.getDelegation());
        checkDelegation(orcidBio.getDelegation());

        assertNull(orcidBio.getScope());
//		Applications are not linked with OrcidProfile object anymore.
//        assertNotNull(orcidBio.getApplications());
//        checkApplications(orcidBio.getApplications());

        ResearcherUrls researcherUrls = orcidBio.getResearcherUrls();
        List<ResearcherUrl> urls = researcherUrls.getResearcherUrl();
        Collections.sort(urls);
        assertEquals(6, urls.size());
        Url url1 = urls.get(0).getUrl();
        String url1Name = urls.get(0).getUrlName().getContent();
        assertEquals(url1Name, "443_1");
        assertEquals("http://www.researcherurl2.com?id=1", url1.getValue());

        Url url2 = urls.get(1).getUrl();
        String url2Name = urls.get(1).getUrlName().getContent();
        assertEquals(url2Name, "443_2");
        assertEquals("http://www.researcherurl2.com?id=2", url2.getValue());

        Url url3 = urls.get(2).getUrl();
        String url3Name = urls.get(2).getUrlName().getContent();
        assertEquals(url3Name, "443_3");
        assertEquals("http://www.researcherurl2.com?id=5", url3.getValue());
        
        Url url4 = urls.get(3).getUrl();
        String url4Name = urls.get(3).getUrlName().getContent();
        assertEquals(url4Name, "443_4");
        assertEquals("http://www.researcherurl2.com?id=6", url4.getValue());
        
        Url url5 = urls.get(4).getUrl();
        String url5Name = urls.get(4).getUrlName().getContent();
        assertEquals(url5Name, "443_5");
        assertEquals("http://www.researcherurl2.com?id=7", url5.getValue());
        
        Url url6 = urls.get(5).getUrl();
        String url6Name = urls.get(5).getUrlName().getContent();
        assertEquals(url6Name, "443_6");
        assertEquals("http://www.researcherurl2.com?id=8", url6.getValue());
        
        checkKeywords(orcidBio.getKeywords());

    }

    private void checkKeywords(Keywords keywords) {
        assertNotNull(keywords);
        assertEquals(Visibility.PRIVATE, keywords.getVisibility());
        assertNotNull(keywords.getKeyword());
        assertEquals(4, keywords.getKeyword().size());
        //They are alpha ordered
        assertEquals("chocolat making", keywords.getKeyword().get(0).getContent());
        assertEquals(Visibility.PRIVATE, keywords.getKeyword().get(0).getVisibility()); 
        
        assertEquals("coffee making", keywords.getKeyword().get(1).getContent());
        assertEquals(Visibility.LIMITED, keywords.getKeyword().get(1).getVisibility());
        
        assertEquals("tea making", keywords.getKeyword().get(2).getContent());
        assertEquals(Visibility.PUBLIC, keywords.getKeyword().get(2).getVisibility());
        
        assertEquals("what else can we make?", keywords.getKeyword().get(3).getContent());
        assertEquals(Visibility.PRIVATE, keywords.getKeyword().get(3).getVisibility());
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
        assertEquals(3, affiliations.size());
        Affiliation affiliation = affiliations.get(2);
        assertEquals(Visibility.LIMITED, affiliation.getVisibility());
        assertEquals("An institution", affiliation.getOrganization().getName());
        assertEquals("A Department", affiliation.getDepartmentName());
        assertEquals("2010-07-02", affiliation.getStartDate().toString());
        assertEquals("2011-07-02", affiliation.getEndDate().toString());
        assertEquals("Primary Researcher", affiliation.getRoleTitle());
        DisambiguatedOrganization disambiguatedAffiliation = affiliation.getOrganization().getDisambiguatedOrganization();
        assertNotNull(disambiguatedAffiliation);
        assertEquals("abc456", disambiguatedAffiliation.getDisambiguatedOrganizationIdentifier());
        assertEquals("WDB", disambiguatedAffiliation.getDisambiguationSource());

        checkAddress(affiliation.getOrganization().getAddress());

    }

    private void checkExternalIdentifiers(ExternalIdentifiers externalIdentifiers) {
        assertNotNull(externalIdentifiers);
        assertEquals(Visibility.PUBLIC, externalIdentifiers.getVisibility());
        assertEquals(1, externalIdentifiers.getExternalIdentifier().size());
        ExternalIdentifier externalIdentifier = externalIdentifiers.getExternalIdentifier().get(0);
        assertEquals("4444-4444-4444-4441", externalIdentifier.getSource().retrieveSourcePath());
        assertEquals("d3clan", externalIdentifier.getExternalIdReference().getContent());
        assertEquals("Facebook", externalIdentifier.getExternalIdCommonName().getContent());
        assertNotNull(externalIdentifier.getVisibility());
        assertNotNull("PUBLIC", externalIdentifier.getVisibility().value());
    }

    private void checkDelegation(Delegation delegation) {
        assertNotNull(delegation);
        assertEquals(1, delegation.getGivenPermissionTo().getDelegationDetails().size());
        DelegationDetails givenPermssionToDetails = delegation.getGivenPermissionTo().getDelegationDetails().iterator().next();
        assertEquals("4444-4444-4444-4446", givenPermssionToDetails.getDelegateSummary().getOrcidIdentifier().getPath());
        assertTrue(givenPermssionToDetails.getApprovalDate().getValue().toXMLFormat().startsWith("2011-10-17T16:59:32.000"));
        assertEquals(1, delegation.getGivenPermissionBy().getDelegationDetails().size());
        DelegationDetails givenPermissionByDetails = delegation.getGivenPermissionBy().getDelegationDetails().iterator().next();
        assertEquals("4444-4444-4444-4441", givenPermissionByDetails.getDelegateSummary().getOrcidIdentifier().getPath());
        assertTrue(givenPermissionByDetails.getApprovalDate().getValue().toXMLFormat().startsWith("2011-08-24T11:28:16.000"));
    }

    private void checkContactDetails(ContactDetails contactDetails) {
        assertNotNull(contactDetails);

        List<Email> emails = contactDetails.getEmail();
        assertEquals(6, emails.size());
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

        String[] alternativeEmails = new String[] { "teddybass@semantico.com", "teddybass2@semantico.com", "teddybass3public@semantico.com", "teddybass3private@semantico.com" };
        for (String alternativeEmail : alternativeEmails) {
            Email email = emailMap.get(alternativeEmail);
            assertNotNull(email);
            switch(email.getValue()) {
            case "teddybass@semantico.com":
                assertEquals("PRIVATE", email.getVisibility().name());
                assertFalse(email.isPrimary());
                assertFalse(email.isVerified());
                break;
            case "teddybass2@semantico.com":
                assertEquals("LIMITED", email.getVisibility().name());
                assertFalse(email.isPrimary());
                assertFalse(email.isVerified());
                break;
            case "teddybass3public@semantico.com":
                assertEquals("PUBLIC", email.getVisibility().name());
                assertFalse(email.isPrimary());
                assertFalse(email.isVerified());
                break;
            case "teddybass3private@semantico.com":
                assertEquals("PRIVATE", email.getVisibility().name());
                assertTrue(email.isPrimary());
                assertTrue(email.isVerified());
                break;
            }
           
            assertTrue(email.isCurrent());
        }

        Address contactDetailsAddress = contactDetails.getAddress();
        assertNotNull(contactDetailsAddress);
        assertEquals(Visibility.LIMITED, contactDetailsAddress.getCountry().getVisibility());

    }

    private void checkAddress(OrganizationAddress address) {
        assertNotNull(address);
        assertEquals(Iso3166Country.GB, address.getCountry());
    }    

    private void validateAgainstSchema(OrcidMessage orcidMessage) throws SAXException, IOException {
        //We need to manually remove the visibility from the given and family names to match the schema
        removeVisibilityAttributeFromNames(orcidMessage);
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = factory.newSchema(getClass().getResource("/orcid-message-" + OrcidMessage.DEFAULT_VERSION + ".xsd"));
        Validator validator = schema.newValidator();
        validator.validate(orcidMessage.toSource());
    }

    private void removeVisibilityAttributeFromNames(OrcidMessage orcidMessage) {
        if(orcidMessage != null && orcidMessage.getOrcidProfile() != null && orcidMessage.getOrcidProfile().getOrcidBio() != null && orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails() != null) {
            if(orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName() != null) {
                orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getFamilyName().setVisibility(null);
            }
            if(orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames() != null) {
                orcidMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames().setVisibility(null); 
            }
        }
    }
}
