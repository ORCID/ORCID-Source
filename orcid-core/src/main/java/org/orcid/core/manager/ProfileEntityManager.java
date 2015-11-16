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
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ApplicationSummary;

/**
 * User: Declan Newman (declan) Date: 10/02/2012 </p>
 */
public interface ProfileEntityManager {

    ProfileEntity findByOrcid(String orcid);

    boolean orcidExists(String orcid);

    boolean hasBeenGivenPermissionTo(String giverOrcid, String receiverOrcid);

    boolean existsAndNotClaimedAndBelongsTo(String messageOrcid, String clientId);

    Long getConfirmedProfileCount();

    public boolean updateProfile(OrcidProfile profile);

    public boolean updateProfile(ProfileEntity profile);

    public boolean deprecateProfile(ProfileEntity deprecatedProfile, ProfileEntity primaryProfile);

    public List<ProfileEntity> findProfilesByOrcidType(OrcidType type);

    public boolean enableDeveloperTools(OrcidProfile profile);

    public boolean disableDeveloperTools(OrcidProfile profile);

    public Iso3166Country getCountry(String orcid);

    public boolean isProfileClaimed(String orcid);

    ClientType getClientType(String orcid);

    MemberType getGroupType(String orcid);

    public boolean lockProfile(String orcid);

    public boolean unlockProfile(String orcid);

    public boolean isLocked(String orcid);

    ActivitiesSummary getActivitiesSummary(String orcid);

    ActivitiesSummary getPublicActivitiesSummary(String orcid);

    Date getLastModified(String orcid);

    boolean isDeactivated(String deactivated);

    boolean unreviewProfile(String orcid);

    boolean reviewProfile(String orcid);
    
    Visibility getResearcherUrlDefaultVisibility(String orcid);

    List<ApplicationSummary> getApplications(List<OrcidOauth2TokenDetail> tokenDetails);
    
    String getOrcidHash(String orcid) throws NoSuchAlgorithmException;
}