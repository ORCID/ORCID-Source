/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.List;

import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * <p>
 * 2011-2012 ORCID
 * </p>
 * <p/>
 * <p>
 * User: Declan Newman (declan) Date: 10/02/2012
 * </p>
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
}