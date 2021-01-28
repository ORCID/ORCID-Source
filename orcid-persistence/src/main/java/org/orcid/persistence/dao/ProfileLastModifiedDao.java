package org.orcid.persistence.dao;

import java.util.Date;

import org.orcid.persistence.jpa.entities.IndexingStatus;

/**
 * DAO bean only for updating and retrieving profile last modified date and updating indexing status.
 * 
 * @author georgenash
 *
 */
public interface ProfileLastModifiedDao {

    void updateLastModifiedDateAndIndexingStatus(String orcid, IndexingStatus indexingStatus);
    
    void updateLastModifiedDateWithoutResult(String orcid);
    
    Date retrieveLastModifiedDate(String orcid);
    
    
    
}
