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
package org.orcid.core.profileEvent;

import java.util.List;
import java.util.concurrent.Callable;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.springframework.context.ApplicationContext;

public interface ProfileEvent extends Callable<ProfileEventType> {
    
    public List <ProfileEventType> outcomes();
    
}
