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
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
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
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml",
            "/data/OrgAffiliationEntityData.xml");

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
    public void testViewActitivies() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4446");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        // Check works
        assertNotNull(summary.getWorks());
        assertEquals(3, summary.getWorks().getWorkGroup().size());
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(),
                anyOf(is("5"), is("6"), is("7")));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(),
                anyOf(is("5"), is("6"), is("7")));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(),
                anyOf(is("5"), is("6"), is("7")));
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));

        // Check fundings
        assertNotNull(summary.getFundings());
        assertEquals(2, summary.getFundings().getFundingGroup().size());
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode(),
                anyOf(is("4"), is("5")));
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Private Funding"), is("Public Funding")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getPutCode(),
                anyOf(is("4"), is("5")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Private Funding"), is("Limited Funding")));

        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(2, summary.getEducations().getSummaries().size());
        assertThat(summary.getEducations().getSummaries().get(0).getPutCode(), anyOf(is("6"), is("7")));
        assertThat(summary.getEducations().getSummaries().get(0).getDepartmentName(), anyOf(is("Education Dept # 1"), is("Education Dept # 2")));

        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(2, summary.getEmployments().getSummaries().size());
        assertThat(summary.getEmployments().getSummaries().get(0).getPutCode(), anyOf(is("5"), is("8")));
        assertThat(summary.getEmployments().getSummaries().get(0).getDepartmentName(), anyOf(is("Employment Dept # 1"), is("Employment Dept # 2")));
    }

    @Test
    public void testViewWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", "5");
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article A", work.getWorkTitle().getTitle().getContent());
        assertEquals("5", work.getPutCode());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
    }

    @Test
    public void testViewFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", "4");
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals("Private Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", "6");
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals("Education Dept # 1", education.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
    }

    @Test
    public void testViewEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", "5");
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }
}
