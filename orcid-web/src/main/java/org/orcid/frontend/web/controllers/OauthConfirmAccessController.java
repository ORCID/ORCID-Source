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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OauthTokensConstants;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.OauthRegistration;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller("oauthConfirmAccessController")
@RequestMapping(value = "/oauth", method = RequestMethod.GET)
public class OauthConfirmAccessController extends BaseController {

    private static String PUBLIC_CLIENT_GROUP_NAME = "PubApp";

    private Pattern clientIdPattern = Pattern.compile("client_id=([^&]*)");
    private Pattern orcidPattern = Pattern.compile("(&|\\?)orcid=([^&]*)");
    private Pattern scopesPattern = Pattern.compile("scope=([^&]*)");
    private Pattern redirectUriPattern = Pattern.compile("redirect_uri=([^&]*)");
    private Pattern responseTypePattern = Pattern.compile("response_type=([^&]*)");

    private static final String RESPONSE_TYPE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String SCOPE_PARAM = "scope";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    private static final String EMPTY_STRING = "";

    private static final String JUST_REGISTERED = "justRegistered";
    @Resource
    private OrcidProfileManager orcidProfileManager;
    @Resource
    private ClientDetailsManager clientDetailsManager;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private OrcidAuthorizationEndpoint authorizationEndpoint;
    @Resource
    private RegistrationController registrationController;

    private static String REDIRECT_URI_ERROR = "/oauth/error/redirect-uri-mismatch?client_id={0}";

    @RequestMapping(value = { "/signin", "/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler2(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
        // find client name if available
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String clientName = "";
        String clientId = "";
        String clientGroupName = "";
        String email = "";
        String clientDescription = "";
        String scope = "";
        String redirectUri = "";
        String responseType = "";
        String orcid = null;
        boolean showLogin = false; // default to Reg
        boolean usePersistentTokens = false;
        if (savedRequest != null) {
            String url = savedRequest.getRedirectUrl();
            if (url.toLowerCase().contains("show_login=true"))
                showLogin = true;
            Matcher matcher = clientIdPattern.matcher(url);
            if (matcher.find()) {
                clientId = matcher.group(1);
                if (clientId != null) {
                    Matcher emailMatcher = RegistrationController.emailPattern.matcher(url);
                    if (emailMatcher.find()) {
                        String tempEmail = emailMatcher.group(1);
                        try {
                            tempEmail = URLDecoder.decode(tempEmail, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                        }
                        if (orcidProfileManager.emailExists(tempEmail))
                            email = tempEmail;
                    }

                    Matcher orcidMatcher = orcidPattern.matcher(url);
                    if (orcidMatcher.find()) {
                        String tempOrcid = orcidMatcher.group(2);
                        try {
                            tempOrcid = URLDecoder.decode(tempOrcid, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                        }
                        if (orcidProfileManager.exists(tempOrcid))
                            orcid = tempOrcid;
                    }

                    Matcher scopeMatcher = scopesPattern.matcher(url);
                    if (scopeMatcher.find()) {
                        scope = scopeMatcher.group(1);
                        try {
                            scope = URLDecoder.decode(scope, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                        }
                    }

                    Matcher redirectUriMatcher = redirectUriPattern.matcher(url);
                    if (redirectUriMatcher.find()) {
                        try {
                            redirectUri = URLDecoder.decode(redirectUriMatcher.group(1), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                        }
                    }

                    Matcher responseTypeMatcher = responseTypePattern.matcher(url);
                    if (responseTypeMatcher.find()) {
                        responseType = responseTypeMatcher.group(1);
                    }

                    // Get client name
                    ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);

                    // Check if the client has persistent tokens enabled
                    if (clientDetails.isPersistentTokensEnabled())
                        usePersistentTokens = true;

                    // Remove client_credentials scopes
                    if (!PojoUtil.isEmpty(scope))
                        scope = trimClientCredentialScopes(scope);

                    // validate client scopes
                    try {
                        authorizationEndpoint.validateScope(scope, clientDetails);
                    } catch (InvalidScopeException ise) {
                        String redirectUriWithParams = redirectUri;
                        redirectUriWithParams += "?error=invalid_scope&error_description=" + ise.getMessage();
                        RedirectView rView = new RedirectView(redirectUriWithParams);

                        ModelAndView error = new ModelAndView();
                        error.setView(rView);
                        return error;
                    }
                    // If client details is ok, continue
                    clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
                    clientDescription = clientDetails.getClientDescription() == null ? "" : clientDetails.getClientDescription();

                    ProfileEntity groupProfile = clientDetails.getGroupProfile();
                    // If client type is null it means it is a public client
                    if (clientDetails.getClientType() == null) {
                        clientGroupName = PUBLIC_CLIENT_GROUP_NAME;
                    } else if (groupProfile != null) {
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
        mav.addObject("scopes", ScopePathType.getScopesFromSpaceSeparatedString(scope));
        mav.addObject("scopesString", scope);
        mav.addObject("redirect_uri", redirectUri);
        mav.addObject("response_type", responseType);
        mav.addObject("client_name", clientName);
        mav.addObject("client_id", clientId);
        mav.addObject("client_group_name", clientGroupName);
        mav.addObject("client_description", clientDescription);
        mav.addObject("userId", orcid != null ? orcid : email);
        mav.addObject("hideUserVoiceScript", true);
        mav.addObject("usePersistentTokens", usePersistentTokens);
        mav.addObject("showLogin", String.valueOf(showLogin));
        mav.setViewName("oauth_login");
        return mav;
    }

    @RequestMapping(value = "/confirm_access", method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, ModelAndView mav, @RequestParam("client_id") String clientId, @RequestParam("scope") String scope) {
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_ONLY);

        // XXX Use T2 API

        Boolean justRegistered = (Boolean) request.getSession().getAttribute(JUST_REGISTERED);
        if (justRegistered != null) {
            request.getSession().removeAttribute(JUST_REGISTERED);
            mav.addObject(JUST_REGISTERED, justRegistered);
        }
        String clientName = "";
        String clientDescription = "";
        String clientGroupName = "";
        String clientWebsite = "";

        // Remove client_credentials scopes
        if (!PojoUtil.isEmpty(scope))
            scope = trimClientCredentialScopes(scope);

        boolean usePersistentTokens = false;

        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
        clientDescription = clientDetails.getClientDescription() == null ? "" : clientDetails.getClientDescription();
        clientWebsite = clientDetails.getClientWebsite() == null ? "" : clientDetails.getClientWebsite();

        // Check if the client has persistent tokens enabled
        if (clientDetails.isPersistentTokensEnabled())
            usePersistentTokens = true;

        if (clientDetails.getClientType() == null) {
            clientGroupName = PUBLIC_CLIENT_GROUP_NAME;
        } else if (clientDetails.getGroupProfile() != null) {
            if (!PojoUtil.isEmpty(clientDetails.getGroupProfile().getCreditName()))
                clientGroupName = clientDetails.getGroupProfile().getCreditName();
        }

        // If the group name is empty, use the same as the client name, since it
        // should be a SSO user
        if (StringUtils.isBlank(clientGroupName)) {
            clientGroupName = clientName;
        }
        mav.addObject("profile", profile);
        mav.addObject("client_name", clientName);
        mav.addObject("client_description", clientDescription);
        mav.addObject("client_group_name", clientGroupName);
        mav.addObject("client_website", clientWebsite);
        mav.addObject("scopes", ScopePathType.getScopesFromSpaceSeparatedString(scope));
        mav.addObject("scopesString", scope);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        mav.addObject("auth", authentication);
        mav.setViewName("confirm-oauth-access");
        mav.addObject("hideUserVoiceScript", true);
        mav.addObject("profile", getEffectiveProfile());
        mav.addObject("usePersistentTokens", usePersistentTokens);
        return mav;
    }

    @RequestMapping(value = "/custom/authorize/empty.json", method = RequestMethod.GET)
    public @ResponseBody
    OauthAuthorizeForm getEmptyAuthorizeForm() {
        OauthAuthorizeForm empty = new OauthAuthorizeForm();
        Text emptyText = Text.valueOf(EMPTY_STRING);
        empty.setClientId(emptyText);
        empty.setPassword(emptyText);
        empty.setRedirectUri(emptyText);
        empty.setResponseType(emptyText);
        empty.setScope(emptyText);
        empty.setUserName(emptyText);
        return empty;
    }

    @RequestMapping(value = { "/custom/signin.json", "/custom/login.json" }, method = RequestMethod.POST)
    public @ResponseBody
    OauthAuthorizeForm authenticateAndAuthorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthAuthorizeForm form) {
        // Clean form errors
        form.setErrors(new ArrayList<String>());
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
                    // Put all request params into the params
                    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                    copyRequestParameters(savedRequest, params);
                    // Then, put the custom authorization params
                    params.put(CLIENT_ID_PARAM, form.getClientId().getValue());
                    if (!PojoUtil.isEmpty(form.getRedirectUri()))
                        params.put(REDIRECT_URI_PARAM, form.getRedirectUri().getValue());
                    else
                        params.put(REDIRECT_URI_PARAM, new String());
                    if (!PojoUtil.isEmpty(form.getScope()))
                        params.put(SCOPE_PARAM, form.getScope().getValue());
                    if (!PojoUtil.isEmpty(form.getResponseType()))
                        params.put(RESPONSE_TYPE_PARAM, form.getResponseType().getValue());
                    params.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "true");
                    Map<String, String> approvalParams = new HashMap<String, String>();
                    approvalParams.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "true");
                    approvalParams.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);
                    // Check if the client have persistent tokens enabled
                    approvalParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "false");
                    if (hasPersistenTokensEnabled(form.getClientId().getValue()))
                        // Then check if the client granted the persistent token
                        if (form.getPersistentTokenEnabled())
                            approvalParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "true");

                    // Authorize
                    try {
                        authorizationEndpoint.authorize(model, RESPONSE_TYPE, params, status, auth);
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
                        return form;
                    }
                    // Approve
                    RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                    form.setRedirectUri(Text.valueOf(view.getUrl()));
                } catch (AuthenticationException ae) {
                    form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
                }
            }
        } else {
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue())));
        }

        return form;
    }

    @RequestMapping(value = "/custom/register/empty.json", method = RequestMethod.GET)
    public @ResponseBody
    OauthRegistration getRegister(HttpServletRequest request, HttpServletResponse response) {
        OauthRegistration empty = new OauthRegistration(registrationController.getRegister(request, response));
        // Creation type in oauth will always be member referred
        empty.setCreationType(Text.valueOf(CreationMethod.MEMBER_REFERRED.value()));
        Text emptyText = Text.valueOf(EMPTY_STRING);
        empty.setClientId(emptyText);
        empty.setPassword(emptyText);
        empty.setRedirectUri(emptyText);
        empty.setResponseType(emptyText);
        empty.setScope(emptyText);
        return empty;
    }

    @RequestMapping(value = "/custom/register.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration checkRegisterForm(HttpServletRequest request, @RequestBody OauthRegistration form) {
        form.setErrors(new ArrayList<String>());

        if (form.getApproved()) {
            registrationController.registerGivenNameValidate(form);
            registrationController.registerPasswordValidate(form);
            registrationController.registerPasswordConfirmValidate(form);
            registrationController.regEmailValidate(request, form, true);
            registrationController.registerTermsOfUseValidate(form);

            copyErrors(form.getEmailConfirm(), form);
            copyErrors(form.getEmail(), form);
            copyErrors(form.getGivenNames(), form);
            copyErrors(form.getPassword(), form);
            copyErrors(form.getPasswordConfirm(), form);
            copyErrors(form.getTermsOfUse(), form);
        } else {
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue())));
        }
        return form;
    }

    @RequestMapping(value = "/custom/registerConfirm.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration registerAndAuthorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthRegistration form) {
        form.setErrors(new ArrayList<String>());

        if (form.getApproved()) {
            // Check there are no errors
            checkRegisterForm(request, form);
            if (form.getErrors() != null && form.getErrors().isEmpty()) {
                // Register user
                registrationController.createMinimalRegistration(request, RegistrationController.toProfile(form));
                // Authenticate user
                String email = form.getEmail().getValue();
                String password = form.getPassword().getValue();
                Authentication auth = authenticateUser(request, email, password);
                // Create authorization params
                SimpleSessionStatus status = new SimpleSessionStatus();
                Map<String, Object> model = new HashMap<String, Object>();
                Map<String, String> params = new HashMap<String, String>();
                // Put all request params into the params
                SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                copyRequestParameters(savedRequest, params);
                // Then, put the custom authorization params
                params.put(CLIENT_ID_PARAM, form.getClientId().getValue());
                if (!PojoUtil.isEmpty(form.getRedirectUri()))
                    params.put(REDIRECT_URI_PARAM, form.getRedirectUri().getValue());
                else
                    params.put(REDIRECT_URI_PARAM, new String());
                if (!PojoUtil.isEmpty(form.getScope()))
                    params.put(SCOPE_PARAM, form.getScope().getValue());
                if (!PojoUtil.isEmpty(form.getResponseType()))
                    params.put(RESPONSE_TYPE_PARAM, form.getResponseType().getValue());
                if (form.getApproved())
                    params.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "true");
                else
                    params.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "false");
                Map<String, String> approvalParams = new HashMap<String, String>();
                if (form.getApproved())
                    approvalParams.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "true");
                else
                    approvalParams.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "false");

                approvalParams.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);
                // Check if the client have persistent tokens enabled
                approvalParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "false");
                if (hasPersistenTokensEnabled(form.getClientId().getValue()))
                    // Then check if the client granted the persistent token
                    if (form.getPersistentTokenEnabled())
                        approvalParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "true");

                // Authorize
                try {
                    authorizationEndpoint.authorize(model, RESPONSE_TYPE, params, status, auth);
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
                    return form;
                }
                // Approve
                RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                form.setRedirectUri(Text.valueOf(view.getUrl()));
            }
        } else {
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue())));
        }

        return form;
    }

    @RequestMapping(value = { "/custom/authorize.json" }, method = RequestMethod.POST)
    public @ResponseBody
    OauthAuthorizeForm authorize(HttpServletRequest request, @RequestBody OauthAuthorizeForm form) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object authorizationRequest = request.getSession().getAttribute("authorizationRequest");
        // Authorization request model
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("authorizationRequest", authorizationRequest);
        // Approval params
        Map<String, String> approvalParams = new HashMap<String, String>();
        if (form.getApproved())
            approvalParams.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "true");
        else
            approvalParams.put(AuthorizationRequest.USER_OAUTH_APPROVAL, "false");

        approvalParams.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);
        // Check if the client have persistent tokens enabled
        approvalParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "false");
        if (hasPersistenTokensEnabled(form.getClientId().getValue()))
            // Then check if the client granted the persistent token
            if (form.getPersistentTokenEnabled())
                approvalParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "true");

        // Session status
        SimpleSessionStatus status = new SimpleSessionStatus();

        // Approve
        RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
        form.setRedirectUri(Text.valueOf(view.getUrl()));
        return form;
    }

    /**
     * Builds the redirect uri string to use when the user deny the request
     * 
     * @param redirectUri
     *            Redirect uri
     * @return the redirect uri string with the deny params
     * */
    private String buildDenyRedirectUri(String redirectUri) {
        if (!PojoUtil.isEmpty(redirectUri)) {
            if (redirectUri.contains("?")) {
                redirectUri = redirectUri.concat("&error=access_denied&error_description=User denied access");
            } else {
                redirectUri = redirectUri.concat("?error=access_denied&error_description=User denied access");
            }
        }
        return redirectUri;
    }

    /**
     * Copies all request parameters into the provided params map
     * 
     * @param request
     *            The server request
     * @param params
     *            The map to copy the params
     * */
    private void copyRequestParameters(SavedRequest request, Map<String, String> params) {
        if (request != null && request.getParameterMap() != null) {
            Map<String, String[]> savedParams = request.getParameterMap();

            if (savedParams != null && !savedParams.isEmpty()) {
                for (String key : savedParams.keySet()) {
                    String[] values = savedParams.get(key);
                    if (values != null && values.length > 0)
                        params.put(key, values[0]);
                }
            }
        }
    }

    /*****************************
     * Authenticate user methods
     ****************************/
    private Authentication authenticateUser(HttpServletRequest request, OauthAuthorizeForm form) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(form.getUserName().getValue(), form.getPassword().getValue());
        token.setDetails(new WebAuthenticationDetails(request));
        return authenticateUser(token);
    }

    private Authentication authenticateUser(HttpServletRequest request, String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        token.setDetails(new WebAuthenticationDetails(request));
        return authenticateUser(token);
    }

    private Authentication authenticateUser(UsernamePasswordAuthenticationToken token) {
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    /*****************************
     * Validators
     ****************************/
    private void validateUserNameAndPassword(OauthAuthorizeForm form) {
        if (PojoUtil.isEmpty(form.getUserName()) || PojoUtil.isEmpty(form.getPassword())) {
            form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
        }
    }

    @RequestMapping(value = "/custom/register/validatePasswordConfirm.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration validatePasswordConfirm(@RequestBody OauthRegistration reg) {
        registrationController.registerPasswordConfirmValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/custom/register/validatePassword.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration validatePassword(@RequestBody OauthRegistration reg) {
        registrationController.registerPasswordValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/custom/register/validateTermsOfUse.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration validateTermsOfUse(@RequestBody OauthRegistration reg) {
        registrationController.registerTermsOfUseValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/custom/register/validateGivenNames.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration validateGivenName(@RequestBody OauthRegistration reg) {

        registrationController.registerGivenNameValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/custom/register/validateEmail.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration validateEmail(HttpServletRequest request, @RequestBody OauthRegistration reg) {
        registrationController.regEmailValidate(request, reg, true);
        return reg;
    }

    @RequestMapping(value = "/custom/register/validateEmailConfirm.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration validateEmailConfirm(@RequestBody OauthRegistration reg) {
        registrationController.regEmailConfirmValidate(reg);
        return reg;
    }

    /**
     * Checks if the client has the persistent tokens enabled
     * 
     * @return true if the persistent tokens are enabled for that client
     * @throws IllegalArgumentException
     * */
    private boolean hasPersistenTokensEnabled(String clientId) throws IllegalArgumentException {
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        if (clientDetails == null)
            throw new IllegalArgumentException("Invalid client details id");
        return clientDetails.isPersistentTokensEnabled();
    }

    private String trimClientCredentialScopes(String scopes) {
        String result = scopes;
        for (String scope : OAuth2Utils.parseParameterList(scopes)) {
            ScopePathType scopeType = ScopePathType.fromValue(scope);
            if (scopeType.isClientCreditalScope()) {
                if (scopes.contains(ScopePathType.ORCID_PROFILE_CREATE.getContent()))
                    result = scopes.replaceAll(ScopePathType.ORCID_PROFILE_CREATE.getContent(), "");
                else if (scopes.contains(ScopePathType.READ_PUBLIC.getContent()))
                    result = scopes.replaceAll(ScopePathType.READ_PUBLIC.getContent(), "");
                else if (scopes.contains(ScopePathType.WEBHOOK.getContent()))
                    result = scopes.replaceAll(ScopePathType.WEBHOOK.getContent(), "");
            }
        }

        return result;
    }

}
