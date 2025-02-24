package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.v3.ClientDetailsManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SpamManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.utils.PasswordResetToken;
import org.orcid.core.utils.VerifyEmailUtils;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.web.util.PasswordConstants;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AdminChangePassword;
import org.orcid.pojo.AdminDelegatesRequest;
import org.orcid.pojo.AdminResetPasswordLink;
import org.orcid.pojo.ConvertClient;
import org.orcid.pojo.LockAccounts;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angel Montenegro
 */

@Controller
@RequestMapping(value = { "/admin-actions" })
public class AdminController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    @Resource(name = "profileEntityManagerV3")
    ProfileEntityManager profileEntityManager;

    @Resource(name = "notificationManagerV3")
    NotificationManager notificationManager;

    @Resource
    private AdminManager adminManager;

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

    @Resource(name = "clientDetailsManagerV3")
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private VerifyEmailUtils verifyEmailUtils;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource(name = "spamManager")
    SpamManager spamManager;

    @Resource
    private RecordEmailSender recordEmailSender;

    @Resource
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Value("${org.orcid.admin.registry.url:https://orcid.org}")
    private String registryUrl;

    private static final String CLAIMED = "(claimed)";
    private static final String DEACTIVATED = "(deactivated)";
    private static final String DEPRECATED = "(deprecated)";
    private static final String UNCLAIMED = "(unclaimed)";
    private static final String ENABLED_2FA = "2FAEnabled";
    private static final String INP_STRING_SEPARATOR = " \n\r\t,";
    private static final String OUT_EMAIL_PRIMARY = "*";
    private static final String OUT_STRING_SEPARATOR = "		";
    private static final String OUT_STRING_SEPARATOR_SINGLE_SPACE = " ";
    private static final String OUT_NOT_AVAILABLE = "N/A";
    private static final String OUT_NOT_AVAILABLE_ID = "N/A                ";
    private static final String OUT_NEW_LINE = "\n";

    private static final int RESET_PASSWORD_LINK_DURATION = 24;

    private void isAdmin(HttpServletRequest serverRequest, HttpServletResponse response) throws IllegalAccessException {
        if (!orcidSecurityManager.isAdmin()) {
            logoutCurrentUser(serverRequest, response);
            throw new IllegalAccessException("You are not an admin");
        }
    }

    @RequestMapping
    public ModelAndView loadAdminPage(HttpServletRequest serverRequest, HttpServletResponse response) throws IllegalAccessException {
        isAdmin(serverRequest, response);
        return new ModelAndView("/admin/admin_actions");
    }

    /**
     * Get a deprecate profile request and process it.
     * 
     * @param deprecatedOrcid
     *            Orcid to deprecate
     * @param primaryOrcid
     *            Orcid to use as a primary account
     * @throws IllegalAccessException
     */
    @RequestMapping(value = { "/deprecate-profile/deprecate-profile.json" }, method = RequestMethod.POST)
    public @ResponseBody ProfileDeprecationRequest deprecateProfile(HttpServletRequest serverRequest, HttpServletResponse response,
            @RequestBody ProfileDeprecationRequest request) throws IllegalAccessException {
        isAdmin(serverRequest, response);
        // Cleanup
        request.setErrors(new ArrayList<String>());
        request.setSuccessMessage(null);

        if (request.getDeprecatedAccount() != null) {
            request.getDeprecatedAccount().setErrors(new ArrayList<String>());
        }

        if (request.getPrimaryAccount() != null) {
            request.getPrimaryAccount().setErrors(new ArrayList<String>());
        }

        String deprecatedOrcid = request.getDeprecatedAccount() == null ? null : getOrcidFromParam(request.getDeprecatedAccount().getOrcid().trim());
        String primaryOrcid = request.getPrimaryAccount() == null ? null : getOrcidFromParam(request.getPrimaryAccount().getOrcid().trim());

        if (deprecatedOrcid != null) {
            ProfileDetails pd = request.getDeprecatedAccount();
            pd.setOrcid(deprecatedOrcid);
            request.setDeprecatedAccount(pd);
        } else {
            deprecatedOrcid = request.getDeprecatedAccount().getOrcid().trim();
        }

        if (primaryOrcid != null) {
            ProfileDetails pd = request.getPrimaryAccount();
            pd.setOrcid(primaryOrcid);
            request.setPrimaryAccount(pd);
        } else {
            primaryOrcid = request.getPrimaryAccount().getOrcid().trim();
        }

        // Check for errors
        if (!OrcidStringUtils.isValidOrcid(deprecatedOrcid)) {
            request.getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", deprecatedOrcid));
        } else if (!OrcidStringUtils.isValidOrcid(primaryOrcid)) {
            request.getErrors().add(getMessage("admin.profile_deprecation.errors.invalid_orcid", primaryOrcid));
        } else if (deprecatedOrcid.equals(primaryOrcid)) {
            request.getErrors().add(getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"));
        } else {
            try {
                boolean wasDeprecated = adminManager.deprecateProfile(request, deprecatedOrcid, primaryOrcid, getCurrentUserOrcid());
                if (wasDeprecated) {
                    request.setSuccessMessage(getMessage("admin.profile_deprecation.deprecate_account.success_message", deprecatedOrcid, primaryOrcid));
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                request.getErrors().add(getMessage("admin.profile_deprecation.errors.internal_error", deprecatedOrcid));
            }
        }

        return request;
    }

    /**
     * Check a deprecation request to validate the id to deprecate and the
     * primary id
     * 
     * @param request
     *            The request
     * @return a ProfileDeprecationRequest validated
     * @throws IllegalAccessException
     */
    @RequestMapping(value = { "/deprecate-profile/check-orcid.json" }, method = RequestMethod.POST)
    public @ResponseBody ProfileDeprecationRequest checkOrcidToDeprecate(HttpServletRequest serverRequest, HttpServletResponse response,
            @RequestBody ProfileDeprecationRequest request) throws IllegalAccessException {
        isAdmin(serverRequest, response);
        // Cleanup
        request.setErrors(new ArrayList<String>());
        request.setSuccessMessage(null);

        if (request.getDeprecatedAccount() != null) {
            request.getDeprecatedAccount().setErrors(new ArrayList<String>());
        }

        if (request.getPrimaryAccount() != null) {
            request.getPrimaryAccount().setErrors(new ArrayList<String>());
        }

        String deprecatedOrcid = request.getDeprecatedAccount() == null ? null : getOrcidFromParam(request.getDeprecatedAccount().getOrcid().trim());
        String primaryOrcid = request.getPrimaryAccount() == null ? null : getOrcidFromParam(request.getPrimaryAccount().getOrcid().trim());

        if (deprecatedOrcid != null) {
            ProfileDetails pd = request.getDeprecatedAccount();
            pd.setOrcid(deprecatedOrcid);
            request.setDeprecatedAccount(pd);
        } else {
            deprecatedOrcid = request.getDeprecatedAccount().getOrcid().trim();
        }

        if (primaryOrcid != null) {
            ProfileDetails pd = request.getPrimaryAccount();
            pd.setOrcid(primaryOrcid);
            request.setPrimaryAccount(pd);
        } else {
            primaryOrcid = request.getPrimaryAccount().getOrcid().trim();
        }

        if (deprecatedOrcid.equals(primaryOrcid)) {
            request.getPrimaryAccount().getErrors().add(getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"));
        } else {
            if (request.getDeprecatedAccount() != null) {
                request.getDeprecatedAccount().setErrors(new ArrayList<String>());
            }

            if (request.getPrimaryAccount() != null) {
                request.getPrimaryAccount().setErrors(new ArrayList<String>());
            }

            validateIdForDeprecation(request.getDeprecatedAccount());
            validateIdForDeprecation(request.getPrimaryAccount());
        }

        if (!request.getDeprecatedAccount().getErrors().isEmpty()) {
            copyErrors(request.getDeprecatedAccount(), request);
        }

        if (!request.getPrimaryAccount().getErrors().isEmpty()) {
            copyErrors(request.getPrimaryAccount(), request);
        }

        return request;
    }

    private void validateIdForDeprecation(ProfileDetails details) {
        String orcid = details.getOrcid().trim();
        try {
            ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
            if (profile.getDeprecatedDate() != null || profile.getPrimaryRecord() != null) {
                details.getErrors().add(getMessage("admin.profile_deprecation.errors.already_deprecated", orcid));
            } else if (profile.getDeactivationDate() != null) {
                details.getErrors().add(getMessage("admin.profile_deactivation.errors.already_deactivated", orcid));
            } else {
                Name recordName = recordNameManagerReadOnly.getRecordName(orcid);
                if (recordName != null) {
                    boolean hasName = false;
                    if (recordName.getFamilyName() != null && !PojoUtil.isEmpty(recordName.getFamilyName().getContent())) {
                        details.setFamilyName(recordName.getFamilyName().getContent());
                        hasName = true;
                    }
                    if (recordName.getGivenNames() != null && !PojoUtil.isEmpty(recordName.getGivenNames().getContent())) {
                        details.setGivenNames(recordName.getGivenNames().getContent());
                        hasName = true;
                    }
                    if (!hasName) {
                        details.setGivenNames(recordName.getCreditName() == null ? "" : recordName.getCreditName().getContent());
                    }
                }
                Email primary = emailManager.findPrimaryEmail(orcid);
                details.setEmail(primary.getEmail());
            }
        } catch (IllegalArgumentException iae) {
            details.getErrors().add(getMessage("admin.profile_deprecation.errors.inexisting_orcid", orcid));
        }
    }

    @RequestMapping(value = "/reactivate-record", method = RequestMethod.POST)
    public @ResponseBody ProfileDetails reactivateOrcidRecord(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody ProfileDetails profileDetails)
            throws IllegalAccessException {
        isAdmin(serverRequest, response);
        profileDetails.setErrors(new ArrayList<String>());

        // Update the profile details email with the filtered email address
        profileDetails.setEmail(OrcidStringUtils.filterEmailAddress(profileDetails.getEmail()));
        String email = profileDetails.getEmail();
        String orcid = getOrcidFromParam(profileDetails.getOrcid().trim());

        ProfileEntity toReactivate = null;

        try {
            toReactivate = profileEntityCacheManager.retrieve(orcid);
        } catch (Exception e) {

        }

        if (toReactivate == null)
            profileDetails.getErrors().add(getMessage("admin.errors.unexisting_orcid"));
        else if (toReactivate.getDeactivationDate() == null)
            profileDetails.getErrors().add(getMessage("admin.profile_reactivation.errors.already_active"));
        else if (toReactivate.getDeprecatedDate() != null)
            profileDetails.getErrors().add(getMessage("admin.errors.deprecated_account"));
        else if (PojoUtil.isEmpty(email) || !validateEmailAddress(email))
            profileDetails.getErrors().add(getMessage("admin.errors.deactivated_account.primary_email_required"));
        else {
            try {
                if (!emailManagerReadOnly.emailExists(email)) {
                    profileDetails.getErrors().add(getMessage("admin.errors.unexisting_email"));
                } else {
                    String orcidId = emailManager.findOrcidIdByEmail(email);
                    if (!orcidId.equals(orcid)) {
                        profileDetails.getErrors().add(getMessage("admin.errors.deactivated_account.orcid_id_dont_match", orcidId));
                    }
                }
            } catch (NoResultException nre) {
                // Don't do nothing, the email doesn't exists
            }
        }

        if (profileDetails.getErrors() == null || profileDetails.getErrors().size() == 0) {
            // Return a list of email addresses that should be notified by this
            // change
            List<String> emailsToNotify = profileEntityManager.reactivate(orcid, email, null);
            // Notify any new email address
            if (!emailsToNotify.isEmpty()) {
                for (String emailToNotify : emailsToNotify) {
                    boolean isPrimaryEmail = emailManager.isPrimaryEmail(orcid, emailToNotify);
                    recordEmailSender.sendVerificationEmail(orcid, emailToNotify, isPrimaryEmail);
                }
            }
            profileDetails.setStatus(getMessage("admin.success"));
        }
        return profileDetails;
    }

    @RequestMapping(value = "/find-id.json", method = RequestMethod.POST)
    public @ResponseBody List<ProfileDetails> findIdByEmail(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String csvEmails)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        csvEmails = URLDecoder.decode(csvEmails, "UTF-8");
        Map<String, String> emailMap = findIdByEmailHelper(csvEmails);
        List<ProfileDetails> profileDetList = new ArrayList<ProfileDetails>();
        ProfileDetails tempObj;
        for (Map.Entry<String, String> entry : emailMap.entrySet()) {
            tempObj = new ProfileDetails();
            tempObj.setOrcid(entry.getValue());
            tempObj.setEmail(entry.getKey());
            if (!profileEntityManager.isProfileClaimed(entry.getValue())) {
                tempObj.setStatus("Unclaimed");
            } else if (profileEntityManager.isLocked(entry.getValue())) {
                String reason = profileEntityManager.getLockedReason(entry.getValue());
                tempObj.setStatus("Locked" + (reason != null ? reason : ""));
            } else if (profileEntityManager.isDeactivated(entry.getValue())) {
                tempObj.setStatus("Deactivated");
            } else {
                tempObj.setStatus("Claimed");
            }
            profileDetList.add(tempObj);
        }
        return profileDetList;
    }

    @RequestMapping(value = "/lookup-id-or-emails.json", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public @ResponseBody String lookupIdOrEmails(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String csvIdOrEmails)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        List<String> idEmailList = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        csvIdOrEmails = URLDecoder.decode(csvIdOrEmails, "UTF-8");
        if (StringUtils.isNotBlank(csvIdOrEmails)) {
            StringTokenizer tokenizer = new StringTokenizer(csvIdOrEmails, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                idEmailList.add(tokenizer.nextToken());
            }

            for (String idEmail : idEmailList) {
                idEmail = idEmail.trim();
                boolean isOrcid = false;
                if (OrcidStringUtils.getOrcidNumber(idEmail) != null && OrcidStringUtils.isValidOrcid(OrcidStringUtils.getOrcidNumber(idEmail))) {
                    isOrcid = true;
                }
                String orcid = idEmail;
                if (!isOrcid) {
                    Map<String, String> email = findIdByEmailHelper(idEmail);
                    orcid = email.get(idEmail);
                } else {
                    orcid = OrcidStringUtils.getOrcidNumber(idEmail);
                }

                try {
                    if (profileEntityManager.orcidExists(orcid)) {
                        if (!profileEntityManager.isProfileClaimed(orcid)) {
                            builder.append(orcid).append(OUT_STRING_SEPARATOR_SINGLE_SPACE).append(UNCLAIMED).append(OUT_STRING_SEPARATOR);
                        } else if (profileEntityManager.isLocked(orcid)) {
                            String lockedReason = "(locked" + profileEntityManager.getLockedReason(orcid) + ")";
                            builder.append(orcid).append(OUT_STRING_SEPARATOR_SINGLE_SPACE).append(lockedReason).append(OUT_STRING_SEPARATOR);
                        } else if (profileEntityManager.isProfileDeprecated(orcid)) {
                            builder.append(orcid).append(OUT_STRING_SEPARATOR_SINGLE_SPACE).append(DEPRECATED).append(OUT_STRING_SEPARATOR);
                        } else if (profileEntityManager.isDeactivated(orcid)) {
                            builder.append(orcid).append(OUT_STRING_SEPARATOR_SINGLE_SPACE).append(DEACTIVATED).append(OUT_STRING_SEPARATOR);
                        } else {
                            builder.append(orcid).append(OUT_STRING_SEPARATOR_SINGLE_SPACE).append(CLAIMED).append(OUT_STRING_SEPARATOR);
                        }

                        Email primary = emailManager.findPrimaryEmail(orcid);
                        if (primary != null) {
                            builder.append(OUT_EMAIL_PRIMARY).append(primary.getEmail()).append(OUT_STRING_SEPARATOR_SINGLE_SPACE);
                        } else {
                            builder.append(OUT_NOT_AVAILABLE);
                        }

                        Emails emails = emailManagerReadOnly.getEmails(orcid);
                        if (emails.getEmails().size() > 0) {
                            for (Email email : emails.getEmails()) {
                                if (!email.isPrimary()) {
                                    builder.append(email.getEmail()).append(OUT_STRING_SEPARATOR_SINGLE_SPACE);
                                }
                            }

                        }

                        Name recordName = recordNameManagerReadOnly.getRecordName(orcid);
                        if (recordName != null) {
                            builder.append(OUT_STRING_SEPARATOR);
                            if (recordName.getGivenNames() != null && !PojoUtil.isEmpty(recordName.getGivenNames().getContent())) {
                                builder.append(recordName.getGivenNames().getContent()).append(OUT_STRING_SEPARATOR_SINGLE_SPACE);
                            }
                            if (recordName.getFamilyName() != null && !PojoUtil.isEmpty(recordName.getFamilyName().getContent())) {
                                builder.append(recordName.getFamilyName().getContent()).append(OUT_STRING_SEPARATOR_SINGLE_SPACE);
                            }
                            if (recordName.getCreditName() != null && !PojoUtil.isEmpty(recordName.getCreditName().getContent())) {
                                builder.append("(").append(recordName.getCreditName().getContent()).append(")");
                            }
                        }

                        boolean twoFactorAuthenticationEnabled = twoFactorAuthenticationManager.userUsing2FA(orcid);
                        if (twoFactorAuthenticationEnabled) {
                            builder.append(OUT_STRING_SEPARATOR).append(ENABLED_2FA);
                        }

                    } else {
                        if (isOrcid) {
                            builder.append(orcid).append(OUT_STRING_SEPARATOR).append(OUT_NOT_AVAILABLE);
                        } else {
                            builder.append(OUT_NOT_AVAILABLE_ID).append(OUT_STRING_SEPARATOR).append(idEmail);
                        }
                    }
                } catch (Exception e) {
                    // Invalid orcid in the params, so, we can just ignore it
                    LOGGER.warn("Unable to get info for " + idEmail, e);
                }
                builder.append(OUT_NEW_LINE);
            }
        }

        return builder.toString();
    }

    @RequestMapping(value = "/deactivate-records.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Set<String>> deactivateOrcidRecords(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String orcidIds)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        orcidIds = URLDecoder.decode(orcidIds, "UTF-8");
        Set<String> deactivatedIds = new HashSet<String>();
        Set<String> successIds = new HashSet<String>();
        Set<String> notFoundIds = new HashSet<String>();
        Set<String> members = new HashSet<String>();
        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                String orcidId = getOrcidFromParam(nextToken);
                if (orcidId == null) {
                    notFoundIds.add(nextToken);
                } else {
                    if (!profileEntityManager.orcidExists(orcidId)) {
                        notFoundIds.add(nextToken);
                    } else {
                        ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                        if (entity.getDeactivationDate() != null) {
                            deactivatedIds.add(nextToken);
                        } else if (OrcidType.GROUP.name().equals(entity.getOrcidType())) {
                            members.add(nextToken);
                        } else {
                            profileEntityManager.deactivateRecord(orcidId);
                            successIds.add(nextToken);
                        }
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFoundList", notFoundIds);
        resendIdMap.put("success", successIds);
        resendIdMap.put("alreadyDeactivated", deactivatedIds);
        resendIdMap.put("members", members);
        return resendIdMap;
    }

    public Map<String, String> findIdByEmailHelper(String csvEmails) {
        if (StringUtils.isBlank(csvEmails))
            return new HashMap<String, String>();
        return emailManager.findOricdIdsByCommaSeparatedEmails(csvEmails);
    }

    @RequestMapping(value = "/add-email-to-record", method = RequestMethod.POST)
    public @ResponseBody ProfileDetails addEmailToRecord(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody ProfileDetails profileDetails)
            throws IllegalAccessException {
        isAdmin(serverRequest, response);
        profileDetails.setErrors(new ArrayList<String>());
        if (StringUtils.isNotBlank(profileDetails.getEmail())) {
            profileDetails.setEmail(OrcidStringUtils.filterEmailAddress(profileDetails.getEmail()));
        }
        profileDetails.setErrors(new ArrayList<String>());

        String email = profileDetails.getEmail();
        String orcid = getOrcidFromParam(profileDetails.getOrcid().trim());

        ProfileEntity profileToUpdate = null;

        try {
            profileToUpdate = profileEntityCacheManager.retrieve(orcid);
        } catch (Exception e) {

        }

        if (profileToUpdate == null) {
            profileDetails.getErrors().add(getMessage("admin.errors.unexisting_orcid"));
        } else if (PojoUtil.isEmpty(email)) {
            profileDetails.getErrors().add(getMessage("admin.error_please_provided_an_email_to_be_added"));
        } else if (profileToUpdate.getDeprecatedDate() != null) {
            profileDetails.getErrors().add(getMessage("admin.errors.deprecated_account"));
        } else if (!validateEmailAddress(email)) {
            profileDetails.getErrors().add(getMessage("admin.error_invalid_email_addres"));
        } else if (emailManager.emailExists(email)) {
            if (emailMatchesUser(orcid, email)) {
                profileDetails.getErrors().add(getMessage("admin.error_this_user_already_has_this_email"));
            } else {
                profileDetails.getErrors().add(getMessage("admin.error_other_account_has_this_email"));
            }

        } else {
            try {

                profileDetails.setErrors(new ArrayList<String>());
                Email emailEntity = new Email();
                emailEntity.setEmail(email);
                emailEntity.setPrimary(false);
                emailEntity.setVerified(false);
                emailEntity.setVisibility(Visibility.PRIVATE);
                emailManager.addEmail(orcid, emailEntity);
            } catch (NoResultException nre) {
                // Don't do nothing, the email doesn't exists
                LOGGER.error("Couldnt add email address to " + orcid);
            }
        }

        return profileDetails;
    }

    /**
     * Reset password
     * 
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/reset-password.json", method = RequestMethod.POST)
    public @ResponseBody AdminChangePassword resetPassword(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody AdminChangePassword form)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        form.setError(null);
        String orcidOrEmail = URLDecoder.decode(form.getOrcidOrEmail(), "UTF-8").trim();
        String password = form.getPassword().trim();
        if (StringUtils.isNotBlank(password) && password.matches(PasswordConstants.ORCID_PASSWORD_REGEX)) {
            String orcid = getOrcidFromParam(orcidOrEmail);
            if (orcid != null) {
                if (profileEntityManager.orcidExists(orcid)) {
                    profileEntityManager.updatePassword(orcid, password);
                    // reset the lock fields
                    profileEntityManager.resetSigninLock(orcid);
                    profileEntityCacheManager.remove(orcid);

                } else {
                    form.setError(getMessage("admin.errors.unexisting_orcid"));
                }
            } else {
                form.setError(getMessage("admin.errors.unable_to_fetch_info"));
            }
        } else {
            form.setError(getMessage("admin.reset_password.error.password_invalid"));
        }

        return form;
    }

    /**
     * Reset password validate
     * 
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/reset-password/validate", method = RequestMethod.POST)
    public @ResponseBody AdminChangePassword resetPasswordValidateId(HttpServletRequest serverRequest, HttpServletResponse response,
            @RequestBody AdminChangePassword form) throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        form.setError(null);
        String orcidOrEmail = URLDecoder.decode(form.getOrcidOrEmail(), "UTF-8").trim();
        String orcid = getOrcidFromParam(form.getOrcidOrEmail().trim());
        if (orcid != null || orcidOrEmail.contains("@")) {
            if (!profileEntityManager.orcidExists(orcid)) {
                form.setError(getMessage("admin.errors.unexisting_orcid"));
            }
        } else {
            form.setError(getMessage("admin.errors.unable_to_fetch_info"));
        }
        return form;
    }

    /**
     * Reset password validate
     * 
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/reset-password-link.json", method = RequestMethod.POST)
    public @ResponseBody AdminResetPasswordLink resetPasswordLink(HttpServletRequest serverRequest, HttpServletResponse response,
            @RequestBody AdminResetPasswordLink form) throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        form.setError(null);
        String orcidOrEmail = URLDecoder.decode(form.getOrcidOrEmail(), "UTF-8").trim();
        boolean isOrcid = OrcidStringUtils.isValidOrcid(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            if (OrcidStringUtils.isEmailValid(orcidOrEmail) && emailManager.emailExists(orcidOrEmail)) {
                orcid = emailManager.findOrcidIdByEmail(orcidOrEmail);
            } else {
                form.setError(getMessage("admin.errors.unable_to_fetch_info"));
                return form;
            }
        } else {
            orcid = orcidOrEmail;
        }

        if (!PojoUtil.isEmpty(orcid) && profileEntityManager.orcidExists(orcid)) {
            Pair<String, Date> resetLinkData = verifyEmailUtils.createResetLinkForAdmin(orcid, registryUrl);
            LOGGER.debug("Reset link to be sent to the client: " + resetLinkData.getKey());

            form.setResetLink(resetLinkData.getKey());
            form.setIssueDate(resetLinkData.getValue());
            form.setDurationInHours(RESET_PASSWORD_LINK_DURATION);

        } else {
            form.setError(getMessage("admin.errors.unexisting_orcid"));
            return form;
        }

        return form;
    }

    /**
     * Admin switch user
     * 
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/admin-switch-user", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> adminSwitchUser(HttpServletRequest serverRequest, HttpServletResponse response,
            @ModelAttribute("orcidOrEmail") String orcidOrEmail) throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        Map<String, String> result = new HashMap<String, String>();
        String orcidId = getOrcidFromParam(orcidOrEmail);
        if (orcidId == null) {
            result.put("errorMessg", "Invalid id " + orcidOrEmail);
        } else {
            if (StringUtils.isEmpty(orcidId) || !profileEntityManager.orcidExists(orcidId)) {
                result.put("errorMessg", "Invalid id " + orcidOrEmail);
            } else {
                result.put("id", orcidId);
            }
        }
        return result;
    }

    /**
     * Admin verify email
     * 
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/admin-verify-email.json", method = RequestMethod.POST)
    public @ResponseBody String adminVerifyEmail(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String email)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        email = URLDecoder.decode(email, "UTF-8").trim();
        String result = getMessage("admin.verify_email.success", email);
        if (emailManager.emailExists(email)) {
            String orcid = emailManagerReadOnly.findOrcidIdByEmail(email);
            emailManager.verifyEmail(orcid, email);
        } else {
            result = getMessage("admin.verify_email.fail", email);
        }
        return result;
    }

    /**
     * Admin starts delegation process
     * 
     * @throws IllegalAccessException
     */
    @RequestMapping(value = "/add-delegate.json", method = RequestMethod.POST)
    public @ResponseBody AdminDelegatesRequest startDelegationProcess(HttpServletRequest serverRequest, HttpServletResponse response,
            @RequestBody AdminDelegatesRequest request) throws IllegalAccessException {
        isAdmin(serverRequest, response);
        // Clear errors
        request.setErrors(new ArrayList<String>());
        request.getManaged().setErrors(new ArrayList<String>());
        request.getTrusted().setErrors(new ArrayList<String>());
        request.setSuccessMessage(null);

        String trusted = getOrcidFromParam(request.getTrusted().getValue().trim());
        String managed = getOrcidFromParam(request.getManaged().getValue().trim());

        boolean haveErrors = false;

        if (trusted == null) {
            request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
            haveErrors = true;
        } else {
            if (!profileEntityManager.orcidExists(managed)) {
                request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                haveErrors = true;
            } else {
                ProfileEntity e = profileEntityCacheManager.retrieve(managed);
                if (!e.isAccountNonLocked() || e.getPrimaryRecord() != null) {
                    request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                    haveErrors = true;
                }
            }
        }

        if (managed == null) {
            request.getManaged().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getManaged().getValue()));
            haveErrors = true;
        } else {
            if (!profileEntityManager.orcidExists(managed)) {
                request.getManaged().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getManaged().getValue()));
                haveErrors = true;
            } else {
                ProfileEntity e = profileEntityCacheManager.retrieve(managed);
                if (!e.isAccountNonLocked() || e.getPrimaryRecord() != null) {
                    request.getManaged().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                    haveErrors = true;
                }
            }
        }

        if (haveErrors)
            return request;

        adminManager.startDelegationProcess(request, trusted, managed);
        return request;
    }

    /**
     * Admin starts delegation process
     * 
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/check-claimed-status.json", method = RequestMethod.POST)
    public @ResponseBody boolean checkClaimedStatus(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String orcidOrEmail)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        orcidOrEmail = URLDecoder.decode(orcidOrEmail, "UTF-8").trim();
        boolean isOrcid = OrcidStringUtils.isValidOrcid(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            if (emailManager.emailExists(orcidOrEmail)) {
                Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
                orcid = email.get(orcidOrEmail);
            }
        } else {
            orcid = orcidOrEmail;
        }

        if (PojoUtil.isEmpty(orcid) || !profileEntityManager.orcidExists(orcid))
            return false;

        return profileEntityManager.isProfileClaimed(orcid);
    }

    private String getOrcidFromParam(String orcidOrEmail) {
        String orcid = null;
        ProfileDetails result = new ProfileDetails();
        result.setErrors(new ArrayList<String>());
        // If it is not an orcid, check the value from the emails table
        if (orcidOrEmail.contains("@")) {
            if (emailManager.emailExists(orcidOrEmail)) {
                Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
                orcid = email.get(orcidOrEmail);
            }
        } else {
            if (OrcidStringUtils.getOrcidNumber(orcidOrEmail) != null && OrcidStringUtils.isValidOrcid(OrcidStringUtils.getOrcidNumber(orcidOrEmail))) {
                orcid = OrcidStringUtils.getOrcidNumber(orcidOrEmail);
            }
        }

        return orcid;
    }

    /**
     * Function to lock an account
     * 
     * @param orcid
     *            The orcid of the account we want to lock
     * @return true if the account was locked, false otherwise
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/lock-records.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Set<String>> lockRecords(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody LockAccounts lockAccounts)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        Set<String> lockedIds = new HashSet<String>();
        Set<String> successIds = new HashSet<String>();
        Set<String> notFoundIds = new HashSet<String>();
        Set<String> descriptionMissing = new HashSet<String>();
        Set<String> reviewedIds = new HashSet<String>();
        String orcidIds = URLDecoder.decode(lockAccounts.getOrcidsToLock(), "UTF-8").trim();

        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                String orcidId = getOrcidFromParam(nextToken);
                if (orcidId == null) {
                    notFoundIds.add(nextToken);
                } else {
                    if (!profileEntityManager.orcidExists(orcidId)) {
                        notFoundIds.add(nextToken);
                    } else {
                        ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                        if (!entity.isAccountNonLocked()) {
                            lockedIds.add(nextToken);
                        } else if (entity.isReviewed()) {
                            reviewedIds.add(nextToken);
                        } else if (lockAccounts.getDescription() == null || lockAccounts.getDescription().isEmpty()) {
                            descriptionMissing.add(nextToken);
                        } else {
                            boolean wasLocked = profileEntityManager.lockProfile(orcidId, lockAccounts.getLockReason(), lockAccounts.getDescription(),
                                    getCurrentUserOrcid());
                            if (wasLocked) {
                                recordEmailSender.sendOrcidLockedEmail(orcidId);
                            }
                            successIds.add(nextToken);
                        }
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFound", notFoundIds);
        resendIdMap.put("successful", successIds);
        resendIdMap.put("alreadyLocked", lockedIds);
        resendIdMap.put("reviewed", reviewedIds);
        resendIdMap.put("descriptionMissing", descriptionMissing);
        return resendIdMap;
    }

    /**
     * Function to get reasons for locking account(s)
     * 
     * @return list of reasons for locking accounts
     */
    @RequestMapping(value = "/lock-reasons.json")
    public @ResponseBody List<String> getLockReasons() {
        return adminManager.getLockReasons();
    }

    /**
     * Function to unlock an account
     * 
     * @param orcid
     *            The orcid of the account we want to unlock
     * @return true if the account was unlocked, false otherwise
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/unlock-records.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Set<String>> unlockRecords(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String orcidIds)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        orcidIds = URLDecoder.decode(orcidIds, "UTF-8").trim();
        Set<String> unlockedIds = new HashSet<String>();
        Set<String> successIds = new HashSet<String>();
        Set<String> notFoundIds = new HashSet<String>();
        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                String orcidId = getOrcidFromParam(nextToken);
                if (orcidId == null) {
                    notFoundIds.add(nextToken);
                } else {
                    if (!profileEntityManager.orcidExists(orcidId)) {
                        notFoundIds.add(nextToken);
                    } else {
                        ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                        if (entity.isAccountNonLocked()) {
                            unlockedIds.add(nextToken);
                        } else {
                            profileEntityManager.unlockProfile(orcidId);
                            successIds.add(nextToken);
                        }
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFound", notFoundIds);
        resendIdMap.put("successful", successIds);
        resendIdMap.put("alreadyUnlocked", unlockedIds);
        return resendIdMap;
    }

    @RequestMapping(value = "/unreview-records.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Set<String>> unreviewRecords(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String orcidIds)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        orcidIds = URLDecoder.decode(orcidIds, "UTF-8").trim();
        Set<String> unreviewedIds = new HashSet<String>();
        Set<String> successIds = new HashSet<String>();
        Set<String> notFoundIds = new HashSet<String>();
        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                String orcidId = getOrcidFromParam(nextToken);
                if (orcidId == null) {
                    notFoundIds.add(nextToken);
                } else {
                    if (!profileEntityManager.orcidExists(orcidId)) {
                        notFoundIds.add(nextToken);
                    } else {
                        ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                        if (!entity.isReviewed()) {
                            unreviewedIds.add(nextToken);
                        } else {
                            profileEntityManager.unreviewProfile(orcidId);
                            successIds.add(nextToken);
                        }
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFound", notFoundIds);
        resendIdMap.put("successful", successIds);
        resendIdMap.put("alreadyUnreviewed", unreviewedIds);
        return resendIdMap;
    }

    @RequestMapping(value = "/review-records.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Set<String>> reviewRecords(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String orcidIds)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        orcidIds = URLDecoder.decode(orcidIds, "UTF-8").trim();
        Set<String> reviewedIds = new HashSet<String>();
        Set<String> successIds = new HashSet<String>();
        Set<String> notFoundIds = new HashSet<String>();
        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                String orcidId = getOrcidFromParam(nextToken);
                if (orcidId == null) {
                    notFoundIds.add(nextToken);
                } else {
                    if (!profileEntityManager.orcidExists(orcidId)) {
                        notFoundIds.add(nextToken);
                    } else {
                        ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                        if (entity.isReviewed()) {
                            reviewedIds.add(nextToken);
                        } else {
                            profileEntityManager.reviewProfile(orcidId);
                            if (spamManager.exists(orcidId)) {
                                spamManager.removeSpam(orcidId);
                            }
                            successIds.add(nextToken);
                        }
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFound", notFoundIds);
        resendIdMap.put("successful", successIds);
        resendIdMap.put("alreadyReviewed", reviewedIds);
        return resendIdMap;
    }

    @RequestMapping(value = "/resend-claim.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, List<String>> resendClaimEmail(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String emailsOrOrcids)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        emailsOrOrcids = URLDecoder.decode(emailsOrOrcids, "UTF-8").trim();
        List<String> emailOrOrcidList = new ArrayList<String>();
        if (StringUtils.isNotBlank(emailsOrOrcids)) {
            StringTokenizer tokenizer = new StringTokenizer(emailsOrOrcids, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                emailOrOrcidList.add(tokenizer.nextToken());
            }
        }

        List<String> claimedIds = new ArrayList<String>();
        List<String> successIds = new ArrayList<String>();
        List<String> notFoundIds = new ArrayList<String>();
        for (String emailOrOrcid : emailOrOrcidList) {
            String orcidId = getOrcidFromParam(emailOrOrcid);
            if (orcidId == null) {
                notFoundIds.add(emailOrOrcid);
            } else {
                if (!profileEntityManager.orcidExists(orcidId)) {
                    notFoundIds.add(orcidId);
                } else {
                    ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                    if (entity.getClaimed()) {
                        claimedIds.add(emailOrOrcid);
                    } else {
                        boolean emailSupplied = !OrcidStringUtils.isValidOrcid(emailOrOrcid) && OrcidStringUtils.isEmailValid(emailOrOrcid);
                        String email;
                        if (!emailSupplied) {
                            email = emailManager.findPrimaryEmail(emailOrOrcid).getEmail();
                        } else {
                            email = emailOrOrcid;
                        }
                        recordEmailSender.sendClaimReminderEmail(orcidId, 0, email);
                        successIds.add(emailOrOrcid);
                    }
                }
            }
        }
        Map<String, List<String>> resendIdMap = new HashMap<String, List<String>>();
        resendIdMap.put("notFound", notFoundIds);
        resendIdMap.put("successful", successIds);
        resendIdMap.put("alreadyClaimed", claimedIds);
        return resendIdMap;
    }

    @RequestMapping(value = "/disable-2fa.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, List<String>> disable2FA(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String emailsOrOrcids)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        emailsOrOrcids = URLDecoder.decode(emailsOrOrcids, "UTF-8").trim();
        List<String> emailOrOrcidList = new ArrayList<String>();
        if (StringUtils.isNotBlank(emailsOrOrcids)) {
            StringTokenizer tokenizer = new StringTokenizer(emailsOrOrcids, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                emailOrOrcidList.add(tokenizer.nextToken());
            }
        }

        List<String> disabledIds = new ArrayList<String>();
        List<String> notFoundIds = new ArrayList<String>();
        List<String> without2FAs = new ArrayList<String>();
        for (String emailOrOrcid : emailOrOrcidList) {
            String orcidId = getOrcidFromParam(emailOrOrcid);
            if (orcidId == null) {
                notFoundIds.add(emailOrOrcid);
            } else {
                if (!profileEntityManager.orcidExists(orcidId)) {
                    notFoundIds.add(orcidId);
                } else {
                    ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);

                    if (entity.getUsing2FA()) {
                        twoFactorAuthenticationManager.adminDisable2FA(orcidId, getCurrentUserOrcid());
                        recordEmailSender.send2FADisabledEmail(orcidId);
                        disabledIds.add(emailOrOrcid);
                    } else {
                        without2FAs.add(emailOrOrcid);
                    }
                }
            }
        }
        Map<String, List<String>> resendIdMap = new HashMap<String, List<String>>();
        resendIdMap.put("notFound", notFoundIds);
        resendIdMap.put("without2FAs", without2FAs);
        resendIdMap.put("disabledIds", disabledIds);
        return resendIdMap;
    }

    @RequestMapping(value = "/validate-client-conversion.json", method = RequestMethod.POST)
    public @ResponseBody ConvertClient validateClientConversion(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody ConvertClient data)
            throws IllegalAccessException {
        data.setGroupIdNotFound(false);
        data.setGroupIdDeactivated(false);
        data.setClientNotFound(false);
        data.setAlreadyMember(false);
        data.setClientDeactivated(false);

        isAdmin(serverRequest, response);
        if (PojoUtil.isEmpty(data.getClientId()) || !clientDetailsManager.exists(data.getClientId())) {
            data.setClientNotFound(true);
        }

        if (!data.isClientNotFound()) {
            ClientDetailsEntity clientDetailsEntity = clientDetailsManager.findByClientId(data.getClientId());
            if (!ClientType.PUBLIC_CLIENT.name().equals(clientDetailsEntity.getClientType())) {
                data.setAlreadyMember(true);
            }
            if (clientDetailsEntity.getDeactivatedDate() != null) {
                data.setClientDeactivated(true);
            }
        }

        if (PojoUtil.isEmpty(data.getGroupId())) {
            data.setGroupIdNotFound(true);
        }

        if (!data.isGroupIdNotFound()) {
            try {
                ProfileEntity group = profileEntityCacheManager.retrieve(data.getGroupId());
                if (group == null || !OrcidType.GROUP.name().equals(group.getOrcidType())) {
                    data.setGroupIdNotFound(true);
                } else {
                    if (!group.isEnabled() || group.getRecordLocked() || group.getDeactivationDate() != null) {
                        data.setGroupIdDeactivated(true);
                    } else {
                        ClientType clientType = MemberType.PREMIUM.name().equals(group.getGroupType()) ? ClientType.PREMIUM_UPDATER : ClientType.UPDATER;
                        data.setTargetClientType(clientType.name());
                    }

                }

            } catch (IllegalArgumentException e) {
                // invalid group id
                data.setGroupIdNotFound(true);
            }
        }

        return data;
    }

    @RequestMapping(value = "/convert-client.json", method = RequestMethod.POST)
    public @ResponseBody ConvertClient convertClient(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody ConvertClient data)
            throws IllegalAccessException {
        isAdmin(serverRequest, response);
        data = validateClientConversion(serverRequest, response, data);
        if (data.isClientNotFound() || data.isAlreadyMember() || data.isGroupIdNotFound()) {
            data.setError("Invalid data");
            data.setSuccess(false);
            return data;
        }

        try {
            clientDetailsManager.convertPublicClientToMember(data.getClientId(), data.getGroupId());
            data.setSuccess(true);
            return data;
        } catch (Exception e) {
            data.setSuccess(false);
            data.setError(e.getMessage());
            return data;
        }
    }

    @RequestMapping(value = "/move-client.json", method = RequestMethod.POST)
    public @ResponseBody ConvertClient moveClient(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody ConvertClient data)
            throws IllegalAccessException {
        isAdmin(serverRequest, response);
        data = validateClientConversion(serverRequest, response, data);
        if (data.isClientNotFound() || !data.isAlreadyMember() || data.isGroupIdNotFound() || data.isClientDeactivated()) {
            data.setError("Invalid data");
            data.setSuccess(false);
            return data;
        }

        try {
            clientDetailsManager.moveClientGroupId(data.getClientId(), data.getGroupId());
            data.setSuccess(true);
            return data;
        } catch (Exception e) {
            data.setSuccess(false);
            data.setError(e.getMessage());
            return data;
        }
    }
}
