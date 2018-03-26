package org.orcid.persistence.dao;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.orcid.jaxb.model.message.Iso3166Country;
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

    OrgDisambiguatedEntity findBySourceIdAndSourceType(String sourceId, String sourceType);

    OrgDisambiguatedEntity findByNameCityRegionCountryAndSourceType(String name, String city, String region, Iso3166Country country, String sourceType);

    List<OrgDisambiguatedEntity> findByName(String name);

    List<OrgDisambiguatedEntity> findOrgsPendingIndexing(int firstResult, int maxResult);

    void updateIndexingStatus(Long orgDisambiguatedId, IndexingStatus indexingStatus);

    List<Pair<Long, Integer>> findDisambuguatedOrgsWithIncorrectPopularity(int maxResults);

    void updatePopularity(Long orgDisambiguatedId, Integer popularity);

    void replace(long deletedOrgDisambiguatedId, long replacementOrgDisambiguatedId);

    void dropUniqueConstraint();

    void createUniqueConstraint();

    List<OrgDisambiguatedEntity> findDuplicates();

}