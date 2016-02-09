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
package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.OauthRegistrationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.ScopeInfoForm;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.ResponseBody;

public class OauthControllerBase extends BaseController {
    protected Pattern clientIdPattern = Pattern.compile("client_id=([^&]*)");
    protected Pattern scopesPattern = Pattern.compile("scope=([^&]*)");
    private Pattern redirectUriPattern = Pattern.compile("redirect_uri=([^&]*)");
    private Pattern responseTypePattern = Pattern.compile("response_type=([^&]*)");
    private Pattern stateParamPattern = Pattern.compile("state=([^&]*)");
    protected static String PUBLIC_MEMBER_NAME = "PubApp";
    protected static String REDIRECT_URI_ERROR = "/oauth/error/redirect-uri-mismatch?client_id={0}";
    protected static String REQUEST_INFO_FORM = "requestInfoForm";

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

    protected @ResponseBody RequestInfoForm generateAndSaveRequestInfoForm(HttpServletRequest request, String requestUrl) throws UnsupportedEncodingException {
        String clientId = "";
        String scopesString = "";        
        String redirectUri = "";
        
        if (!PojoUtil.isEmpty(requestUrl)) {
            Matcher matcher = clientIdPattern.matcher(requestUrl);
            if (matcher.find()) {
                clientId = matcher.group(1);
            }
            Matcher scopeMatcher = scopesPattern.matcher(requestUrl);
            if (scopeMatcher.find()) {
                String scopes = scopeMatcher.group(1);
                scopesString = URLDecoder.decode(scopes, "UTF-8").trim();
                scopesString = scopesString.replaceAll(" +", " ");                
            }
            Matcher redirectUriMatcher = redirectUriPattern.matcher(requestUrl);
            if (redirectUriMatcher.find()) {
                try {
                    redirectUri = URLDecoder.decode(redirectUriMatcher.group(1), "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                }
            }                        
        } 

        return generateAndSaveRequestInfoForm(request, clientId, scopesString, redirectUri);
    }
    
    protected @ResponseBody RequestInfoForm generateAndSaveRequestInfoForm(HttpServletRequest request, String clientId, String scopesString, String redirectUri) throws UnsupportedEncodingException {        
        RequestInfoForm infoForm = new RequestInfoForm();
        Set<ScopePathType> scopes = new HashSet<ScopePathType>();                
        
        if (!PojoUtil.isEmpty(clientId) && !PojoUtil.isEmpty(scopesString)) {
            scopesString = URLDecoder.decode(scopesString, "UTF-8").trim();
            scopesString = scopesString.replaceAll(" +", " ");
            scopes = ScopePathType.getScopesFromSpaceSeparatedString(scopesString);            
        } else {
            throw new InvalidRequestException("Unable to find parameters");
        }

        for (ScopePathType theScope : scopes) {
            ScopeInfoForm scopeInfoForm = new ScopeInfoForm();
            scopeInfoForm.setValue(theScope.value());
            scopeInfoForm.setName(theScope.name());
            scopeInfoForm.setDescription(getMessage(ScopePathType.class.getName() + '.' + theScope.name()));
            scopeInfoForm.setLongDescription(getMessage(ScopePathType.class.getName() + '.' + theScope.name() + ".longDesc"));
            infoForm.getScopes().add(scopeInfoForm);
        }

        // Check if the client has persistent tokens enabled
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        if (clientDetails.isPersistentTokensEnabled()) {
            infoForm.setClientHavePersistentTokens(true);
        }

        // If client details is ok, continue
        String clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
        String clientDescription = clientDetails.getClientDescription() == null ? "" : clientDetails.getClientDescription();
        String memberName = "";
        
        // If client type is null it means it is a public client
        if (clientDetails.getClientType() == null) {
            memberName = PUBLIC_MEMBER_NAME;
        } else if (!PojoUtil.isEmpty(clientDetails.getGroupProfileId())) {
            ProfileEntity groupProfile = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
            memberName = groupProfile.getCreditName();
        }
        // If the group name is empty, use the same as the client
        // name, since it should be a SSO user
        if (StringUtils.isBlank(memberName)) {
            memberName = clientName;
        }
        
        infoForm.setClientId(clientId);
        infoForm.setClientName(clientName);
        infoForm.setMemberName(memberName);
        infoForm.setRedirectUrl(redirectUri);
        
        // Save the request info form in the session
        request.getSession().setAttribute(REQUEST_INFO_FORM, infoForm);

        return infoForm;
    }

    /**
     * Fill the for with the state param and the client and member names.
     * 
     * @param form
     * @param request
     * @param response
     */
    protected void fillOauthFormWithRequestInformation(OauthForm form, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String[]> requestParams = new HashMap<String, String[]>();
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

        // Get the params from the saved request
        if (savedRequest != null) {
            requestParams = savedRequest.getParameterMap();
        } else {
            // If there are no saved request, get them from the session
            AuthorizationRequest authorizationRequest = (AuthorizationRequest) request.getSession().getAttribute("authorizationRequest");
            if (authorizationRequest != null) {
                Map<String, String> authRequestParams = new HashMap<String, String>(authorizationRequest.getRequestParameters());
                for (String param : authRequestParams.keySet()) {
                    requestParams.put(param, new String[] { authRequestParams.get(param) });
                }
            }
        }

        if (requestParams == null || requestParams.isEmpty()) {
            throw new InvalidRequestException("Unable to find parameters");
        }

        // Save state param
        if (requestParams.containsKey(OrcidOauth2Constants.STATE_PARAM)) {
            if (requestParams.get(OrcidOauth2Constants.STATE_PARAM).length > 0)
                form.setStateParam(Text.valueOf(requestParams.get(OrcidOauth2Constants.STATE_PARAM)[0]));
        }

        // Get and set client info
        if (!requestParams.containsKey(OrcidOauth2Constants.CLIENT_ID_PARAM)) {
            throw new InvalidRequestException("Empty client id");
        }
        String clientId = requestParams.get(OrcidOauth2Constants.CLIENT_ID_PARAM)[0];
        try {
            clientId = URLDecoder.decode(clientId, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            throw new InvalidRequestException("Unable to parse client id: " + e);
        }

        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        try {
            orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        } catch (LockedException le) {
            throw new InvalidRequestException("Client " + clientId + " is locked");
        }

        String clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
        String memberName = null;
        // If it is the
        if (ClientType.PUBLIC_CLIENT.equals(clientDetails.getClientType())) {
            memberName = PUBLIC_MEMBER_NAME;
        } else {
            ProfileEntity groupProfile = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
            memberName = groupProfile.getCreditName();
        }

        form.setClientName(Text.valueOf(clientName));
        form.setMemberName(Text.valueOf(memberName));
        form.setClientId(Text.valueOf(clientId));

        // If it is a new registration, set the referred by flag
        if (form instanceof OauthRegistrationForm) {
            ((OauthRegistrationForm) form).setReferredBy(Text.valueOf(clientId));
        }
    }

    /**
     * Set the needed params for the Oauth request
     * 
     * @param savedRequest
     * @param form
     * @param params
     * @param approvalParams
     * @param justRegistred
     */
    protected void setOauthParams(OauthForm form, Map<String, String> params, Map<String, String> approvalParams, String scopes, String redirectUri, boolean justRegistred) {
        // Scope
        if (!PojoUtil.isEmpty(scopes)) {
            params.put(OrcidOauth2Constants.SCOPE_PARAM, scopes);
        }
        // Then, put the custom authorization params
        // Token version
        params.put(OrcidOauth2Constants.TOKEN_VERSION, OrcidOauth2Constants.PERSISTENT_TOKEN);
        // Client ID
        params.put(OrcidOauth2Constants.CLIENT_ID_PARAM, form.getClientId().getValue());
        // Redirect URI
        if (!PojoUtil.isEmpty(redirectUri)) {
            params.put(OrcidOauth2Constants.REDIRECT_URI_PARAM, redirectUri);
        } else {
            params.put(OrcidOauth2Constants.REDIRECT_URI_PARAM, new String());
        }
        // Response type
        if (!PojoUtil.isEmpty(form.getResponseType())) {
            params.put(OrcidOauth2Constants.RESPONSE_TYPE_PARAM, form.getResponseType().getValue());
        }
        // State param
        if (!PojoUtil.isEmpty(form.getStateParam())) {
            params.put(OrcidOauth2Constants.STATE_PARAM, form.getStateParam().getValue());
        }
        // Approved
        if (justRegistred) {
            if (form.getApproved()) {
                params.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
                approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
            } else {
                params.put(OAuth2Utils.USER_OAUTH_APPROVAL, "false");
                approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "false");
            }
        } else {
            params.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
            approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
        }
        // Set persistent tokens flag
        params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "false");
        if (hasPersistenTokensEnabled(form.getClientId().getValue())) {
            // Then check if the client granted the persistent token
            if (form.getPersistentTokenEnabled()) {
                params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "true");
            }
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
    protected Authentication authenticateUser(HttpServletRequest request, OauthAuthorizeForm form) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(form.getUserName().getValue(), form.getPassword().getValue());
        token.setDetails(new WebAuthenticationDetails(request));
        return authenticateUser(token);
    }

    protected Authentication authenticateUser(HttpServletRequest request, String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        token.setDetails(new WebAuthenticationDetails(request));
        return authenticateUser(token);
    }

    protected Authentication authenticateUser(UsernamePasswordAuthenticationToken token) {
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
}
