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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.Orcid;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkVisibilityDefault;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SubjectEntity;
import org.orcid.utils.DateUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Will Simpson
 */
public class OrcidProfileManagerImplTest extends OrcidProfileManagerBaseTest {

    protected static final String APPLICATION_ORCID = "2222-2222-2222-2228";

    protected static final String DELEGATE_ORCID = "1111-1111-1111-1115";

    protected static final String TEST_ORCID = "4444-4444-4444-4447";

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private GenericDao<SubjectEntity, String> subjectDao;

    @Mock
    private OrcidIndexManager orcidIndexManager;

    @Before
    @Transactional
    @Rollback
    public void before() {
        if (profileDao.find(TEST_ORCID) != null) {
            profileDao.remove(TEST_ORCID);
        }
        subjectDao.merge(new SubjectEntity("Computer Science"));
        subjectDao.merge(new SubjectEntity("Dance"));

        OrcidProfile delegateProfile = new OrcidProfile();
        delegateProfile.setOrcid(DELEGATE_ORCID);
        OrcidBio delegateBio = new OrcidBio();
        delegateProfile.setOrcidBio(delegateBio);
        PersonalDetails delegatePersonalDetails = new PersonalDetails();
        delegateBio.setPersonalDetails(delegatePersonalDetails);
        delegatePersonalDetails.setCreditName(new CreditName("H. Shearer"));
        orcidProfileManager.createOrcidProfile(delegateProfile);

        OrcidProfile applicationProfile = new OrcidProfile();
        applicationProfile.setOrcid(APPLICATION_ORCID);
        OrcidBio applicationBio = new OrcidBio();
        applicationProfile.setOrcidBio(applicationBio);
        PersonalDetails applicationPersonalDetails = new PersonalDetails();
        applicationBio.setPersonalDetails(applicationPersonalDetails);
        applicationPersonalDetails.setCreditName(new CreditName("Brown University"));
        orcidProfileManager.createOrcidProfile(applicationProfile);

        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId(applicationProfile.getOrcid().getValue());
        clientDetails.setProfileEntity(profileDao.find(applicationProfile.getOrcid().getValue()));
        clientDetailsDao.merge(clientDetails);

        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setTokenValue("1234");
        token.setClientDetailsEntity(clientDetails);
        token.setProfile(profileDao.find(delegateProfile.getOrcid().getValue()));
        token.setScope(StringUtils.join(new String[] { ScopePathType.ORCID_BIO_READ_LIMITED.value(), ScopePathType.ORCID_BIO_UPDATE.value() }, " "));
        orcidOauth2TokenDetailDao.merge(token);
    }

    @After
    public void after() {
        for (ProfileEntity profileEntity : profileDao.getAll()) {
            orcidProfileManager.deleteProfile(profileEntity.getId());
        }
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfile() {
        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = createBasicProfile();
        orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWhenTokenPresent() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(profile1);
        assertNotNull(profile1.getOrcidBio().getApplications());
        assertEquals(1, profile1.getOrcidBio().getApplications().getApplicationSummary().size());

        OrcidProfile profile2 = createBasicProfile();
        profile2.setOrcid(DELEGATE_ORCID);

        orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(resultProfile);
        assertNotNull(resultProfile.getOrcidBio().getApplications());
        assertEquals(1, resultProfile.getOrcidBio().getApplications().getApplicationSummary().size());
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWithEmailVerified() {
        OrcidProfile profile = createBasicProfile();
        profile = orcidProfileManager.createOrcidProfile(profile);
        assertNotNull(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertFalse(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified());

        profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().setVerified(true);

        orcidProfileManager.updateOrcidProfile(profile);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertTrue(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testOrcidWorksHashCodeAndEquals() {
        OrcidWork workA = createWork1();
        OrcidWork workB = createWork1();
        assertEquals(workA, workB);
        assertEquals(workA.hashCode(), workB.hashCode());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDedupeWorks() {
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidWorks.getOrcidWork().add(createWork1());
        orcidWorks.getOrcidWork().add(createWork1());
        assertEquals(2, orcidWorks.getOrcidWork().size());

        OrcidWorks dedupedOrcidWorks = orcidProfileManager.dedupeWorks(orcidWorks);

        assertEquals(1, dedupedOrcidWorks.getOrcidWork().size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWithDupeWork() {
        OrcidProfile profile = createBasicProfile();
        OrcidProfile createdProfile = orcidProfileManager.createOrcidProfile(profile);
        List<OrcidWork> orcidWorkList = createdProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertEquals(1, orcidWorkList.size());

        orcidWorkList.add(createWork1());
        assertEquals(2, orcidWorkList.size());
        OrcidProfile updatedProfile = orcidProfileManager.updateOrcidProfile(createdProfile);
        List<OrcidWork> updatedOrcidWorkList = updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertEquals(1, updatedOrcidWorkList.size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePersonalInformation() {

        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = createFullOrcidProfile();

        orcidProfileManager.updatePersonalInformation(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals("William", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals("Simpson", resultProfile.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        assertEquals("W. J. R. Simpson", resultProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(1, resultProfile.getOrcidBio().getKeywords().getKeyword().size());
        assertEquals("Will is a software developer at Semantico", resultProfile.getOrcidBio().getBiography().getContent());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePersonalInformationRemovesOrcidIndexFields() throws Exception {

        // re-use the createFull method but add some extra criteria so we can
        // use our specific orcidAllSolrFieldsPopulatedForSave matcher
        OrcidProfile profile = createFullOrcidProfile();
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherName().add(new OtherName("Stan"));
        otherNames.getOtherName().add(new OtherName("Willis"));

        profile.getOrcidBio().getPersonalDetails().setOtherNames(otherNames);

        OrcidWorks orcidWorks = new OrcidWorks();
        profile.setOrcidWorks(orcidWorks);
        WorkTitle singleWorkTitle = new WorkTitle();
        singleWorkTitle.setTitle(new Title("Single works"));
        singleWorkTitle.setSubtitle(new Subtitle("Single works"));
        OrcidWork orcidWork = createWork1(singleWorkTitle);
        // TODO JB some doi testing here?
        // orcidWork.getElectronicResourceNum().add(new
        // ElectronicResourceNum("10.1016/S0021-8502(00)90373-2",
        // ElectronicResourceNumType.DOI));

        orcidWorks.getOrcidWork().add(orcidWork);

        orcidProfileManager.createOrcidProfile(profile);

        // now negate all fields that form part of a solr query, leaving only
        // the orcid itself
        // we do this by passing through an orcid missing the fields from an
        // OrcidSolrDocument
        OrcidProfile negatedProfile = createBasicProfile();
        negatedProfile.getOrcidBio().getPersonalDetails().setFamilyName(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setGivenNames(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setCreditName(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setOtherNames(null);
        negatedProfile.getOrcidBio().getAffiliations().clear();
        orcidProfileManager.updateOrcidBio(negatedProfile);
        assertEquals(IndexingStatus.PENDING, profileDao.find(TEST_ORCID).getIndexingStatus());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAddOrcidWorks() {

        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcid(TEST_ORCID);
        OrcidWorks orcidWorks = new OrcidWorks();
        profile2.setOrcidWorks(orcidWorks);

        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("Another Title"));
        workTitle1.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork work1 = createWork1(workTitle1);
        orcidWorks.getOrcidWork().add(work1);
        // TODO JB - resource num testing here!!!
        // work1.getElectronicResourceNum().add(new
        // ElectronicResourceNum("10.1016/S0021-8502(00)90373-2",
        // ElectronicResourceNumType.DOI));
        // Contributors contributors = new Contributors();
        // work1.setContributors(contributors);
        // Authors authors = new Authors();
        // contributors.setAuthors(authors);
        // Author author = new Author();
        // authors.getAuthor().add(author);
        // author.setCreditName(new CreditName("W. J. R. Simpson"));

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("New Title"));
        workTitle2.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work2 = createWork2(workTitle2);
        orcidWorks.getOrcidWork().add(work2);
        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("New Title"));
        workTitle3.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work3 = createWork2(workTitle3);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(3, works.size());

        assertEquals("Another Title", works.get(0).getWorkTitle().getTitle().getContent());
        assertEquals("Journal of Cloud Spotting", works.get(0).getWorkTitle().getSubtitle().getContent());
        for (OrcidWork work : works) {
            assertEquals(Visibility.PRIVATE, work.getVisibility());
        }

        // assertEquals("10.1016/S0021-8502(00)90373-2",
        // works.get(0).getElectronicResourceNum().get(0).getContent());
        // assertEquals(ElectronicResourceNumType.DOI,
        // works.get(0).getElectronicResourceNum().get(0).getType());
        // assertEquals("495", works.get(0).getVolume().getContent());
        // assertEquals("W. J. R. Simpson",
        // works.get(0).getContributors().getAuthors().getAuthor().get(0).getCreditName().getContent());
        // assertFalse(testStartDate.after(DateUtils.convertToDate(works.get(0).getAddedToProfileDate().getValue())));

        // assertEquals("Test Title",
        // works.get(1).getTitles().getTitle().getContent());
        // assertEquals(DateUtils.convertToDate("2010-03-04"),
        // DateUtils.convertToDate(works.get(1).getAddedToProfileDate().getValue()));

        // assertEquals("Yet Another Title",
        // works.get(2).getTitles().getTitle().getContent());
        // assertFalse(testStartDate.after(DateUtils.convertToDate(works.get(2).getAddedToProfileDate().getValue())));

        // assertEquals(IndexingStatus.PENDING,
        // profileDao.find(TEST_ORCID).getIndexingStatus());

        // assertEquals("10.1016/S0021-8502(00)90373-2",
        // works.get(0).getElectronicResourceNum().get(0).getContent());
        // assertEquals(ElectronicResourceNumType.DOI,
        // works.get(0).getElectronicResourceNum().get(0).getType());
        // assertEquals("495", works.get(0).getVolume().getContent());
        // assertEquals("W. J. R. Simpson",
        // works.get(0).getContributors().getAuthors().getAuthor().get(0).getCreditName().getContent());
        // assertFalse(testStartDate.after(DateUtils.convertToDate(works.get(0).getAddedToProfileDate().getValue())));

        // assertEquals("Test Title",
        // works.get(1).getTitles().getTitle().getContent());
        // assertEquals(DateUtils.convertToDate("2010-03-04"),
        // DateUtils.convertToDate(works.get(1).getAddedToProfileDate().getValue()));

        // assertEquals("Yet Another Title",
        // works.get(2).getTitles().getTitle().getContent());
        // assertFalse(testStartDate.after(DateUtils.convertToDate(works.get(2).getAddedToProfileDate().getValue())));

        // assertEquals(IndexingStatus.PENDING,
        // profileDao.find(TEST_ORCID).getIndexingStatus());

    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAddOrcidWorksWhenDefaultVisibilityIsPublic() {

        OrcidProfile profile1 = createBasicProfile();
        profile1.getOrcidInternal().getPreferences().setWorkVisibilityDefault(new WorkVisibilityDefault(Visibility.PUBLIC));
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcid(TEST_ORCID);
        OrcidWorks orcidWorks = new OrcidWorks();
        profile2.setOrcidWorks(orcidWorks);

        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("Another Title"));
        workTitle1.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork work1 = createWork1(workTitle1);
        orcidWorks.getOrcidWork().add(work1);

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("New Title"));
        workTitle2.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work2 = createWork2(workTitle2);
        orcidWorks.getOrcidWork().add(work2);
        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("Further Title"));
        workTitle3.setSubtitle(new Subtitle("Further subtitle"));
        OrcidWork work3 = createWork3(workTitle3);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(4, works.size());

        assertEquals("Another Title", works.get(0).getWorkTitle().getTitle().getContent());
        assertEquals("Journal of Cloud Spotting", works.get(0).getWorkTitle().getSubtitle().getContent());
        for (OrcidWork work : works) {
            if ("Test Title".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
            } else {
                assertEquals(Visibility.PUBLIC, work.getVisibility());
            }
        }
    }

    /*
     * @Test
     * 
     * @Transactional
     * 
     * @Rollback(true) public void testDeleteOrcidWorks() { OrcidProfile profile
     * = createBasicProfile(); OrcidWork work1 = createWork("Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work1); OrcidWork work2 =
     * createWork("Yet Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work2);
     * 
     * orcidProfileManager.createOrcidProfile(profile);
     * orcidProfileManager.deleteOrcidWorks(TEST_ORCID, new int[] { 0, 2 });
     * 
     * OrcidProfile resultProfile =
     * orcidProfileManager.retrieveOrcidProfile(TEST_ORCID); assertEquals(1,
     * resultProfile.getOrcidWorks().getOrcidWork().size()); //
     * assertEquals("Test Title", //
     * resultProfile.getOrcidWorks().getOrcidWork()
     * .get(0).getTitles().getTitle().getContent());
     * 
     * assertEquals(IndexingStatus.PENDING,
     * profileDao.find(TEST_ORCID).getIndexingStatus()); }
     * 
     * @Test
     * 
     * @Transactional
     * 
     * @Rollback(true) public void testUpdateOrcidWorkVisibility() {
     * OrcidProfile profile = createBasicProfile(); OrcidWork work1 =
     * createWork("Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work1); OrcidWork work2 =
     * createWork("Yet Another Title");
     * profile.getOrcidWorks().getOrcidWork().add(work2);
     * 
     * orcidProfileManager.createOrcidProfile(profile);
     * orcidProfileManager.processProfilesPendingIndexing();
     * orcidProfileManager.updateOrcidWorkVisibility(TEST_ORCID, new int[] { 0,
     * 2 }, Visibility.LIMITED);
     * 
     * OrcidProfile resultProfile =
     * orcidProfileManager.retrieveOrcidProfile(TEST_ORCID); assertEquals(3,
     * resultProfile.getOrcidWorks().getOrcidWork().size());
     * assertEquals(Visibility.LIMITED,
     * resultProfile.getOrcidWorks().getOrcidWork().get(0).getVisibility());
     * assertNull
     * (resultProfile.getOrcidWorks().getOrcidWork().get(1).getVisibility());
     * assertEquals(Visibility.LIMITED,
     * resultProfile.getOrcidWorks().getOrcidWork().get(2).getVisibility()); }
     */

    @Test
    @Transactional
    @Rollback(true)
    public void testRevokeApplication() {
        OrcidProfile userProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(userProfile);
        assertNotNull(userProfile.getOrcidBio().getApplications());
        assertEquals(1, userProfile.getOrcidBio().getApplications().getApplicationSummary().size());

        orcidProfileManager.revokeApplication(DELEGATE_ORCID, APPLICATION_ORCID, Arrays.asList(new ScopePathType[] { ScopePathType.ORCID_BIO_READ_LIMITED,
                ScopePathType.ORCID_BIO_UPDATE }));

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(retrievedProfile);
        assertNull(retrievedProfile.getOrcidBio().getApplications());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRevokeApplicationWithWrongScope() {
        OrcidProfile userProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(userProfile);
        assertNotNull(userProfile.getOrcidBio().getApplications());
        assertEquals(1, userProfile.getOrcidBio().getApplications().getApplicationSummary().size());

        // Shouldn't remove the token because scopes different
        orcidProfileManager.revokeApplication(DELEGATE_ORCID, APPLICATION_ORCID, Arrays.asList(new ScopePathType[] { ScopePathType.ORCID_BIO_READ_LIMITED }));

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(retrievedProfile);
        assertNotNull(retrievedProfile.getOrcidBio().getApplications());
        assertEquals(1, retrievedProfile.getOrcidBio().getApplications().getApplicationSummary().size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePasswordResultsInEncypytedProfile() {
        OrcidProfile basicProfile = createBasicProfile();
        OrcidProfile derivedProfile = orcidProfileManager.createOrcidProfile(basicProfile);
        assertTrue(encryptionManager.hashMatches("password", derivedProfile.getPassword()));
        assertEquals("random answer", encryptionManager.decryptForInternalUse(derivedProfile.getSecurityQuestionAnswer()));
        assertEquals("1234", encryptionManager.decryptForInternalUse(derivedProfile.getVerificationCode()));

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(derivedProfile.getOrcid().getValue());
        assertTrue(encryptionManager.hashMatches("password", derivedProfile.getPassword()));
        assertEquals("random answer", retrievedProfile.getSecurityQuestionAnswer());
        assertEquals("1234", retrievedProfile.getVerificationCode());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePreferences() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);
        assertEquals(Visibility.PRIVATE, profile1.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue());

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcid(TEST_ORCID);
        OrcidInternal internal = new OrcidInternal();
        profile2.setOrcidInternal(internal);
        Preferences preferences = new Preferences();
        internal.setPreferences(preferences);
        preferences.setSendChangeNotifications(new SendChangeNotifications(false));
        preferences.setSendOrcidNews(new SendOrcidNews(true));
        preferences.setWorkVisibilityDefault(new WorkVisibilityDefault(Visibility.PUBLIC));

        orcidProfileManager.updatePreferences(profile2);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(false, retrievedProfile.getOrcidInternal().getPreferences().getSendChangeNotifications().isValue());
        assertEquals(true, retrievedProfile.getOrcidInternal().getPreferences().getSendOrcidNews().isValue());
        assertEquals(Visibility.PUBLIC, retrievedProfile.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAffiliations() throws DatatypeConfigurationException {
        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcid(TEST_ORCID);
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setPersonalDetails(new PersonalDetails());

        Affiliation affiliation1 = getAffiliation();
        Affiliation affiliation2 = getAffiliation();
        affiliation2.setAffiliationName("Past Institution 2");
        affiliation2.setAffiliationType(AffiliationType.CURRENT_PRIMARY_INSTITUTION);

        orcidBio.getAffiliations().add(affiliation1);
        orcidBio.getAffiliations().add(affiliation2);

        profile2.setOrcidBio(orcidBio);
        OrcidProfile profile = orcidProfileManager.addAffiliations(profile2);

        assertNotNull(profile);
        assertEquals(2, profile.getOrcidBio().getAffiliations().size());

        // simulate the ManageProfileController#deletePastAffiliations by
        // creating single past inst
        profile2 = createFullOrcidProfile();
        profile2.getOrcidBio().getAffiliations().clear();
        profile2.getOrcidBio().getAffiliations().add(affiliation1);

        orcidProfileManager.updateOrcidBio(profile2);

        assertEquals(IndexingStatus.PENDING, profileDao.find(TEST_ORCID).getIndexingStatus());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAddDelegates() {
        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcid(TEST_ORCID);
        OrcidBio orcidBio = new OrcidBio();
        profile2.setOrcidBio(orcidBio);
        Delegation delegation = new Delegation();
        orcidBio.setDelegation(delegation);
        GivenPermissionTo givenPermissionTo = new GivenPermissionTo();
        delegation.setGivenPermissionTo(givenPermissionTo);
        DelegationDetails delegationDetails = new DelegationDetails();
        delegationDetails.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar("2011-03-14T02:34:16")));
        DelegateSummary delegateSummary = new DelegateSummary(new Orcid(DELEGATE_ORCID));
        delegationDetails.setDelegateSummary(delegateSummary);
        givenPermissionTo.getDelegationDetails().add(delegationDetails);
        orcidProfileManager.addDelegates(profile2);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNotNull(retrievedProfile);
        GivenPermissionTo retrievedGivenPermissionTo = retrievedProfile.getOrcidBio().getDelegation().getGivenPermissionTo();
        assertNotNull(retrievedGivenPermissionTo);
        assertEquals(1, retrievedGivenPermissionTo.getDelegationDetails().size());
        DelegationDetails retrievedDelegationDetails = retrievedGivenPermissionTo.getDelegationDetails().get(0);
        DelegateSummary retrievedDelegateSummary = retrievedDelegationDetails.getDelegateSummary();
        assertNotNull(retrievedDelegateSummary);
        assertEquals(DELEGATE_ORCID, retrievedDelegateSummary.getOrcid().getValue());
        assertEquals("H. Shearer", retrievedDelegateSummary.getCreditName().getContent());
        assertEquals(DateUtils.convertToDate("2011-03-14T02:34:16"), DateUtils.convertToDate(retrievedDelegationDetails.getApprovalDate().getValue()));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRevokeDelegate() {
        OrcidProfile profile1 = createBasicProfile();
        Delegation delegation = new Delegation();
        profile1.getOrcidBio().setDelegation(delegation);
        GivenPermissionTo givenPermissionTo = new GivenPermissionTo();
        delegation.setGivenPermissionTo(givenPermissionTo);
        DelegationDetails delegationDetails = new DelegationDetails();
        delegationDetails.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar("2011-03-14T02:34:16")));
        DelegateSummary profileSummary = new DelegateSummary(new Orcid(DELEGATE_ORCID));
        delegationDetails.setDelegateSummary(profileSummary);
        givenPermissionTo.getDelegationDetails().add(delegationDetails);
        orcidProfileManager.createOrcidProfile(profile1);

        orcidProfileManager.revokeDelegate(TEST_ORCID, DELEGATE_ORCID);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNull(retrievedProfile.getOrcidBio().getDelegation());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePasswordInformationLeavesSecurityQuestionsUnchanged() {
        OrcidProfile profile1 = createBasicProfile();
        assertEquals("password", profile1.getPassword());
        assertEquals("random answer", profile1.getSecurityQuestionAnswer());
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcid().getValue());

        String hashedPasswordValue = retrievedProfile.getPassword();
        assertTrue("Should have hashed password", 108 == hashedPasswordValue.length() && !"password".equals(hashedPasswordValue));
        assertTrue("Should have decrypted security answer", "random answer".equals(retrievedProfile.getSecurityQuestionAnswer()));

        retrievedProfile.setPassword("A new password");

        OrcidProfile updatedProfile = orcidProfileManager.updatePasswordInformation(retrievedProfile);
        updatedProfile = orcidProfileManager.retrieveOrcidProfile(updatedProfile.getOrcid().getValue());
        // retrieve orcid so as to decrypt

        OrcidProfile retrieved = orcidProfileManager.retrieveOrcidProfile(updatedProfile.getOrcid().getValue());
        assertTrue("Password should have changed and be hashed", 108 == hashedPasswordValue.length() && !hashedPasswordValue.equals(retrieved.getPassword()));
        assertTrue("Should have decrypted security answer", "random answer".equals(retrievedProfile.getSecurityQuestionAnswer()));

    }

    @Test
    @Transactional
    @Rollback(true)
    public void testSecurityQuestionsUpdateLeavePasswordInformationUnchanged() {
        OrcidProfile profile1 = createBasicProfile();
        assertEquals("password", profile1.getPassword());
        assertEquals("random answer", profile1.getSecurityQuestionAnswer());
        orcidProfileManager.createOrcidProfile(profile1);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcid().getValue());

        String hashedPasswordValue = retrievedProfile.getPassword();
        assertTrue("Should have hashed password", 108 == hashedPasswordValue.length() && !"password".equals(hashedPasswordValue));
        assertTrue("Should have decrypted security answer", "random answer".equals(retrievedProfile.getSecurityQuestionAnswer()));

        retrievedProfile.setSecurityQuestionAnswer("A new random answer");

        OrcidProfile updatedProfile = orcidProfileManager.updatePasswordSecurityQuestionsInformation(retrievedProfile);
        updatedProfile = orcidProfileManager.retrieveOrcidProfile(updatedProfile.getOrcid().getValue());
        // retrieve orcid so as to decrypt

        OrcidProfile retrieved = orcidProfileManager.retrieveOrcidProfile(updatedProfile.getOrcid().getValue());
        assertTrue("Password should not have changed", hashedPasswordValue.equals(retrieved.getPassword()));
        assertEquals("A new random answer", retrieved.getSecurityQuestionAnswer());

    }

}
