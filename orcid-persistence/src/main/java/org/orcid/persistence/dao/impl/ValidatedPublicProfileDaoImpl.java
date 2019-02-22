package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.ValidatedPublicProfileDao;
import org.orcid.persistence.jpa.entities.ValidatedPublicProfileEntity;

public class ValidatedPublicProfileDaoImpl extends GenericDaoImpl<ValidatedPublicProfileEntity, String> implements ValidatedPublicProfileDao {

    public ValidatedPublicProfileDaoImpl() {
        super(ValidatedPublicProfileEntity.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getNextRecordsToValidate(int batchSize) {
        Query query = entityManager
                .createNativeQuery("SELECT p.orcid FROM profile p LEFT JOIN validated_public_profile v ON  p.orcid = v.orcid WHERE v IS NULL AND p.enabled IS TRUE AND p.deprecated_date IS NULL AND p.record_locked IS NOT TRUE and p.profile_deactivation_date IS NULL");
        query.setMaxResults(batchSize);
        return query.getResultList();
    }
}
