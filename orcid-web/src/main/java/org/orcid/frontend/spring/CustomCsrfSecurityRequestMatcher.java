/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.spring;

import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CustomCsrfSecurityRequestMatcher implements RequestMatcher {

    private final String[] pathsToIgnore = { "/userStatus.json" };

    public static Pattern allowedMethods = Pattern.compile("^(GET|TRACE|HEAD|OPTIONS)$");

    @Override
    public boolean matches(HttpServletRequest request) {
        if (allowedMethods.matcher(request.getMethod()).matches()) {
            return false;
        }

        String path = OrcidUrlManager.getPathWithoutContextPath(request);
        return !Arrays.stream(pathsToIgnore).filter(s -> path.startsWith(s)).findFirst().isPresent();        
    }

}
