package org.orcid.persistence.dao;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface OrcidPropsDao {

    boolean create(String key, String value);
    boolean update(String key, String value);
    boolean exists(String key);
    String getValue(String key);    
}
