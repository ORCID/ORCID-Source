package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrgDao extends GenericDao<OrgEntity, Long> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<AmbiguousOrgEntity> getAmbiguousOrgs(int firstResult, int maxResults);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgEntity> getOrgs(String searchTerm, int firstResult, int maxResults);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgEntity> getOrgsByName(String searchTerm);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    OrgEntity findByNameCityRegionAndCountry(String name, String city, String region, String country);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    OrgEntity findByNameCityRegionCountryAndType(String name, String city, String region, String country, String sourceType);
    
    @Transactional(propagation = Propagation.REQUIRED)
    void removeOrgsByClientSourceId(String clientSourceId);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    OrgEntity findByAddressAndDisambiguatedOrg(String name, String city, String region, String country, OrgDisambiguatedEntity orgDisambiguated);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctClientSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctUserSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<Object[]> findConstraintViolatingDuplicateOrgDetails();

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getOrgIdsForDuplicateOrgDetails(String name, String city, String region, String country, Long orgDisambiguatedId);

    @Transactional(propagation = Propagation.REQUIRED)
    int convertNullCountriesToEmptyStrings(int batchSize);

    @Transactional(propagation = Propagation.REQUIRED)
    int convertNullCitiesToEmptyStrings(int batchSize);

    @Transactional(propagation = Propagation.REQUIRED)
    int convertNullRegionsToEmptyStrings(int batchSize);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsOfOrgsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OrgEntity> findByOrgDisambiguatedId(Long deprecated);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateOrgDisambiguatedId(long deletedOrgDisambiguatedId, long replacementOrgDisambiguatedId);

}
