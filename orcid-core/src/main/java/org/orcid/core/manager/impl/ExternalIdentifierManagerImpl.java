package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.persistence.dao.ExternalIdentifierDao;

public class ExternalIdentifierManagerImpl implements ExternalIdentifierManager {

    @Resource
    private ExternalIdentifierDao externalIdentifierDao;
    
    @Override
    public void removeExternalIdentifier(String orcid, String externalIdReference) {        
        externalIdentifierDao.removeExternalIdentifier(orcid, externalIdReference);
    }

}
