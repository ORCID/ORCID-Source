package org.orcid.core.adapter.v2.latest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
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
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.dao.WorkDao;
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
    
    @Resource
    private WorkDao workDao;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml"));
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
        removeDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml"));
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

        // Check all email visibility and values
        Set<EmailEntity> emails = profileEntity.getEmails();
        assertNotNull(emails);
        assertEquals(2, emails.size());
        boolean found1 = false, found2 = false;
        
        for(EmailEntity email : emails) {
            if("josiah_carberry@brown.edu".equals(email.getEmail())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name(), email.getVisibility());
                assertTrue(email.getPrimary());
                assertTrue(email.getCurrent());
                assertTrue(email.getVerified());
                assertEquals("4444-4444-4444-4446", email.getElementSourceId());
                found1 = true;
            } else if("josiah_carberry_1@brown.edu".equals(email.getEmail())) {
                assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name(), email.getVisibility());
                assertFalse(email.getPrimary());
                assertTrue(email.getCurrent());
                assertFalse(email.getVerified());
                assertEquals("4444-4444-4444-4446", email.getElementSourceId());
                found2 = true;
            } else {
                fail("Invalid email: " + email.getEmail());
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
                
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
        assertEquals("abc123", retrievedProfileEntity.getEncryptedPassword());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setWorksTest() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_PROTECTED_FULL_XML);
        OrcidWorks works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks();
        ProfileEntity profile = profileDao.find("5555-5555-5555-5558");
        adapter.setWorks(profile, works);

        List<WorkEntity> workEntities = workDao.getWorksByOrcidId(profile.getId());
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
    }
    
    private OrcidMessage getOrcidMessage(String orcidMessagePath) throws JAXBException {
        return (OrcidMessage) unmarshaller.unmarshal(JaxbOrcidMessageUtil.class.getResourceAsStream(orcidMessagePath));
    }

}
