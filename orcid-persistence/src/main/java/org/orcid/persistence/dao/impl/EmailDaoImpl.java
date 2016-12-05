/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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
    public String findOrcidIdByCaseInsenitiveEmail(String email) {
        TypedQuery<String> query = entityManager.createQuery("select profile.id from EmailEntity where trim(lower(email)) = trim(lower(:email))", String.class);
        query.setParameter("email", email);
        return query.getSingleResult();
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
    public void addEmail(String orcid, String email, Visibility visibility, String sourceId, String clientSourceId) {
        addEmail(orcid, email, visibility, sourceId, clientSourceId, false, true);
    }

    @Override 
    @Transactional
    public void addEmail(String orcid, String email, Visibility visibility, String sourceId, String clientSourceId, boolean isVerified, boolean isCurrent) {
        try {
            Query query = entityManager
                    .createNativeQuery("INSERT INTO email (date_created, last_modified, orcid, email, is_primary, is_verified, is_current, visibility, source_id, client_source_id) VALUES (now(), now(), :orcid, :email, false, :isVerified, :isCurrent, :visibility, :sourceId, :clientSourceId)");
            query.setParameter("orcid", orcid);
            query.setParameter("email", email);
            query.setParameter("visibility", visibility.name());
            query.setParameter("sourceId", sourceId);
            query.setParameter("clientSourceId", clientSourceId);
            query.setParameter("isVerified", isVerified);
            query.setParameter("isCurrent", isCurrent);
            query.executeUpdate();
        } catch(Exception psqle) {
            throw psqle;
        }
    }

    @Override
    @Transactional
    public void removeEmail(String orcid, String email) {
        removeEmail(orcid, email, false);
    }

    @Override
    @Transactional
    public void removeEmail(String orcid, String email, boolean removeIfPrimary) {
        String deleteEmailEvent  = null;
        String deleteEmail = null;
        if (removeIfPrimary) {
            deleteEmailEvent = "delete from email_event where trim(lower(email)) = trim(lower(:email))";
            deleteEmail = "delete from email where orcid = :orcid and trim(lower(email)) = trim(lower(:email))";
        } else {
            //Check if the email is primary before removing the email events and the email itself 
            deleteEmailEvent = "delete from email_event where trim(lower(email)) = trim(lower(:email)) and not(select is_primary from email where trim(lower(email)) = trim(lower(:email)))";
            deleteEmail = "delete from email where orcid = :orcid and trim(lower(email)) = trim(lower(:email)) and not(is_primary);";
        }
        
        Query query = entityManager.createNativeQuery(deleteEmailEvent);        
        query.setParameter("email", email);                
        query.executeUpdate();
        
        query = entityManager.createNativeQuery(deleteEmail);
        query.setParameter("email", email);
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List findIdByCaseInsensitiveEmail(List<String> emails) {
        for (int i=0; i < emails.size(); i++) {
            if (emails.get(i) != emails.get(i).toLowerCase().trim()) emails.set(i, emails.get(i).toLowerCase().trim());
        }
        Query query = entityManager.createNativeQuery("select orcid, email from email where trim(lower(email)) in :emails");        
        query.setParameter("emails", emails);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public void addSourceToEmail(String sourceId, String email) {
        Query query = entityManager.createNativeQuery("update email set source_id = :sourceId where email=:email");
        query.setParameter("sourceId", sourceId);
        query.setParameter("email", email);
        query.executeUpdate();        
    }
    
    @Override
    @Transactional
    public boolean verifyEmail(String email) {
        Query query = entityManager.createNativeQuery("update email set is_verified = true, last_modified=now() where trim(lower(email)) = trim(lower(:email))");
        query.setParameter("email", email);
        return query.executeUpdate() > 0;
    }
    
    @Override
    public boolean isPrimaryEmailVerified(String orcid) {
        Query query = entityManager.createNativeQuery("select is_verified from email where orcid=:orcid and is_primary=true");
        query.setParameter("orcid", orcid);
        return (Boolean)query.getSingleResult();
    }
    
    @Override
    @Transactional
    public boolean verifyPrimaryEmail(String orcid) {
        Query query = entityManager.createNativeQuery("update email set is_verified=true where orcid=:orcid and is_primary=true");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Override
    @Transactional
    public boolean moveEmailToOtherAccountAsNonPrimary(String email, String origin, String destination) {
        Query query = entityManager.createNativeQuery("update email set orcid=:destination, is_primary=false, last_modified=now() where orcid=:origin and email=:email");
        query.setParameter("destination", destination);
        query.setParameter("origin", origin);
        query.setParameter("email", email);
        return query.executeUpdate() > 0;
    }
    
    @Override
    public List<EmailEntity> findByOrcid(String orcid) {
        TypedQuery<EmailEntity> query = entityManager.createQuery("from EmailEntity where orcid = :orcid", EmailEntity.class);
        query.setParameter("orcid", orcid);
        List<EmailEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }
    
    @Override
    public List<EmailEntity> findByOrcid(String orcid, org.orcid.jaxb.model.common_rc3.Visibility visibility) {
        TypedQuery<EmailEntity> query = entityManager.createQuery("from EmailEntity where orcid = :orcid and visibility = :visibility", EmailEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", org.orcid.jaxb.model.message.Visibility.fromValue(visibility.value()));
        List<EmailEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }

    @Override
    @Transactional
    public boolean verifySetCurrentAndPrimary(String orcid, String email) {
        Query query = entityManager.createQuery("update EmailEntity set current = true, primary = true, verified = true where orcid = :orcid and email = :email");
        query.setParameter("orcid", orcid);
        query.setParameter("email", email);
        return query.executeUpdate() > 0;
    }

    /***
     * Indicates if the given email address could be auto deprecated given the
     * ORCID rules. See
     * https://trello.com/c/ouHyr0mp/3144-implement-new-auto-deprecate-workflow-
     * for-members-unclaimed-ids
     * 
     * @param email
     *            Email address
     * @return true if the email exists, the owner is not claimed and the
     *         client source of the record allows auto deprecating records
     */
    @Override
    public boolean isAutoDeprecateEnableForEmail(String email) {
        Query query = entityManager.createNativeQuery("SELECT allow_auto_deprecate FROM client_details WHERE client_details_id=(SELECT client_source_id FROM profile WHERE orcid=(SELECT orcid FROM email WHERE trim(lower(email)) = trim(lower(:email))) AND claimed = false)");
        query.setParameter("email", email);
        try {
            Boolean result = (Boolean) query.getSingleResult();
            return result == null ? false : result;
        } catch(Exception e) {
            //TODO log exception
        }
        return false;
    }
}
