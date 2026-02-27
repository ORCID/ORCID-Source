package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_OtherNamesTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewOtherNamesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherNames(ORCID);
    }

    @Test
    public void testViewOtherNamesReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(otherNameManagerReadOnly.getOtherNames(eq(ORCID))).thenReturn(new OtherNames());
        Response r = serviceDelegator.viewOtherNames(ORCID);
        OtherNames element = (OtherNames) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/other-names", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewOtherNameReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        OtherName otherName = new OtherName();
        otherName.setPutCode(9L);
        when(otherNameManagerReadOnly.getOtherName(eq(ORCID), eq(9L))).thenReturn(otherName);
        Response r = serviceDelegator.viewOtherName(ORCID, 9L);
        OtherName element = (OtherName) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/other-names/9", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }
}
