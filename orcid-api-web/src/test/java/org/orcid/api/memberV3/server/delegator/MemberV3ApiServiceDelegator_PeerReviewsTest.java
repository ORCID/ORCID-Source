package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.dev1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.common.Title;
import org.orcid.jaxb.model.v3.dev1.common.Url;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Address;
import org.orcid.jaxb.model.v3.dev1.record.Distinction;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.jaxb.model.v3.dev1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Membership;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.jaxb.model.v3.dev1.record.PeerReviewType;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.Qualification;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.dev1.record.Role;
import org.orcid.jaxb.model.v3.dev1.record.Service;
import org.orcid.jaxb.model.v3.dev1.record.Work;
import org.orcid.jaxb.model.v3.dev1.record.WorkBulk;
import org.orcid.jaxb.model.v3.dev1.record.WorkTitle;
import org.orcid.jaxb.model.v3.dev1.record.WorkType;
import org.orcid.jaxb.model.v3.dev1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.dev1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.PeerReviews;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_PeerReviewsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/OrgsEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegatorV3_0_dev1")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword> serviceDelegator;

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
    public void testViewPeerReviewWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewSummaryWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", Long.valueOf(1));
    }

    @Test
    public void testViewPeerReviewReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
        PeerReview element = (PeerReview) r.getEntity();
        assertNotNull(element);
        assertEquals("/4444-4444-4444-4447/peer-review/2", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPeerReviewSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", Long.valueOf(1));
        PeerReviewSummary element = (PeerReviewSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/4444-4444-4444-4446/peer-review/1", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 1L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/1", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf(1L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("reviewer", peerReview.getRole().value());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());
        assertEquals("review", peerReview.getType().value());
        assertEquals("http://peer_review.com", peerReview.getUrl().getValue());
        assertEquals("Peer Review # 1", peerReview.getSubjectName().getTitle().getContent());
        assertEquals("es", peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
        assertEquals("artistic-performance", peerReview.getSubjectType().value());
        assertEquals("http://work.com", peerReview.getSubjectUrl().getValue());
        assertEquals("Peer Review # 1 container name", peerReview.getSubjectContainerName().getContent());
        assertEquals("peer-review:subject-external-identifier-id#1", peerReview.getSubjectExternalIdentifier().getValue());
        assertEquals("agr", peerReview.getSubjectExternalIdentifier().getType());
        assertEquals("issn:0000001", peerReview.getGroupId());
    }

    @Test
    public void testViewLimitedPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 3L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/3", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf(3L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("limited", peerReview.getVisibility().value());
        assertEquals("issn:0000002", peerReview.getGroupId());
    }

    @Test
    public void testViewPrivatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 4L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/4", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf(4L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("private", peerReview.getVisibility().value());
        assertEquals("issn:0000003", peerReview.getGroupId());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivatePeerReviewWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4446", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewPeerReviewThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4446", 2L);
        fail();
    }

    @Test
    public void testViewPeerReviewSummary() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", Long.valueOf(1));
        assertNotNull(response);
        PeerReviewSummary peerReview = (PeerReviewSummary) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/1", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf("1"), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());
    }

    @Test
    public void testViewPeerReviews() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(r);
        PeerReviews peerReviews = (PeerReviews) r.getEntity();
        assertNotNull(peerReviews);
        assertEquals("/0000-0000-0000-0003/peer-reviews", peerReviews.getPath());
        Utils.verifyLastModified(peerReviews.getLastModifiedDate());
        assertNotNull(peerReviews.getPeerReviewGroup());
        assertEquals(4, peerReviews.getPeerReviewGroup().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getPeerReviewSummary());
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary summary = group.getPeerReviewSummary().get(0);
            Utils.verifyLastModified(summary.getLastModifiedDate());
            switch (group.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "issn:0000009":
                assertEquals("issn:0000009", summary.getGroupId());
                assertEquals(Long.valueOf(9), summary.getPutCode());
                found1 = true;
                break;
            case "issn:0000010":
                assertEquals("issn:0000010", summary.getGroupId());
                assertEquals(Long.valueOf(10), summary.getPutCode());
                found2 = true;
                break;
            case "issn:0000011":
                assertEquals("issn:0000011", summary.getGroupId());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found3 = true;
                break;
            case "issn:0000012":
                assertEquals("issn:0000012", summary.getGroupId());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid group id found: " + group.getIdentifiers().getExternalIdentifier().get(0).getValue());
                break;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }

    @Test
    public void testReadPublicScope_PeerReview() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Public works
        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReview.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewPeerReviewSummary(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReviewSummary.class.getName(), r.getEntity().getClass().getName());
        // Limited where am the source of should work
        serviceDelegator.viewPeerReview(ORCID, 10L);
        serviceDelegator.viewPeerReviewSummary(ORCID, 10L);
        // Limited where am not the source of should fail
        try {
            serviceDelegator.viewPeerReview(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Limited where am the source of should work
        serviceDelegator.viewPeerReview(ORCID, 11L);
        serviceDelegator.viewPeerReviewSummary(ORCID, 11L);
        // Limited where am not the source of should fail
        try {
            serviceDelegator.viewPeerReview(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        Utils.verifyLastModified(peerReview.getLastModifiedDate());

        LastModifiedDate before = peerReview.getLastModifiedDate();

        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        
        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        PeerReview updatedPeerReview = (PeerReview) response.getEntity();
        assertNotNull(updatedPeerReview);
        Utils.verifyLastModified(updatedPeerReview.getLastModifiedDate());
        assertTrue(updatedPeerReview.getLastModifiedDate().after(before));
        assertEquals("http://updated.com/url", updatedPeerReview.getUrl().getValue());
        assertEquals("Updated Title", updatedPeerReview.getSubjectName().getTitle().getContent());
    }

    @Test
    public void testUpdatePeerReviewWhenYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());

        // Update the info
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        peerReview.getExternalIdentifiers().getExternalIdentifier().iterator().next().setValue("different");

        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("some-org");
        disambiguatedOrg.setDisambiguationSource("FUNDREF");
        peerReview.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);
        
        try {
            response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 2L, peerReview);
            fail();
        } catch (WrongSourceException wse) {

        }
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", Long.valueOf(2));
        peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdatePeerReviewChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());

        peerReview.setVisibility(Visibility.PRIVATE);

        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        fail();
    }

    @Test
    public void testUpdatePeerReviewLeavingVisibilityNullTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());

        peerReview.setVisibility(null);

        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());
    }

    @Test
    public void testAddPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4444");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(1, summary.getPeerReviews().getPeerReviewGroup().size());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0));
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0));
        assertEquals("issn:0000001", summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());

        PeerReview peerReview = Utils.getPeerReview();

        response = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4444");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getPeerReviews());
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(2, summary.getPeerReviews().getPeerReviewGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            Utils.verifyLastModified(group.getPeerReviewSummary().get(0).getLastModifiedDate());
            if ("issn:0000001".equals(group.getPeerReviewSummary().get(0).getGroupId())) {
                haveOld = true;
            } else {
                assertEquals("issn:0000003", group.getPeerReviewSummary().get(0).getGroupId());
                haveNew = true;
            }
        }
        assertTrue(haveOld);
        assertTrue(haveNew);

        // Delete the new so it doesn't affect other tests
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddPeerReviewDuplicateFails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");

        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("some-org");
        disambiguatedOrg.setDisambiguationSource("FUNDREF");
        peerReview.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);
        
        peerReview.setPutCode(null);

        response = serviceDelegator.createPeerReview("4444-4444-4444-4447", peerReview);
    }

    @Test
    public void testAddPeerReviewWithSameExtIdValueButDifferentExtIdType() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        PeerReview peerReview1 = new PeerReview();
        ExternalIDs weis1 = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(null);
        wei1.setValue("same_but_different_type");
        wei1.setType(WorkExternalIdentifierType.DOI.value());
        weis1.getExternalIdentifier().add(wei1);
        peerReview1.setExternalIdentifiers(weis1);
        peerReview1.setGroupId("issn:0000003");
        peerReview1.setOrganization(Utils.getOrganization());
        peerReview1.setRole(Role.CHAIR);
        peerReview1.setSubjectContainerName(new Title("subject-container-name"));
        peerReview1.setSubjectExternalIdentifier(wei1);
        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("work-title"));
        peerReview1.setSubjectName(workTitle1);
        peerReview1.setSubjectType(WorkType.DATA_SET);
        peerReview1.setType(PeerReviewType.EVALUATION);

        Response response1 = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview1);
        assertNotNull(response1);
        assertEquals(Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Map<?, ?> map = response1.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        PeerReview peerReview2 = new PeerReview();
        ExternalIDs weis2 = new ExternalIDs();
        ExternalID wei2 = new ExternalID();
        wei2.setRelationship(null);
        wei2.setValue("same_but_different_type"); // Same value
        wei2.setType(WorkExternalIdentifierType.ARXIV.value()); // But different
                                                                // type
        weis2.getExternalIdentifier().add(wei2);
        peerReview2.setExternalIdentifiers(weis2);
        peerReview2.setGroupId("issn:0000003");
        peerReview2.setOrganization(Utils.getOrganization());
        peerReview2.setRole(Role.CHAIR);
        peerReview2.setSubjectContainerName(new Title("subject-container-name"));
        peerReview2.setSubjectExternalIdentifier(wei2);
        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("work-title"));
        peerReview2.setSubjectName(workTitle2);
        peerReview2.setSubjectType(WorkType.DATA_SET);
        peerReview2.setType(PeerReviewType.EVALUATION);

        Response response2 = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview2);
        assertNotNull(response2);
        assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());
        map = response2.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        // Delete new peer reviews so they don't affect other tests
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode1);
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode2);
    }

    @Test
    public void testDeletePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        PeerReview review = (PeerReview) response.getEntity();
        assertNotNull(review);
        assertNotNull(review.getSubjectName());
        assertNotNull(review.getSubjectName().getTitle());
        assertEquals("Peer Review # 3", review.getSubjectName().getTitle().getContent());

        response = serviceDelegator.deletePeerReview("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

    }

    @Test
    public void testAddPeerReviewWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        PeerReview peerReview = Utils.getPeerReview();

        // Set both to a correct value
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");

        // Check it fail on external identifier type
        try {
            peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        /*
         * This case is now ok (external-id-api branch 05/16) - adapters ensure
         * correct value is stored in DB. try {
         * peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).
         * setType("DOI"); serviceDelegator.createPeerReview(orcid, peerReview);
         * fail(); } catch(ActivityIdentifierValidationException e) {
         * 
         * } catch(Exception e) { fail(); }
         */

        // Set the ext id to a correct value to test the subject ext id
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        // Check it fail on subject external identifier type
        try {
            peerReview.getSubjectExternalIdentifier().setType("INVALID");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        /*
         * try { peerReview.getSubjectExternalIdentifier().setType("DOI");
         * serviceDelegator.createPeerReview(orcid, peerReview); fail(); }
         * catch(ActivityIdentifierValidationException e) {
         * 
         * } catch(Exception e) { fail(); }
         */

        // Test it works with correct values
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");
        Response response = serviceDelegator.createPeerReview(orcid, peerReview);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        // Delete it to roll back the test data
        response = serviceDelegator.deletePeerReview(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeletePeerReviewYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deletePeerReview("4444-4444-4444-4447", 2L);
        fail();
    }
}
