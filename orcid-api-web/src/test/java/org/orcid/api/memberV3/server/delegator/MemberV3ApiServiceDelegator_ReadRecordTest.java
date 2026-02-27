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
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_ReadRecordTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewRecordWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewRecord(ORCID);
    }

    @Test
    public void testViewRecordReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(recordManagerReadOnly.getRecord(eq(ORCID), anyBoolean())).thenReturn(new Record());
        
        Response r = serviceDelegator.viewRecord(ORCID);
        Record element = (Record) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/record", element.getPath());
    }
}
