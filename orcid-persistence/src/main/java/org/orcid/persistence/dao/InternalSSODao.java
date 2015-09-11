package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.InternalSSOEntity;

public interface InternalSSODao extends GenericDao<InternalSSOEntity, String> {
    /**
     * TODO
     * */
    InternalSSOEntity insert(String orcid, String token);
    
    /**
     * TODO
     * */
    void delete(String orcid);
    
    /**
     * TODO
     * */
    InternalSSOEntity update(String orcid, String token);
    
    /**
     * TODO
     * */
    boolean verify(String orcid, String token, long maxAge);
    
    /**
     * TODO
     * */
    void recordModified(String orcid, String token, long maxAge);   
}
