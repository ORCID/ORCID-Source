package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.oauth.OrcidRandomValueTokenServices;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller("oauthAuthorizeController")
public class OauthAuthorizeController extends OauthControllerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthAuthorizeController.class);

    @Resource
    protected OrcidRandomValueTokenServices tokenServices;
    
    @Resource 
    private OauthLoginController oauthLoginController;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    /** This is called if user is already logged in.  
     * Checks permissions have been granted to client and generates access code.
     * 
     * @param request
     * @param response
     * @param mav
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/oauth/confirm_access", method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) throws UnsupportedEncodingException {
        //Get and save the request information form

        RequestInfoForm requestInfoForm = generateRequestInfoForm(request);
        request.getSession().setAttribute(REQUEST_INFO_FORM, requestInfoForm);
                
        Boolean justRegistered = (Boolean) request.getSession().getAttribute(OrcidOauth2Constants.JUST_REGISTERED);
        if (justRegistered != null) {
            request.getSession().removeAttribute(OrcidOauth2Constants.JUST_REGISTERED);
            mav.addObject(OrcidOauth2Constants.JUST_REGISTERED, justRegistered);
        }
        
        boolean usePersistentTokens = false;

        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(requestInfoForm.getClientId());        

        // validate client scopes
        try {
            authorizationEndpoint.validateScope(requestInfoForm.getScopesAsString(), clientDetails,requestInfoForm.getResponseType());
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
        
        //Add check for prompt=login and max_age here. This is a MUST in the openid spec.
        //Add check for prompt=confirm here. This is a SHOULD in the openid spec.
        boolean forceConfirm = false;
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString()) && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID) ){
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            String maxAge = request.getParameter(OrcidOauth2Constants.MAX_AGE);
            String orcid = getEffectiveUserOrcid();
            if (maxAge!=null){
                //if maxAge+lastlogin > now, force login.  max_age is in seconds.
                java.util.Date authTime = profileEntityManager.getLastLogin(orcid); //is also on the entity.
                try{
                    long max = Long.parseLong(maxAge);        
                    if (authTime == null || ((authTime.getTime() + (max*1000)) < (new java.util.Date()).getTime())){
                        return oauthLoginController.loginGetHandler(request,response,new ModelAndView());                    
                    }                    
                }catch(NumberFormatException e){
                    //ignore
                }
            }
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_CONFIRM)){
                forceConfirm=true;
            }else if (prompt!=null && prompt.equals(OrcidOauth2Constants.PROMPT_LOGIN)){
                request.getParameterMap().remove(OrcidOauth2Constants.PROMPT);
                return oauthLoginController.loginGetHandler(request,response,new ModelAndView());
            }
        }

        // Check if the client has persistent tokens enabled
        if (clientDetails.isPersistentTokensEnabled()) {
            usePersistentTokens = true;
        }

        if (!forceConfirm && usePersistentTokens) {
            boolean tokenLongLifeAlreadyExists = tokenServices.longLifeTokenExist(requestInfoForm.getClientId(), getEffectiveUserOrcid(), OAuth2Utils.parseParameterList(requestInfoForm.getScopesAsString()));
            if (tokenLongLifeAlreadyExists) {
                AuthorizationRequest authorizationRequest = (AuthorizationRequest) request.getSession().getAttribute("authorizationRequest");
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Map<String, String> requestParams = new HashMap<String, String>();
                copyRequestParameters(request, requestParams);
                Map<String, String> approvalParams = new HashMap<String, String>();

                requestParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
                approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");

                requestParams.put(OrcidOauth2Constants.TOKEN_VERSION, OrcidOauth2Constants.PERSISTENT_TOKEN);

                boolean hasPersistent = hasPersistenTokensEnabled(requestInfoForm.getClientId());
                // Don't let non persistent clients persist
                if (!hasPersistent && "true".equals(requestParams.get(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN))){
                    requestParams.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "false");
                }
                //default to client default if not set
                if (requestParams.get(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN) == null) {
                    if (hasPersistent)
                        requestParams.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "true");
                    else
                        requestParams.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "false");
                }

                // Session status
                SimpleSessionStatus status = new SimpleSessionStatus();

                authorizationRequest.setRequestParameters(requestParams);
                // Authorization request model
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("authorizationRequest", authorizationRequest);

                // Approve using the spring authorization endpoint code. 
                //note this will also handle generting implicit tokens via getTokenGranter().grant("implicit",new ImplicitTokenRequest(tokenRequest, storedOAuth2Request));
                RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
                ModelAndView authCodeView = new ModelAndView();
                authCodeView.setView(view);
                return authCodeView;
            }
        }                                
        
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString()) && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID) ){
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt!=null && prompt.equals(OrcidOauth2Constants.PROMPT_NONE)){
                String redirectUriWithParams = requestInfoForm.getRedirectUrl();
                redirectUriWithParams += "?error=interaction_required";
                RedirectView rView = new RedirectView(redirectUriWithParams);
                ModelAndView error = new ModelAndView();
                error.setView(rView);
                return error;
            }
        }
        
        mav.addObject("hideSupportWidget", true);
        mav.addObject("originalOauth2Process", true);
        mav.setViewName("confirm-oauth-access");        
        return mav;
    }    

    @RequestMapping(value = { "/oauth/custom/authorize.json" }, method = RequestMethod.POST)
    public @ResponseBody RequestInfoForm authorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthAuthorizeForm form) {
        RequestInfoForm requestInfoForm = (RequestInfoForm) request.getSession().getAttribute(REQUEST_INFO_FORM);
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
        requestParams.put(OrcidOauth2Constants.TOKEN_VERSION, OrcidOauth2Constants.PERSISTENT_TOKEN);
        // Check if the client have persistent tokens enabled
        requestParams.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "false");
        if (hasPersistenTokensEnabled(requestInfoForm.getClientId()))
            // Then check if the client granted the persistent token
            if (form.getPersistentTokenEnabled())
                requestParams.put(OrcidOauth2Constants.GRANT_PERSISTENT_TOKEN, "true");

        // strip /email/read-private scope if user has not consented
        if (requestInfoForm.containsEmailReadPrivateScope() && !form.isEmailAccessAllowed()) {
            requestInfoForm.removeEmailReadPrivateScope();
            requestParams.put(OrcidOauth2Constants.SCOPE_PARAM, requestInfoForm.getScopesAsString());
        }
        
        // Session status
        SimpleSessionStatus status = new SimpleSessionStatus();

        authorizationRequest.setRequestParameters(requestParams);
        // Authorization request model
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("authorizationRequest", authorizationRequest);

        // Approve
        RedirectView view = (RedirectView) authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
        requestInfoForm.setRedirectUrl(view.getUrl());
        if(new HttpSessionRequestCache().getRequest(request, response) != null)
            new HttpSessionRequestCache().removeRequest(request, response);
        LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + requestInfoForm.getRedirectUrl());
        return requestInfoForm;
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
}
