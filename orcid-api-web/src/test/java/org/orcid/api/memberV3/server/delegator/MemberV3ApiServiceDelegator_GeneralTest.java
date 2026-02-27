package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.StatusManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;

public class MemberV3ApiServiceDelegator_GeneralTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test
    public void testViewStatusSimple() {
        Map<String, Boolean> statusMap = Collections.singletonMap(StatusManager.TOMCAT_UP, true);
        when(statusManager.createStatusMapSimple()).thenReturn(statusMap);
        Response r = serviceDelegator.viewStatusSimple();
        assertNotNull(r);
        assertEquals(statusMap, r.getEntity());
    }

    @Test
    public void testViewStatus() {
        Map<String, Boolean> statusMap = Collections.singletonMap(StatusManager.OVERALL_OK, true);
        when(statusManager.createStatusMap()).thenReturn(statusMap);
        Response r = serviceDelegator.viewStatus();
        assertNotNull(r);
        assertEquals(statusMap, r.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewActivitiesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewActivities(ORCID);
    }
}
