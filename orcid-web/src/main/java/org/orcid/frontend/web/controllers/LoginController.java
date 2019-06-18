package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.aop.LockedException;
import org.orcid.frontend.spring.web.social.config.SocialSignInUtils;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.frontend.spring.web.social.config.UserCookieGenerator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.ajaxForm.Names;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller("loginController")
public class LoginController extends OauthControllerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    protected OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;

    @Resource
    protected OrcidAuthorizationEndpoint authorizationEndpoint;

    @Resource(name = "recordNameManagerV3")
    private RecordNameManagerReadOnly recordNameManager;

    @Resource
    protected UserConnectionManager userConnectionManager;

    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;
    
    @Resource
    private UserCookieGenerator userCookieGenerator;

    @Resource
    private SocialSignInUtils socialSignInUtils;
    
    @RequestMapping(value = "/account/names/{type}", method = RequestMethod.GET)
    public @ResponseBody Names getAccountNames(@PathVariable String type) {
        String currentOrcid = getCurrentUserOrcid();
        Name currentName = recordNameManager.getRecordName(currentOrcid);
        if (type.equals("public") && !currentName.getVisibility().equals(Visibility.PUBLIC)) {
            currentName = null;
        }
        String currentRealOrcid = getRealUserOrcid();
        Name realName = recordNameManager.getRecordName(currentRealOrcid);
        if (type.equals("public") && !realName.getVisibility().equals(Visibility.PUBLIC)) {
            realName = null;
        }
        return Names.valueOf(currentName, realName);
    }

    @RequestMapping(value = { "/signin", "/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String query = request.getQueryString();
        if (!PojoUtil.isEmpty(query)) {
            if (query.contains("oauth")) {
                return handleOauthSignIn(request, response);
            }
        }

        return new ModelAndView("login");
    }

    // We should go back to regular spring sign out with CSRF protection
    @RequestMapping(value = { "/signout" }, method = RequestMethod.GET)
    public ModelAndView signout(HttpServletRequest request, HttpServletResponse response) {
        // in case have come via a link that requires them to be signed out
        logoutCurrentUser(request, response);
        String redirectString = "redirect:" + orcidUrlManager.getBaseUrl() + "/signin";
        ModelAndView mav = new ModelAndView(redirectString);
        return mav;
    }

    @RequestMapping("wrong-user")
    public String wrongUserHandler() {
        return "wrong_user";
    }

    @RequestMapping("/session-expired")
    public String sessionExpiredHandler() {
        return "session_expired";
    }

    private ModelAndView handleOauthSignIn(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String queryString = request.getQueryString();
        String redirectUri = null;

        // Get and save the request information form
        RequestInfoForm requestInfoForm;
        try {
            requestInfoForm = generateRequestInfoForm(queryString);
        } catch (InvalidRequestException | InvalidClientException e) {
            // convert to a 400
            ModelAndView mav = new ModelAndView("oauth-error");
            mav.setStatus(HttpStatus.BAD_REQUEST);
            return mav;
        }

        // force a login even if the user is already logged in if openid
        // prompt=login param present
        boolean forceLogin = false;
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString())
                && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID)) {
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_LOGIN)) {
                forceLogin = true;
            }
        }

        // Check if user is already logged in, if so, redirect it to
        // oauth/authorize
        OrcidProfileUserDetails userDetails = getCurrentUser();
        if (!forceLogin && userDetails != null) {
            redirectUri = orcidUrlManager.getBaseUrl() + "/oauth/authorize?";
            queryString = queryString.replace("oauth&", "");
            redirectUri = redirectUri + queryString;
            RedirectView rView = new RedirectView(redirectUri);
            return new ModelAndView(rView);
        }

        // Redirect URI
        redirectUri = requestInfoForm.getRedirectUrl();

        // Check that the client have the required permissions
        // Get client name
        String clientId = requestInfoForm.getClientId();
        if (PojoUtil.isEmpty(clientId)) {
            String redirectUriWithParams = redirectUri + "?error=invalid_client&error_description=invalid client_id";
            return new ModelAndView(new RedirectView(redirectUriWithParams));
        }
        // Validate client details
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        try {
            orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        } catch (LockedException e) {
            String redirectUriWithParams = redirectUri + "?error=client_locked&error_description=" + e.getMessage();
            return new ModelAndView(new RedirectView(redirectUriWithParams));
        }

        // validate client scopes
        try {
            authorizationEndpoint.validateScope(requestInfoForm.getScopesAsString(), clientDetails, requestInfoForm.getResponseType());
        } catch (InvalidScopeException e) {
            String redirectUriWithParams = redirectUri + "?error=invalid_scope&error_description=" + e.getMessage();
            return new ModelAndView(new RedirectView(redirectUriWithParams));
        }

        // handle openID prompt and max_age behaviour
        // here we remove prompt=login if present
        // here we remove max_age if present
        //
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString())
                && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID)) {
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_NONE)) {
                String redirectUriWithParams = requestInfoForm.getRedirectUrl();

                if (requestInfoForm.getResponseType().contains(OrcidOauth2Constants.CODE_RESPONSE_TYPE))
                    redirectUriWithParams += "?";
                else
                    redirectUriWithParams += "#";

                redirectUriWithParams += "error=login_required";
                RedirectView rView = new RedirectView(redirectUriWithParams);
                ModelAndView error = new ModelAndView();
                error.setView(rView);
                return error;
            }
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_CONFIRM)) {
                // keep - handled by OAuthAuthorizeController
            } else if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_LOGIN)) {
                // remove because otherwise we'll end up back here again!
                queryString = removeQueryStringParams(queryString, OrcidOauth2Constants.PROMPT);
            }
            if (request.getParameter(OrcidOauth2Constants.MAX_AGE) != null) {
                // remove because otherwise we'll end up back here again!
                queryString = removeQueryStringParams(queryString, OrcidOauth2Constants.MAX_AGE);
            }
        }

        request.getSession().setAttribute(REQUEST_INFO_FORM, requestInfoForm);
        // Save also the original query string
        request.getSession().setAttribute(OrcidOauth2Constants.OAUTH_QUERY_STRING, queryString);
        // Save a flag to indicate this is a request from the new
        request.getSession().setAttribute(OrcidOauth2Constants.OAUTH_2SCREENS, true);

        return new ModelAndView("login");
    }

    @RequestMapping(value = { "/signin/facebook" }, method = RequestMethod.POST)
    public RedirectView initFacebookLogin(HttpServletRequest request) {
        String sessionState = UUID.randomUUID().toString();
        request.getSession().setAttribute("f_state", sessionState);
        return socialSignInUtils.initFacebookLogin(sessionState);
    }

    @RequestMapping(value = { "/signin/facebook" }, method = RequestMethod.GET)
    public ModelAndView getFacebookLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "state") String state,
            @RequestParam(name = "code", required = false) String code, @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "error_code", required = false) String errorCode, @RequestParam(name = "error_description", required = false) String errorDescription,
            @RequestParam(name = "error_reason", required = false) String errorReason) throws UnsupportedEncodingException, IOException, JSONException {
        String facebookSessionState = (String) request.getSession().getAttribute("f_state");
        if (!state.equals(facebookSessionState)) {
            LOGGER.warn("Google session state doesnt match");
            return new ModelAndView("redirect:/login");
        }

        if (StringUtils.isBlank(code)) {
            LOGGER.warn("Can't login to Facebook, {}: {}", error, errorDescription);
            return new ModelAndView("redirect:/login");
        }

        JSONObject userData = socialSignInUtils.getFacebookUserData(code);
        return processLogin(request, response, SocialType.FACEBOOK, userData);
    }

    @RequestMapping(value = { "/signin/google" }, method = RequestMethod.POST)
    public RedirectView initGoogleLogin(HttpServletRequest request) {
        String sessionState = UUID.randomUUID().toString();
        request.getSession().setAttribute("g_state", sessionState);
        return socialSignInUtils.initGoogleLogin(sessionState);
    }

    @RequestMapping(value = { "/signin/google" }, method = RequestMethod.GET)
    public ModelAndView getGoogleLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "state") String state,
            @RequestParam(name = "code", required = false) String code) throws MalformedURLException, IOException, JSONException {
        String googleSessionState = (String) request.getSession().getAttribute("g_state");
        if (!state.equals(googleSessionState)) {
            LOGGER.warn("Google session state doesnt match");
            return new ModelAndView("redirect:/login");
        }
        if (StringUtils.isBlank(code)) {
            LOGGER.warn("Can't login to Google");
            return new ModelAndView("redirect:/login");
        }

        JSONObject userData = socialSignInUtils.getGoogleUserData(code);
        return processLogin(request, response, SocialType.GOOGLE, userData);
    }

    private ModelAndView processLogin(HttpServletRequest request, HttpServletResponse response, SocialType socialType, JSONObject userData) throws JSONException {
        String providerUserId = userData.getString(OrcidOauth2Constants.PROVIDER_USER_ID);
        String accessToken = userData.getString(OrcidOauth2Constants.ACCESS_TOKEN);
        Long expiresIn = Long.valueOf(userData.getString(OrcidOauth2Constants.EXPIRES_IN));

        // Store relevant data
        socialSignInUtils.setSignedInData(request, userData);

        UserconnectionEntity userConnection = userConnectionManager.findByProviderIdAndProviderUserId(userData.getString(OrcidOauth2Constants.PROVIDER_USER_ID),
                socialType.value());
        String userConnectionId = null;
        ModelAndView view = null;
        if (userConnection != null && userConnection.isLinked()) {
            userConnectionId = userConnection.getId().getUserid();
            // If user exists and is linked update user connection info
            // and redirect to user record
            view = updateUserConnectionAndLogUserIn(request, response, socialType, userConnection.getOrcid(), userConnection.getId().getUserid(), providerUserId,
                    accessToken, expiresIn);
        } else {
            // Store user info
            userConnectionId = createUserConnection(socialType, providerUserId, userData.getString(OrcidOauth2Constants.EMAIL),
                    userData.getString(OrcidOauth2Constants.DISPLAY_NAME), accessToken, expiresIn);
            // And forward to user creation
            view = new ModelAndView(new RedirectView(orcidUrlManager.getBaseUrl() + "/social/access", true));
        }
        if (userConnectionId == null) {
            throw new IllegalArgumentException("Unable to find userConnectionId for providerUserId = " + providerUserId);
        }
        userCookieGenerator.addCookie(userConnectionId, response);
        return view;
    }

    private String createUserConnection(SocialType socialType, String providerUserId, String email, String userName, String accessToken, Long expireTime) {
        LOGGER.info("Creating userconnection for type={}, providerUserId={}, userName={}", new Object[] { socialType.value(), providerUserId, userName });
        return userConnectionManager.create(providerUserId, socialType.value(), email, userName, accessToken, expireTime);
    }

    private ModelAndView updateUserConnectionAndLogUserIn(HttpServletRequest request, HttpServletResponse response, SocialType socialType, String userOrcid,
            String userConnectionId, String providerUserId, String accessToken, Long expiresIn) {
        LOGGER.info("Updating existing userconnection for orcid={}, type={}, providerUserId={}", new Object[] { userOrcid, socialType.value(), providerUserId });
        // Update user connection info
        userConnectionManager.update(providerUserId, socialType.value(), accessToken, expiresIn);

        // Log user in
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(userOrcid);
        if (profileEntity.getUsing2FA()) {
            return new ModelAndView("social_2FA");
        }

        UserconnectionPK pk = new UserconnectionPK(userConnectionId, socialType.value(), providerUserId);
        String aCredentials = socialType.value() + ':' + providerUserId;
        PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userOrcid, aCredentials);
        token.setDetails(orcidUserDetailsService.loadUserByProfile(profileEntity));
        Authentication authentication = authenticationManager.authenticate(token);
        userConnectionManager.updateLoginInformation(pk);

        // Update security context with user information
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ModelAndView(new RedirectView(calculateRedirectUrl(request, response, false)));
    }
}
