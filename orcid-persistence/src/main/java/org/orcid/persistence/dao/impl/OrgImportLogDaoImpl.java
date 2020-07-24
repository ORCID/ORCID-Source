package org.orcid.persistence.dao.impl;

import javax.persistence.Query;

import org.orcid.persistence.dao.OrgImportLogDao;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;

public class OrgImportLogDaoImpl extends GenericDaoImpl<OrgImportLogEntity, Long> implements OrgImportLogDao {
    
    public OrgImportLogDaoImpl() {
        super(OrgImportLogEntity.class);
    }

    @Override
    public String getNextImportSourceName() {
        Query query = entityManager.createNativeQuery("SELECT source FROM (SELECT MAX(start_time) AS start, source_type AS source from org_import_log GROUP BY source_type) AS dr ORDER BY start ASC LIMIT 1;");
        return (String) query.getSingleResult();
    }

    
}
