package org.orcid.persistence.dao.impl;

import java.math.BigInteger;

import javax.persistence.Query;

import org.orcid.persistence.dao.SpamDao;
import org.orcid.persistence.jpa.entities.SpamEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Daniel Palafox
 */
public class SpamDaoImpl extends GenericDaoImpl<SpamEntity, Long> implements SpamDao {

    public SpamDaoImpl() {
        super(SpamEntity.class);
    }    

    @Override
    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("select count(*) from spam where orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger)query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }   

    @Override
    public SpamEntity getSpam(String orcid) {
        Query query = entityManager.createQuery("from SpamEntity where orcid=:orcid");
        query.setParameter("orcid", orcid);
        return (SpamEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean updateSpamCount(SpamEntity spam, Integer count) {
        Query query = entityManager.createNativeQuery("update spam set spam_counter = :spam_counter, last_modified = now() where orcid = :orcid");
        query.setParameter("spam_counter", count);
        query.setParameter("orcid", spam.getOrcid());
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public void createSpam(SpamEntity spam) {
        entityManager.persist(spam);
    }

    @Override
    @Transactional
    public boolean removeSpam(String orcid) {
        Query query = entityManager.createQuery("delete from SpamEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
        return query.executeUpdate() > 0 ? true : false;
    }

}
