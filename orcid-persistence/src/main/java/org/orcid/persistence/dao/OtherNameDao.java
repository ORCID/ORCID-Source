package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.persistence.jpa.entities.OtherNameEntity;

public interface OtherNameDao extends GenericDao<OtherNameEntity, Long> {

    /**
     * Get other names for an specific orcid account
     * @param orcid          
     * @return
     * The list of other names related with the specified orcid profile
     * */
    List<OtherNameEntity> getOtherNames(String orcid, long lastModified);

    /**
     * Get other names for an specific orcid account and with the specific visibility
     * @param orcid          
     * @return
     * The list of other names related with the specified orcid profile
     * */
    List<OtherNameEntity> getOtherNames(String orcid, String visibility);

    
    /**
     * Update other name entity with new values
     * @param otherName
     * @return
     *          true if the other name was sucessfully updated, false otherwise
     * */
    boolean updateOtherName(OtherNameEntity otherName);

    /**
     * Create other name for the specified account
     * @param orcid
     * @param displayName
     * @return
     *          true if the other name was successfully created, false otherwise 
     * */
    boolean addOtherName(String orcid, String displayName);

    /**
     * Delete other name from database
     * @param otherName
     * @return 
     *          true if the other name was successfully deleted, false otherwise
     * */
    boolean deleteOtherName(OtherNameEntity otherName);
    
    OtherNameEntity getOtherName(String orcid, Long putCode);
    
    /**
     * Removes all other names that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all other names will be
     *            removed.
     */
    void removeAllOtherNames(String orcid);

    List<OtherNameEntity> getPublicOtherNames(String orcid, long lastModified);

    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    void correctClientSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    void correctUserSource(List<BigInteger> ids);
}
