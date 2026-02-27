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
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_EmailsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmailsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmails(ORCID);
    }

    @Test
    public void testViewEmailsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(emailManagerReadOnly.getEmails(eq(ORCID))).thenReturn(new Emails());
        Response r = serviceDelegator.viewEmails(ORCID);
        Emails element = (Emails) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/email", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }
}
