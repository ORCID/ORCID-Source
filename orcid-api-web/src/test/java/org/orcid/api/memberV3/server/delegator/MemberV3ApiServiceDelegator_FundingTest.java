package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Fundings;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_FundingTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegator")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

    @Resource
    protected EmailFrequencyManager emailFrequencyManager;
    
    @Mock
    protected EmailFrequencyManager mockEmailFrequencyManager;
        
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
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

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding(ORCID, 10L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFundingSummary(ORCID, 10L);
    }

    @Test
    public void testViewFundingReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        Funding element = (Funding) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/funding/10", element.getPath());
        assertNotNull(element.getContributors().getContributor().get(0).getContributorOrcid());
        assertEquals("0000-0000-0000-0000", element.getContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertNull(element.getContributors().getContributor().get(0).getCreditName());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewFundingSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewFundingSummary(ORCID, 10L);
        FundingSummary element = (FundingSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/funding/10", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testAddFundingWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        Funding funding = Utils.getFunding();

        try {
            funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createFunding(orcid, funding);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("grant_number");
        Response response = serviceDelegator.createFunding(orcid, funding);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        // Delete it to roll back the test data
        response = serviceDelegator.deleteFunding(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testViewPublicFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(5), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/5", funding.getPath());
        assertEquals("Public Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewLimitedFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(1), funding.getPutCode());
        assertEquals("/4444-4444-4444-4443/funding/1", funding.getPath());
        assertEquals("Grant # 1", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewPrivateFunding() {
        // Use the smallest scope in the pyramid to verify that you can read
        // your own limited and protected data
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 4L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(4), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/4", funding.getPath());
        assertEquals("Private Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateFundingWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding("4444-4444-4444-4443", 3L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewFundingThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Funding 1 belongs to 4444-4444-4444-4443
        serviceDelegator.viewFunding("4444-4444-4444-4446", 1L);
        fail();
    }

    @Test
    public void testViewFundings() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewFundings(ORCID);
        assertNotNull(r);
        Fundings fundings = (Fundings) r.getEntity();
        assertNotNull(fundings);
        assertEquals("/0000-0000-0000-0003/fundings", fundings.getPath());
        assertNotNull(fundings.getPath());
        Utils.verifyLastModified(fundings.getLastModifiedDate());
        assertNotNull(fundings.getFundingGroup());
        assertEquals(4, fundings.getFundingGroup().size());

        boolean found1 = false, found2 = false, found3 = false, found4 = false;

        for (FundingGroup fundingGroup : fundings.getFundingGroup()) {
            Utils.verifyLastModified(fundingGroup.getLastModifiedDate());
            assertNotNull(fundingGroup.getIdentifiers());
            assertNotNull(fundingGroup.getIdentifiers().getExternalIdentifier());
            assertEquals(1, fundingGroup.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(fundingGroup.getFundingSummary());
            assertEquals(1, fundingGroup.getFundingSummary().size());
            FundingSummary summary = fundingGroup.getFundingSummary().get(0);
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            switch (fundingGroup.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "1":
                assertEquals("PUBLIC", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(10), summary.getPutCode());
                found1 = true;
                break;
            case "2":
                assertEquals("LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found2 = true;
                break;
            case "3":
                assertEquals("PRIVATE", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found3 = true;
                break;
            case "4":
                assertEquals("SELF LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(13), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid external id found: " + fundingGroup.getIdentifiers().getExternalIdentifier().get(0).getValue());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }

    @Test
    public void testReadPublicScope_Funding() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Public works
        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(r);
        assertEquals(Funding.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewFundingSummary(ORCID, 10L);
        assertNotNull(r);
        assertEquals(FundingSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewFunding(ORCID, 11L);
        serviceDelegator.viewFundingSummary(ORCID, 11L);

        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewFunding(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewFundingSummary(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewFunding(ORCID, 12L);
        serviceDelegator.viewFundingSummary(ORCID, 12L);

        // Private am not the source of should fail
        try {
            serviceDelegator.viewFunding(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewFundingSummary(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getFundings());
        Utils.verifyLastModified(summary.getFundings().getLastModifiedDate());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertNotNull(summary.getFundings().getFundingGroup().get(0));
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, summary.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0));
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle());
        assertEquals("Public Funding # 1", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());

        Funding newFunding = Utils.getFunding();

        response = serviceDelegator.createFunding("4444-4444-4444-4447", newFunding);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertEquals(2, summary.getFundings().getFundingGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary().get(0));
            assertNotNull(group.getFundingSummary().get(0).getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle().getContent());
            if ("Public Funding # 1".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveOld = true;
            } else if ("Public Funding # 2".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals("Public Funding # 1", funding.getTitle().getTitle().getContent());
        assertEquals("This is the description for funding with id 6", funding.getDescription());

        LastModifiedDate before = funding.getLastModifiedDate();

        funding.getTitle().getTitle().setContent("Updated funding title");
        funding.setDescription("This is an updated description");
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        funding.setExternalIdentifiers(fExtIds);

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertTrue(funding.getLastModifiedDate().after(before));
        assertEquals("Updated funding title", funding.getTitle().getTitle().getContent());
        assertEquals("This is an updated description", funding.getDescription());

        // Rollback changes
        funding.getTitle().getTitle().setContent("Public Funding # 1");
        funding.setDescription("This is the description for funding with id 6");

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateFundingYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        funding.getTitle().getTitle().setContent("Updated funding title");
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        funding.setExternalIdentifiers(fExtIds);

        serviceDelegator.updateFunding("4444-4444-4444-4446", 5L, funding);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateFundingChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals(Visibility.PUBLIC, funding.getVisibility());

        funding.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        fail();
    }

    @Test
    public void testUpdateFundingLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals(Visibility.PUBLIC, funding.getVisibility());

        funding.setVisibility(null);

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        funding = (Funding) response.getEntity();
        assertEquals(Visibility.PUBLIC, funding.getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4442", 7L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        response = serviceDelegator.deleteFunding("4444-4444-4444-4442", 7L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewFunding("4444-4444-4444-4442", 7L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteFundingYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteFunding("4444-4444-4444-4446", 5L);
        fail();
    }
}
