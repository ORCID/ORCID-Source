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

import org.apache.commons.lang3.tuple.Pair;
import org.dbunit.dataset.DataSetException;
import org.joda.time.LocalDateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
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

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/RecordNameEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
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
        assertNotNull(profile.getRecordNameEntity());
        assertEquals("Given Names", profile.getRecordNameEntity().getGivenNames());
        assertEquals("Family Name", profile.getRecordNameEntity().getFamilyName());
        assertEquals("Credit Name", profile.getRecordNameEntity().getCreditName());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC, profile.getRecordNameEntity().getVisibility());
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

        Date dateCreated = new Date();
        profile.setDateCreated(dateCreated);
        profileDao.merge(profile);

        profileDao.flush();
        profile = profileDao.find(profile.getId());
        assertEquals(dateCreated.getTime(), profile.getDateCreated().getTime());

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

        Date dateCreated = new Date();
        profile.setDateCreated(dateCreated);
        profileDao.merge(profile);

        profileDao.flush();
        profile = profileDao.find(profile.getId());
        assertEquals(dateCreated.getTime(), profile.getDateCreated().getTime());

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

        Date dateCreated = new Date();
        profile.setDateCreated(dateCreated);
        profileDao.merge(profile);
        profileDao.flush();

        ProfileEntity retrievedProfile = profileDao.find(newOrcid);
        assertNotNull(retrievedProfile);
        assertEquals(newOrcid, retrievedProfile.getId());
        assertEquals(dateCreated.getTime(), retrievedProfile.getDateCreated().getTime());

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
    public void testInsertGroupWithClients() {
        String groupOrcid = "4444-1111-6666-4444";
        ProfileEntity groupProfile = new ProfileEntity();
        groupProfile.setId(groupOrcid);
        groupProfile.setOrcidType(OrcidType.GROUP);
        groupProfile.setGroupType(MemberType.BASIC);

        SortedSet<ClientDetailsEntity> clients = new TreeSet<>(new OrcidEntityIdComparator<String>());
        String clientOrcid1 = "4444-4444-4444-4442";
        ClientDetailsEntity clientProfile1 = new ClientDetailsEntity();
        clientProfile1.setId(clientOrcid1);
        clients.add(clientProfile1);
        String clientOrcid2 = "4444-4444-4444-4443";
        ClientDetailsEntity clientProfile2 = new ClientDetailsEntity();
        clientProfile2.setId(clientOrcid2);
        clients.add(clientProfile2);
        groupProfile.setClients(clients);

        profileDao.persist(groupProfile);
        profileDao.flush();

        groupProfile = profileDao.find(groupOrcid);

        assertNotNull(groupProfile);
        assertEquals(groupOrcid, groupProfile.getId());
        assertEquals(MemberType.BASIC, groupProfile.getGroupType());
        assertNotNull(groupProfile.getClients());
        assertEquals(2, groupProfile.getClients().size());
        Map<String, ClientDetailsEntity> map = ProfileEntity.mapById(groupProfile.getClients());
        assertTrue(map.containsKey(clientOrcid1));
        assertTrue(map.containsKey(clientOrcid2));
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertClient() {
        String clientOrcid = "4444-1111-6666-4444";
        ClientDetailsEntity client = new ClientDetailsEntity();
        client.setId(clientOrcid);
        String groupOrcid = "4444-4444-4444-4441";
        client.setGroupProfileId(groupOrcid);

        clientDetailsDao.persist(client);
        clientDetailsDao.flush();

        client = clientDetailsDao.find(clientOrcid);
        assertNotNull(client);
        assertEquals(clientOrcid, client.getId());

        ProfileEntity groupProfile = profileDao.find(groupOrcid);
        assertNotNull(groupProfile);
        assertNotNull(groupProfile.getClients());
        assertEquals(1, groupProfile.getClients().size());
        assertEquals(clientOrcid, groupProfile.getClients().iterator().next().getId());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testRemove() {
        ProfileEntity profile = profileDao.find("4444-4444-4444-4442");
        profileDao.remove(profile);
        profileDao.flush();
        profile = profileDao.find("4444-4444-4444-4442");
        assertNull(profile);

        profile = profileDao.find("4444-4444-4444-4443");
        assertNotNull(profile);
        profileDao.remove(profile.getId());
        profileDao.flush();

        profile = profileDao.find("4444-4444-4444-4443");
        assertNull(profile);

        List<ProfileEntity> all = profileDao.getAll();
        assertEquals(20, all.size());
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
        List<Pair<String, IndexingStatus>> results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, 10);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("4444-4444-4444-4445", results.get(0).getLeft());
        assertEquals("4444-4444-4444-4446", results.get(1).getLeft());

        results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, Integer.MAX_VALUE);
        assertEquals(20, results.size());

        results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, 3);
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
        int startCount = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, Integer.MAX_VALUE).size();
        String orcid = "4444-4444-4444-4446";
        ProfileEntity profileEntity = profileDao.find(orcid);
        assertEquals(IndexingStatus.PENDING, profileEntity.getIndexingStatus());
        profileDao.updateIndexingStatus(orcid, IndexingStatus.DONE);
        ProfileEntity result = profileDao.find(orcid);
        assertEquals(IndexingStatus.DONE, result.getIndexingStatus());
        assertNotNull(result.getLastIndexedDate());
        assertFalse(now.after(new Date(result.getLastIndexedDate().getTime())));
        int endCount = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, Integer.MAX_VALUE).size();
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
        assertFalse(profileDao.getClaimedStatusByEmail("public_0000-0000-0000-0001@test.orcid.org"));
        assertFalse(profileDao.getClaimedStatusByEmail("PUBLIC_0000-0000-0000-0001@test.orcid.org"));
        assertTrue(profileDao.getClaimedStatusByEmail("public_0000-0000-0000-0002@test.orcid.org"));
        assertTrue(profileDao.getClaimedStatusByEmail("PUBLIC_0000-0000-0000-0002@test.orcid.org"));
        assertTrue(profileDao.getClaimedStatusByEmail("public_0000-0000-0000-0003@test.orcid.org"));
        assertTrue(profileDao.getClaimedStatusByEmail("pUbLiC_0000-0000-0000-0003@test.orcid.org"));
        assertTrue(profileDao.getClaimedStatusByEmail("limited_0000-0000-0000-0003@test.orcid.org"));
        assertTrue(profileDao.getClaimedStatusByEmail("private_0000-0000-0000-0003@test.orcid.org"));
    }
    
    @Test
    @Rollback(true)
    public void testUpdateNotificationsPreferences() {
        ProfileEntity entity1 = profileDao.find("1000-0000-0000-0001");
        ProfileEntity entity6 = profileDao.find("0000-0000-0000-0006");
        assertFalse(entity1.getSendChangeNotifications());
        assertFalse(entity1.getSendAdministrativeChangeNotifications());
        assertFalse(entity1.getSendOrcidNews());
        assertFalse(entity1.getSendMemberUpdateRequests());        
        
        assertFalse(entity6.getSendChangeNotifications());
        assertFalse(entity6.getSendAdministrativeChangeNotifications());
        assertFalse(entity6.getSendOrcidNews());
        assertFalse(entity6.getSendMemberUpdateRequests());                       
        
        // Enable some preferences
        assertTrue(profileDao.updateNotificationsPreferences("0000-0000-0000-0006", true, false, true, false));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        // Nothing changed on entity1
        assertFalse(entity1.getSendChangeNotifications());
        assertFalse(entity1.getSendAdministrativeChangeNotifications());
        assertFalse(entity1.getSendOrcidNews());
        assertFalse(entity1.getSendMemberUpdateRequests());        
        
        // Updates on entity6
        assertTrue(entity6.getSendChangeNotifications());
        assertFalse(entity6.getSendAdministrativeChangeNotifications());
        assertTrue(entity6.getSendOrcidNews());
        assertFalse(entity6.getSendMemberUpdateRequests());  
        
        // Enable all preferences
        assertTrue(profileDao.updateNotificationsPreferences("0000-0000-0000-0006", true, true, true, true));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        // Nothing changed on entity1
        assertFalse(entity1.getSendChangeNotifications());
        assertFalse(entity1.getSendAdministrativeChangeNotifications());
        assertFalse(entity1.getSendOrcidNews());
        assertFalse(entity1.getSendMemberUpdateRequests());        
        
        // Updates on entity6
        assertTrue(entity6.getSendChangeNotifications());
        assertTrue(entity6.getSendAdministrativeChangeNotifications());
        assertTrue(entity6.getSendOrcidNews());
        assertTrue(entity6.getSendMemberUpdateRequests());  
        
        // Disable all preferences
        assertTrue(profileDao.updateNotificationsPreferences("0000-0000-0000-0006", false, false, false, false));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        // Nothing changed on entity1
        assertFalse(entity1.getSendChangeNotifications());
        assertFalse(entity1.getSendAdministrativeChangeNotifications());
        assertFalse(entity1.getSendOrcidNews());
        assertFalse(entity1.getSendMemberUpdateRequests());        
        
        // Updates on entity6
        assertFalse(entity6.getSendChangeNotifications());
        assertFalse(entity6.getSendAdministrativeChangeNotifications());
        assertFalse(entity6.getSendOrcidNews());
        assertFalse(entity6.getSendMemberUpdateRequests()); 
    }
    
    @Test
    @Rollback(true)
    public void testUpdateDefaultVisibility() {
        ProfileEntity entity1 = profileDao.find("1000-0000-0000-0001");
        ProfileEntity entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Visibility.PUBLIC, entity1.getActivitiesVisibilityDefault());
        assertEquals(Visibility.PUBLIC, entity6.getActivitiesVisibilityDefault());
        
        // Set it private
        assertTrue(profileDao.updateDefaultVisibility("0000-0000-0000-0006", Visibility.PRIVATE));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Visibility.PUBLIC, entity1.getActivitiesVisibilityDefault());
        assertEquals(Visibility.PRIVATE, entity6.getActivitiesVisibilityDefault());
        
        // Set it limited
        assertTrue(profileDao.updateDefaultVisibility("0000-0000-0000-0006", Visibility.LIMITED));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Visibility.PUBLIC, entity1.getActivitiesVisibilityDefault());
        assertEquals(Visibility.LIMITED, entity6.getActivitiesVisibilityDefault());
        
        // Set it public again
        assertTrue(profileDao.updateDefaultVisibility("0000-0000-0000-0006", Visibility.PUBLIC));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Visibility.PUBLIC, entity1.getActivitiesVisibilityDefault());
        assertEquals(Visibility.PUBLIC, entity6.getActivitiesVisibilityDefault());
    }
    
    @Test
    @Rollback(true)
    public void testUpdateSendEmailFrequencyDays() {
        ProfileEntity entity1 = profileDao.find("1000-0000-0000-0001");
        ProfileEntity entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Float.valueOf(0.0F), Float.valueOf(entity1.getSendEmailFrequencyDays()));
        assertEquals(Float.valueOf(0.0F), Float.valueOf(entity6.getSendEmailFrequencyDays()));
        
        assertTrue(profileDao.updateSendEmailFrequencyDays("0000-0000-0000-0006", Float.valueOf(SendEmailFrequency.DAILY.value())));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Float.valueOf(0.0F), Float.valueOf(entity1.getSendEmailFrequencyDays()));
        assertEquals(Float.valueOf(1.0F), Float.valueOf(entity6.getSendEmailFrequencyDays()));
        
        assertTrue(profileDao.updateSendEmailFrequencyDays("0000-0000-0000-0006", Float.valueOf(SendEmailFrequency.QUARTERLY.value())));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Float.valueOf(0.0F), Float.valueOf(entity1.getSendEmailFrequencyDays()));
        assertEquals(Float.valueOf(91.3105F), Float.valueOf(entity6.getSendEmailFrequencyDays()));
        
        assertTrue(profileDao.updateSendEmailFrequencyDays("0000-0000-0000-0006", Float.valueOf(SendEmailFrequency.IMMEDIATELY.value())));
        
        entity1 = profileDao.find("1000-0000-0000-0001");
        entity6 = profileDao.find("0000-0000-0000-0006");
        
        assertEquals(Float.valueOf(0.0F), Float.valueOf(entity1.getSendEmailFrequencyDays()));
        assertEquals(Float.valueOf(0.0F), Float.valueOf(entity6.getSendEmailFrequencyDays()));
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
    public void findEmailsUnverfiedDaysTest() {
        String orcid = "9999-9999-9999-999X";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(orcid);
        profile.setClaimed(true);
        profileDao.persist(profile);
        
        // Created today
        EmailEntity unverified_1 = new EmailEntity();
        unverified_1.setDateCreated(new Date());
        unverified_1.setLastModified(new Date());
        unverified_1.setProfile(profile);
        unverified_1.setVerified(false);
        unverified_1.setVisibility(Visibility.PUBLIC);
        unverified_1.setPrimary(false);
        unverified_1.setCurrent(true);
        unverified_1.setId("unverified_1@test.orcid.org");
        
        // Created a week ago
        EmailEntity unverified_2 = new EmailEntity();
        unverified_2.setDateCreated(LocalDateTime.now().minusDays(7).toDate());
        unverified_2.setLastModified(LocalDateTime.now().minusDays(7).toDate());
        unverified_2.setProfile(profile);
        unverified_2.setVerified(false);
        unverified_2.setVisibility(Visibility.PUBLIC);
        unverified_2.setPrimary(false);
        unverified_2.setCurrent(true);
        unverified_2.setId("unverified_2@test.orcid.org");
        
        // Created 15 days ago
        EmailEntity unverified_3 = new EmailEntity();
        unverified_3.setDateCreated(LocalDateTime.now().minusDays(15).toDate());
        unverified_3.setLastModified(LocalDateTime.now().minusDays(15).toDate());
        unverified_3.setProfile(profile);
        unverified_3.setVerified(false);
        unverified_3.setVisibility(Visibility.PUBLIC);
        unverified_3.setPrimary(false);
        unverified_3.setCurrent(true);
        unverified_3.setId("unverified_3@test.orcid.org");
        
        // Created 7 days ago and verified
        EmailEntity verified_1 = new EmailEntity();
        verified_1.setDateCreated(LocalDateTime.now().minusDays(7).toDate());
        verified_1.setLastModified(LocalDateTime.now().minusDays(7).toDate());
        verified_1.setProfile(profile);
        verified_1.setVerified(true);
        verified_1.setVisibility(Visibility.PUBLIC);
        verified_1.setPrimary(false);
        verified_1.setCurrent(true);
        verified_1.setId("verified_1@test.orcid.org");
        
        // Created 15 days ago and verified
        EmailEntity verified_2 = new EmailEntity();
        verified_2.setDateCreated(LocalDateTime.now().minusDays(15).toDate());
        verified_2.setLastModified(LocalDateTime.now().minusDays(15).toDate());
        verified_2.setProfile(profile);
        verified_2.setVerified(true);
        verified_2.setVisibility(Visibility.PUBLIC);
        verified_2.setPrimary(false);
        verified_2.setCurrent(true);
        verified_2.setId("verified_2@test.orcid.org");
        
        emailDao.removeAll();
        emailDao.persist(unverified_1);
        emailDao.persist(unverified_2);
        emailDao.persist(unverified_3);
        emailDao.persist(verified_1);
        emailDao.persist(verified_2);
        
        List<Pair<String, Date>> results = profileDao.findEmailsUnverfiedDays(7, 100, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT);
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
        
        results = profileDao.findEmailsUnverfiedDays(7, 100, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("unverified_3@test.orcid.org", results.get(0).getLeft());
        
        // Put an email event on 'unverified_3@test.orcid.org' and verify there is no result anymore
        emailEventDao.persist(new EmailEventEntity("unverified_3@test.orcid.org", EmailEventType.VERIFY_EMAIL_TOO_OLD));
        results = profileDao.findEmailsUnverfiedDays(7, 100, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT);
        assertNotNull(results);
        assertTrue(results.isEmpty());        
    }
}
