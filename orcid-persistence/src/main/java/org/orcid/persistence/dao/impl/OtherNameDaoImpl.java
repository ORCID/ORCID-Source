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

import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class OtherNameDaoImpl extends GenericDaoImpl<OtherNameEntity, Long> implements OtherNameDao {

    public OtherNameDaoImpl(){
        super(OtherNameEntity.class);
    }
    
    @Override
    public List<OtherNameEntity> getOtherName(String orcid) {
        Query query = entityManager.createQuery("FROM OtherNameEntity WHERE profile.id=:orcid");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean updateOtherName(OtherNameEntity otherName) {
        Query query = entityManager.createQuery("UPDATE OtherNameEntity SET lastModified=now(), displayName=:displayName WHERE profile.id=:orcid");
        query.setParameter("displayName", otherName.getDisplayName());
        query.setParameter("orcid", otherName.getProfile().getId());
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean addOtherName(String orcid, String displayName) {
        Query query = entityManager.createNativeQuery("INSERT INTO other_name (other_name_id, date_created, last_modified, display_name, orcid) VALUES (nextval('other_name_seq'), now(), now(), :displayName, :orcid)");
        query.setParameter("orcid", orcid);
        query.setParameter("displayName", displayName);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean deleteOtherName(OtherNameEntity otherName) {
        Assert.notNull(otherName);
        Query query = entityManager.createQuery("DELETE FROM OtherNameEntity WHERE id=:id");
        query.setParameter("id", otherName.getId());        
        return query.executeUpdate() > 0 ? true : false;
    }
}
