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
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.OauthRegistrationForm;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orcid.api.common.server.delegator.OrcidClientCredentialEndPointDelegator;

@Controller("oauthGenericCallsController")
public class OauthGenericCallsController extends OauthControllerBase {
    @Resource
    private RegistrationController registrationController;
    
    @Resource
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;
    
    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST)
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
        } catch (Exception e) {
            return getLegacyOrcidEntity("OAuth2 problem", e);
        }
        return JsonUtils.convertToJsonString(res.getEntity());
    }
    
    @RequestMapping(value = "/oauth/custom/authorize/get_request_info_form.json", method = RequestMethod.GET)
    public @ResponseBody RequestInfoForm getRequestInfoForm(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {                    
        RequestInfoForm requestInfoForm = null;
        
        if(request.getSession() != null && request.getSession().getAttribute(REQUEST_INFO_FORM) != null) {
            requestInfoForm = (RequestInfoForm) request.getSession().getAttribute(REQUEST_INFO_FORM);
        } else {
            throw new InvalidRequestException("Unable to find parameters");
        }
        
        return requestInfoForm;
    }
        
    @RequestMapping(value = "/oauth/custom/authorize/empty.json", method = RequestMethod.GET)
    public @ResponseBody OauthAuthorizeForm getEmptyAuthorizeForm(HttpServletRequest request, HttpServletResponse response) {
        OauthAuthorizeForm empty = new OauthAuthorizeForm();
        Text emptyText = Text.valueOf(StringUtils.EMPTY);        
        empty.setPassword(emptyText);
        empty.setRedirectUri(emptyText);
        empty.setResponseType(emptyText);        
        empty.setUserName(emptyText);
        
        //Set required params empty
        empty.setStateParam(emptyText);
        empty.setClientId(emptyText);
        empty.setClientName(emptyText);
        empty.setMemberName(emptyText);
        
        //Set the state param and the client and member names
        fillOauthFormWithRequestInformation(empty, request, response);
        return empty;
    }

    /*****************************
     * Validators
     ****************************/
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
    
    private OrcidMessage getLegacyOrcidEntity(String prefix, Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setMessageVersion(OrcidMessage.DEFAULT_VERSION);
        entity.setErrorDesc(new ErrorDesc(prefix + " : " + e.getMessage()));
        return entity;
    }
}
