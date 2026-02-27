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
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_ExternalIdentifiersTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewExternalIdentifiersWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifiers(ORCID);
    }

    @Test
    public void testViewExternalIdentifiersReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(eq(ORCID))).thenReturn(new PersonExternalIdentifiers());
        Response r = serviceDelegator.viewExternalIdentifiers(ORCID);
        PersonExternalIdentifiers element = (PersonExternalIdentifiers) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewExternalIdentifierReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setPutCode(9L);
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(eq(ORCID), eq(9L))).thenReturn(extId);
        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 9L);
        PersonExternalIdentifier element = (PersonExternalIdentifier) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/external-identifiers/9", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }
}
