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
import java.util.Date;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ApplicationSummary;
import org.orcid.pojo.ajaxForm.Claim;

/**
 * User: Declan Newman (declan) Date: 10/02/2012 </p>
 */
public interface ProfileEntityManager {

    ProfileEntity findByOrcid(String orcid);
    
    String findByCreditName(String creditName);

    boolean orcidExists(String orcid);

    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);

    Long getConfirmedProfileCount();

    boolean deprecateProfile(ProfileEntity deprecatedProfile, ProfileEntity primaryProfile);

    List<ProfileEntity> findProfilesByOrcidType(OrcidType type);

    boolean enableDeveloperTools(OrcidProfile profile);

    boolean disableDeveloperTools(OrcidProfile profile);

    boolean isProfileClaimed(String orcid);

    ClientType getClientType(String orcid);

    MemberType getGroupType(String orcid);

    ActivitiesSummary getActivitiesSummary(String orcid);

    ActivitiesSummary getPublicActivitiesSummary(String orcid);

    Date getLastModified(String orcid);

    boolean isDeactivated(String deactivated);

    boolean unreviewProfile(String orcid);

    boolean reviewProfile(String orcid);
    
    List<ApplicationSummary> getApplications(List<OrcidOauth2TokenDetail> tokenDetails);
    
    String getOrcidHash(String orcid) throws NoSuchAlgorithmException;
    
    String retrivePublicDisplayName(String orcid);
    
    Person getPersonDetails(String orcid);
    
    Person getPublicPersonDetails(String orcid);
    
    boolean claimProfileAndUpdatePreferences(String orcid, String email, Locale locale, Claim claim);
    
    boolean deactivateRecord(String orcid);      
}