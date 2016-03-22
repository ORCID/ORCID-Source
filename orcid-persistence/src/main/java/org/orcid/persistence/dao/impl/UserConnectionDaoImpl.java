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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;

/**
 * @author Shobhit Tyagi
 */
public class UserConnectionDaoImpl extends GenericDaoImpl<UserconnectionEntity, UserconnectionPK> implements UserConnectionDao {

    public UserConnectionDaoImpl() {
        super(UserconnectionEntity.class);
    }

    @Override
    public void updateLoginInformation(UserconnectionPK pk) {
        UserconnectionEntity entity = find(pk);
        entity.setLastLogin(new Timestamp(new Date().getTime()));
        merge(entity);
    }

    @Override
    public UserconnectionEntity findByProviderIdAndProviderUserId(String providerUserId, String providerId) {
        TypedQuery<UserconnectionEntity> query = entityManager
                .createQuery("from UserconnectionEntity where id.provideruserid = :providerUserId and providerid= :providerId", UserconnectionEntity.class);
        query.setParameter("providerUserId", providerUserId);
        query.setParameter("providerId", providerId);
        List<UserconnectionEntity> results = query.getResultList();
        return results != null && !results.isEmpty() ? results.get(0) : null;
    }

    @Override
    public List<UserconnectionEntity> findByOrcid(String orcid) {
        TypedQuery<UserconnectionEntity> query = entityManager.createQuery("from UserconnectionEntity where orcid = :orcid)", UserconnectionEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @Override
    public void deleteByOrcid(String orcid) {
        Query query = entityManager.createQuery("delete from UserconnectionEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

}
