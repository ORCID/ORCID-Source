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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.OrcidSearchManagerImpl;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.GrantNumber;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidGrant;
import org.orcid.jaxb.model.message.OrcidGrants;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidPatent;
import org.orcid.jaxb.model.message.OrcidPatents;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PatentNumber;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrResult;
import org.springframework.test.annotation.Rollback;

/**
 * Tests for the invocation of Solr retrieval. This class isn't required to have
 * a Solr instance running as it uses Mockito. The purpose of these tests are to
 * check the inner mappings of the search manager return an OrcidMessage
 * instance mapped from a SolrDocument.
 * 
 * @see SolrDao
 * @see SolrDocument
 * @see OrcidMessage
 * 
 * @author jamesb
 * 
 */
public class OrcidSearchManagerImplTest extends BaseTest {

    @Resource
    private OrcidSearchManagerImpl orcidSearchManager;

    @Mock
    private SolrDao solrDao;

    @Mock
    private OrcidProfileManager orcidProfileManager;

    @Before
    public void initMocks() {
        orcidSearchManager.setSolrDao(solrDao);
        orcidSearchManager.setOrcidProfileManager(orcidProfileManager);
    }

    @Test
    @Rollback
    public void orcidRetrievalAllDataPresentInDb() throws Exception {
        when(solrDao.findByOrcid("1434")).thenReturn(getSolrRes5678());
        when(orcidProfileManager.retrieveClaimedOrcidProfile("5678")).thenReturn(getOrcidProfileAllIndexFieldsPopulated());

        String orcid = "1434";

        // demonstrate that the mapping from solr (profile 1234) and dao (5678)
        // are truly seperate - the search results only return a subset of the full orcid
        // because we want to keep the payload down.

        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidSearchResultsById(orcid);
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);

        OrcidSearchResult result = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        assertTrue(new Float(37.2).compareTo(result.getRelevancyScore().getValue()) == 0);

        OrcidProfile retrievedProfile = result.getOrcidProfile();
        assertEquals("5678", retrievedProfile.getOrcid().getValue());
        OrcidBio orcidBio = retrievedProfile.getOrcidBio();
        assertEquals("Logan", orcidBio.getPersonalDetails().getFamilyName().getContent());
        assertEquals("Donald Edward", orcidBio.getPersonalDetails().getGivenNames().getContent());
        assertEquals("Stanley Higgins", orcidBio.getPersonalDetails().getCreditName().getContent());
        List<String> otherNames = orcidBio.getPersonalDetails().getOtherNames().getOtherNamesAsStrings();
        assertTrue(otherNames.contains("Edward Bass"));
        assertTrue(otherNames.contains("Gareth Dove"));

        Affiliation primary = orcidBio.getAffiliationsByType(AffiliationType.CURRENT_PRIMARY_INSTITUTION).get(0);
        Affiliation current = orcidBio.getAffiliationsByType(AffiliationType.CURRENT_INSTITUTION).get(0);
        Affiliation past = orcidBio.getAffiliationsByType(AffiliationType.PAST_INSTITUTION).get(0);

        assertEquals("Primary Institution", primary.getAffiliationName());
        assertEquals("Current Institution", current.getAffiliationName());
        assertEquals("Past Institution", past.getAffiliationName());

        OrcidWorks orcidWorks = retrievedProfile.retrieveOrcidWorks();
        OrcidWork orcidWork1 = orcidWorks.getOrcidWork().get(0);
        OrcidWork orcidWork2 = orcidWorks.getOrcidWork().get(1);

        assertTrue(orcidWork1.getWorkExternalIdentifiers().getWorkExternalIdentifier().size() == 1);
        assertEquals("work1-doi1", orcidWork1.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());

        assertTrue(orcidWork2.getWorkExternalIdentifiers().getWorkExternalIdentifier().size() == 2);
        assertEquals("work2-doi1", orcidWork2.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals("work2-doi2", orcidWork2.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(1).getWorkExternalIdentifierId().getContent());

        List<OrcidPatent> orcidPatents = retrievedProfile.retrieveOrcidPatents().getOrcidPatent();
        assertTrue(orcidPatents.size() == 2);
        OrcidPatent retrievedPatent1 = orcidPatents.get(0);
        OrcidPatent retrievedPatent2 = orcidPatents.get(1);

        //check returns a reduced payload
        assertEquals("patent1", retrievedPatent1.getPatentNumber().getContent());
        assertEquals("Patent 1 - a short description", retrievedPatent1.getShortDescription());
        assertNull(retrievedPatent1.getPutCode());

        assertEquals("patent2", retrievedPatent2.getPatentNumber().getContent());
        assertEquals("Patent 2 - a short description", retrievedPatent2.getShortDescription());
        assertNull(retrievedPatent2.getPutCode());

        List<OrcidGrant> orcidGrants = retrievedProfile.retrieveOrcidGrants().getOrcidGrant();
        OrcidGrant retrievedGrant1 = orcidGrants.get(0);
        OrcidGrant retrievedGrant2 = orcidGrants.get(1);

        //check returns a reduced payload
        assertEquals("grant1", retrievedGrant1.getGrantNumber().getContent());
        assertEquals("Grant 1 - a short description", retrievedGrant1.getShortDescription());
        assertNull(retrievedGrant1.getPutCode());

        assertEquals("grant2", retrievedGrant2.getGrantNumber().getContent());
        assertEquals("Grant 2 - a short description", retrievedGrant2.getShortDescription());
        assertNull(retrievedGrant2.getPutCode());

    }

    @Test
    @Rollback
    public void orcidRetrievalMandatoryFieldsOnly() {

        when(solrDao.findByOrcid("1434")).thenReturn(getSolrRes5678());
        when(orcidProfileManager.retrieveClaimedOrcidProfile("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidSearchResultsById("1434");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);

        OrcidSearchResult result = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile retrievedProfile = result.getOrcidProfile();
        assertEquals("5678", retrievedProfile.getOrcid().getValue());
        OrcidBio orcidBio = retrievedProfile.getOrcidBio();
        assertEquals("Logan", orcidBio.getPersonalDetails().getFamilyName().getContent());
        assertEquals("Donald Edward", orcidBio.getPersonalDetails().getGivenNames().getContent());
    }

    @Test
    @Rollback
    public void orcidInIndexButNotinDb() {

        when(solrDao.findByOrcid("1434")).thenReturn(getSolrRes5678());
        when(orcidProfileManager.retrieveClaimedOrcidProfile("5678")).thenReturn(null);
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidSearchResultsById("1434");
        assertNotNull(retrievedOrcidMessage);
        assertNotNull(retrievedOrcidMessage.getOrcidSearchResults());
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty());
    }

    @Test
    @Rollback
    public void oneOrcidInDbOtherMissing() {

        when(solrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(multipleResultsForQuery());
        when(orcidProfileManager.retrieveClaimedOrcidProfile("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        when(orcidProfileManager.retrieveClaimedOrcidProfile("6789")).thenReturn(null);
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidsByQuery("rndQuery");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults() != null && retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);
        OrcidSearchResult searchResult = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile profileReturnedFromSearch = searchResult.getOrcidProfile();
        assertEquals("5678", profileReturnedFromSearch.getOrcid().getValue());
    }

    private OrcidProfile getOrcidProfile5678MandatoryOnly() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("5678");
        OrcidBio orcidBio = new OrcidBio();
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName("Logan"));
        personalDetails.setGivenNames(new GivenNames("Donald Edward"));
        Affiliation primaryInstitution = new Affiliation();
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
    }

    private OrcidProfile getOrcidProfile6789MandatoryOnly() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("6789");
        OrcidBio orcidBio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email("don@semantico.com"));
        orcidBio.setContactDetails(contactDetails);
        PersonalDetails personalDetails = new PersonalDetails();

        personalDetails.setFamilyName(new FamilyName("Thomson"));
        personalDetails.setGivenNames(new GivenNames("Homer J"));

        orcidBio.getAffiliations().add(createPastAffiliation());
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
    }

    /**
     * 
     */
    @Test
    public void orcidMultipleOrcidsIndexed() {

        when(solrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(multipleResultsForQuery());
        when(orcidProfileManager.retrieveClaimedOrcidProfile("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        when(orcidProfileManager.retrieveClaimedOrcidProfile("6789")).thenReturn(getOrcidProfile6789MandatoryOnly());
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidsByQuery("rndQuery");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 2);

        OrcidSearchResult result = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile retrievedProfile = result.getOrcidProfile();
        assertEquals("5678", retrievedProfile.getOrcid().getValue());
        OrcidBio orcidBio = retrievedProfile.getOrcidBio();
        assertEquals("Logan", orcidBio.getPersonalDetails().getFamilyName().getContent());
        assertEquals("Donald Edward", orcidBio.getPersonalDetails().getGivenNames().getContent());
        assertNull(orcidBio.getContactDetails());

        OrcidSearchResult result2 = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(1);

        OrcidProfile retrievedProfile2 = result2.getOrcidProfile();
        assertEquals("6789", retrievedProfile2.getOrcid().getValue());
        OrcidBio orcidBio2 = retrievedProfile2.getOrcidBio();
        assertEquals("Thomson", orcidBio2.getPersonalDetails().getFamilyName().getContent());
        assertEquals("Homer J", orcidBio2.getPersonalDetails().getGivenNames().getContent());
        assertNotNull(orcidBio2.getContactDetails());
        assertEquals("don@semantico.com", orcidBio2.getContactDetails().retrievePrimaryEmail().getValue());
    }

    private OrcidSolrResult getSolrRes5678() {
        OrcidSolrResult solrResult = new OrcidSolrResult();
        solrResult.setOrcid("5678");
        solrResult.setRelevancyScore(new Float(37.2));
        return solrResult;
    }

    private OrcidSolrResult getSolrRes6789() {
        OrcidSolrResult solrResult = new OrcidSolrResult();
        solrResult.setOrcid("6789");
        solrResult.setRelevancyScore(new Float(52.2));
        return solrResult;
    }

    private List<OrcidSolrResult> multipleResultsForQuery() {
        OrcidSolrResult orcidRes5678 = getSolrRes5678();
        OrcidSolrResult orcidRes6789 = getSolrRes6789();
        return Arrays.asList(new OrcidSolrResult[] { orcidRes5678, orcidRes6789 });
    }

    private OrcidProfile getOrcidProfileAllIndexFieldsPopulated() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcid("5678");

        OrcidBio orcidBio = new OrcidBio();
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName("Logan"));
        personalDetails.setGivenNames(new GivenNames("Donald Edward"));
        personalDetails.setCreditName(new CreditName("Stanley Higgins"));
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherName().add(new OtherName("Edward Bass"));
        otherNames.getOtherName().add(new OtherName("Gareth Dove"));
        personalDetails.setOtherNames(otherNames);
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);

        orcidBio.getAffiliations().add(createPastAffiliation());
        orcidBio.getAffiliations().add(createCurrentAffiliation());
        orcidBio.getAffiliations().add(createCurrentPrimaryAffiliation());

        OrcidWorks orcidWorks = new OrcidWorks();
        OrcidWork orcidWork1 = new OrcidWork();
        OrcidWork orcidWork2 = new OrcidWork();
        assignWorkIdentifers(orcidWork1, orcidWork2);

        orcidWorks.getOrcidWork().add(orcidWork1);
        orcidWorks.getOrcidWork().add(orcidWork2);

        OrcidPatents orcidPatents = new OrcidPatents();
        OrcidPatent orcidPatent1 = new OrcidPatent();
        orcidPatent1.setPatentNumber(new PatentNumber("patent1"));
        orcidPatent1.setShortDescription("Patent 1 - a short description");
        orcidPatent1.setPutCode("patent 1 - put-code");

        OrcidPatent orcidPatent2 = new OrcidPatent();
        orcidPatent2.setPatentNumber(new PatentNumber("patent2"));
        orcidPatent2.setShortDescription("Patent 2 - a short description");
        orcidPatent2.setPutCode("patent 2 - put-code");

        orcidPatents.getOrcidPatent().add(orcidPatent1);
        orcidPatents.getOrcidPatent().add(orcidPatent2);
        orcidProfile.setOrcidPatents(orcidPatents);
        orcidProfile.setOrcidWorks(orcidWorks);

        OrcidGrants orcidGrants = new OrcidGrants();
        OrcidGrant orcidGrant1 = new OrcidGrant();
        orcidGrant1.setGrantNumber(new GrantNumber("grant1"));
        orcidGrant1.setShortDescription("Grant 1 - a short description");
        orcidGrant1.setPutCode("grant 1 - put-code");

        OrcidGrant orcidGrant2 = new OrcidGrant();
        orcidGrant2.setGrantNumber(new GrantNumber("grant2"));
        orcidGrant2.setShortDescription("Grant 2 - a short description");
        orcidGrant2.setPutCode("grant 2 - put-code");

        orcidGrants.getOrcidGrant().add(orcidGrant1);
        orcidGrants.getOrcidGrant().add(orcidGrant2);
        orcidProfile.setOrcidGrants(orcidGrants);
        orcidProfile.setOrcidWorks(orcidWorks);

        return orcidProfile;
    }

    private void assignWorkIdentifers(OrcidWork orcidWork1, OrcidWork orcidWork2) {
        WorkExternalIdentifiers work1ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work1ExternalIdentifier1 = new WorkExternalIdentifier();
        work1ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work1ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work1-doi1"));
        work1ExternalIdentifiers.getWorkExternalIdentifier().add(work1ExternalIdentifier1);
        orcidWork1.setWorkExternalIdentifiers(work1ExternalIdentifiers);

        WorkExternalIdentifiers work2ExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier work2ExternalIdentifier1 = new WorkExternalIdentifier();
        work2ExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work2ExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work2-doi1"));
        WorkExternalIdentifier work2ExternalIdentifier2 = new WorkExternalIdentifier();
        work2ExternalIdentifier2.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        work2ExternalIdentifier2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work2-doi2"));
        work2ExternalIdentifiers.getWorkExternalIdentifier().add(work2ExternalIdentifier1);
        work2ExternalIdentifiers.getWorkExternalIdentifier().add(work2ExternalIdentifier2);
        orcidWork2.setWorkExternalIdentifiers(work2ExternalIdentifiers);

    }

    private Affiliation createPastAffiliation() {
        return createAffiliation(AffiliationType.PAST_INSTITUTION, "Past Institution");
    }

    private Affiliation createCurrentAffiliation() {
        return createAffiliation(AffiliationType.CURRENT_INSTITUTION, "Current Institution");
    }

    private Affiliation createCurrentPrimaryAffiliation() {
        return createAffiliation(AffiliationType.CURRENT_PRIMARY_INSTITUTION, "Primary Institution");
    }

    private Affiliation createAffiliation(AffiliationType affiliationType, String instName) {
        Affiliation affiliation = new Affiliation();
        affiliation.setAffiliationType(affiliationType);
        affiliation.setAffiliationName(instName);
        affiliation.setRoleTitle("A Role");
        Address address = new Address();
        address.setCountry(new Country("United Kingdom"));
        affiliation.setAddress(address);
        return affiliation;
    }
}
