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

import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * @author Declan Newman
 */
@PersistenceContext(unitName = "orcid")
public class ClientDetailsDaoImpl extends GenericDaoImpl<ClientDetailsEntity, String> implements ClientDetailsDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsDaoImpl.class);

    public ClientDetailsDaoImpl() {
        super(ClientDetailsEntity.class);
    }

    @Override
    @Transactional("transactionManager")
    public ClientDetailsEntity findByClientId(String orcid) {
        TypedQuery<ClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where id = :orcid", ClientDetailsEntity.class);
        query.setParameter("orcid", orcid);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No client found for {}", orcid, e);
            return null;
        }
    }

    @Override
    @Transactional("transactionManager")
    public void removeByClientId(String orcid) {
        Query query = entityManager.createQuery("delete from ClientDetailsEntity  where id = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

}
