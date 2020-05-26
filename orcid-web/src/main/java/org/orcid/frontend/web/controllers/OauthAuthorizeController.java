package org.orcid.frontend.web.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.oauth.OrcidRandomValueTokenServices;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    
    private ModelAndView redirectToForceSignin(HttpServletRequest request) {
        String q = request.getQueryString();
        q = removeQueryStringParams(q,"prompt","max_age");
        q += "&prompt=login";
        RedirectView rView = new RedirectView(orcidUrlManager.getBaseUrl() + "/signin?oauth&" +q);
        ModelAndView m = new ModelAndView();
        m.setView(rView);
        return m;
    }
    
}
