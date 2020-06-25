package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.tuple.Pair;
import org.dbunit.dataset.DataSetException;
import org.joda.time.LocalDateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EmailEventEntity;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidEntityIdComparator;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SubjectEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * orcid-persistence - Dec 6, 2011 - ProfileEntityDaoTest
 * 
 * @author Declan Newman (declan)
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-profile-last-modified-aspect-disabled-context.xml" })
public class ProfileDaoTest extends DBUnitTest {

    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private EmailDao emailDao;
    
    @Resource
    private GenericDao<EmailEventEntity, Long> emailEventDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private GenericDao<ProfileEventEntity, Long> profileEventDao;
    
    @Resource(name="entityManager")
    protected EntityManager entityManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/RecordNameEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        
        assertNotNull(profileDao);
    }
    
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindById() {
        ProfileEntity profile = profileDao.find("4444-4444-4444-4442");
        assertNotNull(profile);
        assertEquals("4444-4444-4444-4442", profile.getId());
        assertEquals("API", profile.getCreationMethod());
        assertNotNull(profile.getCompletedDate());
        assertNotNull(profile.getSubmissionDate());
        assertTrue(profile.getClaimed());        
    }        

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testFindAll() {
        List<ProfileEntity> all = profileDao.getAll();
        assertNotNull(all);
        assertEquals(22, all.size());
        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(22), count);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsert() throws DataSetException {
        String newOrcid = "4444-1111-6666-4441";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(newOrcid);

        profileDao.persist(profile);
        profileDao.merge(profile);

        profileDao.flush();
        profile = profileDao.find(profile.getId());
        assertNotNull(profile.getDateCreated());
        assertNotNull(profile.getLastModified());
        assertEquals(profile.getDateCreated(), profile.getLastModified());
        
        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(23), count);
        profile = profileDao.find(newOrcid);

        assertNotNull(profile);
        assertEquals(newOrcid, profile.getId());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertWithPrimaryInstitutions() throws DataSetException {
        String newOrcid = "4444-1111-6666-4442";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(newOrcid);

        profileDao.persist(profile);
        profileDao.merge(profile);

        profileDao.flush();
        profile = profileDao.find(profile.getId());
        assertNotNull(profile.getDateCreated());
        assertNotNull(profile.getLastModified());

        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(23), count);
        profile = profileDao.find(newOrcid);

        assertNotNull(profile);
        assertEquals(newOrcid, profile.getId());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertWithInstitutionDepartments() throws DataSetException {
        String newOrcid = "4444-1111-6666-4443";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(newOrcid);

        profileDao.persist(profile);
        profileDao.merge(profile);
        profileDao.flush();

        ProfileEntity retrievedProfile = profileDao.find(newOrcid);
        assertNotNull(retrievedProfile);
        assertEquals(newOrcid, retrievedProfile.getId());
        assertNotNull(retrievedProfile.getDateCreated());
        assertNotNull(retrievedProfile.getLastModified());

        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(23), count);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertWithSubjectsAndKeywords() {
        String newOrcid = "4444-1111-6666-4444";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(newOrcid);
        Set<SubjectEntity> subjects = new HashSet<SubjectEntity>(2);
        // profile.setSubjects(subjects);
        subjects.add(new SubjectEntity("Rhymin"));
        subjects.add(new SubjectEntity("Stealin"));
        SortedSet<ProfileKeywordEntity> keywords = new TreeSet<ProfileKeywordEntity>();
        profile.setKeywords(keywords);
        
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        entity.setProfile(profile);
        entity.setKeywordName("Bilocation");        
        keywords.add(entity);
        
        entity = new ProfileKeywordEntity();
        entity.setProfile(profile);
        entity.setKeywordName("Humour");        
        keywords.add(entity);
        
        entity = new ProfileKeywordEntity();
        entity.setProfile(profile);
        entity.setKeywordName("Ceramics");        
        keywords.add(entity);
        
        profileDao.persist(profile);
        profileDao.flush();

        profile = profileDao.find(newOrcid);

        assertNotNull(profile);
        assertEquals(newOrcid, profile.getId());
        // assertEquals(2, profile.getSubjects().size());
        assertEquals(3, profile.getKeywords().size());
    }


    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testOrcidExists() {
        assertTrue(profileDao.orcidExists("4444-4444-4444-4442"));
        assertFalse(profileDao.orcidExists("4445-4444-4444-4442"));
    }    

    @Test
    public void testOrcidsFindByIndexingStatus() {
        List<Pair<String, IndexingStatus>> results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, 10, 0);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("4444-4444-4444-4445", results.get(0).getLeft());
        assertEquals("4444-4444-4444-4446", results.get(1).getLeft());

        results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, Integer.MAX_VALUE, 0);
        assertEquals(20, results.size());

        results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, 3, 0);
        assertEquals(3, results.size());
    }

    @Test
    public void testFindUnclaimedNotIndexedAfterWaitPeriod() {
        List<String> resultsList = profileDao.findUnclaimedNotIndexedAfterWaitPeriod(1, 100000, 10, Collections.<String> emptyList());
        assertNotNull(resultsList);
        assertEquals(2, resultsList.size());

        // test far back
        resultsList = profileDao.findUnclaimedNotIndexedAfterWaitPeriod(100000, 200000, 10, Collections.<String> emptyList());
        assertNotNull(resultsList);
        assertEquals(0, resultsList.size());

        // test range that fits test data
        resultsList = profileDao.findUnclaimedNotIndexedAfterWaitPeriod(5, 100000, 10, Collections.<String> emptyList());
        assertNotNull(resultsList);
        assertEquals(3, resultsList.size());
        assertTrue(resultsList.contains("4444-4444-4444-4447"));
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testFindUnclaimedNeedingReminder() {
        List<String> results = profileDao.findUnclaimedNeedingReminder(1, 10, Collections.<String> emptyList());
        assertNotNull(results);
        assertEquals(3, results.size());
        assertTrue(results.contains("4444-4444-4444-4447"));

        // Now insert claimed reminder event, result should be excluded
        // thereafter.
        ProfileEventEntity eventEntity = new ProfileEventEntity();
        eventEntity.setOrcid("4444-4444-4444-4447");
        eventEntity.setType(ProfileEventType.CLAIM_REMINDER_SENT);
        profileEventDao.persist(eventEntity);

        results = profileDao.findUnclaimedNeedingReminder(1, 10, Collections.<String> emptyList());
        assertEquals(2, results.size());
    }

    @Test
    public void testUpdateIndexingStatus() {
        Date now = new Date();
        int startCount = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, Integer.MAX_VALUE, 0).size();
        String orcid = "4444-4444-4444-4446";
        ProfileEntity profileEntity = profileDao.find(orcid);
        assertEquals(IndexingStatus.PENDING, profileEntity.getIndexingStatus());
        profileDao.updateIndexingStatus(orcid, IndexingStatus.DONE);
        ProfileEntity result = profileDao.find(orcid);
        assertEquals(IndexingStatus.DONE, result.getIndexingStatus());
        assertNotNull(result.getLastIndexedDate());
        assertFalse(now.after(new Date(result.getLastIndexedDate().getTime())));
        int endCount = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, Integer.MAX_VALUE, 0).size();
        assertEquals(startCount + 1, endCount);
        profileDao.updateIndexingStatus(orcid, IndexingStatus.PENDING);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testGetConfirmedProfileCount() {
        String orcid = "4444-4444-4444-4446";
        Long confirmedProfileCount = profileDao.getConfirmedProfileCount();
        assertEquals(Long.valueOf(22), confirmedProfileCount);
        ProfileEntity profileEntity = profileDao.find(orcid);
        profileEntity.setCompletedDate(null);
        profileDao.persist(profileEntity);
        confirmedProfileCount = profileDao.getConfirmedProfileCount();
        assertEquals(Long.valueOf(21), confirmedProfileCount);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testDeprecateProfile() {
        ProfileEntity profileToDeprecate = profileDao.find("4444-4444-4444-4441");
        assertNull(profileToDeprecate.getPrimaryRecord());
        boolean result = profileDao.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442", ProfileEntity.ADMIN_DEPRECATION, "4444-4444-4444-4440");
        assertTrue(result);
        profileToDeprecate = profileDao.find("4444-4444-4444-4441");
        profileDao.refresh(profileToDeprecate);
        assertNotNull(profileToDeprecate.getPrimaryRecord());
        assertNotNull(profileToDeprecate.getDeprecatedMethod());
        assertEquals(ProfileEntity.ADMIN_DEPRECATION, profileToDeprecate.getDeprecatedMethod());
        assertNotNull(profileToDeprecate.getDeprecatingAdmin());
        assertEquals("4444-4444-4444-4440", profileToDeprecate.getDeprecatingAdmin());
        ProfileEntity primaryRecord = profileToDeprecate.getPrimaryRecord();
        assertEquals("4444-4444-4444-4442", primaryRecord.getId());
    }
    
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAutoDeprecateProfile() {
        ProfileEntity profileToDeprecate = profileDao.find("4444-4444-4444-4441");
        assertNull(profileToDeprecate.getPrimaryRecord());
        boolean result = profileDao.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442", ProfileEntity.AUTO_DEPRECATION, null);
        assertTrue(result);
        profileToDeprecate = profileDao.find("4444-4444-4444-4441");
        profileDao.refresh(profileToDeprecate);
        assertNotNull(profileToDeprecate.getPrimaryRecord());
        assertNotNull(profileToDeprecate.getDeprecatedMethod());
        assertEquals(ProfileEntity.AUTO_DEPRECATION, profileToDeprecate.getDeprecatedMethod());
        assertNull(profileToDeprecate.getDeprecatingAdmin());
        ProfileEntity primaryRecord = profileToDeprecate.getPrimaryRecord();
        assertEquals("4444-4444-4444-4442", primaryRecord.getId());
    }
    
    @Test
    public void testGetClaimedStatusByEmail() {
        assertFalse(profileDao.getClaimedStatusByEmailHash("8f649f6d12203f020ee26467547432add46bc5395ff8dd72242fa2d7aa4fc04a"));        
        assertTrue(profileDao.getClaimedStatusByEmailHash("ecdc2c6aef7aa5aa4012b9e5f262de2214c9b0e3f3b0201da0eeebc7531ae018"));
        assertTrue(profileDao.getClaimedStatusByEmailHash("c3ba0b26aceb622a04908c202927db3633bd1e748e049e4bd2b070d29b189aa4"));
        assertTrue(profileDao.getClaimedStatusByEmailHash("71d1e18acf189e7b14e486a53691cef30249a3aedfd5b4c988b1754eb179e6b9"));
        assertTrue(profileDao.getClaimedStatusByEmailHash("4cccdb9a8342f8e7e7b730b0870664f9428f6958082957ed36e22997525fe7ce"));
    }       
    
    @Test
    @Rollback(true)
    public void testUpdateDefaultVisibility() {
        ProfileEntity entity1 = profileDao.find("1000-0000-0000-0001");
        ProfileEntity entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals("PUBLIC", entity1.getActivitiesVisibilityDefault());
        assertEquals("PUBLIC", entity6.getActivitiesVisibilityDefault());
        
        // Set it private
        assertTrue(profileDao.updateDefaultVisibility("0000-0000-0000-0006", "PRIVATE"));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals("PUBLIC", entity1.getActivitiesVisibilityDefault());
        assertEquals("PRIVATE", entity6.getActivitiesVisibilityDefault());
        
        // Set it limited
        assertTrue(profileDao.updateDefaultVisibility("0000-0000-0000-0006", "LIMITED"));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals("PUBLIC", entity1.getActivitiesVisibilityDefault());
        assertEquals("LIMITED", entity6.getActivitiesVisibilityDefault());
        
        // Set it public again
        assertTrue(profileDao.updateDefaultVisibility("0000-0000-0000-0006", "PUBLIC"));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals("PUBLIC", entity1.getActivitiesVisibilityDefault());
        assertEquals("PUBLIC", entity6.getActivitiesVisibilityDefault());
    }
    
    @Test
    public void testDisable2FA() {
        ProfileEntity profile = profileDao.find("2000-0000-0000-0002");
        assertTrue(profile.getUsing2FA());
        profileDao.disable2FA("2000-0000-0000-0002");
        profile = profileDao.find("2000-0000-0000-0002");
        assertFalse(profile.getUsing2FA());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void findEmailsUnverfiedDaysTest() throws IllegalAccessException {
        String orcid = "9999-9999-9999-999X";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(orcid);
        profile.setClaimed(true);
        profileDao.persist(profile);
        profileDao.flush();
        emailDao.removeAll();
        
        // Created today
        assertEquals(1, insertEmailWithDateCreated("unverified_1@test.orcid.org", "bd22086b65b6259fe79f7844a6b6a369441733b9ef04eff762f3d640957b78f5", orcid, false, new Date()));
        
        // Created a week ago
        assertEquals(1, insertEmailWithDateCreated("unverified_2@test.orcid.org", "95770578974f683fb05c179a84f57c3fc7d4b260f8079fbc590080e51873bb67", orcid, false, LocalDateTime.now().minusDays(7).toDate()));
           
        // Created 15 days ago
        assertEquals(1, insertEmailWithDateCreated("unverified_3@test.orcid.org", "3cbebfc1de2500494fc95553c956e757cb1998149d366afb71888cdeb1550719", orcid, false, LocalDateTime.now().minusDays(15).toDate()));
        
        // Created 7 days ago and verified
        assertEquals(1, insertEmailWithDateCreated("verified_1@test.orcid.org", "2f4812b9c675e9803a4bb616dd1bc241c8c9302ba5690a1ea9d48049a32e7c5f", orcid, true, LocalDateTime.now().minusDays(7).toDate()));
        
        // Created 15 days ago and verified
        assertEquals(1, insertEmailWithDateCreated("verified_2@test.orcid.org", "896dea808bbf69bde1b177f27800e84d17763860bffde1dfd8ef200e79ff9971", orcid, true, LocalDateTime.now().minusDays(15).toDate()));
        
        List<Pair<String, Date>> results = profileDao.findEmailsUnverfiedDays(7, 100);
        assertNotNull(results);
        assertEquals(2, results.size());
        
        boolean found1 = false, found2 = false;
        
        for(Pair<String, Date> element : results) {
            assertNotNull(element.getRight());
            if(element.getLeft().equals("unverified_2@test.orcid.org")) {
                found1 = true;
            } else if(element.getLeft().equals("unverified_3@test.orcid.org")) {
                found2 = true;
            } else {
                fail("Unexpected email id: " + element.getRight());
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        
        // Put an email event on 'unverified_2@test.orcid.org' and verify there is only one result
        emailEventDao.persist(new EmailEventEntity("unverified_2@test.orcid.org", EmailEventType.VERIFY_EMAIL_7_DAYS_SENT));
        
        results = profileDao.findEmailsUnverfiedDays(7, 100);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("unverified_3@test.orcid.org", results.get(0).getLeft());
        
        // Put an email event on 'unverified_3@test.orcid.org' and verify there is no result anymore
        emailEventDao.persist(new EmailEventEntity("unverified_3@test.orcid.org", EmailEventType.VERIFY_EMAIL_TOO_OLD));
        results = profileDao.findEmailsUnverfiedDays(7, 100);
        assertNotNull(results);
        assertTrue(results.isEmpty());        
    }
    
    private int insertEmailWithDateCreated(String email, String emailHash, String orcid, boolean isVerified, Date dateCreated) {
        Query q = entityManager.createNativeQuery(
        "INSERT INTO email(email,email_hash,orcid,source_id,visibility,is_primary,is_current,is_verified,date_created,last_modified) "
        + "values(:email, :emailHash, :orcid, :sourceId, :visibility, :isPrimary, :isCurrent, :isVerified, :dateCreated, :lastModified)");
        q.setParameter("email", email);
        q.setParameter("emailHash", emailHash);
        q.setParameter("orcid", orcid);
        q.setParameter("sourceId", orcid);
        q.setParameter("visibility", "PUBLIC");
        q.setParameter("isPrimary", false);
        q.setParameter("isCurrent", false);
        q.setParameter("isVerified", isVerified);
        q.setParameter("dateCreated", dateCreated);
        q.setParameter("lastModified", dateCreated);
        return q.executeUpdate();
    }
}
