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
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
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

@Controller
public class ClaimController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimController.class);

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;

    @Resource
    private TransactionTemplate transactionTemplate;

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
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        Claim c = new Claim();
        c.getSendChangeNotifications().setValue(true);
        c.getSendOrcidNews().setValue(true);
        c.getTermsOfUse().setValue(false);
        claimTermsOfUseValidate(c);
        return c;
    }

    @RequestMapping(value = "/claim/{encryptedEmail}", method = RequestMethod.GET)
    public ModelAndView verifyClaim(HttpServletRequest request, @PathVariable("encryptedEmail") String encryptedEmail, RedirectAttributes redirectAttributes)
            throws NoSuchRequestHandlingMethodException, UnsupportedEncodingException {
        try {
            String decryptedEmail = encryptionManager.decryptForExternalUse(new String(Base64.decodeBase64(encryptedEmail), "UTF-8"));
            if (!isEmailOkForCurrentUser(decryptedEmail)) {
                return new ModelAndView("wrong_user");
            }

            if (profileEntityManager.isProfileClaimedByEmail(decryptedEmail)) {
                return new ModelAndView("redirect:/signin?alreadyClaimed");
            }
            return new ModelAndView("claim");
        } catch (EncryptionOperationNotPossibleException e) {
            LOGGER.warn("Error decypting claim email from the claim profile link");
            return new ModelAndView("redirect:/signin?invalidClaimUrl");
        }
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

        copyErrors(claim.getPassword(), claim);
        copyErrors(claim.getPasswordConfirm(), claim);
        copyErrors(claim.getTermsOfUse(), claim);

        if (claim.getErrors().size() > 0) {
            return claim;
        }

        // Do it in a transaction
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    Locale requestLocale = RequestContextUtils.getLocale(request);
                    org.orcid.jaxb.model.common_v2.Locale userLocale = (requestLocale == null) ? null
                            : org.orcid.jaxb.model.common_v2.Locale.fromValue(requestLocale.toString());
                    boolean claimed = profileEntityManager.claimProfileAndUpdatePreferences(orcid, decryptedEmail, userLocale, claim);
                    if (!claimed) {
                        throw new IllegalStateException("Unable to claim record " + orcid);
                    }                    
                    // Update the password
                    profileEntityManager.updatePassword(orcid, claim.getPassword().getValue());
                    // Notify
                    notificationManager.sendAmendEmail(orcid, AmendedSection.UNKNOWN, null);
                }
            });

        } catch (Exception e) {
            throw new InvalidRequestException("Unable to claim record due: " + e.getMessage(), e.getCause());
        }

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
        ModelAndView mav = new ModelAndView("resend_claim");
        return mav;
    }

    @RequestMapping(value = "/resend-claim.json", method = RequestMethod.GET)
    public @ResponseBody EmailRequest getResendClaimRequest() {
        return new EmailRequest();
    }

    @RequestMapping(value = "/validate-resend-claim.json", method = RequestMethod.POST)
    public @ResponseBody EmailRequest validateResendClaimRequest(@RequestBody EmailRequest resendClaimRequest) {
        List<String> errors = new ArrayList<>();
        resendClaimRequest.setErrors(errors);
        if (!validateEmailAddress(resendClaimRequest.getEmail())) {
            errors.add(getMessage("Email.resendClaim.invalidEmail"));
        }
        return resendClaimRequest;
    }

    @RequestMapping(value = "/resend-claim.json", method = RequestMethod.POST)
    public @ResponseBody EmailRequest resendClaimEmail(@RequestBody EmailRequest resendClaimRequest) {
        String email = resendClaimRequest.getEmail();
        List<String> errors = new ArrayList<>();
        resendClaimRequest.setErrors(errors);
        
        if (!validateEmailAddress(email)) {
            errors.add(getMessage("Email.resetPasswordForm.invalidEmail"));
            return resendClaimRequest;
        }

        if (!emailManager.emailExists(email)) {
            errors.add(getMessage("orcid.frontend.reset.password.email_not_found", email));
            return resendClaimRequest;
        }

        String orcid = emailManager.findOrcidIdByEmail(email);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);

        if (profile != null && profile.getClaimed()) {
            errors.add(getMessage("orcid.frontend.security.already_claimed_with_link"));
            return resendClaimRequest;
        }

        notificationManager.sendApiRecordCreationEmail(email, orcid);
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
            LOGGER.warn("User {0} should have been logged-in, but we unable to due to a problem", e, (token != null ? token.getPrincipal() : "empty principle"));
        }
    }
}
