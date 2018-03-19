package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.OrcidEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * orcid-persistence - Oct 28, 2011
 * 
 * @author Will Simpson
 */

public interface GenericDao<E extends OrcidEntity<I>, I extends Serializable> {

    E merge(E e);

    void refresh(E e);

    void detatch(E e);

    E find(I id);

    List<E> findLastModifiedBefore(Date latestDate, int maxResults);

    List<E> getAll();

    void remove(E e);

    void remove(I id);

    void removeAll();

    void flush();
    
    void flushWithoutTransactional();

    void persist(E e);

    Long countAll();

}
