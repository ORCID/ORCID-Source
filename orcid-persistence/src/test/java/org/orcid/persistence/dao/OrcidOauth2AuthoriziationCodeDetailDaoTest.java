package org.orcid.persistence.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Declan Newman (declan) Date: 24/04/2012
 */

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class OrcidOauth2AuthoriziationCodeDetailDaoTest extends DBUnitTest {

    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;

    @Resource(name = "profileDao")
    private ProfileDao profileDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }
    
    @Test
    public void isPersistentTokenTest() {
        assertTrue(orcidOauth2AuthoriziationCodeDetailDao.isPersistentToken("code1"));
        assertFalse(orcidOauth2AuthoriziationCodeDetailDao.isPersistentToken("code2"));
        assertTrue(orcidOauth2AuthoriziationCodeDetailDao.isPersistentToken("code3"));
    }
}
