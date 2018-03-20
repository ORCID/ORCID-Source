package org.orcid.api.common.security.oauth;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.locale.LocaleManager;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

public class RevokeTokenEndpointFilter extends GenericFilterBean {

    @Resource
    private LocaleManager localeManager;

    @Resource
    private AuthenticationProvider clientAuthenticationProvider;
    
    @Resource
    private AuthenticationEntryPoint oauthAuthenticationEntryPoint;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws AuthenticationException, IOException, ServletException {
        String clientId = request.getParameter("client_id");
        
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("RevokeTokenEndpointFilter just supports HTTP requests");
        }
        
        if (clientId != null && (OrcidStringUtils.isValidOrcid(clientId) || OrcidStringUtils.isClientId(clientId))) {
            String clientSecret = request.getParameter("client_secret");
            if (clientSecret == null) {
                clientSecret = "";
            }
            
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId, clientSecret);

            try {
                Authentication auth = clientAuthenticationProvider.authenticate(authRequest);    
                SecurityContextHolder.getContext().setAuthentication(auth);                            
            } catch (AuthenticationException failed) { 
                oauthAuthenticationEntryPoint.commence((HttpServletRequest) request, (HttpServletResponse) response, failed);
                return;
            }
        }

        chain.doFilter(request, response);
    }

}
