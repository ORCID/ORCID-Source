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
package org.orcid.core.manager.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

/**
 * @author: Declan Newman (declan) Date: 10/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ProfileEntityManagerImplTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");
    
    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Before
    public void init() {
        assertNotNull(profileEntityManager);
    }

    @Test
    public void testFindByOrcid() throws Exception {
        String harrysOrcid = "4444-4444-4444-4444";
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(harrysOrcid);
        assertNotNull(profileEntity);
        if(profileEntity.getRecordNameEntity() != null) {
            assertEquals("Harry", profileEntity.getRecordNameEntity().getGivenNames());
            assertEquals("Secombe", profileEntity.getRecordNameEntity().getFamilyName());
        } 
        assertEquals(harrysOrcid, profileEntity.getId());
    }

    @Test    
    public void testDeprecateProfile() throws Exception {
        ProfileEntity profileEntityToDeprecate = profileEntityCacheManager.retrieve("4444-4444-4444-4441");
        ProfileEntity primaryProfileEntity = profileEntityCacheManager.retrieve("4444-4444-4444-4442");
        assertNull(profileEntityToDeprecate.getPrimaryRecord());
        boolean result = profileEntityManager.deprecateProfile(profileEntityToDeprecate, primaryProfileEntity);
        assertTrue(result);
        profileEntityToDeprecate = profileEntityCacheManager.retrieve("4444-4444-4444-4441");
        assertNotNull(profileEntityToDeprecate.getPrimaryRecord());
        assertEquals("4444-4444-4444-4442", profileEntityToDeprecate.getPrimaryRecord().getId());
    }
    
    @Test    
    public void testReviewProfile() throws Exception {
    	boolean result = profileEntityManager.reviewProfile("4444-4444-4444-4441");
        assertTrue(result);
    	
    	result = profileEntityManager.unreviewProfile("4444-4444-4444-4442");
    	assertTrue(result);
    }
    
    @Test  
    @Transactional
    public void testClaimChangingVisibility() {
        Claim claim = new Claim();
        claim.setActivitiesVisibilityDefault(org.orcid.pojo.ajaxForm.Visibility.valueOf(Visibility.PRIVATE));
        claim.setPassword(Text.valueOf("passwordTest1"));
        claim.setPasswordConfirm(Text.valueOf("passwordTest1"));
        Checkbox checked = new Checkbox();
        checked.setValue(true);
        claim.setSendChangeNotifications(checked);
        claim.setSendOrcidNews(checked);
        claim.setTermsOfUse(checked);
        
        assertTrue(profileEntityManager.claimProfileAndUpdatePreferences("0000-0000-0000-0001", "public_0000-0000-0000-0001@test.orcid.org", Locale.EN, claim));
        ProfileEntity profile = profileEntityManager.findByOrcid("0000-0000-0000-0001");
        assertNotNull(profile);
        assertNotNull(profile.getBiographyEntity());
        assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE, profile.getBiographyEntity().getVisibility());
        assertNotNull(profile.getAddresses());
        assertEquals(3, profile.getAddresses().size());
        for(AddressEntity a : profile.getAddresses()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE, a.getVisibility());
        }
        
        assertNotNull(profile.getExternalIdentifiers());
        assertEquals(3, profile.getExternalIdentifiers().size());
        for(ExternalIdentifierEntity e : profile.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE, e.getVisibility());
        }
        assertNotNull(profile.getKeywords());
        assertEquals(3, profile.getKeywords().size());
        for(ProfileKeywordEntity k : profile.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE, k.getVisibility());
        }
        
        assertNotNull(profile.getOtherNames());
        assertEquals(3, profile.getOtherNames().size());
        for(OtherNameEntity o : profile.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE, o.getVisibility());
        }
        
        assertNotNull(profile.getResearcherUrls());
        assertEquals(3, profile.getResearcherUrls().size());
        for(ResearcherUrlEntity r : profile.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE, r.getVisibility());
        }
        
    }
}
