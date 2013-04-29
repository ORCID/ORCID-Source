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
package org.orcid.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public class ExternalIdentifierManagerImpl implements ExternalIdentifierManager {

    @Resource
    private ExternalIdentifierDao externalIdentifierDao;
    
    
    /**
     * Get the list of external identifiers associated with an specific account
     * @param orcid
     *          The orcid of the owner
     * @return
     *          A list that contains all external identifiers for the specific account
     * */
    public List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid){
        return externalIdentifierDao.getExternalIdentifiers(orcid);
    }
    
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
    @Override
    public void removeExternalIdentifier(String orcid, String externalIdReference) { 
        externalIdentifierDao.removeExternalIdentifier(orcid, externalIdReference);
    }

}
