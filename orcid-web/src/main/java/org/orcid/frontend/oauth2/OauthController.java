package org.orcid.frontend.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.security.aop.LockedException;
import org.orcid.frontend.web.controllers.BaseControllerUtil;
import org.orcid.frontend.web.controllers.helper.OauthHelper;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller("oauthController")
public class OauthController {

    private BaseControllerUtil baseControllerUtil = new BaseControllerUtil();

    @Resource
    private OauthHelper oauthHelper;

    @Resource
    private OrcidAuthorizationEndpoint authorizationEndpoint;

    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    protected OrcidUrlManager orcidUrlManager;

    @RequestMapping(value = { "/oauth/custom/init.json" }, method = RequestMethod.POST)
    public @ResponseBody RequestInfoForm loginGetHandler(HttpServletRequest request, Map<String, Object> model, @RequestParam Map<String, String> requestParameters,
            SessionStatus sessionStatus, Principal principal) throws UnsupportedEncodingException {
        // Populate the request info form
        RequestInfoForm requestInfoForm = generateRequestInfoForm(request, request.getQueryString());

        // validate client scopes
        try {
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(requestInfoForm.getClientId());
            authorizationEndpoint.validateScope(requestInfoForm.getScopesAsString(), clientDetails, requestInfoForm.getResponseType());
            orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        } catch (InvalidScopeException | LockedException e) {
            if (e instanceof InvalidScopeException) {
                requestInfoForm.setError("invalid_scope");
                requestInfoForm.setErrorDescription(e.getMessage());
            } else {
                requestInfoForm.setError("client_locked");
                requestInfoForm.setErrorDescription(e.getMessage());
            }
            return requestInfoForm;
        }

        // Populate session data
        populateSession(request, requestInfoForm);
        // Authorize the request if needed
        setAuthorizationRequest(request, model, requestParameters, sessionStatus, principal);
        return requestInfoForm;
    }

    @RequestMapping(value = { "/oauth/custom/authorize.json" }, method = RequestMethod.GET)
    public @ResponseBody RequestInfoForm requestInfoForm(HttpServletRequest request, Map<String, Object> model, @RequestParam Map<String, String> requestParameters,
            SessionStatus sessionStatus, Principal principal) throws UnsupportedEncodingException {
        RequestInfoForm requestInfoForm = oauthHelper.setUserRequestInfoForm((RequestInfoForm) request.getSession().getAttribute(OauthHelper.REQUEST_INFO_FORM));
        request.getSession().setAttribute(OauthHelper.REQUEST_INFO_FORM, requestInfoForm);
        setAuthorizationRequest(request, model, requestParameters, sessionStatus, principal);
        return requestInfoForm;
    }

    private RequestInfoForm generateRequestInfoForm(HttpServletRequest request, String queryString) throws UnsupportedEncodingException {
        // Generate the request info form
        String url = request.getQueryString();
        RequestInfoForm requestInfoForm = new RequestInfoForm();
        try {
            // Get and save the request information form
            requestInfoForm = oauthHelper.generateRequestInfoForm(url);
        } catch (InvalidRequestException | InvalidClientException e) {
            requestInfoForm.setError("oauth_error");
            requestInfoForm.setErrorDescription(e.getMessage());
        }

        // handle openID behaviour
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString())
                && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID)) {
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_NONE)) {
                requestInfoForm.setError("login_required");
            }
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
            requestInfoForm.setForceLogin(forceLogin);
        }

        // Check if user is already logged in, if so, redirect it to
        // oauth/authorize
        SecurityContext sci = getSecurityContext(request);
        OrcidProfileUserDetails userDetails = baseControllerUtil.getCurrentUser(sci);
        if (!forceLogin && userDetails != null) {
            return requestInfoForm;
        }

        // Check that the client have the required permissions
        // Get client name
        String clientId = requestInfoForm.getClientId();
        if (PojoUtil.isEmpty(clientId)) {
            requestInfoForm.setError("invalid_client");
            requestInfoForm.setErrorDescription("invalid client_id");
            return requestInfoForm;
        }

        // Validate client details
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        try {
            orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        } catch (LockedException e) {
            requestInfoForm.setError("client_locked");
            requestInfoForm.setErrorDescription(e.getMessage());
            return requestInfoForm;
        }


        return requestInfoForm;
    }

    private void populateSession(HttpServletRequest request, RequestInfoForm requestInfoForm) {
        String url = request.getQueryString();
        request.getSession().setAttribute(OauthHelper.REQUEST_INFO_FORM, requestInfoForm);
        // Save also the original query string
        request.getSession().setAttribute(OrcidOauth2Constants.OAUTH_QUERY_STRING, url);
        // TODO: We dont need the OAUTH_2SCREENS anymore after the angular
        // migration
        // Save a flag to indicate this is a request from the new
        request.getSession().setAttribute(OrcidOauth2Constants.OAUTH_2SCREENS, true);
        // Check that the client have the required permissions
        // Get client name
    }
    
    private void setAuthorizationRequest(HttpServletRequest request, Map<String, Object> model, @RequestParam Map<String, String> requestParameters,
            SessionStatus sessionStatus, Principal principal) {
        SecurityContext sci = getSecurityContext(request);
        // TODO: Check if the authorizationRequest is already in the session
        if (baseControllerUtil.getCurrentUser(sci) != null) {
            // Authorize the request
            ModelAndView mav = authorizationEndpoint.authorize(model, requestParameters, sessionStatus, principal);
            AuthorizationRequest authRequest = (AuthorizationRequest) mav.getModel().get("authorizationRequest");
            request.getSession().setAttribute("authorizationRequest", authRequest);
        }
    }

    private SecurityContext getSecurityContext(HttpServletRequest request) {
        SecurityContext sci = null;
        if (request.getSession() != null) {
            sci = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        }
        return sci;
    }

}
