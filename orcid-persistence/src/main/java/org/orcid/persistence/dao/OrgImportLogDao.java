package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrgImportLogEntity;

public interface OrgImportLogDao extends GenericDao<OrgImportLogEntity, Long> {
    
    String getNextImportSourceName();
    
}
