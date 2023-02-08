package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.BiographyManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: Declan Newman (declan) Date: 10/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class ProfileEntityManagerImplTest extends DBUnitTest {
    private static final String CLIENT_ID_1 = "APP-5555555555555555";   
    private static final String USER_ORCID = "0000-0000-0000-0001";    
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name = "profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private EmailManagerReadOnly emailManager;
    
    @Resource
    private RecordNameManager recordNameManager;
    
    @Resource
    private BiographyManager biographyManager;
    
    @Resource
    private UserConnectionDao userConnectionDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml", "/data/ClientDetailsEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Test
    public void testFindByOrcid() throws Exception {
        String harrysOrcid = "4444-4444-4444-4444";
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(harrysOrcid);
        assertNotNull(profileEntity);
        assertEquals(harrysOrcid, profileEntity.getId());
    }

    @Test    
    public void testReviewProfile() throws Exception {
    	boolean result = profileEntityManager.reviewProfile("4444-4444-4444-4441");
        assertTrue(result);
    	
    	result = profileEntityManager.unreviewProfile("4444-4444-4444-4442");
    	assertTrue(result);
    }           
}
