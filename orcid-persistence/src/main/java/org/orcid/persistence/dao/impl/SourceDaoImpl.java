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
package org.orcid.persistence.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.orcid.persistence.dao.SourceDao;

/**
 * @author Angel Montenegro
 */
public class SourceDaoImpl implements SourceDao {

    @Resource
    private EntityManager entityManager;
    
    @Override
    public Date getLastModified(String id) {
        Query query = entityManager.createNativeQuery("SELECT last_modified FROM client_details WHERE client_details_id = :id AND client_type != 'PUBLIC_CLIENT' UNION SELECT last_modified FROM profile WHERE orcid = :id");
        query.setParameter("id", id);        
        List<Date> results = (List) query.getResultList();
        return (Date) results.get(0);
    }

}
