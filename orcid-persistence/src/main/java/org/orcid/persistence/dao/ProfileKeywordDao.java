package org.orcid.persistence.dao;

import java.util.List;

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
    
    List<ProfileKeywordEntity> getProfileKeywords(String orcid, org.orcid.jaxb.model.common_v2.Visibility visibility);

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
    boolean addProfileKeyword(String orcid, String keyword, String sourceId, String clientSourceId, org.orcid.jaxb.model.common_v2.Visibility visibility);
    
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
}
