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
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;


/**
 * 
 * @author jamesb
 *
 */
public class ClientRedirectDaoImpl extends GenericDaoImpl<ClientRedirectUriEntity,ClientRedirectUriPk> implements ClientRedirectDao {

    public ClientRedirectDaoImpl() {
        super(ClientRedirectUriEntity.class);
    }
    
    
    @Override
    public List<ClientRedirectUriEntity> findClientDetailsWithRedirectScope() {
       
        Query query = entityManager.createQuery("from ClientRedirectUriEntity as crue " +
                "inner join fetch crue.clientDetailsEntity "+
        		"where crue.predefinedClientScope is not null");
        
        return  query.getResultList();
       
    }

}
