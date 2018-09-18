package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.persistence.jpa.entities.keys.FindMyStuffHistoryEntityPk;

public interface FindMyStuffHistoryDao extends GenericDao<FindMyStuffHistoryEntity, FindMyStuffHistoryEntityPk>{

    public List<FindMyStuffHistoryEntity> findAll(String orcid);
    public boolean markActioned(String orcid, String finderName);
    public void markOptOut(String orcid, String finderName, boolean state);
    
}
