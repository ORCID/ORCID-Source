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
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_ResearcherUrlsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewResearcherUrlsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrls(ORCID);
    }

    @Test
    public void testViewResearcherUrlsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(researcherUrlManagerReadOnly.getResearcherUrls(eq(ORCID))).thenReturn(new ResearcherUrls());
        Response r = serviceDelegator.viewResearcherUrls(ORCID);
        ResearcherUrls element = (ResearcherUrls) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/researcher-urls", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewResearcherUrlReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        ResearcherUrl researcherUrl = new ResearcherUrl();
        researcherUrl.setPutCode(9L);
        when(researcherUrlManagerReadOnly.getResearcherUrl(eq(ORCID), eq(9L))).thenReturn(researcherUrl);
        Response r = serviceDelegator.viewResearcherUrl(ORCID, 9L);
        ResearcherUrl element = (ResearcherUrl) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/researcher-urls/9", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }
}
