package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.SpamEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface SpamDao extends GenericDao<SpamEntity, Long>{

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean exists(String orcid);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeSpam(String orcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    SpamEntity getSpam(String orcid);       
    
    @Transactional(propagation = Propagation.REQUIRED)
    void createSpam(SpamEntity spam);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateSpamCount(SpamEntity spam, Integer count);       

}
