package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_BiogrphyTest extends MemberV3ApiServiceDelegatorMockTest {

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBiographyWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewBiography(ORCID);
    }

    @Test
    public void testViewBiographyReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(biographyManagerReadOnly.getPublicBiography(eq(ORCID))).thenReturn(new Biography());
        Response r = serviceDelegator.viewBiography(ORCID);
        Biography element = (Biography) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/biography", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testReadPublicScope_Biography() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        when(biographyManagerReadOnly.getPublicBiography(eq(ORCID))).thenReturn(new Biography());
        Response r = serviceDelegator.viewBiography(ORCID);
        assertNotNull(r);
        assertEquals(Biography.class.getName(), r.getEntity().getClass().getName());

        try {
            // Bio for 0000-0000-0000-0002 should be limited
            String otherOrcid = "0000-0000-0000-0002";
            doThrow(new OrcidUnauthorizedException("unauthorized")).when(orcidSecurityManager).checkClientAccessAndScopes(eq(otherOrcid), eq(ScopePathType.READ_PUBLIC));
            r = serviceDelegator.viewBiography(otherOrcid);
            fail();
        } catch (OrcidUnauthorizedException e) {

        }
    }
}
