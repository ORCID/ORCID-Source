package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.test.DBUnitTest;
import org.orcid.test.DatabaseTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * The rules in this family that only a database can prove.
 *
 * <p>
 * Every DAO behind the member V3 API scopes its lookup by record as well as by
 * put code -- {@code FROM WorkEntity WHERE id = :workId and orcid = :orcid} and
 * its dozen siblings -- and that predicate is the guarantee that put code 5 read
 * through record B does not return record A's work. It lives in a SQL WHERE
 * clause and nowhere else: with a mocked manager the delegator tests can only
 * show that the resulting {@code NoResultException} is not swallowed, which is
 * why each of those tests carries a comment pointing here.
 *
 * <p>
 * {@code test3_0} is here for the same reason: the {@code https://} on a source
 * URI is applied by the JPA to JAXB adapters and the configured base URL, so
 * with a mocked record manager the assertion would only be checking the URI the
 * test itself wrote.
 *
 * <p>
 * This is the only class in {@code org.orcid.api.memberV3} that still boots the
 * Spring context and loads DBUnit. It is marked {@code @Category(DatabaseTest)},
 * so the default {@code mvn test} run skips it and the {@code db-tests} profile
 * runs it.
 */
@Category(DatabaseTest.class)
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-api-web-context.xml" })
public class MemberV3ApiServiceDelegatorDatabaseRulesTest extends DBUnitTest {

    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml",
            "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegator")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test(expected = NoResultException.class)
    public void testViewDistinctionThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Distinction 27 belongs to 0000-0000-0000-0003
        serviceDelegator.viewDistinction("4444-4444-4444-4446", 27L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEducationThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Education 20 belongs to 0000-0000-0000-0003
        serviceDelegator.viewEducation("4444-4444-4444-4446", 20L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Employment 4 belongs to another record
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 4L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewInvitedPositionThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Invited position 32 belongs to 0000-0000-0000-0003
        serviceDelegator.viewInvitedPosition("4444-4444-4444-4446", 32L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewMembershipThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Membership 37 belongs to 0000-0000-0000-0003
        serviceDelegator.viewMembership("4444-4444-4444-4446", 37L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewQualificationThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Qualification 42 belongs to 0000-0000-0000-0003
        serviceDelegator.viewQualification("4444-4444-4444-4446", 42L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewServiceThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Service 47 belongs to 0000-0000-0000-0003
        serviceDelegator.viewService("4444-4444-4444-4446", 47L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewFundingThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Funding 1 belongs to 4444-4444-4444-4443
        serviceDelegator.viewFunding("4444-4444-4444-4446", 1L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewPeerReviewThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4446", 2L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewWorkThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork("4444-4444-4444-4443", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewOtherNameThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4446", 1L);
        fail();
    }

    /**
     * Keeps the name the keyword suite gave it; it drives viewOtherName, which is
     * what the original did too.
     */
    @Test(expected = NoResultException.class)
    public void testViewKeywordThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4443", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewExternalIdentifierThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 1L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewResearcherUrlThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 1L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewAddressThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewAddress("4444-4444-4444-4447", 1L);
        fail();
    }

    @Test
    public void test3_0() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) response.getEntity();
        assertNotNull(record.getActivitiesSummary());
        ActivitiesSummary activitiesSummary = record.getActivitiesSummary();
        if (activitiesSummary.getEducations() != null) {
            activitiesSummary.getEducations().getEducationGroups().forEach(e -> {
                for (EducationSummary s : e.getActivities()) {
                    assertSourceElement(s);
                }
            });
        }

        if (activitiesSummary.getEmployments() != null) {
            activitiesSummary.getEmployments().getEmploymentGroups().forEach(e -> {
                for (EmploymentSummary s : e.getActivities()) {
                    assertSourceElement(s);
                }
            });
        }

        if (activitiesSummary.getFundings() != null) {
            activitiesSummary.getFundings().getFundingGroup().forEach(g -> {
                g.getFundingSummary().forEach(e -> assertSourceElement(e));
            });
        }

        if (activitiesSummary.getWorks() != null) {
            activitiesSummary.getWorks().getWorkGroup().forEach(g -> {
                g.getWorkSummary().forEach(e -> assertSourceElement(e));
            });
        }

        if (activitiesSummary.getPeerReviews() != null) {
            activitiesSummary.getPeerReviews().getPeerReviewGroup().forEach(g -> {
                for (PeerReviewDuplicateGroup pg : g.getPeerReviewGroup()) {
                    pg.getPeerReviewSummary().forEach(e -> assertSourceElement(e));
                }
            });
        }

        if (activitiesSummary.getResearchResources() != null) {
            activitiesSummary.getResearchResources().getResearchResourceGroup().forEach(g -> {
                for (ResearchResourceSummary rs : g.getResearchResourceSummary()) {
                    assertSourceElement(rs);
                }
            });
        }

        assertNotNull(record.getPerson());

        Person person = record.getPerson();
        if (person.getAddresses() != null) {
            person.getAddresses().getAddress().forEach(e -> assertSourceElement(e));
        }

        if (person.getExternalIdentifiers() != null) {
            person.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertSourceElement(e));
        }

        if (person.getKeywords() != null) {
            person.getKeywords().getKeywords().forEach(e -> assertSourceElement(e));
        }

        if (person.getOtherNames() != null) {
            person.getOtherNames().getOtherNames().forEach(e -> assertSourceElement(e));
        }

        if (person.getResearcherUrls() != null) {
            person.getResearcherUrls().getResearcherUrls().forEach(e -> assertSourceElement(e));
        }
    }

    private void assertSourceElement(SourceAware element) {
        if (element.getSource() != null && element.getSource().getSourceOrcid() != null) {
            assertProtocol(element.getSource().getSourceOrcid().getUri());
        }
    }

    private void assertProtocol(String url) {
        assertTrue(url.startsWith("https://"));
    }
}
