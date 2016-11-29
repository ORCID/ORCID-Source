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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: Declan Newman (declan) Date: 10/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ProfileEntityManagerImplTest extends DBUnitTest {
    private static final String CLIENT_ID_1 = "APP-5555555555555555";   
    private static final String USER_ORCID = "0000-0000-0000-0001";    
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name = "profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/BiographyEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/BiographyEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
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
        assertNull(profileEntityToDeprecate.getPrimaryRecord());
        boolean result = profileEntityManager.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442");
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
        assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE, profile.getBiographyEntity().getVisibility());
        assertNotNull(profile.getAddresses());
        assertEquals(3, profile.getAddresses().size());
        for(AddressEntity a : profile.getAddresses()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE, a.getVisibility());
        }
        
        assertNotNull(profile.getExternalIdentifiers());
        assertEquals(3, profile.getExternalIdentifiers().size());
        for(ExternalIdentifierEntity e : profile.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE, e.getVisibility());
        }
        assertNotNull(profile.getKeywords());
        assertEquals(3, profile.getKeywords().size());
        for(ProfileKeywordEntity k : profile.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE, k.getVisibility());
        }
        
        assertNotNull(profile.getOtherNames());
        assertEquals(3, profile.getOtherNames().size());
        for(OtherNameEntity o : profile.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE, o.getVisibility());
        }
        
        assertNotNull(profile.getResearcherUrls());
        assertEquals(3, profile.getResearcherUrls().size());
        for(ResearcherUrlEntity r : profile.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.common_rc3.Visibility.PRIVATE, r.getVisibility());
        }        
    }
    
    @Test
    public void testGetApplications() {
        Date expiration = new Date(System.currentTimeMillis() + 10000);
        OrcidOauth2TokenDetail token1 = createToken(CLIENT_ID_1, "token-1", USER_ORCID, expiration, "/read-limited", false);
        OrcidOauth2TokenDetail token2 = createToken(CLIENT_ID_1, "token-2", USER_ORCID, expiration, "/orcid-profile/read-limited", false);
        OrcidOauth2TokenDetail token3 = createToken(CLIENT_ID_1, "token-3", USER_ORCID, expiration, "/activities/update", false);
        OrcidOauth2TokenDetail token4 = createToken(CLIENT_ID_1, "token-4", USER_ORCID, expiration, "/activities/read-limited", false);
        OrcidOauth2TokenDetail token5 = createToken(CLIENT_ID_1, "token-5", USER_ORCID, expiration, "/orcid-works/read-limited", false);
        
        List<ApplicationSummary> applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertEquals(5, applications.size());
        
        boolean found1 = false, found2=false, found3 = false, found4 = false, found5 = false;
        
        for(ApplicationSummary summary : applications) {
            assertNotNull(summary.getTokenId());
            if(summary.getTokenId().equals(String.valueOf(token1.getId()))) {
                found1 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token2.getId()))) {
                found2 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token3.getId()))) {
                found3 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token4.getId()))) {
                found4 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token5.getId()))) {
                found5 = true;
            }              
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        
        //Assert we can delete them
        profileEntityManager.disableApplication(token1.getId(), USER_ORCID);
        profileEntityManager.disableApplication(token5.getId(), USER_ORCID);
        
        found1 = found2 = found3 = found4 = found5 = false;
        
        applications = profileEntityManager.getApplications(USER_ORCID);
        assertEquals(3, applications.size());
        
        for(ApplicationSummary summary : applications) {
            assertNotNull(summary.getTokenId());
            if(summary.getTokenId().equals(String.valueOf(token1.getId()))) {
                found1 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token2.getId()))) {
                found2 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token3.getId()))) {
                found3 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token4.getId()))) {
                found4 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token5.getId()))) {
                found5 = true;
            }     
        }

        assertFalse(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertFalse(found5);
        
        //Revoke the others
        profileEntityManager.disableApplication(token2.getId(), USER_ORCID);
        profileEntityManager.disableApplication(token3.getId(), USER_ORCID);        
        profileEntityManager.disableApplication(token4.getId(), USER_ORCID);                
        
        applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertTrue(applications.isEmpty());
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testDontGetDuplicatedApplications() {
        Date expiration = new Date(System.currentTimeMillis() + 10000);
        OrcidOauth2TokenDetail token1 = createToken(CLIENT_ID_1, "token-1", USER_ORCID, expiration, "/read-limited", false); // Displayed
        OrcidOauth2TokenDetail token2 = createToken(CLIENT_ID_1, "token-2", USER_ORCID, expiration, "/orcid-profile/read-limited", false); // Displayed
        OrcidOauth2TokenDetail token3 = createToken(CLIENT_ID_1, "token-3", USER_ORCID, expiration, "/activities/update", false); // Displayed
        OrcidOauth2TokenDetail token4 = createToken(CLIENT_ID_1, "token-4", USER_ORCID, expiration, "/read-limited", false);
        OrcidOauth2TokenDetail token5 = createToken(CLIENT_ID_1, "token-5", USER_ORCID, expiration, "/orcid-profile/read-limited", false);
        OrcidOauth2TokenDetail token6 = createToken(CLIENT_ID_1, "token-6", USER_ORCID, expiration, "/activities/update", false);
        OrcidOauth2TokenDetail token7 = createToken(CLIENT_ID_1, "token-7", USER_ORCID, expiration, "/read-limited", false);
        OrcidOauth2TokenDetail token8 = createToken(CLIENT_ID_1, "token-8", USER_ORCID, expiration, "/orcid-profile/read-limited", false);
        OrcidOauth2TokenDetail token9 = createToken(CLIENT_ID_1, "token-9", USER_ORCID, expiration, "/activities/update", false);
        OrcidOauth2TokenDetail token10 = createToken(CLIENT_ID_1, "token-10", USER_ORCID, expiration, "/person/read-limited", false); // Displayed
        OrcidOauth2TokenDetail token11 = createToken(CLIENT_ID_1, "token-11", USER_ORCID, expiration, "/person/read-limited", false);
        
        List<ApplicationSummary> applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertEquals(4, applications.size());
        
        boolean found1 = false, found2 = false, found3 = false, found10 = false;
        
        for(ApplicationSummary summary : applications) {
            assertNotNull(summary.getTokenId());
            if(summary.getTokenId().equals(String.valueOf(token1.getId()))) {
                found1 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token2.getId()))) {
                found2 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token3.getId()))) {
                found3 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token10.getId()))) {
                found10 = true;
            } 
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found10);
        
        //Revoke them to check revoking one revokes all the ones with the same scopes
        profileEntityManager.disableApplication(token1.getId(), USER_ORCID);
        profileEntityManager.disableApplication(token2.getId(), USER_ORCID);
        
        applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertEquals(2, applications.size());
        
        found1 = found2 = found3 = found10 = false;
        
        for(ApplicationSummary summary : applications) {
            assertNotNull(summary.getTokenId());
            if(summary.getTokenId().equals(String.valueOf(token1.getId()))) {
                found1 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token2.getId()))) {
                found2 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token3.getId()))) {
                found3 = true;
            } else if(summary.getTokenId().equals(String.valueOf(token10.getId()))) {
                found10 = true;
            } 
        }
        
        assertFalse(found1);
        assertFalse(found2);
        assertTrue(found3);
        assertTrue(found10);
        
        //Revoke them all
        profileEntityManager.disableApplication(token3.getId(), USER_ORCID);
        profileEntityManager.disableApplication(token10.getId(), USER_ORCID);
        
        applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertTrue(applications.isEmpty());
    }
    
    private OrcidOauth2TokenDetail createToken(String clientId, String tokenValue, String userOrcid, Date expirationDate, String scopes, boolean disabled) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(clientId);
        token.setDateCreated(new Date());
        token.setLastModified(new Date());
        token.setProfile(new ProfileEntity(userOrcid));
        token.setScope(scopes);
        token.setTokenDisabled(disabled);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setTokenValue(tokenValue);
        orcidOauth2TokenDetailService.saveOrUpdate(token);
        return token;
    }
    
}
