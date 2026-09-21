package org.orcid.api.common.security.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class OrcidBearerTokenFilterTest {

    @Test
    public void resolveUserOrcidUsesUsernameFirst() throws Exception {
        OrcidBearerTokenFilter filter = new OrcidBearerTokenFilter();
        JSONObject tokenInfo = new JSONObject();
        tokenInfo.put("username", "0000-0001-2345-6789");
        tokenInfo.put("orcid", "0000-0009-9999-9999");

        String result = ReflectionTestUtils.invokeMethod(filter, "resolveUserOrcid", tokenInfo);

        assertEquals("0000-0001-2345-6789", result);
    }

    @Test
    public void resolveUserOrcidUsesOrcidWhenUsernameMissing() throws Exception {
        OrcidBearerTokenFilter filter = new OrcidBearerTokenFilter();
        JSONObject tokenInfo = new JSONObject();
        tokenInfo.put("orcid", "0000-0001-2345-6789");

        String result = ReflectionTestUtils.invokeMethod(filter, "resolveUserOrcid", tokenInfo);

        assertEquals("0000-0001-2345-6789", result);
    }

    @Test
    public void resolveUserOrcidUsesSubWhenValidOrcid() throws Exception {
        OrcidBearerTokenFilter filter = new OrcidBearerTokenFilter();
        JSONObject tokenInfo = new JSONObject();
        tokenInfo.put("sub", "0000-0001-2345-6789");

        String result = ReflectionTestUtils.invokeMethod(filter, "resolveUserOrcid", tokenInfo);

        assertEquals("0000-0001-2345-6789", result);
    }

    @Test
    public void resolveUserOrcidIgnoresSubWhenNotOrcid() throws Exception {
        OrcidBearerTokenFilter filter = new OrcidBearerTokenFilter();
        JSONObject tokenInfo = new JSONObject();
        tokenInfo.put("sub", "APP-6RTM54FDADENEKUK");

        String result = ReflectionTestUtils.invokeMethod(filter, "resolveUserOrcid", tokenInfo);

        assertNull(result);
    }
}