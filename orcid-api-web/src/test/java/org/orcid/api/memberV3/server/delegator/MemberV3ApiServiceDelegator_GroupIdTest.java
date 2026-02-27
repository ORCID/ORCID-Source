package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;

public class MemberV3ApiServiceDelegator_GroupIdTest extends MemberV3ApiServiceDelegatorMockTest {

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewGroupIdRecordWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewGroupIdRecord(10L);
    }

    @Test
    public void testViewGroupIdRecordReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        GroupIdRecord record = new GroupIdRecord();
        record.setPutCode(10L);
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(eq(10L))).thenReturn(record);
        
        Response r = serviceDelegator.viewGroupIdRecord(10L);
        GroupIdRecord element = (GroupIdRecord) r.getEntity();
        assertNotNull(element);
        assertEquals(Long.valueOf(10L), element.getPutCode());
    }
}
