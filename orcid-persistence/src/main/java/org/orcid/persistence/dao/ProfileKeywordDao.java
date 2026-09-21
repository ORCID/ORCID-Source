package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ProfileKeywordDao extends GenericDao<ProfileKeywordEntity, Long> {

    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ProfileKeywordEntity> getProfileKeywords(String orcid, long lastModified);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ProfileKeywordEntity> getPublicProfileKeywords(String orcid, long lastModified);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ProfileKeywordEntity> getProfileKeywords(String orcid, String visibility);

    /**
     * Deleted a keyword from database
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully deleted
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean deleteProfileKeyword(String orcid, String keyword);
    
    /**
     * Adds a keyword to a specific profile
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean addProfileKeyword(String orcid, String keyword, String sourceId, String clientSourceId, String visibility);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean deleteProfileKeyword(ProfileKeywordEntity entity);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    ProfileKeywordEntity getProfileKeyword(String orcid, Long putCode);
    
    /**
     * Removes all keywords that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all keywords will be
     *            removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void removeAllKeywords(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctClientSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctUserSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateUserOBODetails(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void revertUserOBODetails(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBORecords(int max);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsOfKeywordsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibility(String orcid, Visibility visibility);
}
