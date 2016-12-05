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
package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.JaxbOrcidMessageUtil;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * orcid-persistence - Dec 7, 2011 - JpaJaxbEntityAdapterTest
 * 
 * @author Declan Newman (declan)
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class JpaJaxbEntityAdapterToProfileEntityTest extends DBUnitTest {

    public static final String ORCID_PROTECTED_FULL_XML = "/orcid-protected-full-message-latest.xml";
    public static final String ORCID_INTERNAL_FULL_XML = "/orcid-internal-full-message-latest.xml";

    private Unmarshaller unmarshaller;

    @Autowired
    private GenericDao<ProfileEntity, String> profileDao;

    @Autowired
    private JpaJaxbEntityAdapter adapter;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Before
    public void initDelegationProfiles() {
        ProfileEntity entityReceivingPermission = new ProfileEntity();
        entityReceivingPermission.setId("1111-1111-1111-1115");
        profileDao.merge(entityReceivingPermission);
        ProfileEntity entityGivingPermission = new ProfileEntity();
        entityGivingPermission.setId("2222-2222-2222-2229");
        profileDao.merge(entityGivingPermission);
        ProfileEntity source1 = new ProfileEntity();
        source1.setId("2111-1111-1111-1114");
        profileDao.merge(source1);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Before
    public void init() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
        assertNotNull(profileDao);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(true)
    public void testMarshalling() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_INTERNAL_FULL_XML);
        System.out.println(orcidMessage);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(true)
    public void testToProfileEntity() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_PROTECTED_FULL_XML);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidMessage.getOrcidProfile());
        assertNotNull(profileEntity);
        profileDao.persist(profileEntity);
        
        ProfileEntity retrievedProfileEntity = profileDao.find(orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        assertNotNull(retrievedProfileEntity);
        assertEquals("Josiah", retrievedProfileEntity.getRecordNameEntity().getGivenNames());

        // Check all email visibility and values
        Set<EmailEntity> emails = profileEntity.getEmails();
        assertNotNull(emails);
        assertEquals(2, emails.size());
        Map<String, EmailEntity> emailMap = EmailEntity.mapById(emails);

        EmailEntity primaryEmail = emailMap.get("josiah_carberry@brown.edu");
        assertNotNull(primaryEmail);
        assertEquals(Visibility.LIMITED, primaryEmail.getVisibility());
        assertTrue(primaryEmail.getPrimary());
        assertTrue(primaryEmail.getCurrent());
        assertTrue(primaryEmail.getVerified());
        assertEquals("4444-4444-4444-4446", primaryEmail.getElementSourceId());

        EmailEntity nonPrimaryEmail1 = emailMap.get("josiah_carberry_1@brown.edu");
        assertNotNull(nonPrimaryEmail1);
        assertEquals(Visibility.LIMITED, nonPrimaryEmail1.getVisibility());
        assertFalse(nonPrimaryEmail1.getPrimary());
        assertTrue(nonPrimaryEmail1.getCurrent());
        assertFalse(nonPrimaryEmail1.getVerified());
        assertEquals("4444-4444-4444-4446", nonPrimaryEmail1.getElementSourceId());

        Set<WorkEntity> workEntities = profileEntity.getWorks();
        assertEquals(3, workEntities.size());

        for (WorkEntity workEntity : workEntities) {
            String contributorsJson = workEntity.getContributorsJson();
            if ("Work title 1".equals(workEntity.getTitle())) {
                assertEquals("Journal Title # 1", workEntity.getJournalTitle());
                
                assertNotNull(contributorsJson);
                assertTrue(contributorsJson.startsWith("{\"contributor\":[{\""));
                Map<String, Object> mappedJson = JsonUtils.<HashMap> readObjectFromJsonString(contributorsJson, HashMap.class);
                List<Map<String, Object>> contributorsList = (List<Map<String, Object>>) mappedJson.get("contributor");
                

                Map<String, Object> contributor0 = contributorsList.get(0);
                assertEquals(4, contributor0.keySet().size());
                Map<String, Object> contributorOrcid0 = (Map<String, Object>) contributor0.get("contributorOrcid");
                assertEquals("http://orcid.org/4444-4444-4444-4446", contributorOrcid0.get("uri"));
                assertEquals("4444-4444-4444-4446", contributorOrcid0.get("path"));
                assertEquals("orcid.org", contributorOrcid0.get("host"));
                assertTrue(contributorOrcid0.containsKey("value"));
                assertNull(contributorOrcid0.get("value"));
                assertTrue(contributorOrcid0.containsKey("valueAsString"));
                assertNull(contributorOrcid0.get("valueAsString"));
                assertTrue(contributor0.containsKey("creditName"));
                assertNull(contributor0.get("creditName"));
                assertTrue(contributor0.containsKey("contributorEmail"));
                assertNull(contributor0.get("contributorEmail"));
                Map<String, Object> contributorAttributes0 = (Map<String, Object>) contributor0.get("contributorAttributes");
                assertNotNull(contributorAttributes0);
                assertEquals("FIRST", contributorAttributes0.get("contributorSequence"));
                assertEquals("AUTHOR", contributorAttributes0.get("contributorRole"));

                Map<String, Object> contributor1 = contributorsList.get(1);
                assertEquals(4, contributor1.keySet().size());
                assertTrue(contributor1.containsKey("contributorOrcid"));
                assertNull(contributor1.get("contributorOrcid"));
                assertEquals("John W. Spaeth", ((Map<String, Object>) contributor1.get("creditName")).get("content"));
                assertEquals("PUBLIC", ((Map<String, Object>) contributor1.get("creditName")).get("visibility"));
                assertTrue(contributor1.containsKey("contributorEmail"));
                assertNull(contributor1.get("contributorEmail"));
                assertTrue(contributor1.containsKey("contributorAttributes"));
                assertNull(contributor1.get("contributorAttributes"));

            } else if (workEntity.getTitle().equals("Work title 2")) {
                assertNull(contributorsJson);
                assertEquals("Journal Title # 2", workEntity.getJournalTitle());
            } else {
                assertNull(contributorsJson);
                assertNull(workEntity.getJournalTitle());
            }
        }

        assertEquals(2, profileEntity.getProfileFunding().size());
        for (ProfileFundingEntity profileGrantEntity : profileEntity.getProfileFunding()) {
            assertNotNull(profileGrantEntity.getContributorsJson());
        }

        assertNull(profileEntity.getGivenPermissionBy());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInternalMessageToProfileEntity() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_INTERNAL_FULL_XML);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidMessage.getOrcidProfile());
        assertNotNull(profileEntity);
        profileDao.persist(profileEntity);

        ProfileEntity retrievedProfileEntity = profileDao.find(orcidMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        assertNotNull(retrievedProfileEntity);
        assertEquals("Josiah", retrievedProfileEntity.getRecordNameEntity().getGivenNames());
        assertEquals("abc123", retrievedProfileEntity.getEncryptedPassword());
        assertEquals(1, retrievedProfileEntity.getSecurityQuestion().getId().intValue());
        assertEquals("dMDyJ1Z7Qn6xWClFzA63fQ==", retrievedProfileEntity.getEncryptedSecurityAnswer());
        assertEquals("ghi789", retrievedProfileEntity.getEncryptedVerificationCode());
        assertTrue(retrievedProfileEntity.getSendChangeNotifications());
        assertFalse(retrievedProfileEntity.getSendOrcidNews());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(true)
    public void testReligiousTextConvertedFromBible() throws Exception {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_INTERNAL_FULL_XML);
        List<OrcidWork> currentOrcidWorks = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertTrue(currentOrcidWorks.size() == 1);
        currentOrcidWorks.get(0).setWorkType(WorkType.DATA_SET);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidMessage.getOrcidProfile());
        List<WorkEntity> works = new ArrayList<WorkEntity>(profileEntity.getWorks());
        assertEquals(1, works.size());
        assertTrue(works.get(0).getWorkType().equals(WorkType.DATA_SET));
    }

    private OrcidMessage getOrcidMessage(String orcidMessagePath) throws JAXBException {
        return (OrcidMessage) unmarshaller.unmarshal(JaxbOrcidMessageUtil.class.getResourceAsStream(orcidMessagePath));
    }

}
