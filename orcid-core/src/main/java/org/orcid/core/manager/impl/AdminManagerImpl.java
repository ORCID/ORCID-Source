package org.orcid.core.manager.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.orcid.core.admin.LockReason;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AdminDelegatesRequest;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.springframework.transaction.annotation.Transactional;

public class AdminManagerImpl implements AdminManager {
    public static final String AUTHORIZE_DELEGATION_ACTION = "/manage/authorize-delegates";
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;        
    
    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource(name = "notificationManagerV3")
    NotificationManager notificationManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;
    
    @Resource(name = "profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Override    
    @Transactional
    public boolean deprecateProfile(ProfileDeprecationRequest result, String deprecatedOrcid, String primaryOrcid, String adminUser) {        
        return deprecateProfile(result, deprecatedOrcid, primaryOrcid, ProfileEntity.ADMIN_DEPRECATION, adminUser);
    }
    
    @Override    
    @Transactional
    public boolean autoDeprecateProfile(ProfileDeprecationRequest result, String deprecatedOrcid, String primaryOrcid) {        
        return deprecateProfile(result, deprecatedOrcid, primaryOrcid, ProfileEntity.AUTO_DEPRECATION, null);
    }
    
    private boolean deprecateProfile(ProfileDeprecationRequest result, String deprecatedOrcid, String primaryOrcid, String deprecationMethod, String adminUser) {
        // Get deprecated profile
        ProfileEntity deprecated = profileEntityCacheManager.retrieve(deprecatedOrcid);
        ProfileEntity primary = profileEntityCacheManager.retrieve(primaryOrcid);        
        
        // If both users exists
        if (deprecated != null && primary != null) {
            // If account is already deprecated
            if (deprecated.getDeprecatedDate() != null) {
                result.getErrors().add(localeManager.resolveMessage("admin.profile_deprecation.errors.already_deprecated", deprecatedOrcid));
            } else if (primary.getDeprecatedDate() != null) {
                // If primary is deprecated
                result.getErrors().add(localeManager.resolveMessage("admin.profile_deprecation.errors.primary_account_deprecated", primaryOrcid));
            } else {
                // If the primary account is deactivated, return an error
                if (primary.getDeactivationDate() != null) {
                    result.getErrors().add(localeManager.resolveMessage("admin.profile_deprecation.errors.primary_account_is_deactivated", primaryOrcid));
                } else {                    
                    return profileEntityManager.deprecateProfile(deprecatedOrcid, primaryOrcid, deprecationMethod, adminUser);
                }
            }
        }
        
        return false;
    }
    
    public AdminDelegatesRequest startDelegationProcess(AdminDelegatesRequest request, String trusted, String managed) {
        boolean haveErrors = false;
        // The permission should not already exist
        if (givenPermissionToManagerReadOnly.findByGiverAndReceiverOrcid(managed, trusted) != null) {
            request.getErrors().add(localeManager.resolveMessage("admin.delegate.error.permission_already_exists", trusted, managed));
            haveErrors = true;
        }
        
        // Restriction #1: Both accounts must be claimed
        boolean isTrustedClaimed = profileEntityManager.isProfileClaimed(trusted);
        boolean isManagedClaimed = profileEntityManager.isProfileClaimed(managed);

        if (!isTrustedClaimed || !isManagedClaimed) {
            if (!isTrustedClaimed && !isManagedClaimed) {
                request.getErrors().add(localeManager.resolveMessage("admin.delegate.error.not_claimed.both", trusted, request.getManaged().getValue()));
            } else if (!isTrustedClaimed) {
                request.getTrusted().getErrors().add(localeManager.resolveMessage("admin.delegate.error.not_claimed", request.getTrusted().getValue()));
            } else {
                request.getManaged().getErrors().add(localeManager.resolveMessage("admin.delegate.error.not_claimed", request.getManaged().getValue()));
            }
            haveErrors = true;
        }

        // Restriction #2: Trusted individual must have a verified primary email
        // address
        if (!emailManager.isPrimaryEmailVerified(trusted)) {
            request.getTrusted().getErrors().add(localeManager.resolveMessage("admin.delegate.error.primary_email_not_verified", request.getTrusted().getValue()));
            haveErrors = true;
        }

        // Restriction #3: They cant be the same account
        if (trusted.equalsIgnoreCase(managed)) {
            request.getErrors().add(localeManager.resolveMessage("admin.delegate.error.cant_be_the_same", request.getTrusted().getValue(), request.getManaged().getValue()));
            haveErrors = true;
        }

        if (haveErrors)
            return request;

        // Generate link
        String link = generateEncryptedLink(trusted, managed);

        // Send email to managed account
        notificationManager.sendDelegationRequestEmail(managed, trusted, link);

        Email primaryEmail = emailManager.findPrimaryEmail(managed);
        
        request.setSuccessMessage(localeManager.resolveMessage("admin.delegate.admin.success", primaryEmail.getEmail()));
        
        return request;
    }
    
    /**
     * Encrypts a string
     * 
     * @param unencrypted
     * @return the string encrypted
     * */
    private String generateEncryptedLink(String trusted, String managed) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(TRUSTED_USER_PARAM, trusted);
        params.put(MANAGED_USER_PARAM, managed);
        String paramsString = JsonUtils.convertToJsonString(params);
        if (StringUtils.isNotBlank(paramsString)) {
            String encryptedParams = encryptionManager.encryptForExternalUse(paramsString);
            String base64EncodedParams = Base64.encodeBase64URLSafeString(encryptedParams.getBytes());
            String url = orcidUrlManager.getBaseUrl() + AUTHORIZE_DELEGATION_ACTION + "?key=" + base64EncodedParams;
            return url;
        } else {
            return null;
        }
    }
    
    @Override
    public List<String> getLockReasons() {
        return Arrays.asList(LockReason.values()).stream().map(lr -> lr.getLabel()).collect(Collectors.toList());
    }
}
