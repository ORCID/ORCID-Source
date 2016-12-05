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
package org.orcid.core.manager.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ajax.JSON;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AdminDelegatesRequest;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.springframework.transaction.annotation.Transactional;

public class AdminManagerImpl implements AdminManager {
    public static final String AUTHORIZE_DELEGATION_ACTION = "/manage/authorize-delegates";
    
    @Resource
    private ProfileEntityManager profileEntityManager;        
    
    @Resource
    private EmailManager emailManager;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    NotificationManager notificationManager;
    
    @Resource
    protected OrcidProfileManager orcidProfileManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private GivenPermissionToDao givenPermissionToDao;
    
    @Resource(name = "profileEntityCacheManager")
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Override    
    @Transactional
    public boolean deprecateProfile(ProfileDeprecationRequest result, String deprecatedOrcid, String primaryOrcid) {        
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
                    return profileEntityManager.deprecateProfile(deprecatedOrcid, primaryOrcid);
                }
            }
        }
        
        return false;
    }
    
    public AdminDelegatesRequest startDelegationProcess(AdminDelegatesRequest request, String trusted, String managed) {
        boolean haveErrors = false;
        // The permission should not already exist
        if (givenPermissionToDao.findByGiverAndReceiverOrcid(managed, trusted) != null) {
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
        // Get primary emails
        OrcidProfile managedOrcidProfile = orcidProfileManager.retrieveClaimedOrcidProfile(managed);
        OrcidProfile trustedOrcidProfile = orcidProfileManager.retrieveClaimedOrcidProfile(trusted);
        // Send email to managed account
        notificationManager.sendDelegationRequestEmail(managedOrcidProfile, trustedOrcidProfile, link);

        request.setSuccessMessage(localeManager.resolveMessage("admin.delegate.admin.success", managedOrcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue()));
        
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
        String paramsString = JSON.toString(params);
        if (StringUtils.isNotBlank(paramsString)) {
            String encryptedParams = encryptionManager.encryptForExternalUse(paramsString);
            String base64EncodedParams = Base64.encodeBase64URLSafeString(encryptedParams.getBytes());
            String url = orcidUrlManager.getBaseUrl() + AUTHORIZE_DELEGATION_ACTION + "?key=" + base64EncodedParams;
            return url;
        } else {
            return null;
        }
    }
    
    public String removeSecurityQuestion(String orcid) {
        if(profileEntityManager.orcidExists(orcid)) {                    
                OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                if (orcidProfile != null) {
                    orcidProfile.getOrcidInternal().getSecurityDetails().setSecurityQuestionId(null);
                    orcidProfile.setSecurityQuestionAnswer(null);
                    orcidProfileManager.updateSecurityQuestionInformation(orcidProfile);
                } else {
                    return localeManager.resolveMessage("admin.errors.unexisting_orcid");
                }
            
        } else {
            return localeManager.resolveMessage("admin.errors.unable_to_fetch_info");
        }
        
        return null;
    }
}
