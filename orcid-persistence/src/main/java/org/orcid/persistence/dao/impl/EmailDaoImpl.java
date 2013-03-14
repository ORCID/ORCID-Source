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

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.springframework.util.Assert;

/**
 * 
 * @author Will Simpson
 *
 */
public class EmailDaoImpl extends GenericDaoImpl<EmailEntity, String> implements EmailDao {

    public EmailDaoImpl() {
        super(EmailEntity.class);
    }

    @Override
    public boolean emailExists(String email) {
        Assert.hasText(email, "Cannot check for an empty email address");
        TypedQuery<Long> query = entityManager.createQuery("select count(email) from EmailEntity where lower(email) = lower(:email)", Long.class);
        query.setParameter("email", email);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public EmailEntity findCaseInsensitive(String email) {
        Assert.hasText(email, "Cannot find using an empty email address");
        TypedQuery<EmailEntity> query = entityManager.createQuery("from EmailEntity where lower(email) = lower(:email)", EmailEntity.class);
        query.setParameter("email", email);
        List<EmailEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

}
