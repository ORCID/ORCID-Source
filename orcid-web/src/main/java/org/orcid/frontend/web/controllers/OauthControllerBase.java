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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.ScopeInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.ResponseBody;

public class OauthControllerBase extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthControllerBase.class);    
    protected Pattern clientIdPattern = Pattern.compile("client_id=([^&]*)");
    protected Pattern scopesPattern = Pattern.compile("scope=([^&]*)");
    private Pattern redirectUriPattern = Pattern.compile("redirect_uri=([^&]*)");
    private Pattern responseTypePattern = Pattern.compile("response_type=([^&]*)");
    private Pattern stateParamPattern = Pattern.compile("state=([^&]*)");
    private Pattern orcidPattern = Pattern.compile("(&|\\?)orcid=([^&]*)");    
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

    protected @ResponseBody RequestInfoForm generateRequestInfoForm(HttpServletRequest request) throws UnsupportedEncodingException {
        String clientId = request.getParameter("client_id");
        String scopesString = request.getParameter("scope");
        String redirectUri = request.getParameter("redirect_uri");
        String responseType = request.getParameter("response_type");
        String stateParam = request.getParameter("state");
        String email = request.getParameter("email");
        String orcid = request.getParameter("orcid");
        String givenNames = request.getParameter("given_names");
        String familyNames = request.getParameter("family_names");
        
        return generateRequestInfoForm(clientId, scopesString, redirectUri, responseType, stateParam, email, orcid, givenNames, familyNames);
    }
    
    protected @ResponseBody RequestInfoForm generateRequestInfoForm(String requestUrl) throws UnsupportedEncodingException {
        String clientId = "";
        String scopesString = "";
        String redirectUri = "";
        String responseType = "";
        String stateParam = "";
        String email = "";
        String orcid = "";
        String givenNames = "";
        String familyNames = "";

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

            Matcher responseTypeMatcher = responseTypePattern.matcher(requestUrl);
            if (responseTypeMatcher.find()) {
                try {
                    responseType = URLDecoder.decode(responseTypeMatcher.group(1), "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                }
            }

            Matcher stateParamMatcher = stateParamPattern.matcher(requestUrl);
            if (stateParamMatcher.find()) {
                try {
                    stateParam = URLDecoder.decode(stateParamMatcher.group(1), "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {}
            }
            
            Matcher emailMatcher = RegistrationController.emailPattern.matcher(requestUrl);
            if (emailMatcher.find()) {
                String tempEmail = emailMatcher.group(1);
                try {
                    tempEmail = URLDecoder.decode(tempEmail, "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                }
                if (emailManager.emailExists(tempEmail)) {
                    email = tempEmail;
                }
            }

            Matcher orcidMatcher = orcidPattern.matcher(requestUrl);
            if (orcidMatcher.find()) {
                String tempOrcid = orcidMatcher.group(2);
                try {
                    tempOrcid = URLDecoder.decode(tempOrcid, "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                }
                if (orcidProfileManager.exists(tempOrcid)) {
                    orcid = tempOrcid;
                }
            }
            
            Matcher givenNamesMatcher = RegistrationController.givenNamesPattern.matcher(requestUrl);
            if(givenNamesMatcher.find()) {
                givenNames = URLDecoder.decode(givenNamesMatcher.group(1), "UTF-8").trim();
            }
            
            Matcher familyNamesMatcher = RegistrationController.familyNamesPattern.matcher(requestUrl);
            if(familyNamesMatcher.find()) {
                familyNames = URLDecoder.decode(familyNamesMatcher.group(1), "UTF-8").trim();
            }
            
        }        
        return generateRequestInfoForm(clientId, scopesString, redirectUri, responseType, stateParam, email, orcid, givenNames, familyNames);
    }
    
    private RequestInfoForm generateRequestInfoForm(String clientId, String scopesString, String redirectUri, String responseType, String stateParam, String email, String orcid, String givenNames, String familyNames) throws UnsupportedEncodingException {
        RequestInfoForm infoForm = new RequestInfoForm();
        
        //If the user is logged in 
        String loggedUserOrcid = getEffectiveUserOrcid();
        if(!PojoUtil.isEmpty(loggedUserOrcid)) {
            infoForm.setUserOrcid(loggedUserOrcid);
            
            ProfileEntity profile = profileEntityCacheManager.retrieve(loggedUserOrcid);
            String creditName = "";
            
            RecordNameEntity recordName = profile.getRecordNameEntity();
            if(recordName != null) {
                if (!PojoUtil.isEmpty(profile.getRecordNameEntity().getCreditName())) {
                    creditName = profile.getRecordNameEntity().getCreditName();
                } else {
                    creditName = PojoUtil.isEmpty(profile.getRecordNameEntity().getGivenNames()) ? profile.getRecordNameEntity().getFamilyName() : profile.getRecordNameEntity().getGivenNames() + " " + profile.getRecordNameEntity().getFamilyName();
                }
            } else {
                if(!PojoUtil.isEmpty(profile.getCreditName())) {
                    creditName = profile.getCreditName();
                } else {
                    creditName = PojoUtil.isEmpty(profile.getGivenNames()) ? profile.getFamilyName() : profile.getGivenNames() + " " + profile.getFamilyName(); 
                }
            }
                                    
            if(!PojoUtil.isEmpty(creditName)) {
                infoForm.setUserName(URLDecoder.decode(creditName, "UTF-8").trim());
            }                        
        }        
        
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
            try {
                scopeInfoForm.setDescription(getMessage(ScopePathType.class.getName() + '.' + theScope.name()));
                scopeInfoForm.setLongDescription(getMessage(ScopePathType.class.getName() + '.' + theScope.name() + ".longDesc"));
            } catch(NoSuchMessageException e) {
                LOGGER.warn("Unable to find key message for scope: " + theScope.name() + " " + theScope.value());
            }
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
        if (ClientType.PUBLIC_CLIENT.equals(clientDetails.getClientType())) {
            memberName = PUBLIC_MEMBER_NAME;
        } else if (!PojoUtil.isEmpty(clientDetails.getGroupProfileId())) {
            ProfileEntity groupProfile = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
            if(groupProfile.getRecordNameEntity() != null) {
                memberName = groupProfile.getRecordNameEntity().getCreditName();
            } else {
                memberName = groupProfile.getCreditName();
            }
        }
        // If the group name is empty, use the same as the client
        // name, since it should be a SSO user
        if (StringUtils.isBlank(memberName)) {
            memberName = clientName;
        }

        if(!PojoUtil.isEmpty(email) || !PojoUtil.isEmpty(orcid)) {                        
            // Check if orcid exists, if so, show login screen
            if(!PojoUtil.isEmpty(orcid)) {
                orcid = orcid.trim();
                if(orcidProfileManager.exists(orcid)) {
                    infoForm.setUserId(orcid);
                }
            } else {
                // Check if email exists, if so, show login screen
                if(!PojoUtil.isEmpty(email)) {
                    email = email.trim();
                    if(emailManager.emailExists(email)) {
                        infoForm.setUserId(email);
                    }
                }
            }
        }  
        
        infoForm.setUserEmail(email);
        if(PojoUtil.isEmpty(loggedUserOrcid))
            infoForm.setUserOrcid(orcid);
        infoForm.setUserGivenNames(givenNames);
        infoForm.setUserFamilyNames(familyNames);
        infoForm.setClientId(clientId);
        infoForm.setClientDescription(clientDescription);
        infoForm.setClientName(clientName);
        infoForm.setMemberName(memberName);
        infoForm.setRedirectUrl(redirectUri);
        infoForm.setStateParam(stateParam);
        infoForm.setResponseType(responseType);

        return infoForm;
    }
    
    protected void fillOauthParams(RequestInfoForm requestInfoForm, Map<String, String> params, Map<String, String> approvalParams, boolean userEnabledPersistentTokens) {
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
