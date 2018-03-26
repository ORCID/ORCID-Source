package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;

/**
 * @author Will Simpson
 */
public class OrcidProfileManagerImpl_NonTransactionalTest extends OrcidProfileManagerBaseTest {
    private static final String MEMBER_ID = "0000-0000-0000-0000";
    private static final String CLIENT_1 = "0000-0000-0000-0001";
    private static final String CLIENT_2 = "0000-0000-0000-0002";
    private static final String TEST_ORCID = "0000-0000-0000-0001";

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private WorkManager workManager;

    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private OrcidJaxbCopyManager orcidJaxbCopyManager;

    @Mock
    private SourceManager mockSourceManager;

    @Mock
    private SourceManager anotherMockSourceManager;

    private static boolean init = false;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", mockSourceManager);        
        TargetProxyHelper.injectIntoProxy(orcidJaxbCopyManager, "sourceManager", mockSourceManager);
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(new ClientDetailsEntity(CLIENT_1));
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);

        SourceEntity sourceEntity2 = new SourceEntity();
        sourceEntity2.setSourceClient(new ClientDetailsEntity(CLIENT_2));
        when(anotherMockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity2);

        if (!init) {
            orcidProfileManager.setCompareWorksUsingScopusWay(true);

            OrcidProfile applicationProfile = new OrcidProfile();
            applicationProfile.setOrcidIdentifier(MEMBER_ID);
            OrcidBio applicationBio = new OrcidBio();
            applicationProfile.setOrcidBio(applicationBio);
            PersonalDetails applicationPersonalDetails = new PersonalDetails();
            applicationBio.setPersonalDetails(applicationPersonalDetails);
            applicationPersonalDetails.setCreditName(new CreditName("ORCID TEST"));
            orcidProfileManager.createOrcidProfile(applicationProfile, false, false);

            ClientDetailsEntity clientDetails = new ClientDetailsEntity();
            clientDetails.setId(CLIENT_1);
            clientDetails.setGroupProfileId(MEMBER_ID);
            clientDetailsManager.merge(clientDetails);

            ClientDetailsEntity clientDetails2 = new ClientDetailsEntity();
            clientDetails2.setId(CLIENT_2);
            clientDetails.setGroupProfileId(MEMBER_ID);
            clientDetailsManager.merge(clientDetails2);

            OrcidProfile profile1 = createBasicProfile();
            profile1.setOrcidActivities(null);
            // Change the orcid identifier
            profile1.setOrcidIdentifier(TEST_ORCID);
            profile1.setOrcidHistory(new OrcidHistory());
            profile1.getOrcidHistory().setClaimed(new Claimed(true));
            profile1.getOrcidHistory().setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
            // Change the email address
            profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(TEST_ORCID + "@test.com");
            profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

            ProfileEntity persisted = profileDao.find(TEST_ORCID);
            assertNotNull(persisted.getHashedOrcid());

            init = true;
        }
        // Set the default visibility to PRIVATE
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PRIVATE));
        orcidProfileManager.updatePreferences(TEST_ORCID, profile1.getOrcidInternal().getPreferences());

        addDataToTestRecord();
    }

    @After
    public void after() {
        workManager.removeAllWorks(TEST_ORCID);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(orcidJaxbCopyManager, "sourceManager", sourceManager);        
    }

    private void addDataToTestRecord() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        OrcidWork work1 = createWork1();
        profile1.setOrcidActivities(new OrcidActivities());
        profile1.getOrcidActivities().setOrcidWorks(new OrcidWorks());
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work1);
        orcidProfileManager.updateOrcidProfile(profile1);
    }

    @Test
    public void testUpdateProfileButRemoveWorkExternalIdentifier() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        List<WorkExternalIdentifier> workExternalIdentifiers = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers()
                .getWorkExternalIdentifier();
        assertEquals(3, workExternalIdentifiers.size());
        Iterator<WorkExternalIdentifier> workExternalIdentifiersIterator = workExternalIdentifiers.iterator();
        while (workExternalIdentifiersIterator.hasNext()) {
            if (WorkExternalIdentifierType.PMID.equals(workExternalIdentifiersIterator.next().getWorkExternalIdentifierType())) {
                workExternalIdentifiersIterator.remove();
            }
        }

        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).setModified(true);

        orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(2, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers().getWorkExternalIdentifier().size());
    }

    @Test
    public void testUpdateProfileButRemoveWorkContributor() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        List<Contributor> contributors = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor();
        assertEquals(2, contributors.size());
        Iterator<Contributor> contributorsIterator = contributors.iterator();
        while (contributorsIterator.hasNext()) {
            if (SequenceType.ADDITIONAL.equals(contributorsIterator.next().getContributorAttributes().getContributorSequence())) {
                contributorsIterator.remove();
            }
        }

        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).setModified(true);

        orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(1, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().size());
    }

    @Test
    public void testCreateRecordWithClient1AddDuplicateWithClient2() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        assertEquals(CLIENT_1, profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getSource().retrieveSourcePath());
        String work1PutCode = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        // Change the source entity
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", anotherMockSourceManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", anotherMockSourceManager);

        OrcidWork dupWork = createWork1();
        profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().clear();
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(dupWork);

        orcidProfileManager.addOrcidWorks(profile1);

        OrcidProfile profileWithWork1FromTwoDifferentSources = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(2, profileWithWork1FromTwoDifferentSources.getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        profileWithWork1FromTwoDifferentSources.getOrcidActivities().getOrcidWorks().getOrcidWork().forEach(work -> {
            if (work.getPutCode().equals(work1PutCode)) {
                assertEquals(CLIENT_1, work.retrieveSourcePath());
            } else {
                assertEquals(CLIENT_2, work.retrieveSourcePath());
            }
        });
    }

    @Test
    public void testCreateRecordWithClient1AddTryToAddExactDuplicate() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        OrcidWork work1 = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0);

        OrcidWork dupWork = createWork1();
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(dupWork);

        orcidProfileManager.addOrcidWorks(profile1);

        OrcidProfile updatedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        OrcidWork existingWork = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0);
        assertEquals(work1.getPutCode(), existingWork.getPutCode());
        assertEquals(work1, existingWork);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRecordWithClient1AddTryToAddADuplicate() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        OrcidWork dupWork = createWork1();
        dupWork.getWorkTitle().getTitle().setContent("Updated title");
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(dupWork);

        orcidProfileManager.addOrcidWorks(profile1);
        fail();
    }

    @Test
    public void testAddWorkRespectDefaultVisibility() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.LIMITED));
        orcidProfileManager.updatePreferences(TEST_ORCID, profile1.getOrcidInternal().getPreferences());

        OrcidWork work2 = createWork2();
        work2.setVisibility(Visibility.PUBLIC);
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work2);

        orcidProfileManager.updateOrcidWorks(profile1);

        OrcidProfile updatedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(2, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        assertEquals(Visibility.PRIVATE, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getVisibility());
        assertEquals(Visibility.LIMITED, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(1).getVisibility());

        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PUBLIC));
        orcidProfileManager.updatePreferences(TEST_ORCID, profile1.getOrcidInternal().getPreferences());

        OrcidWork work3 = createWork3();
        work3.setVisibility(Visibility.PRIVATE);
        profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work3);

        orcidProfileManager.updateOrcidWorks(profile1);

        updatedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(3, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().forEach(work -> {
            if (work.getWorkTitle().getTitle().getContent().equals("Test Title")) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
            } else if (work.getWorkTitle().getTitle().getContent().equals("Test Title # 2")) {
                assertEquals(Visibility.LIMITED, work.getVisibility());
            } else if (work.getWorkTitle().getTitle().getContent().equals("Test Title # 3")) {
                assertEquals(Visibility.PUBLIC, work.getVisibility());
            } else {
                fail("Unknown work " + work.getWorkTitle().getTitle().getContent());
            }
        });

        // Clear all works
        workManager.removeAllWorks(TEST_ORCID);

        // Start over with addOrcidWorks
        profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        profile1.setOrcidActivities(new OrcidActivities());
        profile1.getOrcidActivities().setOrcidWorks(new OrcidWorks());
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.LIMITED));
        orcidProfileManager.updatePreferences(TEST_ORCID, profile1.getOrcidInternal().getPreferences());

        work2 = createWork2();
        work2.setVisibility(Visibility.PUBLIC);
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work2);

        orcidProfileManager.addOrcidWorks(profile1);

        updatedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        //TODO: public?????
        assertEquals(Visibility.LIMITED, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getVisibility());

        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PUBLIC));
        orcidProfileManager.updatePreferences(TEST_ORCID, profile1.getOrcidInternal().getPreferences());

        work3 = createWork3();
        work3.setVisibility(Visibility.PRIVATE);
        profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile1);

        updatedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(2, updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().forEach(work -> {
            if (work.getWorkTitle().getTitle().getContent().equals("Test Title # 2")) {
                assertEquals(Visibility.LIMITED, work.getVisibility());
            } else if (work.getWorkTitle().getTitle().getContent().equals("Test Title # 3")) {
                assertEquals(Visibility.PUBLIC, work.getVisibility());
            } else {
                fail("Unknown work " + work.getWorkTitle().getTitle().getContent());
            }
        });
    }

    @Test
    public void testOrcidWorksHashCodeAndEquals() {
        OrcidWork workA = createWork1();
        OrcidWork workB = createWork1();
        assertEquals(workA, workB);
        assertEquals(workA.hashCode(), workB.hashCode());
    }

    @Test
    public void testDedupeWorks() {
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidWorks.getOrcidWork().add(createWork1());
        orcidWorks.getOrcidWork().add(createWork1());
        assertEquals(2, orcidWorks.getOrcidWork().size());

        OrcidWorks dedupedOrcidWorks = orcidProfileManager.dedupeWorks(orcidWorks);

        assertEquals(1, dedupedOrcidWorks.getOrcidWork().size());
    }

    @Test
    public void testAddWorks() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().size());
        String putCode1 = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        profile2.setOrcidActivities(new OrcidActivities());
        profile2.getOrcidActivities().setOrcidWorks(new OrcidWorks());
        profile2.getOrcidActivities().getOrcidWorks().getOrcidWork().clear();
        OrcidWorks orcidWorks = profile2.getOrcidActivities().getOrcidWorks();

        OrcidWork work1 = createWork1();
        orcidWorks.getOrcidWork().add(work1);

        OrcidWork work2 = createWork2();
        orcidWorks.getOrcidWork().add(work2);

        OrcidWork work3 = createWork3();
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);

        // Retry them again and verify that only work2 and work3 where created
        // Since work1 is an exact duplicated, it will just be ignored, so, we
        // must verify it keep having the same put code
        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(3, works.size());

        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        for (OrcidWork work : works) {
            //TODO: limited?????
            assertEquals(Visibility.PRIVATE, work.getVisibility());
            if (work.getWorkTitle().getTitle().getContent().equals("Test Title")) {
                assertEquals(putCode1, work.getPutCode());
                found1 = true;
            } else if (work.getWorkTitle().getTitle().getContent().equals("Test Title # 2")) {
                found2 = true;
            } else if (work.getWorkTitle().getTitle().getContent().equals("Test Title # 3")) {
                found3 = true;
            } else {
                fail("Invalid work found " + work.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddWorks_duplicatedShouldFail() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().size());

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        profile2.setOrcidActivities(new OrcidActivities());
        profile2.getOrcidActivities().setOrcidWorks(new OrcidWorks());
        profile2.getOrcidActivities().getOrcidWorks().getOrcidWork().clear();
        OrcidWorks orcidWorks = profile2.getOrcidActivities().getOrcidWorks();

        OrcidWork work1 = createWork1();
        work1.getWorkTitle().getTitle().setContent("Updated title");
        orcidWorks.getOrcidWork().add(work1);

        OrcidWork work2 = createWork2();
        orcidWorks.getOrcidWork().add(work2);

        OrcidWork work3 = createWork3();
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);
        fail("Must not allow different works with same ext ids from the same source");
    }

    @Test
    public void testAddOrcidWorkToUnclaimedProfile() {
        String orcid = "0000-0000-0000-0003";
        OrcidProfile profile1 = createBasicProfile();
        profile1.setOrcidIdentifier(orcid);
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PUBLIC));
        orcidProfileManager.createOrcidProfile(profile1, false, false);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(orcid);
        OrcidWorks orcidWorks = new OrcidWorks();
        profile2.setOrcidWorks(orcidWorks);

        OrcidWork work1 = createWork1();
        work1.setVisibility(Visibility.PUBLIC);
        orcidWorks.getOrcidWork().add(work1);

        OrcidWork work2 = createWork2();
        work2.setVisibility(Visibility.LIMITED);
        orcidWorks.getOrcidWork().add(work2);

        OrcidWork work3 = createWork3();
        work3.setVisibility(Visibility.PRIVATE);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
        assertEquals(3, resultProfile.retrieveOrcidWorks().getOrcidWork().size());

        for (OrcidWork work : resultProfile.retrieveOrcidWorks().getOrcidWork()) {
            if ("Test Title".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.PUBLIC, work.getVisibility());
            } else if ("Test Title # 2".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.LIMITED, work.getVisibility());
            } else if ("Test Title # 2".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
            }
        }

        workManager.removeAllWorks(orcid);
        orcidProfileManager.deleteProfile(orcid);
    }
}
