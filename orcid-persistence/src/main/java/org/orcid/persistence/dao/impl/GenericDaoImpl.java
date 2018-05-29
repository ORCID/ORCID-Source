package org.orcid.persistence.dao.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.OrcidEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * orcid-persistence - Dec 7, 2011 - GenericDaoImpl
 * 
 * @author Will Simpson and Declan Newman
 */
public class GenericDaoImpl<E extends OrcidEntity<I>, I extends Serializable> implements GenericDao<E, I> {

    @Resource(name="entityManager")
    protected EntityManager entityManager;

    private Class<E> clazz;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

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
    public void flushWithoutTransactional() {
        entityManager.flush();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void persist(E e) {
        entityManager.persist(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @ExcludeFromProfileLastModifiedUpdate
    public void persistIgnoringProfileLastModifiedUpdate(E e) {
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
    public void refresh(E e) {
        entityManager.refresh(e);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long countAll() {
        return (Long) entityManager.createQuery("select count(e) from " + clazz.getSimpleName() + " e").getSingleResult();
    }

}
