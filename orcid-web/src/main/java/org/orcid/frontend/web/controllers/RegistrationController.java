package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.common.manager.EventManager;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.OrcidRequestUtil;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.spring.ShibbolethAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.SocialAjaxAuthenticationSuccessHandler;
import org.orcid.frontend.spring.web.social.config.SocialSignInUtils;
import org.orcid.frontend.web.controllers.helper.OauthHelper;
import org.orcid.frontend.web.util.RecaptchaVerifier;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.EventType;
import org.orcid.pojo.Redirect;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.RequestInfoForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Resource
    private RecordEmailSender recordEmailSender;

    @Resource
    private RecaptchaVerifier recaptchaVerifier;

    @Resource
    private SocialAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerSocial;

    @Resource
    private ShibbolethAjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandlerShibboleth;

    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

    @Resource
    private SocialSignInUtils socialSignInUtils;

    @Resource
    private EventManager eventManager;

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

        registerPasswordValidate(reg);

        Boolean isOauth2ScreensRequest = (Boolean) request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_2SCREENS);
        if (isOauth2ScreensRequest != null) {
            reg.setCreationType(Text.valueOf(CreationMethod.MEMBER_REFERRED.value()));
        } else {
            reg.setCreationType(Text.valueOf(CreationMethod.DIRECT.value()));
        }

        setError(reg.getTermsOfUse(), "validations.acceptTermsAndConditions");

        RequestInfoForm requestInfoForm = (RequestInfoForm) request.getSession().getAttribute(OauthHelper.REQUEST_INFO_FORM);
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
            if (Features.EVENTS.isActive()) {
                eventManager.createEvent(EventType.NEW_REGISTRATION, request);
            }
            createMinimalRegistrationAndLogUserIn(request, response, reg, usedCaptcha, locale, ip);
        } catch (Exception e) {
            LOGGER.error("Error registering a new user", e);
            r.getErrors().add(getMessage("register.error.generalError"));
            return r;
        }

        if (OrcidOauth2Constants.SOCIAL.equals(reg.getLinkType())) {
            Map<String, String> signedInData = socialSignInUtils.getSignedInData(request, response);
            if (signedInData != null && signedInData.containsKey(OrcidOauth2Constants.PROVIDER_ID)) {
                String providerId = signedInData.get(OrcidOauth2Constants.PROVIDER_ID);
                if (OrcidOauth2Constants.FACEBOOK.equals(providerId) || OrcidOauth2Constants.GOOGLE.equals(providerId)) {
                    ajaxAuthenticationSuccessHandlerSocial.linkSocialAccount(request, response);
                }
            }
        } else if (OrcidOauth2Constants.SHIBBOLETH.equals(reg.getLinkType())) {
            ajaxAuthenticationSuccessHandlerShibboleth.linkShibbolethAccount(request, response);
        }
        String redirectUrl = calculateRedirectUrl(request, response, true);
        if (request.getQueryString() == null || request.getQueryString().isEmpty()) {
            redirectUrl = calculateRedirectUrl(request, response, true, true);
        } 
        r.setUrl(redirectUrl);
        return r;
    }

    public void validateRegistrationFields(HttpServletRequest request, Registration reg) {
        reg.setErrors(new ArrayList<String>());

        registerGivenNameValidate(reg);
        registerFamilyNameValidate(reg);
        registerPasswordValidate(reg);
        registerPasswordConfirmValidate(reg);
        registerActivitiesVisibilityDefaultValidate(reg);
        regEmailValidate(request, reg, false, false);
        registerTermsOfUseValidate(reg);

        if (reg.getAffiliationForm() != null) {
            AffiliationForm affiliationForm = reg.getAffiliationForm();
            if (!AffiliationType.EMPLOYMENT.equals(AffiliationType.fromValue(affiliationForm.getAffiliationType().getValue()))) {
                setError(affiliationForm.getAffiliationType(), "Invalid affiliation type");
            }
            if (affiliationForm.getDepartmentName() != null) {
                if (affiliationForm.getDepartmentName().getValue() != null && affiliationForm.getDepartmentName().getValue().trim().length() > 1000) {
                    setError(affiliationForm.getDepartmentName(), "common.length_less_1000");
                }
            }
            if (affiliationForm.getRoleTitle() != null) {
                if (!PojoUtil.isEmpty(affiliationForm.getRoleTitle()) && affiliationForm.getRoleTitle().getValue().trim().length() > 1000) {
                    setError(affiliationForm.getRoleTitle(), "common.length_less_1000");
                }
            }
            if (affiliationForm.getStartDate() != null) {
                if(!validDate(affiliationForm.getStartDate())) {
                    setError(affiliationForm.getStartDate(), "common.dates.invalid");
                }
            }
        }

        copyErrors(reg.getActivitiesVisibilityDefault(), reg);
        copyErrors(reg.getEmailConfirm(), reg);
        copyErrors(reg.getEmail(), reg);
        copyErrors(reg.getGivenNames(), reg);
        copyErrors(reg.getPassword(), reg);
        copyErrors(reg.getPasswordConfirm(), reg);
        copyErrors(reg.getTermsOfUse(), reg);
        if(reg.getAffiliationForm() != null && reg.getAffiliationForm().getStartDate() != null) {
            copyErrors(reg.getAffiliationForm().getStartDate(), reg);
        }

        additionalEmailsValidateOnRegister(request, reg);
        for (Text emailAdditional : reg.getEmailsAdditional()) {
            if (!PojoUtil.isEmpty(emailAdditional)) {
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
        List<Text> emails = new ArrayList<Text>(reg.getEmailsAdditional());
        emails.add(reg.getEmail());
        passwordChecklistValidate(reg.getPasswordConfirm(), reg.getPassword(), emails.stream().map(email -> email.getValue()).collect(Collectors.toList()));
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

    @RequestMapping(value = "/registerFamilyNamesValidate.json", method = RequestMethod.POST)
    public @ResponseBody Registration registerFamilyNameValidate(@RequestBody Registration reg) {
        super.familyNameValidate(reg.getFamilyNames());
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
        // Clean the email address so it doesn't contains any horizontal white spaces
        reg.getEmail().setValue(OrcidStringUtils.filterEmailAddress(reg.getEmail().getValue()));

        String emailAddress = reg.getEmail().getValue();

        // Validate the email address is ok
        if (!validateEmailAddress(emailAddress)) {
            reg.getEmail().getErrors().add(getMessage("Email.personalInfoForm.email", emailAddress));
            return reg;
        }

        if (!reg.isReactivation() && emailManager.emailExists(emailAddress)) {
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
                reg.getEmail().getErrors().add("orcid.frontend.verify.unclaimed_email");
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
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        LOGGER.debug("Saved url before registration is: " + (savedRequest != null ? savedRequest.getRedirectUrl() : " no saved request"));
        return mav;
    }

    @RequestMapping(value = "/verify-email/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyEmail(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        if (PojoUtil.isEmpty(encryptedEmail) || !Base64.isBase64(encryptedEmail)) {
            LOGGER.error("Error decypting verify email from the verify email link: {} ", encryptedEmail);
            redirectAttributes.addFlashAttribute("invalidVerifyUrl", true);
            return new ModelAndView("redirect:" + calculateRedirectUrl("/signin"));
        }
        String redirect = "redirect:" + calculateRedirectUrl("/signin");
        StringBuilder sb = new StringBuilder();
        try {
            String toDecrypt = new String(Base64.decodeBase64(encryptedEmail), "UTF-8");
            String decryptedEmail = encryptionManager.decryptForExternalUse(toDecrypt);
            if (emailManagerReadOnly.emailExists(decryptedEmail)) {
                String orcid = emailManagerReadOnly.findOrcidIdByEmail(decryptedEmail);
                String currentUser = getCurrentUserOrcid();
                if (currentUser != null && !currentUser.equals(orcid)) {
                    return new ModelAndView("wrong_user");
                }

                boolean verified = emailManager.verifyEmail(orcid, decryptedEmail);
                if (verified) {
                    profileEntityManager.updateLocale(orcid, AvailableLocales.fromValue(RequestContextUtils.getLocale(request).toString()));
                    redirectAttributes.addFlashAttribute("emailVerified", true);
                    sb.append("emailVerified=true");

                    if (!emailManagerReadOnly.isPrimaryEmail(orcid, decryptedEmail)) {
                        if (!emailManagerReadOnly.isPrimaryEmailVerified(orcid)) {
                            redirectAttributes.addFlashAttribute("primaryEmailUnverified", true);
                            sb.append("&");
                            sb.append("primaryEmailUnverified=true");
                        }
                    }
                } else {
                    redirectAttributes.addFlashAttribute("emailVerified", false);
                    sb.append("emailVerified=false");
                }

                if (currentUser != null && currentUser.equals(orcid)) {
                    redirect = "redirect:" + calculateRedirectUrl("/my-orcid?" + sb.toString());
                } else {
                    redirect = "redirect:" + calculateRedirectUrl("/signin?" + sb.toString());
                }
            }
        } catch (EncryptionOperationNotPossibleException eonpe) {
            LOGGER.warn("Error decypting verify email from the verify email link");
            redirectAttributes.addFlashAttribute("invalidVerifyUrl", true);
            sb.append("invalidVerifyUrl=true");
            redirect = "redirect:" + calculateRedirectUrl("/signin?" + sb.toString());
            SecurityContextHolder.clearContext();
        }       

        return new ModelAndView(redirect);
    }    

    private void createMinimalRegistrationAndLogUserIn(HttpServletRequest request, HttpServletResponse response, Registration registration,
            boolean usedCaptchaVerification, Locale locale, String ip) {
        String unencryptedPassword = registration.getPassword().getValue();
        String orcidId = createMinimalRegistration(request, registration, usedCaptchaVerification, locale, ip);
        logUserIn(request, response, orcidId, unencryptedPassword);
        if (registration.getAffiliationForm() != null) {
            createAffiliation(registration, orcidId);
        }
    }

    public void logUserIn(HttpServletRequest request, HttpServletResponse response, String orcidId, String password) {
        UsernamePasswordAuthenticationToken token = null;
        try {
            token = new UsernamePasswordAuthenticationToken(orcidId, password);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);            
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
        recordEmailSender.sendWelcomeEmail(newUserOrcid, email);
        recordEmailSender.sendVerificationEmailToNonPrimaryEmails(newUserOrcid);
        request.getSession().setAttribute(EmailConstants.CHECK_EMAIL_VALIDATED, false);
        LOGGER.debug("Created profile from registration orcid={}, email={}, sessionid={}", new Object[] { newUserOrcid, email, sessionId });
        return newUserOrcid;
    }

    private void createAffiliation(Registration registration, String newUserOrcid) {
        registrationManager.createAffiliation(registration, newUserOrcid);
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

    protected boolean validDate(Date date) {
        DateTimeFormatter[] formatters = {
                new DateTimeFormatterBuilder().appendPattern("yyyy").parseDefaulting(ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyyMM").parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter(),
                new DateTimeFormatterBuilder().appendPattern("yyyyMMdd").parseStrict().toFormatter() };
        String dateString = date.getYear();        
        
        // If year is blank and month or day is not, then it is invalid
        if (StringUtils.isBlank(date.getYear())) {
            if (!StringUtils.isBlank(date.getMonth()) || !StringUtils.isBlank(date.getDay())) {
                return false;
            }
        } else if (StringUtils.isBlank(date.getMonth())) {
            // If the month is empty and day is not empty, then it is invalid
            if (!StringUtils.isBlank(date.getDay())) {
                return false;
            }
        } else {
            dateString += StringUtils.leftPad(date.getMonth(), 2, '0');
            if (!StringUtils.isBlank(date.getDay())) {
                dateString += StringUtils.leftPad(date.getDay(), 2, '0');
            }
        }

        if(StringUtils.isBlank(dateString)) {
            return true;
        } else {
            for (DateTimeFormatter formatter : formatters) {
                try {
                    LocalDate.parse(dateString, formatter);
                    return true;
                } catch (DateTimeParseException e) {                    
                }
            }
        } 
        return false;
    }

}
