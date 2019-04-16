package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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

@Controller
public class ClaimController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimController.class);

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Value("${org.orcid.core.claimWaitPeriodDays:10}")
    private int claimWaitPeriodDays;
    
    @Value("${org.orcid.core.claimReminderAfterDays:8}")
    private int claimReminderAfterDays;
    
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

    @RequestMapping(value = "/claimTermsOfUseValidate.json", method = RequestMethod.POST)
    public @ResponseBody Claim claimTermsOfUseValidate(@RequestBody Claim claim) {
        termsOfUserValidate(claim.getTermsOfUse());
        return claim;
    }

    @RequestMapping(value = "/claim/{encryptedEmail}.json", method = RequestMethod.GET)
    public @ResponseBody Claim verifyClaimJson(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws UnsupportedEncodingException {
        Claim c = new Claim();
        c.getSendChangeNotifications().setValue(true);
        c.getSendOrcidNews().setValue(false);
        c.getTermsOfUse().setValue(false);
        claimTermsOfUseValidate(c);
        return c;
    }

    @RequestMapping(value = "/claim/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyClaim(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws UnsupportedEncodingException {
        try {
            String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
            if (!isEmailOkForCurrentUser(decryptedEmail)) {
                return new ModelAndView("wrong_user");
            }

            if (profileEntityManager.isProfileClaimedByEmail(decryptedEmail)) {
                return new ModelAndView("redirect:/signin?alreadyClaimed");
            }
            
            ModelAndView result = new ModelAndView("claim");
            result.addObject("noIndex", true);
            return result;
        } catch (EncryptionOperationNotPossibleException e) {
            LOGGER.warn("Error decypting claim email from the claim profile link");
            return new ModelAndView("redirect:/signin?invalidClaimUrl");
        }
    }

    @RequestMapping(value = "/claim/{encryptedEmail}.json", method = RequestMethod.POST)
    public @ResponseBody Claim submitClaimJson(HttpServletRequest request, HttpServletResponse response, @PathVariable("encryptedEmail") String encryptedEmail,
            @RequestBody Claim claim) throws UnsupportedEncodingException {
        claim.setErrors(new ArrayList<String>());
        String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8")).trim();
        if (!isEmailOkForCurrentUser(decryptedEmail)) {
            claim.setUrl(getBaseUri() + "/claim/wrong_user");
            return claim;
        }

        String orcid = emailManager.findOrcidIdByEmail(decryptedEmail);

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
        activitiesVisibilityDefaultValidate(claim.getActivitiesVisibilityDefault());

        copyErrors(claim.getPassword(), claim);
        copyErrors(claim.getPasswordConfirm(), claim);
        copyErrors(claim.getTermsOfUse(), claim);
        copyErrors(claim.getActivitiesVisibilityDefault(), claim);

        if (claim.getErrors().size() > 0) {
            return claim;
        }

        Locale requestLocale = RequestContextUtils.getLocale(request);
        AvailableLocales userLocale = (requestLocale == null) ? null
                : AvailableLocales.fromValue(requestLocale.toString());
        
        boolean claimed = profileEntityManager.claimProfileAndUpdatePreferences(orcid, decryptedEmail, userLocale, claim);
        if (!claimed) {
            throw new IllegalStateException("Unable to claim record " + orcid);
        }                    
                
        // Notify
        notificationManager.sendAmendEmail(orcid, AmendedSection.UNKNOWN, null);
        // Log user in 
        automaticallyLogin(request, claim.getPassword().getValue(), orcid);
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

    @RequestMapping(value = "/resend-claim", method = RequestMethod.GET)
    public ModelAndView viewResendClaimEmail(@RequestParam(value = "email", required = false) String email) {
        return new ModelAndView("resend_claim");        
    }

    @RequestMapping(value = "/resend-claim.json", method = RequestMethod.POST)
    public @ResponseBody EmailRequest resendClaimEmail(@RequestBody EmailRequest resendClaimRequest) {
        resendClaimRequest.setErrors(new ArrayList<String>());
        resendClaimRequest.setSuccessMessage(null);
        String email = resendClaimRequest.getEmail();
        List<String> errors = new ArrayList<>();
        resendClaimRequest.setErrors(errors);
        
        if (!validateEmailAddress(email)) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
            return resendClaimRequest;
        }

        if (!emailManager.emailExists(email)) {
            String message = getMessage("orcid.frontend.reset.password.email_not_found_1") + " " + email + " " + getMessage("orcid.frontend.reset.password.email_not_found_2");
            message += "<a href=\"https://orcid.org/help/contact-us\">";
            message += getMessage("orcid.frontend.reset.password.email_not_found_3");
            message += "</a>";
            message += getMessage("orcid.frontend.reset.password.email_not_found_4");
            errors.add(message);
            return resendClaimRequest;
        }

        String orcid = emailManager.findOrcidIdByEmail(email);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);

        if (profile != null && profile.getClaimed()) {
            String message = getMessage("orcid.frontend.security.already_claimed_with_link_1");
            message += "<a href=\"./signin\">";
            message += getMessage("orcid.frontend.security.already_claimed_with_link_2");
            message += "</a>";
            message += getMessage("orcid.frontend.security.already_claimed_with_link_3");
            errors.add(message);
            return resendClaimRequest;
        }

        notificationManager.sendClaimReminderEmail(orcid, (claimWaitPeriodDays - claimReminderAfterDays), email);
        resendClaimRequest.setSuccessMessage(getMessage("resend_claim.successful_resend"));
        return resendClaimRequest;
    }

    private void automaticallyLogin(HttpServletRequest request, String password, String orcid) {
        UsernamePasswordAuthenticationToken token = null;
        try {
            token = new UsernamePasswordAuthenticationToken(orcid, password);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            // this should never happen
            SecurityContextHolder.getContext().setAuthentication(null);
            LOGGER.warn("User " + orcid + " should have been logged-in, but we unable to due to a problem", e, (token != null ? token.getPrincipal() : "empty principle"));
        }
    }
}
