package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.ValidatedPublicProfileDao;
import org.orcid.persistence.jpa.entities.ValidatedPublicProfileEntity;

public class ValidatedPublicProfileDaoImpl extends GenericDaoImpl<ValidatedPublicProfileEntity, String> implements ValidatedPublicProfileDao {

    public ValidatedPublicProfileDaoImpl() {
        super(ValidatedPublicProfileEntity.class);
    }

}
