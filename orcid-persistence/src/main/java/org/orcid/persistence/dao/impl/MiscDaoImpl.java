package org.orcid.persistence.dao.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

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
        return (Date) query.getSingleResult();
    }

}
