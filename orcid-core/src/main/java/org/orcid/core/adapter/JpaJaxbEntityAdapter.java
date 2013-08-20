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
package org.orcid.core.adapter;

import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * Class to convert from JPA to JAXB classes and vice-versa
 * 
 * orcid-entities - Dec 5, 2011 - DateAdapter
 * 
 * @author Declan Newman (declan)
 **/

public interface JpaJaxbEntityAdapter {

    OrcidProfile toOrcidProfile(ProfileEntity profileEntity);

    OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions);

    ProfileEntity toProfileEntity(OrcidProfile profile);

    ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity);

    OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity);

    ProfileEntity toProfileEntity(OrcidClientGroup orcidClientGroup);

}
