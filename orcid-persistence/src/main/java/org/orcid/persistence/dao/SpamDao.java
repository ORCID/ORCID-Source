package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.SpamEntity;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface SpamDao extends GenericDao<SpamEntity, Long>{

    boolean exists(String orcid);
    
    boolean removeSpam(String orcid);
    
    SpamEntity getSpam(String orcid);       
    
    void createSpam(SpamEntity spam);

    boolean updateSpamCount(SpamEntity spam, Integer count);       

}
