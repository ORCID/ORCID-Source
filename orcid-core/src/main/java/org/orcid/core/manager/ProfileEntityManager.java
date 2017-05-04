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
package org.orcid.core.manager;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.orcid.core.manager.read_only.ProfileEntityManagerReadOnly;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Claim;

/**
 * User: Declan Newman (declan) Date: 10/02/2012 </p>
 */
public interface ProfileEntityManager extends ProfileEntityManagerReadOnly {

    String findByCreditName(String creditName);
    
    boolean orcidExists(String orcid);

    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);    

    boolean deprecateProfile(String deprecated, String primary);
    
    boolean isProfileDeprecated(String orcid);

    boolean enableDeveloperTools(OrcidProfile profile);

    boolean disableDeveloperTools(OrcidProfile profile);

    boolean isProfileClaimed(String orcid);
    
    boolean isProfileClaimedByEmail(String email);

    MemberType getGroupType(String orcid);    

    boolean isDeactivated(String deactivated);

    boolean unreviewProfile(String orcid);

    boolean reviewProfile(String orcid);
    
    List<ApplicationSummary> getApplications(String orcid);
    
    void disableApplication(Long tokenId, String userOrcid);
    
    String getOrcidHash(String orcid) throws NoSuchAlgorithmException;
    
    String retrivePublicDisplayName(String orcid);
    
    boolean claimProfileAndUpdatePreferences(String orcid, String email, Locale locale, Claim claim);
    
    boolean deactivateRecord(String orcid);
    
    boolean reactivateRecord(String orcid);

    void updateLastModifed(String orcid);

    void updateLocale(String orcid, Locale locale);

    void reactivate(String orcid, String givenNames, String familyName, String password, Visibility defaultVisibility);

    public void updatePassword(String orcid, String encryptedPassword);
    
    public void updateSecurityQuestion(String orcid, Integer questionId, String answer);
    
    public void updateIpAddress(String orcid, String ipAddress);
    
    public Locale retrieveLocale(String orcid);      
    
    boolean lockProfile(String orcid, String lockReason, String description);

    boolean unlockProfile(String orcid);
}