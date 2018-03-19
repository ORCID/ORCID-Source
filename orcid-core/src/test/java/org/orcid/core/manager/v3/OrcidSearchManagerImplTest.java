package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.common.SolrDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.v3.impl.OrcidSearchManagerImpl;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
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
import org.orcid.jaxb.model.v3.dev1.search.Search;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.test.TargetProxyHelper;
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

    @Resource(name = "orcidSearchManagerV3")
    private OrcidSearchManagerImpl orcidSearchManager;

    @Mock
    private SolrDao mockSolrDao;

    @Mock
    private OrcidProfileCacheManager mockOrcidProfileCacheManager;
    
    @Mock
    private OrcidSecurityManager mockOrcidSecurityManager;

    @Mock
    private ProfileDao mockProfileDao;
    
    @Resource
    private SolrDao solrDao;

    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
        
    @Resource
    private ProfileDao profileDaoReadOnly;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "solrDao", mockSolrDao);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidProfileCacheManager", mockOrcidProfileCacheManager);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSecurityManager", mockOrcidSecurityManager);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "profileDaoReadOnly", mockProfileDao);
        when(mockProfileDao.retrieveLastModifiedDate(Matchers.anyString())).thenReturn(new Date());
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "solrDao", solrDao);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidProfileCacheManager", orcidProfileCacheManager);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "orcidSecurityManager", orcidSecurityManager);
        TargetProxyHelper.injectIntoProxy(orcidSearchManager, "profileDaoReadOnly", profileDaoReadOnly);
    }
    
    @Test
    public void testFindOrcidIds() {
        when(mockSolrDao.findByDocumentCriteria(Matchers.<Map<String, List<String>>>any())).thenReturn(multipleResultsForQuery());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(2, search.getResults().size());
        assertEquals(Long.valueOf(2), search.getNumFound());
        assertEquals("5678", search.getResults().get(0).getOrcidIdentifier().getPath());
        assertEquals("6789", search.getResults().get(1).getOrcidIdentifier().getPath());
    }
    
    @Test
    public void testFindOrcidIdsNoResults() {
        when(mockSolrDao.findByDocumentCriteria(Matchers.<Map<String, List<String>>>any())).thenReturn(new OrcidSolrResults());
        Search search = orcidSearchManager.findOrcidIds(new HashMap<>());
        assertNotNull(search);
        assertEquals(Long.valueOf(0), search.getNumFound());
        assertEquals(0, search.getResults().size());
    }

    @Test
    @Rollback
    public void orcidRetrievalAllDataPresentInDb() throws Exception {
        when(mockSolrDao.findByOrcid("1434")).thenReturn(getOrcidSolrResult("5678", new Float(37.2)));
        when(mockOrcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfileAllIndexFieldsPopulated());

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

        when(mockSolrDao.findByOrcid("1434")).thenReturn(getOrcidSolrResult("5678", new Float(37.2)));
        when(mockOrcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
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

        when(mockSolrDao.findByOrcid("1434")).thenReturn(getOrcidSolrResult("5678", new Float(37.2)));
        when(mockOrcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(null);
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidSearchResultsById("1434");
        assertNotNull(retrievedOrcidMessage);
        assertNotNull(retrievedOrcidMessage.getOrcidSearchResults());
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().isEmpty());
    }

    @Test
    @Rollback
    public void oneOrcidInDbOtherMissing() {

        when(mockSolrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(multipleResultsForQuery());
        when(mockOrcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        when(mockOrcidProfileCacheManager.retrievePublicBio("6789")).thenReturn(null);
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

        when(mockSolrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(multipleResultsForQuery());
        when(mockOrcidProfileCacheManager.retrievePublicBio("5678")).thenReturn(getOrcidProfile5678MandatoryOnly());
        when(mockOrcidProfileCacheManager.retrievePublicBio("6789")).thenReturn(getOrcidProfile6789MandatoryOnly());
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

    private OrcidSolrResult getOrcidSolrResult(String orcid, Float relevancy) {
        OrcidSolrResult solrResult = new OrcidSolrResult();
        solrResult.setOrcid(orcid);
        solrResult.setRelevancyScore(relevancy);
        return solrResult;
    }

    private OrcidSolrResults multipleResultsForQuery() {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> resultsList = new ArrayList<>();
        orcidSolrResults.setResults(resultsList);
        resultsList.add(getOrcidSolrResult("5678", new Float(37.2)));
        resultsList.add(getOrcidSolrResult("6789", new Float(52.2)));
        orcidSolrResults.setNumFound(2);
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
    
    @Test
    public void recordLockedTest() {
        OrcidProfile orcidProfile = getOrcidProfileAllIndexFieldsPopulated();
        orcidProfile.getOrcidIdentifier().setPath("0000");
        when(mockSolrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(invalidRecordSearchResult());
        when(mockOrcidProfileCacheManager.retrievePublicBio("0000")).thenReturn(orcidProfile);
        doThrow(new LockedException()).when(mockOrcidSecurityManager).checkProfile("0000");
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidsByQuery("rndQuery");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults() != null && retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);
        OrcidSearchResult searchResult = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile profileReturnedFromSearch = searchResult.getOrcidProfile();
        assertEquals("0000", profileReturnedFromSearch.getOrcidIdentifier().getPath());
        assertNotNull(profileReturnedFromSearch.getOrcidHistory().getLastModifiedDate().getValue());
        assertNull(profileReturnedFromSearch.getOrcidActivities());
        assertNull(profileReturnedFromSearch.getOrcidBio());           
    }
    
    @Test
    public void recordDeactivatedTest() {
        OrcidProfile orcidProfile = getOrcidProfileAllIndexFieldsPopulated();
        orcidProfile.getOrcidIdentifier().setPath("0000");
        when(mockSolrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(invalidRecordSearchResult());
        when(mockOrcidProfileCacheManager.retrievePublicBio("0000")).thenReturn(orcidProfile);
        doThrow(new DeactivatedException()).when(mockOrcidSecurityManager).checkProfile("0000");
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidsByQuery("rndQuery");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults() != null && retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);
        OrcidSearchResult searchResult = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile profileReturnedFromSearch = searchResult.getOrcidProfile();
        assertEquals("0000", profileReturnedFromSearch.getOrcidIdentifier().getPath());
        assertNotNull(profileReturnedFromSearch.getOrcidHistory().getLastModifiedDate().getValue());
        assertNull(profileReturnedFromSearch.getOrcidActivities());
        assertNull(profileReturnedFromSearch.getOrcidBio());           
    }
    
    @Test
    public void recordDeprecatedTest() {
        OrcidProfile orcidProfile = getOrcidProfileAllIndexFieldsPopulated();
        orcidProfile.getOrcidIdentifier().setPath("0000");
        when(mockSolrDao.findByDocumentCriteria("rndQuery", null, null)).thenReturn(invalidRecordSearchResult());
        when(mockOrcidProfileCacheManager.retrievePublicBio("0000")).thenReturn(orcidProfile);
        doThrow(new OrcidDeprecatedException()).when(mockOrcidSecurityManager).checkProfile("0000");
        OrcidMessage retrievedOrcidMessage = orcidSearchManager.findOrcidsByQuery("rndQuery");
        assertNotNull(retrievedOrcidMessage);
        assertTrue(retrievedOrcidMessage.getOrcidSearchResults() != null && retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().size() == 1);
        OrcidSearchResult searchResult = retrievedOrcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0);
        OrcidProfile profileReturnedFromSearch = searchResult.getOrcidProfile();
        assertEquals("0000", profileReturnedFromSearch.getOrcidIdentifier().getPath());
        assertNotNull(profileReturnedFromSearch.getOrcidHistory().getLastModifiedDate().getValue());
        assertNull(profileReturnedFromSearch.getOrcidActivities());
        assertNull(profileReturnedFromSearch.getOrcidBio());           
    }
    
    private OrcidSolrResults invalidRecordSearchResult() {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> resultsList = new ArrayList<>();
        orcidSolrResults.setResults(resultsList);
        resultsList.add(getOrcidSolrResult("0000", new Float(37.2)));        
        orcidSolrResults.setNumFound(2);
        return orcidSolrResults;
    }

}
