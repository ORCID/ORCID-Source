package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.frontend.spring.ShibbolethAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.SocialAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.web.social.config.SocialContext;
import org.orcid.frontend.web.controllers.helper.SearchOrcidSolrCriteria;
import org.orcid.frontend.web.util.RecaptchaVerifier;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidSearchResult;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.persistence.constants.SendEmailFrequency;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
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

    @Resource(name = "orcidSearchManagerV3")
    private OrcidSearchManager orcidSearchManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource(name = "notificationManagerV3")
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

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @RequestMapping(value = "/register.json", method = RequestMethod.GET)
    public @ResponseBody Registration getRegister(HttpServletRequest request, HttpServletResponse response) {
        // Remove the session hash if needed
        if (request.getSession().getAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME) != null) {
            request.getSession().removeAttribute(GRECAPTCHA_SESSION_ATTRIBUTE_NAME);
        }
        Registration reg = new Registration();

        reg.getEmail().setRequired(true);
        reg.getEmailConfirm().setRequired(true);
        reg.getEmailsAdditional().get(0).setRequired(false);
        reg.getFamilyNames().setRequired(false);
        reg.getGivenNames().setRequired(true);
        reg.getSendChangeNotifications().setValue(true);
        reg.getSendOrcidNews().setValue(false);                   
        reg.getSendMemberUpdateRequests().setValue(true);
        reg.getSendEmailFrequencyDays().setValue(SendEmailFrequency.WEEKLY.value());
        reg.getTermsOfUse().setValue(false);   
        
        Boolean isOauth2ScreensRequest = (Boolean) request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_2SCREENS);
        if(isOauth2ScreensRequest != null) {
            reg.setCreationType(Text.valueOf(CreationMethod.MEMBER_REFERRED.value()));
        } else {
            reg.setCreationType(Text.valueOf(CreationMethod.DIRECT.value()));
        }
        
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
        Cookie justRegisteredCookie = new Cookie("justRegistered", "true");
        justRegisteredCookie.setMaxAge(30000);
        response.addCookie(justRegisteredCookie);
        String redirectUrl = calculateRedirectUrl(request, response);
        r.setUrl(redirectUrl);
        return r;
    }

    public void validateRegistrationFields(HttpServletRequest request, Registration reg) {
        reg.setErrors(new ArrayList<String>());

        registerGivenNameValidate(reg);
        registerPasswordValidate(reg);
        registerPasswordConfirmValidate(reg);
        registerActivitiesVisibilityDefaultValidate(reg);
        regEmailValidate(request, reg, false, false);
        registerTermsOfUseValidate(reg);

        copyErrors(reg.getActivitiesVisibilityDefault(), reg);
        copyErrors(reg.getEmailConfirm(), reg);
        copyErrors(reg.getEmail(), reg);
        copyErrors(reg.getGivenNames(), reg);
        copyErrors(reg.getPassword(), reg);
        copyErrors(reg.getPasswordConfirm(), reg);
        copyErrors(reg.getTermsOfUse(), reg);
        
        additionalEmailsValidateOnRegister(request, reg);
        for(Text emailAdditional : reg.getEmailsAdditional()) {
            if(!PojoUtil.isEmpty(emailAdditional)){
                copyErrors(emailAdditional, reg);
            }
        }
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
    
    @RequestMapping(value = "/registerActivitiesVisibilityDefaultValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration registerActivitiesVisibilityDefaultValidate(@RequestBody Registration reg) {
        activitiesVisibilityDefaultValidate(reg.getActivitiesVisibilityDefault());
        return reg;
    } 

    @RequestMapping(value = "/registerEmailValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration regEmailValidate(HttpServletRequest request, @RequestBody Registration reg) {
        return regEmailValidate(request, reg, false, true);
    }

    @RequestMapping(value = "/registerEmailsAdditionalValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration regEmailAdditionalValidate(HttpServletRequest request, @RequestBody Registration reg) {
        additionalEmailsValidateOnRegister(request, reg);
        return reg;
    }
    
    public Registration regEmailValidate(HttpServletRequest request, Registration reg, boolean isOauthRequest, boolean isKeyup) {
        reg.getEmail().setErrors(new ArrayList<String>());

        if (!isKeyup && (reg.getEmail().getValue() == null || reg.getEmail().getValue().trim().isEmpty())) {
            setError(reg.getEmail(), "Email.registrationForm.email");
            return reg;
        }
        
        String emailAddress = reg.getEmail().getValue();

        // Validate the email address is ok        
        if(!validateEmailAddress(emailAddress)) {
            reg.getEmail().getErrors().add(getMessage("Email.personalInfoForm.email", emailAddress));
            return reg;
        } 

        if(emailManager.emailExists(emailAddress)) {
            String orcid = emailManager.findOrcidIdByEmail(emailAddress);

            if (profileEntityManager.isDeactivated(orcid)) {
                reg.getEmail().getErrors().add("orcid.frontend.verify.deactivated_email");
                return reg;
            }
            
            if (profileEntityManager.isProfileClaimedByEmail(emailAddress)) {
                reg.getEmail().getErrors().add("orcid.frontend.verify.duplicate_email");
                return reg;
            }

            if (!emailManager.isAutoDeprecateEnableForEmail(emailAddress)) {
                // If the email is not eligible for auto deprecate, we
                // should show an email duplicated exception
                String resendUrl = createResendClaimUrl(emailAddress, request);
                String message = getVerifyUnclaimedMessage(emailAddress, resendUrl);
                reg.getEmail().getErrors().add(message);
                return reg;
            } else {
                LOGGER.info("Email " + emailAddress + " belongs to a unclaimed record and can be auto deprecated");
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
            throws UnsupportedEncodingException {
        try {
            String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));            
            if(emailManagerReadOnly.emailExists(decryptedEmail)) {
                String orcid = emailManagerReadOnly.findOrcidIdByEmail(decryptedEmail);
                if(!getCurrentUserOrcid().equals(orcid)) {
                    return new ModelAndView("wrong_user");
                }
                
                boolean verified = emailManager.verifyEmail(decryptedEmail, orcid);
                if(verified) {
                    profileEntityManager.updateLocale(decryptedEmail, org.orcid.jaxb.model.v3.rc1.common.Locale.fromValue(RequestContextUtils.getLocale(request).toString()));
                    redirectAttributes.addFlashAttribute("emailVerified", true);
                    if(!emailManagerReadOnly.isPrimaryEmail(orcid, decryptedEmail)) {
                        if (!emailManagerReadOnly.isPrimaryEmailVerified(orcid)) {
                            redirectAttributes.addFlashAttribute("primaryEmailUnverified", true);
                        }
                    }
                } else {
                    redirectAttributes.addFlashAttribute("emailVerified", false);                    
                }
            }            
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
        
        processProfileHistoryEvents(registration, newUserOrcid);
        notificationManager.sendWelcomeEmail(newUserOrcid, email);
        notificationManager.sendVerificationEmailToNonPrimaryEmails(newUserOrcid);
        request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
        LOGGER.debug("Created profile from registration orcid={}, email={}, sessionid={}",
                new Object[] { newUserOrcid, email, sessionId });
        return newUserOrcid;
    }

    private void processProfileHistoryEvents(Registration registration, String newUserOrcid) {
        // t&cs must be accepted but check just in case!
        if (registration.getTermsOfUse().getValue()) {
            profileHistoryEventManager.recordEvent(ProfileHistoryEventType.ACCEPTED_TERMS_CONDITIONS, newUserOrcid);
        }
        if (Visibility.PRIVATE.equals(registration.getActivitiesVisibilityDefault().getVisibility())) {
            profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PRIVATE, newUserOrcid);
        }
        if (Visibility.LIMITED.equals(registration.getActivitiesVisibilityDefault().getVisibility())) {
            profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_LIMITED, newUserOrcid);
        }
        if (Visibility.PUBLIC.equals(registration.getActivitiesVisibilityDefault().getVisibility())) {
            profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PUBLIC, newUserOrcid);
        }
    }            
    
}
