package org.orcid.frontend.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.oauth.OrcidRandomValueTokenServices;
import org.orcid.core.oauth.service.OrcidAuthorizationEndpoint;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.security.aop.LockedException;
import org.orcid.frontend.web.controllers.BaseControllerUtil;
import org.orcid.frontend.web.controllers.helper.OauthHelper;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.web.bind.support.SimpleSessionStatus;
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

    @Resource
    protected OrcidRandomValueTokenServices tokenServices;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @RequestMapping(value = { "/oauth/custom/init.json" }, method = RequestMethod.POST)
    public @ResponseBody RequestInfoForm loginGetHandler(HttpServletRequest request, Map<String, Object> model, @RequestParam Map<String, String> requestParameters,
            SessionStatus sessionStatus, Principal principal) throws UnsupportedEncodingException {
        // Populate the request info form

        RequestInfoForm requestInfoForm = null;
        if(request.getSession() != null && request.getSession().getAttribute(OauthHelper.REQUEST_INFO_FORM) != null &&
            request.getSession().getAttribute("authorizationRequest") != null) {
            requestInfoForm = (RequestInfoForm) request.getSession().getAttribute(OauthHelper.REQUEST_INFO_FORM);
            if (isRequestInfoFormDifferent(requestInfoForm, request)) {
                return requestInfoForm;
            }
        }
        requestInfoForm = generateRequestInfoForm(request, request.getQueryString());
        request.getSession().setAttribute(OauthHelper.REQUEST_INFO_FORM, requestInfoForm);
        request.getSession().setAttribute("authorizationRequest", null);

        if (requestInfoForm.getError() != null) {
            return requestInfoForm;
        }
        
        // validate client scopes
        try {
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(requestInfoForm.getClientId());
            authorizationEndpoint.validateScope(requestInfoForm.getScopesAsString(), clientDetails, requestInfoForm.getResponseType());
            orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        } catch (InvalidScopeException | LockedException | InvalidClientException e) {
            if (e instanceof InvalidScopeException) {
                requestInfoForm.setError("invalid_scope");
                requestInfoForm.setErrorDescription(e.getMessage());
            } else if (e instanceof InvalidClientException) {
                requestInfoForm.setError("invalid_client");
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
        return setAuthorizationRequest(request, model, requestParameters, sessionStatus, principal, requestInfoForm);
    }

    @RequestMapping(value = { "/oauth/custom/authorize.json" }, method = RequestMethod.GET)
    public @ResponseBody RequestInfoForm requestInfoForm(HttpServletRequest request, Map<String, Object> model, @RequestParam Map<String, String> requestParameters,
            SessionStatus sessionStatus, Principal principal) throws UnsupportedEncodingException {
        RequestInfoForm requestInfoForm = oauthHelper.setUserRequestInfoForm((RequestInfoForm) request.getSession().getAttribute(OauthHelper.REQUEST_INFO_FORM));
        request.getSession().setAttribute(OauthHelper.REQUEST_INFO_FORM, requestInfoForm);
        return setAuthorizationRequest(request, model, requestParameters, sessionStatus, principal, requestInfoForm);
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
            return requestInfoForm;
        }

        // handle openID behaviour
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString())
                && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID)) {
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_NONE)) {
                SecurityContext sci = getSecurityContext(request);
                if (baseControllerUtil.getCurrentUser(sci) != null) {
                    requestInfoForm.setError("interaction_required");
                } else {
                    requestInfoForm.setError("login_required");
                }

                return requestInfoForm;
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

        //implicit id_token requests must have nonce.
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString())
                && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID)
                && request.getParameter(OAuth2Utils.RESPONSE_TYPE).contains("id_token")
                && request.getParameter(OrcidOauth2Constants.NONCE) == null) {
            requestInfoForm.setError("invalid_request");
            requestInfoForm.setErrorDescription("Implicit id_token requests must have nonce");
            return requestInfoForm;
        }

        SecurityContext sci = getSecurityContext(request);

        //Check for prompt=login and max_age. This is a MUST in the openid spec.
        //If found redirect back to the signin page.
        //Add check for prompt=confirm here. This is a SHOULD in the openid spec.
        //If found, force user to confirm permissions.
        boolean forceConfirm = false;
        if (!PojoUtil.isEmpty(requestInfoForm.getScopesAsString()) && ScopePathType.getScopesFromSpaceSeparatedString(requestInfoForm.getScopesAsString()).contains(ScopePathType.OPENID) ){
            String prompt = request.getParameter(OrcidOauth2Constants.PROMPT);
            String maxAge = request.getParameter(OrcidOauth2Constants.MAX_AGE);
            if (baseControllerUtil.getCurrentUser(sci) != null) {
                String orcid = baseControllerUtil.getCurrentUser(sci).getOrcid();
                if (maxAge != null) {
                    //if maxAge+lastlogin > now, force login.  max_age is in seconds.
                    java.util.Date authTime = profileEntityManager.getLastLogin(orcid); //is also on the entity.
                    try {
                        long max = Long.parseLong(maxAge);
                        if (authTime == null || ((authTime.getTime() + (max * 1000)) < (new java.util.Date()).getTime())) {
                            requestInfoForm.setForceLogin(true);
                        }
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                }
                if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_CONFIRM)) {
                    forceConfirm = true;
                } else if (prompt != null && prompt.equals(OrcidOauth2Constants.PROMPT_LOGIN)) {
                    requestInfoForm.setForceLogin(true);
                }
            }
        }
        boolean usePersistentTokens = false;

        // Check if the client has persistent tokens enabled
        if (clientDetails.isPersistentTokensEnabled()) {
            usePersistentTokens = true;
        }

        if (!forceConfirm && usePersistentTokens) {
            boolean tokenLongLifeAlreadyExists = tokenServices.longLifeTokenExist(requestInfoForm.getClientId(), baseControllerUtil.getCurrentUser(sci).getOrcid(), OAuth2Utils.parseParameterList(requestInfoForm.getScopesAsString()));
            if (tokenLongLifeAlreadyExists) {
                AuthorizationRequest authorizationRequest = (AuthorizationRequest) request.getSession().getAttribute("authorizationRequest");
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Map<String, String> requestParams = new HashMap<String, String>();
                copyRequestParameters(request, requestParams);
                Map<String, String> approvalParams = new HashMap<String, String>();

                requestParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");
                approvalParams.put(OAuth2Utils.USER_OAUTH_APPROVAL, "true");

                requestParams.put(OrcidOauth2Constants.TOKEN_VERSION, OrcidOauth2Constants.PERSISTENT_TOKEN);

                boolean hasPersistent = hasPersistenTokensEnabled(requestInfoForm.getClientId(), requestInfoForm);
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
                authorizationEndpoint.approveOrDeny(approvalParams, model, status, auth);
            }
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
    
    private RequestInfoForm setAuthorizationRequest(HttpServletRequest request, Map<String, Object> model, @RequestParam Map<String, String> requestParameters,
            SessionStatus sessionStatus, Principal principal, RequestInfoForm requestInfoForm) {
        SecurityContext sci = getSecurityContext(request);
        // TODO: Check if the authorizationRequest is already in the session
        if (baseControllerUtil.getCurrentUser(sci) != null) {
            // Authorize the request
            ModelAndView mav = authorizationEndpoint.authorize(model, requestParameters, sessionStatus, principal);
            RedirectView rev = (RedirectView) mav.getView();
            String url = rev.getUrl();
            String errorDescription = "error_description=";
            if (url.contains("error")) {
                requestInfoForm.setError("invalid_scope");
                requestInfoForm.setErrorDescription(url.substring(url.indexOf("error_description=") + errorDescription.length()));
            }
            AuthorizationRequest authRequest = (AuthorizationRequest) mav.getModel().get("authorizationRequest");
            request.getSession().setAttribute("authorizationRequest", authRequest);            
        }
        return requestInfoForm;
    }

    private SecurityContext getSecurityContext(HttpServletRequest request) {
        SecurityContext sci = null;
        if (request.getSession() != null) {
            sci = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        }
        return sci;
    }

    protected boolean hasPersistenTokensEnabled(String clientId, RequestInfoForm requestInfoForm) throws IllegalArgumentException {
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        if (clientDetails == null) {
            requestInfoForm.setError("invalid_client");
            requestInfoForm.setErrorDescription("invalid client_id");
            return false;
        } else {
            return clientDetails.isPersistentTokensEnabled();
        }
    }

    private void copyRequestParameters(HttpServletRequest request, Map<String, String> params) {
        if (request != null && request.getParameterMap() != null) {
            Map<String, String[]> savedParams = request.getParameterMap();
            copy(savedParams, params);
        }
    }

    protected void copy(Map<String, String[]> savedParams, Map<String, String> params) {
        if (savedParams != null && !savedParams.isEmpty()) {
            for (String key : savedParams.keySet()) {
                String[] values = savedParams.get(key);
                if (values != null && values.length > 0)
                    params.put(key, values[0]);
            }
        }
    }
    private boolean isRequestInfoFormDifferent(RequestInfoForm requestInfoForm, HttpServletRequest request) throws UnsupportedEncodingException {
        RequestInfoForm newRequestInfoForm = generateRequestInfoForm(request, request.getQueryString());
        return requestInfoForm.equals(newRequestInfoForm);
    }
}
