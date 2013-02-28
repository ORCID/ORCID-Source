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
package org.orcid.persistence.jpa.entities;

import java.util.Comparator;

/**
 * 
 * Comparator that compares OrcidEntity objects based on their ID.
 * 
 * @author Will Simpson
 * 
 */
public class OrcidEntityIdComparator<T> implements Comparator<OrcidEntity<T>> {

    @SuppressWarnings("unchecked")
    @Override
    public int compare(OrcidEntity<T> o1, OrcidEntity<T> o2) {
        T id = o1.getId();
        T otherId = o2.getId();
        if (id == null) {
            if (otherId == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (otherId == null) {
                return 1;
            } else if (id instanceof Comparable) {
                return ((Comparable<T>) id).compareTo(otherId);
            } else {
                return 0;
            }
        }
    }

}
