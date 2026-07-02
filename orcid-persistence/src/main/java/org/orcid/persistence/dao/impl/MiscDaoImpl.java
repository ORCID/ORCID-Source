package org.orcid.persistence.dao.impl;

import java.time.Instant;
import java.util.Date;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.orcid.persistence.dao.MiscDao;

/**
 * @author Will Simpson
 */
public class MiscDaoImpl implements MiscDao {

    @Resource(name="entityManager")
    protected EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Date retrieveDatabaseDatetime() {
        Query query = entityManager.createNativeQuery("SELECT now()");
        Object result = query.getSingleResult();
        
        // Handle both java.time.Instant and java.util.Date returns
        if (result instanceof Instant) {
            return Date.from((Instant) result);
        }
        return (Date) result;
    }

}
