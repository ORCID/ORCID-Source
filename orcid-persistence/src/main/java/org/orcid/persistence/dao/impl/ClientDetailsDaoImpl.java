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

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

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
    @Cacheable(value = "client-details", key = "#orcid.concat('-').concat(#lastModified)")
    public ClientDetailsEntity findByClientId(String orcid, Date lastModified) {
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
    public Date getLastModified(String orcid) {
        TypedQuery<Date> query = entityManager.createQuery("select lastModified from ClientDetailsEntity where id = :orcid", Date.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateLastModified(String orcid) {
        Query updateQuery = entityManager.createQuery("update ClientDetailsEntity set lastModified = now() where id = :orcid");
        updateQuery.setParameter("orcid", orcid);
        updateQuery.executeUpdate();
    }
    
    @Override
    @Transactional
    public boolean removeClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager.createNativeQuery("delete from client_secret where client_details_id=:clientId and client_secret=:clientSecret");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }
    
    @Override
    @Transactional
    public boolean createClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager.createNativeQuery("INSERT INTO client_secret (client_details_id, client_secret, date_created, last_modified) VALUES (:clientId, :clientSecret, now(), now())");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }
    
    @Override
    public List<ClientSecretEntity> getClientSecretsByClientId(String clientId) {
        TypedQuery<ClientSecretEntity> query = entityManager.createQuery("From ClientSecretEntity WHERE client_details_id=:clientId", ClientSecretEntity.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }
    
    @Override
    public boolean exists(String clientId) {
        TypedQuery<Long> query = entityManager.createQuery("select count(*) from ClientDetailsEntity where client_details_id=:clientId", Long.class);
        query.setParameter("clientId", clientId);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }
}
