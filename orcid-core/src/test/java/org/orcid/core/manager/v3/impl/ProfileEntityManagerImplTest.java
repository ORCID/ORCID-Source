package org.orcid.core.manager.v3.impl;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.AddressManager;
import org.orcid.core.manager.v3.BiographyManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ExternalIdentifierManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OtherNameManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.ProfileKeywordManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author: Declan Newman (declan) Date: 10/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
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
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource(name="orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    @Resource(name = "addressManagerV3")
    private AddressManager addressManager;
    
    @Resource(name = "externalIdentifierManagerV3")
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource(name = "profileKeywordManagerV3")
    private ProfileKeywordManager profileKeywordManager;

    @Resource(name = "otherNameManagerV3")
    private OtherNameManager otherNameManager;

    @Resource(name = "researcherUrlManagerV3")
    private ResearcherUrlManager researcherUrlManager;
    
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
        userConnection.setEmail("blah@blah.com");
        userConnection.setOrcid("4444-4444-4444-4441");
        userConnection.setId(pk);
        userConnection.setRank(1);
        userConnectionDao.persist(userConnection);
        assertNotNull(userConnection.getDateCreated());
        assertNotNull(userConnection.getLastModified());
        
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
        userConnection.setEmail("blah@blah.com");
        userConnection.setOrcid("4444-4444-4444-4441");
        userConnection.setId(pk);
        userConnection.setRank(1);
        userConnectionDao.persist(userConnection);
        assertNotNull(userConnection.getDateCreated());
        assertNotNull(userConnection.getLastModified());
        
        boolean result = profileEntityManager.deactivateRecord("4444-4444-4444-4441");
        assertTrue(result);

        ProfileEntity deactivated = profileEntityCacheManager.retrieve("4444-4444-4444-4441");     
        assertEquals(0, userConnectionDao.findByOrcid("4444-4444-4444-4441").size());
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
        String orcid = "0000-0000-0000-0001";
        Claim claim = new Claim();
        claim.setActivitiesVisibilityDefault(org.orcid.pojo.ajaxForm.Visibility.valueOf(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE));
        claim.setPassword(Text.valueOf("passwordTest1"));
        claim.setPasswordConfirm(Text.valueOf("passwordTest1"));
        Checkbox checked = new Checkbox();
        checked.setValue(true);
        claim.setSendChangeNotifications(checked);
        claim.setSendOrcidNews(checked);
        claim.setTermsOfUse(checked);
        
        assertTrue(profileEntityManager.claimProfileAndUpdatePreferences(orcid, "public_0000-0000-0000-0001@test.orcid.org", AvailableLocales.EN, claim));
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        assertNotNull(profile);
        
        Addresses addresses = addressManager.getAddresses(orcid);
        assertEquals(3, addresses.getAddress().size());
        for(Address a : addresses.getAddress()) {
            assertEquals(Visibility.PRIVATE, a.getVisibility());
        }
        
        PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(orcid);
        assertEquals(3, extIds.getExternalIdentifiers().size());
        for(PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            assertEquals(Visibility.PRIVATE, extId.getVisibility());
        }
        
        Keywords keywords = profileKeywordManager.getKeywords(orcid);
        assertEquals(3, keywords.getKeywords().size());
        for(Keyword k : keywords.getKeywords()) {
            assertEquals(Visibility.PRIVATE, k.getVisibility());
        }
        
        OtherNames otherNames = otherNameManager.getOtherNames(orcid);
        assertEquals(3, otherNames.getOtherNames().size());
        for(OtherName o : otherNames.getOtherNames()) {
            assertEquals(Visibility.PRIVATE, o.getVisibility());
        }
        
        ResearcherUrls rUrls = researcherUrlManager.getResearcherUrls(orcid);
        assertEquals(3, rUrls.getResearcherUrls().size());
        for(ResearcherUrl r : rUrls.getResearcherUrls()) {
            assertEquals(Visibility.PRIVATE, r.getVisibility());
        }
        
        Biography bio = biographyManager.getBiography("0000-0000-0000-0001");
        assertEquals(org.orcid.jaxb.model.v3.release.common.Visibility.PRIVATE, bio.getVisibility());
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
        Mockito.doNothing().when(mockProfileDao).changeEncryptedPassword(Mockito.eq("orcid"), Mockito.eq("encryptedPassword"));
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
        assertEquals(5, applications.get(0).getScopePaths().keySet().size());
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.READ_LIMITED.toString()));
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.ORCID_PROFILE_READ_LIMITED.toString()));
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.ACTIVITIES_UPDATE.toString()));
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.ACTIVITIES_READ_LIMITED.toString()));
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.ORCID_WORKS_READ_LIMITED.toString()));
        
        assertEquals(3, applications.get(1).getScopePaths().keySet().size());
        assertTrue(applications.get(1).getScopePaths().keySet().contains(ScopePathType.READ_LIMITED.toString()));
        assertTrue(applications.get(1).getScopePaths().keySet().contains(ScopePathType.ORCID_PROFILE_READ_LIMITED.toString()));
        assertTrue(applications.get(1).getScopePaths().keySet().contains(ScopePathType.ACTIVITIES_UPDATE.toString()));

        // test ordering based on name
        assertEquals(CLIENT_ID_1, applications.get(0).getClientId());
        assertEquals(CLIENT_ID_2, applications.get(1).getClientId());

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
        assertEquals(4, applications.get(0).getScopePaths().keySet().size());
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.READ_LIMITED.toString()));
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.ORCID_PROFILE_READ_LIMITED.toString()));
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.ACTIVITIES_UPDATE.toString()));
        assertTrue(applications.get(0).getScopePaths().keySet().contains(ScopePathType.PERSON_READ_LIMITED.toString()));
        
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
    
    @Transactional
    private OrcidOauth2TokenDetail createToken(String clientId, String tokenValue, String userOrcid, Date expirationDate, String scopes, boolean disabled) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setClientDetailsId(clientId);
        token.setOrcid(userOrcid);
        token.setScope(scopes);
        token.setTokenDisabled(disabled);
        token.setTokenExpiration(expirationDate);
        token.setTokenType("bearer");
        token.setTokenValue(tokenValue);
        orcidOauth2TokenDetailDao.persist(token);
        assertNotNull(token.getDateCreated());
        assertNotNull(token.getLastModified());
        return token;
    }
    
}
