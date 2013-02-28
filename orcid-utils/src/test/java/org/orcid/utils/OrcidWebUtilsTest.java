/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.utils;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.net.URI;

import static junit.framework.Assert.assertEquals;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 29/03/2012
 */
public class OrcidWebUtilsTest {

    @Test
    public void testGetServerUriWithContextPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(443);
        URI uri = OrcidWebUtils.getServerUriWithContextPath(request);
        assertEquals("https://orcid.org/orcid", uri.toString());

        request.setScheme("https");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(8443);
        uri = OrcidWebUtils.getServerUriWithContextPath(request);
        assertEquals("https://orcid.org:8443/orcid", uri.toString());
    }

    @Test
    public void testGetServerUri() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(80);
        URI uri = OrcidWebUtils.getServerUri(request);
        assertEquals("http://orcid.org", uri.toString());

        request.setScheme("http");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(8080);
        uri = OrcidWebUtils.getServerUri(request);
        assertEquals("http://orcid.org:8080", uri.toString());
    }

    @Test
    public void testGetServerStringWithContextPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(443);
        String uri = OrcidWebUtils.getServerStringWithContextPath(request);
        assertEquals("http://orcid.org:443/orcid", uri);

        request.setScheme("http");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(8443);
        uri = OrcidWebUtils.getServerStringWithContextPath(request);
        assertEquals("http://orcid.org:8443/orcid", uri);
    }

    @Test
    public void testGetServerString() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(443);
        String uri = OrcidWebUtils.getServerString(request);
        assertEquals("https://orcid.org", uri);

        request.setScheme("https");
        request.setServerName("orcid.org");
        request.setContextPath("/orcid");
        request.setServerPort(8443);
        uri = OrcidWebUtils.getServerString(request);
        assertEquals("https://orcid.org:8443", uri);
    }
}
