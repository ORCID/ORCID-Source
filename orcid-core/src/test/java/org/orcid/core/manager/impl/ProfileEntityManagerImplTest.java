package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;

@RunWith(MockitoJUnitRunner.class)
public class ProfileEntityManagerImplTest {

    @InjectMocks
    private ProfileEntityManagerImpl profileEntityManager;

    @Mock
    private ProfileDao profileDao;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        profileEntityManager.setProfileDao(profileDao);
    }

    @Test
    public void testFindByOrcid() throws Exception {
        String harrysOrcid = "4444-4444-4444-4444";
        ProfileEntity profileEntity = new ProfileEntity(harrysOrcid);
        when(profileEntityCacheManager.retrieve(harrysOrcid)).thenReturn(profileEntity);

        ProfileEntity result = profileEntityCacheManager.retrieve(harrysOrcid);
        assertNotNull(result);
        assertEquals(harrysOrcid, result.getId());
    }

    @Test
    public void testReviewProfile() throws Exception {
        String orcid1 = "4444-4444-4444-4441";
        String orcid2 = "4444-4444-4444-4442";

        when(profileDao.reviewProfile(orcid1)).thenReturn(true);
        when(profileDao.unreviewProfile(orcid2)).thenReturn(true);

        boolean result = profileEntityManager.reviewProfile(orcid1);
        assertTrue(result);

        result = profileEntityManager.unreviewProfile(orcid2);
        assertTrue(result);
    }
}
