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
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private ProfileDao profileDao;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private OrcidProfileCacheManager orcidProfileCacheManager;

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
}
