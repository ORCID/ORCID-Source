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

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.OrcidSocialDao;
import org.orcid.persistence.jpa.entities.OrcidSocialEntity;
import org.orcid.persistence.jpa.entities.OrcidSocialType;
import org.orcid.persistence.jpa.entities.keys.OrcidSocialPk;
import org.springframework.transaction.annotation.Transactional;

public class OrcidSocialDaoImpl extends GenericDaoImpl<OrcidSocialEntity, OrcidSocialPk> implements OrcidSocialDao {

    public OrcidSocialDaoImpl() {
        super(OrcidSocialEntity.class);
    }

    @Override
    @Transactional
    public void save(String orcid, OrcidSocialType type, String encryptedCredentials) {
        Query query = entityManager.createNativeQuery("INSERT INTO orcid_social(orcid, type, encrypted_credentials, date_created, last_modified) values(:orcid,:type,:credentials,now(),now())");
        query.setParameter("orcid", orcid);
        query.setParameter("type", type.name());
        query.setParameter("credentials", encryptedCredentials);
        query.executeUpdate();
    }
    
    @Override
    @Transactional
    public void delete(String orcid, OrcidSocialType type) {
        Query query = entityManager.createNativeQuery("DELETE FROM orcid_social WHERE orcid=:orcid and type=:type");
        query.setParameter("orcid", orcid);
        query.setParameter("type", type.name());
        query.executeUpdate();
    }

    @Override
    public boolean isEnabled(String orcid, OrcidSocialType type) {
        Query query = entityManager.createNativeQuery("SELECT * FROM orcid_social WHERE orcid=:orcid and type=:type");
        query.setParameter("orcid", orcid);
        query.setParameter("type", type.name());
        try {
            query.getSingleResult();
            return true;
        } catch(NoResultException nre) {
            return false;
        }
    }
    
    @Override
    public List<OrcidSocialEntity> getRecordsToTweet() {
        TypedQuery<OrcidSocialEntity> query = entityManager.createQuery("from OrcidSocialEntity where lastRun < (NOW() - CAST('1' as INTERVAL HOUR)) and type='TWITTER'", OrcidSocialEntity.class);        
        return query.getResultList();
    }
}
