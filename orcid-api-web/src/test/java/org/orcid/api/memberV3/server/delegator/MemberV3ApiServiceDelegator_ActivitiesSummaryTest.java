package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_ActivitiesSummaryTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewActivitiesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewActivities(ORCID);
    }

    @Test
    public void testViewActivitiesReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        ActivitiesSummary summary = new ActivitiesSummary();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Response r = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary element = (ActivitiesSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/activities", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewActivities() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ActivitiesSummary summary = new ActivitiesSummary();
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Response r = serviceDelegator.viewActivities(ORCID);
        assertNotNull(r);
        ActivitiesSummary result = (ActivitiesSummary) r.getEntity();
        assertNotNull(result);
        assertEquals("/0000-0000-0000-0003/activities", result.getPath());
    }
}
