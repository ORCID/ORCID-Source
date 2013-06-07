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
package org.orcid.persistence.jpa.entities;

/**
 * Interface to indicate that an entity contains the owning profile entity (and
 * therefore can be used to update the last modified date of the profile).
 * 
 * @author Will Simpson
 * 
 */
public interface ProfileAware {

    ProfileEntity getProfile();

}
