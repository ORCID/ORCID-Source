package org.orcid.frontend.web.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
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

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.AdminChangePassword;
import org.orcid.pojo.AdminDelegatesRequest;
import org.orcid.pojo.LockAccounts;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    private static final String INP_STRING_SEPARATOR = " \n\r\t,";
    private static final String OUT_STRING_SEPARATOR = "		";
    private static final String OUT_NOT_AVAILABLE = "N/A";
    private static final String OUT_NEW_LINE = "\n";

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

        String deprecatedOrcid = request.getDeprecatedAccount() == null ? null : request.getDeprecatedAccount().getOrcid().trim();
        String primaryOrcid = request.getPrimaryAccount() == null ? null : request.getPrimaryAccount().getOrcid().trim();
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

        String deprecatedOrcid = request.getDeprecatedAccount() == null ? null : request.getDeprecatedAccount().getOrcid().trim();
        String primaryOrcid = request.getPrimaryAccount() == null ? null : request.getPrimaryAccount().getOrcid().trim();

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
                RecordNameEntity recordName = profile.getRecordNameEntity();
                if (recordName != null) {
                    boolean hasName = false;
                    if (!PojoUtil.isEmpty(recordName.getFamilyName())) {
                        details.setFamilyName(recordName.getFamilyName());
                        hasName = true;
                    }
                    if (!PojoUtil.isEmpty(recordName.getGivenNames())) {
                        details.setGivenNames(recordName.getGivenNames());
                        hasName = true;
                    }
                    if (!hasName) {
                        details.setGivenNames(recordName.getCreditName());
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

        String email = profileDetails.getEmail().trim();
        String orcid = profileDetails.getOrcid().trim();

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
            // Null Reactivation object means the reactivation is done by an admin
            profileEntityManager.reactivate(orcid, email, null);
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
            } else if (orcidProfileManager.isLocked(entry.getValue())) {
                tempObj.setStatus("Locked");
            } else if (profileEntityManager.isDeactivated(entry.getValue())) {
                tempObj.setStatus("Deactivated");
            } else {
                tempObj.setStatus("Claimed");
            }
            profileDetList.add(tempObj);
        }
        return profileDetList;
    }

    @RequestMapping(value = "/lookup-id-or-emails.json", method = RequestMethod.POST)
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
                boolean isOrcid = OrcidStringUtils.isValidOrcid(idEmail);
                String orcid = idEmail;
                if (!isOrcid) {
                    Map<String, String> email = findIdByEmailHelper(idEmail);
                    orcid = email.get(idEmail);
                }

                try {
                    if (profileEntityManager.orcidExists(orcid)) {
                        Email email = emailManager.findPrimaryEmail(orcid);
                        if (email != null) {
                            builder.append(email.getEmail());
                        } else {
                            builder.append(OUT_NOT_AVAILABLE);
                        }
                        builder.append(OUT_STRING_SEPARATOR).append(orcid);
                    } else {
                        if (isOrcid) {
                            builder.append(OUT_NOT_AVAILABLE).append(OUT_STRING_SEPARATOR).append(idEmail);
                        } else {
                            builder.append(idEmail).append(OUT_STRING_SEPARATOR).append(OUT_NOT_AVAILABLE);
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
        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String orcid = tokenizer.nextToken();
                ProfileEntity profile = null;
                try {
                    profile = profileEntityCacheManager.retrieve(orcid);
                } catch (Exception e) {
                    // Invalid orcid id provided
                }
                if (profile == null) {
                    notFoundIds.add(orcid);
                } else {
                    if (profile.getDeactivationDate() != null) {
                        deactivatedIds.add(orcid);
                    } else {
                        profileEntityManager.deactivateRecord(orcid);
                        successIds.add(orcid);
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFoundList", notFoundIds);
        resendIdMap.put("success", successIds);
        resendIdMap.put("alreadyDeactivated", deactivatedIds);
        return resendIdMap;
    }

    public Map<String, String> findIdByEmailHelper(String csvEmails) {
        if (StringUtils.isBlank(csvEmails))
            return new HashMap<String, String>();
        return emailManager.findOricdIdsByCommaSeparatedEmails(csvEmails);
    }

    /**
     * Reset password
     * 
     * @throws IllegalAccessException
     */
    @RequestMapping(value = "/reset-password.json", method = RequestMethod.POST)
    public @ResponseBody AdminChangePassword resetPassword(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody AdminChangePassword form)
            throws IllegalAccessException {
        isAdmin(serverRequest, response);
        form.setError(null);
        String orcidOrEmail = form.getOrcidOrEmail().trim();
        String password = form.getPassword().trim();
        if (StringUtils.isNotBlank(password) && password.matches(OrcidPasswordConstants.ORCID_PASSWORD_REGEX)) {
            String orcid = null;
            if (StringUtils.isNotBlank(orcidOrEmail))
                orcidOrEmail = orcidOrEmail.trim();
            boolean isOrcid = OrcidStringUtils.isValidOrcid(orcidOrEmail);
            // If it is not an orcid, check the value from the emails table
            if (!isOrcid) {
                Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
                orcid = email.get(orcidOrEmail);
            } else {
                orcid = orcidOrEmail;
            }

            if (StringUtils.isNotEmpty(orcid)) {
                if (profileEntityManager.orcidExists(orcid)) {
                    profileEntityManager.updatePassword(orcid, password);
                } else {
                    form.setError(getMessage("admin.errors.unexisting_orcid"));
                }
            } else {
                form.setError(getMessage("admin.errors.unable_to_fetch_info"));
            }            
        } else {
            form.setError(getMessage("admin.reset_password.error.invalid_password"));
        }

        return form;
    }

    /**
     * Reset password validate
     * 
     * @throws IllegalAccessException
     */
    @RequestMapping(value = "/reset-password/validate", method = RequestMethod.POST)
    public @ResponseBody AdminChangePassword resetPasswordValidateId(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody AdminChangePassword form) throws IllegalAccessException {
        isAdmin(serverRequest, response);
        form.setError(null);
        String orcidOrEmail = form.getOrcidOrEmail().trim();
        
        String orcid = null;
        if (StringUtils.isNotBlank(orcidOrEmail))
            orcidOrEmail = orcidOrEmail.trim();
        boolean isOrcid = OrcidStringUtils.isValidOrcid(orcidOrEmail);
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
            orcid = email.get(orcidOrEmail);
        } else {
            orcid = orcidOrEmail;
        }
        
        if (StringUtils.isNotEmpty(orcid)) {
            if (!profileEntityManager.orcidExists(orcid)) {                
                form.setError(getMessage("admin.errors.unexisting_orcid"));
            }
        } else {
            form.setError(getMessage("admin.errors.unable_to_fetch_info"));
        }
        return form;
    }
        
    /**
     * Remove security question
     * 
     * @throws IllegalAccessException
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/remove-security-question.json", method = RequestMethod.POST)
    public @ResponseBody String removeSecurityQuestion(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody String orcidOrEmail)
            throws IllegalAccessException, UnsupportedEncodingException {
        isAdmin(serverRequest, response);
        orcidOrEmail = URLDecoder.decode(orcidOrEmail, "UTF-8");
        if (StringUtils.isNotBlank(orcidOrEmail))
            orcidOrEmail = orcidOrEmail.trim();
        boolean isOrcid = OrcidStringUtils.isValidOrcid(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
            orcid = email.get(orcidOrEmail);
        } else {
            orcid = orcidOrEmail;
        }

        if (StringUtils.isNotEmpty(orcid)) {            
            String result = adminManager.removeSecurityQuestion(orcid);
            // If the resulting string is not null, it means there was an
            // error
            if (result != null)
                return result;            
        } else {
            return getMessage("admin.errors.unable_to_fetch_info");
        }
        return getMessage("admin.remove_security_question.success");
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
        orcidOrEmail = orcidOrEmail.trim();
        if (StringUtils.isNotBlank(orcidOrEmail))
            orcidOrEmail = orcidOrEmail.trim();
        boolean isOrcid = OrcidStringUtils.isValidOrcid(orcidOrEmail);
        String orcid = null;
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
            orcid = email.get(orcidOrEmail);
        } else {
            orcid = orcidOrEmail;
        }

        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isEmpty(orcid) || !profileEntityManager.orcidExists(orcid)) {            
            result.put("errorMessg", "Invalid id " + orcidOrEmail);
        } else {
            result.put("id", orcid);
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
            emailManager.verifyEmail(email, orcid);
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

        String trusted = request.getTrusted().getValue().trim();
        String managed = request.getManaged().getValue().trim();
        boolean haveErrors = false;

        if (!OrcidStringUtils.isValidOrcid(trusted)) {
            if (emailManager.emailExists(trusted)) {
                Map<String, String> email = findIdByEmailHelper(trusted);
                trusted = email.get(trusted);
            } else {
                request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                haveErrors = true;
            }
        } else {
            if (!profileEntityManager.orcidExists(trusted)) {
                request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                haveErrors = true;
            } else {
                ProfileEntity e = profileEntityCacheManager.retrieve(trusted);
                if(!e.isAccountNonLocked() || e.getPrimaryRecord() != null) {
                    request.getTrusted().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getTrusted().getValue()));
                    haveErrors = true;
                }
            }
        }

        if (!OrcidStringUtils.isValidOrcid(managed)) {
            if (emailManager.emailExists(managed)) {
                Map<String, String> email = findIdByEmailHelper(managed);
                managed = email.get(managed);
            } else {
                request.getManaged().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getManaged().getValue()));
                haveErrors = true;
            }
        } else {
            if (!profileEntityManager.orcidExists(managed)) {
                request.getManaged().getErrors().add(getMessage("admin.delegate.error.invalid_orcid_or_email", request.getManaged().getValue()));
                haveErrors = true;
            } else {
                ProfileEntity e = profileEntityCacheManager.retrieve(managed);
                if(!e.isAccountNonLocked() || e.getPrimaryRecord() != null) {
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
        boolean isOrcid = OrcidStringUtils.isValidOrcid(orcidOrEmail);
        String orcid = null;
        ProfileDetails result = new ProfileDetails();
        result.setErrors(new ArrayList<String>());
        // If it is not an orcid, check the value from the emails table
        if (!isOrcid) {
            if (emailManager.emailExists(orcidOrEmail)) {
                Map<String, String> email = findIdByEmailHelper(orcidOrEmail);
                orcid = email.get(orcidOrEmail);
            }
        } else {
            orcid = orcidOrEmail;
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
     */
    @RequestMapping(value = "/lock-records.json", method = RequestMethod.POST)
    public @ResponseBody Map<String, Set<String>> lockRecords(HttpServletRequest serverRequest, HttpServletResponse response, @RequestBody LockAccounts lockAccounts)
            throws IllegalAccessException {
        isAdmin(serverRequest, response);
        Set<String> lockedIds = new HashSet<String>();
        Set<String> successIds = new HashSet<String>();
        Set<String> notFoundIds = new HashSet<String>();
        Set<String> reviewedIds = new HashSet<String>();
        String orcidIds = lockAccounts.getOrcidsToLock().trim();
        if (StringUtils.isNotBlank(orcidIds)) {
            StringTokenizer tokenizer = new StringTokenizer(orcidIds, INP_STRING_SEPARATOR);
            while (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                String orcidId = getOrcidFromParam(nextToken);
                if (!profileEntityManager.orcidExists(orcidId)) {
                    notFoundIds.add(nextToken);
                } else {
                    ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                    if (!entity.isAccountNonLocked()) {
                        lockedIds.add(nextToken);
                    } else if (entity.isReviewed()) {
                        reviewedIds.add(nextToken);
                    } else {
                        profileEntityManager.lockProfile(orcidId, lockAccounts.getLockReason(), lockAccounts.getDescription());
                        successIds.add(orcidId);
                    }
                }
            }
        }

        Map<String, Set<String>> resendIdMap = new HashMap<String, Set<String>>();
        resendIdMap.put("notFound", notFoundIds);
        resendIdMap.put("successful", successIds);
        resendIdMap.put("alreadyLocked", lockedIds);
        resendIdMap.put("reviewed", reviewedIds);
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
                if (!profileEntityManager.orcidExists(orcidId)) {
                    notFoundIds.add(nextToken);
                } else {
                    ProfileEntity entity = profileEntityCacheManager.retrieve(orcidId);
                    if (entity.isReviewed()) {
                        reviewedIds.add(nextToken);
                    } else {
                        profileEntityManager.reviewProfile(orcidId);
                        successIds.add(nextToken);
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
                        boolean emailSupplied = !OrcidStringUtils.isValidOrcid(emailOrOrcid);
                        notificationManager.sendApiRecordCreationEmail(emailSupplied ? emailOrOrcid : null, orcidId);
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

}
