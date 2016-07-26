package org.orcid.core.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class Jpa2JaxbAdapterTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/BiographyEntityData.xml", "/data/RecordNameEntityData.xml", "/data/WorksEntityData.xml", "/data/OrgsEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/ProfileFundingEntityData.xml");
    private final String userOrcid = "0000-0000-0000-0003";
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private Jpa2JaxbAdapter adapter;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testSourceOnAllElements() {
        ProfileEntity profileEntity = profileDao.find(userOrcid);
        assertNotNull(profileEntity);
        assertEquals(userOrcid, profileEntity.getId());
        OrcidProfile orcidProfile = adapter.toOrcidProfile(profileEntity);
        assertNotNull(orcidProfile);
        assertNotNull(orcidProfile.getOrcidActivities());
        assertNotNull(orcidProfile.getOrcidBio());
        
    }
    
}
