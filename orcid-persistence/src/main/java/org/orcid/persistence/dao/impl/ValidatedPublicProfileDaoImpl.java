package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ValidatedPublicProfileDao;
import org.orcid.persistence.jpa.entities.ValidatedPublicProfileEntity;

public class ValidatedPublicProfileDaoImpl extends GenericDaoImpl<ValidatedPublicProfileEntity, String> implements ValidatedPublicProfileDao {

    public ValidatedPublicProfileDaoImpl() {
        super(ValidatedPublicProfileEntity.class);
    }

    public List<String> getNextRecordsToValidate() {
        TypedQuery<String> query = entityManager
                .createQuery("SELECT orcid FROM ProfileEntity p LEFT JOIN ValidatedPublicProfileEntity v ON  p.orcid = v.orcid WHERE v IS NULL", String.class);
        query.setMaxResults(BATCH_SIZE);
        return query.getResultList();
    }
}
