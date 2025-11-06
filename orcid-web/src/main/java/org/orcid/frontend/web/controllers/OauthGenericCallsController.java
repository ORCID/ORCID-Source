package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.api.common.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.oauth.OAuthError;
import org.orcid.core.oauth.OAuthErrorUtils;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.util.RequestInfoFormLocalCache;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.OauthRegistrationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.orcid.core.constants.OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE;


@Controller("oauthGenericCallsController")
public class OauthGenericCallsController extends OauthControllerBase {
    @Resource
    private RegistrationController registrationController;
    
    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    @Context
    private UriInfo uriInfo;

    @Resource
    private RequestInfoFormLocalCache requestInfoFormLocalCache;

    @Resource
    private AuthorizationServerUtil authCodeExchangeForwardUtil;
    
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> obtainOauth2TokenPost(HttpServletRequest request) throws IOException, URISyntaxException, InterruptedException {
        String grantType = request.getParameter("grant_type");
        if(grantType == null) {
            OAuthError error = new OAuthError();
            error.setErrorDescription("grant_type is missing");
            error.setError(OAuthError.UNSUPPORTED_GRANT_TYPE);
            error.setResponseStatus(Response.Status.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        if(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.isActive() && AuthorizationServerUtil.AUTH_SERVER_ALLOWED_GRANT_TYPES.contains(grantType)) {
            String clientId = request.getParameter("client_id");
            String clientSecret = request.getParameter("client_secret");
            String redirectUri = request.getParameter("redirect_uri");
            String code = request.getParameter("code");
            String scopeList = request.getParameter("scope");
            String refreshToken = request.getParameter("refresh_token");
            String subjectToken = request.getParameter("subject_token");
            String subjectTokenType = request.getParameter("subject_token_type");
            String requestedTokenType = request.getParameter("requested_token_type");

            Response response = null;
            try {
                switch (grantType) {
                    case OrcidOauth2Constants.GRANT_TYPE_AUTHORIZATION_CODE:
                        response = authCodeExchangeForwardUtil.forwardAuthorizationCodeExchangeRequest(clientId, clientSecret, redirectUri, code);
                        break;
                    case OrcidOauth2Constants.GRANT_TYPE_REFRESH_TOKEN:
                        response = authCodeExchangeForwardUtil.forwardRefreshTokenRequest(clientId, clientSecret, refreshToken, scopeList);
                        break;
                    case OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS:
                        response = authCodeExchangeForwardUtil.forwardClientCredentialsRequest(clientId, clientSecret, scopeList);
                        break;
                    case IETF_EXCHANGE_GRANT_TYPE:
                        response = authCodeExchangeForwardUtil.forwardTokenExchangeRequest(clientId, clientSecret, subjectToken, subjectTokenType, requestedTokenType, scopeList);
                        break;
                }
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(Features.OAUTH_AUTHORIZATION_CODE_EXCHANGE.name(),
                        "ON");
                return ResponseEntity.status(response.getStatus()).headers(responseHeaders).body(response.getEntity());
            } catch(Exception e) {
                OAuthError error = OAuthErrorUtils.getOAuthError(e);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        } else {
            String authorization = request.getHeader("Authorization");
            Enumeration<String> paramNames = request.getParameterNames();
            MultivaluedMap<String, String> formParams = new MultivaluedHashMap<String, String>();
            while(paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                formParams.add(paramName, request.getParameter(paramName));
            }

            try {
                Response response = orcidClientCredentialEndPointDelegator.obtainOauth2Token(authorization, formParams);
                return ResponseEntity.status(response.getStatus()).body(response.getEntity());
            } catch(Exception e) {
                OAuthError error = OAuthErrorUtils.getOAuthError(e);
                HttpStatus status = HttpStatus.valueOf(error.getResponseStatus().getStatusCode());
                return ResponseEntity.status(status).body(error);
            }
        }
    }
    
    @RequestMapping(value = "/oauth/custom/authorize/get_request_info_form.json", method = RequestMethod.GET)
    public @ResponseBody RequestInfoForm getRequestInfoForm(HttpServletRequest request) throws UnsupportedEncodingException {                    
        RequestInfoForm requestInfoForm = new RequestInfoForm();
        if(requestInfoFormLocalCache.containsKey(request.getSession().getId())) {
            requestInfoForm = requestInfoFormLocalCache.get(request.getSession().getId());
        } 
        return requestInfoForm;
    }
        
    @RequestMapping(value = "/oauth/custom/authorize/empty.json", method = RequestMethod.GET)
    public @ResponseBody OauthAuthorizeForm getEmptyAuthorizeForm(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        OauthAuthorizeForm empty = new OauthAuthorizeForm();
        Text emptyText = Text.valueOf(StringUtils.EMPTY);        
        empty.setPassword(emptyText);
        empty.setUserName(emptyText);
        
        RequestInfoForm requestInfoForm = getRequestInfoForm(request);
        if(requestInfoForm != null) {
            if(!PojoUtil.isEmpty(requestInfoForm.getUserId())) {
                empty.setUserName(Text.valueOf(requestInfoForm.getUserId()));
            }
        }
        
        return empty;
    }

    /*****************************
     * Validators
     ****************************/
    @RequestMapping(value = "/oauth/custom/register/validateActivitiesVisibilityDefault.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validateActivitiesVisibilityDefaul(@RequestBody OauthRegistrationForm reg) {
        registrationController.registerActivitiesVisibilityDefaultValidate(reg);
        return reg;
    }
    
    @RequestMapping(value = "/oauth/custom/register/validatePasswordConfirm.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validatePasswordConfirm(@RequestBody OauthRegistrationForm reg) {
        registrationController.registerPasswordConfirmValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/oauth/custom/register/validatePassword.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validatePassword(@RequestBody OauthRegistrationForm reg) {
        registrationController.registerPasswordValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/oauth/custom/register/validateTermsOfUse.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validateTermsOfUse(@RequestBody OauthRegistrationForm reg) {
        registrationController.registerTermsOfUseValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/oauth/custom/register/validateGivenNames.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validateGivenName(@RequestBody OauthRegistrationForm reg) {

        registrationController.registerGivenNameValidate(reg);
        return reg;
    }

    
    @RequestMapping(value = "/oauth/custom/register/validateFamilyNames.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validateFamilyName(@RequestBody OauthRegistrationForm reg) {

        registrationController.registerFamilyNameValidate(reg);
        return reg;
    }

    @RequestMapping(value = "/oauth/custom/register/validateEmail.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validateEmail(HttpServletRequest request, @RequestBody OauthRegistrationForm reg) {
        registrationController.regEmailValidate(request, reg, true, false);
        return reg;
    }

    @RequestMapping(value = "/oauth/custom/register/validateEmailConfirm.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validateEmailConfirm(@RequestBody OauthRegistrationForm reg) {
        registrationController.regEmailConfirmValidate(reg);
        return reg;
    }     
    
    @RequestMapping(value = "/oauth/custom/register/validateEmailsAdditional.json", method = RequestMethod.POST)
    public @ResponseBody OauthRegistrationForm validateEmailsAdditional(HttpServletRequest request, @RequestBody OauthRegistrationForm reg) {
        additionalEmailsValidateOnRegister(request, reg);
        return reg;
    }
}
