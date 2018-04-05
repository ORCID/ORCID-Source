package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;

public interface ProfileHistoryEventDao extends GenericDao<ProfileHistoryEventEntity, Long> {

    List<ProfileHistoryEventEntity> findByProfile(String orcid);
    
}
