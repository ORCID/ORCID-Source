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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OauthTokensConstants;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidRandomValueTokenServices;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.OauthForm;
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

import com.orcid.api.common.server.delegator.OrcidClientCredentialEndPointDelegator;

@Controller("oauthConfirmAccessController")
@RequestMapping(value = "/oauth", method = RequestMethod.GET)
public class OauthConfirmAccessController extends BaseController {

    private static String PUBLIC_CLIENT_GROUP_NAME = "PubApp";

    private Pattern clientIdPattern = Pattern.compile("client_id=([^&]*)");
    private Pattern orcidPattern = Pattern.compile("(&|\\?)orcid=([^&]*)");
    private Pattern scopesPattern = Pattern.compile("scope=([^&]*)");
    private Pattern redirectUriPattern = Pattern.compile("redirect_uri=([^&]*)");
    private Pattern responseTypePattern = Pattern.compile("response_type=([^&]*)");

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
    @Resource
    private ProfileEntityManager profileEntityManager;
    @Resource
    private OrcidRandomValueTokenServices tokenServices;
    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private EncryptionManager encryptionManager;

    private static String REDIRECT_URI_ERROR = "/oauth/error/redirect-uri-mismatch?client_id={0}";
        
    
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public @ResponseBody Object obtainOauth2TokenPost(HttpServletRequest request) {
    	String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String redirectUri = request.getParameter("redirect_uri");
        String resourceId = request.getParameter("resource_id");
        String refreshToken = request.getParameter("refresh_token");
        String scopeList = request.getParameter("scope");
        String grantType = request.getParameter("grant_type");
        Set<String> scopes = new HashSet<String>();
        if (StringUtils.isNotEmpty(scopeList)) {
            scopes = OAuth2Utils.parseParameterList(scopeList);
        }
        Response res = null;
        try {
        	res = orcidClientCredentialEndPointDelegator.obtainOauth2Token(clientId, clientSecret, refreshToken, grantType, code, scopes, state, redirectUri, resourceId);
        } catch(Exception e) {
            return getLegacyOrcidEntity("OAuth2 problem", e);
        }
        return res.getEntity();
    }
    
    private OrcidMessage getLegacyOrcidEntity(String prefix, Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(prefix + " : " + e.getMessage()));
        return entity;
    }
    
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

                    Matcher redirectUriMatcher = redirectUriPattern.matcher(url);
                    if (redirectUriMatcher.find()) {
                        try {
                            redirectUri = URLDecoder.decode(redirectUriMatcher.group(1), "UTF-8").trim();
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
                    ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);

                    // Check if the client has persistent tokens enabled
                    if (clientDetails.isPersistentTokensEnabled())
                        usePersistentTokens = true;                    

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

                    // If client type is null it means it is a public client
                    if (clientDetails.getClientType() == null) {
                        clientGroupName = PUBLIC_CLIENT_GROUP_NAME;
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
    public ModelAndView loginGetHandler(HttpServletRequest request,  HttpServletResponse response, ModelAndView mav, @RequestParam("client_id") String clientId, @RequestParam("scope") String scope, @RequestParam("redirect_uri") String redirectUri) {
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_ONLY);
        clientId = (clientId != null) ? clientId.trim() : clientId;
        scope = (scope != null) ? scope.trim().replaceAll(" +", " ") : scope;
        redirectUri = (redirectUri != null) ? redirectUri.trim() : redirectUri;
       
        Boolean justRegistered = (Boolean) request.getSession().getAttribute(JUST_REGISTERED);
        if (justRegistered != null) {
            request.getSession().removeAttribute(JUST_REGISTERED);
            mav.addObject(JUST_REGISTERED, justRegistered);
        }
        String clientName = "";
        String clientDescription = "";
        String clientGroupName = "";
        String clientWebsite = "";

        boolean usePersistentTokens = false;
        
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
        clientDescription = clientDetails.getClientDescription() == null ? "" : clientDetails.getClientDescription();
        clientWebsite = clientDetails.getClientWebsite() == null ? "" : clientDetails.getClientWebsite();
        
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
       
        // Check if the client has persistent tokens enabled
        if (clientDetails.isPersistentTokensEnabled()) {
            usePersistentTokens = true;
        }

        // TODO: If persistent tokens are enabled, check for the existing tokens
        // for this client, if one exists with the same scopes, just return the
        // authorization code
        if (usePersistentTokens) {
            boolean tokenAlreadyExists = tokenServices.tokenAlreadyExists(clientId, getEffectiveUserOrcid(), OAuth2Utils.parseParameterList(scope));
            if (tokenAlreadyExists) {                
                AuthorizationRequest authorizationRequest = (AuthorizationRequest) request.getSession().getAttribute("authorizationRequest");
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Map<String, String> requestParams = new HashMap<String, String>();
                copyRequestParameters(request, requestParams);
                Map<String, String> approvalParams = new HashMap<String, String>();

                requestParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
                approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");

                requestParams.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);

                // Check if the client have persistent tokens enabled
                requestParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "false");
                if (hasPersistenTokensEnabled(clientId)) {
                    // Then check if the client granted the persistent token
                    requestParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "true");
                }
                
                // Session status
                SimpleSessionStatus status = new SimpleSessionStatus();

                authorizationRequest.setRequestParameters(requestParams);
                // Authorization request model
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("authorizationRequest", authorizationRequest);
                
                // Approve
                RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                ModelAndView authCodeView = new ModelAndView();
                authCodeView.setView(view);
                return authCodeView;
            }
        }
        
        if (clientDetails.getClientType() == null) {
            clientGroupName = PUBLIC_CLIENT_GROUP_NAME;
        } else if (!PojoUtil.isEmpty(clientDetails.getGroupProfileId())) {
            ProfileEntity groupProfile = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
            clientGroupName = groupProfile.getCreditName();
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
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
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
                    
                    // Set params 
                    setOauthParams(savedRequest, form, params, approvalParams, false);
                    
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
            String stateParam = null;
            
            if(savedRequest != null && savedRequest.getParameterMap() != null && savedRequest.getParameterValues("state") != null) {
                if(savedRequest.getParameterValues("state").length > 0)
                    stateParam = savedRequest.getParameterValues("state")[0];
            }
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue(), stateParam)));
        }

        return form;
    }

    @RequestMapping(value = "/custom/register/empty.json", method = RequestMethod.GET)
    public @ResponseBody
    OauthRegistration getRegister(HttpServletRequest request, HttpServletResponse response) {
        // Remove the session hash if needed
        if (request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
            request.getSession().removeAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME);
        }
        
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
    OauthRegistration checkRegisterForm(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthRegistration form) {
        form.setErrors(new ArrayList<String>());

        if (form.getApproved()) {
            registrationController.validateRegistrationFields(request, form);
            registrationController.validateGrcaptcha(request, form);
        } else {
            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
            String stateParam = null;
            
            if(savedRequest != null && savedRequest.getParameterMap() != null && savedRequest.getParameterValues("state") != null) {
                if(savedRequest.getParameterValues("state").length > 0)
                    stateParam = savedRequest.getParameterValues("state")[0];
            }
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue(), stateParam)));
        }
        return form;
    }

    @RequestMapping(value = "/custom/registerConfirm.json", method = RequestMethod.POST)
    public @ResponseBody
    OauthRegistration registerAndAuthorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthRegistration form) {                
        if (form.getApproved()) {
            boolean usedCaptcha = false;

            // If recatcha wasn't loaded do nothing. This is for countries that
            // block google.
            if (form.getGrecaptchaWidgetId().getValue() != null) {
                // If the captcha verified key is not in the session, redirect to
                // the login page
                if (request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME) == null || PojoUtil.isEmpty(form.getGrecaptcha())
                        || !encryptionManager.encryptForExternalUse(form.getGrecaptcha().getValue()).equals(request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME))) {
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
                    if(!PojoUtil.isEmpty(request.getParameter("state")))                    
                        redirectUri += "&state=" + request.getParameter("state");
                    form.setRedirectUri(Text.valueOf(redirectUri));
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
                // Put all request params into the params
                SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                
                // Set params 
                setOauthParams(savedRequest, form, params, approvalParams, true);
                
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
                    return form;
                }
                // Approve
                RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                form.setRedirectUri(Text.valueOf(view.getUrl()));
            }
        } else {
            form.setRedirectUri(Text.valueOf(buildDenyRedirectUri(form.getRedirectUri().getValue(), request.getParameter("state"))));
        }

        return form;
    }

    /**
     * Set the needed params for the Oauth request
     * 
     * @param savedRequest
     * @param form
     * @param params
     * @param approvalParams
     * @param justRegistred
     * */
    private void setOauthParams(SavedRequest savedRequest, OauthForm form, Map<String, String> params, Map<String, String> approvalParams, boolean justRegistred) {
        // Put all request params into the params
        copyRequestParameters(savedRequest, params);
        // Then, put the custom authorization params
        // Token version
        params.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);
        // Client ID
        params.put(CLIENT_ID_PARAM, form.getClientId().getValue());
        // Redirect URI
        if (!PojoUtil.isEmpty(form.getRedirectUri())) {
            params.put(REDIRECT_URI_PARAM, form.getRedirectUri().getValue());
        } else {
            params.put(REDIRECT_URI_PARAM, new String());
        }
        // Scope
        if (!PojoUtil.isEmpty(form.getScope())) {
            params.put(SCOPE_PARAM, form.getScope().getValue());
        }
        // Response type
        if (!PojoUtil.isEmpty(form.getResponseType())) {
            params.put(RESPONSE_TYPE_PARAM, form.getResponseType().getValue());
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
        params.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "false");
        if (hasPersistenTokensEnabled(form.getClientId().getValue())) {
            // Then check if the client granted the persistent token
            if (form.getPersistentTokenEnabled()) {
                params.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "true");
            }
        }
    }
        
    @RequestMapping(value = { "/custom/authorize.json" }, method = RequestMethod.POST)
    public @ResponseBody
    OauthAuthorizeForm authorize(HttpServletRequest request, @RequestBody OauthAuthorizeForm form) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) request.getSession().getAttribute("authorizationRequest");
        Map<String, String> requestParams = new HashMap<String, String>(authorizationRequest.getRequestParameters());
        Map<String, String> approvalParams = new HashMap<String, String>();
                        
        // Add the persistent token information        
        if (form.getApproved()) {
            requestParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
            approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
        } else {
            requestParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "false");
            approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "false");
        }
        requestParams.put(OauthTokensConstants.TOKEN_VERSION, OauthTokensConstants.PERSISTENT_TOKEN);
        // Check if the client have persistent tokens enabled
        requestParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "false");
        if (hasPersistenTokensEnabled(form.getClientId().getValue()))
            // Then check if the client granted the persistent token
            if (form.getPersistentTokenEnabled())
                requestParams.put(OauthTokensConstants.GRANT_PERSISTENT_TOKEN, "true");

        // Session status
        SimpleSessionStatus status = new SimpleSessionStatus();

        authorizationRequest.setRequestParameters(requestParams);
        // Authorization request model
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("authorizationRequest", authorizationRequest);
        
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
    private String buildDenyRedirectUri(String redirectUri, String stateParam) {        
        if (!PojoUtil.isEmpty(redirectUri)) {
            if (redirectUri.contains("?")) {
                redirectUri = redirectUri.concat("&error=access_denied&error_description=User denied access");
            } else {
                redirectUri = redirectUri.concat("?error=access_denied&error_description=User denied access");
            }
        }
        if(!PojoUtil.isEmpty(stateParam))
            redirectUri += "&state=" + stateParam;
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
            copy(savedParams, params);
        }
    }
    
    /**
     * Copies all request parameters into the provided params map
     * 
     * @param request
     *            The server request
     * @param params
     *            The map to copy the params
     * */
    private void copyRequestParameters(HttpServletRequest request, Map<String, String> params) {
        if (request != null && request.getParameterMap() != null) {
            Map<String, String[]> savedParams = request.getParameterMap();
            copy(savedParams, params);
        }
    }
    
    private void copy(Map<String, String[]> savedParams, Map<String, String> params) {
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
        registrationController.regEmailValidate(request, reg, true, false);
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
            throw new IllegalArgumentException(getMessage("web.orcid.oauth_invalid_client.exception"));
        return clientDetails.isPersistentTokensEnabled();
    }
}
