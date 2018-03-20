package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * Comparator that compares OrcidEntity objects based on their ID.
 * 
 * @author Will Simpson
 * 
 */
public class OrcidEntityIdComparator<T> implements Comparator<OrcidEntity<T>>, Serializable {

    private static final long serialVersionUID = 1L;

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
