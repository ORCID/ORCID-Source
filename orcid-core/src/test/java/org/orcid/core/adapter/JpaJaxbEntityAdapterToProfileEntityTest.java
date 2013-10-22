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
package org.orcid.core.adapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.NewWorkType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileGrantEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * orcid-persistence - Dec 7, 2011 - JpaJaxbEntityAdapterTest
 * 
 * @author Declan Newman (declan)
 */
@RunWith(SpringJUnit4ClassRunner.class)
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
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml"), null);
    }

    @Before
    public void initDelegationProfiles() {
        ProfileEntity entityReceivingPermission = new ProfileEntity();
        entityReceivingPermission.setId("1111-1111-1111-1115");
        profileDao.merge(entityReceivingPermission);
        ProfileEntity entityGivingPermission = new ProfileEntity();
        entityGivingPermission.setId("2222-2222-2222-2229");
        profileDao.merge(entityGivingPermission);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml"), null);
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

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(true)
    public void testToProfileEntity() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_PROTECTED_FULL_XML);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidMessage.getOrcidProfile());
        assertNotNull(profileEntity);
        profileDao.persist(profileEntity);

        ProfileEntity retrievedProfileEntity = profileDao.find(orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(retrievedProfileEntity);
        assertEquals("Josiah", retrievedProfileEntity.getGivenNames());

        // Check all email visibility and values
        Set<EmailEntity> emails = profileEntity.getEmails();
        assertNotNull(emails);
        assertEquals(3, emails.size());
        Map<String, EmailEntity> emailMap = EmailEntity.mapById(emails);

        EmailEntity primaryEmail = emailMap.get("josiah_carberry@brown.edu");
        assertNotNull(primaryEmail);
        assertEquals(Visibility.LIMITED, primaryEmail.getVisibility());
        assertTrue(primaryEmail.getPrimary());
        assertTrue(primaryEmail.getCurrent());
        assertTrue(primaryEmail.getVerified());
        assertEquals("4444-4444-4444-4446", primaryEmail.getSource().getId());

        EmailEntity nonPrimaryEmail1 = emailMap.get("josiah_carberry_1@brown.edu");
        assertNotNull(nonPrimaryEmail1);
        assertEquals(Visibility.LIMITED, nonPrimaryEmail1.getVisibility());
        assertFalse(nonPrimaryEmail1.getPrimary());
        assertTrue(nonPrimaryEmail1.getCurrent());
        assertFalse(nonPrimaryEmail1.getVerified());
        assertEquals("1111-1111-1111-1115", nonPrimaryEmail1.getSource().getId());

        EmailEntity nonPrimaryEmail2 = emailMap.get("josiah_carberry_2@brown.edu");
        assertNotNull(nonPrimaryEmail2);
        assertEquals(Visibility.LIMITED, nonPrimaryEmail2.getVisibility());
        assertFalse(nonPrimaryEmail2.getPrimary());
        assertFalse(nonPrimaryEmail2.getCurrent());
        assertTrue(nonPrimaryEmail2.getVerified());
        assertEquals("1111-1111-1111-1115", nonPrimaryEmail1.getSource().getId());

        Set<ProfileWorkEntity> profileWorkEntities = profileEntity.getProfileWorks();
        assertEquals(3, profileWorkEntities.size());

        for (ProfileWorkEntity profileWorkEntity : profileWorkEntities) {
            WorkEntity workEntity = profileWorkEntity.getWork();
            String contributorsJson = workEntity.getContributorsJson();
            if ("Work title 1".equals(workEntity.getTitle())) {
                assertEquals(
                        "{\"contributor\":[{\"contributorOrcid\":{\"value\":\"4444-4444-4444-4446\"},\"creditName\":null,\"contributorEmail\":null,\"contributorAttributes\":{\"contributorSequence\":\"FIRST\",\"contributorRole\":\"AUTHOR\"}},{\"contributorOrcid\":null,\"creditName\":{\"content\":\"John W. Spaeth\",\"visibility\":\"PUBLIC\"},\"contributorEmail\":null,\"contributorAttributes\":null}]}",
                        contributorsJson);
            } else {
                assertNull(contributorsJson);
            }
        }

        assertEquals(2, profileEntity.getProfileGrants().size());
        for (ProfileGrantEntity profileGrantEntity : profileEntity.getProfileGrants()) {
            assertEquals(2, profileGrantEntity.getGrant().getContributors().size());
        }

        assertNotNull(profileEntity.getGivenPermissionTo());
        assertEquals(1, profileEntity.getGivenPermissionTo().size());
        GivenPermissionToEntity retrievedGivenPermissionToEntity = profileEntity.getGivenPermissionTo().iterator().next();
        assertEquals("1111-1111-1111-1115", retrievedGivenPermissionToEntity.getReceiver().getId());
        assertEquals(DateUtils.convertToDate("2012-11-10T13:18:51"), retrievedGivenPermissionToEntity.getApprovalDate());
        assertNotNull(profileEntity.getGivenPermissionBy());
        assertEquals(1, profileEntity.getGivenPermissionBy().size());
        GivenPermissionByEntity retrievedGivenPermissionByEntity = profileEntity.getGivenPermissionBy().iterator().next();
        assertEquals("2222-2222-2222-2229", retrievedGivenPermissionByEntity.getGiver().getId());
        assertEquals(DateUtils.convertToDate("2012-12-22T08:16:22"), retrievedGivenPermissionByEntity.getApprovalDate());

        adapter.toOrcidProfile(retrievedProfileEntity);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testInternalMessageToProfileEntity() throws JAXBException {
        OrcidMessage orcidMessage = getOrcidMessage(ORCID_INTERNAL_FULL_XML);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidMessage.getOrcidProfile());
        assertNotNull(profileEntity);
        profileDao.persist(profileEntity);

        ProfileEntity retrievedProfileEntity = profileDao.find(orcidMessage.getOrcidProfile().getOrcid().getValue());
        assertNotNull(retrievedProfileEntity);
        assertEquals("Josiah", retrievedProfileEntity.getGivenNames());
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
        currentOrcidWorks.get(0).setWorkType(WorkType.BIBLE);
        ProfileEntity profileEntity = adapter.toProfileEntity(orcidMessage.getOrcidProfile());
        List<ProfileWorkEntity> profileWorks = new ArrayList<ProfileWorkEntity>(profileEntity.getProfileWorks());
        assertTrue(profileWorks.size() == 1 && profileWorks.get(0).getWork().getWorkType().equals(WorkType.RELIGIOUS_TEXT));

    }

    private OrcidMessage getOrcidMessage(String orcidMessagePath) throws JAXBException {
        return (OrcidMessage) unmarshaller.unmarshal(JaxbOrcidMessageUtil.class.getResourceAsStream(orcidMessagePath));
    }

}
