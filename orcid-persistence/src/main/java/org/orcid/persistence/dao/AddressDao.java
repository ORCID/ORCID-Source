package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressDao extends GenericDao<AddressEntity, Long> {
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    AddressEntity getAddress(String orcid, Long putCode);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<AddressEntity> getAddresses(String orcid, long lastModified);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<AddressEntity> getAddresses(String orcid, String visibility);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<Object[]> findAddressesToMigrate();
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean deleteAddress(String orcid, Long putCode);
    
    /**
     * Removes all address that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all address will be
     *            removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void removeAllAddress(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<AddressEntity> getPublicAddresses(String orcid, long lastModified);

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
    List<BigInteger> getIdsOfAddressesReferencingClientProfiles(int max, List<String> ids);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibility(String orcid, Visibility visibility);
    
}
