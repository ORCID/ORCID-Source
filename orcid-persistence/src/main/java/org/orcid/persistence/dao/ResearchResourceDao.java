package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ResearchResourceDao extends GenericDao<ResearchResourceEntity, Long>{

    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeResearchResource(String userOrcid, Long researchResourceId);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ResearchResourceEntity> getByUser(String userOrcid, long lastModified);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public ResearchResourceEntity getResearchResource(String userOrcid, Long researchResourceId);

    @Transactional(propagation = Propagation.REQUIRED)
    void removeResearchResources(String orcid);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibilities(String orcid, ArrayList<Long> researchResourceIds, String visibility);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateToMaxDisplay(String orcid, Long researchResourceId);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Boolean hasPublicResearchResources(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getResearchResourcesReferencingOrgs(List<Long> orgIds);

}
