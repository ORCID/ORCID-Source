/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.oauth.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

public class OrcidAuthorizationEndpoint extends AuthorizationEndpoint {

    private static String SCOPE = "scope";
    
    private String redirectUriError = "forward:/oauth/error/redirect-uri-mismatch";
    private String oauthError = "forward:/oauth/error";
    
    
    @Override
    @ExceptionHandler(HttpSessionRequiredException.class)
    public ModelAndView handleHttpSessionRequiredException(HttpSessionRequiredException e, ServletWebRequest webRequest) throws Exception {
        return new ModelAndView("redirect:" + buildRedirectUri(webRequest).toString());
    }

    @Override
    @ExceptionHandler(OAuth2Exception.class)
    public ModelAndView handleOAuth2Exception(OAuth2Exception e, ServletWebRequest webRequest) throws Exception {
        logger.info("Handling OAuth2 error: " + e.getSummary());
        if (e instanceof RedirectMismatchException) {
            return new ModelAndView(redirectUriError);
        } else if (e instanceof ClientAuthenticationException) {
            return new ModelAndView(oauthError);
        } 
        
        return super.handleOAuth2Exception(e, webRequest);
    }
    
    @Override
    @RequestMapping
    public ModelAndView authorize(Map<String, Object> model,
                    @RequestParam(value = "response_type", required = false, defaultValue = "none") String responseType,
                    @RequestParam Map<String, String> requestParameters, SessionStatus sessionStatus, Principal principal) {
        String scopes = requestParameters.get(AuthorizationRequest.SCOPE);
        scopes = trimClientCredentialScopes(scopes);
        requestParameters.put(AuthorizationRequest.SCOPE, scopes);
        return super.authorize(model, responseType, requestParameters, sessionStatus, principal);
    }
    
    /**
     * Validate if the given client have the defined scope
     * @param scopes a space or comma separated list of scopes
     * @param clientDetails
     * @throws InvalidScopeException in case the given client doesnt have any of the given scopes
     * */
    public void validateScope(String scopes, ClientDetails clientDetails) throws InvalidScopeException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(SCOPE, scopes);
        
        //Check the user have permissions to the other scopes
        getAuthorizationRequestManager().validateParameters(parameters, clientDetails);
    }
    
   
    
    private URI buildRedirectUri(ServletWebRequest webRequest) throws URISyntaxException {
        String[] referers = webRequest.getHeaderValues("referer");
        if (referers != null && referers.length > 0) {
            return new URI(referers[0]);
        }
        String uri = "/session-expired";
        String contextPath = webRequest.getContextPath();
        if (contextPath != null) {
            uri = contextPath + uri;
        }
        return new URI(uri);
    } 
    
    private String trimClientCredentialScopes(String scopes) {
        String result = scopes;
        for (String scope : OAuth2Utils.parseParameterList(scopes)) {
            ScopePathType scopeType = ScopePathType.fromValue(scope);
            if (scopeType.isClientCreditalScope()) {
                if(scopes.contains(ScopePathType.ORCID_PROFILE_CREATE.getContent()))
                    result = scopes.replaceAll(ScopePathType.ORCID_PROFILE_CREATE.getContent(), "");
                else if(scopes.contains(ScopePathType.READ_PUBLIC.getContent()))
                    result = scopes.replaceAll(ScopePathType.READ_PUBLIC.getContent(), "");
                else if(scopes.contains(ScopePathType.WEBHOOK.getContent()))
                    result = scopes.replaceAll(ScopePathType.WEBHOOK.getContent(), "");
            }                           
        }
        
        return result;
    }
}
