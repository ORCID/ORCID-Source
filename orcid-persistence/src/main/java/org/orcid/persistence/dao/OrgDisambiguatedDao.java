package org.orcid.persistence.dao;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDisambiguatedDao extends GenericDao<OrgDisambiguatedEntity, Long> {

    List<OrgDisambiguatedEntity> getOrgs(String searchTerm, int firstResult, int maxResults);

    List<OrgDisambiguatedEntity> getChunk(int firstResult, int maxResults);
    
    List<OrgDisambiguatedEntity> findBySourceType(String sourceType,int firstResult, int maxResults);

    OrgDisambiguatedEntity findBySourceIdAndSourceType(String sourceId, String sourceType);

    OrgDisambiguatedEntity findByNameCityRegionCountryAndSourceType(String name, String city, String region, String country, String sourceType);

    List<OrgDisambiguatedEntity> findByName(String name);

    List<OrgDisambiguatedEntity> findOrgsToGroup(int firstResult, int maxResult);
    
    List<Long> findOrgsPendingIndexing(int maxResult);

    void updateIndexingStatus(Long orgDisambiguatedId, IndexingStatus indexingStatus);

    List<Pair<Long, Integer>> findDisambuguatedOrgsWithIncorrectPopularity(int maxResults);

    void updatePopularity(Long orgDisambiguatedId, Integer popularity);

    void dropUniqueConstraint();

    void createUniqueConstraint();

    List<OrgDisambiguatedEntity> findDuplicates();
    
}