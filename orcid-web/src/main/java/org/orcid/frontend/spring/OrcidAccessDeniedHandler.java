package org.orcid.frontend.spring;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;

public class OrcidAccessDeniedHandler extends AccessDeniedHandlerImpl {

    @Resource
    private OrcidUrlManager orcidUrlManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAccessDeniedHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (accessDeniedException != null) {
            if (CsrfException.class.isAssignableFrom(accessDeniedException.getClass())) {
                String path = request.getRequestURL().toString();
                if (path.endsWith("/userStatus.json")) {
                    response.setStatus(HttpServletResponse.SC_RESET_CONTENT);
                    response.getWriter().println("{\"loggedIn\":false}");
                } else {
                    String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                    LOGGER.error("Path: {} Session: {} Message: {}", new Object[] { path, sessionId, accessDeniedException.getMessage() });
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().println("<html><head><title>Oops an error happened!</title></head>");
                    response.getWriter().println("<body>403</body>");
                    response.getWriter().println("</html>");
                }
                return;
            }
        }

        // Check if the current user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            response.sendRedirect(orcidUrlManager.getBaseUrl() + "/404");
            return;
        }

        super.handle(request, response, accessDeniedException);
    }
}
