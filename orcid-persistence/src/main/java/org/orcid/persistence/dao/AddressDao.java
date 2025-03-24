package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AddressDao extends GenericDao<AddressEntity, Long> {
    AddressEntity getAddress(String orcid, Long putCode);

    List<AddressEntity> getAddresses(String orcid, long lastModified);
    
    List<AddressEntity> getAddresses(String orcid, String visibility);

    List<Object[]> findAddressesToMigrate();
    
    boolean deleteAddress(String orcid, Long putCode);
    
    /**
     * Removes all address that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all address will be
     *            removed.
     */
    void removeAllAddress(String orcid);

    List<AddressEntity> getPublicAddresses(String orcid, long lastModified);

    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    void correctClientSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    void correctUserSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max);

    void updateUserOBODetails(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max);

    void revertUserOBODetails(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBORecords(int max);

    List<BigInteger> getIdsOfAddressesReferencingClientProfiles(int max, List<String> ids);

    boolean updateVisibility(String orcid, Visibility visibility);
    
}
