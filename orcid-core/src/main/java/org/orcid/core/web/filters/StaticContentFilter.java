package org.orcid.core.web.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.utils.ReleaseNameUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class StaticContentFilter extends OncePerRequestFilter {

    private static final Pattern versionPattern = Pattern.compile("/static/([^/]+)/.+");

    private static final String currentReleaseVersion = ReleaseNameUtils.getReleaseName();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        Matcher matcher = versionPattern.matcher(requestUri);
        if (matcher.find()) {
            String releaseVersion = matcher.group(1);
            if (!currentReleaseVersion.equals(releaseVersion)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("<html><head><title>Oops an error happened!</title></head>");
                response.getWriter().println("<body>404</body>");
                response.getWriter().println("</html>");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
