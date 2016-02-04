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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.pojo.ajaxForm.OauthRegistrationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.view.RedirectView;

@Controller("oauthRegisterController")
public class OauthRegisterController extends OauthControllerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthRegisterController.class);    
    
    @Resource
    private RegistrationController registrationController;        
    
    @RequestMapping(value = "/oauth/custom/register/empty.json", method = RequestMethod.GET)
    public @ResponseBody OauthRegistrationForm getRegister(HttpServletRequest request, HttpServletResponse response) {
        // Remove the session hash if needed
        if (request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
            request.getSession().removeAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME);
        }

        OauthRegistrationForm empty = new OauthRegistrationForm(registrationController.getRegister(request, response));
        // Creation type in oauth will always be member referred
        empty.setCreationType(Text.valueOf(CreationMethod.MEMBER_REFERRED.value()));
        Text emptyText = Text.valueOf(StringUtils.EMPTY);
        empty.setClientId(emptyText);
        empty.setPassword(emptyText);
        empty.setRedirectUri(emptyText);
        empty.setResponseType(emptyText);
        empty.setScope(emptyText);

        //Set the state param and the client and member names
        fillOauthFormWithRequestInformation(empty, request, response);        
        return empty;
    }       
    
    @RequestMapping(value = "/oauth/custom/register.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm checkRegisterForm(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthRegistrationForm form) {
        form.setErrors(new ArrayList<String>());

        if (form.getApproved()) {
            registrationController.validateRegistrationFields(request, form);
            registrationController.validateGrcaptcha(request, form);
        } else {
            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
            String stateParam = null;

            if (savedRequest != null && savedRequest.getParameterMap() != null && savedRequest.getParameterValues("state") != null) {
                if (savedRequest.getParameterValues("state").length > 0)
                    stateParam = savedRequest.getParameterValues("state")[0];
            }
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue(), stateParam)));
        }

        return form;
    }

    @RequestMapping(value = "/oauth/custom/registerConfirm.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm registerAndAuthorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthRegistrationForm form) {
        if (form.getApproved()) {
            boolean usedCaptcha = false;

            // If recatcha wasn't loaded do nothing. This is for countries that
            // block google.
            if (form.getGrecaptchaWidgetId().getValue() != null) {
                // If the captcha verified key is not in the session, redirect
                // to
                // the login page
                if (request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME) == null
                        || PojoUtil.isEmpty(form.getGrecaptcha())
                        || !form.getGrecaptcha().getValue().equals(
                                request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME))) {
                    String redirectUri = this.getBaseUri() + REDIRECT_URI_ERROR;
                    // Set the client id
                    redirectUri = redirectUri.replace("{0}", form.getClientId().getValue());
                    // Set the response type if needed
                    if (!PojoUtil.isEmpty(form.getResponseType()))
                        redirectUri += "&response_type=" + form.getResponseType().getValue();
                    // Set the redirect uri
                    if (!PojoUtil.isEmpty(form.getRedirectUri()))
                        redirectUri += "&redirect_uri=" + form.getRedirectUri().getValue();
                    // Set the scope param
                    if (!PojoUtil.isEmpty(form.getScope()))
                        redirectUri += "&scope=" + form.getScope().getValue();
                    // Copy the state param if present
                    if (!PojoUtil.isEmpty(request.getParameter("state")))
                        redirectUri += "&state=" + request.getParameter("state");
                    form.setRedirectUri(Text.valueOf(redirectUri));
                    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                    if (savedRequest != null)
                        LOGGER.info("OauthConfirmAccessController original request: " + savedRequest.getRedirectUrl());
                    LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + form.getRedirectUri());
                    return form;
                }

                usedCaptcha = true;
            }

            // Remove the session hash if needed
            if (request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
                request.getSession().removeAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME);
            }

            // Check there are no errors
            registrationController.validateRegistrationFields(request, form);
            if (form.getErrors().isEmpty()) {
                // Register user
                registrationController.createMinimalRegistration(request, RegistrationController.toProfile(form, request), usedCaptcha);
                // Authenticate user
                String email = form.getEmail().getValue();
                String password = form.getPassword().getValue();
                Authentication auth = authenticateUser(request, email, password);
                // Create authorization params
                SimpleSessionStatus status = new SimpleSessionStatus();
                Map<String, Object> model = new HashMap<String, Object>();
                Map<String, String> params = new HashMap<String, String>();
                Map<String, String> approvalParams = new HashMap<String, String>();                
                // Set params
                setOauthParams(form, params, approvalParams, true);

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
                    if (!PojoUtil.isEmpty(form.getRedirectUri()))
                        redirectUri += "&redirect_uri=" + form.getRedirectUri().getValue();
                    // Set the scope param
                    if (!PojoUtil.isEmpty(form.getScope()))
                        redirectUri += "&scope=" + form.getScope().getValue();
                    // Copy the state param if present
                    if (params != null && params.containsKey("state"))
                        redirectUri += "&state=" + params.get("state");
                    form.setRedirectUri(Text.valueOf(redirectUri));
                    LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + form.getRedirectUri());
                    return form;
                }
                // Approve
                RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                form.setRedirectUri(Text.valueOf(view.getUrl()));
            }
        } else {
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue(), request.getParameter("state"))));
        }        
        
        if(new HttpSessionRequestCache().getRequest(request, response) != null)
            new HttpSessionRequestCache().removeRequest(request, response);
        LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + form.getRedirectUri());
        return form;
    }    
}
