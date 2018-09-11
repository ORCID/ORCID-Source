package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.persistence.jpa.entities.keys.FindMyStuffHistoryEntityPk;

public interface FindMyStuffHistoryDao extends GenericDao<FindMyStuffHistoryEntity, FindMyStuffHistoryEntityPk>{

    public void setOptOut(String orcid, String finderName, boolean optOut);
    public List<FindMyStuffHistoryEntity> findAll(String orcid);
    public void markActioned(String orcid, String finderName);
    
}
