package org.orcid.frontend.spring;

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
                    String headerToken = request.getHeader("x-xsrf-token");
                    String cookieToken = getCookieValue(request, "XSRF-TOKEN");
                    String authCookieToken = getCookieValue(request, "AUTH-XSRF-TOKEN");
                    String jsessionId = getCookieValue(request, "JSESSIONID");
                    LOGGER.error("Path: {} Session: {} Message: {}", new Object[] { path, sessionId, accessDeniedException.getMessage() });
                    LOGGER.warn(
                            "CSRF diagnostics path={} method={} session={} origin={} referer={} x-xsrf-token={} cookie-xsrf={} cookie-auth-xsrf={}",
                            path,
                            request.getMethod(),
                            sessionId,
                            request.getHeader("Origin"),
                            request.getHeader("Referer"),
                            maskToken(headerToken),
                            maskToken(cookieToken),
                            maskToken(authCookieToken)
                    );
                            response.setHeader("X-ORCID-CSRF-DEBUG", "access-denied-handler");
                    response.setHeader("X-ORCID-CSRF-HDR-PRESENT", String.valueOf(headerToken != null && !headerToken.isBlank()));
                    response.setHeader("X-ORCID-CSRF-COOKIE-PRESENT", String.valueOf(cookieToken != null && !cookieToken.isBlank()));
                    response.setHeader("X-ORCID-CSRF-HDR-COOKIE-MATCH", String.valueOf(headerToken != null && headerToken.equals(cookieToken)));
                    response.setHeader("X-ORCID-JSESSIONID-PRESENT", String.valueOf(jsessionId != null && !jsessionId.isBlank()));
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

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "<missing>";
        }
        int visible = Math.min(8, token.length());
        return token.substring(0, visible) + "...(" + token.length() + ")";
    }
}
