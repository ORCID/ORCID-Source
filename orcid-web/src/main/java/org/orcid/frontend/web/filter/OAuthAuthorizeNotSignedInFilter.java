package org.orcid.frontend.web.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.frontend.web.controllers.BaseControllerUtil;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

/**
 * 
 * @author rcpeters
 * 
 */
public class OAuthAuthorizeNotSignedInFilter implements Filter {    
    
    BaseControllerUtil baseControllerUtil = new BaseControllerUtil();
    
    @Resource
    protected OrcidUrlManager orcidUrlManager;

    @Override
    public void destroy() {
        // Do nothing
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (OrcidUrlManager.getPathWithoutContextPath(request).equals("/oauth/authorize")) {
            HttpServletResponse response = (HttpServletResponse) res;
            HttpSession session = request.getSession();
            SecurityContext sci = null;
            if (session != null)
                sci = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
            if (baseControllerUtil.getCurrentUser(sci) == null) {
                String queryString = request.getQueryString();
                String redirectUrl = orcidUrlManager.getBaseUrl() + "/signin?oauth&" + queryString;
                if(session != null) {
                    session.setAttribute("OAUTH_REDIRECT_URL", redirectUrl);
                
                    HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
                    requestCache.saveRequest(request, response);
                    SavedRequest savedRequest = requestCache.getRequest(request, response);                    
                    session.setAttribute(OrcidOauth2Constants.ORIGINAL_OAUTH_URL, savedRequest.getRedirectUrl());                    
                }
                response.sendRedirect(redirectUrl);                                             
                return;
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Do nothing
    }

}
