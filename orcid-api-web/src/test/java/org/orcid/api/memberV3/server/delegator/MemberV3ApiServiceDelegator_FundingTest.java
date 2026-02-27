package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_FundingTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFundings(ORCID);
    }

    @Test
    public void testViewFundingReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Funding funding = new Funding();
        funding.setPutCode(27L);
        when(profileFundingManagerReadOnly.getFunding(eq(ORCID), eq(27L))).thenReturn(funding);
        Response r = serviceDelegator.viewFunding(ORCID, 27L);
        Funding element = (Funding) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/funding/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewFundingsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<FundingSummary> summaries = new ArrayList<>();
        when(profileFundingManagerReadOnly.getFundingSummaryList(eq(ORCID))).thenReturn(summaries);
        when(profileFundingManagerReadOnly.groupFundings(eq(summaries), anyBoolean())).thenReturn(Collections.emptyList());
        
        Response r = serviceDelegator.viewFundings(ORCID);
        Fundings element = (Fundings) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/fundings", element.getPath());
    }

    @Test
    public void testViewPublicFunding() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Funding funding = createFunding(27L, Visibility.PUBLIC, ORCID, null);
        when(profileFundingManagerReadOnly.getFunding(eq(ORCID), eq(27L))).thenReturn(funding);
        Response response = serviceDelegator.viewFunding(ORCID, 27L);
        assertNotNull(response);
        Funding result = (Funding) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(27L), result.getPutCode());
        assertEquals("/0000-0000-0000-0003/funding/27", result.getPath());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateFundingWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        when(profileFundingManagerReadOnly.getFunding(eq(ORCID), eq(31L))).thenReturn(createFunding(31L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Funding.class), any(ScopePathType.class));
        serviceDelegator.viewFunding(ORCID, 31L);
        fail();
    }

    @Test
    public void testAddFunding() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setFundings(new Fundings());
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Funding saved = createFunding(12345L);
        when(profileFundingManager.createFunding(eq(ORCID), any(Funding.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createFunding(ORCID, createFunding(null));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private Funding createFunding(Long putCode) {
        return createFunding(putCode, Visibility.PUBLIC, null, null);
    }

    private Funding createFunding(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Funding d = new Funding();
        d.setPutCode(putCode);
        d.setVisibility(visibility);
        if (sourceOrcid != null || sourceClientId != null) {
            Source s = new Source();
            if (sourceOrcid != null) s.setSourceOrcid(new SourceOrcid(sourceOrcid));
            if (sourceClientId != null) s.setSourceClientId(new SourceClientId(sourceClientId));
            d.setSource(s);
        }
        return d;
    }
}
