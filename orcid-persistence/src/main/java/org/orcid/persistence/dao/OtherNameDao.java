package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface OtherNameDao extends GenericDao<OtherNameEntity, Long> {

    /**
     * Get other names for an specific orcid account
     * @param orcid          
     * @return
     * The list of other names related with the specified orcid profile
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OtherNameEntity> getOtherNames(String orcid, long lastModified);

    /**
     * Get other names for an specific orcid account and with the specific visibility
     * @param orcid          
     * @return
     * The list of other names related with the specified orcid profile
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OtherNameEntity> getOtherNames(String orcid, String visibility);

    
    /**
     * Update other name entity with new values
     * @param otherName
     * @return
     *          true if the other name was sucessfully updated, false otherwise
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateOtherName(OtherNameEntity otherName);

    /**
     * Create other name for the specified account
     * @param orcid
     * @param displayName
     * @return
     *          true if the other name was successfully created, false otherwise 
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean addOtherName(String orcid, String displayName);

    /**
     * Delete other name from database
     * @param otherName
     * @return 
     *          true if the other name was successfully deleted, false otherwise
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean deleteOtherName(OtherNameEntity otherName);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    OtherNameEntity getOtherName(String orcid, Long putCode);
    
    /**
     * Removes all other names that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all other names will be
     *            removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void removeAllOtherNames(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<OtherNameEntity> getPublicOtherNames(String orcid, long lastModified);

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
    List<BigInteger> getIdsOfOtherNamesReferencingClientProfiles(int max, List<String> clientProfileOrcidIds);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibility(String orcid, Visibility visibility);
}
