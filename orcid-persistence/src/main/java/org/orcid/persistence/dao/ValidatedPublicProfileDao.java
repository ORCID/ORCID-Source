package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ValidatedPublicProfileEntity;

public interface ValidatedPublicProfileDao extends GenericDao<ValidatedPublicProfileEntity, String> {
    
    public static final int BATCH_SIZE = 100;

    List<String> getNextRecordsToValidate();
    
}
