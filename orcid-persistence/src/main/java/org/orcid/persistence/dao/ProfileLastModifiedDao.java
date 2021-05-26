package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

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
    
    boolean updateIndexingStatus(List<String> orcidIds, IndexingStatus indexingStatus);
    
    Date retrieveLastModifiedDate(String orcid);
    
    
    
}
