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

    void persist(E e);

    Long countAll();
}
