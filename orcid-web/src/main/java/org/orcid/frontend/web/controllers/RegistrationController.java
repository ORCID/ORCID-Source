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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.frontend.spring.ShibbolethAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.SocialAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.web.controllers.helper.SearchOrcidSolrCriteria;
import org.orcid.frontend.web.util.RecaptchaVerifier;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.pojo.DupicateResearcher;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidRequestUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author Will Simpson
 */
@Controller
public class RegistrationController extends BaseController {
    public static Pattern givenNamesPattern = Pattern.compile("given_names=([^&]*)");
    public static Pattern familyNamesPattern = Pattern.compile("family_names=([^&]*)");
    public static Pattern emailPattern = Pattern.compile("email=([^&]*)");

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    final static Integer DUP_SEARCH_START = 0;

    final static Integer DUP_SEARCH_ROWS = 25;

    public final static String GRECAPTCHA_SESSION_ATTRIBUTE_NAME = "verified-recaptcha";

    private static Random rand = new Random();

    @Resource
    private RegistrationManager registrationManager;

    @Resource
    private AuthenticationManager authenticationManager;    

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private RecaptchaVerifier recaptchaVerifier;

    @Resource
    private InternalSSOManager internalSSOManager;

    @Autowired
    private SocialContext socialContext;

    @Resource
    private SocialAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerSocial;

    @Resource
    private ShibbolethAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerShibboleth;

    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @RequestMapping(value = "/register.json", method = RequestMethod.GET)
    public @ResponseBody Registration getRegister(HttpServletRequest request, HttpServletResponse response) {
        // Remove the session hash if needed
        if (request.getSession().getAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
            request.getSession().removeAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME);
        }
        Registration reg = new Registration();

        reg.getEmail().setRequired(true);
        reg.getEmailConfirm().setRequired(true);
        reg.getFamilyNames().setRequired(false);
        reg.getGivenNames().setRequired(true);
        reg.getSendChangeNotifications().setValue(true);
        reg.getSendOrcidNews().setValue(true);
        reg.getSendMemberUpdateRequests().setValue(true);
        reg.getSendEmailFrequencyDays().setValue(SendEmailFrequency.WEEKLY.value());
        reg.getTermsOfUse().setValue(false);        
        setError(reg.getTermsOfUse(), "validations.acceptTermsAndConditions");
        
        RequestInfoForm requestInfoForm = (RequestInfoForm) request.getSession().getAttribute(OauthControllerBase.REQUEST_INFO_FORM);
        if (requestInfoForm != null) {
            if (!PojoUtil.isEmpty(requestInfoForm.getUserEmail())) {
                reg.getEmail().setValue(requestInfoForm.getUserEmail());
            }

            if (!PojoUtil.isEmpty(requestInfoForm.getUserGivenNames())) {
                reg.getGivenNames().setValue(requestInfoForm.getUserGivenNames());
            }

            if (!PojoUtil.isEmpty(requestInfoForm.getUserFamilyNames())) {
                reg.getFamilyNames().setValue(requestInfoForm.getUserFamilyNames());
            }
        }

        long numVal = generateRandomNumForValidation();
        reg.setValNumServer(numVal);
        reg.setValNumClient(0);
        return reg;
    }

    public long generateRandomNumForValidation() {
        int numCheck = rand.nextInt(1000000);
        if (numCheck % 2 != 0)
            numCheck += 1;
        return numCheck;
    }

    @RequestMapping(value = "/register.json", method = RequestMethod.POST)
    public @ResponseBody Registration setRegister(HttpServletRequest request, @RequestBody Registration reg) {
        validateRegistrationFields(request, reg);
        validateGrcaptcha(request, reg);
        return reg;
    }

    public void validateGrcaptcha(HttpServletRequest request, @RequestBody Registration reg) {
        // If recatcha wasn't loaded do nothing. This is for countries that
        // block google.
        if (reg.getGrecaptchaWidgetId().getValue() != null) {
            if (reg.getGrecaptcha() == null) {
                reg.setGrecaptcha(new Text());
                reg.getGrecaptcha().setErrors(new ArrayList<String>());
                setError(reg.getGrecaptcha(), "registrationForm.recaptcha.error");
                setError(reg, "registrationForm.recaptcha.error");
            } else {
                reg.getGrecaptcha().setErrors(new ArrayList<String>());
            }

            if (request.getSession().getAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
                if (!reg.getGrecaptcha().getValue().equals(request.getSession().getAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME))) {
                    setError(reg.getGrecaptcha(), "registrationForm.recaptcha.error");
                    setError(reg, "registrationForm.recaptcha.error");
                }
            } else if (!recaptchaVerifier.verify(reg.getGrecaptcha().getValue())) {
                reg.getGrecaptcha().setErrors(new ArrayList<String>());
                setError(reg.getGrecaptcha(), "registrationForm.recaptcha.error");
                setError(reg, "registrationForm.recaptcha.error");
            } else {
                request.getSession().setAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME, reg.getGrecaptcha().getValue());
            }
        }
    }

    @RequestMapping(value = { "/registerConfirm.json", "/shibboleth/registerConfirm.json" }, method = RequestMethod.POST)
    public @ResponseBody Redirect setRegisterConfirm(HttpServletRequest request, HttpServletResponse response, @RequestBody Registration reg)
            throws UnsupportedEncodingException {
        Redirect r = new Redirect();

        boolean usedCaptcha = false;

        // If recatcha wasn't loaded do nothing. This is for countries that
        // block google.
        if (reg.getGrecaptchaWidgetId().getValue() != null) {
            // If the captcha verified key is not in the session, redirect to
            // the login page
            if (request.getSession().getAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME) == null || PojoUtil.isEmpty(reg.getGrecaptcha())
                    || !reg.getGrecaptcha().getValue().equals(request.getSession().getAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME))) {
                r.setUrl(getBaseUri() + "/register");
                return r;
            }

            usedCaptcha = true;
        }

        // Remove the session hash if needed
        if (request.getSession().getAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
            request.getSession().removeAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME);
        }

        // Strip any html code from names before validating them
        if (!PojoUtil.isEmpty(reg.getFamilyNames())) {
            reg.getFamilyNames().setValue(OrcidStringUtils.stripHtml(reg.getFamilyNames().getValue()));
        }

        if (!PojoUtil.isEmpty(reg.getGivenNames())) {
            reg.getGivenNames().setValue(OrcidStringUtils.stripHtml(reg.getGivenNames().getValue()));
        }

        // make sure validation still passes
        validateRegistrationFields(request, reg);
        if (reg.getErrors() != null && reg.getErrors().size() > 0) {
            r.getErrors().add("Please revalidate at /register.json");
            return r;
        }

        if (reg.getValNumServer() == 0 || reg.getValNumClient() != reg.getValNumServer() / 2) {
            r.setUrl(getBaseUri() + "/register");
            return r;
        }

        try {                    
            // Locale
            Locale locale = RequestContextUtils.getLocale(request);            
            // Ip
            String ip = OrcidRequestUtil.getIpAddress(request);            
            createMinimalRegistrationAndLogUserIn(request, response, reg, usedCaptcha, locale, ip);
        } catch(Exception e) {
            r.getErrors().add(getMessage("register.error.generalError"));
            return r;
        }
        
        if ("social".equals(reg.getLinkType()) && socialContext.isSignedIn(request, response) != null) {
            ajaxAuthenticationSuccessHandlerSocial.linkSocialAccount(request, response);
        } else if ("shibboleth".equals(reg.getLinkType())) {
            ajaxAuthenticationSuccessHandlerShibboleth.linkShibbolethAccount(request, response);
        }
        String redirectUrl = calculateRedirectUrl(request, response);
        r.setUrl(redirectUrl);
        return r;
    }

    public void validateRegistrationFields(HttpServletRequest request, Registration reg) {
        reg.setErrors(new ArrayList<String>());

        registerGivenNameValidate(reg);
        registerPasswordValidate(reg);
        registerPasswordConfirmValidate(reg);
        regEmailValidate(request, reg, false, false);
        registerTermsOfUseValidate(reg);

        copyErrors(reg.getEmailConfirm(), reg);
        copyErrors(reg.getEmail(), reg);
        copyErrors(reg.getGivenNames(), reg);
        copyErrors(reg.getPassword(), reg);
        copyErrors(reg.getPasswordConfirm(), reg);
        copyErrors(reg.getTermsOfUse(), reg);
    }

    @RequestMapping(value = "/registerPasswordConfirmValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration registerPasswordConfirmValidate(@RequestBody Registration reg) {
        passwordConfirmValidate(reg.getPasswordConfirm(), reg.getPassword());
        return reg;
    }

    @RequestMapping(value = "/registerPasswordValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration registerPasswordValidate(@RequestBody Registration reg) {
        passwordValidate(reg.getPasswordConfirm(), reg.getPassword());
        return reg;
    }    

    @RequestMapping(value = "/registerTermsOfUseValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration registerTermsOfUseValidate(@RequestBody Registration reg) {
        termsOfUserValidate(reg.getTermsOfUse());
        return reg;
    }        

    @RequestMapping(value = "/registerGivenNamesValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration registerGivenNameValidate(@RequestBody Registration reg) {
        super.givenNameValidate(reg.getGivenNames());
        return reg;
    }

    @RequestMapping(value = "/registerEmailValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration regEmailValidate(HttpServletRequest request, @RequestBody Registration reg) {
        return regEmailValidate(request, reg, false, true);
    }

    public Registration regEmailValidate(HttpServletRequest request, Registration reg, boolean isOauthRequest, boolean isKeyup) {
        reg.getEmail().setErrors(new ArrayList<String>());
        if (!isKeyup && (reg.getEmail().getValue() == null || reg.getEmail().getValue().trim().isEmpty())) {
            setError(reg.getEmail(), "Email.registrationForm.email");
        }
        String emailAddress = reg.getEmail().getValue();
        
        MapBindingResult mbr = new MapBindingResult(new HashMap<String, String>(), "Email");
        // Validate the email address is ok        
        if(!validateEmailAddress(emailAddress)) {
            String[] codes = { "Email.personalInfoForm.email" };
            String[] args = { emailAddress };
            mbr.addError(new FieldError("email", "email", emailAddress, false, codes, args, "Not vaild"));
        } else {
            //Validate duplicates 
            //If email exists
            if(emailManager.emailExists(emailAddress)) {
            	String orcid = emailManager.findOrcidIdByEmail(emailAddress);
            	String[] args = { emailAddress };
                //If it is claimed, should return a duplicated exception
                if(profileEntityManager.isProfileClaimedByEmail(emailAddress)) {                  	                	                	
                	String[] codes = null;
                	if(profileEntityManager.isDeactivated(orcid)) {
                		codes = new String[] { "orcid.frontend.verify.deactivated_email" };
                    } else {
                        codes = new String[] { "orcid.frontend.verify.duplicate_email" };
                    }                                        
                    mbr.addError(new FieldError("email", "email", emailAddress, false, codes, args, "Email already exists"));                    
                } else {
                	if(profileEntityManager.isDeactivated(orcid)) {
                		String[] codes = new String[] { "orcid.frontend.verify.deactivated_email" };
                		mbr.addError(new FieldError("email", "email", emailAddress, false, codes, args, "Email already exists"));
                	} else if(!emailManager.isAutoDeprecateEnableForEmail(emailAddress)) {
                		//If the email is not eligible for auto deprecate, we should show an email duplicated exception                        
                		String resendUrl = createResendClaimUrl(emailAddress, request);
                        String[] codes = { "orcid.frontend.verify.unclaimed_email" };
                        args = new String[] { emailAddress, resendUrl };
                		mbr.addError(new FieldError("email", "email", emailAddress, false, codes, args, "Unclaimed record exists"));                        
                    } else {
                        LOGGER.info("Email " + emailAddress + " belongs to a unclaimed record and can be auto deprecated");
                    }
                }                                
            }
        }
        
        for (ObjectError oe : mbr.getAllErrors()) {
            Object[] arguments = oe.getArguments();
            if (isOauthRequest && oe.getCode().equals("orcid.frontend.verify.duplicate_email")) {
                // XXX
                reg.getEmail().getErrors().add(getMessage("oauth.registration.duplicate_email", arguments));
            } else if (oe.getCode().equals("orcid.frontend.verify.duplicate_email")) {
                Object email = "";
                if (arguments != null && arguments.length > 0) {
                    email = arguments[0];
                }
                String link = "/signin";
                String linkType = reg.getLinkType();
                if ("social".equals(linkType)) {
                    link = "/social/access";
                } else if ("shibboleth".equals(linkType)) {
                    link = "/shibboleth/signin";
                }
                reg.getEmail().getErrors().add(getMessage(oe.getCode(), email, orcidUrlManager.getBaseUrl() + link));
            }
            else if(oe.getCode().equals("orcid.frontend.verify.deactivated_email")){
                // Handle this message in angular to allow AJAX action
                reg.getEmail().getErrors().add(oe.getCode());
            }
            else {
                reg.getEmail().getErrors().add(getMessage(oe.getCode(), oe.getArguments()));
            }
        }

        // validate confirm if already field out
        if (reg.getEmailConfirm().getValue() != null) {
            regEmailConfirmValidate(reg);
        }

        return reg;
    }

    @RequestMapping(value = "/registerEmailConfirmValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration regEmailConfirmValidate(@RequestBody Registration reg) {
        reg.getEmailConfirm().setErrors(new ArrayList<String>());
        // normalize to "" sometimes angular sends null
        if (reg.getEmail().getValue() == null)
            reg.getEmail().setValue("");
        if (reg.getEmailConfirm().getValue() == null)
            reg.getEmailConfirm().setValue("");
        if (!reg.getEmailConfirm().getValue().equalsIgnoreCase(reg.getEmail().getValue())) {
            setError(reg.getEmailConfirm(), "StringMatchIgnoreCase.registrationForm");
        }

        return reg;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("register");
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        LOGGER.debug("Saved url before registration is: " + (savedRequest != null ? savedRequest.getRedirectUrl() : " no saved request"));
        return mav;
    }

    @RequestMapping(value = "/dupicateResearcher.json", method = RequestMethod.GET)
    public @ResponseBody List<DupicateResearcher> getDupicateResearcher(@RequestParam("givenNames") String givenNames, @RequestParam("familyNames") String familyNames) {
        List<DupicateResearcher> drList = new ArrayList<DupicateResearcher>();

        List<OrcidProfile> potentialDuplicates = findPotentialDuplicatesByFirstNameLastName(givenNames, familyNames);
        for (OrcidProfile op : potentialDuplicates) {
            DupicateResearcher dr = new DupicateResearcher();
            if (op.getOrcidBio() != null) {
                if (op.getOrcidBio().getContactDetails() != null) {
                    if (op.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null) {
                        dr.setEmail(op.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
                    }
                }
                FamilyName familyName = op.getOrcidBio().getPersonalDetails().getFamilyName();
                if (familyName != null) {
                    dr.setFamilyNames(familyName.getContent());
                }
                dr.setGivenNames(op.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
                dr.setInstitution(null);
            }
            OrcidIdentifier orcidIdentifier = op.getOrcidIdentifier();
            // Everything should be reindexed with orcid-identifier by now, but
            // check for null just in case.
            if (orcidIdentifier != null) {
                dr.setOrcid(orcidIdentifier.getPath());
            }
            drList.add(dr);
        }

        return drList;
    }            

    @RequestMapping(value = "/verify-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        try {
            String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
            EmailEntity emailEntity = emailManager.find(decryptedEmail);
            String emailOrcid = emailEntity.getProfile().getId();
            if (!getCurrentUserOrcid().equals(emailOrcid)) {
                return new ModelAndView("wrong_user");
            }
            emailEntity.setVerified(true);
            emailEntity.setCurrent(true);
            emailManager.update(emailEntity);
            
            profileEntityManager.updateLocale(emailOrcid, org.orcid.jaxb.model.common_v2.Locale.fromValue(RequestContextUtils.getLocale(request).toString()));
            redirectAttributes.addFlashAttribute("emailVerified", true);
        } catch (EncryptionOperationNotPossibleException eonpe) {
            LOGGER.warn("Error decypting verify email from the verify email link");
            redirectAttributes.addFlashAttribute("invalidVerifyUrl", true);
        }
        return new ModelAndView("redirect:/my-orcid");
    }

    private List<OrcidProfile> findPotentialDuplicatesByFirstNameLastName(String firstName, String lastName) {
        LOGGER.debug("About to search for potential duplicates during registration for first name={}, last name={}", firstName, lastName);
        List<OrcidProfile> orcidProfiles = new ArrayList<OrcidProfile>();
        SearchOrcidSolrCriteria queryForm = new SearchOrcidSolrCriteria();

        queryForm.setGivenName(firstName);
        queryForm.setFamilyName(lastName);

        String query = queryForm.deriveQueryString();
        
        OrcidMessage visibleProfiles = orcidSearchManager.findOrcidsByQuery(query, DUP_SEARCH_START, DUP_SEARCH_ROWS);
        if (visibleProfiles.getOrcidSearchResults() != null) {
            for (OrcidSearchResult searchResult : visibleProfiles.getOrcidSearchResults().getOrcidSearchResult()) {
                orcidProfiles.add(searchResult.getOrcidProfile());
            }
        }
        LOGGER.debug("Found {} potential duplicates during registration for first name={}, last name={}", new Object[] { orcidProfiles.size(), firstName, lastName });
        return orcidProfiles;

    }

    private void createMinimalRegistrationAndLogUserIn(HttpServletRequest request, HttpServletResponse response, Registration registration,
            boolean usedCaptchaVerification, Locale locale, String ip) {
    	String unencryptedPassword = registration.getPassword().getValue();
    	String orcidId = createMinimalRegistration(request, registration, usedCaptchaVerification, locale, ip);
        logUserIn(request, response, orcidId, unencryptedPassword);
    }

    public void logUserIn(HttpServletRequest request, HttpServletResponse response, String orcidId, String password) {
        UsernamePasswordAuthenticationToken token = null;
        try {
            token = new UsernamePasswordAuthenticationToken(orcidId, password);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (internalSSOManager.enableCookie()) {
                // Set user cookie
                internalSSOManager.writeCookie(orcidId, request, response);
            }
        } catch (AuthenticationException e) {
            // this should never happen
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.warn("User {0} should have been logged-in, but we unable to due to a problem", e, (token != null ? token.getPrincipal() : "empty principle"));
        }
    }

    public String createMinimalRegistration(HttpServletRequest request, Registration registration, boolean usedCaptcha, Locale locale, String ip) {
        String sessionId = request.getSession() == null ? null : request.getSession().getId();
        String email = registration.getEmail().getValue();                
        
        LOGGER.debug("About to create profile from registration email={}, sessionid={}", email, sessionId);
        String newUserOrcid = registrationManager.createMinimalRegistration(registration, usedCaptcha, locale, ip);
        notificationManager.sendWelcomeEmail(newUserOrcid, email);
        request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
        LOGGER.debug("Created profile from registration orcid={}, email={}, sessionid={}",
                new Object[] { newUserOrcid, email, sessionId });
        return newUserOrcid;
    }                
}
