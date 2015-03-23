package org.orcid.api.memberV2.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkType;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.test.DBUnitTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml",
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml"); 
    
    
    @Resource(name = "memberV2ApiServiceDelegator")
    private MemberV2ApiServiceDelegator serviceDelegator;
    
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
    @SuppressWarnings("unchecked")
    public void testFindWorksDetails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4446");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary)response.getEntity();
        assertNotNull(summary);
        //Check works
        assertNotNull(summary.getWorks());
        assertEquals(3, summary.getWorks().getWorkGroup().size());
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));        
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));        
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
        
        //Check fundings
        assertNotNull(summary.getFundings());
        assertEquals(2, summary.getFundings().getFundingGroup().size());
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode(), anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5")));
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Private Funding"), is("Limited Funding")));        
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getPutCode(), anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Private Funding"), is("Limited Funding")));
        
        //Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(1, summary.getEducations().getSummaries().size());
        assertEquals("/4444-4444-4444-4446/education/6", summary.getEducations().getSummaries().get(0).getPutCode());
        assertEquals("Education Dept # 1", summary.getEducations().getSummaries().get(0).getDepartmentName());
        
        //Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(1, summary.getEmployments().getSummaries().size());
        assertEquals("/4444-4444-4444-4446/employment/5", summary.getEmployments().getSummaries().get(0).getPutCode());
        assertEquals("Employment Dept # 1", summary.getEmployments().getSummaries().get(0).getDepartmentName());
    }
    
    @Test
    public void testViewWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", "5");
        assertNotNull(response);
        Work work = (Work)response.getEntity();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article A", work.getWorkTitle().getTitle().getContent());
        assertEquals("/4444-4444-4444-4446/work/5", work.getPutCode());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
    }
    
    @Test
    public void testViewFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", "4");
        assertNotNull(response);
        Funding funding = (Funding)response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals("Private Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
    }
}








