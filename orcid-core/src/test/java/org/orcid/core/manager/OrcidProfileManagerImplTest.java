package org.orcid.core.manager;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.manager.impl.OrcidProfileManagerImpl;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.DeveloperToolsEnabled;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdUrl;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.SubjectEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Will Simpson
 */
public class OrcidProfileManagerImplTest extends OrcidProfileManagerBaseTest {

    protected static final String APPLICATION_ORCID = "2222-2222-2222-2228";
    
    protected static final String APPLICATION_ORCID_2 = "2222-2222-2222-2229";    

    protected static final String DELEGATE_ORCID = "1111-1111-1111-1115";

    protected static final String TEST_ORCID = "4444-4444-4444-0001";

    protected static final String TEST_ORCID_WITH_WORKS = "4444-4444-4444-4443";

    private int currentWorkId = 0;
    
    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private GenericDao<SubjectEntity, String> subjectDao;

    @Resource
    private WorkDao workDao;

    @Resource(name = "securityQuestionDao")
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Mock
    private NotificationManager notificationManager;
    
    @Resource
    private ProfileFundingDao profileFundingDao;
    
    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private SourceManager sourceManager;
    
    @Mock
    private SourceManager mockSourceManager;
    
    @Mock
    private SourceManager anotherMockSourceManager;
    
    @Mock
    private EmailFrequencyManager mockEmailFrequencyManager;
    
    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;
    
    @Before
    @Transactional
    @Rollback
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        Map<String, String> frequenciesMap = new HashMap<String, String>();
        frequenciesMap.put(EmailFrequencyManager.ADMINISTRATIVE_CHANGE_NOTIFICATIONS, "0.0");
        frequenciesMap.put(EmailFrequencyManager.CHANGE_NOTIFICATIONS, "0.0");
        frequenciesMap.put(EmailFrequencyManager.MEMBER_UPDATE_REQUESTS, "0.0");
        frequenciesMap.put(EmailFrequencyManager.QUARTERLY_TIPS, "true");
        when(mockEmailFrequencyManager.getEmailFrequency(anyString())).thenReturn(frequenciesMap);

        TargetProxyHelper.injectIntoProxy(jpa2JaxbAdapter, "emailFrequencyManager", mockEmailFrequencyManager);
        
        OrcidProfileManagerImpl orcidProfileManagerImpl = getTargetObject(orcidProfileManager, OrcidProfileManagerImpl.class);
        orcidProfileManagerImpl.setNotificationManager(notificationManager);

        if (profileDao.find(TEST_ORCID) != null) {
            profileDao.remove(TEST_ORCID);
        }
        subjectDao.merge(new SubjectEntity("Computer Science"));
        subjectDao.merge(new SubjectEntity("Dance"));

        OrcidProfile delegateProfile = new OrcidProfile();
        delegateProfile.setOrcidIdentifier(DELEGATE_ORCID);
        OrcidBio delegateBio = new OrcidBio();
        delegateProfile.setOrcidBio(delegateBio);
        PersonalDetails delegatePersonalDetails = new PersonalDetails();
        delegateBio.setPersonalDetails(delegatePersonalDetails);
        CreditName delegateCreditName = new CreditName("H. Shearer");
        delegateCreditName.setVisibility(Visibility.PUBLIC);
        delegatePersonalDetails.setCreditName(delegateCreditName);
        orcidProfileManager.createOrcidProfile(delegateProfile, false, false);
        
        OrcidProfile applicationProfile = new OrcidProfile();
        applicationProfile.setOrcidIdentifier(APPLICATION_ORCID);
        OrcidBio applicationBio = new OrcidBio();
        applicationProfile.setOrcidBio(applicationBio);
        PersonalDetails applicationPersonalDetails = new PersonalDetails();
        applicationBio.setPersonalDetails(applicationPersonalDetails);
        applicationPersonalDetails.setCreditName(new CreditName("Brown University"));
        orcidProfileManager.createOrcidProfile(applicationProfile, false, false);
        
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setId(applicationProfile.getOrcidIdentifier().getPath());
        ProfileEntity applicationProfileEntity = profileDao.find(applicationProfile.getOrcidIdentifier().getPath());
        profileDao.refresh(applicationProfileEntity);
        clientDetails.setGroupProfileId(applicationProfileEntity.getId());
        clientDetailsManager.merge(clientDetails);

        ClientDetailsEntity clientDetails2 = new ClientDetailsEntity();
        clientDetails2.setId(APPLICATION_ORCID_2);
        clientDetails.setGroupProfileId(applicationProfileEntity.getId());
        clientDetailsManager.merge(clientDetails2);
        
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setTokenValue("1234");
        token.setClientDetailsId(clientDetails.getId());
        token.setProfile(profileDao.find(delegateProfile.getOrcidIdentifier().getPath()));
        token.setScope(StringUtils.join(new String[] { ScopePathType.ORCID_BIO_READ_LIMITED.value(), ScopePathType.ORCID_BIO_UPDATE.value() }, " "));
        SortedSet<OrcidOauth2TokenDetail> tokens = new TreeSet<>();
        tokens.add(token);
        ProfileEntity delegateProfileEntity = profileDao.find(delegateProfile.getOrcidIdentifier().getPath());
        delegateProfileEntity.setTokenDetails(tokens);
        profileDao.merge(delegateProfileEntity);

        SecurityQuestionEntity existingSecurityQuestionEntity = securityQuestionDao.find(3);
        if (existingSecurityQuestionEntity == null) {
            SecurityQuestionEntity securityQuestionEntity = new SecurityQuestionEntity();
            securityQuestionEntity.setId(3);
            securityQuestionEntity.setQuestion("What?");
            securityQuestionDao.persist(securityQuestionEntity);
        }
        
        orcidProfileManager.setCompareWorksUsingScopusWay(true);
    
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", mockSourceManager);
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceClient(clientDetails);
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);    
        
        SourceEntity sourceEntity2 = new SourceEntity();
        sourceEntity2.setSourceClient(clientDetails2);
        when(anotherMockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity2);    
    }

    @After
    public void after() {
        profileDao.remove(DELEGATE_ORCID);
        profileDao.remove(APPLICATION_ORCID);
        orcidProfileManager.clearOrcidProfileCache();
        TargetProxyHelper.injectIntoProxy(jaxb2JpaAdapter, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(orcidProfileManager, "sourceManager", sourceManager);        
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfile() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);
        String originalPutCode = profile1.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        OrcidProfile profile2 = createBasicProfile();
        profile2.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).setPutCode(originalPutCode);
        profile2 = orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        String resultPutCode = resultProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().get(0).getPutCode();

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("Put code should not change", originalPutCode, resultPutCode);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileButRemoveActivities() {
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);

        OrcidProfile profile2 = createBasicProfile();
        profile2.setOrcidActivities(null);
        profile2 = orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertNull("There should be no activities", resultProfile.getOrcidActivities());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWhenTokenPresent() {
        OrcidProfile profile1 = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(profile1);
//		Applications are not linked with OrcidProfile object anymore.
//        assertNotNull(profile1.getOrcidBio().getApplications());
//        assertEquals(1, profile1.getOrcidBio().getApplications().getApplicationSummary().size());

        OrcidProfile profile2 = createBasicProfile();
        profile2.setOrcidIdentifier(DELEGATE_ORCID);

        orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(DELEGATE_ORCID);
        assertNotNull(resultProfile);
        //Applications are not linked with OrcidProfile object anymore.
        //assertNotNull(resultProfile.getOrcidBio().getApplications());
        //assertEquals(1, resultProfile.getOrcidBio().getApplications().getApplicationSummary().size());
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWithEmailVerified() {
        OrcidProfile profile = createBasicProfile();
        profile = orcidProfileManager.createOrcidProfile(profile, false, false);
        assertNotNull(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail());
        assertFalse(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified());

        profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().setVerified(true);

        orcidProfileManager.updateOrcidProfile(profile);

        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNotNull(resultProfile);
        assertEquals("Will", resultProfile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals(1, resultProfile.retrieveOrcidWorks().getOrcidWork().size());
        assertEquals(1, resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://www.wjrs.co.uk", resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertTrue(profile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileDefaultVisibilityForItemsAndUpdate() {
        OrcidProfile profile = createBasicProfile();
        
        Keyword k = new Keyword("word",null);
        Keywords kk = new Keywords();
        kk.getKeyword().add(k);
        
        ResearcherUrl r = new ResearcherUrl(new Url("http://whatever.com"),null);
        ResearcherUrls rr = new ResearcherUrls();
        rr.getResearcherUrl().add(r);
        
        ExternalIdentifier i = new ExternalIdentifier(null);
        i.setExternalIdReference(new ExternalIdReference("ref"));
        i.setExternalIdCommonName(new ExternalIdCommonName("cn"));
        ExternalIdentifiers ii = new ExternalIdentifiers();
        ii.getExternalIdentifier().add(i);
        
        OtherNames oo = new OtherNames();
        oo.addOtherName("other", null);
        
        profile.getOrcidBio().setKeywords(kk);
        profile.getOrcidBio().setResearcherUrls(rr);
        profile.getOrcidBio().setExternalIdentifiers(ii);
        profile.getOrcidBio().getPersonalDetails().setOtherNames(oo);
        
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);

        assertEquals("word",profile.getOrcidBio().getKeywords().getKeyword().iterator().next().getContent());
        assertEquals(Visibility.PRIVATE,profile.getOrcidBio().getKeywords().getKeyword().iterator().next().getVisibility());
        assertEquals(new Url("http://whatever.com"),profile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getUrl());
        assertEquals(Visibility.PRIVATE,profile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getVisibility());
        assertEquals("cn",profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getExternalIdCommonName().getContent());
        assertEquals(Visibility.PRIVATE,profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getVisibility());
        assertEquals("other",profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getContent());
        assertEquals(Visibility.PRIVATE,profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getVisibility());
        
        profile.getOrcidBio().getKeywords().setVisibility(Visibility.PUBLIC);
        profile.getOrcidBio().getKeywords().getKeyword().get(0).setContent("kk - updated");
        profile.getOrcidBio().getResearcherUrls().setVisibility(Visibility.PUBLIC);
        profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().setValue("http://whatever.com/updated");
        profile.getOrcidBio().getExternalIdentifiers().setVisibility(Visibility.PUBLIC);        
        
        profile.getOrcidBio().getPersonalDetails().getOtherNames().setVisibility(Visibility.PUBLIC);
        profile = orcidProfileManager.updateOrcidProfile(profile);
        
        assertEquals("kk - updated",profile.getOrcidBio().getKeywords().getKeyword().iterator().next().getContent());
        assertEquals(Visibility.PUBLIC,profile.getOrcidBio().getKeywords().getKeyword().iterator().next().getVisibility());
        assertEquals(new Url("http://whatever.com/updated"),profile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getUrl());
        assertEquals(Visibility.PUBLIC,profile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getVisibility());
        assertEquals("cn",profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getVisibility());
        assertEquals("other",profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getContent());
        assertEquals(Visibility.PUBLIC,profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getVisibility());        
        
        OrcidProfile resultProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        
        assertEquals(new Url("http://whatever.com/updated"),resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getUrl());
        assertEquals(Visibility.PUBLIC,resultProfile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getVisibility());
        assertEquals("cn",resultProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,resultProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getVisibility());
        assertEquals("other",resultProfile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getContent());
        assertEquals(Visibility.PUBLIC,resultProfile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getVisibility());        

        Keyword kw = resultProfile.getOrcidBio().getKeywords().getKeyword().iterator().next();
        assertEquals("kk - updated",kw.getContent());
        assertEquals(Visibility.PUBLIC,kw.getVisibility());        

    }

    @Test
    @Transactional
    @Rollback(true)
    public void testDefaultVisibilityForItemsAppliedOnUpdate() {
        OrcidProfile profile = createBasicProfile();

        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.DIRECT);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile.setOrcidHistory(orcidHistory);
        
        Keyword k = new Keyword("word",null);
        Keywords kk = new Keywords();
        kk.getKeyword().add(k);
        kk.setVisibility(Visibility.LIMITED);
        
        ResearcherUrl r = new ResearcherUrl(new Url("http://whatever.com"),null);
        ResearcherUrls rr = new ResearcherUrls();
        rr.getResearcherUrl().add(r);
        rr.setVisibility(Visibility.LIMITED);
        
        ExternalIdentifier i = new ExternalIdentifier(null);
        i.setExternalIdReference(new ExternalIdReference("ref"));
        i.setExternalIdCommonName(new ExternalIdCommonName("cn"));
        ExternalIdentifiers ii = new ExternalIdentifiers();
        ii.getExternalIdentifier().add(i);
        ii.setVisibility(Visibility.LIMITED);
        
        OtherNames oo = new OtherNames();
        oo.addOtherName("other", null);
        oo.setVisibility(Visibility.LIMITED);
        
        profile.getOrcidBio().setKeywords(kk);
        profile.getOrcidBio().setResearcherUrls(rr);
        profile.getOrcidBio().setExternalIdentifiers(ii);
        profile.getOrcidBio().getPersonalDetails().setOtherNames(oo);        
        
        //Create the profile
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(true));
        preferences.setSendOrcidNews(new SendOrcidNews(true));
        //Default visibility for user will be LIMITED
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.LIMITED));
        preferences.setNotificationsEnabled(DefaultPreferences.NOTIFICATIONS_ENABLED);
        preferences.setSendEmailFrequencyDays(DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS);
        preferences.setSendMemberUpdateRequests(DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidInternal(internal);
        profile.getOrcidInternal().getPreferences().setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.LIMITED));

        //Claim the profile
        profile = orcidProfileManager.updateOrcidProfile(profile);
        
        //now attempt to alter privacy.  It should fail as record has been claimed.
        profile.getOrcidBio().getKeywords().setVisibility(Visibility.PUBLIC);
        profile.getOrcidBio().getResearcherUrls().setVisibility(Visibility.PUBLIC);
        profile.getOrcidBio().getExternalIdentifiers().setVisibility(Visibility.PUBLIC);
        profile.getOrcidBio().getPersonalDetails().getOtherNames().setVisibility(Visibility.PUBLIC);
        profile = orcidProfileManager.updateOrcidProfile(profile);
        
        assertEquals("word",profile.getOrcidBio().getKeywords().getKeyword().iterator().next().getContent());
        assertEquals(Visibility.LIMITED,profile.getOrcidBio().getKeywords().getKeyword().iterator().next().getVisibility());
        assertEquals(new Url("http://whatever.com"),profile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getUrl());
        assertEquals(Visibility.LIMITED,profile.getOrcidBio().getResearcherUrls().getResearcherUrl().iterator().next().getVisibility());
        assertEquals("cn",profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getExternalIdCommonName().getContent());
        assertEquals(Visibility.LIMITED,profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().iterator().next().getVisibility());
        assertEquals("other",profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getContent());
        assertEquals(Visibility.LIMITED,profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().iterator().next().getVisibility());        

    }      
    
    

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateProfileWithDupeWork() {
        OrcidProfile profile = createBasicProfile();
        OrcidProfile createdProfile = orcidProfileManager.createOrcidProfile(profile, false, false);
        List<OrcidWork> orcidWorkList = createdProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertEquals(1, orcidWorkList.size());

        orcidWorkList.add(createWork1());
        assertEquals(2, orcidWorkList.size());
        OrcidProfile updatedProfile = orcidProfileManager.updateOrcidProfile(createdProfile);
        List<OrcidWork> updatedOrcidWorkList = updatedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        assertEquals(1, updatedOrcidWorkList.size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePersonalInformationRemovesOrcidIndexFields() throws Exception {

        // re-use the createFull method but add some extra criteria so we can
        // use our specific orcidAllSolrFieldsPopulatedForSave matcher
        OrcidProfile profile = createFullOrcidProfile();
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherName().add(new OtherName("Stan",null));
        otherNames.getOtherName().add(new OtherName("Willis",null));

        profile.getOrcidBio().getPersonalDetails().setOtherNames(otherNames);

        OrcidWorks orcidWorks = new OrcidWorks();
        profile.setOrcidWorks(orcidWorks);
        WorkTitle singleWorkTitle = new WorkTitle();
        singleWorkTitle.setTitle(new Title("Single works"));
        singleWorkTitle.setSubtitle(new Subtitle("Single works"));
        OrcidWork orcidWork = createWork1(singleWorkTitle);
        // TODO JB some doi testing here?
        // orcidWork.getElectronicResourceNum().add(new
        // ElectronicResourceNum("10.1016/S0021-8502(00)90373-2",
        // ElectronicResourceNumType.DOI));

        orcidWorks.getOrcidWork().add(orcidWork);

        orcidProfileManager.createOrcidProfile(profile, false, false);
        // now negate all fields that form part of a solr query, leaving only
        // the orcid itself
        // we do this by passing through an orcid missing the fields from an
        // OrcidSolrDocument
        OrcidProfile negatedProfile = createBasicProfile();
        negatedProfile.getOrcidBio().getPersonalDetails().setFamilyName(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setGivenNames(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setCreditName(null);
        negatedProfile.getOrcidBio().getPersonalDetails().setOtherNames(null);
        negatedProfile.getOrcidActivities().setAffiliations(null);
        orcidProfileManager.updateOrcidBio(negatedProfile);
        assertEquals(IndexingStatus.PENDING, profileDao.find(TEST_ORCID).getIndexingStatus());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePasswordResultsInEncypytedProfile() {
        OrcidProfile basicProfile = createBasicProfile();
        OrcidProfile derivedProfile = orcidProfileManager.createOrcidProfile(basicProfile, false, false);
        assertTrue(encryptionManager.hashMatches("password", derivedProfile.getPassword()));
        assertEquals("random answer", encryptionManager.decryptForInternalUse(derivedProfile.getSecurityQuestionAnswer()));
        assertEquals("1234", encryptionManager.decryptForInternalUse(derivedProfile.getVerificationCode()));

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(derivedProfile.getOrcidIdentifier().getPath());
        assertTrue(encryptionManager.hashMatches("password", derivedProfile.getPassword()));
        assertEquals("random answer", retrievedProfile.getSecurityQuestionAnswer());
        assertEquals("1234", retrievedProfile.getVerificationCode());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testAffiliations() throws DatatypeConfigurationException {
        OrcidProfile profile1 = createBasicProfile();
        orcidProfileManager.createOrcidProfile(profile1, false, false);

        OrcidProfile profile2 = new OrcidProfile();
        profile2.setOrcidIdentifier(TEST_ORCID);
        OrcidBio orcidBio = new OrcidBio();
        orcidBio.setPersonalDetails(new PersonalDetails());

        OrcidActivities orcidActivities = new OrcidActivities();
        profile2.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);

        Affiliation affiliation1 = getAffiliation();
        Affiliation affiliation2 = getAffiliation();
        affiliation2.setType(AffiliationType.EDUCATION);
        affiliation2.getOrganization().setName("Past Institution 2");

        affiliations.getAffiliation().add(affiliation1);
        affiliations.getAffiliation().add(affiliation2);

        profile2.setOrcidBio(orcidBio);
        orcidProfileManager.addAffiliations(profile2);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);

        assertNotNull(retrievedProfile);
        assertEquals(2, retrievedProfile.getOrcidActivities().getAffiliations().getAffiliation().size());
        for (Affiliation affiliation : retrievedProfile.getOrcidActivities().getAffiliations().getAffiliation()) {
            assertNotNull(affiliation.getPutCode());
        }

        // Remove an affiliation
        profile2 = createFullOrcidProfile();
        affiliations.getAffiliation().clear();
        affiliations.getAffiliation().add(affiliation1);
        profile2.setOrcidActivities(orcidActivities);

        orcidProfileManager.updateOrcidProfile(profile2);

        OrcidProfile profile3 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertEquals(1, profile3.getOrcidActivities().getAffiliations().getAffiliation().size());

        assertEquals(IndexingStatus.PENDING, profileDao.find(TEST_ORCID).getIndexingStatus());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testOrgReuse() {
        OrcidProfile profile1 = createBasicProfile();
        OrcidHistory history = new OrcidHistory();
        history.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile1.setOrcidHistory(history);
        history.setClaimed(new Claimed(true));
        OrcidActivities orcidActivities = profile1.getOrcidActivities();
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation = new Affiliation();
        affiliations.getAffiliation().add(affiliation);
        Organization organization = new Organization();
        affiliation.setOrganization(organization);
        organization.setName("New College");
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);

        orcidProfileManager.createOrcidProfile(profile1, false, false);

        ProfileEntity profileEntity = profileDao.find(TEST_ORCID);
        assertEquals(1, profileEntity.getOrgAffiliationRelations().size());
        OrgEntity orgEntity = profileEntity.getOrgAffiliationRelations().iterator().next().getOrg();
        assertNotNull(orgEntity);

        // Now create another profile with the same affiliation and check that
        // the org is reused;

        String otherOrcid = "4444-4444-4444-4448";
        OrcidProfile profile2 = createBasicProfile();
        profile2.setOrcidIdentifier(otherOrcid);
        List<Email> emailList2 = profile2.getOrcidBio().getContactDetails().getEmail();
        emailList2.clear();
        emailList2.add(new Email("another@semantico.com"));
        profile2.getOrcidActivities().setAffiliations(affiliations);
        orcidProfileManager.createOrcidProfile(profile2, false, false);

        ProfileEntity profileEntity2 = profileDao.find(otherOrcid);
        assertEquals(1, profileEntity2.getOrgAffiliationRelations().size());
        OrgEntity orgEntity2 = profileEntity2.getOrgAffiliationRelations().iterator().next().getOrg();
        assertNotNull(orgEntity);

        assertEquals(orgEntity.getId(), orgEntity2.getId());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRevokeDelegate() {
        OrcidProfile profile1 = createBasicProfile();
        Delegation delegation = new Delegation();
        profile1.getOrcidBio().setDelegation(delegation);
        GivenPermissionTo givenPermissionTo = new GivenPermissionTo();
        delegation.setGivenPermissionTo(givenPermissionTo);
        DelegationDetails delegationDetails = new DelegationDetails();
        delegationDetails.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar("2011-03-14T02:34:16")));
        DelegateSummary profileSummary = new DelegateSummary(new OrcidIdentifier(DELEGATE_ORCID));
        delegationDetails.setDelegateSummary(profileSummary);
        givenPermissionTo.getDelegationDetails().add(delegationDetails);
        orcidProfileManager.createOrcidProfile(profile1, false, false);

        orcidProfileManager.revokeDelegate(TEST_ORCID, DELEGATE_ORCID);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        assertNull(retrievedProfile.getOrcidBio().getDelegation());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdatePasswordInformationLeavesSecurityQuestionsUnchanged() {
        OrcidProfile profile1 = createBasicProfile();
        assertEquals("password", profile1.getPassword());
        assertEquals("random answer", profile1.getSecurityQuestionAnswer());
        orcidProfileManager.createOrcidProfile(profile1, false, false);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        String hashedPasswordValue = retrievedProfile.getPassword();
        assertTrue("Should have hashed password", 108 == hashedPasswordValue.length() && !"password".equals(hashedPasswordValue));
        assertEquals("Should have security question", 3, retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertEquals("Should have decrypted security answer", "random answer", retrievedProfile.getSecurityQuestionAnswer());

        retrievedProfile.setPassword("A new password");

        orcidProfileManager.updatePasswordInformation(retrievedProfile);

        OrcidProfile updatedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        String updatedPassword = updatedProfile.getPassword();
        assertEquals("Password should be hashed", 108, updatedPassword.length());
        assertFalse("Password should have changed but was still: " + updatedPassword, hashedPasswordValue.equals(updatedPassword));
        assertEquals("Should have security question", 3, updatedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertEquals("Should have decrypted security answer", "random answer", updatedProfile.getSecurityQuestionAnswer());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testSecurityQuestionsUpdateLeavePasswordInformationUnchanged() {
        OrcidProfile profile1 = createBasicProfile();
        assertEquals("password", profile1.getPassword());
        assertEquals("random answer", profile1.getSecurityQuestionAnswer());
        orcidProfileManager.createOrcidProfile(profile1, false, false);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        String hashedPasswordValue = retrievedProfile.getPassword();
        assertTrue("Should have hashed password", 108 == hashedPasswordValue.length() && !"password".equals(hashedPasswordValue));
        assertEquals("Should have security question", 3, retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertTrue("Should have decrypted security answer", "random answer".equals(retrievedProfile.getSecurityQuestionAnswer()));

        retrievedProfile.setSecurityQuestionAnswer("A new random answer");

        orcidProfileManager.updateSecurityQuestionInformation(retrievedProfile);

        OrcidProfile updatedProfile = orcidProfileManager.retrieveOrcidProfile(profile1.getOrcidIdentifier().getPath());

        assertTrue("Password should not have changed", hashedPasswordValue.equals(updatedProfile.getPassword()));
        assertEquals("Should have security question", 3, updatedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue());
        assertEquals("A new random answer", updatedProfile.getSecurityQuestionAnswer());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testUpdateLastModifiedDate() throws InterruptedException {
        Date start = new Date();
        OrcidProfile profile1 = createBasicProfile();
        profile1 = orcidProfileManager.createOrcidProfile(profile1, false, false);
        Date profile1LastModified = profile1.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        assertNotNull(profile1LastModified);
        assertFalse(start.after(profile1LastModified));

        Thread.sleep(100);
        orcidProfileManager.updateLastModifiedDate(TEST_ORCID);

        OrcidProfile profile2 = orcidProfileManager.retrieveOrcidProfile(TEST_ORCID);
        Date profile2LastModified = profile2.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        assertTrue(profile2LastModified.getTime() > profile1LastModified.getTime());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRetrieveProfileWhenNonExistant() {
        OrcidProfile orcidProfile = orcidProfileManager.retrievePublicOrcidProfile("1234-5678-8765-4321");
        assertNull(orcidProfile);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testDuplicatedExternalIdentifiersThrowsException() {
        OrcidWork work2 = createWork2();
        OrcidWork work3 = createWork3();
        
        WorkExternalIdentifier sharedExternalIdentifier1 = new WorkExternalIdentifier();
        sharedExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        sharedExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("shared-doi1"));
        
        work2.getWorkExternalIdentifiers().getWorkExternalIdentifier().add(sharedExternalIdentifier1);
        work3.getWorkExternalIdentifiers().getWorkExternalIdentifier().add(sharedExternalIdentifier1);

        OrcidProfile profile = createBasicProfile();
        profile = orcidProfileManager.createOrcidProfile(profile, false, false);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidActivities());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks());
        assertNotNull(profile.getOrcidActivities().getOrcidWorks().getOrcidWork());
        assertEquals(1, profile.getOrcidActivities().getOrcidWorks().getOrcidWork().size());        

        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work2);
        profile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(work3);
                
        try {
            
            orcidProfileManager.addOrcidWorks(profile);
            fail("This should not pass since we add works with duplicated external identifiers");
        } catch(IllegalArgumentException iae) {
            assertEquals("Works \"Test Title # 2\" and \"Test Title # 3\" have the same external id \"shared-doi1\"", iae.getMessage());
        }                
    }
    
    @Test
    @Transactional
    public void testCheckWorkExternalIdentifiersAreNotDuplicated1() {
        List<OrcidWork> newOrcidWorksList = new ArrayList<OrcidWork>();
        List<OrcidWork> existingWorkList = new ArrayList<OrcidWork>();
        
        OrcidWork newWork1 = getOrcidWork("work1", false);
        OrcidWork newWork2 = getOrcidWork("work2", false);
        OrcidWork existingWork4 = getOrcidWork("work4", true);
        OrcidWork existingWork5 = getOrcidWork("work5", true);
        
        //Check no duplicates at all
        newOrcidWorksList.add(newWork1);
        newOrcidWorksList.add(newWork2);
        existingWorkList.add(existingWork4);
        existingWorkList.add(existingWork5);
        try {
            orcidProfileManager.checkWorkExternalIdentifiersAreNotDuplicated(newOrcidWorksList, existingWorkList);
        } catch(Exception e){
            fail();
        }
    }
    
    @Test
    @Transactional
    public void testCheckWorkExternalIdentifiersAreNotDuplicated2() {
        List<OrcidWork> newOrcidWorksList = new ArrayList<OrcidWork>();
        List<OrcidWork> existingWorkList = new ArrayList<OrcidWork>();
        
        OrcidWork newWork1 = getOrcidWork("work1", false);
        OrcidWork newWork2 = getOrcidWork("work2", false);
        OrcidWork newWork3 = getOrcidWork("work3", false);
        
        OrcidWork existingWork3 = getOrcidWork("work3", true);
        OrcidWork existingWork4 = getOrcidWork("work4", true);
        OrcidWork existingWork5 = getOrcidWork("work5", true);
        
        //Check duplicates in new works and existing works
        newOrcidWorksList.add(newWork1);
        newOrcidWorksList.add(newWork2);
        existingWorkList.add(existingWork4);
        existingWorkList.add(existingWork5);
        newOrcidWorksList.add(newWork3);
        existingWorkList.add(existingWork3);
        try {
            orcidProfileManager.checkWorkExternalIdentifiersAreNotDuplicated(newOrcidWorksList, existingWorkList);
            fail();
        } catch(IllegalArgumentException iae) {
            assertEquals("Works \"Title for work3->updated\" and \"Title for work3\"(put-code '" + existingWork3.getPutCode() + "') have the same external id \"doi-work3\"", iae.getMessage());
        }
    }
    
    @Test
    @Transactional
    public void testCheckWorkExternalIdentifiersAreNotDuplicated3() {
        List<OrcidWork> newOrcidWorksList = new ArrayList<OrcidWork>();
        List<OrcidWork> existingWorkList = new ArrayList<OrcidWork>();
        
        OrcidWork newWork1 = getOrcidWork("work1", false);
        OrcidWork newWork2 = getOrcidWork("work2", false);
        OrcidWork newWork3 = getOrcidWork("work3", false);
        OrcidWork newWork3_fixed = getOrcidWork("work3", false);
        WorkTitle updatedTitle = new WorkTitle();
        updatedTitle.setTitle(new Title("updated title"));
        newWork3_fixed.setWorkTitle(updatedTitle);
        
        //Check #3: Check duplicates in new works
        newOrcidWorksList.add(newWork1);
        newOrcidWorksList.add(newWork2);
        newOrcidWorksList.add(newWork3);
        newOrcidWorksList.add(newWork3_fixed);
        try {
            orcidProfileManager.checkWorkExternalIdentifiersAreNotDuplicated(newOrcidWorksList, existingWorkList);
            fail();
        } catch(IllegalArgumentException iae) {
            assertEquals("Works \"Title for work3->updated\" and \"updated title\" have the same external id \"doi-work3\"", iae.getMessage());
        }
    }
    
    @Test
    @Transactional
    public void testMemberCreateOrcidProfileWithoutVisibilityOnBioSoDefaultVisibilityIsSetOnBioElements() {
        OrcidProfile profile = createBasicProfile();
        String orcidIdentifier = null;
        profile.setOrcidIdentifier(orcidIdentifier);
        setBio(profile, null);        
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidIdentifier());
        assertNotNull(profile.getOrcidIdentifier().getPath());
        OrcidProfile newProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(newProfile);
        assertNotNull(newProfile.getOrcidBio());
        OrcidBio bio = newProfile.getOrcidBio();
        assertEquals(Visibility.PRIVATE, bio.getBiography().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getKeywords().getVisibility());        
        assertEquals(Visibility.PRIVATE, bio.getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getResearcherUrls().getVisibility());        
    }
    
    @Test
    @Transactional
    public void testMemberCreateOrcidProfileWithVisibilityOnBioSoThatVisibilityIsSetOnBioElements() {
        //Test setting it LIMITED
        OrcidProfile profile = createBasicProfile();
        String orcidIdentifier = null;
        profile.setOrcidIdentifier(orcidIdentifier);
        setBio(profile, Visibility.LIMITED);        
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidIdentifier());
        assertNotNull(profile.getOrcidIdentifier().getPath());
        OrcidProfile newProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(newProfile);
        assertNotNull(newProfile.getOrcidBio());
        OrcidBio bio = newProfile.getOrcidBio();
        assertEquals(Visibility.LIMITED, bio.getBiography().getVisibility());
        assertEquals(Visibility.LIMITED, bio.getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Visibility.LIMITED, bio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.LIMITED, bio.getKeywords().getVisibility());        
        assertEquals(Visibility.LIMITED, bio.getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(Visibility.LIMITED, bio.getResearcherUrls().getVisibility());
        
        //Test again setting everything to PUBLIC
        profile = createBasicProfile();
        orcidIdentifier = null;
        profile.setOrcidIdentifier(orcidIdentifier);
        setBio(profile, Visibility.PUBLIC);        
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidIdentifier());
        assertNotNull(profile.getOrcidIdentifier().getPath());
        newProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(newProfile);
        assertNotNull(newProfile.getOrcidBio());
        bio = newProfile.getOrcidBio();
        assertEquals(Visibility.PUBLIC, bio.getBiography().getVisibility());
        assertEquals(Visibility.PUBLIC, bio.getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Visibility.PUBLIC, bio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PUBLIC, bio.getKeywords().getVisibility());        
        assertEquals(Visibility.PUBLIC, bio.getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(Visibility.PUBLIC, bio.getResearcherUrls().getVisibility());
        
        //Test again setting everything to PRIVATE
        profile = createBasicProfile();
        orcidIdentifier = null;
        profile.setOrcidIdentifier(orcidIdentifier);
        setBio(profile, Visibility.PRIVATE);        
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidIdentifier());
        assertNotNull(profile.getOrcidIdentifier().getPath());
        newProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(newProfile);
        assertNotNull(newProfile.getOrcidBio());
        bio = newProfile.getOrcidBio();
        assertEquals(Visibility.PRIVATE, bio.getBiography().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getKeywords().getVisibility());        
        assertEquals(Visibility.PRIVATE, bio.getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getResearcherUrls().getVisibility());
        
        //Test again setting different visibility values to each bio section
        profile = createBasicProfile();
        orcidIdentifier = null;
        profile.setOrcidIdentifier(orcidIdentifier);
        setBio(profile, Visibility.PRIVATE);        
        
        profile.getOrcidBio().getBiography().setVisibility(Visibility.PUBLIC);
        profile.getOrcidBio().getContactDetails().getAddress().getCountry().setVisibility(Visibility.LIMITED);
        profile.getOrcidBio().getExternalIdentifiers().setVisibility(Visibility.PUBLIC);
        profile.getOrcidBio().getKeywords().setVisibility(Visibility.PRIVATE);
        profile.getOrcidBio().getPersonalDetails().getOtherNames().setVisibility(Visibility.LIMITED);
        profile.getOrcidBio().getResearcherUrls().setVisibility(Visibility.PRIVATE);               
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidIdentifier());
        assertNotNull(profile.getOrcidIdentifier().getPath());
        newProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(newProfile);
        assertNotNull(newProfile.getOrcidBio());
        bio = newProfile.getOrcidBio();
        assertEquals(Visibility.PUBLIC, bio.getBiography().getVisibility());
        assertEquals(Visibility.PUBLIC, bio.getExternalIdentifiers().getVisibility());
        assertEquals(Visibility.LIMITED, bio.getContactDetails().getAddress().getCountry().getVisibility());                
        assertEquals(Visibility.LIMITED, bio.getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getKeywords().getVisibility());
        assertEquals(Visibility.PRIVATE, bio.getResearcherUrls().getVisibility());
                
    }           
    
    @Test
    @Transactional
    public void testUpdateOrcidBioKeepTheUserVisibility() {
        OrcidProfile profile = createBasicProfile();
        String orcidIdentifier = null;
        profile.setOrcidIdentifier(orcidIdentifier);
        setBio(profile, Visibility.LIMITED);        
        
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.DIRECT);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile.setOrcidHistory(orcidHistory);
        
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(true));
        preferences.setSendOrcidNews(new SendOrcidNews(true));
        //Default visibility for user will be LIMITED
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.LIMITED));
        preferences.setNotificationsEnabled(DefaultPreferences.NOTIFICATIONS_ENABLED);
        preferences.setSendEmailFrequencyDays(DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS);
        preferences.setSendMemberUpdateRequests(DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidInternal(internal);
        
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        
        //Update it setting it to PUBLIC and check
        profile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(profile);
        assertNotNull(profile.getOrcidBio());
        
        OrcidBio bioToUpdate = profile.getOrcidBio();
        assertEquals(Visibility.LIMITED, bioToUpdate.getBiography().getVisibility());
        assertEquals("This is my biography", bioToUpdate.getBiography().getContent());
        assertEquals(Visibility.LIMITED, bioToUpdate.getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Iso3166Country.US , bioToUpdate.getContactDetails().getAddress().getCountry().getValue());               
        assertEquals(Visibility.LIMITED, bioToUpdate.getExternalIdentifiers().getVisibility());
        assertEquals(1, bioToUpdate.getExternalIdentifiers().getExternalIdentifier().size());        
        assertEquals(Visibility.LIMITED, bioToUpdate.getKeywords().getVisibility());
        assertEquals(1, bioToUpdate.getKeywords().getKeyword().size());
        assertEquals(Visibility.LIMITED, bioToUpdate.getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(1, bioToUpdate.getPersonalDetails().getOtherNames().getOtherName().size());
        assertEquals(Visibility.LIMITED, bioToUpdate.getResearcherUrls().getVisibility());
        assertEquals(1, bioToUpdate.getResearcherUrls().getResearcherUrl().size());
        
        //Update bio
        bioToUpdate.getBiography().setContent("Updated biography");
        bioToUpdate.getBiography().setVisibility(Visibility.PRIVATE);
        
        //Update address
        bioToUpdate.getContactDetails().getAddress().getCountry().setValue(Iso3166Country.CR);
        bioToUpdate.getContactDetails().getAddress().getCountry().setVisibility(Visibility.PRIVATE);
        
        //Update external identifiers
        ExternalIdentifier extId = new ExternalIdentifier();
        extId.setExternalIdCommonName(new ExternalIdCommonName("common-name-2"));
        extId.setExternalIdReference(new ExternalIdReference("ext-id-reference-2"));
        extId.setExternalIdUrl(new ExternalIdUrl("http://orcid.org/ext-id/2"));
        extId.setVisibility(Visibility.PRIVATE);
        bioToUpdate.getExternalIdentifiers().setVisibility(Visibility.PRIVATE);
        bioToUpdate.getExternalIdentifiers().getExternalIdentifier().add(extId);

        //Update keywords
        Keyword k = new Keyword();
        k.setContent("keyword-2");
        k.setVisibility(Visibility.PRIVATE);
        bioToUpdate.getKeywords().getKeyword().add(k);
        bioToUpdate.getKeywords().setVisibility(Visibility.PRIVATE);
        
        //Update researcher urls
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://orcid.org/researcher-url-2"));
        rUrl.setUrlName(new UrlName("url-name-2"));
        rUrl.setVisibility(Visibility.PRIVATE);
        bioToUpdate.getResearcherUrls().getResearcherUrl().add(rUrl);
        bioToUpdate.getResearcherUrls().setVisibility(Visibility.PRIVATE);
        
        //Update other names
        OtherName o = new OtherName();
        o.setContent("other-name-2");
        o.setVisibility(Visibility.PRIVATE);
        bioToUpdate.getPersonalDetails().getOtherNames().getOtherName().add(o);
        bioToUpdate.getPersonalDetails().getOtherNames().setVisibility(Visibility.PRIVATE);
        
        //Update the biography
        orcidProfileManager.updateOrcidBio(profile);
        
        //Get the record again and check that visibilities where not updated 
        OrcidProfile updatedProfile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(updatedProfile);
        assertNotNull(updatedProfile.getOrcidBio());
        OrcidBio updatedBio = updatedProfile.getOrcidBio();
        assertEquals(Visibility.LIMITED, updatedBio.getBiography().getVisibility());
        assertEquals("Updated biography", updatedBio.getBiography().getContent());
        assertEquals(Visibility.LIMITED, updatedBio.getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Iso3166Country.US, updatedBio.getContactDetails().getAddress().getCountry().getValue());               
        assertEquals(Visibility.LIMITED, updatedBio.getExternalIdentifiers().getVisibility());
        assertEquals(2, updatedBio.getExternalIdentifiers().getExternalIdentifier().size());        
        assertEquals(Visibility.LIMITED, updatedBio.getKeywords().getVisibility());
        assertEquals(2, updatedBio.getKeywords().getKeyword().size());
        assertEquals(Visibility.LIMITED, updatedBio.getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(2, updatedBio.getPersonalDetails().getOtherNames().getOtherName().size());
        assertEquals(Visibility.LIMITED, updatedBio.getResearcherUrls().getVisibility());
        assertEquals(2, updatedBio.getResearcherUrls().getResearcherUrl().size());
    }
        
    @Test
    @Transactional
    @Rollback(true)
    public void testPrimaryAddressDontChangeOnClaimedRecords() {
        OrcidProfile profile = createBasicProfile();
        String orcidIdentifier = null;
        profile.setOrcidIdentifier(orcidIdentifier);
        setBio(profile, Visibility.PUBLIC);   
        String email = profile.getOrcidBio().getContactDetails().getEmail().get(0).getValue();
        profile = orcidProfileManager.createOrcidProfile(profile, true, false);
        assertNotNull(profile);
        assertNotNull(profile.getOrcidIdentifier());
        assertFalse(PojoUtil.isEmpty(profile.getOrcidIdentifier().getPath()));
        profile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(profile);
        assertNotNull(profile.getOrcidBio());
        assertNotNull(profile.getOrcidBio().getBiography());
        assertEquals("This is my biography", profile.getOrcidBio().getBiography().getContent());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getBiography().getVisibility());
        assertNotNull(profile.getOrcidBio().getContactDetails());
        assertNotNull(profile.getOrcidBio().getContactDetails().getAddress());
        assertNotNull(profile.getOrcidBio().getContactDetails().getAddress().getCountry());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Iso3166Country.US, profile.getOrcidBio().getContactDetails().getAddress().getCountry().getValue());
        assertNotNull(profile.getOrcidBio().getContactDetails().getEmail());
        assertEquals(1, profile.getOrcidBio().getContactDetails().getEmail().size());
        assertNotNull(profile.getOrcidBio().getContactDetails().getEmail().get(0).getValue());
        assertEquals(email, profile.getOrcidBio().getContactDetails().getEmail().get(0).getValue());
        assertNotNull(profile.getOrcidBio().getExternalIdentifiers());
        assertNotNull(profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getExternalIdentifiers().getVisibility());
        assertEquals(1, profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("common-name", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("ext-id-reference", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdReference().getContent());
        assertEquals("http://orcid.org/ext-id", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdUrl().getValue());
        assertNotNull(profile.getOrcidBio().getKeywords());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getKeywords().getVisibility());
        assertEquals(1, profile.getOrcidBio().getKeywords().getKeyword().size());
        assertEquals("k1", profile.getOrcidBio().getKeywords().getKeyword().get(0).getContent());
        assertNotNull(profile.getOrcidBio().getPersonalDetails());        
        assertNotNull(profile.getOrcidBio().getPersonalDetails().getOtherNames());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(1, profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().size());
        assertEquals("o1", profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).getContent());
        assertNotNull(profile.getOrcidBio().getResearcherUrls());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getResearcherUrls().getVisibility());
        assertEquals(1, profile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://orcid.org/researcher-url-1", profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("url-name-1", profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrlName().getContent());
    
        ProfileEntity profileEntity = profileDao.find(profile.getOrcidIdentifier().getPath());
        assertNotNull(profileEntity);
        assertNotNull(profileEntity.getAddresses());
        assertEquals(1, profileEntity.getAddresses().size());
        assertEquals(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name(), profileEntity.getAddresses().iterator().next().getIso2Country());
        
        //Update all values
        profile.getOrcidBio().getBiography().setContent("This is my biography # 2");        
        profile.getOrcidBio().getContactDetails().getAddress().setCountry(new Country(Iso3166Country.CR));        
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setSource(null);
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setExternalIdCommonName(new ExternalIdCommonName("common-name-2"));
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setExternalIdReference(new ExternalIdReference("ext-id-reference-2"));
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setExternalIdUrl(new ExternalIdUrl("http://orcid.org/ext-id-2"));
        profile.getOrcidBio().getKeywords().getKeyword().get(0).setSource(null);
        profile.getOrcidBio().getKeywords().getKeyword().get(0).setContent("k2");
        profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).setSource(null);
        profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).setContent("o2");
        profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).setSource(null);
        profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).setUrl(new Url("http://orcid.org/researcher-url-2"));
        profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).setUrlName(new UrlName("url-name-2"));
        
        orcidProfileManager.updateOrcidBio(profile);
        
        //Everything should be updated but the primary address that was already set
        profile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(profile);
        assertNotNull(profile.getOrcidBio());
        assertNotNull(profile.getOrcidBio().getBiography());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getBiography().getVisibility());
        assertEquals("This is my biography # 2", profile.getOrcidBio().getBiography().getContent());
        assertNotNull(profile.getOrcidBio().getContactDetails());
        assertNotNull(profile.getOrcidBio().getContactDetails().getAddress());
        assertNotNull(profile.getOrcidBio().getContactDetails().getAddress().getCountry());
        assertNotNull(profile.getOrcidBio().getContactDetails().getEmail());
        assertEquals(1, profile.getOrcidBio().getContactDetails().getEmail().size());
        assertNotNull(profile.getOrcidBio().getContactDetails().getEmail().get(0).getValue());
        assertEquals(email, profile.getOrcidBio().getContactDetails().getEmail().get(0).getValue());
        assertNotNull(profile.getOrcidBio().getExternalIdentifiers());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getExternalIdentifiers().getVisibility());
        assertNotNull(profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("common-name-2", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("ext-id-reference-2", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdReference().getContent());
        assertEquals("http://orcid.org/ext-id-2", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdUrl().getValue());
        assertNotNull(profile.getOrcidBio().getKeywords());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getKeywords().getVisibility());
        assertEquals(1, profile.getOrcidBio().getKeywords().getKeyword().size());
        assertEquals("k2", profile.getOrcidBio().getKeywords().getKeyword().get(0).getContent());
        assertNotNull(profile.getOrcidBio().getPersonalDetails());        
        assertNotNull(profile.getOrcidBio().getPersonalDetails().getOtherNames());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(1, profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().size());
        assertEquals("o2", profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).getContent());
        assertNotNull(profile.getOrcidBio().getResearcherUrls());
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getResearcherUrls().getVisibility());
        assertEquals(1, profile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://orcid.org/researcher-url-2", profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("url-name-2", profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrlName().getContent());
        //Primary address should remain
        assertEquals(Iso3166Country.US, profile.getOrcidBio().getContactDetails().getAddress().getCountry().getValue());
                
        profileEntity = profileDao.find(profile.getOrcidIdentifier().getPath());
        assertNotNull(profileEntity);
        assertNotNull(profileEntity.getAddresses());
        assertEquals(2, profileEntity.getAddresses().size());
        Iterator<AddressEntity> it = profileEntity.getAddresses().iterator();
        while(it.hasNext()) {
            assertThat(it.next().getIso2Country(), anyOf(is(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name()), is(org.orcid.jaxb.model.common_v2.Iso3166Country.CR.name())));
        }        
        
        //Claim the record
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.DIRECT);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        profile.setOrcidHistory(orcidHistory);
        
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(true));
        preferences.setSendOrcidNews(new SendOrcidNews(true));
        //Default visibility for user will be LIMITED
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.LIMITED));
        preferences.setNotificationsEnabled(DefaultPreferences.NOTIFICATIONS_ENABLED);
        preferences.setSendEmailFrequencyDays(DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS);
        preferences.setSendMemberUpdateRequests(DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidInternal(internal);
        
        orcidProfileManager.updateOrcidProfile(profile);
        
        //Everything should be updated but the address, because the record is claimed
        profile.getOrcidBio().getBiography().setContent("This is my biography # 3");        
        profile.getOrcidBio().getContactDetails().getAddress().setCountry(new Country(Iso3166Country.PE));        
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setSource(null);
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setExternalIdCommonName(new ExternalIdCommonName("common-name-3"));
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setExternalIdReference(new ExternalIdReference("ext-id-reference-3"));
        profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).setExternalIdUrl(new ExternalIdUrl("http://orcid.org/ext-id-3"));
        profile.getOrcidBio().getKeywords().getKeyword().get(0).setSource(null);
        profile.getOrcidBio().getKeywords().getKeyword().get(0).setContent("k3");
        profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).setSource(null);
        profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).setContent("o3");
        profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).setSource(null);
        profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).setUrl(new Url("http://orcid.org/researcher-url-3"));
        profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).setUrlName(new UrlName("url-name-3"));
        
        orcidProfileManager.updateOrcidBio(profile);
        
        profile = orcidProfileManager.retrieveOrcidProfile(profile.getOrcidIdentifier().getPath());
        assertNotNull(profile);
        assertNotNull(profile.getOrcidBio());
        assertNotNull(profile.getOrcidBio().getBiography());
        assertEquals(Visibility.LIMITED, profile.getOrcidBio().getBiography().getVisibility());
        assertEquals("This is my biography # 3", profile.getOrcidBio().getBiography().getContent());
        assertNotNull(profile.getOrcidBio().getContactDetails());
        assertNotNull(profile.getOrcidBio().getContactDetails().getAddress());
        assertNotNull(profile.getOrcidBio().getContactDetails().getAddress().getCountry());
        assertNotNull(profile.getOrcidBio().getContactDetails().getEmail());
        assertEquals(1, profile.getOrcidBio().getContactDetails().getEmail().size());
        assertNotNull(profile.getOrcidBio().getContactDetails().getEmail().get(0).getValue());
        assertEquals(email, profile.getOrcidBio().getContactDetails().getEmail().get(0).getValue());
        assertNotNull(profile.getOrcidBio().getExternalIdentifiers());
        assertEquals(Visibility.LIMITED, profile.getOrcidBio().getExternalIdentifiers().getVisibility());
        assertNotNull(profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("common-name-3", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("ext-id-reference-3", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdReference().getContent());
        assertEquals("http://orcid.org/ext-id-3", profile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdUrl().getValue());
        assertNotNull(profile.getOrcidBio().getKeywords());
        assertEquals(Visibility.LIMITED, profile.getOrcidBio().getKeywords().getVisibility());
        assertEquals(1, profile.getOrcidBio().getKeywords().getKeyword().size());
        assertEquals("k3", profile.getOrcidBio().getKeywords().getKeyword().get(0).getContent());
        assertNotNull(profile.getOrcidBio().getPersonalDetails());        
        assertNotNull(profile.getOrcidBio().getPersonalDetails().getOtherNames());
        assertEquals(Visibility.LIMITED, profile.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility());
        assertEquals(1, profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().size());
        assertEquals("o3", profile.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).getContent());
        assertNotNull(profile.getOrcidBio().getResearcherUrls());
        assertEquals(Visibility.LIMITED, profile.getOrcidBio().getResearcherUrls().getVisibility());
        assertEquals(1, profile.getOrcidBio().getResearcherUrls().getResearcherUrl().size());
        assertEquals("http://orcid.org/researcher-url-3", profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl().getValue());
        assertEquals("url-name-3", profile.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrlName().getContent());
        
        profileEntity = profileDao.find(profile.getOrcidIdentifier().getPath());
        assertNotNull(profileEntity);
        assertNotNull(profileEntity.getAddresses());
        assertEquals(3, profileEntity.getAddresses().size());        
        it = profileEntity.getAddresses().iterator();
        while(it.hasNext()) {
            assertThat(it.next().getIso2Country(), anyOf(is(org.orcid.jaxb.model.common_v2.Iso3166Country.US.name()), is(org.orcid.jaxb.model.common_v2.Iso3166Country.CR.name()), is(org.orcid.jaxb.model.common_v2.Iso3166Country.PE.name())));
        }  
        
        //Primary address should remain
        assertEquals(Visibility.PUBLIC, profile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility());
        assertEquals(Iso3166Country.US, profile.getOrcidBio().getContactDetails().getAddress().getCountry().getValue());        
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void addNewWorksModifyExistingWorksDisplayIndex() {
        OrcidProfile profile1 = createBasicProfile();
        String orcidIdentifier = null;
        profile1.setOrcidIdentifier(orcidIdentifier);
        setBio(profile1, Visibility.PUBLIC);   
        profile1 = orcidProfileManager.createOrcidProfile(profile1, true, false);
        assertNotNull(profile1);
        assertNotNull(profile1.getOrcidIdentifier());
        
        String orcidId = profile1.getOrcidIdentifier().getPath();
        OrcidProfile profile = getWorkInsideOrcidProfile("w1", orcidId);
        orcidProfileManager.addOrcidWorks(profile);
        
        profile = getWorkInsideOrcidProfile("w2", orcidId);
        orcidProfileManager.addOrcidWorks(profile);
        
        profile = getWorkInsideOrcidProfile("w3", orcidId);
        orcidProfileManager.addOrcidWorks(profile);
        
        List<WorkLastModifiedEntity> wlme = workDao.getWorkLastModifiedList(orcidId);
        List<Long> ids = wlme.stream().map((w) -> w.getId()).collect(Collectors.toList());
        
        List<MinimizedWorkEntity> all = workDao.getMinimizedWorkEntities(ids);
        assertNotNull(all);
        Long displayIndex1 = null;
        Long displayIndex2 = null;
        Long displayIndex3 = null;
        
        for(MinimizedWorkEntity entity : all) {
            Long displayIndex = entity.getDisplayIndex();
            if("w1".equals(entity.getTitle())) {
                displayIndex1 = displayIndex;
            } else if("w2".equals(entity.getTitle())) {
                displayIndex2 = displayIndex;
            } else if("w3".equals(entity.getTitle())) {
                displayIndex3 = displayIndex;
            }
        }
        
        assertNotNull(displayIndex1);
        assertNotNull(displayIndex2);
        assertNotNull(displayIndex3);
        assertEquals(Long.valueOf(0L), displayIndex3);
        //TODO: Might need to be readed in a later release
        //assertTrue(displayIndex3 < displayIndex2);
        //assertTrue(displayIndex2 < displayIndex1);
    }
    
    @Test
    @Transactional
    @Rollback(true)    
    public void addNewFundingModifyExistingWorksDisplayIndex() {
        OrcidProfile profile1 = createBasicProfile();
        String orcidIdentifier = null;
        profile1.setOrcidIdentifier(orcidIdentifier);
        setBio(profile1, Visibility.PUBLIC);   
        profile1 = orcidProfileManager.createOrcidProfile(profile1, true, false);
        assertNotNull(profile1);
        assertNotNull(profile1.getOrcidIdentifier());
        
        String orcidId = profile1.getOrcidIdentifier().getPath();
        
        OrcidProfile profile = getFundingInsideOrcidProfile("f1", orcidId);
        orcidProfileManager.addFundings(profile);
        
        profile = getFundingInsideOrcidProfile("f2", orcidId);
        orcidProfileManager.addFundings(profile);
        
        profile = getFundingInsideOrcidProfile("f3", orcidId);
        orcidProfileManager.addFundings(profile);
        
        profile = getFundingInsideOrcidProfile("f4", orcidId);
        orcidProfileManager.addFundings(profile);        
        
        List<ProfileFundingEntity> all = profileFundingDao.getByUser(orcidId, System.currentTimeMillis());
        assertNotNull(all);
        Long displayIndex1 = null;
        Long displayIndex2 = null;
        Long displayIndex3 = null;
        
        for(ProfileFundingEntity entity : all) {
            Long displayIndex = entity.getDisplayIndex();
            if("f1".equals(entity.getTitle())) {
                displayIndex1 = displayIndex;
            } else if("f2".equals(entity.getTitle())) {
                displayIndex2 = displayIndex;
            } else if("f3".equals(entity.getTitle())) {
                displayIndex3 = displayIndex;
            }
        }
        
        assertNotNull(displayIndex1);
        assertNotNull(displayIndex2);
        assertNotNull(displayIndex3);
        assertEquals(Long.valueOf(0L), displayIndex3);
        //TODO: Might need to be readed in a later release
        //assertTrue(displayIndex3 < displayIndex2);
        //assertTrue(displayIndex2 < displayIndex1);
    }
    
    private void setBio(OrcidProfile profile, Visibility defaultVisibility) {
        OrcidBio bio = new OrcidBio();
        Biography biography = new Biography("This is my biography");
        if(defaultVisibility != null) {
            biography.setVisibility(defaultVisibility);
        }
        bio.setBiography(biography);
        ContactDetails contactDetails = new ContactDetails();
        Address address = new Address();
        address.setCountry(new Country(Iso3166Country.US));  
        if(defaultVisibility != null) {
            address.getCountry().setVisibility(defaultVisibility);
        }
        contactDetails.setAddress(address);
        List<Email> emails = new ArrayList<Email>();
        Email email = new Email();
        email.setPrimary(true);
        email.setValue(System.currentTimeMillis() + "@test.orcid.org");
        emails.add(email);
        contactDetails.setEmail(emails);
        bio.setContactDetails(contactDetails);
        
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        ExternalIdentifier extId = new ExternalIdentifier();
        extId.setExternalIdCommonName(new ExternalIdCommonName("common-name"));
        extId.setExternalIdReference(new ExternalIdReference("ext-id-reference"));
        extId.setExternalIdUrl(new ExternalIdUrl("http://orcid.org/ext-id"));
        extIds.getExternalIdentifier().add(extId);
        if(defaultVisibility != null) {
            extIds.setVisibility(defaultVisibility);
        }
        bio.setExternalIdentifiers(extIds);
        
        Keywords keywords = new Keywords();
        Keyword keyword = new Keyword();
        keyword.setContent("k1");
        keywords.getKeyword().add(keyword);
        if(defaultVisibility != null) {
            keywords.setVisibility(defaultVisibility);
        }
        bio.setKeywords(keywords);
        
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setCreditName(new CreditName("credit-name"));
        personalDetails.setGivenNames(new GivenNames("given-names"));
        personalDetails.setFamilyName(new FamilyName("family-name"));
        OtherNames otherNames = new OtherNames();
        OtherName otherName = new OtherName();
        otherName.setContent("o1");
        otherNames.getOtherName().add(otherName);
        if(defaultVisibility != null) {
            otherNames.setVisibility(defaultVisibility);
        }
        personalDetails.setOtherNames(otherNames);   
        bio.setPersonalDetails(personalDetails);
        
        ResearcherUrls researcherUrls = new ResearcherUrls();
        ResearcherUrl researcherUrl = new ResearcherUrl();
        researcherUrl.setUrl(new Url("http://orcid.org/researcher-url-1"));
        researcherUrl.setUrlName(new UrlName("url-name-1"));
        researcherUrls.getResearcherUrl().add(researcherUrl);
        if(defaultVisibility != null) {
            researcherUrls.setVisibility(defaultVisibility);
        }
        bio.setResearcherUrls(researcherUrls);
        profile.setOrcidBio(bio);
    }
    
    private OrcidWork getOrcidWork(String workName, boolean isExistingWork) {
        OrcidWork orcidWork = new OrcidWork();
        if(isExistingWork) {
            orcidWork.setPutCode(String.valueOf(currentWorkId));
            currentWorkId += 1;
        }
        //Set title
        WorkTitle title = new WorkTitle();
        if(isExistingWork)
            title.setTitle(new Title("Title for " + workName));            
        else
            title.setTitle(new Title("Title for " + workName + "->updated"));
        orcidWork.setWorkTitle(title);
        
        //Set source
        Source workSource = new Source(TEST_ORCID);
        orcidWork.setSource(workSource);
        
        //Set external identifiers
        WorkExternalIdentifier extId1 = new WorkExternalIdentifier();
        extId1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("doi-" + workName));
        extId1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        
        WorkExternalIdentifier extId2 = new WorkExternalIdentifier();
        if(isExistingWork)
            extId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("issn-" + workName));
        else
            extId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("issn-" + workName + "->updated"));
        extId2.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
        
        WorkExternalIdentifiers extIds = new WorkExternalIdentifiers();
        extIds.getWorkExternalIdentifier().add(extId1);
        extIds.getWorkExternalIdentifier().add(extId2);        
        
        orcidWork.setWorkExternalIdentifiers(extIds);
        
        return orcidWork;
    }

    private OrcidProfile getFundingInsideOrcidProfile(String defaultTitle, String orcid) {
        Funding funding = new Funding();
        funding.setType(FundingType.AWARD);
        FundingTitle title = new FundingTitle();
        if(defaultTitle == null) {
            title.setTitle(new Title("New Funding"));
        } else {
            title.setTitle(new Title(defaultTitle));
        }
        
        funding.setTitle(title);
        FundingExternalIdentifiers fExtIds = new FundingExternalIdentifiers();
        FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        if(defaultTitle == null) {
            fExtId.setValue("123");
        } else {
            fExtId.setValue("123-" + defaultTitle);
        }
        fExtIds.getFundingExternalIdentifier().add(fExtId);
        funding.setFundingExternalIdentifiers(fExtIds);
        Organization org = new Organization();
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("city");
        add.setCountry(Iso3166Country.US);
        org.setName("Test org");
        org.setAddress(add);
        funding.setOrganization(org);
        
        FundingList fList = new FundingList();
        fList.getFundings().add(funding);
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcidIdentifier(orcid);
        profile.setFundings(fList);
        return profile;
    }
    
    private OrcidProfile getWorkInsideOrcidProfile(String defaultTitle, String orcid) {
        OrcidWork orcidWork = new OrcidWork();
        //Set title
        WorkTitle title = new WorkTitle();
        if(defaultTitle != null)
            title.setTitle(new Title(defaultTitle));            
        else
            title.setTitle(new Title("Title"));
        orcidWork.setWorkTitle(title);
        
        //Set external identifiers
        WorkExternalIdentifier extId1 = new WorkExternalIdentifier();
        extId1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("doi-" + defaultTitle));
        extId1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        
        WorkExternalIdentifier extId2 = new WorkExternalIdentifier();
        if(defaultTitle != null)
            extId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("issn-" + defaultTitle));
        else
            extId2.setWorkExternalIdentifierId(new WorkExternalIdentifierId("issn-" + System.currentTimeMillis()));
        extId2.setWorkExternalIdentifierType(WorkExternalIdentifierType.ISSN);
        
        WorkExternalIdentifiers extIds = new WorkExternalIdentifiers();
        extIds.getWorkExternalIdentifier().add(extId1);
        extIds.getWorkExternalIdentifier().add(extId2);        
        
        orcidWork.setWorkExternalIdentifiers(extIds);
        
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcidIdentifier(orcid);
        List<OrcidWork> workList = new ArrayList<OrcidWork>();
        workList.add(orcidWork);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidWorks.setOrcidWork(workList);       
        profile.setOrcidWorks(orcidWorks);
        return profile;
    }
    
    
}
