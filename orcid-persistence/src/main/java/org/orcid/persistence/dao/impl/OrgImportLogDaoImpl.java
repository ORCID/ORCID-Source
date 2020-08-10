package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.OrgImportLogDao;
import org.orcid.persistence.jpa.entities.OrgImportLogEntity;

public class OrgImportLogDaoImpl extends GenericDaoImpl<OrgImportLogEntity, Long> implements OrgImportLogDao {
    
    public OrgImportLogDaoImpl() {
        super(OrgImportLogEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getImportSourceOrder() {
        Query query = entityManager.createNativeQuery("SELECT source FROM (SELECT MAX(start_time) AS start, source_type AS source from org_import_log GROUP BY source_type) AS dr ORDER BY start ASC;");
        return (List<String>) query.getResultList();
    }

    
}
