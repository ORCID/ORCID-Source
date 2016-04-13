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
package org.orcid.persistence.dao;

import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.persistence.jpa.entities.BiographyEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface BiographyDao extends GenericDao<BiographyEntity, Long> {
    boolean exists(String orcid);
    
    BiographyEntity getBiography(String orcid);

    boolean updateBiography(String orcid, String biography, Visibility visibility);

    void createBiography(String orcid, String biography, Visibility visibility);
}
