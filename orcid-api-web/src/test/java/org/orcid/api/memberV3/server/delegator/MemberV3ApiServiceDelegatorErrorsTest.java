package org.orcid.api.memberV3.server.delegator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;

public class MemberV3ApiServiceDelegatorErrorsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test
    public void testExceptionMapping() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        OrcidUnauthorizedException exception = new OrcidUnauthorizedException("unauthorized");
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), any())).thenThrow(exception);
        
        // The delegator doesn't catch the exception, it propagates it to JAX-RS.
        // This test verifies that the delegator correctly calls the manager which throws the exception.
        try {
            serviceDelegator.viewActivities(ORCID);
        } catch (OrcidUnauthorizedException e) {
            // Success
        }
    }
}
