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

import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.OrcidEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * orcid-persistence - Dec 7, 2011 - GenericDaoImpl
 * 
 * @author Will Simpson and Declan Newman
 */
@PersistenceUnit(name = "entityManagerFactory")
public class GenericDaoImpl<E extends OrcidEntity<I>, I extends Serializable> implements GenericDao<E, I> {

    @PersistenceContext(unitName="orcid")
    protected EntityManager entityManager;

    private Class<E> clazz;

    public GenericDaoImpl(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E find(I id) {
        return entityManager.find(clazz, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> findLastModifiedBefore(Date latestDate, int maxResults) {
        Query query = entityManager.createQuery("from " + clazz.getSimpleName() + " where lastModified <= :latestDate");
        query.setParameter("latestDate", latestDate);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> getAll() {
        return entityManager.createQuery("from " + clazz.getSimpleName()).getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void remove(E e) {
        entityManager.remove(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void remove(I id) {
        E e = find(id);
        entityManager.remove(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAll() {
        entityManager.createQuery("delete from " + clazz.getSimpleName()).executeUpdate();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void flush() {
        entityManager.flush();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void persist(E e) {
        entityManager.persist(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public E merge(E e) {
        return entityManager.merge(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void detatch(E e) {
        entityManager.detach(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void refresh(E e) {
        entityManager.refresh(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long countAll() {
        return (Long) entityManager.createQuery("select count(e) from " + clazz.getSimpleName() + " e").getSingleResult();
    }

}
