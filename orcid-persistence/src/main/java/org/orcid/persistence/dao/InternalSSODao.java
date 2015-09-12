/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
    boolean delete(String orcid);
    
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
