package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.security.UnclaimedProfileExistsException;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.pojo.ajaxForm.OauthAuthorizeForm;
import org.orcid.pojo.ajaxForm.OauthRegistrationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("OauthController")
public class OauthController extends OauthControllerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OauthController.class);    
    
    @Resource
    private RegistrationController registrationController; 
    
    @RequestMapping(value = "/oauth/login/form", method = RequestMethod.GET)
    public @ResponseBody OauthAuthorizeForm getLoginForm(HttpServletRequest request, HttpServletResponse response) {
        String queryString = request.getQueryString();
        OauthAuthorizeForm empty = new OauthAuthorizeForm();
        Text emptyText = Text.valueOf(StringUtils.EMPTY);        
        empty.setPassword(emptyText);
        empty.setUserName(emptyText);
        
        Matcher redirectUriMatcher = orcidPattern.matcher(queryString);
        if (redirectUriMatcher.find()) {
            try {
                empty.setUserName(Text.valueOf(URLDecoder.decode(redirectUriMatcher.group(1), "UTF-8").trim()));
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("Unable to parse orcid param from " + queryString);
            }
        }        
        
        return empty;
    }
    
    @RequestMapping(value = "/oauth/registration/form", method = RequestMethod.GET)
    public @ResponseBody OauthRegistrationForm getRegistrationForm(HttpServletRequest request, HttpServletResponse response) {
        String queryString = request.getQueryString();
        // Remove the session hash if needed
        if (request.getSession().getAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
            request.getSession().removeAttribute(RegistrationController.GRECAPTCHA_SESSION_ATTRIBUTE_NAME);
        }
        
        Registration registration = registrationController.getRegister(request, response);
                
        Matcher givenNamesMatcher = RegistrationController.givenNamesPattern.matcher(queryString);
        if(givenNamesMatcher.find()) {
            try {
            registration.getGivenNames().setValue(URLDecoder.decode(givenNamesMatcher.group(1), "UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("Unable to parse given names from URL: " + queryString);
            }
        }
        
        Matcher familyNamesMatcher = RegistrationController.familyNamesPattern.matcher(queryString);
        if(familyNamesMatcher.find()) {
            try {
                registration.getFamilyNames().setValue(URLDecoder.decode(familyNamesMatcher.group(1), "UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("Unable to parse familiy names from URL: " + queryString);
            }
        }
        
        Matcher emailMatcher = RegistrationController.emailPattern.matcher(queryString);
        if (emailMatcher.find()) {
            try {
                registration.getEmail().setValue(URLDecoder.decode(emailMatcher.group(1), "UTF-8").trim());
            } catch (UnsupportedEncodingException e) {
                LOGGER.warn("Unable to parse email from URL: " + queryString);
            }            
        }
        
        OauthRegistrationForm empty = new OauthRegistrationForm(registration);
        // Creation type in oauth will always be member referred
        empty.setCreationType(Text.valueOf(CreationMethod.MEMBER_REFERRED.value()));
        Text emptyText = Text.valueOf(StringUtils.EMPTY);
        empty.setPassword(emptyText);
        return empty;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @RequestMapping(value = { "/oauth/login.json" }, method = RequestMethod.POST)
    public @ResponseBody OauthAuthorizeForm login(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthAuthorizeForm form) {
        String queryString = request.getQueryString();
        // Clean form errors
        form.setErrors(new ArrayList<String>());
        if (form.getApproved()) {
            // Validate name and password
            validateUserNameAndPassword(form);
            if (form.getErrors().isEmpty()) {
                try {
                    authenticateUser(request, form.getUserName().getValue(), form.getPassword().getValue());
                    String redirectUri = orcidUrlManager.getBaseUrl() + "/oauth/authorize.json?" + queryString;
                    form.setRedirectUrl(redirectUri);
                } catch (AuthenticationException ae) {
                    if(ae.getCause() instanceof DisabledException){
                        // Handle this message in angular to allow AJAX action
                        form.getErrors().add("orcid.frontend.security.orcid_deactivated");
                    } else if(ae.getCause() instanceof UnclaimedProfileExistsException) {
                        String email = PojoUtil.isEmpty(form.getUserName()) ? null : form.getUserName().getValue();
                        String resendEmailUrl = createResendClaimUrl(email, request);
                        String errorMessage = getMessage("orcid.frontend.security.unclaimed_exists");
                        errorMessage = errorMessage.replace("{{resendClaimUrl}}", resendEmailUrl);
                        form.getErrors().add(errorMessage);
                    } else {
                        form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
                    }                                            
                }
            }
        } else {
            String redirectUri = null;
            String stateParam = null;
            Matcher redirectUriMatcher = redirectUriPattern.matcher(queryString);
            if (redirectUriMatcher.find()) {
                try {
                    redirectUri = URLDecoder.decode(redirectUriMatcher.group(1), "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                    LOGGER.warn("Unable to parse redirect uri param from " + queryString);
                }
            }
            
            Matcher stateParamMatcher = stateParamPattern.matcher(queryString);
            if (stateParamMatcher.find()) {
                try {
                    stateParam = URLDecoder.decode(stateParamMatcher.group(1), "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                    LOGGER.warn("Unable to parse state param param from " + queryString);
                }
            }
            redirectUri = buildDenyRedirectUri(redirectUri, stateParam);
            form.setRedirectUrl(redirectUri);
            LOGGER.info("OauthConfirmAccessController form.getRedirectUri being sent to client browser: " + redirectUri);
        }               
        
        return form;
    }
    
    @RequestMapping(value = { "/oauth/authorize.json" }, method = RequestMethod.GET)
    public ModelAndView authorize(HttpServletRequest request, HttpServletResponse response, @RequestBody OauthAuthorizeForm form) {
        ModelAndView mav = new ModelAndView("oauth/authorize");
        mav.addObject("hideUserVoiceScript", true);
        return mav;
    }
    
    private void validateUserNameAndPassword(OauthAuthorizeForm form) {
        if (PojoUtil.isEmpty(form.getUserName()) || PojoUtil.isEmpty(form.getPassword())) {
            form.getErrors().add(getMessage("orcid.frontend.security.bad_credentials"));
        }
    }  
}
