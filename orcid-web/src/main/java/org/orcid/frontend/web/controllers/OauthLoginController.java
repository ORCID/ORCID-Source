package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.security.UnclaimedProfileExistsException;
import org.orcid.core.security.aop.LockedException;
import org.orcid.frontend.spring.OrcidWebAuthenticationDetails;
import org.orcid.frontend.web.exception.Bad2FARecoveryCodeException;
import org.orcid.frontend.web.exception.Bad2FAVerificationCodeException;
import org.orcid.frontend.web.exception.VerificationCodeFor2FARequiredException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.utils.OrcidRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Deprecated
@Controller("oauthLoginController")
public class OauthLoginController extends OauthControllerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthLoginController.class);    

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @RequestMapping(value = { "/oauth/signin", "/oauth/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) throws UnsupportedEncodingException {
        String url = request.getQueryString();
        // Get and save the request information form
        RequestInfoForm requestInfoForm = generateRequestInfoForm(url);
        request.getSession().setAttribute(REQUEST_INFO_FORM, requestInfoForm);

        // Check that the client have the required permissions
        // Get client name
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(requestInfoForm.getClientId());

        // validate client scopes
        try {
            authorizationEndpoint.validateScope(requestInfoForm.getScopesAsString(), clientDetails,requestInfoForm.getResponseType());
            orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        } catch (InvalidScopeException | LockedException e) {
            String redirectUriWithParams = requestInfoForm.getRedirectUrl();
            if (e instanceof InvalidScopeException) {
                redirectUriWithParams += "?error=invalid_scope&error_description=" + e.getMessage();
            } else {
                redirectUriWithParams += "?error=client_locked&error_description=" + e.getMessage();
            }
            RedirectView rView = new RedirectView(redirectUriWithParams);
            ModelAndView error = new ModelAndView();
            error.setView(rView);
            return error;
        }
        
        //handle openID behaviour
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString()) && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID) ){
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_NONE)){
                String redirectUriWithParams = requestInfoForm.getRedirectUrl();
                redirectUriWithParams += "?error=login_required";
                RedirectView rView = new RedirectView(redirectUriWithParams);
                ModelAndView error = new ModelAndView();
                error.setView(rView);
                return error;
            }
        }
        
        mav.addObject("hideSupportWidget", true);
        mav.setViewName("oauth_login");
        return mav;
    }

    @RequestMapping(value = { "/oauth/custom/signin.json", "/oauth/custom/login.json" }, method = RequestMethod.POST)
    public @ResponseBody OauthAuthorizeForm authenticateAndAuthorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthAuthorizeForm form) {
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
                    copy2FAFields(form, request);
                    Authentication auth = authenticateUser(request, form.getUserName().getValue(), form.getPassword().getValue());
                    profileEntityManager.updateLastLoginDetails(auth.getName(), OrcidRequestUtil.getIpAddress(request));

                    // Create authorization params
                    SimpleSessionStatus status = new SimpleSessionStatus();
                    Map<String, Object> model = new HashMap<String, Object>();
                    Map<String, String> params = new HashMap<String, String>();
                    Map<String, String> approvalParams = new HashMap<String, String>();
                    
                    fillOauthParams(requestInfoForm, params, approvalParams, form.getPersistentTokenEnabled(), form.isEmailAccessAllowed());                                        

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
                        form.setRedirectUrl(redirectUri);
                        LOGGER.info("OauthLoginController being sent to client browser: " + form.getRedirectUrl());
                        return form;
                    }
                    // Approve
                    RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                    form.setRedirectUrl(view.getUrl());
                    willBeRedirected = true;
                    
                    
                } catch (AuthenticationException ae) {
                    if(ae.getCause() instanceof DisabledException){
                        // Handle this message in angular to allow AJAX action
                        form.getErrors().add("orcid.frontend.security.orcid_deactivated");
                    } else if(ae.getCause() instanceof UnclaimedProfileExistsException) {
                        String email = PojoUtil.isEmpty(form.getUserName()) ? null : form.getUserName().getValue();
                        String resendEmailUrl = createResendClaimUrl(email, request);
                        String errorMessage = getMessage("orcid.frontend.security.unclaimed_exists_1");
                        errorMessage += "<a href=\"" + resendEmailUrl + "\">";
                        errorMessage += getMessage("orcid.frontend.security.unclaimed_exists_2");
                        errorMessage += "</a>" + getMessage("orcid.frontend.security.unclaimed_exists_3");
                        form.getErrors().add(errorMessage);
                    } else if (ae instanceof VerificationCodeFor2FARequiredException) {
                        form.setVerificationCodeRequired(true);
                    } else if (ae instanceof Bad2FAVerificationCodeException) {
                        form.getErrors().add(getMessage("orcid.frontend.security.2fa.bad_verification_code"));
                    } else if (ae instanceof Bad2FARecoveryCodeException) {
                        form.getErrors().add(getMessage("orcid.frontend.security.2fa.bad_recovery_code"));
                    } else {
                        form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
                    }                                            
                }
            }
        } else {
            form.setRedirectUrl(buildDenyRedirectUri(requestInfoForm.getRedirectUrl(), requestInfoForm.getStateParam()));
            willBeRedirected = true;
        }

        // If there was an authentication error, dont log since the user will
        // not be redirected yet
        if (willBeRedirected) {
            if (new HttpSessionRequestCache().getRequest(request, response) != null)
                new HttpSessionRequestCache().removeRequest(request, response);
            LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + requestInfoForm.getRedirectUrl());
        }
        return form;
    }

    private void copy2FAFields(OauthAuthorizeForm form, HttpServletRequest request) {
        if (form.getVerificationCode() != null) {
            request.setAttribute(OrcidWebAuthenticationDetails.VERIFICATION_CODE_PARAMETER, form.getVerificationCode().getValue());
        }
        
        if (form.getRecoveryCode() != null) {
            request.setAttribute(OrcidWebAuthenticationDetails.RECOVERY_CODE_PARAMETER, form.getRecoveryCode().getValue());
        }
    }

    private void validateUserNameAndPassword(OauthAuthorizeForm form) {
        if (PojoUtil.isEmpty(form.getUserName()) || PojoUtil.isEmpty(form.getPassword())) {
            form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
        }
    }           
}
