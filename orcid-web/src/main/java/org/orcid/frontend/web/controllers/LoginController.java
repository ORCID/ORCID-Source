package org.orcid.frontend.web.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.aop.LockedException;
import org.orcid.frontend.spring.web.social.config.SocialType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.ajaxForm.Names;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    
    @Resource(name = "profileEntityManagerV3")
    protected ProfileEntityManager profileEntityManager;
    
    @Resource(name = "emailManagerReadOnlyV3")
    protected EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource(name = "recordNameManagerV3")
    private RecordNameManagerReadOnly recordNameManager;
    
    @Resource
    protected UserConnectionManager userConnectionManager;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;
    
    private final String facebookOauthUrl;

    private final String facebookTokenExchangeUrl;
    
    private final String facebookUserInfoEndpoint;
    
    public LoginController(@Value("${org.orcid.social.fb.key}") String fbKey, @Value("${org.orcid.social.fb.secret}") String fbSecret, @Value("${org.orcid.social.fb.redirectUri}") String fbRedirectUri) {
        facebookOauthUrl = "https://www.facebook.com/v3.3/dialog/oauth?client_id=" + fbKey + "&redirect_uri=" + fbRedirectUri + "&scope=email";        
        facebookTokenExchangeUrl = "https://graph.facebook.com/v3.3/oauth/access_token?client_id=" + fbKey + "&redirect_uri=" + fbRedirectUri + "&client_secret=" + fbSecret + "&code={code}";           
        facebookUserInfoEndpoint = "https://graph.facebook.com/me?access_token={access-token}&fields=id,email,name,first_name,last_name";
    }
    
    
    @RequestMapping(value = "/account/names/{type}", method = RequestMethod.GET)
    public @ResponseBody Names getAccountNames(@PathVariable String type) {
        String currentOrcid = getCurrentUserOrcid();
        Name currentName = recordNameManager.getRecordName(currentOrcid);
        if (type.equals("public") &&  !currentName.getVisibility().equals(Visibility.PUBLIC) ) {
        	currentName = null;
        }
        String currentRealOrcid = getRealUserOrcid();
        Name realName = recordNameManager.getRecordName(currentRealOrcid);
        if (type.equals("public") &&  !realName.getVisibility().equals(Visibility.PUBLIC) ) {
        	realName = null;
        }
        return Names.valueOf(currentName, realName);
    }

    @RequestMapping(value = { "/signin", "/login" }, method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String query = request.getQueryString();
        if(!PojoUtil.isEmpty(query)) {
            if(query.contains("oauth")) {
                return handleOauthSignIn(request, response);
            }
        }

        return new ModelAndView("login");
    }

    // We should go back to regular spring sign out with CSRF protection
    @RequestMapping(value = { "/signout"}, method = RequestMethod.GET)
    public ModelAndView signout(HttpServletRequest request, HttpServletResponse response) {
        // in case have come via a link that requires them to be signed out
        logoutCurrentUser(request, response);    
        String redirectString = "redirect:" + orcidUrlManager.getBaseUrl()  + "/signin";
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
        try{
            requestInfoForm = generateRequestInfoForm(queryString);
        }catch (InvalidRequestException | InvalidClientException e){
            //convert to a 400
            ModelAndView mav = new ModelAndView("oauth-error");
            mav.setStatus(HttpStatus.BAD_REQUEST);
            return mav;
        }
        
        //force a login even if the user is already logged in if openid prompt=login param present
        boolean forceLogin = false;
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString()) && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID) ){
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt!=null && prompt.equals(OrcidOauth2Constants.PROMPT_LOGIN)){
                forceLogin = true;
            }
        }
        
        // Check if user is already logged in, if so, redirect it to oauth/authorize
        OrcidProfileUserDetails userDetails = getCurrentUser();
        if(!forceLogin && userDetails != null) {
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
            authorizationEndpoint.validateScope(requestInfoForm.getScopesAsString(), clientDetails,requestInfoForm.getResponseType());
        } catch (InvalidScopeException e) {
            String redirectUriWithParams = redirectUri + "?error=invalid_scope&error_description=" + e.getMessage();
            return new ModelAndView(new RedirectView(redirectUriWithParams));
        }

        //handle openID prompt and max_age behaviour
        //here we remove prompt=login if present
        //here we remove max_age if present
        //
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString()) && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID) ){
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_NONE)){
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
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_CONFIRM)){
                //keep - handled by OAuthAuthorizeController
            }else if (prompt!=null && prompt.equals(OrcidOauth2Constants.PROMPT_LOGIN)){
                //remove because otherwise we'll end up back here again!
                queryString = removeQueryStringParams(queryString, OrcidOauth2Constants.PROMPT);
            }
            if (request.getParameter(OrcidOauth2Constants.MAX_AGE) != null) {
                //remove because otherwise we'll end up back here again!
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
    public RedirectView initFacebookLogin() {
        return new RedirectView(facebookOauthUrl);
    }
    
    @RequestMapping(value = { "/signin/facebook" }, method = RequestMethod.GET)
    public void getFacebookLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) throws UnsupportedEncodingException, IOException, JSONException {
        // Exchange the code for an access token 
        HttpURLConnection con = (HttpURLConnection) new URL(facebookTokenExchangeUrl.replace("{code}", code)).openConnection();
        con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent")+ " (orcid.org)");        
        con.setRequestMethod("GET");
        con.setInstanceFollowRedirects(true);
        int responseCode = con.getResponseCode();        
        if(responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8.name()));
            StringBuffer accessTokenResponse = new StringBuffer();
            in.lines().forEach(i -> accessTokenResponse.append(i));
            in.close();
            // Read JSON response and print
            JSONObject tokenJson = new JSONObject(response.toString());
            // Get user info from Facebook
            String accessToken = tokenJson.getString("access_token");
            Long expiresIn = tokenJson.getLong("expires_in");
            con = (HttpURLConnection) new URL(facebookUserInfoEndpoint.replace("{access-token}", accessToken)).openConnection();
            con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent")+ " (orcid.org)");        
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            int userInfoResponseCode = con.getResponseCode();  
            if(userInfoResponseCode == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8.name()));
                StringBuffer userInfoResponse = new StringBuffer();
                in.lines().forEach(i -> userInfoResponse.append(i));
                in.close();
                JSONObject userInfoJson = new JSONObject(userInfoResponse.toString());                
                String providerUserId = userInfoJson.getString("id");
                
                UserconnectionEntity userConnection = userConnectionManager.findByProviderIdAndProviderUserId(providerUserId, SocialType.FACEBOOK.value());
                if(userConnection != null && userConnection.isLinked()) {
                    // If user exists and is linked update user connection info and redirect to user record
                    updateUserConnectionAndLogUserIn(request, response, SocialType.FACEBOOK, userConnection.getOrcid(), userConnection.getId().getUserid(), providerUserId, 
                } else {
                    // Else forward to user creation
                }
                
                // Store user info
                createOrUpdateUserConnection(userInfoJson.getString("id"), "facebook", userInfoJson.getString("email"), userInfoJson.getString("name"), accessToken, expiresIn);
                
            }            
        }
    }
    
    @RequestMapping(value = { "/signin/google" }, method = RequestMethod.POST)
    public void initGoogleLogin() {
        
    }
    
    private void createOrUpdateUserConnection(String remoteUserId, String providerId, String email, String userName, String accessToken, Long expireTime) {
        
        if (userConnectionEntity == null) {
            LOGGER.info("No user connection found for remoteUserId={}, displayName={}, providerId={}",
                    new Object[] { remoteUserId, userName, providerId});
            userConnectionEntity = new UserconnectionEntity();
            String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
            UserconnectionPK pk = new UserconnectionPK(randomId, providerId, remoteUserId);                                
            userConnectionEntity.setDisplayname(userName);
            userConnectionEntity.setRank(1);
            userConnectionEntity.setId(pk);
            userConnectionEntity.setLinked(false);
            userConnectionEntity.setLastLogin(new Date());   
            userConnectionEntity.setEmail(email);
            userConnectionEntity.setAccesstoken(accessToken);
            userConnectionEntity.setExpiretime(expireTime);
            userConnectionEntity.setConnectionSatus(UserConnectionStatus.STARTED);                
            userConnectionDao.persist(userConnectionEntity);
        } else {
            LOGGER.info("Found existing user connection, {}", userConnectionEntity);
            
        }        
    }
    
    
    
    
    private ModelAndView updateUserConnectionAndLogUserIn(HttpServletRequest request, HttpServletResponse response, SocialType socialType, String userOrcid, String userConnectionId, String providerUserId, String accessToken, Long expiresIn) {
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

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ModelAndView("redirect:" + calculateRedirectUrl(request, response, false));
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @RequestMapping(value = { "/access" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response) {
        //TODO: get the connection type from signed in status
        SocialType connectionType = null;
        if (connectionType != null) {
            Map<String, String> userMap = retrieveUserDetails(connectionType);

            String providerId = connectionType.value();
            String userId = null; //TODO: where is the user id comming from?
            UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserId(userMap.get("providerUserId"), providerId);
            if (userConnectionEntity != null) {
                if (userConnectionEntity.isLinked()) {
                    ProfileEntity profile = profileEntityCacheManager.retrieve(userConnectionEntity.getOrcid());
                    if (profile.getUsing2FA()) {
                        return new ModelAndView("social_2FA");
                    }

                    UserconnectionPK pk = new UserconnectionPK(userId, providerId, userMap.get("providerUserId"));
                    String aCredentials = new StringBuffer(providerId).append(":").append(userMap.get("providerUserId")).toString();
                    PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), aCredentials);
                    token.setDetails(getOrcidProfileUserDetails(userConnectionEntity.getOrcid()));
                    Authentication authentication = authenticationManager.authenticate(token);
                    userConnectionManager.updateLoginInformation(pk);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return new ModelAndView("redirect:" + calculateRedirectUrl(request, response, false));
                } else {
                    return new ModelAndView("social_link_signin");
                }
            } else {
                throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
            }
        } else {
            throw new UsernameNotFoundException("Could not find an orcid account associated with the email id.");
        }
    }
}
