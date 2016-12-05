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

import java.util.List;

import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public interface ExternalIdentifierDao extends GenericDao<ExternalIdentifierEntity, Long> {

    /**
     * Removes an external identifier from database based on his ID. The ID for
     * external identifiers consists of the "orcid" of the owner and the
     * "externalIdReference" which is an identifier of the external id.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     */
    boolean removeExternalIdentifier(String orcid, String externalIdReference);

    /**
     * Retrieves all external identifiers associated with the given profile
     * 
     * @param orcid
     * @return a list of all external identifiers associated with the given
     *         profile
     */
    List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, long lastModified);

    /**
     * Retrieves all external identifiers associated with the given profile and
     * that have the given visibility
     * 
     * @param orcid
     * @param visibility
     * @return a list of all external identifiers associated with the given
     *         profile and that have the given visibility
     */
    List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, Visibility visibility);

    /**
     * Retrieve the external identifier that matches the give id and profile id
     * 
     * @param orcid
     * @param id
     * @return an external identifier that matches the given id and profile id
     */
    ExternalIdentifierEntity getExternalIdentifierEntity(String orcid, Long id);
    
    /**
     * Removes an external identifier from database based on his ID and the orcid.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     * @return true if an external identifier was deleted           
     */
    boolean removeExternalIdentifier(String orcid, Long id);
}
