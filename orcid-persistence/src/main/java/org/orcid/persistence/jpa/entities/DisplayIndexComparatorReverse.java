package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * Comparator that compares OrcidEntity objects based on their ID.
 * 
 * Spring Sort annotation didn't seem to have descending function.
 * 
 * @author rcpeters
 * 
 */
public class DisplayIndexComparatorReverse<T> implements Comparator<DisplayIndexInterface>, Serializable {

    private static final long serialVersionUID = 1L;
    @Override
    public int compare(DisplayIndexInterface o1, DisplayIndexInterface o2) {
        Long index = o1.getDisplayIndex();
        Long otherIndex = o2.getDisplayIndex();
        if (index == null) {
            if (otherIndex == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (otherIndex == null) {
                return -1;
            } else if (index instanceof Comparable) {
                return  otherIndex.compareTo(index);
            } else {
                return 0;
            }
        }
    }

}
