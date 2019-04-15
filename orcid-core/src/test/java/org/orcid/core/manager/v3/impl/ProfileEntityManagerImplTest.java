package org.orcid.core.manager.v3.impl;

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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.BiographyManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: Declan Newman (declan) Date: 10/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ProfileEntityManagerImplTest extends DBUnitTest {
    private static final String CLIENT_ID_1 = "APP-5555555555555555";   
    private static final String CLIENT_ID_2 = "APP-5555555555555556";
    private static final String USER_ORCID = "0000-0000-0000-0001";    
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name = "profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;
    
    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManager;
    
    @Resource(name = "biographyManagerV3")
    private BiographyManager biographyManager;
    
    @Resource
    private UserConnectionDao userConnectionDao;
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @Mock
    private EmailFrequencyManager emailFrequencyManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml", "/data/ClientDetailsEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }    
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(profileEntityManager, "emailFrequencyManager", emailFrequencyManager);
        Mockito.when(emailFrequencyManager.createOnClaim(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(true);
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
        ProfileHistoryEventManager profileHistoryEventManager = Mockito.mock(ProfileHistoryEventManagerImpl.class);
        ReflectionTestUtils.setField(profileEntityManager, "profileHistoryEventManager", profileHistoryEventManager);
        Mockito.doNothing().when(profileHistoryEventManager).recordEvent(Mockito.any(ProfileHistoryEventType.class), Mockito.anyString(), Mockito.anyString());
        
        UserconnectionPK pk = new UserconnectionPK();
        pk.setProviderid("providerId");
        pk.setProvideruserid("provideruserid");
        pk.setUserid("4444-4444-4444-4441");
        
        UserconnectionEntity userConnection = new UserconnectionEntity();
        userConnection.setAccesstoken("blah");
        userConnection.setConnectionSatus(UserConnectionStatus.STARTED);
        userConnection.setDisplayname("blah");
        userConnection.setDateCreated(new Date());
        userConnection.setLastModified(new Date());
        userConnection.setEmail("blah@blah.com");
        userConnection.setOrcid("4444-4444-4444-4441");
        userConnection.setId(pk);
        userConnection.setRank(1);
        userConnectionDao.persist(userConnection);
        
        ProfileEntity profileEntityToDeprecate = profileEntityCacheManager.retrieve("4444-4444-4444-4441");     
        assertNull(profileEntityToDeprecate.getPrimaryRecord());
        boolean result = profileEntityManager.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442", ProfileEntity.USER_DRIVEN_DEPRECATION, null);
        assertTrue(result);
        profileEntityToDeprecate = profileEntityCacheManager.retrieve("4444-4444-4444-4441");
        assertNotNull(profileEntityToDeprecate.getPrimaryRecord());
        assertNotNull(profileEntityToDeprecate.getDeprecatedMethod());
        assertEquals(ProfileEntity.USER_DRIVEN_DEPRECATION, profileEntityToDeprecate.getDeprecatedMethod());
        assertEquals("4444-4444-4444-4442", profileEntityToDeprecate.getPrimaryRecord().getId());
        assertEquals(0, userConnectionDao.findByOrcid("4444-4444-4444-4441").size());
        assertEquals(0, profileEntityToDeprecate.getGivenPermissionBy().size());
        assertEquals(0, profileEntityToDeprecate.getGivenPermissionTo().size());
        assertFalse(profileEntityToDeprecate.getUsing2FA());
        assertNull(profileEntityToDeprecate.getSecretFor2FA());
        assertEquals(0, notificationManager.findByOrcid("4444-4444-4444-4441", true, 0, 1000).size());
    }
    
    @Test    
    public void testDeactivateRecord() throws Exception {
        ProfileHistoryEventManager profileHistoryEventManager = Mockito.mock(ProfileHistoryEventManagerImpl.class);
        ReflectionTestUtils.setField(profileEntityManager, "profileHistoryEventManager", profileHistoryEventManager);
        Mockito.doNothing().when(profileHistoryEventManager).recordEvent(Mockito.any(ProfileHistoryEventType.class), Mockito.anyString(), Mockito.anyString());
        
        UserconnectionPK pk = new UserconnectionPK();
        pk.setProviderid("providerId");
        pk.setProvideruserid("provideruserid");
        pk.setUserid("4444-4444-4444-4441");
        
        UserconnectionEntity userConnection = new UserconnectionEntity();
        userConnection.setAccesstoken("blah");
        userConnection.setConnectionSatus(UserConnectionStatus.STARTED);
        userConnection.setDisplayname("blah");
        userConnection.setDateCreated(new Date());
        userConnection.setLastModified(new Date());
        userConnection.setEmail("blah@blah.com");
        userConnection.setOrcid("4444-4444-4444-4441");
        userConnection.setId(pk);
        userConnection.setRank(1);
        userConnectionDao.persist(userConnection);
        
        boolean result = profileEntityManager.deactivateRecord("4444-4444-4444-4441");
        assertTrue(result);

        ProfileEntity deactivated = profileEntityCacheManager.retrieve("4444-4444-4444-4441");     
        assertEquals(0, userConnectionDao.findByOrcid("4444-4444-4444-4441").size());
        assertEquals(0, deactivated.getGivenPermissionBy().size());
        assertEquals(0, deactivated.getGivenPermissionTo().size());
        assertFalse(deactivated.getUsing2FA());
        assertNull(deactivated.getSecretFor2FA());
        assertEquals(0, notificationManager.findByOrcid("4444-4444-4444-4441", true, 0, 1000).size());
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
        claim.setActivitiesVisibilityDefault(org.orcid.pojo.ajaxForm.Visibility.valueOf(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE));
        claim.setPassword(Text.valueOf("passwordTest1"));
        claim.setPasswordConfirm(Text.valueOf("passwordTest1"));
        Checkbox checked = new Checkbox();
        checked.setValue(true);
        claim.setSendChangeNotifications(checked);
        claim.setSendOrcidNews(checked);
        claim.setTermsOfUse(checked);
        
        assertTrue(profileEntityManager.claimProfileAndUpdatePreferences("0000-0000-0000-0001", "public_0000-0000-0000-0001@test.orcid.org", AvailableLocales.EN, claim));
        ProfileEntity profile = profileEntityManager.findByOrcid("0000-0000-0000-0001");
        assertNotNull(profile);
        assertNotNull(profile.getBiographyEntity());
        assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), profile.getBiographyEntity().getVisibility());
        assertNotNull(profile.getAddresses());
        assertEquals(3, profile.getAddresses().size());
        for(AddressEntity a : profile.getAddresses()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), a.getVisibility());
        }
        
        assertNotNull(profile.getExternalIdentifiers());
        assertEquals(3, profile.getExternalIdentifiers().size());
        for(ExternalIdentifierEntity e : profile.getExternalIdentifiers()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), e.getVisibility());
        }
        assertNotNull(profile.getKeywords());
        assertEquals(3, profile.getKeywords().size());
        for(ProfileKeywordEntity k : profile.getKeywords()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), k.getVisibility());
        }
        
        assertNotNull(profile.getOtherNames());
        assertEquals(3, profile.getOtherNames().size());
        for(OtherNameEntity o : profile.getOtherNames()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), o.getVisibility());
        }
        
        assertNotNull(profile.getResearcherUrls());
        assertEquals(3, profile.getResearcherUrls().size());
        for(ResearcherUrlEntity r : profile.getResearcherUrls()) {
            assertEquals(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name(), r.getVisibility());
        }        
    }
    
    @Test
    public void testUpdatePassword() {
        EncryptionManager encryptionManager = (EncryptionManager) ReflectionTestUtils.getField(profileEntityManager, "encryptionManager");
        ProfileDao profileDao = (ProfileDao) ReflectionTestUtils.getField(profileEntityManager, "profileDao");
        ProfileHistoryEventManager profileHistoryEventManager = (ProfileHistoryEventManager) ReflectionTestUtils.getField(profileEntityManager, "profileHistoryEventManager");
        
        EncryptionManager mockEncryptionManager = Mockito.mock(EncryptionManager.class);
        ProfileDao mockProfileDao = Mockito.mock(ProfileDao.class);
        ProfileHistoryEventManager mockProfileHistoryEventManager = Mockito.mock(ProfileHistoryEventManagerImpl.class);
        
        ReflectionTestUtils.setField(profileEntityManager, "encryptionManager", mockEncryptionManager);
        ReflectionTestUtils.setField(profileEntityManager, "profileDao", mockProfileDao);
        ReflectionTestUtils.setField(profileEntityManager, "profileHistoryEventManager", mockProfileHistoryEventManager);
        
        Mockito.when(mockEncryptionManager.hashForInternalUse(Mockito.eq("password"))).thenReturn("encryptedPassword");
        Mockito.doNothing().when(mockProfileDao).updateEncryptedPassword(Mockito.eq("orcid"), Mockito.eq("encryptedPassword"));
        Mockito.doNothing().when(mockProfileHistoryEventManager).recordEvent(Mockito.eq(ProfileHistoryEventType.RESET_PASSWORD), Mockito.eq("orcid"));
        
        profileEntityManager.updatePassword("orcid", "password");
        
        Mockito.verify(mockProfileHistoryEventManager, Mockito.times(1)).recordEvent(Mockito.eq(ProfileHistoryEventType.RESET_PASSWORD), Mockito.eq("orcid"));
        
        ReflectionTestUtils.setField(profileEntityManager, "encryptionManager", encryptionManager);
        ReflectionTestUtils.setField(profileEntityManager, "profileDao", profileDao);
        ReflectionTestUtils.setField(profileEntityManager, "profileHistoryEventManager", profileHistoryEventManager);
    }
    
    @Test
    public void testGetApplications() {
        Date expiration = new Date(System.currentTimeMillis() + 10000);
        createToken(CLIENT_ID_1, "token-1", USER_ORCID, expiration, "/read-limited", false);
        createToken(CLIENT_ID_1, "token-2", USER_ORCID, expiration, "/orcid-profile/read-limited", false);
        createToken(CLIENT_ID_1, "token-3", USER_ORCID, expiration, "/activities/update", false);
        createToken(CLIENT_ID_1, "token-4", USER_ORCID, expiration, "/activities/read-limited", false);
        createToken(CLIENT_ID_1, "token-5", USER_ORCID, expiration, "/orcid-works/read-limited", false);
        
        createToken(CLIENT_ID_2, "token-6", USER_ORCID, expiration, "/read-limited", false);
        createToken(CLIENT_ID_2, "token-7", USER_ORCID, expiration, "/orcid-profile/read-limited", false);
        createToken(CLIENT_ID_2, "token-8", USER_ORCID, expiration, "/activities/update", false);
        
        List<ApplicationSummary> applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertEquals(2, applications.size());
        assertEquals(4, applications.get(0).getScopePaths().keySet().size());
        assertEquals(2, applications.get(1).getScopePaths().keySet().size());
        
        // test ordering based on name
        assertEquals(CLIENT_ID_1, applications.get(0).getOrcidPath());
        assertEquals(CLIENT_ID_2, applications.get(1).getOrcidPath());
        
        //Assert we can delete them
        profileEntityManager.disableClientAccess(CLIENT_ID_1, USER_ORCID);
        profileEntityManager.disableClientAccess(CLIENT_ID_2, USER_ORCID);
        
        applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertTrue(applications.isEmpty());
    }
    
    @SuppressWarnings("unused")
    @Test
    public void testDontGetDuplicatedApplications() {
        Long seed = System.currentTimeMillis();
        Date expiration = new Date(System.currentTimeMillis() + 10000);
        OrcidOauth2TokenDetail token1 = createToken(CLIENT_ID_1, "token-1-" + seed, USER_ORCID, expiration, "/read-limited", false); // Displayed
        OrcidOauth2TokenDetail token2 = createToken(CLIENT_ID_1, "token-2-" + seed, USER_ORCID, expiration, "/orcid-profile/read-limited", false); // Displayed
        OrcidOauth2TokenDetail token3 = createToken(CLIENT_ID_1, "token-3-" + seed, USER_ORCID, expiration, "/activities/update", false); // Displayed
        OrcidOauth2TokenDetail token4 = createToken(CLIENT_ID_1, "token-4-" + seed, USER_ORCID, expiration, "/read-limited", false);
        OrcidOauth2TokenDetail token5 = createToken(CLIENT_ID_1, "token-5-" + seed, USER_ORCID, expiration, "/orcid-profile/read-limited", false);
        OrcidOauth2TokenDetail token6 = createToken(CLIENT_ID_1, "token-6-" + seed, USER_ORCID, expiration, "/activities/update", false);
        OrcidOauth2TokenDetail token7 = createToken(CLIENT_ID_1, "token-7-" + seed, USER_ORCID, expiration, "/read-limited", false);
        OrcidOauth2TokenDetail token8 = createToken(CLIENT_ID_1, "token-8-" + seed, USER_ORCID, expiration, "/orcid-profile/read-limited", false);
        OrcidOauth2TokenDetail token9 = createToken(CLIENT_ID_1, "token-9-" + seed, USER_ORCID, expiration, "/activities/update", false);
        OrcidOauth2TokenDetail token10 = createToken(CLIENT_ID_1, "token-10-" + seed, USER_ORCID, expiration, "/person/read-limited", false); // Displayed
        OrcidOauth2TokenDetail token11 = createToken(CLIENT_ID_1, "token-11-" + seed, USER_ORCID, expiration, "/person/read-limited", false);
        
        List<ApplicationSummary> applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertEquals(1, applications.size());
        
        // scopes grouped by label - Read limited information from your biography., Read your information with visibility set to Trusted Parties, Add/update your research activities (works, affiliations, etc)
        assertEquals(3, applications.get(0).getScopePaths().keySet().size());
        
        //Revoke them to check revoking one revokes all the ones with the same scopes
        profileEntityManager.disableClientAccess(CLIENT_ID_1, USER_ORCID);
        
        applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertTrue(applications.isEmpty());
    }
    
    @Test
    public void testDontGetDuplicatedApplicationsSameScopes() {
        Long seed = System.currentTimeMillis();
        Date expiration = new Date(System.currentTimeMillis() + 10000);
        createToken(CLIENT_ID_1, "token-1-" + seed, USER_ORCID, expiration, "/openid", false); // Displayed
        createToken(CLIENT_ID_1, "token-2-" + seed, USER_ORCID, expiration, "/openid", false);
        createToken(CLIENT_ID_1, "token-3-" + seed, USER_ORCID, expiration, "/openid", false);

        List<ApplicationSummary> applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertEquals(1, applications.size());
        
        //Revoke them to check revoking one revokes all the ones with the same scopes
        profileEntityManager.disableClientAccess(CLIENT_ID_1, USER_ORCID);
        
        applications = profileEntityManager.getApplications(USER_ORCID);
        assertNotNull(applications);
        assertEquals(0, applications.size());
    }
    
    public void testDisable2FA() {
        ProfileDao profileDao = Mockito.mock(ProfileDao.class);
        Mockito.doNothing().when(profileDao).disable2FA(Mockito.eq("some-orcid"));
        profileEntityManager.disable2FA("some-orcid");
        Mockito.verify(profileDao).disable2FA(Mockito.eq("some-orcid"));
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
