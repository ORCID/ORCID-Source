package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate;
import org.orcid.persistence.dao.InternalSSODao;
import org.orcid.persistence.jpa.entities.InternalSSOEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Angel Montenegro
 */
@PersistenceContext(unitName = "orcid")
public class InternalSSODaoImpl extends GenericDaoImpl<InternalSSOEntity, String> implements InternalSSODao {

    public InternalSSODaoImpl() {
        super(InternalSSOEntity.class);
    }

    @Override
    @Transactional
    @ExcludeFromProfileLastModifiedUpdate
    public InternalSSOEntity insert(String orcid, String token) {
        InternalSSOEntity entity = new InternalSSOEntity();
        entity.setDateCreated(new Date());
        entity.setLastModified(new Date());
        entity.setId(orcid);
        entity.setToken(token);
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    @ExcludeFromProfileLastModifiedUpdate
    public boolean delete(String orcid) {
        Query query = entityManager.createNativeQuery("DELETE FROM internal_sso WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    @Transactional
    @ExcludeFromProfileLastModifiedUpdate
    public boolean update(String orcid, String token) {
        Query query = entityManager.createNativeQuery("UPDATE internal_sso SET last_modified = now() WHERE orcid = :orcid AND token = :token");
        query.setParameter("orcid", orcid);
        query.setParameter("token", token);
        return query.executeUpdate() > 0;
    }

    @Override
    public boolean verify(String orcid, String token, Date maxAge) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM internal_sso WHERE orcid = :orcid AND token = :token AND last_modified >= :maxAge");
        query.setParameter("orcid", orcid);
        query.setParameter("token", token);
        query.setParameter("maxAge", maxAge);
        try {
            BigInteger result = (BigInteger)query.getSingleResult();
            return result.longValue() > 0;
        } catch(NoResultException nre) {
            
        }
        return false;
    }

    @Override
    public Date getRecordLastModified(String orcid, String token, Date maxAge) {
        TypedQuery<Date> query = entityManager.createQuery("SELECT lastModified FROM InternalSSOEntity WHERE orcid = :orcid AND token = :token AND last_modified >= :maxAge", Date.class);
        query.setParameter("orcid", orcid);
        query.setParameter("token", token);
        query.setParameter("maxAge", maxAge);
        try {
            Date result = query.getSingleResult();
            return result;
        } catch(NoResultException nre) {
            
        }        
        return null;
    }

}
