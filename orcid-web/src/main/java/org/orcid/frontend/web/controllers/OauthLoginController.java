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

import org.apache.commons.lang.StringUtils;
import org.orcid.core.security.aop.LockedException;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
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
    private Pattern responseTypePattern = Pattern.compile("response_type=([^&]*)");        
    
    @RequestMapping(value = { "/oauth/signin", "/oauth/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler2(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) throws UnsupportedEncodingException {
        // find client name if available
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String clientName = "";
        String clientId = "";
        String clientGroupName = "";
        String email = "";
        String clientDescription = "";
        String scope = "";        
        String responseType = "";
        String orcid = null;                        
        boolean showLogin = false; // default to Reg
        if (savedRequest != null) {
            String url = savedRequest.getRedirectUrl();
            
            //Save the request information form
            RequestInfoForm requestInfoForm = generateAndSaveRequestInfoForm(request, url);
            
            if (url.toLowerCase().contains("show_login=true"))
                showLogin = true;
            //TODO: We should not load any info in the freemarker ModelAndViewObject, we should move all info we need to the forms
            Matcher matcher = clientIdPattern.matcher(url);
            if (matcher.find()) {
                clientId = matcher.group(1);
                if (clientId != null) {
                    try {
                        clientId = URLDecoder.decode(clientId, "UTF-8").trim();
                    } catch (UnsupportedEncodingException e) {
                    }
                    Matcher emailMatcher = RegistrationController.emailPattern.matcher(url);
                    if (emailMatcher.find()) {
                        String tempEmail = emailMatcher.group(1);
                        try {
                            tempEmail = URLDecoder.decode(tempEmail, "UTF-8").trim();
                        } catch (UnsupportedEncodingException e) {
                        }
                        if (orcidProfileManager.emailExists(tempEmail))
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

                    Matcher scopeMatcher = scopesPattern.matcher(url);
                    if (scopeMatcher.find()) {
                        scope = scopeMatcher.group(1);
                        try {
                            scope = URLDecoder.decode(scope, "UTF-8").trim();
                            scope = scope.replaceAll(" +", " ");
                        } catch (UnsupportedEncodingException e) {
                        }
                    }                    

                    Matcher responseTypeMatcher = responseTypePattern.matcher(url);
                    if (responseTypeMatcher.find()) {
                        responseType = responseTypeMatcher.group(1);
                        try {
                            responseType = URLDecoder.decode(responseType, "UTF-8").trim();
                        } catch (UnsupportedEncodingException e) {
                        }
                    }

                    // Get client name
                    ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);

                    // validate client scopes
                    try {
                        authorizationEndpoint.validateScope(scope, clientDetails);
                        orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
                    } catch (InvalidScopeException ise) {
                        String redirectUriWithParams = requestInfoForm.getRedirectUrl();
                        redirectUriWithParams += "?error=invalid_scope&error_description=" + ise.getMessage();
                        RedirectView rView = new RedirectView(redirectUriWithParams);

                        ModelAndView error = new ModelAndView();
                        error.setView(rView);
                        return error;
                    } catch (LockedException le) {
                        String redirectUriWithParams = requestInfoForm.getRedirectUrl();
                        redirectUriWithParams += "?error=client_locked&error_description=" + le.getMessage();
                        RedirectView rView = new RedirectView(redirectUriWithParams);

                        ModelAndView error = new ModelAndView();
                        error.setView(rView);
                        return error;
                    }
                    // If client details is ok, continue
                    clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
                    clientDescription = clientDetails.getClientDescription() == null ? "" : clientDetails.getClientDescription();

                    // If client type is null it means it is a public client
                    if (clientDetails.getClientType() == null) {
                        clientGroupName = PUBLIC_MEMBER_NAME;
                    } else if (!PojoUtil.isEmpty(clientDetails.getGroupProfileId())) {
                        ProfileEntity groupProfile = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
                        clientGroupName = groupProfile.getCreditName();
                    }
                    // If the group name is empty, use the same as the client
                    // name, since it should be a SSO user
                    if (StringUtils.isBlank(clientGroupName)) {
                        clientGroupName = clientName;
                    }
                }
            }
        }        
        mav.addObject("response_type", responseType);
        mav.addObject("client_name", clientName);
        mav.addObject("client_id", clientId);
        mav.addObject("client_group_name", clientGroupName);
        mav.addObject("client_description", clientDescription);
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
                    String scopes = requestInfoForm.getScopesAsString();
                    
                    // Set params
                    setOauthParams(form, params, approvalParams, scopes, requestInfoForm.getRedirectUrl(), false);

                    // Authorize
                    try {
                        authorizationEndpoint.authorize(model, params, status, auth);
                    } catch (RedirectMismatchException rUriError) {
                        String redirectUri = this.getBaseUri() + REDIRECT_URI_ERROR;
                        // Set the client id
                        redirectUri = redirectUri.replace("{0}", form.getClientId().getValue());
                        // Set the response type if needed
                        if (!PojoUtil.isEmpty(form.getResponseType()))
                            redirectUri += "&response_type=" + form.getResponseType().getValue();
                        // Set the redirect uri
                        if (!PojoUtil.isEmpty(requestInfoForm.getRedirectUrl()))
                            redirectUri += "&redirect_uri=" + requestInfoForm.getRedirectUrl();
                        // Set the scope param
                        if (!PojoUtil.isEmpty(scopes))
                            redirectUri += "&scope=" + scopes;
                        // Copy the state param if present
                        if (params != null && params.containsKey("state"))
                            redirectUri += "&state=" + params.get("state");
                        requestInfoForm.setRedirectUrl(redirectUri);
                        LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + requestInfoForm.getRedirectUrl());
                        return requestInfoForm;
                    }
                    // Approve
                    RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                    requestInfoForm.setRedirectUrl(view.getUrl());
                    willBeRedirected = true;
                } catch (AuthenticationException ae) {
                    form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
                }
            }
        } else {
            String stateParam = null;
            if (!PojoUtil.isEmpty(form.getStateParam())) {                
                stateParam = form.getStateParam().getValue();
            }
            requestInfoForm.setRedirectUrl(buildDenyRedirectUri(requestInfoForm.getRedirectUrl(), stateParam));
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
