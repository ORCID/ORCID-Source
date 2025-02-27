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
import org.orcid.frontend.util.RequestInfoFormLocalCache;
import org.orcid.frontend.web.controllers.BaseControllerUtil;
import org.orcid.frontend.web.controllers.helper.OauthHelper;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * 
 * @author rcpeters
 * 
 */
public class OAuthAuthorizeNotSignedInFilter implements Filter {    
    
    private BaseControllerUtil baseControllerUtil = new BaseControllerUtil();
    
    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private OauthHelper oauthHelper;

    @Resource
    private RequestInfoFormLocalCache requestInfoFormLocalCache;
    
    @Override
    public void destroy() {
        // Do nothing
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (OrcidUrlManager.getPathWithoutContextPath(request).equals("/oauth/authorize")) {
            HttpServletResponse response = (HttpServletResponse) res;
            HttpSession session = request.getSession();
            String queryString = request.getQueryString();
            boolean forceLogin = false;
            
            if (OrcidOauth2Constants.PROMPT_LOGIN.equals(request.getParameter(OrcidOauth2Constants.PROMPT))) {
                //remove prompt param later on to prevent loop
                forceLogin = true;
            } 
            
            //users not logged in must sign in
            SecurityContext sci = null;
            if (session != null)
                sci = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
            if (forceLogin || baseControllerUtil.getCurrentUser(sci) == null) {
                if (session != null) {
                    new HttpSessionRequestCache().saveRequest(request, response);
                    RequestInfoForm rif = oauthHelper.generateRequestInfoForm(request.getQueryString());

                    // Store the request info form in the cache
                    requestInfoFormLocalCache.put(request.getSession().getId(), rif);

                    request.getSession().setAttribute(OrcidOauth2Constants.OAUTH_QUERY_STRING, queryString);
                }                
                response.sendRedirect(orcidUrlManager.getBaseUrl() + "/signin?oauth&" + queryString);                                             
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
