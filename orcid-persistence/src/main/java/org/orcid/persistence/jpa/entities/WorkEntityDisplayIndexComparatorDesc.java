/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class WorkEntityDisplayIndexComparatorDesc<T> implements Comparator<WorkEntity>, Serializable {

    private static final long serialVersionUID = 1L;
    @Override
    public int compare(WorkEntity o1, WorkEntity o2) {
        Long index = o1.getDisplayIndex();
        Long otherIndex = o2.getDisplayIndex();
        if (index == null? otherIndex == null : index.equals(otherIndex)) return o2.compareTo(o1);
        if (index == null) return 1;
        if (otherIndex == null) return -1;
        return otherIndex.compareTo(index);
    }
}