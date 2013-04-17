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
package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.solr.entities.OrcidSolrDocument;
import org.orcid.persistence.solr.entities.OrcidSolrResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
/**
 * Integration tests for Solr Daos. 
 * In particular these are used to test that query strings return the Orcids that are expected from SOLR.
 * You may need to compare the queries given in the test methods below with those of the SearchOrcidFormToQueryMapperTest. 
 * 
 * @author jamesb
 * @See SearchOrcidFormToQueryMapperTest
 *
 */
public class SolrDaoTest {

    @Resource
    private SolrDao solrDao;

    private String firstOrcid = "1234-5678";
    private String secondOrcid = "5677-1235";
    private List<String> orcidsToDelete;

    @Before
    public void initOrcid() {

        orcidsToDelete = new ArrayList<String>();
        orcidsToDelete.add(firstOrcid);
        orcidsToDelete.add(secondOrcid);
    }

    @After
    public void deleteOrcid() {
        solrDao.removeOrcids(orcidsToDelete);
    }

    @Test
    public void searchByOrcid() throws Exception {

        OrcidSolrResult firstOrcidResult = solrDao.findByOrcid(firstOrcid);
        assertNull(firstOrcidResult);

        OrcidSolrDocument secondOrcid = buildAndPersistSecondOrcid();
        OrcidSolrDocument firstOrcid = buildAndPersistFirstOrcid();

        firstOrcidResult = solrDao.findByOrcid(firstOrcid.getOrcid());
        assertFalse(secondOrcid.getOrcid().equals(firstOrcidResult.getOrcid()));
        assertEquals("1234-5678", firstOrcidResult.getOrcid());
    }

    @Test
    public void queryStringSearchPatent() throws Exception {
        OrcidSolrDocument firstOrcid = buildAndPersistFirstOrcid();
        String patentQueryString = "patent-numbers:Elec-hammer01X%3A";
        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(patentQueryString, null, null);
        assertTrue(solrResults.size() == 1);
        assertEquals(firstOrcid.getOrcid(), solrResults.get(0).getOrcid());
    }

    @Test
    public void queryStringSearchGrant() throws Exception {
        OrcidSolrDocument secondOrcid = buildAndPersistSecondOrcid();
        String patentQueryString = "grant-numbers:grant-number02X%3A";
        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(patentQueryString, null, null);
        assertTrue(solrResults.size() == 1);
        assertEquals(secondOrcid.getOrcid(), solrResults.get(0).getOrcid());
    }

    @Test
    public void queryStringSearchFamilyNameGivenName() throws Exception {

        OrcidSolrResult orcidSolrDocument = solrDao.findByOrcid(firstOrcid);
        assertNull(orcidSolrDocument);

        orcidSolrDocument = solrDao.findByOrcid(secondOrcid);
        assertNull(orcidSolrDocument);

        OrcidSolrDocument profile1 = new OrcidSolrDocument();
        profile1.setOrcid(firstOrcid);
        profile1.setFamilyName("Bass");
        profile1.setGivenNames("Teddy");

        OrcidSolrDocument profile2 = new OrcidSolrDocument();
        profile2.setOrcid(secondOrcid);
        profile2.setFamilyName("Bass");
        profile2.setGivenNames("Terry");

        solrDao.persist(profile1);
        solrDao.persist(profile2);

        String familyNameGivenNameQuery = "given-names:teddy AND family-name:bass";

        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(familyNameGivenNameQuery, null, null);
        assertTrue(solrResults.size() == 1);
        assertEquals(firstOrcid, solrResults.get(0).getOrcid());

        String familyNameQueryRelaxed = "given-names:te* AND family-name:Bass";
        solrResults = solrDao.findByDocumentCriteria(familyNameQueryRelaxed, null, null);
        assertTrue(solrResults.size() == 2);
        assertEquals(firstOrcid, solrResults.get(0).getOrcid());
        assertEquals(secondOrcid, solrResults.get(1).getOrcid());

    }

    @Test
    public void queryFieldWithExclusions() throws Exception {
        buildAndPersistFirstOrcid();
        buildAndPersistSecondOrcid();

        String givenNameQueryString = "given-names:Given";
        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(givenNameQueryString, null, null);
        assertTrue(solrResults.size() == 2);

        String givenNameWithExclusionsQueryString = MessageFormat.format("given-names:Given -orcid: {0}", new Object[] { secondOrcid });
        solrResults = solrDao.findByDocumentCriteria(givenNameWithExclusionsQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(firstOrcid));
    }

    @Test
    public void queryFieldWithKeyword() throws Exception {
        buildAndPersistFirstOrcid();
        OrcidSolrDocument secondDoc = buildSupplementaryOrcid();
        String subjectKeyword1 = "Advanced Muppetry";
        String subjectKeyword2 = "Basic Muppetry";
        secondDoc.setOrcid(secondOrcid);
        secondDoc.setKeywords(Arrays.asList(subjectKeyword1, subjectKeyword2));
        persistOrcid(secondDoc);

        String familyNameKeywordsQueryString = "given-names:given AND keyword:basic";
        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(familyNameKeywordsQueryString, null, null);
        assertTrue(solrResults.size() == 1);
        OrcidSolrResult result = solrResults.get(0);
        assertEquals(secondOrcid, result.getOrcid());

        familyNameKeywordsQueryString = "given-names:given AND keyword:advanced";
        solrResults = solrDao.findByDocumentCriteria(familyNameKeywordsQueryString, null, null);
        assertTrue(solrResults.size() == 1);
        result = solrResults.get(0);
        assertEquals(secondOrcid, result.getOrcid());

    }

    @Test
    public void queryFieldWeyword() throws Exception {
        buildAndPersistFirstOrcid();
        OrcidSolrDocument secondDoc = buildSupplementaryOrcid();
        String subjectKeyword1 = "Advanced Muppetry";
        String subjectKeyword2 = "Basic Muppetry";
        secondDoc.setOrcid(secondOrcid);
        secondDoc.setKeywords(Arrays.asList(subjectKeyword1, subjectKeyword2));
        persistOrcid(secondDoc);

        String familyNameKeywordsQueryString = "given-names:given AND keyword:basic";
        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(familyNameKeywordsQueryString, null, null);
        assertTrue(solrResults.size() == 1);
        OrcidSolrResult result = solrResults.get(0);
        assertEquals(secondOrcid, result.getOrcid());

        familyNameKeywordsQueryString = "given-names:given AND keyword:advanced";
        solrResults = solrDao.findByDocumentCriteria(familyNameKeywordsQueryString, null, null);
        assertTrue(solrResults.size() == 1);
        result = solrResults.get(0);
        assertEquals(secondOrcid, result.getOrcid());

    }

    @Test
    public void queryFieldWithBoostAndExclusions() throws Exception {
        OrcidSolrDocument firstOrcidDoc = buildFirstOrcid();
        firstOrcidDoc.setOrcid(firstOrcid);
        firstOrcidDoc.setFamilyName("James");

        OrcidSolrDocument secondOrcidDoc = buildSupplementaryOrcid();
        secondOrcidDoc.setOrcid(secondOrcid);
        secondOrcidDoc.setGivenNames("James");

        OrcidSolrDocument thirdOrcidDoc = buildSupplementaryOrcid();
        thirdOrcidDoc.setFamilyName("James");
        thirdOrcidDoc.setOrcid(RandomStringUtils.randomAlphabetic(9));

        persistOrcid(firstOrcidDoc);
        persistOrcid(secondOrcidDoc);
        persistOrcid(thirdOrcidDoc);

        String familyNameGivenNameQuery = "{!edismax qf='family-name^1.0 given-names^2.0'}James";

        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(familyNameGivenNameQuery, null, null);

        assertTrue(solrResults.size() == 3);
        OrcidSolrResult givenNameMatch = solrResults.get(0);
        assertTrue(secondOrcid.equals(givenNameMatch.getOrcid()) && givenNameMatch.getRelevancyScore() > 1);
        OrcidSolrResult familyNameMatch1 = solrResults.get(1);
        OrcidSolrResult familyNameMatch2 = solrResults.get(2);
        assertTrue(familyNameMatch1.getRelevancyScore() < 1.0);
        assertTrue(familyNameMatch2.getRelevancyScore() < 1.0);

        String familyNameGivenNameQueryWithExclude = familyNameGivenNameQuery + " -orcid: " + thirdOrcidDoc.getOrcid();
        solrResults = solrDao.findByDocumentCriteria(familyNameGivenNameQueryWithExclude, null, null);
        assertTrue(solrResults.size() == 2);
        givenNameMatch = solrResults.get(0);
        assertTrue(givenNameMatch.getOrcid().equals(secondOrcid));
        assertTrue(givenNameMatch.getRelevancyScore() > 1);
        familyNameMatch1 = solrResults.get(1);
        assertTrue(familyNameMatch1.getOrcid().equals(firstOrcid));
        assertTrue(familyNameMatch1.getRelevancyScore() < 1);
    }

    @Test
    public void queryFieldWithBoost() throws Exception {

        OrcidSolrDocument firstOrcidDoc = buildFirstOrcid();
        firstOrcidDoc.setOrcid(firstOrcid);
        firstOrcidDoc.setFamilyName("James");

        OrcidSolrDocument secondOrcidDoc = buildSupplementaryOrcid();
        secondOrcidDoc.setOrcid(secondOrcid);
        secondOrcidDoc.setGivenNames("James");

        solrDao.persist(firstOrcidDoc);
        solrDao.persist(secondOrcidDoc);

        String familyNameGivenNameQuery = "{!edismax qf='family-name^1.0 given-names^2.0'}James";

        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(familyNameGivenNameQuery, null, null);

        assertTrue(solrResults.size() == 2);
        assertTrue(solrResults.get(0).getOrcid().equals(secondOrcid));
        assertTrue(solrResults.get(1).getOrcid().equals(firstOrcid));
    }

    @Test
    public void queryStringSearchFamilyNameGivenNameTenRows() throws Exception {

        int numRecordsToCreate = 10;
        List<String> orcidsToGetStored = new ArrayList<String>();
        for (int i = 0; i < numRecordsToCreate; i++) {
            OrcidSolrDocument orcidSolrDocument = buildSupplementaryOrcid();
            orcidSolrDocument.setGivenNames(RandomStringUtils.randomAscii(20));
            // format of orcid irrelevant to solr - just need to make them
            // different
            orcidSolrDocument.setOrcid(RandomStringUtils.randomAscii(20));
            orcidsToGetStored.add(orcidSolrDocument.getOrcid());
            persistOrcid(orcidSolrDocument);
        }

        String familyNameOnlyQuery = "family-name:Family Name";
        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(familyNameOnlyQuery, null, null);
        assertTrue(solrResults.size() == 10);

        familyNameOnlyQuery = "{!start=0 rows=5} family-name:Family Name";
        solrResults = solrDao.findByDocumentCriteria(familyNameOnlyQuery, null, null);
        assertTrue(solrResults.size() == 5);

    }

    @Test
    public void queryStringWithTextFieldSpansAllFields() throws Exception {
        buildAndPersistFirstOrcid();
        buildAndPersistSecondOrcid();

        String orcidQueryString = "text=1234\\-5678";
        List<OrcidSolrResult> solrResults = solrDao.findByDocumentCriteria(orcidQueryString, null, null);
        assertTrue(solrResults.size() == 1);

        String givenNameQueryString = "text=Given";
        solrResults = solrDao.findByDocumentCriteria(givenNameQueryString, null, null);
        assertTrue(solrResults.size() == 2);

        String familyNameQueryString = "text=Smith";
        solrResults = solrDao.findByDocumentCriteria(familyNameQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(firstOrcid));

        String pastInstitutionsQueryString = "text=Brown";
        solrResults = solrDao.findByDocumentCriteria(pastInstitutionsQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(firstOrcid));

        String currentInstitutionsQueryString = "text=Current";
        solrResults = solrDao.findByDocumentCriteria(currentInstitutionsQueryString, null, null);
        assertTrue(solrResults.size() == 2 && solrResults.get(0).getOrcid().equals(firstOrcid));
        assertEquals(solrResults.get(1).getOrcid(), secondOrcid);

        String primaryInstitutionsQueryString = "text=Primary";
        solrResults = solrDao.findByDocumentCriteria(primaryInstitutionsQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(secondOrcid));

        String patentsQueryString = "text=Elec-hammer01X%3A";
        solrResults = solrDao.findByDocumentCriteria(patentsQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(firstOrcid));

        String grantQueryString = "text=Grant-number02X%3A";
        solrResults = solrDao.findByDocumentCriteria(grantQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(secondOrcid));

        String creditNameQueryString = "text=Credit";
        solrResults = solrDao.findByDocumentCriteria(creditNameQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(secondOrcid));

        String otherNamesQueryString = "text=Other";
        solrResults = solrDao.findByDocumentCriteria(otherNamesQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(secondOrcid));

        String doiQueryString = "text=id2";
        solrResults = solrDao.findByDocumentCriteria(doiQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(secondOrcid));

        String worksTitlesQueryString = "text=Work Title 1";
        solrResults = solrDao.findByDocumentCriteria(worksTitlesQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(secondOrcid));

        String emailQueryString = "text=stan@ficitional.co.uk";
        solrResults = solrDao.findByDocumentCriteria(emailQueryString, null, null);
        assertTrue(solrResults.size() == 1 && solrResults.get(0).getOrcid().equals(secondOrcid));

    }

    private OrcidSolrDocument buildAndPersistFirstOrcid() {
        OrcidSolrDocument firstDocument = buildFirstOrcid();
        firstDocument.setOrcid(firstOrcid);
        persistOrcid(firstDocument);
        return firstDocument;
    }

    private OrcidSolrDocument buildFirstOrcid() {
        OrcidSolrDocument testDoc = new OrcidSolrDocument();
        testDoc.setOrcid(firstOrcid);
        testDoc.setGivenNames("Given Name of Person");
        testDoc.setFamilyName("Smith");
        testDoc.setPastInstitutionNames(Arrays.asList(new String[] { "Harvard", "Brown" }));
        testDoc.setAffiliateInstitutionNames(Arrays.asList(new String[] { "Current Inst 2" }));
        testDoc.setPatentNumbers(Arrays.asList(new String[] { "Elec-hammer01X:" }));
        return testDoc;
    }

    private OrcidSolrDocument buildAndPersistSecondOrcid() {

        OrcidSolrDocument secondDoc = buildSupplementaryOrcid();
        secondDoc.setOrcid(secondOrcid);
        persistOrcid(secondDoc);
        return secondDoc;

    }

    private OrcidSolrDocument buildSupplementaryOrcid() {

        OrcidSolrDocument secondOrcidDoc = new OrcidSolrDocument();
        secondOrcidDoc.setCreditName("Credit Name");
        secondOrcidDoc.setEmailAddress("stan@ficitional.co.uk");
        secondOrcidDoc.setFamilyName("Family Name");
        secondOrcidDoc.setGivenNames("Given Names");
        secondOrcidDoc.setDigitalObjectIds(Arrays.asList(new String[] { "id1", "id2" }));
        secondOrcidDoc.setOtherNames(Arrays.asList(new String[] { "Other Name 1", "Other Name 2" }));
        secondOrcidDoc.setPastInstitutionNames(Arrays.asList(new String[] { "Past Inst 1", "Past Inst 2" }));
        secondOrcidDoc.setAffiliateInstitutionNames(Arrays.asList(new String[] { "Current Inst 1" }));
        secondOrcidDoc.setAffiliatePrimaryInstitutionNames(Arrays.asList(new String[] { "Primary Institution Name" }));
        secondOrcidDoc.setWorkTitles(Arrays.asList(new String[] { "Work Title 1", "Work Title 2" }));
        secondOrcidDoc.setGrantNumbers(Arrays.asList(new String[] { "Grant-number02X:" }));
        return secondOrcidDoc;
    }

    private void persistOrcid(OrcidSolrDocument orcidSolrDocument) {
        orcidsToDelete.add(orcidSolrDocument.getOrcid());
        solrDao.persist(orcidSolrDocument);

    }

}
