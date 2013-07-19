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

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 29/03/2012
 */
public class OrcidWebUtils {

    public static URI getServerUriWithContextPath(HttpServletRequest request) {
        try {
            return new URI(getServerStringWithContextPath(request));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Cannot create a URI from request");
        }
    }

    public static URI getServerUri(HttpServletRequest request) {
        try {
            return new URI(getServerString(request));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Cannot create a URI from request");
        }
    }

    public static String getServerStringWithContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String serverString = getServerString(request);
        return contextPath == null ? serverString : serverString + contextPath;
    }

    public static String getServerString(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String scheme = forwardedProto != null ? forwardedProto : request.getScheme();
        String forwardedPort = request.getHeader("X-Forwarded-Port");
        int serverPort = forwardedPort != null ? Integer.valueOf(forwardedPort) : request.getServerPort();
        String serverName = request.getServerName();

        StringBuilder sb = new StringBuilder();

        sb.append(scheme);
        sb.append("://");
        sb.append(serverName);
        sb.append((("https".equalsIgnoreCase(scheme) && serverPort == 443) || ("http".equalsIgnoreCase(scheme) && serverPort == 80)) ? "" : ":" + serverPort);
        return sb.toString();
    }
}
