package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface BiographyDao extends GenericDao<BiographyEntity, Long> {
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean exists(String orcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    BiographyEntity getBiography(String orcid, long lastModified);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateBiography(String orcid, String biography, String visibility);

    @Transactional(propagation = Propagation.REQUIRED)
    void persistBiography(String orcid, String biography, String visibility);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeForId(String orcid);
}
