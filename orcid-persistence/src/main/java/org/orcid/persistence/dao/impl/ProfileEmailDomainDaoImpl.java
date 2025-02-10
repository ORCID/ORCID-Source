package org.orcid.persistence.dao.impl;

import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
import org.orcid.persistence.dao.ProfileEmailDomainDao;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class ProfileEmailDomainDaoImpl extends GenericDaoImpl<ProfileEmailDomainEntity, Long> implements ProfileEmailDomainDao {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileEmailDomainDaoImpl.class);

    public ProfileEmailDomainDaoImpl() {
        super(ProfileEmailDomainEntity.class);
    }
        
    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public ProfileEmailDomainEntity addEmailDomain(String orcid, String emailDomain, String visibility) {
        ProfileEmailDomainEntity e = new ProfileEmailDomainEntity();
        e.setEmailDomain(emailDomain);
        e.setOrcid(orcid);
        e.setVisibility(visibility);
        e.setGeneratedByScript(false);
        entityManager.persist(e);
        return e;
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public void removeEmailDomain(String orcid, String emailDomain) {
        String deleteEmail = "delete from profile_email_domain where orcid = :orcid and trim(lower(email_domain)) = trim(lower(:emailDomain))";

        Query query = entityManager.createNativeQuery(deleteEmail);
        query.setParameter("emailDomain", emailDomain);
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public void removeAllEmailDomains(String orcid) {
        String deleteEmail = "delete from profile_email_domain where orcid = :orcid";

        Query query = entityManager.createNativeQuery(deleteEmail);
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean updateVisibility(String orcid, String emailDomain, String visibility) {
        Query query = entityManager.createNativeQuery("UPDATE profile_email_domain SET visibility=:visibility, last_modified = now() WHERE orcid = :orcid and email_domain = :emailDomain");
        query.setParameter("orcid", orcid);
        query.setParameter("emailDomain", emailDomain);
        query.setParameter("visibility", visibility);
        return query.executeUpdate() > 0;
    }

    @Override
    public List<ProfileEmailDomainEntity> findByOrcid(String orcid) {
        TypedQuery<ProfileEmailDomainEntity> query = entityManager.createQuery("from ProfileEmailDomainEntity where orcid = :orcid", ProfileEmailDomainEntity.class);
        query.setParameter("orcid", orcid);
        List<ProfileEmailDomainEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }

    @Override
    public List<ProfileEmailDomainEntity> findPublicEmailDomains(String orcid) {
        TypedQuery<ProfileEmailDomainEntity> query = entityManager.createQuery("from ProfileEmailDomainEntity where orcid = :orcid and visibility = 'PUBLIC'", ProfileEmailDomainEntity.class);
        query.setParameter("orcid", orcid);
        List<ProfileEmailDomainEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }

    @Override
    public ProfileEmailDomainEntity findByEmailDomain(String orcid, String emailDomain) {
        TypedQuery<ProfileEmailDomainEntity> query = entityManager.createQuery("from ProfileEmailDomainEntity where orcid = :orcid and emailDomain = :emailDomain", ProfileEmailDomainEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("emailDomain", emailDomain);
        try {
            return query.getSingleResult();
        } catch(NoResultException nre) {
            // Ignore this exception
        } catch(Exception e) {
            // Propagate any other exception
            throw e;
        }
        return null;
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public void moveEmailDomainToAnotherAccount(String emailDomain, String deprecatedOrcid, String primaryOrcid) {
        Query query = entityManager.createNativeQuery("UPDATE profile_email_domain SET orcid=:primaryOrcid, last_modified = now() WHERE orcid = :deprecatedOrcid and email_domain = :emailDomain");
        query.setParameter("primaryOrcid", primaryOrcid);
        query.setParameter("emailDomain", emailDomain);
        query.setParameter("deprecatedOrcid", deprecatedOrcid);
        query.executeUpdate();
    }
}
