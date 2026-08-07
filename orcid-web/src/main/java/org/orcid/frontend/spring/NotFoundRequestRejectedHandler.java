package org.orcid.frontend.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles {@link org.springframework.security.web.firewall.RequestRejectedException}
 * by returning an HTTP 404 response instead of propagating the exception (which
 * would result in a 500 error).
 */
@Component
public class NotFoundRequestRejectedHandler implements RequestRejectedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundRequestRejectedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            org.springframework.security.web.firewall.RequestRejectedException requestRejectedException)
            throws IOException {
        LOGGER.warn("Rejected request for URL '{}': {}", request.getRequestURL(), requestRejectedException.getMessage());
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}

