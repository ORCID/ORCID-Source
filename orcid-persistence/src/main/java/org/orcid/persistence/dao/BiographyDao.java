package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.BiographyEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface BiographyDao extends GenericDao<BiographyEntity, Long> {
    boolean exists(String orcid);
    
    BiographyEntity getBiography(String orcid, long lastModified);

    boolean updateBiography(String orcid, String biography, String visibility);

    void persistBiography(String orcid, String biography, String visibility);

    boolean removeForId(String orcid);
}
