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
package org.orcid.integration.whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.common_rc2.Country;
import org.orcid.jaxb.model.common_rc2.CreatedDate;
import org.orcid.jaxb.model.common_rc2.CreditName;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.Source;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.FamilyName;
import org.orcid.jaxb.model.record_rc2.GivenNames;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
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
    private static final String LOCKED = "locked";
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

    // Member # 2 - Locked
    @Value("${org.orcid.web.locked.member.id}")
    protected String lockedMemberOrcid;
    @Value("${org.orcid.web.locked.member.email}")
    protected String lockedMemberEmail;
    @Value("${org.orcid.web.locked.member.password}")
    protected String lockedMemberPassword;
    @Value("${org.orcid.web.locked.member.name}")
    protected String lockedMemberName;
    @Value("${org.orcid.web.locked.member.type}")
    protected String lockedMemberType;

    // Member # 2 - Client
    @Value("${org.orcid.web.locked.member.client.id}")
    protected String lockedMemberClient1ClientId;
    @Value("${org.orcid.web.locked.member.client.secret}")
    protected String lockedMemberClient1ClientSecret;
    @Value("${org.orcid.web.locked.member.client.ruri}")
    protected String lockedMemberClient1RedirectUri;
    @Value("${org.orcid.web.locked.member.client.name}")
    protected String lockedMemberClient1Name;
    @Value("${org.orcid.web.locked.member.client.description}")
    protected String lockedMemberClient1Description;
    @Value("${org.orcid.web.locked.member.client.website}")
    protected String lockedMemberClient1Website;

    @Resource
    protected OrcidProfileManager orcidProfileManager;
    @Resource
    protected EncryptionManager encryptionManager;
    @Resource
    protected OtherNameManager otherNameManager;
    @Resource
    protected ProfileKeywordManager profileKeywordManager;
    @Resource
    protected ExternalIdentifierManager externalIdentifierManager;
    @Resource
    protected AddressManager addressManager;
    @Resource
    protected EmailManager emailManager;
    @Resource
    protected WorkManager workManager;
    @Resource
    protected AffiliationsManager affiliationsManager;
    @Resource
    protected ProfileFundingManager profileFundingManager;
    @Resource
    protected PeerReviewManager peerReviewManager;
    @Resource
    protected NotificationDao notificationDao;
    @Resource
    protected OrgAffiliationRelationDao orgAffiliationRelationDao;
    @Resource
    protected ProfileDao profileDao;
    @Resource
    protected OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    @Resource
    protected ResearcherUrlManager researcherUrlManager;
    @Resource
    protected ClientDetailsManager clientDetailsManager;
    @Resource
    protected OrcidClientGroupManager orcidClientGroupManager;
    @Resource
    protected ProfileEntityManager profileEntityManager;
    @Resource
    protected GivenPermissionToDao givenPermissionToDao;
    
    @Before
    public void before() throws Exception {
        // Create admin user
        Map<String, String> adminParams = getParams(adminOrcidId);
        OrcidProfile adminProfile = orcidProfileManager.retrieveOrcidProfile(adminOrcidId);
        if (adminProfile == null) {
            createUser(adminParams);
        } else {
            clearRegistry(adminProfile, adminParams);
        }

        // Create user 1
        Map<String, String> user1Params = getParams(user1OrcidId);
        OrcidProfile user1Profile = orcidProfileManager.retrieveOrcidProfile(user1OrcidId);
        if (user1Profile == null) {
            createUser(user1Params);
        } else {
            clearRegistry(user1Profile, user1Params);
        }
        setUpAddresses(user1OrcidId);
        setUpKeywords(user1OrcidId);
        setUpOtherNames(user1OrcidId);
        setUpEmails(user1OrcidId);
        setUpExternalIdentifiers(user1OrcidId);
        
        // Create user 2
        Map<String, String> user2Params = getParams(user2OrcidId);
        OrcidProfile user2Profile = orcidProfileManager.retrieveOrcidProfile(user2OrcidId);
        if (user2Profile == null) {
            createUser(user2Params);
        } else {
            clearRegistry(user2Profile, user2Params);
        }

        // Create member 1
        Map<String, String> member1Params = getParams(member1Orcid);
        OrcidProfile member1Profile = orcidProfileManager.retrieveOrcidProfile(member1Orcid);
        if (member1Profile == null) {
            createUser(member1Params);
        } else {
            clearRegistry(member1Profile, member1Params);
        }

        // Create locked member
        Map<String, String> lockedMemberParams = getParams(lockedMemberOrcid);
        OrcidProfile lockedMemberProfile = orcidProfileManager.retrieveOrcidProfile(lockedMemberOrcid);
        if (lockedMemberProfile == null) {
            createUser(lockedMemberParams);
        } else {
            clearRegistry(lockedMemberProfile, lockedMemberParams);
        }

        // Create public client
        Map<String, String> publicClientParams = getParams(publicClientId);
        ClientDetailsEntity publicClient = clientDetailsManager.findByClientId(publicClientId);
        if (publicClient == null) {
            createClient(publicClientParams);
        } 

        // Create client 1
        Map<String, String> client1Params = getParams(client1ClientId);
        ClientDetailsEntity client1 = clientDetailsManager.findByClientId(client1ClientId);
        if (client1 == null) {
            createClient(client1Params);
        } 

        // Create client 2
        Map<String, String> client2Params = getParams(client2ClientId);
        ClientDetailsEntity client2 = clientDetailsManager.findByClientId(client2ClientId);
        if (client2 == null) {
            createClient(client2Params);
        } 

        // Create locked client
        Map<String, String> lockedClientParams = getParams(lockedMemberClient1ClientId);
        ClientDetailsEntity lockedClient = clientDetailsManager.findByClientId(lockedMemberClient1ClientId);
        if (lockedClient == null) {
            createClient(lockedClientParams);
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
        } else if (userId.equals(lockedMemberOrcid)) {
            params.put(EMAIL, lockedMemberEmail);
            params.put(PASSWORD, lockedMemberPassword);
            params.put(ORCID, lockedMemberOrcid);
            params.put(CREDIT_NAME, lockedMemberName);
            params.put(ORCID_TYPE, OrcidType.GROUP.value());
            params.put(MEMBER_TYPE, lockedMemberType);
            params.put(LOCKED, "true");
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
        } else if (userId.equals(lockedMemberClient1ClientId)) {
            params.put(MEMBER_ID, lockedMemberOrcid);
            params.put(CLIENT_ID, lockedMemberClient1ClientId);
            params.put(CLIENT_NAME, lockedMemberClient1Name);
            params.put(CLIENT_DESCRIPTION, lockedMemberClient1Description);
            params.put(REDIRECT_URI, lockedMemberClient1RedirectUri);
            params.put(CLIENT_SECRET, lockedMemberClient1ClientSecret);
            params.put(CLIENT_WEBSITE, lockedMemberClient1Website);
            params.put(CLIENT_TYPE, ClientType.PREMIUM_CREATOR.value());
        } else {
            throw new ApplicationException("Unable to find params for orcid: " + userId);
        }
        return params;
    }

    private void createUser(Map<String, String> params) throws Exception {
        // Create it
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier(params.get(ORCID)));
        orcidProfile.setType(OrcidType.fromValue(params.get(ORCID_TYPE)));
        if (params.get(MEMBER_TYPE) != null) {
            orcidProfile.setGroupType(MemberType.fromValue(params.get(MEMBER_TYPE)));
        }
        orcidProfile.setPassword(params.get(PASSWORD));
        
        OrcidInternal internal = new OrcidInternal();
        Preferences preferences = new Preferences();
        ActivitiesVisibilityDefault visibilityDefaults = new ActivitiesVisibilityDefault();
        visibilityDefaults.setValue(Visibility.PUBLIC);
        preferences.setActivitiesVisibilityDefault(visibilityDefaults);
        internal.setPreferences(preferences);
        orcidProfile.setOrcidInternal(internal);
        
        Email email = new Email(params.get(EMAIL));
        email.setCurrent(true);
        email.setPrimary(true);
        email.setVerified(true);
        email.setVisibility(Visibility.PUBLIC);
        List<Email> emails = new ArrayList<Email>();
        emails.add(email);
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setEmail(emails);

        org.orcid.jaxb.model.message.PersonalDetails personalDetails = new org.orcid.jaxb.model.message.PersonalDetails();
        org.orcid.jaxb.model.message.CreditName creditName = new org.orcid.jaxb.model.message.CreditName(params.get(CREDIT_NAME));
        creditName.setVisibility(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility());
        personalDetails.setCreditName(creditName);
        personalDetails.setFamilyName(new org.orcid.jaxb.model.message.FamilyName(params.get(FAMILY_NAMES)));
        personalDetails.setGivenNames(new org.orcid.jaxb.model.message.GivenNames(params.get(GIVEN_NAMES)));

        OrcidBio bio = new OrcidBio();
        bio.setContactDetails(contactDetails);
        bio.setPersonalDetails(personalDetails);
        bio.setBiography(new Biography(params.get(BIO), OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility()));
        orcidProfile.setOrcidBio(bio);

        OrcidHistory history = new OrcidHistory();
        history.setClaimed(new Claimed(true));
        history.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        orcidProfile.setOrcidHistory(history);
        orcidProfileManager.createOrcidProfile(orcidProfile, false, false);
        
        if(params.containsKey(LOCKED)) {
            profileEntityManager.lockProfile(params.get(ORCID));               
        }        
        
        if(params.containsKey(DEVELOPER_TOOLS)) {
            profileEntityManager.enableDeveloperTools(orcidProfile);
        }
    }

    private boolean clearRegistry(OrcidProfile existingProfile, Map<String, String> params) {
        if (existingProfile != null) {
            String orcid = params.get(ORCID);
            String email = params.get(EMAIL);
            // Check if the profile have the same email, if not, throw an
            // exception
            if (existingProfile.getOrcidBio() == null || existingProfile.getOrcidBio().getContactDetails() == null
                    || existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() == null
                    || !email.equals(existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue())) {
                throw new ApplicationException(
                        "User with email " + params.get(EMAIL) + " must have orcid id '" + orcid + "' but it is '" + existingProfile.getOrcidId() + "'");
            }

            // Check if the profile have the same password, if not, update the
            // password
            String encryptedPassword = encryptionManager.hashForInternalUse(params.get(PASSWORD));
            if (!encryptedPassword.equals(existingProfile.getPassword())) {
                existingProfile.setPassword(params.get(PASSWORD));
                orcidProfileManager.updatePasswordInformation(existingProfile);
            }

            // Set default names
            PersonalDetails personalDetails = new PersonalDetails();
            Name name = new Name();
            name.setCreditName(new CreditName(params.get(CREDIT_NAME)));
            name.setGivenNames(new GivenNames(params.get(GIVEN_NAMES)));
            name.setFamilyName(new FamilyName(params.get(FAMILY_NAMES)));
            name.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));
            personalDetails.setName(name);
            orcidProfileManager.updateNames(orcid, personalDetails);

            // Set default bio
            OrcidBio bio = new OrcidBio();
            bio.setBiography(new Biography(params.get(BIO), OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility()));
            existingProfile.setOrcidBio(bio);
            orcidProfileManager.updateBiography(existingProfile);

            // Remove other names
            otherNameManager.updateOtherNames(orcid, new OtherNames(),
                    org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT.getVisibility().value()));

            // Remove keywords
            profileKeywordManager.updateKeywords(orcid, new Keywords(),
                    org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.KEYWORD_DEFAULT.getVisibility().value()));

            researcherUrlManager.updateResearcherUrls(orcid, new ResearcherUrls(),
                    org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility().value()));

            // Remove external ids
            PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(orcid, System.currentTimeMillis());

            if (extIds != null && extIds.getExternalIdentifier() != null && !extIds.getExternalIdentifier().isEmpty()) {
                for (PersonExternalIdentifier extId : extIds.getExternalIdentifier()) {
                    externalIdentifierManager.deleteExternalIdentifier(orcid, extId.getPutCode(), false);
                }
            }

            // Remove addresses
            addressManager.updateAddresses(orcid, new Addresses(),
                    org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility().value()));

            // Remove emails
            Emails emails = emailManager.getEmails(orcid, System.currentTimeMillis());
            if (emails != null && emails.getEmails() != null) {
                for (org.orcid.jaxb.model.record_rc2.Email rc2Email : emails.getEmails()) {
                    if (!params.get(EMAIL).equals(rc2Email.getEmail())) {
                        emailManager.removeEmail(orcid, rc2Email.getEmail());
                    }
                }
            }

            // Remove notifications
            List<NotificationEntity> notifications = notificationDao.findByOrcid(orcid, true, 0, 10000);
            if (notifications != null && !notifications.isEmpty()) {
                for (NotificationEntity notification : notifications) {
                    notificationDao.remove(notification.getId());
                }
            }

            // Remove works
            List<WorkSummary> works = workManager.getWorksSummaryList(orcid, System.currentTimeMillis());
            List<Long> workIds = new ArrayList<Long>();
            if (works != null && !works.isEmpty()) {
                for (WorkSummary work : works) {
                    workIds.add(work.getPutCode());
                }
                workManager.removeWorks(orcid, workIds);
            }

            // Remove affiliations
            List<OrgAffiliationRelationEntity> affiliations = orgAffiliationRelationDao.getByUser(orcid);
            if (affiliations != null && !affiliations.isEmpty()) {
                for (OrgAffiliationRelationEntity affiliation : affiliations) {
                    orgAffiliationRelationDao.remove(affiliation.getId());
                }
            }

            // Remove fundings
            List<FundingSummary> fundings = profileFundingManager.getFundingSummaryList(orcid, System.currentTimeMillis());
            if (fundings != null && !fundings.isEmpty()) {
                for (FundingSummary funding : fundings) {
                    profileFundingManager.removeProfileFunding(orcid, funding.getPutCode());
                }
            }

            // Remove peer reviews
            List<PeerReview> peerReviews = peerReviewManager.findPeerReviews(orcid, System.currentTimeMillis());
            if (peerReviews != null && !peerReviews.isEmpty()) {
                for (PeerReview peerReview : peerReviews) {
                    peerReviewManager.removePeerReview(orcid, peerReview.getPutCode());
                }
            }
            // Remove 3d party links
            List<OrcidOauth2TokenDetail> tokenDetails = orcidOauth2TokenDetailDao.findByUserName(orcid);
            if (tokenDetails != null && !tokenDetails.isEmpty()) {
                for (OrcidOauth2TokenDetail token : tokenDetails) {
                    orcidOauth2TokenDetailDao.remove(token.getId());
                }
            }

            return true;
        }
        return false;
    }

    private void createClient(Map<String, String> params) {
        Set<String> clientResourceIds = new HashSet<String>();
        clientResourceIds.add("orcid");
        Set<String> clientAuthorizedGrantTypes = new HashSet<String>();
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("refresh_token");
        
        ClientType clientType = ClientType.PREMIUM_CREATOR;
        
        if(params.containsKey(CLIENT_TYPE)) {
            clientType = ClientType.fromValue(params.get(CLIENT_TYPE));
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
        ProfileEntity memberEntity = new ProfileEntity(params.get(MEMBER_ID));
        String clientSecret = encryptionManager.encryptForInternalUse(params.get(CLIENT_SECRET));

        Set<String> scopes = orcidClientGroupManager.premiumCreatorScopes();
        if(params.containsKey(ADD_ORCID_INTERNAL_SCOPES)) {
            scopes.add(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED.value());
        }
               
        //Add scopes to allow group read and update
        scopes.add(ScopePathType.GROUP_ID_RECORD_READ.value());
        scopes.add(ScopePathType.GROUP_ID_RECORD_UPDATE.value());
        
        //Add notifications scope
        scopes.add(ScopePathType.PREMIUM_NOTIFICATION.value());
        
        clientDetailsManager.populateClientDetailsEntity(clientId, memberEntity, name, description, website, clientSecret, clientType, scopes,
                clientResourceIds, clientAuthorizedGrantTypes, redirectUrisToAdd, clientGrantedAuthorities);
    }
    
    @Test
    public void testSetUpIsDone() {
        OrcidProfile existingProfile = orcidProfileManager.retrieveOrcidProfile(adminOrcidId);
        assertNotNull(existingProfile);
        assertNotNull(existingProfile.getOrcidIdentifier());
        assertEquals(adminOrcidId, existingProfile.getOrcidIdentifier().getPath());
        assertNotNull(existingProfile.getOrcidBio());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertEquals(adminUserName, existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        
        existingProfile = orcidProfileManager.retrieveOrcidProfile(user1OrcidId);
        assertNotNull(existingProfile);
        assertNotNull(existingProfile.getOrcidIdentifier());
        assertEquals(user1OrcidId, existingProfile.getOrcidIdentifier().getPath());
        assertNotNull(existingProfile.getOrcidBio());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertEquals(user1UserName, existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        
        existingProfile = orcidProfileManager.retrieveOrcidProfile(user2OrcidId);
        assertNotNull(existingProfile);
        assertNotNull(existingProfile.getOrcidIdentifier());
        assertEquals(user2OrcidId, existingProfile.getOrcidIdentifier().getPath());
        assertNotNull(existingProfile.getOrcidBio());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertEquals(user2UserName, existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        
        existingProfile = orcidProfileManager.retrieveOrcidProfile(member1Orcid);
        assertNotNull(existingProfile);
        assertNotNull(existingProfile.getOrcidIdentifier());
        assertEquals(member1Orcid, existingProfile.getOrcidIdentifier().getPath());
        assertNotNull(existingProfile.getOrcidBio());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertEquals(member1Email, existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        
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
                
        existingClient = clientDetailsManager.findByClientId(lockedMemberClient1ClientId);
        assertNotNull(existingClient);
        assertEquals(lockedMemberOrcid, existingClient.getGroupProfileId());
        assertNotNull(existingClient.getRegisteredRedirectUri());
        assertEquals(1, existingClient.getRegisteredRedirectUri().size());
        assertNotNull(existingClient.getRegisteredRedirectUri().iterator());
        assertTrue(existingClient.getRegisteredRedirectUri().iterator().hasNext());
        assertEquals(lockedMemberClient1RedirectUri, existingClient.getRegisteredRedirectUri().iterator().next());
    }
    
    //------------------------------------------------------------------------------
    
    /**
     * Set up default addresses 
     * Please see tests:
     *  AddressTest.testGetAddressWithMembersAPI
     * */
    public void setUpAddresses(String orcid) {
        Address a1 = new Address();
        a1.setCountry(new Country(Iso3166Country.US));
        a1.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        a1.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        a1.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        addressManager.createAddress(orcid, a1);
    }
    
    /**
     * Set up default other names
     * Please see tests: 
     *  OtherNamesTest.testGetOtherNamesWihtMembersAPI
     * */
    public void setUpOtherNames(String orcid) {
        OtherName o1 = new OtherName();
        o1.setContent("other-name-1");
        o1.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        o1.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));        
        o1.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        otherNameManager.createOtherName(orcid, o1);
        
        OtherName o2 = new OtherName();
        o2.setContent("other-name-2");
        o2.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        o2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        o2.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        otherNameManager.createOtherName(orcid, o2);
    }
    
    /**
     * Set up default keywords
     * Please see tests:
     *  KeywordsTest.testGetKeywordsWihtMembersAPI
     * */
    public void setUpKeywords(String orcid) {
        Keyword k1 = new Keyword();
        k1.setContent("keyword-1");
        k1.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        k1.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        k1.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        profileKeywordManager.createKeyword(orcid, k1);
        
        Keyword k2 = new Keyword();
        k2.setContent("keyword-2");
        k2.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        k2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        k2.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        profileKeywordManager.createKeyword(orcid, k2);
    }
    
    /**
     * Set up defaut email info
     * Please see tests: 
     *  EmailTest.testGetWithMembersAPI
     * */
    public void setUpEmails(String orcid) {
        Email email = new Email();
        email.setValue("limited@test.orcid.org");
        email.setVisibility(Visibility.LIMITED);
        email.setCurrent(false);
        email.setPrimary(false);
        email.setVerified(true);
        email.setSource(orcid);
        emailManager.addEmail(orcid, email);
    }
    
    /**
     * Set up external identifiers
     * Please see tests: 
     *  ExternalIdentifiersTest.testGetExternalIdentifiersWihtMembersAPI
     *  ExternalIdentifiersTest.testCreateGetUpdateAndDeleteExternalIdentifier
     *  ExternalIdentifiersTest.testGetExternalIdentifiersWihtPublicAPI
     * */
    public void setUpExternalIdentifiers(String orcid) {
        PersonExternalIdentifier e1 = new PersonExternalIdentifier();
        e1.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        e1.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        e1.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
        e1.setUrl(new Url("http://ext-id/A-0001"));
        e1.setValue("A-0001");
        e1.setSource(new Source(client1ClientId));  
        e1.setType("A-0001");
        externalIdentifierManager.createExternalIdentifier(orcid, e1);
        
        PersonExternalIdentifier e2 = new PersonExternalIdentifier();
        e2.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        e2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis())));
        e2.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.LIMITED);
        e2.setUrl(new Url("http://ext-id/A-0002"));
        e2.setValue("A-0002");
        e2.setSource(new Source(client1ClientId));
        e2.setType("A-0002");
        externalIdentifierManager.createExternalIdentifier(orcid, e2);
    }   
    
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
