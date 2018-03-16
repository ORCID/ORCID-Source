package org.orcid.frontend.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.web.context.request.RequestContextHolder;

public class OrcidAccessDeniedHandler extends AccessDeniedHandlerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAccessDeniedHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (accessDeniedException != null) {
            if (CsrfException.class.isAssignableFrom(accessDeniedException.getClass())) {
                String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                String path = request.getRequestURL().toString();
                LOGGER.error("Path: {} Session: {} Message: {}", new Object[] { path, sessionId, accessDeniedException.getMessage() });
                if (path.endsWith("/userStatus.json")) {
                    response.setStatus(HttpServletResponse.SC_RESET_CONTENT);
                    response.getWriter().println("{\"loggedIn\":false}");
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().println("<html><head><title>Oops an error happened!</title></head>");
                    response.getWriter().println("<body>403</body>");
                    response.getWriter().println("</html>");
                }
                return;
            }
        }

        super.handle(request, response, accessDeniedException);
    }
}
