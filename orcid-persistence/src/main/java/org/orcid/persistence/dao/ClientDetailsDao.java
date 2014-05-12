/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.Date;
import java.util.List;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

/**
 * 
 * @author Declan Newman
 * 
 */
public interface ClientDetailsDao extends GenericDao<ClientDetailsEntity, String> {

    ClientDetailsEntity findByClientId(String orcid, Date lastModified);

    Date getLastModified(String orcid);
    
    void updateLastModified(String orcid);
    
    boolean removeClientSecret(String clientId, String clientSecret);
    
    boolean createClientSecret(String clientId, String clientSecret);
    
    List<ClientSecretEntity> getClientSecretsByClientId(String clientId);
}
