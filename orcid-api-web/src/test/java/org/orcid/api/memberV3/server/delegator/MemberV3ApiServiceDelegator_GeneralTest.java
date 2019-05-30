package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.api.memberV3.server.delegator.impl.MemberV3ApiServiceDelegatorImpl;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.locale.LocaleManagerImpl;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.impl.OrcidSearchManagerImpl;
import org.orcid.core.manager.v3.impl.OrcidSecurityManagerImpl;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.search.Result;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_GeneralTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";
    
    @Resource
    private GroupIdRecordDao groupIdRecordDao;

    @Resource
    protected EmailFrequencyManager emailFrequencyManager;
    
    @Mock
    protected EmailFrequencyManager mockEmailFrequencyManager;
        
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @Resource(name = "memberV3ApiServiceDelegator")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put(EmailFrequencyManager.ADMINISTRATIVE_CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.MEMBER_UPDATE_REQUESTS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.QUARTERLY_TIPS, String.valueOf(true));
        
        when(mockEmailFrequencyManager.getEmailFrequency(anyString())).thenReturn(map);
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailFrequencyManager", mockEmailFrequencyManager); 
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(notificationManager, "emailFrequencyManager", emailFrequencyManager);         
    }
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testOrcidProfileCreate_CANT_AddOnClaimedAccounts() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();

        // Test can't create
        try {
            serviceDelegator.createAddress(ORCID, Utils.getAddress());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createEducation(ORCID, (Education) Utils.getAffiliation(AffiliationType.EDUCATION));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createEmployment(ORCID, (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createExternalIdentifier(ORCID, Utils.getPersonExternalIdentifier());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createFunding(ORCID, Utils.getFunding());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createKeyword(ORCID, Utils.getKeyword());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createOtherName(ORCID, Utils.getOtherName());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createPeerReview(ORCID, Utils.getPeerReview());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createResearcherUrl(ORCID, Utils.getResearcherUrl());
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.createWork(ORCID, Utils.getWork("work # 1 " + System.currentTimeMillis()));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
    }

    @Test
    public void testOrcidProfileCreate_CANT_ViewOnClaimedAccounts() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        try {
            serviceDelegator.viewActivities(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewAddress(ORCID, 9L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewAddresses(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewBiography(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEducation(ORCID, 20L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEducationSummary(ORCID, 20L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEducations(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmails(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmployment(ORCID, 17L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 17L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewEmployments(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewExternalIdentifier(ORCID, 13L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewExternalIdentifiers(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewFunding(ORCID, 10L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewFundingSummary(ORCID, 10L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewFundings(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewKeyword(ORCID, 9L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewKeywords(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewOtherName(ORCID, 13L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewOtherNames(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPeerReview(ORCID, 9L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 9L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPeerReviews(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPerson(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewPersonalDetails(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewResearcherUrl(ORCID, 13L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewResearcherUrls(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewWork(ORCID, 11L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewWorkSummary(ORCID, 11L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewWorks(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.viewRecord(ORCID);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
    }
    
    @Test
    public void testOrcidProfileCreateCanViewAndCreateGroupIds() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        try {
            serviceDelegator.viewGroupIdRecord(1L);
        } catch(Exception e) {
            fail();
        } 
        
        try {
            serviceDelegator.viewGroupIdRecords("10", "1");
        } catch(Exception e) {
            fail();
        } 
        
        GroupIdRecord groupIdRecord = Utils.getNonIssnGroupIdRecord();
        try {
            serviceDelegator.createGroupIdRecord(groupIdRecord);
        } catch(Exception e) {
            fail();
        } 
        
        GroupIdRecordEntity toDelete = groupIdRecordDao.findByGroupId(groupIdRecord.getGroupId());
        groupIdRecordDao.remove(toDelete.getId());
    }

    @Test
    public void testOrcidProfileCreate_CANT_DeleteOnClaimedAccounts() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        try {
            serviceDelegator.deleteAddress(ORCID, 9L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteAffiliation(ORCID, 20L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteExternalIdentifier(ORCID, 13L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteFunding(ORCID, 10L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteKeyword(ORCID, 9L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteOtherName(ORCID, 13L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deletePeerReview(ORCID, 9L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteResearcherUrl(ORCID, 13L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
        try {
            serviceDelegator.deleteWork(ORCID, 11L);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
    }

    @Test
    public void testOrcidProfileCreate_CANT_UpdateOnClaimedAccounts() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(response);
        Address a = (Address) response.getEntity();
        assertNotNull(a);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateAddress(ORCID, a.getPutCode(), a);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education edu = (Education) response.getEntity();
        assertNotNull(edu);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateEducation(ORCID, edu.getPutCode(), edu);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment emp = (Employment) response.getEntity();
        assertNotNull(emp);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateEmployment(ORCID, emp.getPutCode(), emp);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateExternalIdentifier(ORCID, extId.getPutCode(), extId);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(response);
        Funding f = (Funding) response.getEntity();
        assertNotNull(f);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateFunding(ORCID, f.getPutCode(), f);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(response);
        Keyword k = (Keyword) response.getEntity();
        assertNotNull(k);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateKeyword(ORCID, k.getPutCode(), k);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(response);
        OtherName o = (OtherName) response.getEntity();
        assertNotNull(o);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateOtherName(ORCID, o.getPutCode(), o);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(response);
        PeerReview p = (PeerReview) response.getEntity();
        assertNotNull(p);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updatePeerReview(ORCID, p.getPutCode(), p);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(response);
        ResearcherUrl r = (ResearcherUrl) response.getEntity();
        assertNotNull(r);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateResearcherUrl(ORCID, r.getPutCode(), r);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }

        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        response = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(response);
        Work w = (Work) response.getEntity();
        assertNotNull(w);
        try {
            SecurityContextTestUtils.setUpSecurityContextForClientOnly();
            serviceDelegator.updateWork(ORCID, w.getPutCode(), w);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Non client credential scope found in client request", e.getMessage());
        }
    }

    @Test
    public void testOrcidProfileCreate_CAN_CRUDOnUnclaimedAccounts() {
        String orcid = "0000-0000-0000-0001";
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        // Test address
        Response response = serviceDelegator.createAddress(orcid, Utils.getAddress());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewAddress(orcid, putCode);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        address.getCountry().setValue(Iso3166Country.ZW);
        response = serviceDelegator.updateAddress(orcid, putCode, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAddress(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test education
        Education education = (Education) Utils.getAffiliation(AffiliationType.EDUCATION);
        response = serviceDelegator.createEducation(orcid, education);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewEducation(orcid, putCode);
        assertNotNull(response);
        education = (Education) response.getEntity();
        assertNotNull(education);
        education.setDepartmentName("Updated department name");
        response = serviceDelegator.updateEducation(orcid, putCode, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAffiliation(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test employment
        response = serviceDelegator.createEmployment(orcid, (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewEmployment(orcid, putCode);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        employment.setDepartmentName("Updated department name");
        response = serviceDelegator.updateEmployment(orcid, putCode, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAffiliation(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test external identifiers
        response = serviceDelegator.createExternalIdentifier(orcid, Utils.getPersonExternalIdentifier());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewExternalIdentifier(orcid, putCode);
        assertNotNull(response);
        PersonExternalIdentifier externalIdentifier = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(externalIdentifier);
        response = serviceDelegator.updateExternalIdentifier(orcid, putCode, externalIdentifier);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteExternalIdentifier(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test funding
        response = serviceDelegator.createFunding(orcid, Utils.getFunding());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewFunding(orcid, putCode);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        response = serviceDelegator.updateFunding(orcid, putCode, funding);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteFunding(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test keyword
        response = serviceDelegator.createKeyword(orcid, Utils.getKeyword());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewKeyword(orcid, putCode);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        response = serviceDelegator.updateKeyword(orcid, putCode, keyword);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteKeyword(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test other names
        response = serviceDelegator.createOtherName(orcid, Utils.getOtherName());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewOtherName(orcid, putCode);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        response = serviceDelegator.updateOtherName(orcid, putCode, otherName);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteOtherName(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test peer review
        response = serviceDelegator.createPeerReview(orcid, Utils.getPeerReview());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewPeerReview(orcid, putCode);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        response = serviceDelegator.updatePeerReview(orcid, putCode, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deletePeerReview(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test researcher url
        response = serviceDelegator.createResearcherUrl(orcid, Utils.getResearcherUrl());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewResearcherUrl(orcid, putCode);
        assertNotNull(response);
        ResearcherUrl rUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(rUrl);
        response = serviceDelegator.updateResearcherUrl(orcid, putCode, rUrl);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteResearcherUrl(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test work
        response = serviceDelegator.createWork(orcid, Utils.getWork("work # 1 " + System.currentTimeMillis()));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        response = serviceDelegator.viewWork(orcid, putCode);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        response = serviceDelegator.updateWork(orcid, putCode, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteWork(orcid, putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void testSearchByQuery() {
        Search search = new Search();
        Result result = new Result();
        result.setOrcidIdentifier(new OrcidIdentifier("some-orcid-id"));
        search.getResults().add(result);
        OrcidSearchManager orcidSearchManager = Mockito.mock(OrcidSearchManagerImpl.class);
        Mockito.when(orcidSearchManager.findOrcidIds(Matchers.<Map<String, List<String>>> any())).thenReturn(search);

        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManagerImpl.class);
        Mockito.doNothing().when(orcidSecurityManager).checkScopes(Mockito.any(ScopePathType.class));

        MemberV3ApiServiceDelegatorImpl delegator = new MemberV3ApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "orcidSearchManager", orcidSearchManager);
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);

        Response response = delegator.searchByQuery(new HashMap<String, List<String>>());

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Search);
        assertEquals(1, ((Search) response.getEntity()).getResults().size());
        assertEquals("some-orcid-id", ((Search) response.getEntity()).getResults().get(0).getOrcidIdentifier().getPath());
    }

    @Test(expected = OrcidBadRequestException.class)
    public void testSearchByQueryTooManyRows() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("rows", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_ROWS + 20)));

        LocaleManager localeManager = Mockito.mock(LocaleManagerImpl.class);
        Mockito.when(localeManager.resolveMessage(Mockito.anyString())).thenReturn("a message");

        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManagerImpl.class);
        Mockito.doNothing().when(orcidSecurityManager).checkScopes(Mockito.any(ScopePathType.class));

        MemberV3ApiServiceDelegatorImpl delegator = new MemberV3ApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "localeManager", localeManager);
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);
        delegator.searchByQuery(params);
    }

    @Test(expected = AccessControlException.class)
    public void testSearchByQueryBadScope() {
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManagerImpl.class);
        Mockito.doThrow(new AccessControlException("some problem with scope")).when(orcidSecurityManager).checkScopes(Mockito.any(ScopePathType.class));

        MemberV3ApiServiceDelegatorImpl delegator = new MemberV3ApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);

        delegator.searchByQuery(new HashMap<>());
    }

    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        serviceDelegator.viewClient("some-client-that-doesn't-exist");
        fail();
    }

    @Test
    public void testViewClient() {
        Response response = serviceDelegator.viewClient("APP-6666666666666666");
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);

        ClientSummary clientSummary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", clientSummary.getName());
        assertEquals("A test source client", clientSummary.getDescription());
    }
}
