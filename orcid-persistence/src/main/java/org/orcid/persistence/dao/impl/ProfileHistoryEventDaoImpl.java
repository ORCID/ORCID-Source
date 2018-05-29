package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.ProfileHistoryEventDao;
import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;

public class ProfileHistoryEventDaoImpl extends GenericDaoImpl<ProfileHistoryEventEntity, Long> implements ProfileHistoryEventDao {

    public ProfileHistoryEventDaoImpl() {
        super(ProfileHistoryEventEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProfileHistoryEventEntity> findByProfile(String orcid) {
        Query query = entityManager.createQuery("FROM ProfileHistoryEventEntity WHERE orcid = :orcid ORDER BY dateCreated DESC");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }

}
