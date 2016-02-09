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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.security.aop.LockedException;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller("oauthLoginController")
public class OauthLoginController extends OauthControllerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthLoginController.class);
    
    private Pattern orcidPattern = Pattern.compile("(&|\\?)orcid=([^&]*)");    
            
    
    @RequestMapping(value = { "/oauth/signin", "/oauth/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler2(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) throws UnsupportedEncodingException {
        // find client name if available
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);        
        String email = "";
        String orcid = null;                        
        boolean showLogin = false; // default to Reg
        if (savedRequest != null) {
            String url = savedRequest.getRedirectUrl();
            //Get and save the request information form
            RequestInfoForm requestInfoForm = generateRequestInfoForm(url);
            request.getSession().setAttribute(REQUEST_INFO_FORM, requestInfoForm);
                        
            if (url.toLowerCase().contains("show_login=true")) {
                showLogin = true;
            }
            
            Matcher emailMatcher = RegistrationController.emailPattern.matcher(url);
            if (emailMatcher.find()) {
                String tempEmail = emailMatcher.group(1);
                try {
                    tempEmail = URLDecoder.decode(tempEmail, "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                }
                if (emailManager.emailExists(tempEmail))
                    email = tempEmail;
            }

            Matcher orcidMatcher = orcidPattern.matcher(url);
            if (orcidMatcher.find()) {
                String tempOrcid = orcidMatcher.group(2);
                try {
                    tempOrcid = URLDecoder.decode(tempOrcid, "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                }
                if (orcidProfileManager.exists(tempOrcid))
                    orcid = tempOrcid;
            }                    
            
            //Check that the client have the required permissions
            // Get client name
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(requestInfoForm.getClientId());

            // validate client scopes
            try {
                authorizationEndpoint.validateScope(requestInfoForm.getScopesAsString(), clientDetails);
                orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
            } catch (InvalidScopeException | LockedException e) {
                String redirectUriWithParams = requestInfoForm.getRedirectUrl();                
                if(e instanceof InvalidScopeException) {
                    redirectUriWithParams += "?error=invalid_scope&error_description=" + e.getMessage();
                } else {
                    redirectUriWithParams += "?error=client_locked&error_description=" + e.getMessage();
                }                               
                RedirectView rView = new RedirectView(redirectUriWithParams);
                ModelAndView error = new ModelAndView();
                error.setView(rView);
                return error;
            }            
        }
        
        mav.addObject("userId", orcid != null ? orcid : email);
        mav.addObject("hideUserVoiceScript", true);
        mav.addObject("showLogin", String.valueOf(showLogin));
        mav.setViewName("oauth_login");
        return mav;
    }
    
    @RequestMapping(value = { "/oauth/custom/signin.json", "/oauth/custom/login.json" }, method = RequestMethod.POST)
    public @ResponseBody RequestInfoForm authenticateAndAuthorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthAuthorizeForm form) {
        // Clean form errors
        form.setErrors(new ArrayList<String>());        
        RequestInfoForm requestInfoForm = (RequestInfoForm) request.getSession().getAttribute(REQUEST_INFO_FORM);
        
        boolean willBeRedirected = false;
        if (form.getApproved()) {
            // Validate name and password
            validateUserNameAndPassword(form);
            if (form.getErrors().isEmpty()) {
                try {
                    // Authenticate user
                    Authentication auth = authenticateUser(request, form);
                    // Create authorization params
                    SimpleSessionStatus status = new SimpleSessionStatus();
                    Map<String, Object> model = new HashMap<String, Object>();
                    Map<String, String> params = new HashMap<String, String>();
                    Map<String, String> approvalParams = new HashMap<String, String>();                    
                    
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
                    if(requestInfoForm.isClientHavePersistentTokens() && form.getPersistentTokenEnabled()) {
                        params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "true");
                    } else {
                        params.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "false");
                    }
                                                                                
                    // Authorize
                    try {
                        authorizationEndpoint.authorize(model, params, status, auth);
                    } catch (RedirectMismatchException rUriError) {
                        String redirectUri = this.getBaseUri() + REDIRECT_URI_ERROR;
                        // Set the client id
                        redirectUri = redirectUri.replace("{0}", requestInfoForm.getClientId());
                        // Set the response type if needed
                        if (!PojoUtil.isEmpty(requestInfoForm.getResponseType()))
                            redirectUri += "&response_type=" + requestInfoForm.getResponseType();
                        // Set the redirect uri
                        if (!PojoUtil.isEmpty(requestInfoForm.getRedirectUrl()))
                            redirectUri += "&redirect_uri=" + requestInfoForm.getRedirectUrl();
                        // Set the scope param
                        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString()))
                            redirectUri += "&scope=" + requestInfoForm.getScopesAsString();
                        // Copy the state param if present
                        if (!PojoUtil.isEmpty(requestInfoForm.getStateParam()))
                            redirectUri += "&state=" + requestInfoForm.getStateParam();
                        requestInfoForm.setRedirectUrl(redirectUri);
                        LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + requestInfoForm.getRedirectUrl());
                        return requestInfoForm;
                    }
                    // Approve
                    RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                    requestInfoForm.setRedirectUrl(view.getUrl());
                    willBeRedirected = true;
                } catch (AuthenticationException ae) {
                    requestInfoForm.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
                }
            }
        } else {            
            requestInfoForm.setRedirectUrl(buildDenyRedirectUri(requestInfoForm.getRedirectUrl(), requestInfoForm.getStateParam()));
            willBeRedirected = true;
        }

        // If there was an authentication error, dont log since the user will
        // not be redirected yet
        if (willBeRedirected) {
            if(new HttpSessionRequestCache().getRequest(request, response) != null)
                new HttpSessionRequestCache().removeRequest(request, response);
            LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + requestInfoForm.getRedirectUrl());
        }
        return requestInfoForm;
    }
    
    private void validateUserNameAndPassword(OauthAuthorizeForm form) {
        if (PojoUtil.isEmpty(form.getUserName()) || PojoUtil.isEmpty(form.getPassword())) {
            form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
        }
    }    
}
