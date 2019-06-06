package org.orcid.frontend.spring.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Service;

@Service("orcidRequestCache")
public class OrcidRequestCache extends HttpSessionRequestCache {
    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        String requestUrl = OrcidUrlManager.getPathWithoutContextPath(request);

        // Save the request just in case it matches the SAVED_REQUEST_PATTERN
        // pattern
        if (OrcidUrlManager.SAVED_REQUEST_PATTERN.matcher(requestUrl).find()) {
            super.saveRequest(request, response);
        }
    }
}
