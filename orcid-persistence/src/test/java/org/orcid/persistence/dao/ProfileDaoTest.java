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
package org.orcid.persistence.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
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

import org.dbunit.dataset.DataSetException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.InstitutionEntity;
import org.orcid.persistence.jpa.entities.OrcidEntityIdComparator;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SubjectEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * orcid-persistence - Dec 6, 2011 - ProfileEntityDaoTest
 * 
 * @author Declan Newman (declan)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ProfileDaoTest extends DBUnitTest {

    @Resource
    private ProfileDao profileDao;

    @Resource
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Before
    public void beforeRunning() {
        assertNotNull(profileDao);
    }

    // vocative_name="Spike Milligan"/>

    // orcid="4444-4444-4444-4441"
    // creation_method="API"
    // completed_date="2011-07-02 15:31:00.00"
    // submission_date="2011-06-29 15:31:00.00"
    // confirmed="true"
    // full_name="Spike Milligan"
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindById() {
        ProfileEntity profile = profileDao.find("4444-4444-4444-4441");
        assertNotNull(profile);
        assertEquals("4444-4444-4444-4441", profile.getId());
        assertEquals("API", profile.getCreationMethod());
        assertNotNull(profile.getCompletedDate());
        assertNotNull(profile.getSubmissionDate());
        assertTrue(profile.getClaimed());
        assertEquals("Spike", profile.getGivenNames());
        assertEquals("Milligan", profile.getFamilyName());
        assertEquals("Spike Milligan", profile.getVocativeName());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testFindAll() {
        List<ProfileEntity> all = profileDao.getAll();
        assertNotNull(all);
        assertEquals(7, all.size());
        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(7), count);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsert() throws DataSetException {
        String newOrcid = "4444-1111-6666-4441";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(newOrcid);
        profile.setEmail("new@newman.com");

        profileDao.persist(profile);

        Date dateCreated = new Date();
        profile.setDateCreated(dateCreated);
        profileDao.merge(profile);

        profileDao.flush();
        profile = profileDao.find(profile.getId());
        assertEquals(dateCreated.getTime(), profile.getDateCreated().getTime());

        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(8), count);
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
        profile.setEmail("an@email.com");

        profileDao.persist(profile);

        Date dateCreated = new Date();
        profile.setDateCreated(dateCreated);
        profileDao.merge(profile);

        profileDao.flush();
        profile = profileDao.find(profile.getId());
        assertEquals(dateCreated.getTime(), profile.getDateCreated().getTime());

        Long count = profileDao.countAll();
        assertEquals(Long.valueOf(8), count);
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
        profile.setEmail("an@email.com");

        Set<InstitutionEntity> institutionEntities = new HashSet<InstitutionEntity>();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntities.add(institutionEntity);
        institutionEntity.setName("Semantico");

        // List<InstitutionDepartmentEntity> departmentEntities = new
        // ArrayList<InstitutionDepartmentEntity>();
        // InstitutionDepartmentEntity departmentEntity1 = new
        // InstitutionDepartmentEntity();
        // departmentEntities.add(departmentEntity1);
        // institutionEntity.setDepartments(departmentEntities);
        // departmentEntity1.setInstitution(institutionEntity);
        // departmentEntity1.setName("Development Team");
        //
        // InstitutionDepartmentEntity departmentEntity2 = new
        // InstitutionDepartmentEntity();
        // departmentEntities.add(departmentEntity2);
        // institutionEntity.setDepartments(departmentEntities);
        // departmentEntity2.setInstitution(institutionEntity);
        // departmentEntity2.setName("Declan's Crew");

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
        assertEquals(Long.valueOf(8), count);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertWithSubjectsAndKeywords() {
        String newOrcid = "4444-1111-6666-4444";
        ProfileEntity profile = new ProfileEntity();
        profile.setId(newOrcid);
        profile.setEmail("an@email.com");
        Set<SubjectEntity> subjects = new HashSet<SubjectEntity>(2);
        // profile.setSubjects(subjects);
        subjects.add(new SubjectEntity("Rhymin"));
        subjects.add(new SubjectEntity("Stealin"));
        SortedSet<ProfileKeywordEntity> keywords = new TreeSet<ProfileKeywordEntity>();
        profile.setKeywords(keywords);
        keywords.add(new ProfileKeywordEntity(profile, "Bilocation"));
        keywords.add(new ProfileKeywordEntity(profile, "Humour"));
        keywords.add(new ProfileKeywordEntity(profile, "Ceramics"));

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
        groupProfile.setEmail("an@email.com");
        groupProfile.setOrcidType(OrcidType.GROUP);

        SortedSet<ProfileEntity> clientProfiles = new TreeSet<ProfileEntity>(new OrcidEntityIdComparator<String>());
        String clientOrcid1 = "4444-4444-4444-4441";
        ProfileEntity clientProfile1 = new ProfileEntity();
        clientProfile1.setId(clientOrcid1);
        clientProfiles.add(clientProfile1);
        String clientOrcid2 = "4444-4444-4444-4442";
        ProfileEntity clientProfile2 = new ProfileEntity();
        clientProfile2.setId(clientOrcid2);
        clientProfiles.add(clientProfile2);
        groupProfile.setClientProfiles(clientProfiles);

        profileDao.persist(groupProfile);
        profileDao.flush();

        groupProfile = profileDao.find(groupOrcid);

        assertNotNull(groupProfile);
        assertEquals(groupOrcid, groupProfile.getId());
        assertEquals(OrcidType.GROUP, groupProfile.getOrcidType());
        assertNotNull(groupProfile.getClientProfiles());
        assertEquals(2, groupProfile.getClientProfiles().size());
        Map<String, ProfileEntity> map = ProfileEntity.mapById(groupProfile.getClientProfiles());
        assertTrue(map.containsKey(clientOrcid1));
        assertTrue(map.containsKey(clientOrcid2));
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInsertClient() {
        String clientOrcid = "4444-1111-6666-4444";
        ProfileEntity clientProfile = new ProfileEntity();
        clientProfile.setId(clientOrcid);
        clientProfile.setOrcidType(OrcidType.CLIENT);
        String groupOrcid = "4444-4444-4444-4441";
        clientProfile.setGroupOrcid(groupOrcid);

        profileDao.persist(clientProfile);
        profileDao.flush();

        clientProfile = profileDao.find(clientOrcid);
        assertNotNull(clientProfile);
        assertEquals(clientOrcid, clientProfile.getId());
        assertEquals(OrcidType.CLIENT, clientProfile.getOrcidType());

        ProfileEntity groupProfile = profileDao.find(groupOrcid);
        assertNotNull(groupProfile);
        assertNotNull(groupProfile.getClientProfiles());
        assertEquals(1, groupProfile.getClientProfiles().size());
        assertEquals(clientOrcid, groupProfile.getClientProfiles().iterator().next().getId());
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
        profile = profileDao.find("4444-4444-4444-4443");
        assertNull(profile);

        profileDao.flush();

        List<ProfileEntity> all = profileDao.getAll();
        assertEquals(5, all.size());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testOrcidExists() {
        assertTrue(profileDao.orcidExists("4444-4444-4444-4442"));
        assertFalse(profileDao.orcidExists("4445-4444-4444-4442"));
    }

    @Test
    @Rollback(true)
    public void testRetrieveSelectableSponsors() {
        List<ProfileEntity> results = profileDao.retrieveSelectableSponsors();
        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals("Billie Holiday", results.get(0).getVocativeName());
    }

    @Test
    @Rollback(true)
    public void testFindByEmail() {
        String email = "spike@milligan.com";
        ProfileEntity entity = profileDao.findByEmail(email);
        assertNotNull(entity);
        assertEquals(email, entity.getEmail());
    }

    @Test
    public void testFindOrcidsByName() {
        List<String> results = profileDao.findOrcidsByName("Milligan");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("4444-4444-4444-4441", results.get(0));
    }

    @Test
    public void testOrcidsFindByIndexingStatus() {
        List<String> results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.PENDING, 10);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("4444-4444-4444-4445", results.get(0));
        assertEquals("4444-4444-4444-4446", results.get(1));

        results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, Integer.MAX_VALUE);
        assertEquals(5, results.size());

        results = profileDao.findOrcidsByIndexingStatus(IndexingStatus.DONE, 3);
        assertEquals(3, results.size());
    }

    @Test
    public void testFindUnclaimedNotIndexedAfterWaitPeriod() {
        List<String> resultsList = profileDao.findUnclaimedNotIndexedAfterWaitPeriod(1, 10, Collections.<String> emptyList());
        assertNotNull(resultsList);
        assertTrue(resultsList.isEmpty());

        resultsList = profileDao.findUnclaimedNotIndexedAfterWaitPeriod(5, 10, Collections.<String> emptyList());
        assertNotNull(resultsList);
        assertEquals(1, resultsList.size());
        assertTrue(resultsList.contains("4444-4444-4444-4447"));
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testFindUnclaimedNeedingReminder() {
        List<String> results = profileDao.findUnclaimedNeedingReminder(1, 10, Collections.<String> emptyList());
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.contains("4444-4444-4444-4447"));

        // Now insert claimed reminder event, result should be excluded
        // thereafter.
        ProfileEventEntity eventEntity = new ProfileEventEntity();
        eventEntity.setOrcid("4444-4444-4444-4447");
        eventEntity.setType(ProfileEventType.CLAIM_REMINDER_SENT);
        profileEventDao.persist(eventEntity);

        results = profileDao.findUnclaimedNeedingReminder(1, 10, Collections.<String> emptyList());
        assertTrue(results.isEmpty());
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
        assertEquals(Long.valueOf(7), confirmedProfileCount);
        ProfileEntity profileEntity = profileDao.find(orcid);
        profileEntity.setCompletedDate(null);
        profileDao.persist(profileEntity);
        confirmedProfileCount = profileDao.getConfirmedProfileCount();
        assertEquals(Long.valueOf(6), confirmedProfileCount);
    }

}
