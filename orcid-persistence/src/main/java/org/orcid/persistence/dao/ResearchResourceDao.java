package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ResearchResourceEntity;

public interface ResearchResourceDao extends GenericDao<ResearchResourceEntity, Long>{

    boolean removeResearchResource(String userOrcid, Long researchResourceId);

    List<ResearchResourceEntity> getByUser(String userOrcid, long lastModified);
    
    public ResearchResourceEntity getResearchResource(String userOrcid, Long researchResourceId);

    void removeResearchResources(String orcid);

}
