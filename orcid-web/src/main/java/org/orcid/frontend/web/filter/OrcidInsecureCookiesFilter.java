package org.orcid.frontend.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class OrcidInsecureCookiesFilter extends OncePerRequestFilter {

    private static final String ORCID_TOKEN_COOKIE_NAME = "orcid_token";
    private static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    private static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final String LOCALE_COOKIE_NAME = "locale_v3";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        // Update cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (ORCID_TOKEN_COOKIE_NAME.equals(cookieName) || JSESSIONID_COOKIE_NAME.equals(cookieName) || CSRF_COOKIE_NAME.equals(cookieName)
                        || LOCALE_COOKIE_NAME.equals(cookieName)) {
                    if (!cookie.getSecure()) {
                        cookie.setMaxAge(0);
                        cookie.setValue(StringUtils.EMPTY);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
