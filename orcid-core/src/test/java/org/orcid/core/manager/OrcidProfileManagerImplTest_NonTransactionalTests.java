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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Date;
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
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;

/**
 * @author Will Simpson
 */
public class OrcidProfileManagerImplTest_NonTransactionalTests extends OrcidProfileManagerBaseTest {

    private static final String TEST_CLIENT = "0000-0000-0000-0000";

    private static boolean initialized = false;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Mock
    private SourceManager mockSourceManager;
    
    @Resource
    private SourceManager sourceManager;

    @Before
    public void before() {
        if (!initialized) {
            OrcidProfile delegateProfile = new OrcidProfile();
            delegateProfile.setOrcidIdentifier(TEST_CLIENT);
            OrcidBio delegateBio = new OrcidBio();
            delegateProfile.setOrcidBio(delegateBio);
            PersonalDetails delegatePersonalDetails = new PersonalDetails();
            delegateBio.setPersonalDetails(delegatePersonalDetails);
            CreditName delegateCreditName = new CreditName("H. Shearer");
            delegateCreditName.setVisibility(Visibility.PUBLIC);
            delegatePersonalDetails.setCreditName(delegateCreditName);
            orcidProfileManager.createOrcidProfile(delegateProfile, false, false);

            ClientDetailsEntity clientDetails = new ClientDetailsEntity();
            clientDetails.setId(TEST_CLIENT);
            clientDetails.setGroupProfileId(TEST_CLIENT);
            clientDetailsManager.merge(clientDetails);

            orcidProfileManager.setCompareWorksUsingScopusWay(true);

            initialized = true;
        }

        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(TEST_CLIENT);
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(clientDetails);
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", mockSourceManager);
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);
        when(mockSourceManager.retrieveSourceOrcid()).thenReturn(clientDetails.getId());
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", sourceManager);        
    }

    @Test
    public void testUpdateProfileButRemoveWorkExternalIdentifier() {
        String orcid = "0000-0000-0000-0001";
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(orcid);
        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(orcid + "@test.com");
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

        List<WorkExternalIdentifier> workExternalIdentifiers = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers()
                .getWorkExternalIdentifier();
        assertEquals(3, workExternalIdentifiers.size());
        Iterator<WorkExternalIdentifier> workExternalIdentifiersIterator = workExternalIdentifiers.iterator();
        while (workExternalIdentifiersIterator.hasNext()) {
            if (WorkExternalIdentifierType.PMID.equals(workExternalIdentifiersIterator.next().getWorkExternalIdentifierType())) {
                workExternalIdentifiersIterator.remove();
            }
        }
        // Mark the work as modified
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).setModified(true);

        orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(orcid);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(2, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkExternalIdentifiers().getWorkExternalIdentifier().size());
    }

    @Test
    public void testUpdateProfileButRemoveWorkContributor() {
        String orcid = "0000-0000-0000-0002";
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(orcid);

        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(orcid + "@test.com");
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

        List<Contributor> contributors = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor();
        assertEquals(2, contributors.size());
        Iterator<Contributor> contributorsIterator = contributors.iterator();
        while (contributorsIterator.hasNext()) {
            if (SequenceType.ADDITIONAL.equals(contributorsIterator.next().getContributorAttributes().getContributorSequence())) {
                contributorsIterator.remove();
            }
        }

        // Mark the work as modified
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).setModified(true);

        orcidProfileManager.updateOrcidProfile(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(orcid);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals(1, resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getWorkContributors().getContributor().size());
    }

    @Test
    public void testAddOrcidWorksWithPublicDefaultVisibility() {
        String orcid = "0000-0000-0000-0003";
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(orcid);
        profile1.setOrcidHistory(getHistory(true));

        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(orcid + "@test.com");
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PUBLIC));
        profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).setVisibility(Visibility.PUBLIC);
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);
        String originalPutCode = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        OrcidWorks orcidWorks = new OrcidWorks();
        profile1.setOrcidWorks(orcidWorks);

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("Another Title"));
        workTitle2.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork work2 = createWork2(workTitle2);
        work2.setVisibility(Visibility.PRIVATE);
        orcidWorks.getOrcidWork().add(work2);

        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("New Title"));
        workTitle3.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work3 = createWork3(workTitle3);
        work3.setVisibility(Visibility.LIMITED);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(3, works.size());

        assertEquals("Another Title", works.get(0).getWorkTitle().getTitle().getContent());
        assertEquals("Journal of Cloud Spotting", works.get(0).getWorkTitle().getSubtitle().getContent());
        for (OrcidWork work : works) {
            assertEquals(Visibility.PUBLIC, work.getVisibility());
        }
        assertEquals("Put code of original work should not have changed", originalPutCode, works.get(2).getPutCode());
    }

    @Test
    public void testAddOrcidWorksWithPrivateDefaultVisibility() {
        String orcid = "0000-0000-0000-0004";
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(orcid);
        profile1.setOrcidHistory(getHistory(true));

        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(orcid + "@test.com");
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PRIVATE));
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);
        String originalPutCode = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        OrcidWorks orcidWorks = new OrcidWorks();
        profile1.setOrcidWorks(orcidWorks);

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("Another Title"));
        workTitle2.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork work2 = createWork2(workTitle2);
        work2.setVisibility(Visibility.PUBLIC);
        orcidWorks.getOrcidWork().add(work2);

        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("New Title"));
        workTitle3.setSubtitle(new Subtitle("Another New subtitle"));
        OrcidWork work3 = createWork3(workTitle3);
        work3.setVisibility(Visibility.LIMITED);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(3, works.size());

        assertEquals("Another Title", works.get(0).getWorkTitle().getTitle().getContent());
        assertEquals("Journal of Cloud Spotting", works.get(0).getWorkTitle().getSubtitle().getContent());
        for (OrcidWork work : works) {
            assertEquals(Visibility.PRIVATE, work.getVisibility());
        }
        assertEquals("Put code of original work should not have changed", originalPutCode, works.get(2).getPutCode());
    }

    @Test
    public void tryToAddDuplicatedWorkTest() {
        String orcid = "0000-0000-0000-0005";
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(orcid);
        profile1.setOrcidHistory(getHistory(true));

        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(orcid + "@test.com");
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

        OrcidWorks orcidWorks = new OrcidWorks();
        profile1.setOrcidWorks(orcidWorks);

        WorkTitle dupWorkTitle = new WorkTitle();
        dupWorkTitle.setTitle(new Title("Another Title"));
        dupWorkTitle.setSubtitle(new Subtitle("Journal of Cloud Spotting"));
        OrcidWork dupWork = createWork1(dupWorkTitle);
        orcidWorks.getOrcidWork().add(dupWork);

        try {
            orcidProfileManager.addOrcidWorks(profile1);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().endsWith("have the same external id \"work1-doi1\""));
        }
    }

    @Test
    public void testAddOrcidWorkToUnclaimedProfile() {
        String orcid = "0000-0000-0000-0006";
        OrcidProfile profile1 = createBasicProfile();
        // Change the orcid identifier
        profile1.setOrcidIdentifier(orcid);
        profile1.setOrcidHistory(getHistory(false));

        // Change the email address
        profile1.getOrcidBio().getContactDetails().retrievePrimaryEmail().setValue(orcid + "@test.com");
        profile1.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.PRIVATE));
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

        OrcidWorks orcidWorks = new OrcidWorks();
        profile1.setOrcidWorks(orcidWorks);

        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("Public work"));
        OrcidWork work2 = createWork2(workTitle2);
        work2.setVisibility(Visibility.PUBLIC);
        orcidWorks.getOrcidWork().add(work2);

        // Try to add a duplicate
        WorkTitle workTitle3 = new WorkTitle();
        workTitle3.setTitle(new Title("Limited work"));
        OrcidWork work3 = createWork3(workTitle3);
        work3.setVisibility(Visibility.LIMITED);
        orcidWorks.getOrcidWork().add(work3);

        orcidProfileManager.addOrcidWorks(profile1);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        List<OrcidWork> works = resultProfile.retrieveOrcidWorks().getOrcidWork();
        assertEquals(3, works.size());

        boolean publicFound = false;
        boolean limitedFound = false;
        boolean privateFound = false;
        for (OrcidWork work : works) {
            if ("Public work".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.PUBLIC, work.getVisibility());
                publicFound = true;
            } else if ("Limited work".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.LIMITED, work.getVisibility());
                limitedFound = true;
            } else if ("Test Title".equals(work.getWorkTitle().getTitle().getContent())) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
                privateFound = true;
            } else {
                fail();
            }
        }
        assertTrue(publicFound);
        assertTrue(limitedFound);
        assertTrue(privateFound);
    }    
    
    @Test
    public void testDedupeWorks() {
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidWorks.getOrcidWork().add(createWork1());
        orcidWorks.getOrcidWork().add(createWork1());
        assertEquals(2, orcidWorks.getOrcidWork().size());

        OrcidWorks dedupedOrcidWorks = orcidProfileManager.dedupeWorks(orcidWorks);

        assertEquals(1, dedupedOrcidWorks.getOrcidWork().size());

        orcidWorks = new OrcidWorks();
        orcidWorks.getOrcidWork().add(createWork1());
        orcidWorks.getOrcidWork().add(createWork1());
        orcidWorks.getOrcidWork().add(createWork1());
        orcidWorks.getOrcidWork().add(createWork1());
        assertEquals(4, orcidWorks.getOrcidWork().size());

        dedupedOrcidWorks = orcidProfileManager.dedupeWorks(orcidWorks);

        assertEquals(1, dedupedOrcidWorks.getOrcidWork().size());
    }
    
    private OrcidHistory getHistory(boolean claimed) {
        OrcidHistory history = new OrcidHistory();
        history.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        history.setClaimed(new Claimed(claimed));
        return history;
    }
}
