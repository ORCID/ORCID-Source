package org.orcid.persistence.dao;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface OrcidPropsDao {

    @Transactional(propagation = Propagation.REQUIRED)
    boolean create(String key, String value);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean update(String key, String value);
    
    @Transactional(readOnly = true)
    boolean exists(String key);
    
    @Transactional(readOnly = true)
    String getValue(String key);    
}
