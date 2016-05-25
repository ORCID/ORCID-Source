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

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.dao.SourceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Angel Montenegro
 */
public class SourceDaoImpl implements SourceDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceDaoImpl.class);
    
    @Resource
    private EntityManager entityManager;
    
    @Override
    public Date getLastModified(String id) {
        try {
            Query query = entityManager.createQuery("SELECT lastModified FROM ClientDetailsEntity WHERE id = :id AND clientType != :type");
            query.setParameter("id", id);        
            query.setParameter("type", ClientType.PUBLIC_CLIENT);
            Date result = (Date)query.getSingleResult();
            return result;
        } catch(Exception e1) {
            LOGGER.debug("Unable to find id in client details table: " + id, e1);
            try {
                Query query = entityManager.createQuery("SELECT lastModified FROM ProfileEntity WHERE orcid = :id");
                query.setParameter("id", id);        
                Date result = (Date)query.getSingleResult();
                return result;
            } catch(Exception e2) {
                LOGGER.error("Unable to find id in any of the tables: " + id, e2);
                throw e2;
            }
        }
    }

}
