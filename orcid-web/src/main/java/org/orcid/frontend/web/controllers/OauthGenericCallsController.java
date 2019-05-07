package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.oauth.OAuthError;
import org.orcid.core.oauth.OAuthErrorUtils;
import org.orcid.core.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.OauthRegistrationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jersey.core.util.MultivaluedMapImpl;

@Controller("oauthGenericCallsController")
public class OauthGenericCallsController extends OauthControllerBase {
    @Resource
    private RegistrationController registrationController;
    
    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    @Context
    private UriInfo uriInfo;
    
    @RequestMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<?> obtainOauth2TokenPost(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Enumeration<String> paramNames = request.getParameterNames();
        MultivaluedMap<String, String> formParams = new MultivaluedMapImpl();
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
    
    @RequestMapping(value = "/oauth/custom/authorize/get_request_info_form.json", method = RequestMethod.GET)
    public @ResponseBody RequestInfoForm getRequestInfoForm(HttpServletRequest request) throws UnsupportedEncodingException {                    
        RequestInfoForm requestInfoForm = null;
    
        if(request.getSession() != null && request.getSession().getAttribute(REQUEST_INFO_FORM) != null) {
            requestInfoForm = (RequestInfoForm) request.getSession().getAttribute(REQUEST_INFO_FORM);
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
