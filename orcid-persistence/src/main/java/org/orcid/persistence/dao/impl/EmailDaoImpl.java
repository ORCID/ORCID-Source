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
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.springframework.transaction.annotation.Transactional;
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
        TypedQuery<Long> query = entityManager.createQuery("select count(email) from EmailEntity where trim(lower(email)) = trim(lower(:email))", Long.class);
        query.setParameter("email", email);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public EmailEntity findCaseInsensitive(String email) {
        Assert.hasText(email, "Cannot find using an empty email address");
        TypedQuery<EmailEntity> query = entityManager.createQuery("from EmailEntity where trim(lower(email)) = trim(lower(:email))", EmailEntity.class);
        query.setParameter("email", email);
        List<EmailEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public void updateEmail(String orcid, String email, boolean isCurrent, Visibility visibility) {
        Query query = entityManager.createQuery("update EmailEntity set current = :current, visibility = :visibility where orcid = :orcid and email = :email");
        query.setParameter("orcid", orcid);
        query.setParameter("email", email);
        query.setParameter("current", isCurrent);
        query.setParameter("visibility", visibility);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void updatePrimary(String orcid, String primaryEmail) {
        Query updatePrimaryQuery = entityManager.createQuery("update EmailEntity set primary = 'true' where orcid = :orcid and email = :primaryEmail");
        Query updateNonPrimaryQuery = entityManager.createQuery("update EmailEntity set primary = 'false' where orcid = :orcid and email != :primaryEmail");
        for (Query query : new Query[] { updatePrimaryQuery, updateNonPrimaryQuery }) {
            query.setParameter("orcid", orcid);
            query.setParameter("primaryEmail", primaryEmail);
            query.executeUpdate();
        }
    }

    @Override
    @Transactional
    public void addEmail(String orcid, String email, Visibility visibility, String sourceOrcid) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO email (date_created, last_modified, orcid, email, is_primary, visibility, source_id) VALUES (now(), now(), :orcid, :email, 'false', :visibility, :sourceOrcid)");
        query.setParameter("orcid", orcid);
        query.setParameter("email", email);
        query.setParameter("visibility", visibility.name());
        query.setParameter("sourceOrcid", sourceOrcid);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void removeEmail(String orcid, String email) {
        Query query = entityManager.createQuery("delete from EmailEntity where orcid = :orcid and email = :email and primary != 'true'");
        query.setParameter("orcid", orcid);
        query.setParameter("email", email);
        query.executeUpdate();
    }

}
