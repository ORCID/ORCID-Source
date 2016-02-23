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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common_rc2.CreditName;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.FamilyName;
import org.orcid.jaxb.model.record_rc2.GivenNames;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
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
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String ORCID = "orcid";
    private static final String GIVEN_NAMES = "givenNames";
    private static final String FAMILY_NAMES = "familyNames";
    private static final String CREDIT_NAME = "creditName";
    private static final String BIO = "bio";
    private static final String ORCID_TYPE = "orcidType";
    private static final String MEMBER_TYPE = "memberType";

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

    // Client # 2
    @Value("${org.orcid.web.testClient2.clientId}")
    protected String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    protected String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;
    @Value("${org.orcid.web.testClient2.name}")
    protected String client2Name;

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
    
    @Before
    public void before() throws Exception {
        Map<String, String> adminParams = getUserParams(adminOrcidId);
        OrcidProfile adminProfile = orcidProfileManager.retrieveOrcidProfile(adminParams.get(ORCID));
        if (adminProfile == null) {
            createUser(adminParams);
        } else {
            clearRegistry(adminProfile, adminParams);
        }

        Map<String, String> user1Params = getUserParams(user1OrcidId);
        OrcidProfile user1Profile = orcidProfileManager.retrieveOrcidProfile(user1Params.get(ORCID));
        if (user1Profile == null) {
            createUser(user1Params);
        } else {
            clearRegistry(user1Profile, user1Params);
        }
        
        Map<String, String> user2Params = getUserParams(user2OrcidId);
        OrcidProfile user2Profile = orcidProfileManager.retrieveOrcidProfile(user2Params.get(ORCID));
        if(user2Profile == null) {
            createUser(user2Params);
        } else {
            clearRegistry(user2Profile, user2Params);
        }
        
        Map<String, String> member1Params = getUserParams(member1Orcid);
        OrcidProfile member1Profile = orcidProfileManager.retrieveOrcidProfile(member1Params.get(ORCID));
        if(member1Profile == null) {
            createUser(member1Params);
        } else {
            clearRegistry(member1Profile, member1Params);
        }
        
        Map<String, String> lockedMemberParams = getUserParams(lockedMemberOrcid);
        OrcidProfile lockedMemberProfile = orcidProfileManager.retrieveOrcidProfile(lockedMemberParams.get(ORCID));
        if(lockedMemberProfile == null) {
            createUser(lockedMemberParams);
        } else {
            clearRegistry(lockedMemberProfile, lockedMemberParams);
        }        
    }

    private Map<String, String> getUserParams(String userOrcid) {
        Map<String, String> params = new HashMap<String, String>();
        if (userOrcid.equals(adminOrcidId)) {
            params.put(EMAIL, adminUserName);
            params.put(PASSWORD, adminPassword);
            params.put(ORCID, adminOrcidId);
            params.put(GIVEN_NAMES, adminGivenName);
            params.put(FAMILY_NAMES, adminFamilyNames);
            params.put(CREDIT_NAME, adminCreditName);
            params.put(BIO, adminBio);
            params.put(ORCID_TYPE, OrcidType.ADMIN.value());
        } else if (userOrcid.equals(user1OrcidId)) {
            params.put(EMAIL, user1UserName);
            params.put(PASSWORD, user1Password);
            params.put(ORCID, user1OrcidId);
            params.put(GIVEN_NAMES, user1GivenName);
            params.put(FAMILY_NAMES, user1FamilyNames);
            params.put(CREDIT_NAME, user1CreditName);
            params.put(BIO, user1Bio);
            params.put(ORCID_TYPE, OrcidType.USER.value());
        } else if (userOrcid.equals(user2OrcidId)) {
            params.put(EMAIL, user2UserName);
            params.put(PASSWORD, user2Password);
            params.put(ORCID, user2OrcidId);
            params.put(GIVEN_NAMES, user2GivenName);
            params.put(FAMILY_NAMES, user2FamilyNames);
            params.put(CREDIT_NAME, user2CreditName);
            params.put(BIO, user2Bio);
            params.put(ORCID_TYPE, OrcidType.USER.value());
        } else if (userOrcid.equals(member1Orcid)) {
            params.put(EMAIL, member1Email);
            params.put(PASSWORD, member1Password);
            params.put(ORCID, member1Orcid);
            params.put(CREDIT_NAME, member1Name);
            params.put(ORCID_TYPE, OrcidType.GROUP.value());
            params.put(MEMBER_TYPE, member1Type);
        } else if (userOrcid.equals(lockedMemberOrcid)) {
            params.put(EMAIL, lockedMemberEmail);
            params.put(PASSWORD, lockedMemberPassword);
            params.put(ORCID, lockedMemberOrcid);
            params.put(CREDIT_NAME, lockedMemberName);
            params.put(ORCID_TYPE, OrcidType.GROUP.value());
            params.put(MEMBER_TYPE, lockedMemberType);
        } else {
            throw new ApplicationException("Unable to find params for orcid: " + userOrcid);
        }
        return params;
    }

    private void createUser(Map<String, String> params) throws Exception {
        // Create it
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier(params.get(ORCID)));
        orcidProfile.setType(OrcidType.fromValue(params.get(ORCID_TYPE)));
        if(params.get(MEMBER_TYPE) != null) {
            orcidProfile.setGroupType(MemberType.fromValue(params.get(MEMBER_TYPE)));
        }
        orcidProfile.setPassword(params.get(PASSWORD));
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

            researcherUrlManager.updateResearcherUrls(orcid, new ResearcherUrls(), org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.RESEARCHER_URLS_DEFAULT.getVisibility().value()));
            
            // Remove external ids
            ExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(orcid, System.currentTimeMillis());

            if (extIds != null && extIds.getExternalIdentifier() != null && !extIds.getExternalIdentifier().isEmpty()) {
                for (ExternalIdentifier extId : extIds.getExternalIdentifier()) {
                    externalIdentifierManager.deleteExternalIdentifier(orcid, extId.getPutCode(), false);
                }
            }

            // Remove addresses
            addressManager.updateAddresses(orcid, new Addresses(),
                    org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.COUNTRY_DEFAULT.getVisibility().value()));

            // Remove emails
            Emails emails = emailManager.getEmails(orcid);
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
    
    @Test
    public void testSetupIsDone() {
        OrcidProfile existingProfile = orcidProfileManager.retrieveOrcidProfile(adminOrcidId);
        assertNotNull(existingProfile);
        assertNotNull(existingProfile.getOrcidIdentifier());
        assertEquals(adminOrcidId, existingProfile.getOrcidIdentifier().getPath());
        assertNotNull(existingProfile.getOrcidBio());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails());
        assertNotNull(existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertEquals(adminUserName, existingProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());        
    }   
}
