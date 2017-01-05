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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.impl.OrcidSearchManagerImpl;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.OrcidSearchResults;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.OrcidIds;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.utils.solr.entities.OrcidSolrResult;
import org.orcid.utils.solr.entities.OrcidSolrResults;
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
    private OrcidProfileCacheManager orcidProfileCacheManager;

    @Before
    public void initMocks() {
        orcidSearchManager.setSolrDao(solrDao);
        orcidSearchManager.setOrcidProfileCacheManager(orcidProfileCacheManager);
    }
    
    @Test
    public void testFindOrcidIds() {
        when(solrDao.findByDocumentCriteria(Matchers.<Map<String, List<String>>>any())).thenReturn(multipleResultsForQuery());
        OrcidIds orcidIds = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(orcidIds);
        assertEquals(2, orcidIds.getOrcidIds().size());
        assertEquals("5678", orcidIds.getOrcidIds().get(0).getValue());
        assertEquals("6789", orcidIds.getOrcidIds().get(1).getValue());
    }
    
    @Test
    public void testFindOrcidIdsNoResults() {
        when(solrDao.findByDocumentCriteria(Matchers.<Map<String, List<String>>>any())).thenReturn(new OrcidSolrResults());
        OrcidIds orcidIds = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(orcidIds);
        assertEquals(0, orcidIds.getOrcidIds().size());
    }

    @Test
    @Rollback
    public void orcidRetrievalAllDataPresentInDb() throws Exception {
        when(solrDao.findByOrcid("1434")).thenReturn(getSolrRes5678());
        when(orcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfileAllIndexFieldsPopulated());

        String orcid = "1434";

        // demonstrate that the mapping from solr (profile 1234) and dao (5678)
        // are truly seperate - the search results only return a subset of the
        // full orcid
        // because we want to keep the payload down.

        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidSearchResultsById(orcid);
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);

        OrcidSearchResult result = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        assertTrue(new Float(37.2).compareTo(result.getRelevancyScore().getValue()) == 0);

        OrcidProfile retrievedProfile = result.getOrcidProfile();
        assertEquals("5678", retrievedProfile.getOrcidIdentifier().getPath());
        OrcidBio orcidBio = retrievedProfile.getOrcidBio();
        assertEquals("Logan", orcidBio.getPersonalDetails().getFamilyName().getContent());
        assertEquals("Donald Edward", orcidBio.getPersonalDetails().getGivenNames().getContent());
        assertEquals("Stanley Higgins", orcidBio.getPersonalDetails().getCreditName().getContent());
        List<String> otherNames = orcidBio.getPersonalDetails().getOtherNames().getOtherNamesAsStrings();
        assertTrue(otherNames.contains("Edward Bass"));
        assertTrue(otherNames.contains("Gareth Dove"));

        OrcidWorks orcidWorks = retrievedProfile.retrieveOrcidWorks();
        OrcidWork orcidWork1 = orcidWorks.getOrcidWork().get(0);
        OrcidWork orcidWork2 = orcidWorks.getOrcidWork().get(1);

        assertTrue(orcidWork1.getWorkExternalIdentifiers().getWorkExternalIdentifier().size() == 1);
        assertEquals("work1-doi1", orcidWork1.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());

        assertTrue(orcidWork2.getWorkExternalIdentifiers().getWorkExternalIdentifier().size() == 2);
        assertEquals("work2-doi1", orcidWork2.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals("work2-doi2", orcidWork2.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(1).getWorkExternalIdentifierId().getContent());

        List<Funding> fundings = retrievedProfile.retrieveFundings().getFundings();
        Funding funding1 = fundings.get(0);
        Funding funding2 = fundings.get(1);

        // check returns a reduced payload
        assertNotNull(funding1.getTitle());
        assertNotNull(funding1.getTitle().getTitle());
        assertEquals("grant1", funding1.getTitle().getTitle().getContent());
        assertEquals("Grant 1 - a short description", funding1.getDescription());
        assertNull(funding1.getPutCode());

        assertNotNull(funding2.getTitle());
        assertNotNull(funding2.getTitle().getTitle());
        assertEquals("grant2", funding2.getTitle().getTitle().getContent());
        assertEquals("Grant 2 - a short description", funding2.getDescription());
        assertNull(funding2.getPutCode());

    }

    @Test
    @Rollback
    public void orcidRetrievalMandatoryFieldsOnly() {

        when(solrDao.findByOrcid("1434")).thenReturn(getSolrRes5678());
        when(orcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidSearchResultsById("1434");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);

        OrcidSearchResult result = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile retrievedProfile = result.getOrcidProfile();
        assertEquals("5678", retrievedProfile.getOrcidIdentifier().getPath());
        OrcidBio orcidBio = retrievedProfile.getOrcidBio();
        assertEquals("Logan", orcidBio.getPersonalDetails().getFamilyName().getContent());
        assertEquals("Donald Edward", orcidBio.getPersonalDetails().getGivenNames().getContent());
    }

    @Test
    @Rollback
    public void orcidInIndexButNotinDb() {

        when(solrDao.findByOrcid("1434")).thenReturn(getSolrRes5678());
        when(orcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(null);
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidSearchResultsById("1434");
        assertNotNull(retrievedOrcidMessage);
        assertNotNull(retrievedOrcidMessage.getOrcidSearchResults());
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty());
    }

    @Test
    @Rollback
    public void oneOrcidInDbOtherMissing() {

        when(solrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(multipleResultsForQuery());
        when(orcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        when(orcidProfileCacheManager.retrievePublicBio("6789")).thenReturn(null);
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidsByQuery("rndQuery");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults() != null && retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);
        OrcidSearchResult searchResult = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile profileReturnedFromSearch = searchResult.getOrcidProfile();
        assertEquals("5678", profileReturnedFromSearch.getOrcidIdentifier().getPath());
    }

    private OrcidProfile getOrcidProfile5678MandatoryOnly() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier("5678");
        OrcidBio orcidBio = new OrcidBio();
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName("Logan"));
        personalDetails.setGivenNames(new GivenNames("Donald Edward"));
        new Affiliation();
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
    }

    private OrcidProfile getOrcidProfile6789MandatoryOnly() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier("6789");
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new Email("don@semantico.com"));
        orcidBio.setContactDetails(contactDetails);

        PersonalDetails personalDetails = new PersonalDetails();
        orcidBio.setPersonalDetails(personalDetails);
        personalDetails.setFamilyName(new FamilyName("Thomson"));
        personalDetails.setGivenNames(new GivenNames("Homer J"));

        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);

        return orcidProfile;
    }

    /**
     * 
     */
    @Test
    public void orcidMultipleOrcidsIndexed() {

        when(solrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(multipleResultsForQuery());
        when(orcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        when(orcidProfileCacheManager.retrievePublicBio("6789")).thenReturn(getOrcidProfile6789MandatoryOnly());
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidsByQuery("rndQuery");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 2);

        OrcidSearchResult result = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile retrievedProfile = result.getOrcidProfile();
        assertEquals("5678", retrievedProfile.getOrcidIdentifier().getPath());
        OrcidBio orcidBio = retrievedProfile.getOrcidBio();
        assertEquals("Logan", orcidBio.getPersonalDetails().getFamilyName().getContent());
        assertEquals("Donald Edward", orcidBio.getPersonalDetails().getGivenNames().getContent());
        assertNull(orcidBio.getContactDetails());

        OrcidSearchResult result2 = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(1);

        OrcidProfile retrievedProfile2 = result2.getOrcidProfile();
        assertEquals("6789", retrievedProfile2.getOrcidIdentifier().getPath());
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

    private OrcidSolrResults multipleResultsForQuery() {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> resultsList = new ArrayList<>();
        orcidSolrResults.setResults(resultsList);
        resultsList.add(getSolrRes5678());
        resultsList.add(getSolrRes6789());
        return orcidSolrResults;
    }

    private OrcidProfile getOrcidProfileAllIndexFieldsPopulated() {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier("5678");

        OrcidBio orcidBio = new OrcidBio();
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName("Logan"));
        personalDetails.setGivenNames(new GivenNames("Donald Edward"));
        personalDetails.setCreditName(new CreditName("Stanley Higgins"));
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherName().add(new OtherName("Edward Bass",null));
        otherNames.getOtherName().add(new OtherName("Gareth Dove",null));
        personalDetails.setOtherNames(otherNames);
        orcidBio.setPersonalDetails(personalDetails);
        orcidProfile.setOrcidBio(orcidBio);

        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);

        OrcidWorks orcidWorks = new OrcidWorks();
        orcidProfile.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork1 = new OrcidWork();
        OrcidWork orcidWork2 = new OrcidWork();
        assignWorkIdentifers(orcidWork1, orcidWork2);

        orcidWorks.getOrcidWork().add(orcidWork1);
        orcidWorks.getOrcidWork().add(orcidWork2);

        orcidProfile.setOrcidWorks(orcidWorks);

        FundingList orcidFundings = new FundingList();
        orcidProfile.setFundings(orcidFundings);
        Funding funding1 = new Funding();
        funding1.setVisibility(Visibility.PUBLIC);
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("grant1"));
        funding1.setTitle(title);
        funding1.setDescription("Grant 1 - a short description");
        funding1.setPutCode("grant 1 - put-code");

        Funding funding2 = new Funding();
        funding2.setVisibility(Visibility.PUBLIC);
        FundingTitle title2 = new FundingTitle();
        title2.setTitle(new Title("grant2"));
        funding2.setTitle(title2);
        funding2.setDescription("Grant 2 - a short description");
        funding2.setPutCode("grant 2 - put-code");

        orcidFundings.getFundings().add(funding1);
        orcidFundings.getFundings().add(funding2);

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

}
