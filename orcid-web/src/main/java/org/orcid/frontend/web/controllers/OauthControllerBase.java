package org.orcid.frontend.web.controllers;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.frontend.spring.OrcidWebAuthenticationDetails;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.util.OAuth2Utils;

@Deprecated
public class OauthControllerBase extends BaseController {      
    protected static String REDIRECT_URI_ERROR = "/oauth/error/redirect-uri-mismatch?client_id={0}";
            
    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    protected OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;

    @Resource
    protected ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    protected AuthenticationManager authenticationManager;

    @Resource
    protected OrcidAuthorizationEndpoint authorizationEndpoint;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;
    
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public OrcidAuthorizationEndpoint getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(OrcidAuthorizationEndpoint authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }
    
    protected void fillOauthParams(RequestInfoForm requestInfoForm, Map<String, String> params, Map<String, String> approvalParams, boolean userEnabledPersistentTokens, boolean allowEmailAccess) {
        if (requestInfoForm.containsEmailReadPrivateScope() && !allowEmailAccess) {
            requestInfoForm.removeEmailReadPrivateScope();
        }
        
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString())) {
            params.put(OrcidOauth2Constants.SCOPE_PARAM, requestInfoForm.getScopesAsString());
        }
        
        params.put(OrcidOauth2Constants.TOKEN_VERSION, OrcidOauth2Constants.PERSISTENT_TOKEN);                    
        params.put(OrcidOauth2Constants.CLIENT_ID_PARAM, requestInfoForm.getClientId());
        
        // Redirect URI
        if (!PojoUtil.isEmpty(requestInfoForm.getRedirectUrl())) {
            params.put(OrcidOauth2Constants.REDIRECT_URI_PARAM, requestInfoForm.getRedirectUrl());
        } else {
            params.put(OrcidOauth2Constants.REDIRECT_URI_PARAM, new String());
        }
        
        // Response type
        if (!PojoUtil.isEmpty(requestInfoForm.getResponseType())) {
            params.put(OrcidOauth2Constants.RESPONSE_TYPE_PARAM, requestInfoForm.getResponseType());
        }
        // State param
        if (!PojoUtil.isEmpty(requestInfoForm.getStateParam())) {
            params.put(OrcidOauth2Constants.STATE_PARAM, requestInfoForm.getStateParam());
        }
        
        // Set approval params               
        params.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
        approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
        
        // Set persistent token flag
        if(requestInfoForm.getClientHavePersistentTokens() && userEnabledPersistentTokens) {
            params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "true");
        } else {
            params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "false");
        }
        
        //OpenID connect
        if (!PojoUtil.isEmpty(requestInfoForm.getNonce())){
            params.put(OrcidOauth2Constants.NONCE, requestInfoForm.getNonce());
        }
    }
    
    /**
     * Builds the redirect uri string to use when the user deny the request
     * 
     * @param redirectUri
     *            Redirect uri
     * @return the redirect uri string with the deny params
     */
    protected String buildDenyRedirectUri(String redirectUri, String stateParam) {
        if (!PojoUtil.isEmpty(redirectUri)) {
            if (redirectUri.contains("?")) {
                redirectUri = redirectUri.concat("&error=access_denied&error_description=User denied access");
            } else {
                redirectUri = redirectUri.concat("?error=access_denied&error_description=User denied access");
            }
        }
        if (!PojoUtil.isEmpty(stateParam))
            redirectUri += "&state=" + stateParam;
        return redirectUri;
    }

    protected void copy(Map<String, String[]> savedParams, Map<String, String> params) {
        if (savedParams != null && !savedParams.isEmpty()) {
            for (String key : savedParams.keySet()) {
                String[] values = savedParams.get(key);
                if (values != null && values.length > 0)
                    params.put(key, values[0]);
            }
        }
    }

    /*****************************
     * Authenticate user methods
     ****************************/
    protected Authentication authenticateUser(HttpServletRequest request, String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        token.setDetails(new OrcidWebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    /**
     * Checks if the client has the persistent tokens enabled
     * 
     * @return true if the persistent tokens are enabled for that client
     * @throws IllegalArgumentException
     */
    protected boolean hasPersistenTokensEnabled(String clientId) throws IllegalArgumentException {
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        if (clientDetails == null)
            throw new IllegalArgumentException(getMessage("web.orcid.oauth_invalid_client.exception"));
        return clientDetails.isPersistentTokensEnabled();
    }
    
    protected String removeQueryStringParams(String queryString, String... params) {
        for (String param : params) {
            String keyValue = param + "=[^&]*?";
            queryString = queryString.replaceAll("(&" + keyValue + "(?=(&|$))|^" + keyValue + "(&|$))", "");
        }
        return queryString;
    }
}
