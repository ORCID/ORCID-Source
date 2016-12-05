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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.utils.PasswordResetToken;
import org.orcid.frontend.spring.ShibbolethAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.SocialAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.web.controllers.helper.SearchOrcidSolrCriteria;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.frontend.web.forms.PasswordTypeAndConfirmForm;
import org.orcid.frontend.web.util.RecaptchaVerifier;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.ReferredBy;
import org.orcid.jaxb.model.message.SecurityQuestionId;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendEmailFrequency;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.notification.amended_rc3.AmendedSection;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.DupicateResearcher;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Reactivation;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidRequestUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private RegistrationManager registrationManager;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private SecurityQuestionManager securityQuestionManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EmailDao emailDao;

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
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private AdminManager adminManager;
    
    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;    
    
    @ModelAttribute("securityQuestions")
    public Map<String, String> retrieveSecurityQuestionsAsMap() {
        Map<String, String> securityQuestions = securityQuestionManager.retrieveSecurityQuestionsAsInternationalizedMap();
        Map<String, String> securityQuestionsWithMessages = new LinkedHashMap<String, String>();

        for (String key : securityQuestions.keySet()) {
            securityQuestionsWithMessages.put(key, getMessage(securityQuestions.get(key)));
        }

        return securityQuestionsWithMessages;
    }

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

    public static OrcidProfile toProfile(Registration reg, HttpServletRequest request) {
        OrcidProfile profile = new OrcidProfile();
        OrcidBio bio = new OrcidBio();

        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email(reg.getEmail().getValue()));
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(reg.getSendChangeNotifications().getValue()));
        preferences.setSendOrcidNews(new SendOrcidNews(reg.getSendOrcidNews().getValue()));
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.fromValue(reg.getActivitiesVisibilityDefault().getVisibility().value())));
        preferences.setNotificationsEnabled(DefaultPreferences.NOTIFICATIONS_ENABLED);
        if (PojoUtil.isEmpty(reg.getSendEmailFrequencyDays())) {
            preferences.setSendEmailFrequencyDays(DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS);
        } else {
            preferences.setSendEmailFrequencyDays(reg.getSendEmailFrequencyDays().getValue());
        }

        if (reg.getSendMemberUpdateRequests() == null) {
            preferences.setSendMemberUpdateRequests(DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS);
        } else {
            preferences.setSendMemberUpdateRequests(reg.getSendMemberUpdateRequests().getValue());
        }

        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName(reg.getFamilyNames().getValue()));
        personalDetails.setGivenNames(new GivenNames(reg.getGivenNames().getValue()));

        bio.setContactDetails(contactDetails);
        bio.setPersonalDetails(personalDetails);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidBio(bio);
        if (!PojoUtil.isEmpty(reg.getReferredBy()))
            internal.setReferredBy(new ReferredBy(reg.getReferredBy().getValue()));

        profile.setOrcidInternal(internal);

        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.fromValue(reg.getCreationType().getValue()));

        profile.setOrcidHistory(orcidHistory);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        profile.setPassword(reg.getPassword().getValue());

        profile.setUserLastIp(OrcidRequestUtil.getIpAddress(request));
        return profile;
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
            createMinimalRegistrationAndLogUserIn(request, response, toProfile(reg, request), usedCaptcha);
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

    @RequestMapping(value = "/claimPasswordConfirmValidate.json", method = RequestMethod.POST)
    public @ResponseBody Claim claimPasswordConfirmValidate(@RequestBody Claim claim) {
        passwordConfirmValidate(claim.getPasswordConfirm(), claim.getPassword());
        return claim;
    }

    @RequestMapping(value = "/claimPasswordValidate.json", method = RequestMethod.POST)
    public @ResponseBody Claim claimPasswordValidate(@RequestBody Claim claim) {
        passwordValidate(claim.getPasswordConfirm(), claim.getPassword());
        return claim;
    }

    private void passwordConfirmValidate(Text passwordConfirm, Text password) {
        passwordConfirm.setErrors(new ArrayList<String>());
        // validate passwords match
        if (passwordConfirm.getValue() == null || !passwordConfirm.getValue().equals(password.getValue())) {
            setError(passwordConfirm, "FieldMatch.registrationForm");
        }
    }

    private void passwordValidate(Text passwordConfirm, Text password) {
        password.setErrors(new ArrayList<String>());
        // validate password regex
        if (password.getValue() == null || !password.getValue().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(password, "Pattern.registrationForm.password");
        }

        if (passwordConfirm.getValue() != null) {
            passwordConfirmValidate(passwordConfirm, password);
        }
    }

    @RequestMapping(value = "/registerTermsOfUseValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration registerTermsOfUseValidate(@RequestBody Registration reg) {
        termsOfUserValidate(reg.getTermsOfUse());
        return reg;
    }

    @RequestMapping(value = "/claimTermsOfUseValidate.json", method = RequestMethod.POST)
    public @ResponseBody Claim claimTermsOfUseValidate(@RequestBody Claim claim) {
        termsOfUserValidate(claim.getTermsOfUse());
        return claim;
    }

    private void termsOfUserValidate(Checkbox termsOfUser) {
        termsOfUser.setErrors(new ArrayList<String>());
        if (termsOfUser.getValue() != true) {
            setError(termsOfUser, "validations.acceptTermsAndConditions");
        }
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

    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public ModelAndView resetPassword() {
        ModelAndView mav = new ModelAndView("reset_password");
        EmailAddressForm resetPasswordForm = new EmailAddressForm();
        mav.addObject(resetPasswordForm);
        return mav;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ModelAndView issuePasswordResetRequest(HttpServletRequest request, @ModelAttribute @Valid EmailAddressForm resetPasswordForm, BindingResult bindingResult) {

        String submittedEmail = resetPasswordForm.getUserEmailAddress();

        ModelAndView mav = new ModelAndView("reset_password");

        // if the email doesn't exist, or any other form errors.. don't bother
        // hitting db
        if (bindingResult.hasErrors()) {
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }

        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(submittedEmail, LoadOptions.BIO_ONLY);
        // if the email can't be found on the system, then add to errors
        if (profile == null) {

            String[] codes = { "orcid.frontend.reset.password.email_not_found" };
            String[] args = { submittedEmail };
            bindingResult.addError(new FieldError("userEmailAddress", "userEmailAddress", submittedEmail, false, codes, args, "Email not found"));
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }

        else {
            if (profile.isDeactivated()) {
                mav.addObject("disabledAccount", true);
                return mav;
            } else {
                registrationManager.resetUserPassword(submittedEmail, profile);
                mav.addObject("passwordResetSuccessful", true);
                return mav;
            }
        }
    }
    
    @RequestMapping(value = "/resend-claim", method = RequestMethod.GET)
    public ModelAndView viewResendClaimEmail(@RequestParam(value = "email", required = false) String email) {
        ModelAndView mav = new ModelAndView("resend_claim");
        EmailAddressForm emailAddressForm = new EmailAddressForm();
        emailAddressForm.setUserEmailAddress(email);
        mav.addObject(emailAddressForm);
        return mav;
    }

    @RequestMapping(value = "/resend-claim", method = RequestMethod.POST)
    public ModelAndView resendClaimEmail(HttpServletRequest request, @ModelAttribute @Valid EmailAddressForm emailAddressForm, BindingResult bindingResult) {
        String userEmailAddress = emailAddressForm.getUserEmailAddress();
        ModelAndView mav = new ModelAndView("resend_claim");
        // if the email doesn't exist, or any other form errors.. don't bother
        // hitting db
        if (bindingResult.hasErrors()) {
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        }
        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfileByEmail(userEmailAddress);
        // if the email can't be found on the system, then add to errors
        if (profile == null) {

            String[] codes = { "orcid.frontend.reset.password.email_not_found" };
            String[] args = { userEmailAddress };
            bindingResult.addError(new FieldError("userEmailAddress", "userEmailAddress", userEmailAddress, false, codes, args, "Email not found"));
            mav.addAllObjects(bindingResult.getModel());
            return mav;
        } else {
            if (profile.getOrcidHistory() != null && profile.getOrcidHistory().isClaimed()) {
                mav.addObject("alreadyClaimed", true);
                return mav;
            } else {
                notificationManager.sendApiRecordCreationEmail(userEmailAddress, profile);
                mav.addObject("claimResendSuccessful", true);
                return mav;
            }
        }
    }

    private void automaticallyLogin(HttpServletRequest request, String password, OrcidProfile orcidProfile) {
        UsernamePasswordAuthenticationToken token = null;
        try {
            String orcid = orcidProfile.getOrcidIdentifier().getPath();
            // Force refresh of profile entity to ensure new password value is
            // picked up from DB.
            profileDao.refresh(profileDao.find(orcid));
            token = new UsernamePasswordAuthenticationToken(orcid, password);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            // this should never happen
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.warn("User {0} should have been logged-in, but we unable to due to a problem", e, (token != null ? token.getPrincipal() : "empty principle"));
        }
    }

    @RequestMapping(value = "/verify-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        return buildVerificationView(request, encryptedEmail, redirectAttributes);
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView resetPasswordEmail(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setSecurityQuestionId(0);
        // otherwise, straight to the screen with the option to set a security
        // question
        return buildPasswordScreenWithOptionalSecurityQuestion(form);
    }

    @RequestMapping(value = "/reset-password-form-validate.json", method = RequestMethod.POST)
    public @ResponseBody PasswordTypeAndConfirmForm resetPasswordConfirmValidate(@RequestBody PasswordTypeAndConfirmForm resetPasswordForm) {
        resetPasswordValidateFields(resetPasswordForm.getPassword(), resetPasswordForm.getRetypedPassword());
        return resetPasswordForm;
    }

    private void resetPasswordValidateFields(Text password, Text retypedPassword) {
        if (password == null) {
            password = new Text();
        }

        if (retypedPassword == null) {
            retypedPassword = new Text();
        }
        password.setErrors(new ArrayList<String>());
        retypedPassword.setErrors(new ArrayList<String>());

        // validate password regex
        if (password.getValue() == null || !password.getValue().matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            setError(password, "Pattern.registrationForm.password");
        }

        // validate passwords match
        if (retypedPassword.getValue() != null && !retypedPassword.getValue().equals(password.getValue())) {
            setError(retypedPassword, "FieldMatch.registrationForm");
        }
    }

    @RequestMapping(value = "/password-reset.json", method = RequestMethod.GET)
    public @ResponseBody PasswordTypeAndConfirmForm getResetPassword(HttpServletRequest request) {
        PasswordTypeAndConfirmForm form = new PasswordTypeAndConfirmForm();
        form.setPassword(new Text());
        form.setRetypedPassword(new Text());
        return form;
    }

    private ModelAndView buildPasswordScreenWithOptionalSecurityQuestion(OneTimeResetPasswordForm oneTimeResetPasswordForm) {
        ModelAndView combinedView = new ModelAndView("password_one_time_reset_optional_security_questions");
        combinedView.addObject(oneTimeResetPasswordForm);
        return combinedView;
    }

    @RequestMapping(value = "/answer-security-question/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView buildAnswerSecurityQuestionView(@PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        int securityQuestionId = deriveSecurityIdByEmailForUser(passwordResetToken.getEmail());
        ModelAndView answerSecurityQuestionView = new ModelAndView("answer_security_question");
        ChangeSecurityQuestionForm changeSecurityQuestionForm = new ChangeSecurityQuestionForm();
        changeSecurityQuestionForm.setSecurityQuestionId(securityQuestionId);
        String securityQuestion = retrieveSecurityQuestionsAsMap().get(String.valueOf(securityQuestionId));
        answerSecurityQuestionView.addObject("securityQuestionText", securityQuestion);
        answerSecurityQuestionView.addObject(changeSecurityQuestionForm);
        return answerSecurityQuestionView;
    }

    @RequestMapping(value = "/answer-security-question/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView submitSecurityAnswer(@PathVariable("encryptedEmail") String encryptedEmail, @Valid ChangeSecurityQuestionForm changeSecurityQuestionForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail(), LoadOptions.INTERNAL_ONLY);

        if (bindingResult.hasErrors()) {
            ModelAndView errorView = buildAnswerSecurityQuestionView(encryptedEmail, redirectAttributes);
            errorView.addAllObjects(bindingResult.getModel());
            return errorView;
        }

        String securityAnswer = retrievedProfile.getSecurityQuestionAnswer();
        if (!changeSecurityQuestionForm.getSecurityQuestionAnswer().trim().equalsIgnoreCase(securityAnswer.trim())) {
            ModelAndView errorView = buildAnswerSecurityQuestionView(encryptedEmail, redirectAttributes);
            errorView.addObject("securityQuestionIncorrect", true);
            return errorView;
        }
        // build password standalone view
        return new ModelAndView("redirect:/one-time-password/" + encryptedEmail);
    }

    @RequestMapping(value = "/one-time-password/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView buildPasswordOneTimeResetView(@PathVariable("encryptedEmail") String encryptedEmail) {
        ModelAndView mav = new ModelAndView("password_one_time_reset");
        PasswordTypeAndConfirmForm passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        mav.addObject("passwordTypeAndConfirmForm", passwordTypeAndConfirmForm);
        return mav;
    }

    @RequestMapping(value = "/one-time-password/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView confirmPasswordOneTimeResetView(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            @Valid PasswordTypeAndConfirmForm passwordTypeAndConfirmForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        if (bindingResult.hasErrors()) {
            ModelAndView errorView = buildPasswordOneTimeResetView(encryptedEmail);
            errorView.addAllObjects(bindingResult.getModel());
            return errorView;
        }

        OrcidProfile passwordOnlyProfileUpdate = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail(), LoadOptions.INTERNAL_ONLY);
        passwordOnlyProfileUpdate.setPassword(passwordTypeAndConfirmForm.getPassword() == null ? null : passwordTypeAndConfirmForm.getPassword().getValue());
        return updatePasswordAndGoToAccountsPage(request, response, passwordOnlyProfileUpdate);
    }

    @RequestMapping(value = "/reset-password-email/{encryptedEmail}", method = RequestMethod.POST)
    public ModelAndView submitPasswordReset(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            @Valid OneTimeResetPasswordForm oneTimeResetPasswordForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(encryptedEmail);
        // double check link not expired - someone could send a curl request..
        if (isTokenExpired(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("passwordResetLinkExpired", true);
            return new ModelAndView("redirect:/reset-password");
        }

        // validate the form and redirect back to the reset view
        if (bindingResult.hasErrors()) {
            ModelAndView errorView = buildPasswordScreenWithOptionalSecurityQuestion(oneTimeResetPasswordForm);
            errorView.addAllObjects(bindingResult.getModel());
            return errorView;

        }
        // update password, and optionally question and answer
        OrcidProfile profileToUpdate = orcidProfileManager.retrieveOrcidProfileByEmail(passwordResetToken.getEmail(), LoadOptions.INTERNAL_ONLY);
        profileToUpdate.setPassword(oneTimeResetPasswordForm.getPassword().getValue());
        if (oneTimeResetPasswordForm.isSecurityDetailsPopulated()) {
            profileToUpdate.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(new SecurityQuestionId(oneTimeResetPasswordForm.getSecurityQuestionId()));
            profileToUpdate.setSecurityQuestionAnswer(oneTimeResetPasswordForm.getSecurityQuestionAnswer());
        }

        return updatePasswordAndGoToAccountsPage(request, response, profileToUpdate);

    }

    private ModelAndView updatePasswordAndGoToAccountsPage(HttpServletRequest request, HttpServletResponse response, OrcidProfile updatedProfile) {
        orcidProfileManager.updatePasswordInformation(updatedProfile);
        String redirectUrl = calculateRedirectUrl(request, response);
        return new ModelAndView("redirect:" + redirectUrl);
    }

    private Integer deriveSecurityIdByEmailForUser(String email) {
        Integer securityQuestionVal = null;
        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfileByEmail(email, LoadOptions.INTERNAL_ONLY);
        if (retrievedProfile != null) {
            SecurityQuestionId securityQuestion = retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId();
            securityQuestionVal = securityQuestion != null ? (int) retrievedProfile.getOrcidInternal().getSecurityDetails().getSecurityQuestionId().getValue() : null;

        }
        return securityQuestionVal;
    }

    private PasswordResetToken buildResetTokenFromEncryptedLink(String encryptedLink) {

        try {
            String paramsString = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedLink), "UTF-8"));
            return new PasswordResetToken(paramsString);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Could not decrypt " + encryptedLink);
            throw new RuntimeException(getMessage("web.orcid.decrypt_passwordreset.exception"));
        }

    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        Date expiryDateOfOneHourFromIssueDate = org.apache.commons.lang.time.DateUtils.addHours(passwordResetToken.getIssueDate(), 4);
        Date now = new Date();
        return (expiryDateOfOneHourFromIssueDate.getTime() < now.getTime());
    }

    @RequestMapping(value = "/claim/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyClaim(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        try {
            String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
            if (!isEmailOkForCurrentUser(decryptedEmail)) {
                return new ModelAndView("wrong_user");
            }
            OrcidProfile profileToClaim = orcidProfileManager.retrieveOrcidProfileByEmail(decryptedEmail);
            if (profileToClaim.getOrcidHistory().isClaimed()) {
                return new ModelAndView("redirect:/signin?alreadyClaimed");
            }
            ModelAndView mav = new ModelAndView("claim");
            return mav;
        } catch (EncryptionOperationNotPossibleException e) {
            LOGGER.warn("Error decypting claim email from the claim profile link");
            return new ModelAndView("redirect:/signin?invalidClaimUrl");
        }
    }

    @RequestMapping(value = "/claim/{encryptedEmail}.json", method = RequestMethod.GET)
    public @ResponseBody Claim verifyClaimJson(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        Claim c = new Claim();
        c.getSendChangeNotifications().setValue(true);
        c.getSendOrcidNews().setValue(true);
        c.getTermsOfUse().setValue(false);
        claimTermsOfUseValidate(c);
        return c;
    }

    @RequestMapping(value = "/claim/{encryptedEmail}.json", method = RequestMethod.POST)
    public @ResponseBody Claim submitClaimJson(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            @RequestBody Claim claim) throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        claim.setErrors(new ArrayList<String>());
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8")).trim();
        if (!isEmailOkForCurrentUser(decryptedEmail)) {
            claim.setUrl(getBaseUri() + "/claim/wrong_user");
            return claim;
        }

        Map<String, String> emails = emailManager.findOricdIdsByCommaSeparatedEmails(decryptedEmail);
        String orcid = emails.get(decryptedEmail);

        if (PojoUtil.isEmpty(orcid)) {
            throw new OrcidBadRequestException("Unable to find an ORCID ID for the given email: " + decryptedEmail);
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);

        if (profile != null && profile.getClaimed() != null && profile.getClaimed()) {
            // Already claimed so send to sign in page
            claim.setUrl(getBaseUri() + "/signin?alreadyClaimed");
            return claim;
        }

        claimPasswordValidate(claim);
        claimPasswordConfirmValidate(claim);
        claimTermsOfUseValidate(claim);

        copyErrors(claim.getPassword(), claim);
        copyErrors(claim.getPasswordConfirm(), claim);
        copyErrors(claim.getTermsOfUse(), claim);

        if (claim.getErrors().size() > 0) {
            return claim;
        }
        OrcidProfile orcidProfile = confirmEmailAndClaim(orcid, decryptedEmail, claim, request);
        orcidProfile.setPassword(claim.getPassword().getValue());
        orcidProfileManager.updatePasswordInformation(orcidProfile);
        automaticallyLogin(request, claim.getPassword().getValue(), orcidProfile);
        // detech this situation
        String targetUrl = orcidUrlManager.determineFullTargetUrlFromSavedRequest(request, response);
        if (targetUrl == null)
            claim.setUrl(getBaseUri() + "/my-orcid?recordClaimed");
        else
            claim.setUrl(targetUrl);
        return claim;
    }

    @RequestMapping(value = "/claim/wrong_user", method = RequestMethod.GET)
    public ModelAndView claimWrongUser(HttpServletRequest request) {
        return new ModelAndView("wrong_user");
    }

    private ModelAndView buildVerificationView(HttpServletRequest request, String encryptedEmail, RedirectAttributes redirectAttributes)
            throws UnsupportedEncodingException, NoSuchRequestHandlingMethodException {
        try {
            String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
            EmailEntity emailEntity = emailDao.find(decryptedEmail);
            String emailOrcid = emailEntity.getProfile().getId();
            if (!getCurrentUserOrcid().equals(emailOrcid)) {
                return new ModelAndView("wrong_user");
            }
            emailEntity.setVerified(true);
            emailEntity.setCurrent(true);
            emailDao.merge(emailEntity);
            profileDao.updateLocale(emailOrcid, org.orcid.jaxb.model.message.Locale.fromValue(RequestContextUtils.getLocale(request).toString()));
            redirectAttributes.addFlashAttribute("emailVerified", true);
        } catch (EncryptionOperationNotPossibleException eonpe) {
            LOGGER.warn("Error decypting verify email from the verify email link");
            redirectAttributes.addFlashAttribute("invalidVerifyUrl", true);
        }
        return new ModelAndView("redirect:/my-orcid");
    }

    @Transactional
    private OrcidProfile confirmEmailAndClaim(String orcid, String email, Claim claim, HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        Locale requestLocale = RequestContextUtils.getLocale(request);
        org.orcid.jaxb.model.message.Locale userLocale = requestLocale == null ? null : org.orcid.jaxb.model.message.Locale.fromValue(requestLocale.toString());
        boolean claimed = profileEntityManager.claimProfileAndUpdatePreferences(orcid, email, userLocale, claim);
        if (!claimed) {
            throw new IllegalStateException("Unable to claim record " + orcid);
        }

        OrcidProfile profileToReturn = orcidProfileCacheManager.retrieve(orcid);
        notificationManager.sendAmendEmail(profileToReturn, AmendedSection.UNKNOWN);
        return profileToReturn;
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

    public void createMinimalRegistrationAndLogUserIn(HttpServletRequest request, HttpServletResponse response, OrcidProfile profileToSave,
            boolean usedCaptchaVerification) {
    	String unencryptedPassword = profileToSave.getPassword();
        profileToSave = createMinimalRegistration(request, profileToSave, usedCaptchaVerification);
        String orcidId = profileToSave.getOrcidIdentifier().getPath();        
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

    public OrcidProfile createMinimalRegistration(HttpServletRequest request, OrcidProfile profileToSave, boolean usedCaptchaVerification) {
        orcidProfileManager.addLocale(profileToSave, RequestContextUtils.getLocale(request));
        String sessionId = request.getSession() == null ? null : request.getSession().getId();
        String email = profileToSave.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();

        LOGGER.debug("About to create profile from registration email={}, sessionid={}", email, sessionId);
        profileToSave = registrationManager.createMinimalRegistration(profileToSave, usedCaptchaVerification);
        notificationManager.sendWelcomeEmail(profileToSave, profileToSave.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        request.getSession().setAttribute(ManageProfileController.CHECK_EMAIL_VALIDATED, false);
        LOGGER.debug("Created profile from registration orcid={}, email={}, sessionid={}",
                new Object[] { profileToSave.getOrcidIdentifier().getPath(), email, sessionId });
        return profileToSave;
    }
    
    @RequestMapping(value = "/sendReactivation.json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void sendReactivation(@RequestParam("email") String email) {
        OrcidProfile orcidProfile = orcidProfileCacheManager.retrieve(emailManager.findOrcidIdByEmail(email));
        notificationManager.sendReactivationEmail(email, orcidProfile);
    }
    
    @RequestMapping(value = "/reactivation/{resetParams}", method = RequestMethod.GET)
    public ModelAndView reactivation(HttpServletRequest request, @PathVariable("resetParams") String resetParams, RedirectAttributes redirectAttributes) {
        PasswordResetToken passwordResetToken = buildResetTokenFromEncryptedLink(resetParams);
        ModelAndView mav = new ModelAndView("reactivation");
        if (isTokenExpired(passwordResetToken)) {
            mav.addObject("reactivationLinkExpired", true);
        }
        mav.addObject("resetParams", resetParams);
        return mav;
    }
    
    @RequestMapping(value = { "/reactivationConfirm.json", "/shibboleth/reactivationConfirm.json" }, method = RequestMethod.POST)
    public @ResponseBody Object setReactivationConfirm(HttpServletRequest request, HttpServletResponse response, @RequestBody Reactivation reg)
            throws UnsupportedEncodingException {
        Redirect r = new Redirect();

        // Strip any html code from names before validating them
        if (!PojoUtil.isEmpty(reg.getFamilyNames())) {
            reg.getFamilyNames().setValue(OrcidStringUtils.stripHtml(reg.getFamilyNames().getValue()));
        }

        if (!PojoUtil.isEmpty(reg.getGivenNames())) {
            reg.getGivenNames().setValue(OrcidStringUtils.stripHtml(reg.getGivenNames().getValue()));
        }

        // make sure validation still passes
        validateReactivationFields(request, reg);
        if (reg.getErrors() != null && reg.getErrors().size() > 0) {
            return reg;
        }

        if (reg.getValNumServer() == 0 || reg.getValNumClient() != reg.getValNumServer() / 2) {
            r.setUrl(getBaseUri() + "/register");
            return r;
        }

        reactivateAndLogUserIn(request, response, reg);
        if ("social".equals(reg.getLinkType()) && socialContext.isSignedIn(request, response) != null) {
            ajaxAuthenticationSuccessHandlerSocial.linkSocialAccount(request, response);
        } else if ("shibboleth".equals(reg.getLinkType())) {
            ajaxAuthenticationSuccessHandlerShibboleth.linkShibbolethAccount(request, response);
        }
        String redirectUrl = calculateRedirectUrl(request, response);
        r.setUrl(redirectUrl);
        return r;
    }
    
    public void validateReactivationFields(HttpServletRequest request, Registration reg) {
        reg.setErrors(new ArrayList<String>());

        registerGivenNameValidate(reg);
        registerPasswordValidate(reg);
        registerPasswordConfirmValidate(reg);
        registerTermsOfUseValidate(reg);

        copyErrors(reg.getGivenNames(), reg);
        copyErrors(reg.getPassword(), reg);
        copyErrors(reg.getPasswordConfirm(), reg);
        copyErrors(reg.getTermsOfUse(), reg);
    }

    public void reactivateAndLogUserIn(HttpServletRequest request, HttpServletResponse response, Reactivation reactivation) {
        PasswordResetToken resetParams = buildResetTokenFromEncryptedLink(reactivation.getResetParams());
        String email = resetParams.getEmail();
        String orcid = emailManager.findOrcidIdByEmail(email);
        LOGGER.info("About to reactivate record, orcid={}, email={}", orcid, email);
        String password = reactivation.getPassword().getValue();
        //Reactivate user
        profileEntityManager.reactivate(orcid, reactivation.getGivenNames().getValue(), reactivation.getFamilyNames().getValue(), password);
        //Verify email used to reactivate
        emailManager.verifyEmail(email);
        logUserIn(request, response, orcid, password);
    }
}
