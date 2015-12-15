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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.persistence.dao.ExternalIdentifierDao;

public class ExternalIdentifierManagerImpl implements ExternalIdentifierManager {

    @Resource
    private ExternalIdentifierDao externalIdentifierDao;

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

    @Override
    public ExternalIdentifiers getPublicExternalIdentifiersV2(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExternalIdentifiers getExternalIdentifiersV2(String orcid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExternalIdentifier getExternalIdentifierV2(String orcid, long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExternalIdentifier createExternalIdentifierV2(String orcid, ExternalIdentifier externalIdentifier) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExternalIdentifier updateExternalIdentifierV2(String orcid, ExternalIdentifier externalIdentifier) {
        // TODO Auto-generated method stub
        return null;
    }

}
