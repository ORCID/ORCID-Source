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
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_ReadPersonTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPerson(ORCID);
    }

    @Test
    public void testViewPersonReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(personDetailsManagerReadOnly.getPersonDetails(eq(ORCID), anyBoolean())).thenReturn(new Person());
        
        Response r = serviceDelegator.viewPerson(ORCID);
        Person element = (Person) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/person", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }
}
