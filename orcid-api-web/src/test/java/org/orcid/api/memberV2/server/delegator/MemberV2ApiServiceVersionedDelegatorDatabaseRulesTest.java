package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.DatabaseTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

/**
 * The rules of the versioned member v2 delegators that only the real beans can prove.
 *
 * <p>
 * {@code MemberV2ApiServiceVersionedDelegatorTest} runs on mocks, with both version
 * converter chains stubbed as pass-throughs, so it cannot see what the real 2.0 to 2.1
 * conversion does to a work. The converter copies every bean property, which is how a
 * field the inner delegator removed could come back on one version and not the other.
 * That is checked here, against fixtures, through both versioned beans.
 */
@Category(DatabaseTest.class)
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-api-web-context.xml" })
public class MemberV2ApiServiceVersionedDelegatorDatabaseRulesTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml",
            "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    private static final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV2ApiServiceDelegatorV2_0")
    private MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @Resource(name = "memberV2ApiServiceDelegatorV2_1")
    private MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator_v2_1;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * The contributor email is write-only. The versioned beans run every work through the
     * 2.0 <-> 2.1 converter, which copies every bean property, so check it stays out on both.
     */
    @Test
    public void testViewWorkOmitsContributorEmailOnV2_0AndV2_1() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        assertNoContributorEmail((Work) serviceDelegator.viewWork(ORCID, 11L).getEntity());
        assertNoContributorEmail((Work) serviceDelegator_v2_1.viewWork(ORCID, 11L).getEntity());
        assertNoContributorEmail((Work) ((WorkBulk) serviceDelegator.viewBulkWorks(ORCID, "11").getEntity()).getBulk().get(0));
        assertNoContributorEmail((Work) ((WorkBulk) serviceDelegator_v2_1.viewBulkWorks(ORCID, "11").getEntity()).getBulk().get(0));
    }

    private void assertNoContributorEmail(Work work) {
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertEquals(1, work.getWorkContributors().getContributor().size());
        assertEquals("0000-0000-0000-0000", work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertNull(work.getWorkContributors().getContributor().get(0).getContributorEmail());
    }
}
