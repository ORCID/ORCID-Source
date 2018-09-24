package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.record.Name;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.Names;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller("loginController")
public class LoginController extends OauthControllerBase {
   
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
    private RecordNameManager recordNameManager;
    
    
    @ModelAttribute("yesNo")
    public Map<String, String> retrieveYesNoMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("true", "Yes");
        map.put("false", "No");
        return map;
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
        // in case have come via a link that requires them to be signed out        
        ModelAndView mav = new ModelAndView("login");
        boolean showLogin = true;
        String queryString = request.getQueryString();
        // Check show_login params to decide if the login form should be
        // displayed by default
        if (!PojoUtil.isEmpty(queryString) && queryString.toLowerCase().contains("show_login=false")) {
            showLogin = false;
        }   
        mav.addObject("showLogin", String.valueOf(showLogin));
        return mav;
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
                redirectUriWithParams += "?error=login_required";
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

        ModelAndView mav = new ModelAndView("login");
        boolean showLogin = false;
        // Check orcid, email and show_login params to decide if the login form should be
        // displayed by default
        // orcid and email take precedence over show_login param
        if (PojoUtil.isEmpty(requestInfoForm.getUserOrcid()) && PojoUtil.isEmpty(requestInfoForm.getUserEmail()) && queryString.toLowerCase().contains("show_login=false")) {
            showLogin = false;
        } else if (PojoUtil.isEmpty(requestInfoForm.getUserOrcid()) && PojoUtil.isEmpty(requestInfoForm.getUserEmail())) {
            showLogin = true;
        } else if (!PojoUtil.isEmpty(requestInfoForm.getUserOrcid()) && profileEntityManager.orcidExists(requestInfoForm.getUserOrcid())) {
            mav.addObject("oauth_userId", requestInfoForm.getUserOrcid());
            showLogin = true;
        } else if (!PojoUtil.isEmpty(requestInfoForm.getUserEmail())) {
            mav.addObject("oauth_userId", requestInfoForm.getUserEmail());
            if(emailManagerReadOnly.emailExists(requestInfoForm.getUserEmail())) {
                showLogin = true;
            }            
        }
        
        mav.addObject("showLogin", String.valueOf(showLogin));
        mav.addObject("hideUserVoiceScript", true);
        mav.addObject("oauth2Screens", true);
        mav.addObject("oauthRequest", true);
        return mav;
    }
}
