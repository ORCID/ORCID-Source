package org.orcid.integration.whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.manager.BiographyManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RecordNameManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.dao.AddressDao;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.TargetProxyHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.Sets;

/**
 * Usually run with -Xmx2g -Dorg.orcid.config.file=classpath:staging-persistence.properties
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-setup-context.xml" })
public class SetUpClientsAndUsers {
    // User variables
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String ORCID = "orcid";
    private static final String GIVEN_NAMES = "givenNames";
    private static final String FAMILY_NAMES = "familyNames";
    private static final String CREDIT_NAME = "creditName";
    private static final String BIO = "bio";
    private static final String ORCID_TYPE = "orcidType";
    private static final String MEMBER_TYPE = "memberType";    
    private static final String DEVELOPER_TOOLS = "developerTools";
    
    // Client variables
    private static final String MEMBER_ID = "memberId";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_NAME = "clientName";
    private static final String CLIENT_DESCRIPTION = "clientDescription";
    private static final String REDIRECT_URI = "clientRedirectUri";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String CLIENT_WEBSITE = "clientWebsite";
    private static final String CLIENT_TYPE = "clientType";
    private static final String ADD_ORCID_INTERNAL_SCOPES = "addInternalScopes";

    // Admin user
    @Value("${org.orcid.web.adminUser.username}")
    protected String adminUserName;
    @Value("${org.orcid.web.adminUser.password}")
    protected String adminPassword;
    @Value("${org.orcid.web.adminUser.orcidId}")
    protected String adminOrcidId;
    @Value("${org.orcid.web.adminUser.names.given_name}")
    protected String adminGivenName;
    @Value("${org.orcid.web.adminUser.names.family_names}")
    protected String adminFamilyNames;
    @Value("${org.orcid.web.adminUser.names.credit_name}")
    protected String adminCreditName;
    @Value("${org.orcid.web.adminUser.bio}")
    protected String adminBio;

    // User # 1
    @Value("${org.orcid.web.testUser1.username}")
    protected String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    protected String user1Password;
    @Value("${org.orcid.web.testUser1.orcidId}")
    protected String user1OrcidId;
    @Value("${org.orcid.web.testUser1.names.given_name}")
    protected String user1GivenName;
    @Value("${org.orcid.web.testUser1.names.family_names}")
    protected String user1FamilyNames;
    @Value("${org.orcid.web.testUser1.names.credit_name}")
    protected String user1CreditName;
    @Value("${org.orcid.web.testUser1.bio}")
    protected String user1Bio;

    // User # 2
    @Value("${org.orcid.web.testUser2.username}")
    protected String user2UserName;
    @Value("${org.orcid.web.testUser2.password}")
    protected String user2Password;
    @Value("${org.orcid.web.testUser2.orcidId}")
    protected String user2OrcidId;
    @Value("${org.orcid.web.testUser2.names.given_name}")
    protected String user2GivenName;
    @Value("${org.orcid.web.testUser2.names.family_names}")
    protected String user2FamilyNames;
    @Value("${org.orcid.web.testUser2.names.credit_name}")
    protected String user2CreditName;
    @Value("${org.orcid.web.testUser2.bio}")
    protected String user2Bio;

    // Public client
    @Value("${org.orcid.web.publicClient1.clientId}")
    protected String publicClientId;
    @Value("${org.orcid.web.publicClient1.clientSecret}")
    protected String publicClientSecret;
    @Value("${org.orcid.web.publicClient1.name}")
    protected String publicClientName;
    @Value("${org.orcid.web.publicClient1.redirectUri}")
    protected String publicClientRedirectUri;
    @Value("${org.orcid.web.publicClient1.description}")
    protected String publicClientDescription;
    @Value("${org.orcid.web.publicClient1.website}")
    protected String publicClientWebsite;
    // Lets assume testUser1 is also the owner of the public client
    @Value("${org.orcid.web.testUser1.orcidId}")
    protected String publicClientUserOwner;

    // Member # 1
    @Value("${org.orcid.web.member.id}")
    protected String member1Orcid;
    @Value("${org.orcid.web.member.email}")
    protected String member1Email;
    @Value("${org.orcid.web.member.password}")
    protected String member1Password;
    @Value("${org.orcid.web.member.type}")
    protected String member1Type;
    @Value("${org.orcid.web.member.name}")
    protected String member1Name;

    // Client # 1
    @Value("${org.orcid.web.testClient1.clientId}")
    protected String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    protected String client1ClientSecret;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    protected String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.name}")
    protected String client1Name;
    @Value("${org.orcid.web.testClient1.description}")
    protected String client1Description;
    @Value("${org.orcid.web.testClient1.website}")
    protected String client1Website;

    // Client # 2
    @Value("${org.orcid.web.testClient2.clientId}")
    protected String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    protected String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;
    @Value("${org.orcid.web.testClient2.name}")
    protected String client2Name;
    @Value("${org.orcid.web.testClient2.description}")
    protected String client2Description;
    @Value("${org.orcid.web.testClient2.website}")
    protected String client2Website;    

    // read-public access token
    @Value("${org.orcid.message-listener.api.read_public_access_token}")
    protected String readPublicAccessToken;
    
    @Resource
    protected EncryptionManager encryptionManager;
    @Resource
    protected OtherNameDao otherNameDao;
    @Resource
    protected ProfileKeywordDao profileKeywordDao;
    @Resource
    protected ExternalIdentifierDao externalIdentifierDao;
    @Resource
    protected AddressDao addressDao;
    @Resource
    protected EmailDao emailDao;
    @Resource
    protected WorkDao workDao;
    @Resource
    protected OrgAffiliationRelationDao orgAffiliationRelationDao;
    @Resource
    protected ProfileFundingDao profileFundingDao;
    @Resource
    protected PeerReviewDao peerReviewDao;
    @Resource
    protected NotificationDao notificationDao;
    @Resource
    protected ProfileDao profileDao;
    @Resource
    protected OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    @Resource
    protected ResearcherUrlDao researcherUrlDao;
    @Resource
    protected ClientDetailsManager clientDetailsManager;    
    @Resource
    protected ProfileEntityManager profileEntityManager;
    @Resource
    protected GivenPermissionToDao givenPermissionToDao;   
    @Resource
    protected BiographyManager biographyManager;
    @Resource
    protected RecordNameManager recordNameManager;    
    @Resource
    protected ClientDetailsDao clientDetailsDao;
    @Resource
    protected SourceManager sourceManager;
    @Resource
    protected RegistrationManager registrationManager;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Mock 
    protected SourceManager mockSourceManager;
    @Mock
    protected OrcidGenerationManager mockOrcidGenerationManager;
    
    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(registrationManager, "orcidGenerationManager", mockOrcidGenerationManager);        
                
        // Create admin user
        Map<String, String> adminParams = getParams(adminOrcidId);
        ProfileEntity adminProfile = profileDao.find(adminOrcidId);
        if (adminProfile == null) {
            createUser(adminParams);
            setAsAdmin(adminOrcidId);
        } else {
            clearRegistry(adminProfile, adminParams);
        }

        // Create user 1
        Map<String, String> user1Params = getParams(user1OrcidId);
        ProfileEntity user1Profile = profileDao.find(user1OrcidId);
        if (user1Profile == null) {
            createUser(user1Params);
        } else {
            clearRegistry(user1Profile, user1Params);
        }        
        
        // Create user 2
        Map<String, String> user2Params = getParams(user2OrcidId);
        ProfileEntity user2Profile = profileDao.find(user2OrcidId);
        if (user2Profile == null) {
            createUser(user2Params);
        } else {
            clearRegistry(user2Profile, user2Params);
        }

        // Create member 1
        Map<String, String> member1Params = getParams(member1Orcid);
        ProfileEntity member1Profile = profileDao.find(member1Orcid);
        if (member1Profile == null) {
            createUser(member1Params);
            setAsMember(member1Orcid);
        } else {
            clearRegistry(member1Profile, member1Params);
        }        

        // Create public client
        Map<String, String> publicClientParams = getParams(publicClientId);
        ClientType clientType = ClientType.PREMIUM_CREATOR;   
        if(publicClientParams.containsKey(CLIENT_TYPE)) {
            clientType = ClientType.fromValue(publicClientParams.get(CLIENT_TYPE));
        }
        
        ClientDetailsEntity publicClient = clientDetailsManager.findByClientId(publicClientId);
        clientType = ClientType.PUBLIC_CLIENT;
        if (publicClient == null) {
            createClient(publicClientParams, clientType);
        } else {
            clientDetailsManager.addScopesToClient(getScopes(publicClientParams, clientType), publicClient);
        }

        // Create client 1
        Map<String, String> client1Params = getParams(client1ClientId);
        clientType = ClientType.PREMIUM_CREATOR;   
        if(client1Params.containsKey(CLIENT_TYPE)) {
            clientType = ClientType.fromValue(client1Params.get(CLIENT_TYPE));
        }
        
        ClientDetailsEntity client1 = clientDetailsManager.findByClientId(client1ClientId);
        if (client1 == null) {
            createClient(client1Params, clientType);
        } else {
            clientDetailsManager.addAuthorizedGrantTypeToClient(Sets.newHashSet("implicit"), client1);
            clientDetailsManager.addScopesToClient(getScopes(client1Params, clientType), client1);
        }

        // Create client 2
        Map<String, String> client2Params = getParams(client2ClientId);
        clientType = ClientType.PREMIUM_CREATOR;   
        if(client2Params.containsKey(CLIENT_TYPE)) {
            clientType = ClientType.fromValue(client2Params.get(CLIENT_TYPE));
        }
        
        ClientDetailsEntity client2 = clientDetailsManager.findByClientId(client2ClientId);
        if (client2 == null) {
            createClient(client2Params, clientType);            
        } else {
            clientDetailsManager.addScopesToClient(getScopes(client2Params, clientType), client2);
        }
        
        // Ensure persistent tokens is disabled for client # 2
        clientDetailsDao.changePersistenceTokensProperty(client2ClientId, false);  
        
        // Create read-public access token for client # 1
        OrcidOauth2TokenDetail token = null;
        try {
            token = orcidOauth2TokenDetailDao.findByTokenValue(readPublicAccessToken);
        } catch(Exception e) {
            
        }
        if(token != null) {
            if(token.getTokenDisabled()) {
                token.setTokenDisabled(false);
            }
            token.setClientDetailsId(client1ClientId);
            token.setRevocationDate(null);
            token.setRevokeReason(null);
            token.setScope("/read-public");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, 20);
            token.setTokenExpiration(c.getTime());
            orcidOauth2TokenDetailDao.merge(token);
        } else {
            token = new OrcidOauth2TokenDetail();
            token.setTokenValue(readPublicAccessToken);
            token.setApproved(true);
            token.setClientDetailsId(client1ClientId);
            token.setDateCreated(new Date());
            token.setLastModified(new Date());            
            token.setPersistent(true);
            token.setRedirectUri(client1RedirectUri);
            token.setResourceId("orcid");
            token.setScope("/read-public");
            token.setTokenType("bearer");
            token.setTokenDisabled(false);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, 20);
            token.setTokenExpiration(c.getTime());
            orcidOauth2TokenDetailDao.persist(token);
        }
        
        setUpDelegates(user1OrcidId, user2OrcidId);
    }      

    private Map<String, String> getParams(String userId) {
        Map<String, String> params = new HashMap<String, String>();
        if (userId.equals(adminOrcidId)) {
            params.put(EMAIL, adminUserName);
            params.put(PASSWORD, adminPassword);
            params.put(ORCID, adminOrcidId);
            params.put(GIVEN_NAMES, adminGivenName);
            params.put(FAMILY_NAMES, adminFamilyNames);
            params.put(CREDIT_NAME, adminCreditName);
            params.put(BIO, adminBio);
            params.put(ORCID_TYPE, OrcidType.ADMIN.value());
        } else if (userId.equals(user1OrcidId)) {
            params.put(EMAIL, user1UserName);
            params.put(PASSWORD, user1Password);
            params.put(ORCID, user1OrcidId);
            params.put(GIVEN_NAMES, user1GivenName);
            params.put(FAMILY_NAMES, user1FamilyNames);
            params.put(CREDIT_NAME, user1CreditName);
            params.put(BIO, user1Bio);
            params.put(ORCID_TYPE, OrcidType.USER.value());
            params.put(DEVELOPER_TOOLS, "true");
        } else if (userId.equals(user2OrcidId)) {
            params.put(EMAIL, user2UserName);
            params.put(PASSWORD, user2Password);
            params.put(ORCID, user2OrcidId);
            params.put(GIVEN_NAMES, user2GivenName);
            params.put(FAMILY_NAMES, user2FamilyNames);
            params.put(CREDIT_NAME, user2CreditName);
            params.put(BIO, user2Bio);
            params.put(ORCID_TYPE, OrcidType.USER.value());
        } else if (userId.equals(member1Orcid)) {
            params.put(EMAIL, member1Email);
            params.put(PASSWORD, member1Password);
            params.put(ORCID, member1Orcid);
            params.put(CREDIT_NAME, member1Name);
            params.put(ORCID_TYPE, OrcidType.GROUP.value());
            params.put(MEMBER_TYPE, member1Type);
        } else if (userId.equals(publicClientId)) {
            params.put(MEMBER_ID, publicClientUserOwner);
            params.put(CLIENT_ID, publicClientId);
            params.put(CLIENT_NAME, publicClientName);
            params.put(CLIENT_DESCRIPTION, publicClientDescription);
            params.put(REDIRECT_URI, publicClientRedirectUri);
            params.put(CLIENT_SECRET, publicClientSecret);
            params.put(CLIENT_WEBSITE, publicClientWebsite);
            params.put(CLIENT_TYPE, ClientType.PUBLIC_CLIENT.value());            
        } else if (userId.equals(client1ClientId)) {
            params.put(MEMBER_ID, member1Orcid);
            params.put(CLIENT_ID, client1ClientId);
            params.put(CLIENT_NAME, client1Name);
            params.put(CLIENT_DESCRIPTION, client1Description);
            params.put(REDIRECT_URI, client1RedirectUri);
            params.put(CLIENT_SECRET, client1ClientSecret);
            params.put(CLIENT_WEBSITE, client1Website);
            params.put(CLIENT_TYPE, ClientType.PREMIUM_CREATOR.value());
            params.put(ADD_ORCID_INTERNAL_SCOPES, "true");
        } else if (userId.equals(client2ClientId)) {
            params.put(MEMBER_ID, member1Orcid);
            params.put(CLIENT_ID, client2ClientId);
            params.put(CLIENT_NAME, client2Name);
            params.put(CLIENT_DESCRIPTION, client2Description);
            params.put(REDIRECT_URI, client2RedirectUri);
            params.put(CLIENT_SECRET, client2ClientSecret);
            params.put(CLIENT_WEBSITE, client2Website);
            params.put(CLIENT_TYPE, ClientType.PREMIUM_CREATOR.value());
        } else {
            throw new ApplicationException("Unable to find params for orcid: " + userId);
        }
        return params;
    }

    private void createUser(Map<String, String> params) throws Exception {
        
        String orcid = params.get(ORCID);
        when(mockOrcidGenerationManager.createNewOrcid()).thenReturn(orcid);
        
        Registration registration = new Registration();
        org.orcid.pojo.ajaxForm.Visibility v = new org.orcid.pojo.ajaxForm.Visibility();
        v.setVisibility(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC);
        registration.setActivitiesVisibilityDefault(v);
        registration.setEmail(Text.valueOf(params.get(EMAIL)));
        registration.setFamilyNames(Text.valueOf(params.get(FAMILY_NAMES)));
        registration.setGivenNames(Text.valueOf(params.get(GIVEN_NAMES)));
        registration.setPassword(Text.valueOf(params.get(PASSWORD)));
        registration.setPasswordConfirm(Text.valueOf(params.get(PASSWORD)));
        
        String verifyOrcid = registrationManager.createMinimalRegistration(registration, false, null, "");
        assertEquals(orcid, verifyOrcid);
        emailDao.verifyEmail(params.get(EMAIL));
    }
    
    private void setAsMember(String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ProfileEntity e = profileDao.find(orcid);
                e.setOrcidType(OrcidType.GROUP.name());
                e.setGroupType(MemberType.PREMIUM.name());
                profileDao.merge(e);
            }
        });
    }

    private void setAsAdmin(String orcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                ProfileEntity e = profileDao.find(orcid);
                e.setOrcidType(OrcidType.ADMIN.name());
                profileDao.merge(e);
            }
        });
    }
    
    private boolean clearRegistry(ProfileEntity entity, Map<String, String> params) {
        if (entity != null) {
            String orcid = params.get(ORCID);
            String email = params.get(EMAIL);
            // Check if the profile have the same email, if not, throw an
            // exception
            EmailEntity e = emailDao.findPrimaryEmail(orcid);
            if (!email.equals(e.getEmail())) {
                throw new ApplicationException(
                        "User with email " + params.get(EMAIL) + " must have orcid id '" + orcid + "' but it is '" + entity.getId() + "'");
            }

            // Check if the profile have the same password, if not, update the
            // password
            String encryptedPassword = encryptionManager.hashForInternalUse(params.get(PASSWORD));
            if (!encryptedPassword.equals(entity.getEncryptedPassword())) {                
                profileDao.updateEncryptedPassword(orcid, encryptedPassword);                
            }

            // Set default names
            Name name = new Name();
            name.setCreditName(new CreditName(params.get(CREDIT_NAME)));
            name.setGivenNames(new GivenNames(params.get(GIVEN_NAMES)));
            name.setFamilyName(new FamilyName(params.get(FAMILY_NAMES)));
            name.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));                       
            if(recordNameManager.exists(orcid)) {
                recordNameManager.updateRecordName(orcid, name);
            } else {
                recordNameManager.createRecordName(orcid, name);
            }
                                   
            // Set default bio
            org.orcid.jaxb.model.record_v2.Biography bio = biographyManager.getBiography(orcid);
            if (bio == null || bio.getContent() == null) {
                bio = new org.orcid.jaxb.model.record_v2.Biography(params.get(BIO)); 
                bio.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility().value()));
                biographyManager.createBiography(orcid, bio);
            } else {
                bio.setContent(params.get(BIO));
                bio.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility().value()));
                biographyManager.updateBiography(orcid, bio);
            }
            
            // Remove other names
            List<OtherNameEntity> otherNames = otherNameDao.getOtherNames(orcid, 0L);
            if(otherNames != null && !otherNames.isEmpty()) {
                for(OtherNameEntity otherName : otherNames) {
                    otherNameDao.deleteOtherName(otherName);
                }
            }

            // Remove keywords
            List<ProfileKeywordEntity> keywords = profileKeywordDao.getProfileKeywords(orcid, 0L);
            if(keywords != null && !keywords.isEmpty()) {
                for(ProfileKeywordEntity keyword : keywords) {
                    profileKeywordDao.deleteProfileKeyword(keyword);
                }
            }            

            //Remove researcher urls
            List<ResearcherUrlEntity> rUrls = researcherUrlDao.getResearcherUrls(orcid, 0L);
            if(rUrls != null && !rUrls.isEmpty()) {
                for(ResearcherUrlEntity rUrl : rUrls) {
                    researcherUrlDao.deleteResearcherUrl(orcid, rUrl.getId());
                }
            }

            // Remove external ids
            List<ExternalIdentifierEntity> extIds = externalIdentifierDao.getExternalIdentifiers(orcid, System.currentTimeMillis());
            if (extIds != null && !extIds.isEmpty()) {
                for (ExternalIdentifierEntity extId : extIds) {
                    externalIdentifierDao.removeExternalIdentifier(orcid, extId.getId());                    
                }
            }

            // Remove addresses
            List<AddressEntity> addresses = addressDao.getAddresses(orcid, 0L);
            if(addresses != null && !addresses.isEmpty()) {
                for(AddressEntity address : addresses) {
                    addressDao.deleteAddress(orcid, address.getId());
                }
            }

            // Remove emails
            List<EmailEntity> emails = emailDao.findByOrcid(orcid, profileEntityManager.getLastModified(orcid));
            if(emails != null && !emails.isEmpty()) {
                for(EmailEntity rc2Email : emails) {
                    if (!params.get(EMAIL).equals(rc2Email.getEmail())) {
                        emailDao.removeEmail(orcid, rc2Email.getEmail());
                    }
                }
            }
            
            // Remove notifications
            List<NotificationEntity> notifications = notificationDao.findByOrcid(orcid, true, 0, 10000);
            if (notifications != null && !notifications.isEmpty()) {
                for (NotificationEntity notification : notifications) {
                    notificationDao.deleteNotificationItemByNotificationId(notification.getId());
                    notificationDao.deleteNotificationWorkByNotificationId(notification.getId());
                    notificationDao.deleteNotificationById(notification.getId());
                }
            }

            // Remove works
            List<WorkLastModifiedEntity> works = workDao.getWorkLastModifiedList(orcid);
            if(works != null && !works.isEmpty()) {
                for(WorkLastModifiedEntity work : works) {
                    workDao.removeWork(orcid, work.getId());
                }
            }
            
            // Remove affiliations
            List<OrgAffiliationRelationEntity> affiliations = orgAffiliationRelationDao.getByUser(orcid);
            if (affiliations != null && !affiliations.isEmpty()) {
                for (OrgAffiliationRelationEntity affiliation : affiliations) {
                    orgAffiliationRelationDao.remove(affiliation.getId());
                }
            }

            // Remove fundings
            List<ProfileFundingEntity> fundings = profileFundingDao.getByUser(orcid, profileEntityManager.getLastModified(orcid));
            if(fundings != null && !fundings.isEmpty()) {
                for(ProfileFundingEntity funding : fundings) {
                    profileFundingDao.removeProfileFunding(orcid, funding.getId());
                }
            }
            
            // Remove peer reviews
            List<PeerReviewEntity> peerReviews = peerReviewDao.getByUser(orcid, profileEntityManager.getLastModified(orcid));
            if(peerReviews != null && !peerReviews.isEmpty()) {
                for(PeerReviewEntity peerReview : peerReviews) {
                    peerReviewDao.removePeerReview(orcid, peerReview.getId());
                }
            }
            
            // Remove 3d party links
            List<OrcidOauth2TokenDetail> tokenDetails = orcidOauth2TokenDetailDao.findByUserName(orcid);
            if (tokenDetails != null && !tokenDetails.isEmpty()) {
                for (OrcidOauth2TokenDetail token : tokenDetails) {
                    orcidOauth2TokenDetailDao.remove(token.getId());
                }
            }

            //Unlock just in case it is locked
            profileDao.unlockProfile(orcid);
            
            return true;
        }
        return false;
    }

    private void createClient(Map<String, String> params, ClientType clientType) {
        Set<String> clientResourceIds = new HashSet<String>();
        clientResourceIds.add("orcid");
        Set<String> clientAuthorizedGrantTypes = new HashSet<String>();
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("refresh_token");
        if (client1ClientId.equals(params.get(CLIENT_ID))){
            clientAuthorizedGrantTypes.add("implicit");
        }
        
        Set<RedirectUri> redirectUrisToAdd = new HashSet<RedirectUri>();
        RedirectUri redirectUri = new RedirectUri(params.get(REDIRECT_URI));
                        
        if(clientType.equals(ClientType.PUBLIC_CLIENT)) {
            redirectUri.setType(RedirectUriType.SSO_AUTHENTICATION);
        } else {
            redirectUri.setType(RedirectUriType.DEFAULT);    
        }
        redirectUrisToAdd.add(redirectUri);
        
        
        List<String> clientGrantedAuthorities = new ArrayList<String>();
        clientGrantedAuthorities.add("ROLE_CLIENT");

        String name = params.get(CLIENT_NAME);
        String description = params.get(CLIENT_DESCRIPTION);
        String website = params.get(CLIENT_WEBSITE);
        String clientId = params.get(CLIENT_ID);        
        String clientSecret = encryptionManager.encryptForInternalUse(params.get(CLIENT_SECRET));
        String memberId = params.get(MEMBER_ID);
        
        Set<String> scopes = getScopes(params, clientType);
            
        clientDetailsManager.populateClientDetailsEntity(clientId, memberId, name, description, null, website, clientSecret, clientType, scopes,
                clientResourceIds, clientAuthorizedGrantTypes, redirectUrisToAdd, clientGrantedAuthorities, true);
    }

    private Set<String> getScopes(Map<String, String> params, ClientType clientType) {
        Set<String> scopes = null;
        if(clientType.equals(ClientType.PUBLIC_CLIENT)) {
            scopes = new HashSet<String>(Arrays.asList(ScopePathType.AUTHENTICATE.value(), ScopePathType.READ_PUBLIC.value(),ScopePathType.OPENID.value()));
        } else {
            scopes = ClientType.premiumCreatorScopes();
            if(params.containsKey(ADD_ORCID_INTERNAL_SCOPES)) {
                scopes.add(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED.value());
            }
                   
            //Add scopes to allow group read and update
            scopes.add(ScopePathType.GROUP_ID_RECORD_READ.value());
            scopes.add(ScopePathType.GROUP_ID_RECORD_UPDATE.value());
            
            //Add notifications scope
            scopes.add(ScopePathType.PREMIUM_NOTIFICATION.value());
            
            //Add openid scope
            scopes.add(ScopePathType.OPENID.value());
        }
        return scopes;
    }
    
    @Test
    public void testSetUpIsDone() {
        ProfileEntity p = profileDao.find(adminOrcidId);
        assertNotNull(p);
        assertEquals(adminOrcidId, p.getId());
        assertEquals("ADMIN", p.getOrcidType());
        
        p = profileDao.find(user1OrcidId);
        assertNotNull(p);
        assertEquals(user1OrcidId, p.getId());
        assertEquals("USER", p.getOrcidType());
        
        p = profileDao.find(user2OrcidId);
        assertNotNull(p);
        assertEquals(user2OrcidId, p.getId());
        assertEquals("USER", p.getOrcidType());
        
        p = profileDao.find(member1Orcid);
        assertNotNull(p);
        assertEquals(member1Orcid, p.getId());
        assertEquals("GROUP", p.getOrcidType());
                
        ClientDetailsEntity existingClient = clientDetailsManager.findByClientId(publicClientId);
        assertNotNull(existingClient);
        assertEquals(user1OrcidId, existingClient.getGroupProfileId());
        assertNotNull(existingClient.getRegisteredRedirectUri());
        assertEquals(1, existingClient.getRegisteredRedirectUri().size());
        assertNotNull(existingClient.getRegisteredRedirectUri().iterator());
        assertTrue(existingClient.getRegisteredRedirectUri().iterator().hasNext());
        assertEquals(publicClientRedirectUri, existingClient.getRegisteredRedirectUri().iterator().next());
                
        existingClient = clientDetailsManager.findByClientId(client1ClientId);
        assertNotNull(existingClient);
        assertEquals(member1Orcid, existingClient.getGroupProfileId());
        assertNotNull(existingClient.getRegisteredRedirectUri());
        assertEquals(1, existingClient.getRegisteredRedirectUri().size());
        assertNotNull(existingClient.getRegisteredRedirectUri().iterator());
        assertTrue(existingClient.getRegisteredRedirectUri().iterator().hasNext());
        assertEquals(client1RedirectUri, existingClient.getRegisteredRedirectUri().iterator().next());
        
        existingClient = clientDetailsManager.findByClientId(client2ClientId);
        assertNotNull(existingClient);
        assertEquals(member1Orcid, existingClient.getGroupProfileId());
        assertNotNull(existingClient.getRegisteredRedirectUri());
        assertEquals(1, existingClient.getRegisteredRedirectUri().size());
        assertNotNull(existingClient.getRegisteredRedirectUri().iterator());
        assertTrue(existingClient.getRegisteredRedirectUri().iterator().hasNext());
        assertEquals(client2RedirectUri, existingClient.getRegisteredRedirectUri().iterator().next());                        
    }
    
    //------------------------------------------------------------------------------           
    /**
     * Set up delegates
     * Please see tests: 
     * */
    public void setUpDelegates(String giver, String receiver) {
        GivenPermissionToEntity permission = new GivenPermissionToEntity();
        permission.setGiver(giver);
        ProfileSummaryEntity r = new ProfileSummaryEntity(receiver);
        r.setLastModified(new Date());
        permission.setReceiver(r);
        permission.setApprovalDate(new Date());
        givenPermissionToDao.merge(permission);
        givenPermissionToDao.flush();
    }
    
}