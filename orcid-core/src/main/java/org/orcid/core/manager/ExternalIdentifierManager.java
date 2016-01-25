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

import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;

public interface ExternalIdentifierManager {
    
    /**
     * Return the list of public external identifiers associated to a specific
     * profile
     * 
     * @param orcid
     * @return the list of public external identifiers associated with the orcid
     *         profile
     */
    ExternalIdentifiers getPublicExternalIdentifiers(String orcid, long lastModified);

    /**
     * Return the list of external identifiers associated to a specific profile
     * 
     * @param orcid
     * @return the list of external identifiers associated with the orcid
     *         profile
     */
    ExternalIdentifiers getExternalIdentifiers(String orcid, long lastModified);

    /**
     * Retrieve a external identifier from database
     * 
     * @param id
     * @return the externalIdentifierEntity associated with the parameter id
     */
    ExternalIdentifier getExternalIdentifier(String orcid, Long id);

    /**
     * Add a new external identifier to a specific profile
     * 
     * @param orcid
     * @param externalIdentifier
     * @return true if the external identifier was successfully created on
     *         database
     */
    ExternalIdentifier createExternalIdentifier(String orcid, ExternalIdentifier externalIdentifier);

    /**
     * Updates an existing external identifier
     * 
     * @param orcid
     * @param externalIdentifier
     * @return the updated external identifier
     */
    ExternalIdentifier updateExternalIdentifier(String orcid, ExternalIdentifier externalIdentifier);

    /**
     * Deletes an external identifier
     * 
     * @param orcid
     * @param id
     */
    boolean deleteExternalIdentifier(String orcid, Long id, boolean checkSource);
}
