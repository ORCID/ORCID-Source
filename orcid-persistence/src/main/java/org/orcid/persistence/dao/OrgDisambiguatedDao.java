package org.orcid.persistence.dao;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDisambiguatedDao extends GenericDao<OrgDisambiguatedEntity, Long> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedEntity> getOrgs(String searchTerm, int firstResult, int maxResults);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedEntity> getChunk(int firstResult, int maxResults);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedEntity> findBySourceType(String sourceType,int firstResult, int maxResults);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    OrgDisambiguatedEntity findBySourceIdAndSourceType(String sourceId, String sourceType);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    OrgDisambiguatedEntity findByNameCityRegionCountryAndSourceType(String name, String city, String region, String country, String sourceType);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedEntity> findByName(String name);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedEntity> findOrgsToGroup(int firstResult, int maxResult);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<Long> findOrgsPendingIndexing(int maxResult);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateIndexingStatus(Long orgDisambiguatedId, IndexingStatus indexingStatus);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<Pair<Long, Integer>> findDisambuguatedOrgsWithIncorrectPopularity(int maxResults);

    @Transactional(propagation = Propagation.REQUIRED)
    void updatePopularity(Long orgDisambiguatedId, Integer popularity);

    @Transactional(propagation = Propagation.REQUIRED)
    void dropUniqueConstraint();

    @Transactional(propagation = Propagation.REQUIRED)
    void createUniqueConstraint();

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgDisambiguatedEntity> findDuplicates();
    
}