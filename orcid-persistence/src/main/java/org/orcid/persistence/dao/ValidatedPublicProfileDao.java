package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ValidatedPublicProfileEntity;

public interface ValidatedPublicProfileDao extends GenericDao<ValidatedPublicProfileEntity, String> {
    
    List<String> getNextRecordsToValidate(int batchSize);
    
}
