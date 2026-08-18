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

    E find(I id);

    List<E> findLastModifiedBefore(Date latestDate, int maxResults);

    List<E> getAll();

    @Transactional(propagation = Propagation.REQUIRED)
    void remove(E e);

    @Transactional(propagation = Propagation.REQUIRED)
    void remove(I id);

    @Transactional(propagation = Propagation.REQUIRED)
    void removeAll();

    @Transactional(propagation = Propagation.REQUIRED)
    void flush();
    
    void flushWithoutTransactional();

    @Transactional(propagation = Propagation.REQUIRED)
    void persist(E e);
    
    @Transactional(propagation = Propagation.REQUIRED)
    Long countAll();

}
