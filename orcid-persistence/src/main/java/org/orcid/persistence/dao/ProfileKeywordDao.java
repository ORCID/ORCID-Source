package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public interface ProfileKeywordDao extends GenericDao<ProfileKeywordEntity, Long> {

    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    List<ProfileKeywordEntity> getProfileKeywords(String orcid, long lastModified);
    
    List<ProfileKeywordEntity> getPublicProfileKeywords(String orcid, long lastModified);
    
    List<ProfileKeywordEntity> getProfileKeywords(String orcid, String visibility);

    /**
     * Deleted a keyword from database
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully deleted
     * */
    boolean deleteProfileKeyword(String orcid, String keyword);
    
    /**
     * Adds a keyword to a specific profile
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     * */
    boolean addProfileKeyword(String orcid, String keyword, String sourceId, String clientSourceId, String visibility);
    
    boolean deleteProfileKeyword(ProfileKeywordEntity entity);
    
    ProfileKeywordEntity getProfileKeyword(String orcid, Long putCode);
    
    /**
     * Removes all keywords that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all keywords will be
     *            removed.
     */
    void removeAllKeywords(String orcid);

    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    void correctClientSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    void correctUserSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max);

    void updateUserOBODetails(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max);

    void revertUserOBODetails(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBORecords(int max);

    List<BigInteger> getIdsOfKeywordsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds);

    boolean updateVisibility(String orcid, Visibility visibility);
}
