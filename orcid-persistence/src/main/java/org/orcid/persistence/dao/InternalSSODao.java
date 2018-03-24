package org.orcid.persistence.dao;

import java.util.Date;

import org.orcid.persistence.jpa.entities.InternalSSOEntity;

public interface InternalSSODao extends GenericDao<InternalSSOEntity, String> {
    /**
     * Creates a new InternalSSOEntity
     * @param orcid
     *          ORCID ID 
     * @param token
     *          Token value
     * @return the new entry         
     * */
    InternalSSOEntity insert(String orcid, String token);
    
    /**
     * Deletes the sso key that belongs to the given ORCID ID
     * @param orcid
     * @return true if the sso key was deleted
     * */
    boolean delete(String orcid);
    
    /**
     * Updates the last modified field of the sso key with the given ORCID id and token
     * @param orcid
     * @param token
     * @return true if it actually updates a key
     * */
    boolean update(String orcid, String token);
    
    /**
     * Verifies that a token exists and is not older than the given value
     * @param orcid
     * @param token
     * @param maxAge
     * @return true if the token exists and is not older than the given value  
     * */
    boolean verify(String orcid, String token, Date maxAge);
    
    /**
     * Verifies that a token exists and is not older than the given value, if so, return his last modified date
     * @param orcid
     * @param token
     * @param maxAge
     * @return Date if the token exists and is not older than the given value, null otherwise 
     * */
    Date getRecordLastModified(String orcid, String token, Date maxAge);   
}
