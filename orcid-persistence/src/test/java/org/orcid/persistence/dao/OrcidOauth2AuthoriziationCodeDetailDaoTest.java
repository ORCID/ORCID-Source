package org.orcid.persistence.dao;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @author Declan Newman (declan) Date: 24/04/2012
 */

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
@Transactional
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

    @Test
    public void removeArchivedAuthorizationCodes_now_Test() {
        List<OrcidOauth2AuthoriziationCodeDetail> all = orcidOauth2AuthoriziationCodeDetailDao.getAll();
        assertEquals(4, all.size());
        Boolean anyDeleted = orcidOauth2AuthoriziationCodeDetailDao.removeArchivedAuthorizationCodes(new java.util.Date());
        assertTrue(anyDeleted);
        all = orcidOauth2AuthoriziationCodeDetailDao.getAll();
        assertEquals(1, all.size());
        assertEquals("code4", all.get(0).getId());
        assertTrue(all.get(0).getDateCreated().after(new Date()));
    }

    @Test
    public void removeArchivedAuthorizationCodes_2010_Test() {
        List<OrcidOauth2AuthoriziationCodeDetail> all = orcidOauth2AuthoriziationCodeDetailDao.getAll();
        assertEquals(4, all.size());

        ZonedDateTime zdt = LocalDate.of(2010, 1, 1).atStartOfDay(ZoneId.systemDefault());
        Date jan01_2010 = Date.from(zdt.toInstant());

        Boolean anyDeleted = orcidOauth2AuthoriziationCodeDetailDao.removeArchivedAuthorizationCodes(jan01_2010);
        assertFalse(anyDeleted);
        all = orcidOauth2AuthoriziationCodeDetailDao.getAll();
        assertEquals(4, all.size());

        for(OrcidOauth2AuthoriziationCodeDetail code : all) {
            assertFalse(code.getDateCreated().before(jan01_2010));
        }
    }

    @Test
    public void removeArchivedAuthorizationCodes_2020_Test() {
        List<OrcidOauth2AuthoriziationCodeDetail> all = orcidOauth2AuthoriziationCodeDetailDao.getAll();
        assertEquals(4, all.size());

        ZonedDateTime zdt = LocalDate.of(2020, 1, 1).atStartOfDay(ZoneId.systemDefault());
        Date jan01_2020 = Date.from(zdt.toInstant());

        Boolean anyDeleted = orcidOauth2AuthoriziationCodeDetailDao.removeArchivedAuthorizationCodes(jan01_2020);
        assertTrue(anyDeleted);
        all = orcidOauth2AuthoriziationCodeDetailDao.getAll();
        assertEquals(2, all.size());

        for(OrcidOauth2AuthoriziationCodeDetail code : all) {
            assertFalse(code.getDateCreated().before(jan01_2020));
            assertTrue(code.getId().equals("code3") || code.getId().equals("code4"));
        }
    }
}
