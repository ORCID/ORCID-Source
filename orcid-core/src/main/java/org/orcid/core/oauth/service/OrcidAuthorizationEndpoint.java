package org.orcid.core.oauth.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator.OpenIDConnectScopesToIgnore;
import org.orcid.jaxb.model.message.ScopeConstants;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping(value = "/oauth/authorize")
public class OrcidAuthorizationEndpoint extends AuthorizationEndpoint {

    private String redirectUriError = "forward:/oauth/error/redirect-uri-mismatch";
    private String oauthError = "forward:/oauth/error";    
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
        
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
                    @RequestParam Map<String, String> requestParameters, SessionStatus sessionStatus, Principal principal) {
        try {

            modifyRequestParameters(requestParameters);
        } catch (InvalidScopeException ise) {
        	String redirectUri = requestParameters.get("redirect_uri");
        	String redirectUriWithParams = "";
        	if(redirectUri != null) {
        		redirectUriWithParams = redirectUri;
        	}
            redirectUriWithParams += "?error=invalid_scope&error_description=" + ise.getMessage();
            RedirectView rView = new RedirectView(redirectUriWithParams);

            ModelAndView error = new ModelAndView();
            error.setView(rView);
            return error;
        }
        return super.authorize(model, requestParameters, sessionStatus, principal);
    }
    
    private void modifyRequestParameters(Map<String, String> requestParameters) throws InvalidScopeException {
        for (Map.Entry<String, String> entry : requestParameters.entrySet()) {
            requestParameters.put(entry.getKey(), entry.getValue().trim());
        }
        String scopes = requestParameters.get(OAuth2Utils.SCOPE);
        if (scopes != null) {
            requestParameters.put(OAuth2Utils.SCOPE, trimClientCredentialScopes(scopes.trim().replaceAll(" +", " ")));
        }
        // if we have an id_token response_type but no token scope, add it in.
        // this is because spring can't cope without the 'token' response type.
        if (OrcidOauth2Constants.ID_TOKEN.equals(requestParameters.get(OAuth2Utils.RESPONSE_TYPE))
                && requestParameters.get(OAuth2Utils.SCOPE).contains(ScopeConstants.OPENID)) {
            requestParameters.put(OAuth2Utils.RESPONSE_TYPE, "id_token token");
        }
    }

	/**
     * Validate if the given client have the defined scope
     * @param scopes a space or comma separated list of scopes
     * @param clientDetails
     * @param responseType
     * @throws InvalidScopeException in case the given client doesnt have any of the given scopes
     * */
    public void validateScope(String scopes, ClientDetails clientDetails, String responseType) throws InvalidScopeException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(OAuth2Utils.SCOPE, scopes);
        
        //Check the user have permissions to the other scopes
        orcidOAuth2RequestValidator.validateParameters(parameters, clientDetails,responseType);
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
    
    private String trimClientCredentialScopes(String scopes) throws InvalidScopeException {
        String result = scopes;
        for (String scope : OAuth2Utils.parseParameterList(scopes)) {
            if (StringUtils.isNotBlank(scope)) {
                if (OpenIDConnectScopesToIgnore.contains(scope)) {//remove openid connect scopes we do not support.
                    result = result.replaceAll(scope, "");
                    continue; 
                }
                ScopePathType scopeType = null;
                try {
                    scopeType = ScopePathType.fromValue(scope);
                } catch (Exception e) {
                    throw new InvalidScopeException("Invalid scope: " + scope);
                }
                if (scopeType.isClientCreditalScope()) {
                    if (result.contains(ScopePathType.ORCID_PROFILE_CREATE.getContent()))
                        result = result.replaceAll(ScopePathType.ORCID_PROFILE_CREATE.getContent(), "");
                    else if (result.contains(ScopePathType.READ_PUBLIC.getContent()))
                        result = result.replaceAll(ScopePathType.READ_PUBLIC.getContent(), "");
                    else if (result.contains(ScopePathType.WEBHOOK.getContent()))
                        result = result.replaceAll(ScopePathType.WEBHOOK.getContent(), "");
                }
            }
        }

        return result;
    }

    public OrcidOAuth2RequestValidator getOrcidOAuth2RequestValidator() {
        return orcidOAuth2RequestValidator;
    }

    public void setOrcidOAuth2RequestValidator(OrcidOAuth2RequestValidator orcidOAuth2RequestValidator) {
        this.orcidOAuth2RequestValidator = orcidOAuth2RequestValidator;
    }            
}
