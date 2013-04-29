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
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public interface ExternalIdentifierDao {
    
    /**
     * Get the list of external identifiers associated with an specific account
     * @param orcid
     *          The orcid of the owner
     * @return
     *          A list that contains all external identifiers for the specific account
     * */
    List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid);
    
    /**
     * Removes an external identifier from database based on his ID.
     * The ID for external identifiers consists of the "orcid" of the owner and
     * the "externalIdReference" which is an identifier of the external id.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     * */
    void removeExternalIdentifier(String orcid, String externalIdReference);
}
