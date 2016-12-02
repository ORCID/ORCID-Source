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
package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
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
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = { "classpath:orcid-persistence-context.xml" })
public class ProfileDaoTest extends DBUnitTest {

    @Resource
    private ProfileDao profileDao;

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
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PUBLIC, profile.getRecordNameEntity().getVisibility());
    }        

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testFindAll() {
        List<ProfileEntity> all = profileDao.getAll();
        assertNotNull(all);
        assertEquals(20, all.size());
        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(20), count);
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
        assertEquals(Long.valueOf(21), count);
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
        assertEquals(Long.valueOf(21), count);
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
        assertEquals(Long.valueOf(21), count);
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
        assertEquals(18, all.size());
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
        assertEquals(18, results.size());

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
        assertEquals(Long.valueOf(20), confirmedProfileCount);
        ProfileEntity profileEntity = profileDao.find(orcid);
        profileEntity.setCompletedDate(null);
        profileDao.persist(profileEntity);
        confirmedProfileCount = profileDao.getConfirmedProfileCount();
        assertEquals(Long.valueOf(19), confirmedProfileCount);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testDeprecateProfile() {
        ProfileEntity profileToDeprecate = profileDao.find("4444-4444-4444-4441");
        assertNull(profileToDeprecate.getPrimaryRecord());
        boolean result = profileDao.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442");
        assertTrue(result);
        profileToDeprecate = profileDao.find("4444-4444-4444-4441");
        profileDao.refresh(profileToDeprecate);
        assertNotNull(profileToDeprecate.getPrimaryRecord());
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
}
