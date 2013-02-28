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
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SubjectEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class OrcidMultiThreadedIndexingTest extends OrcidProfileManagerBaseTest {

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

    }

    @After
    public void after() {
        for (ProfileEntity profileEntity : profileDao.getAll()) {
            orcidProfileManager.deleteProfile(profileEntity.getId());
        }
    }

    /**
     * This test is in a class of its own, so it doesn't interfere with other
     * tests. It's non a transactional test with rollback, because it's
     * multi-threaded, and transactions/db locking get in the way of the test.
     */
    @Test
    public void testProcessProfilesPendingIndexing() {
        reset(orcidIndexManager);
        assertEquals(2, profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, Integer.MAX_VALUE).size());
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1);
        assertEquals(3, profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, Integer.MAX_VALUE).size());
        orcidProfileManager.processProfilesPendingIndexing();
        verify(orcidIndexManager, times(1)).persistProfileInformationForIndexing(argThat(OrcidIndexManagerTypeMatcherTestFactory.orcidBasicProfileCreate()));
        assertEquals(0, profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, Integer.MAX_VALUE).size());
    }

}
