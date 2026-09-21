package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrcidEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * orcid-persistence - Oct 28, 2011
 * 
 * @author Will Simpson
 */

public interface GenericDao<E extends OrcidEntity<I>, I extends Serializable> {

    @Transactional(propagation = Propagation.REQUIRED)
    E merge(E e);

    void refresh(E e);

    @Transactional(propagation = Propagation.REQUIRED)
    void detatch(E e);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    E find(I id);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<E> findLastModifiedBefore(Date latestDate, int maxResults);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<E> getAll();

    @Transactional(propagation = Propagation.REQUIRED)
    void remove(E e);

    @Transactional(propagation = Propagation.REQUIRED)
    void remove(I id);

    @Transactional(propagation = Propagation.REQUIRED)
    void removeAll();

    @Transactional(propagation = Propagation.REQUIRED)
    void flush();
    
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRED)
    void flushWithoutTransactional();

    @Transactional(propagation = Propagation.REQUIRED)
    void persist(E e);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Long countAll();

}
