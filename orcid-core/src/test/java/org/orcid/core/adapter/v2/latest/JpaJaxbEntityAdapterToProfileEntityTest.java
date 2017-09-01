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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.JaxbOrcidMessageUtil;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
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
    private Jaxb2JpaAdapter jaxb2JpaAdapter;
    
    @Mock
    private SourceManager mockSourceManager;

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
    
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", mockSourceManager);
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceProfile(new ProfileEntity("5555-5555-5555-5558"));
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);
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
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, primaryEmail.getVisibility());
        assertTrue(primaryEmail.getPrimary());
        assertTrue(primaryEmail.getCurrent());
        assertTrue(primaryEmail.getVerified());
        assertEquals("4444-4444-4444-4446", primaryEmail.getElementSourceId());

        EmailEntity nonPrimaryEmail1 = emailMap.get("josiah_carberry_1@brown.edu");
        assertNotNull(nonPrimaryEmail1);
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.LIMITED, nonPrimaryEmail1.getVisibility());
        assertFalse(nonPrimaryEmail1.getPrimary());
        assertTrue(nonPrimaryEmail1.getCurrent());
        assertFalse(nonPrimaryEmail1.getVerified());
        assertEquals("4444-4444-4444-4446", nonPrimaryEmail1.getElementSourceId());

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
    public void setWorksTest() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_INTERNAL_FULL_XML);
        OrcidWorks works = orcidMessage.getOrcidProfile().getOrcidActivities().getOrcidWorks();
        ProfileEntity profile = profileDao.find("5555-5555-5555-5558");
        adapter.setWorks(profile, works);
    }
    
    private OrcidMessage getOrcidMessage(String orcidMessagePath) throws JAXBException {
        return (OrcidMessage) unmarshaller.unmarshal(JaxbOrcidMessageUtil.class.getResourceAsStream(orcidMessagePath));
    }

}
