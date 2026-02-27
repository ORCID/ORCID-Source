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
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_KeywordsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewKeywordsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeywords(ORCID);
    }

    @Test
    public void testViewKeywordsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        when(profileKeywordManagerReadOnly.getKeywords(eq(ORCID))).thenReturn(new Keywords());
        Response r = serviceDelegator.viewKeywords(ORCID);
        Keywords element = (Keywords) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewKeywordReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Keyword keyword = new Keyword();
        keyword.setPutCode(9L);
        when(profileKeywordManagerReadOnly.getKeyword(eq(ORCID), eq(9L))).thenReturn(keyword);
        Response r = serviceDelegator.viewKeyword(ORCID, 9L);
        Keyword element = (Keyword) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/keywords/9", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }
}
