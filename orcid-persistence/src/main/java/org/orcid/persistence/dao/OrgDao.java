package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDao extends GenericDao<OrgEntity, Long> {

    List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults);

    List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults);
    
    List<OrgEntity> getOrgsByName(String searchTerm);

    OrgEntity findByNameCityRegionAndCountry(String name, String city, String region, String country);
    
    OrgEntity findByNameCityRegionCountryAndType(String name, String city, String region, String country, String orgType);
    
    void removeOrgsByClientSourceId(String clientSourceId);

    OrgEntity findByAddressAndDisambiguatedOrg(String name, String city, String region, String country, OrgDisambiguatedEntity orgDisambiguated);

    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    void correctClientSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    void correctUserSource(List<BigInteger> ids);

    List<Object[]> findConstraintViolatingDuplicateOrgDetails();

    List<BigInteger> getOrgIdsForDuplicateOrgDetails(String name, String city, String region, String country, Long orgDisambiguatedId);

    int convertNullCountriesToEmptyStrings(int batchSize);

    int convertNullCitiesToEmptyStrings(int batchSize);

    int convertNullRegionsToEmptyStrings(int batchSize);

}
