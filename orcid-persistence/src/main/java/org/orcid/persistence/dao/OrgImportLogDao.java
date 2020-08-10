package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrgImportLogEntity;

public interface OrgImportLogDao extends GenericDao<OrgImportLogEntity, Long> {
    
    /**
     * Returns the an ordered list of org import sources that are due to be loaded.
     * @return list of org import source names
     */
    List<String> getImportSourceOrder();
    
}
