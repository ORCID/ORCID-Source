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
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;

/**
 * orcid-persistence - Dec 7, 2011 - Jaxb2JpaAdapter
 * 
 * @author Declan Newman (declan)
 **/

public interface Jpa2JaxbAdapter {

    OrcidProfile toOrcidProfile(ProfileEntity profileEntity);

    OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions);

    OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity);

    OrcidWork getOrcidWork(ProfileWorkEntity profileWorkEntity);

}
