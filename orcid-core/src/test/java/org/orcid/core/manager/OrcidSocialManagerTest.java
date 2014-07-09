package org.orcid.core.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;

public class OrcidSocialManagerTest extends BaseTest {

    @Resource
    OrcidSocialManager orcidSocialManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(
                Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"),
                null);
    }

    @Before
    public void before() {
        assertNotNull(orcidSocialManager);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(
                Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"),
                null);
    }
    

    @Test
    public void testGetTwitterAuthUrl() {
        try {
            String url = orcidSocialManager.getTwitterAuthorizationUrl("4444-4444-4444-4442");
            assertNotNull(url);
        } catch(Exception e) {
            fail();
        }        
    }

}
